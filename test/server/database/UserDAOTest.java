package server.database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.rmi.ServerException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import server.databaseaccess.*;
import shared.modelclasses.*;

public class UserDAOTest {
	UserDAO ud;
	Database db;
	
	@Before
	public void setup() throws ServerException, SQLException {
		ud = new UserDAO();
		db = new Database();
		db.startTransaction();
		db.loadDefaultTables();
		ud.setParent(db);
		db.setUserDAO(ud);
	}
	
	@After
	public void teardown() {
		db.endTransaction(true);
	}
	
	@Test
	public void add_basic_user() throws SQLException {
		Credentials creds = new Credentials("username", "password");
		User user = new User (creds, new UserInfo("first name", "last name", "emailaddress"));
		ud.addNewUser(user);
		assertTrue(ud.validateUser(creds));
		assertTrue(ud.getUser(creds)!=null);
	}
	
	@Test
	public void add_an_identical_user() throws SQLException, ServerException {
		Credentials creds = new Credentials("username", "password");
		User user = new User (creds, new UserInfo("first name", "last name", "emailaddress"));
		ud.addNewUser(user);
		assertTrue(ud.validateUser(creds));
		assertTrue(ud.getUser(creds)!=null);
		db.endTransaction(true);
		db.startTransaction();
		ud.addNewUser(user);
		db.endTransaction(true);
		db.startTransaction();
		assertTrue(ud.validateUser(creds));
		String sql = "SELECT * FROM User";
		PreparedStatement stmt = db.getConnection().prepareStatement(sql);
		ResultSet rs = stmt.executeQuery();
		rs.next();
		assertFalse(rs.next());
	}
	
	@Test
	public void add_second_user() throws SQLException, ServerException {
		Credentials creds = new Credentials("username", "password");
		User user = new User (creds, new UserInfo("first name", "last name", "emailaddress"));
		ud.addNewUser(user);
		db.endTransaction(true);
		creds.setUsername("new_username");
		db.startTransaction();
		ud.addNewUser(user);
		assertTrue(ud.validateUser(creds));
		assertTrue(ud.getUser(creds)!=null);
	}

}
