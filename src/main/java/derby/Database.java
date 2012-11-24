package derby;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;


/**
 * This code creates an instance of the Apache Derby Database running in
 * embedded mode. Instantiating the database does not, by default, start
 * up the database.
 *  
 * @author zozer
 */
public class Database
{
	/* the default framework is embedded*/
	private String framework;
	private String driver;
	private String protocol;
	private Statement s;
	private Connection conn;
	
	/**
	 * Database constructor. Instantiating the database does not start up the
	 * database.
	 *
	 */
	public Database() {
		framework = "embedded";
		driver = "org.apache.derby.jdbc.EmbeddedDriver";
		protocol = "jdbc:derby:";
	}
	
	/**
	 * Starts up the database.
	 * @return boolean (true if runs to completion)
	 * @throws DBException (If the database fails to startup from a driver error)
	 */
	public boolean startup() throws DBException{		
		try {
			/*
               The driver is installed by loading its class.
               In an embedded environment, this will start up Derby, since it is not already running.
			 */
			Class.forName(driver).newInstance();
		//System.out.println("Loaded the appropriate driver.");
		//System.out.println("Derby starting in " + framework + " mode.");
			conn = null;
			Properties props = new Properties();
			props.put("user", "user1");
			props.put("password", "user1");
			
			/*
               The connection specifies create=true to cause
               the database to be created. To remove the database,
               remove the directory derbyDB and its contents.
               The directory derbyDB will be created under
               the directory that the system property
               derby.system.home points to, or the current
               directory if derby.system.home is not set.
			 */
			conn = DriverManager.getConnection(protocol +
					"rss;create=true", props);
			
		//System.out.println("Connected to and created database rss");
			
			conn.setAutoCommit(true);
			
			/*
               Creating a statement lets us issue commands against
               the connection.
			 */
			
			s = conn.createStatement();

		} catch (Exception e) {
			throw new DBException("Database failed to startup: "+e);
		}
		return true;
	}
	
	/**
	 * Performs modifications on the database. 
	 * @param sqlstatement Any valid SQL statement
	 * @return boolean (true if the query successfully executes, false o/w)
	 * @throws DBException for invalid SQL Syntax or if the query fails to
	 * execute
	 */
	synchronized public boolean modify(String sqlstatement) throws DBException{
		boolean result = false;
		try {
			//System.out.println(sqlstatement);
			result = !s.execute(sqlstatement);
			if (result != true){
				throw new DBException("DB modifcation failed.");
			}
		} catch (SQLException e) {
			//System.out.println(sqlstatement);
			throw new DBException("DB modification failed because of a SQLError: "+e);
		} catch (Exception e){
			//System.out.println(sqlstatement);
			throw new DBException("DB modifcation failed because of an unknown exception: "+e);
		}
		return result; 
	}
	/**
	 * Performs a query on an existing database and returns the ResultSet
	 * @param sqlstatement Any valid SQL statement
	 * @return ResultSet (never null)
	 * @throws DBException for invalid SQL Syntax
	 */
	synchronized public ResultSet query(String sqlstatement) throws DBException{
		ResultSet rs = null;
		try {
			//System.out.println(sqlstatement);
			rs = s.executeQuery(sqlstatement);
		} catch (SQLException e) {
			//System.out.println(sqlstatement);
			throw new DBException("DB search failed because of a SQLError: "+e);
		} catch (Exception e){
			//System.out.println(sqlstatement);
			throw new DBException("DB search failed because of an unknown exception: "+e);
		}
		return rs;
	}
	
	/**
	 * Shuts down the database.
	 * @return boolean True if the database was properly shutdown, false o/w
	 * @throws DBException
	 */
	public boolean shutdown() throws DBException{
		try {
			/*
        We release the result and statement resources.
			 */
			s.close();
		//System.out.println("Closed result set and statement");
			s = null;
			
			/*
        We end the transaction and the connection.*/
			conn.commit();
			conn.close();
		//System.out.println("Committed transaction and closed connection");
		} catch (Throwable e) {
			throw new DBException("Database failed to shutdown.");
		}
		/*
        In embedded mode, an application should shut down Derby.
        If the application fails to shut down Derby explicitly,
        the Derby does not perform a checkpoint when the JVM shuts down, which means
        that the next connection will be slower.
        Explicitly shutting down Derby with the URL is preferred.
        This style of shutdown will always throw an "exception".
		 */
		boolean gotSQLExc = false;
		
		if (framework.equals("embedded"))
		{
			try
			{
				DriverManager.getConnection("jdbc:derby:;shutdown=true");
			}
			catch (SQLException se)
			{
				gotSQLExc = true;
			}
			
			if (!gotSQLExc)
			{
				throw new DBException("Database did not shut down normally");
			}
			else
			{
			//System.out.println("Database shut down normally");
			}
		}
		
	//System.out.println("Derby finished");
		
		return gotSQLExc;
	}
	
	/**
	 * Boolean for telling if the database is currently running.
	 * @return boolean
	 */
	public boolean isReady(){
		return !(s==null);
	}
	
}
