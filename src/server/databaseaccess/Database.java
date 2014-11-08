package server.databaseaccess;
import java.io.File;
import java.rmi.ServerException;
import java.sql.*;
import java.util.logging.Logger;

public class Database {
	private static final String DATABASE_DIRECTORY = "database";
	private static final String DATABASE_FILE = "Damocles.sqlite";
	private static final String DATABASE_URL = "jdbc:sqlite:" + DATABASE_DIRECTORY +
												File.separator + DATABASE_FILE;
	private BatchDAO batchDAO;
	private FieldDAO fieldDAO;
	private ProjectDAO projectDAO;
	private UserDAO userDAO;
	private ValueDAO valueDAO;
	private Connection connection;
	static Logger logger = Logger.getLogger("damocles");
	public Database () throws ServerException{
		//System.out.print("Powering up database: " + DATABASE_FILE);
		
		//-----------------Initialize children database access objects-----------------//
		batchDAO = new BatchDAO();
		batchDAO.setParent(this);
		fieldDAO = new FieldDAO();
		fieldDAO.setParent(this);
		projectDAO = new ProjectDAO();
		projectDAO.setParent(this);
		userDAO = new UserDAO();
		userDAO.setParent(this);
		valueDAO = new ValueDAO();
		valueDAO.setParent(this);
		
		//------------------------Load a driver------------------------------------//
		try {
			final String driver = "org.sqlite.JDBC";
			Class.forName(driver);
			//System.out.println("...success!");

		}
		catch(ClassNotFoundException e) {
			throw new ServerException("...failure!", e);
		}
		connection = null;
	}
	
	
	
	public void setBatchDAO(BatchDAO batchDAO) {
		this.batchDAO = batchDAO;
	}



	public void setFieldDAO(FieldDAO fieldDAO) {
		this.fieldDAO = fieldDAO;
	}



	public void setProjectDAO(ProjectDAO projectDAO) {
		this.projectDAO = projectDAO;
	}



	public void setUserDAO(UserDAO userDAO) {
		this.userDAO = userDAO;
	}



	public void setValueDAO(ValueDAO valueDAO) {
		this.valueDAO = valueDAO;
	}



	public Connection getConnection(){
		return connection;
	}
	
	public BatchDAO getBatchDAO() {
		return batchDAO;
	}

	public FieldDAO getFieldDAO() {
		return fieldDAO;
	}

	public ProjectDAO getProjectDAO() {
		return projectDAO;
	}

	public UserDAO getUserDAO() {
		return userDAO;
	}

	public ValueDAO getValueDAO() {
		return valueDAO;
	}

	public void startTransaction() throws ServerException {
		try {
			//System.out.print("Starting transaction");
			assert (connection == null);			
			connection = DriverManager.getConnection(DATABASE_URL);
			connection.setAutoCommit(false);
			//System.out.println("...success!");
		}
		catch (SQLException e) {
			//System.out.println("...failure!");
			throw new ServerException("Could not connect to database. Make sure " + 
				DATABASE_FILE + " is available in ./" + DATABASE_DIRECTORY, e);
		}
	}
	
	public void endTransaction(boolean commit) {
		//System.out.print("Ending transaction...");
		if (connection != null) {		
			try {
				if (commit) {
				 //System.out.println("success, committed!");
					connection.commit();
				}
				else {
					//System.out.println("success, rollback.");
					connection.rollback();
				}
			}
			catch (SQLException e) {
				//System.out.println("failure, could not load transaction.");
				logger.severe("Rollback/Commit failure on database");
				e.printStackTrace();
			}
			finally {
				//System.out.println("Closing connection");
				safeClose(connection);
				connection = null;
			}
		}
	}
		
	public void loadDefaultTables() throws SQLException{
		Statement stmt = connection.createStatement();
		stmt.executeUpdate("DROP TABLE IF EXISTS Value");
		stmt.executeUpdate("DROP TABLE IF EXISTS User");
		stmt.executeUpdate("DROP TABLE IF EXISTS Project");
		stmt.executeUpdate("DROP TABLE IF EXISTS Batch");
		stmt.executeUpdate("DROP TABLE IF EXISTS Field");
		stmt.executeUpdate("  CREATE TABLE Value (ID INTEGER PRIMARY KEY  AUTOINCREMENT  "
				+ "NOT NULL  UNIQUE , batch_id INTEGER NOT NULL , data TEXT, field_id INTEGER NOT NULL, rec_num INTEGER);");
				
		stmt.executeUpdate("CREATE TABLE User (ID INTEGER PRIMARY KEY  AUTOINCREMENT  "
				+ "NOT NULL  UNIQUE , Username VARCHAR NOT NULL  UNIQUE , Password VARCHAR NOT NULL, "
				+ "Firstname VARCHAR, Lastname VARCHAR, Email TEXT, record_count INTEGER DEFAULT 0, current_batch INTEGER);");

		stmt.executeUpdate("CREATE TABLE Project (ID INTEGER PRIMARY KEY  AUTOINCREMENT "
				+ " NOT NULL  UNIQUE , Name TEXT NOT NULL  UNIQUE , Records_per_image INTEGER NOT NULL  DEFAULT 0,"
				+ " first_y_coordinate INTEGER NOT NULL  DEFAULT 0, record_height INTEGER NOT NULL  DEFAULT 0);");

		stmt.executeUpdate("CREATE TABLE Field (ID INTEGER PRIMARY KEY  AUTOINCREMENT "
				+ " NOT NULL  UNIQUE , project_id INTEGER NOT NULL , field_help_file_path TEXT NOT NULL ,"
				+ " known_data_file_path TEXT NOT NULL , xcoord INTEGER NOT NULL , "
				+ "width INTEGER, num INTEGER NOT NULL , title TEXT NOT NULL );");

		stmt.executeUpdate("CREATE TABLE Batch (ID INTEGER PRIMARY KEY  AUTOINCREMENT "
				+ " NOT NULL  UNIQUE , image_file_path TEXT NOT NULL , project_id INTEGER NOT NULL , "
				+ "state INTEGER NOT NULL  DEFAULT 0);");
		safeClose(stmt);
	}
	
	public static void safeClose(Connection conn) {
		if (conn != null) {
			try {
				conn.close();
			}
			catch (SQLException e) {
				// ...
			}
		}
	}
	
	public static void safeClose(Statement stmt) {
		if (stmt != null) {
			try {
				stmt.close();
			}
			catch (SQLException e) {
				// ...
			}
		}
	}
	
	public static void safeClose(PreparedStatement stmt) {
		if (stmt != null) {
			try {
				stmt.close();
			}
			catch (SQLException e) {
				// ...
			}
		}
	}
	
	public static void safeClose(ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			}
			catch (SQLException e) {
				// ...
			}
		}
	}
	
	public static void main(String args[]) throws ServerException, SQLException{
		Database db = new Database();
		db.startTransaction();
		db.loadDefaultTables();
		db.endTransaction(true);
		System.out.println("Terminated program");
	}
}
