
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.transaction.UserTransaction;

public class StandardXAPoolDataSourceExample {

    private static final String USAGE = "usage: java StandardXAPoolDataSourceExample [commit|rollback] [number]";
    private static final String SQL_REQUEST = "select id, foo from testdata";
    private static final String SQL_QUERY = "update testdata set foo = ? where id=1";
    private static final String USER_TRANSACTION_JNDI_NAME = "UserTransaction";
    private static Connection conn = null;

    private static void printTable() {
        try {
            PreparedStatement pstmt = conn.prepareStatement(SQL_REQUEST);
            ResultSet rset = pstmt.executeQuery();
            int numcols = rset.getMetaData().getColumnCount();
            for (int i = 1; i <= numcols; i++) {
                System.out.print("\t" + rset.getMetaData().getColumnName(i));
            }
            System.out.println();
            while (rset.next()) {
                for (int i = 1; i <= numcols; i++) {
                    System.out.print("\t" + rset.getString(i));
                }
                System.out.println("");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void updateTable(int newValue) {
        try {
            PreparedStatement pstmt = conn.prepareStatement(SQL_QUERY);
            pstmt.setInt(1, newValue);
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        if (args.length !=  2|| (!args[0].equals("commit") && !args[0].equals("rollback"))) {
            System.err.println(USAGE + "\n");
            System.exit(1);
        }

        String completion = args[0];

        int newValue = 0;
        try {
            newValue = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.err.println(USAGE);
            System.err.println("[number] has to be an integer\n");
            System.exit(1);
        }

        System.out.println("start server");
        DatabaseHelper dbHelper = new DatabaseHelper();

        UserTransaction utx = null;
        try {
            System.out.println("create initial context");
            Context ictx = new InitialContext();
            System.out.println("lookup UserTransaction at : " + USER_TRANSACTION_JNDI_NAME);
            utx = (UserTransaction) ictx.lookup(USER_TRANSACTION_JNDI_NAME);
        } catch (Exception e) {
            System.err.println("Exception of type :" + e.getClass().getName() + " has been thrown");
            System.err.println("Exception message :" + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

        try {
            System.out.println("get a connection");
            conn = dbHelper.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("before transaction, table is:");
        printTable();

        try {
            System.out.println("begin a transaction");
            utx.begin();

            System.out.println("update the table");
            updateTable(newValue);

            if (completion.equals("commit")) {
                System.out.println("*commit* the transaction");
                utx.commit();
            } else {
                System.out.println("*rollback* the transaction");
                utx.rollback();
            }
        } catch (Exception e) {
            System.out.println("Exception of type :" + e.getClass().getName() + " has been thrown");
            System.out.println("Exception message :" + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

        utx = null;

        System.out.println("after transaction, table is:");
        printTable();

        try {
            System.out.println("close connection");
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn = null;
        }

        System.out.println("stop server");
        dbHelper.stop();

        System.out.println("JDBC example is ok.\n");
        System.exit(0);
    }
}
