package server.databaseaccess;
import java.rmi.ServerException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import shared.modelclasses.*;

public class UserDAO {
	private Database parent;
	public UserDAO(){
		
	}
	/**
	 * Adds a new user to the database. If existing credentials already exist, nothing happens
	 * @param newuser the user to be added
	 * @throws SQLException 
	 */
	public void addNewUser(User newuser) throws SQLException{
		Credentials creds = newuser.getCreds();
		if(validateUser(creds)){
			//System.out.println("User credentials already exist. No new user added");
			return;
		}
		String sql = "INSERT INTO User (Username, Password, Firstname, Lastname, Email, record_count, current_batch) "
				+ "values (?, ?, ?, ?, ?, ?, ?);";
		PreparedStatement stmt = parent.getConnection().prepareStatement(sql);
		stmt.setString(1, newuser.getCreds().getUsername());
		stmt.setString(2, newuser.getCreds().getPassword());
		stmt.setString(3, newuser.getUserinfo().getFirstName());
		stmt.setString(4, newuser.getUserinfo().getLastName());
		stmt.setString(5, newuser.getUserinfo().getEmail());
		stmt.setString(6, Integer.toString(newuser.getRecordCount()));
		stmt.setString(7, Integer.toString(newuser.getCurrentBatch()));
		stmt.executeUpdate();
		//System.out.println("Inserted new user into tables");
		stmt.close();
		
	}
	/**
	 * Accepts a user object and queries the database to determine whether or not the user is found
	 * @param creds the user's credentials to be validated
	 * @return returns true if the user is found in the database else false
	 * @throws SQLException 
	 */
	public boolean validateUser(Credentials creds) throws SQLException{
		String sql = "SELECT *"
				+ "FROM User "
				+ "WHERE Username = ? AND Password = ?"; 
				
		PreparedStatement stmt = parent.getConnection().prepareStatement(sql); 
		stmt.setString(1, creds.getUsername());
		stmt.setString(2, creds.getPassword());
		ResultSet keyRS = stmt.executeQuery();

		return (keyRS.next());
	}
	/**
	 * Returns the user's name and email based on credentials 
	 * @param creds the user's credentials
	 * @return a Credentials object associated with that user. If no user is found, returns null
	 * @throws SQLException 
	 */
	public UserInfo getUserInfo (Credentials creds) throws SQLException{
		String sql = "SELECT *"
				+ "FROM User "
				+ "WHERE Username = ? AND Password = ?"; 
				
		PreparedStatement stmt = parent.getConnection().prepareStatement(sql); 
		stmt.setString(1, creds.getUsername());
		stmt.setString(2, creds.getPassword());
		ResultSet keyRS = stmt.executeQuery();
		
		if(keyRS.next()){
			return new UserInfo(keyRS.getString(4), keyRS.getString(5), keyRS.getString(6));
		}
		else
			return null;
	}
	/**
	 * Returns all information pertaining to a user
	 * @param creds the user's credentials
	 * @return returns a User object if found, else null
	 * @throws SQLException 
	 */
	public User getUser(Credentials creds) throws SQLException{
		String sql = "SELECT *"
				+ " FROM User "
				+ " WHERE Username = ? AND Password = ?"; 
				
		PreparedStatement stmt = parent.getConnection().prepareStatement(sql); 
		stmt.setString(1, creds.getUsername());
		stmt.setString(2, creds.getPassword());
		ResultSet keyRS = stmt.executeQuery();
		
		keyRS.next();
		
		UserInfo ui = new UserInfo(keyRS.getString(4), keyRS.getString(5), keyRS.getString(6));
		User output = new User(creds, ui);
		output.setID(keyRS.getInt(1));
		output.setCurrentBatch(keyRS.getInt(8));
		output.setRecordCount(keyRS.getInt(7));
		
		return output;
	}
	
	/**
	 * Replaces an existing user with an updated version. Adds the user if no such user yet exists.
	 * Finds a user with the same credentials, removes that user from the table, then adds the updated version
	 * @param user the updated version to replace the existing user
	 * @throws SQLException 
	 */
	public void updateUser (User user) throws SQLException{
		Credentials temp = user.getCreds();
		if(validateUser(temp)){
			removeUser(temp);
		}
		addNewUser(user);
		
		
	}
	
	/**
	 * Queries the database for a user based on credentials and removes the user if found
	 * @param creds the credentials to be checked
	 * @throws SQLException 
	 */
	private void removeUser(Credentials creds) throws SQLException{
		String sql = "DELETE FROM User WHERE Username = ? AND Password = ?;"; 
		PreparedStatement stmt = parent.getConnection().prepareStatement(sql);
		stmt.setString(1, creds.getUsername());
		stmt.setString(2, creds.getPassword());
		
		try{
			stmt.executeUpdate();
		} catch (SQLException e){
			System.out.println("***Problem executing remove***");
			e.printStackTrace();
		}
	}
	
	public void setParent(Database db){
		this.parent = db;
	}
	
	public static void main(String args[]) throws ServerException, SQLException{
		Database db = new Database();
		UserDAO ud = new UserDAO();
		ud.setParent(db);
		db.setUserDAO(ud);
		Credentials creds = new Credentials("myUsername", "popchart");
		UserInfo ui = new UserInfo("Trent Q.", "Example", "hot_thang@gmail.com");
		User user = new User(creds, ui);
		db.startTransaction();
		db.loadDefaultTables();
		ud.addNewUser(user);
		db.endTransaction(true);
		db.startTransaction();
		ud.getUser(creds);
		creds.setUsername("Blackfire");
		ud.addNewUser(user);
		db.endTransaction(true);
		
	}

}
