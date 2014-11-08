package server.database;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.rmi.ServerException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import server.databaseaccess.BatchDAO;
import server.databaseaccess.Database;
import server.databaseaccess.UserDAO;
import shared.modelclasses.Batch;
import shared.modelclasses.Credentials;
import shared.modelclasses.User;
import shared.modelclasses.UserInfo;

public class BatchDAOTest {
	BatchDAO bd;
	Database db;
	
	@Before
	public void setup() throws ServerException, SQLException {
		bd = new BatchDAO();
		db = new Database();
		db.startTransaction();
		db.loadDefaultTables();
		bd.setParent(db);
		db.setBatchDAO(bd);
	}
	
	@After
	public void teardown() {
		db.endTransaction(true);
	}
	
	@Test
	public void add_basic_batch() throws SQLException {
		Batch batch = new Batch();
		batch.setImage_file_path("new image file path");
		batch.setProject_id(1);
		batch.setState(0);
		bd.updateBatch(batch);
		assertTrue(bd.find(batch));
	}
	
	@Test
	public void add_two_batches() throws SQLException {
		Batch batch = new Batch();
		batch.setImage_file_path("new image file path");
		batch.setProject_id(1);
		batch.setState(0);
		bd.updateBatch(batch);
		assertTrue(bd.find(batch));
		batch.setProject_id(2);
		batch.setImage_file_path("newString");
		bd.updateBatch(batch);
		assertTrue(bd.find(batch));
	}
	
	@Test
	public void add_identicalBatch() throws SQLException, ServerException {
		Batch batch = new Batch();
		batch.setImage_file_path("new image file path");
		batch.setProject_id(1);
		batch.setState(0);
		bd.updateBatch(batch);
		assertTrue(bd.find(batch));
		bd.updateBatch(batch);
		assertTrue(bd.find(batch));
		String sql = "SELECT *"
				+ "FROM Batch"; 
				
		PreparedStatement stmt = db.getConnection().prepareStatement(sql);
		ResultSet keyRS = stmt.executeQuery();
		keyRS.next();
		assertFalse(keyRS.next());
		db.endTransaction(true);
		db.startTransaction();
		assertTrue(bd.getBatch(7)==null);
	}

}
