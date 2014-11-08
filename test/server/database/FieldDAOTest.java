package server.database;

import static org.junit.Assert.*;

import java.rmi.ServerException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import server.databaseaccess.BatchDAO;
import server.databaseaccess.Database;
import server.databaseaccess.FieldDAO;
import shared.modelclasses.Batch;
import shared.modelclasses.Field;

public class FieldDAOTest {
	FieldDAO fd;
	Database db;
	
	@Before
	public void setup() throws ServerException, SQLException {
		fd = new FieldDAO();
		db = new Database();
		db.startTransaction();
		db.loadDefaultTables();
		fd.setParent(db);
		db.setFieldDAO(fd);
	}
	
	@After
	public void teardown() {
		db.endTransaction(true);
	}
	
	@Test
	public void add_basic_field() throws SQLException {
		Field field = new Field("title");
		field.setField_help_file_path(("help file"));
		field.setKnown_data_path("knownstuff");
		field.setNum(290);
		field.setProjectID(1);
		field.setWidth(10);
		field.setXcoordinate(20);
		assertTrue(fd.getField("title")==null);
		fd.updateField(field);
		assertFalse(fd.getField("title")==null);
	}
	
	@Test
	public void add_second_field() throws SQLException, ServerException {
		Field field = new Field("title");
		field.setField_help_file_path(("help file"));
		field.setKnown_data_path("knownstuff");
		field.setNum(290);
		field.setProjectID(1);
		field.setWidth(10);
		field.setXcoordinate(20);
		assertTrue(fd.getField("title")==null);
		fd.updateField(field);
		assertFalse(fd.getField("title")==null);
		db.endTransaction(true);
		db.startTransaction();
		field.setField_help_file_path("newstuff");
		field.setNum(12);
		field.setProjectID(17);
		fd.updateField(field);
		assertFalse(fd.getField("title")==null);
		String sql = "SELECT *"
				+ "FROM Field"; 
				
		PreparedStatement stmt = db.getConnection().prepareStatement(sql);
		ResultSet keyRS = stmt.executeQuery();
		keyRS.next();
		assertTrue(keyRS.next());
	}
	
	@Test
	public void add_identical_field() throws SQLException, ServerException {
		Field field = new Field("title");
		field.setField_help_file_path(("help file"));
		field.setKnown_data_path("knownstuff");
		field.setNum(290);
		field.setProjectID(1);
		field.setWidth(10);
		field.setXcoordinate(20);
		assertTrue(fd.getField("title")==null);
		fd.updateField(field);
		assertFalse(fd.getField("title")==null);
		db.endTransaction(true);
		db.startTransaction();
		fd.updateField(field);
		assertFalse(fd.getField("title")==null);
		
		String sql = "SELECT *"
				+ "FROM Field"; 
				
		PreparedStatement stmt = db.getConnection().prepareStatement(sql);
		ResultSet keyRS = stmt.executeQuery();
		keyRS.next();
		assertFalse(keyRS.next());
	}

}
