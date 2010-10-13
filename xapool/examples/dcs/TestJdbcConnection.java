import org.enhydra.jdbc.pool.StandardPoolDataSource;
import org.enhydra.jdbc.standard.StandardConnectionPoolDataSource;

import java.sql.Statement;
import java.sql.Connection;

public class TestJdbcConnection extends TesterObject {
    String url = null;
    String username = null;
    String password = null;
    String driver = null;

    public static void main(String[] args) {
	TestJdbcConnection jdbc = new TestJdbcConnection(args[0], args[1], args[2], args[3]);
	jdbc.testIt();
    }


    public TestJdbcConnection(String u, String user, String p, String d) {
	url = u;
	username = user;
	password = p;
	driver = d;
    }

    public int testIt() {
	try {

	    StandardConnectionPoolDataSource connect = new StandardConnectionPoolDataSource();
	    
	    connect.setUrl(url);
	    connect.setDriverName(driver);
	    connect.setUser(username);
	    connect.setPassword(password);
	    
	    // second, we create the pool of connection with the previous object
	    StandardPoolDataSource spds = new StandardPoolDataSource(connect);

	    spds.setMinSize(1);
	    spds.setMaxSize(2);
	    spds.setUser(username);
	    spds.setPassword(password);
	    spds.setCheckLevelObject(1);
	    System.out.println("username="+username+" password="+password+" driver="+driver+" url="+url);

	    System.out.println("before getConnection");	    
	    Connection db = spds.getConnection(username, password);
	    System.out.println("before createStatement");
	    Statement stmt = db.createStatement();
	    System.out.println("before execute");
	    stmt.executeUpdate("close connection");
	    System.out.println("before close");
	    db.close();



	    System.out.println("");
	    System.out.println("2 before getConnection");	    
	    db = spds.getConnection(username, password);
	    System.out.println("2 before createStatement");
	    stmt = db.createStatement();
	    System.out.println("2 before execute");
	    stmt.executeUpdate("close connection");
	    System.out.println("2 before close");
	    db.close();


	    System.out.println("");
	    System.out.println("3 before getConnection");	    
	    db = spds.getConnection(username, password);
	    System.out.println("3 before createStatement");
	    stmt = db.createStatement();
	    System.out.println("3 before execute");
	    stmt.executeUpdate("3 close connection;");
	    System.out.println("3 before close");
	    db.close();

	    System.out.println("before return 1");
	    return 1;
	} catch (Exception e) {
	    System.out.println("ERROR! impossible to get a JDBC Connection");
	    e.printStackTrace();
	    return 0;
	}

    }

}
