/*
 * @(#) DistributedDataSourceExample.java
 *
 * JOTM: Java Open Transaction Manager 
 *
 * This project is developed inside the ObjectWeb Consortium,
 * http://www.objectweb.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 * --------------------------------------------------------------------------
 * $Id: DistributedDataSourceExample.java,v 1.1 2003/04/02 13:18:55 jmesnil Exp $
 * --------------------------------------------------------------------------
 */

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.naming.InitialContext;
import javax.sql.DataSource;
import javax.transaction.UserTransaction;

/**
 * @author jmesnil
 */
public class DistributedDataSourceExample {

    private static final String SQL_REQUEST = "select id, foo from testdata";

    private static final String SQL_QUERY =
        "update testdata set foo = ? where id=1";

    private static Connection conn;

    private static void updateTable(int newValue) {
        try {
            PreparedStatement pstmt = conn.prepareStatement(SQL_QUERY);
            pstmt.setInt(1, newValue);
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void printTable() {
        try {
            Statement stmt = conn.createStatement();
            ResultSet rset = stmt.executeQuery(SQL_REQUEST);
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

    public static void main(String[] args) {
        
		if (args.length !=  2|| (!args[0].equals("commit") && !args[0].equals("rollback"))) {
		  System.err.println("usage: java DistributedDataSourceExample [completion] [number]\n");
            System.exit(1);
        }
	
		String completion = args[0];
        int value = 0;
        try {
            value = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.out.println("[number] has to be an integer\n");
            System.exit(1);
        }

        UserTransaction utx = null;
        try {
            System.out.println("create initial context");
            InitialContext ictx = new InitialContext();
            System.out.println("lookup UserTransaction at : UserTransaction");
            utx = (UserTransaction) ictx.lookup("UserTransaction");
            System.out.println("lookup DataSource at: DataSource");
            DataSource ds = (DataSource) ictx.lookup("DataSource");
            System.out.println("get a connection");
            conn = ds.getConnection("mojo", "jojo");
        } catch (Exception e) {
            System.out.println(
                "Exception of type :"
                    + e.getClass().getName()
                    + " has been thrown");
            System.out.println("Exception message :" + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println("before transaction, table is:");
        printTable();

        try {
            System.out.println("begin a transaction");
            utx.begin();

            System.out.println("update the table");
            updateTable(value);

			if (completion.equals("commit")) {
				System.out.println("*commit* the transaction");
				utx.commit();
			} else {
				System.out.println("*rollback* the transaction");
				utx.rollback();
			}
        } catch (Exception e) {
            System.out.println(
                "Exception of type :"
                    + e.getClass().getName()
                    + " has been thrown");
            System.out.println("Exception message :" + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println("after transaction, table is:");
        printTable();
        System.out.println("Recovery Example is ok.\n");
        System.exit(0);
    }
}
