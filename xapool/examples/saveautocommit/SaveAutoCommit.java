
import org.enhydra.jdbc.pool.StandardXAPoolDataSource;
import org.enhydra.jdbc.standard.StandardXADataSource;
import org.objectweb.jotm.Jotm;
import org.objectweb.transaction.jta.TMService;

import javax.naming.InitialContext;
import javax.naming.Context;
import javax.transaction.UserTransaction;
import java.util.Properties;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Connection;

/**
 * this test is used to show the following process:<br>
 * <pre>
 *    c = ds.getConnection();
 *    c.setAutoCommit(autocommit);
 *    utx.begin();
 *    ...
 *    utx.commit();
 *    autocommit2 = c.getAutoCommit();
 *    // autocommit == autocommit2
 *    c.close();
 * </pre>
 */
public class SaveAutoCommit {
    private String SQL_REQUEST = "select id, foo from testdata";
    private String SQL_QUERY = "update testdata set foo = ? where id=1";
    private String USAGE = "usage: java SaveAutoCommit [number] [autocommit]";

    private Connection conn;
    private TMService jotm;

    private String login = null;
    private String password = null;
    private String url = null;
    private String driver = null;
    private String USER_TRANSACTION_JNDI_NAME = "UserTransaction";
    private PreparedStatement STMT = null;

    public SaveAutoCommit(String [] args) throws Exception {
        if (args.length != 2) {
            System.out.println(USAGE);
            System.exit(1);
        }

        // first, load the properties from the spy.properties file
        Properties prop = new Properties();
        try {
            prop.load(ClassLoader.getSystemResourceAsStream("spy.properties"));
        } catch (Exception e) {
            System.err.println("problem to load properties.");
            e.printStackTrace();
            System.exit(1);
        }
	
        login = prop.getProperty("login");
        password = prop.getProperty("password");
        url = prop.getProperty("url");
        driver = prop.getProperty("driver");

        // get the new value which will be assign to the database
        int newValue = 0;
        try {
            newValue = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            System.err.println(USAGE);
            System.err.println("[number] has to be an integer\n");
            System.exit(1);
        }

        // Get a transaction manager
        try {
            // creates an instance of JOTM with a local transaction factory which is not bound to a registry
            jotm = new Jotm(true, false);
            InitialContext ictx = new InitialContext();
            ictx.rebind(USER_TRANSACTION_JNDI_NAME, jotm.getUserTransaction());
        } catch (Exception e) {
            System.err.println("JOTM problem");
            e.printStackTrace();
            System.exit(1);
        }

        // get an user transaction
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


        // create an XA pool datasource with a minimum of 4 objects
        StandardXAPoolDataSource spds = new StandardXAPoolDataSource(4);
        spds.setMaxSize(15);
        spds.setMinSize(13);
        spds.setUser(login);
        spds.setPassword(password);

        // create an XA datasource which will be given to the XA pool
        StandardXADataSource xads = new StandardXADataSource();
        try {
            xads.setDriverName(driver);
            xads.setUrl(url);
            xads.setUser(login);
            xads.setPassword(password);
            xads.setTransactionManager(jotm.getTransactionManager());
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        // give the XA datasource to the pool (to create futur objects)
        spds.setDataSource(xads);

        try {
            conn = spds.getConnection(login, password);
            boolean autocom =(new Boolean(args[1].trim())).booleanValue();
            conn.setAutoCommit(autocom);

            utx.begin();
            PreparedStatement pstmt = conn.prepareStatement(SQL_QUERY);
            pstmt.setInt(1, newValue);
            pstmt.executeUpdate();
    	    utx.commit();
            System.out.println("SetAutoCommit is '"+conn.getAutoCommit()+"'"+
                    " and should be '"+autocom+"'");
            conn.close();

        } catch (Exception e) {
            System.out.println("Exception of type :" + e.getClass().getName() + " has been thrown");
            System.out.println("Exception message :" + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

        stop();
    }

    public void printTable() {
        try {
	    STMT = conn.prepareStatement(SQL_REQUEST);
            ResultSet rset = STMT.executeQuery(SQL_REQUEST);
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

    public void stop() {
        try {
           InitialContext ictx = new InitialContext();
           ictx.unbind(USER_TRANSACTION_JNDI_NAME);
        } catch (Exception e) {
            e.printStackTrace();
        }
        jotm.stop();
        jotm = null;
    }

    static public void main(String [] argv) throws Exception{
        SaveAutoCommit spdse = new SaveAutoCommit(argv);
        System.exit(1);
    }
}
