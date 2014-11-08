package server.database;

import static org.junit.Assert.*;

import java.rmi.ServerException;
import java.sql.SQLException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import server.databaseaccess.BatchDAO;
import server.databaseaccess.Database;
import server.databaseaccess.ValueDAO;
import shared.modelclasses.Batch;
import shared.modelclasses.Value;

public class ValueDAOTest {
	ValueDAO vd;
	Database db;
	
	@Before
	public void setup() throws ServerException, SQLException {
		vd = new ValueDAO();
		db = new Database();
		db.startTransaction();
		db.loadDefaultTables();
		vd.setParent(db);
		db.setValueDAO(vd);
	}
	
	@After
	public void teardown() {
		db.endTransaction(true);
	}
	
	@Test
	public void add_basic_value() throws SQLException {
		Value value = new Value("data");
		assertFalse(vd.findValue(value));
		vd.addValue(value);
		assertTrue(vd.findValue(value));
	}
	
	@Test
	public void add_duplicate_value() throws SQLException, ServerException {
		Value value = new Value("data");
		assertFalse(vd.findValue(value));
		vd.addValue(value);
		db.endTransaction(true);
		db.startTransaction();
		assertTrue(vd.findValue(value));
		value.setField_id(12);
		vd.addValue(value);
		System.out.println(value.getData() + " " + value.getField_id());
		assertFalse(vd.getValue(value.getData(), value.getField_id())==null);
	}

}
