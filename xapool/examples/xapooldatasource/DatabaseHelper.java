
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.naming.InitialContext;
import javax.sql.XAConnection;
import javax.sql.XADataSource;
import org.objectweb.jotm.Jotm;
import org.objectweb.transaction.jta.TMService;

import org.enhydra.jdbc.standard.StandardXADataSource;


public class DatabaseHelper {

    private String password;
    private TMService jotm;
    private XADataSource xads;
    private String login;
    private static final String USER_TRANSACTION_JNDI_NAME = "UserTransaction";

    /**
     * Constructor for DatabaseHelper.
     */
    public DatabaseHelper() {
        Properties props = new Properties();
        try {
            props.load(ClassLoader.getSystemResourceAsStream("spy.properties"));
        } catch (Exception e) {
            System.err.println("no properties file found to init the database");
            System.exit(1);
        }

        System.out.println("\n database configuration:");
        props.list(System.out);
        System.out.println("------------------------\n");

        login = props.getProperty("login");
        password = props.getProperty("password");

        // Get a transction manager       
        try {
        	// creates an instance of JOTM with a local transaction factory which is not bound to a registry
            jotm = new Jotm(true, false);
            InitialContext ictx = new InitialContext();
            ictx.rebind(USER_TRANSACTION_JNDI_NAME, jotm.getUserTransaction());
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        xads = new StandardXADataSource();
        try {
            ((StandardXADataSource) xads).setDriverName(props.getProperty("driver"));
            ((StandardXADataSource) xads).setUrl(props.getProperty("url"));
            ((StandardXADataSource) xads).setTransactionManager(jotm.getTransactionManager());
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public Connection getConnection() throws SQLException {
        XAConnection xaconn = xads.getXAConnection(login, password);
        return xaconn.getConnection();
    }
    /**
     * Method stop.
     */
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

}

