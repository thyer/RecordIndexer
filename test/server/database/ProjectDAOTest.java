package server.database;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.rmi.ServerException;
import java.sql.SQLException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import server.databaseaccess.*;
import shared.modelclasses.Field;
import shared.modelclasses.Project;
import shared.modelclasses.ProjectInfo;

public class ProjectDAOTest {
	ProjectDAO pd;
	Database db;
	
	@Before
	public void setup() throws ServerException, SQLException {
		pd = new ProjectDAO();
		db = new Database();
		db.startTransaction();
		db.loadDefaultTables();
		pd.setParent(db);
		db.setProjectDAO(pd);
	}
	
	@After
	public void teardown() {
		db.endTransaction(true);
	}
	
	@Test
	public void add_basic_project() throws SQLException {
		Project project = new Project();
		ProjectInfo pi = new ProjectInfo("Project Title");
		pi.setFirst_y_coord(1);
		project.setProjectinfo(pi);
		pi.setRecord_height(1);
		pi.setRecords_per_image(1);
		assertTrue(pd.getAllProjects().length==0);
		pd.addProject(project);
		assertTrue(pd.getAllProjects().length==1);
	}
	
	@Test
	public void add_identical_project() throws SQLException {
		Project project = new Project();
		ProjectInfo pi = new ProjectInfo("Project Title");
		pi.setFirst_y_coord(1);
		project.setProjectinfo(pi);
		pi.setRecord_height(1);
		pi.setRecords_per_image(1);
		assertTrue(pd.getAllProjects().length==0);
		pd.addProject(project);
		assertTrue(pd.getAllProjects().length==1);
		pd.addProject(project);
		assertTrue(pd.getAllProjects().length==1);
	}
	
	@Test
	public void add_project_with_nulls() throws SQLException {
		Project project = new Project();
		ProjectInfo pi = new ProjectInfo("Project Title");
		project.setProjectinfo(pi);
		assertTrue(pd.getAllProjects().length==0);
		pd.addProject(project);
		assertTrue(pd.getAllProjects().length==1);
		pd.addProject(project);
		assertTrue(pd.getAllProjects().length==1);
	}

}
