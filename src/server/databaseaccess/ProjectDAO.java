package server.databaseaccess;

import java.rmi.ServerException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import shared.modelclasses.Project;
import shared.modelclasses.ProjectInfo;
import shared.modelclasses.Record;
import shared.modelclasses.User;
import shared.modelclasses.UserInfo;

public class ProjectDAO {
	private Database parent;
	public ProjectDAO (){
		
	}
	/**
	 * Returns all projects in the database
	 * @return An array of Project objects
	 * @throws SQLException 
	 */
	public Project[] getAllProjects() throws SQLException{
		String sql = "SELECT *"
				+ "FROM Project "; 
			
		
		PreparedStatement stmt = parent.getConnection().prepareStatement(sql);
		ResultSet keyRS = stmt.executeQuery();
		int i = 0;
		while(keyRS.next()){
			//System.out.println("Found result Projects: " + i);
			++i;
		}
		keyRS = stmt.executeQuery();
		Project[] output = new Project[i];
		//System.out.println("Total Projects found: " + i);
		i=0;
		while(keyRS.next()){
			Project project = new Project();
			ProjectInfo projectinfo = new ProjectInfo(keyRS.getString(2));
			projectinfo.setID(keyRS.getInt(1));
			projectinfo.setRecords_per_image(keyRS.getInt(3));
			projectinfo.setFirst_y_coord(keyRS.getInt(4));
			projectinfo.setRecord_height(keyRS.getInt(5));
			project.setProjectinfo(projectinfo);
			output[i] = project;
			++i;
		}
		
		return output;
	}
	
	/**
	 * Returns a project object based on the ID given
	 * @param id the ID to be searched for
	 * @return A project object associated with the input ID. Returns null if the ID is not found
	 * @throws SQLException 
	 */
	public Project getProject(int id) throws SQLException{
		String sql = "SELECT *"
				+ "FROM Project "
				+ "WHERE ID = ?"; 
				
		PreparedStatement stmt = parent.getConnection().prepareStatement(sql); 
		stmt.setString(1, Integer.toString(id));
		ResultSet keyRS = stmt.executeQuery();
		
		keyRS.next();
		
		Project project = new Project();
		ProjectInfo projectinfo = new ProjectInfo(keyRS.getString(2));
		projectinfo.setID(keyRS.getInt(1));
		projectinfo.setRecords_per_image(keyRS.getInt(3));
		projectinfo.setFirst_y_coord(keyRS.getInt(4));
		projectinfo.setRecord_height(keyRS.getInt(5));
		project.setProjectinfo(projectinfo);
		
		return project;
	}
	
	public boolean find(Project project) throws SQLException{
		String sql = "SELECT *"
				+ "FROM Project "
				+ "WHERE Name = ?"; 
				
		PreparedStatement stmt = parent.getConnection().prepareStatement(sql); 
		stmt.setString(1, project.getProjectinfo().getName());
		ResultSet keyRS = stmt.executeQuery();
		
		return(keyRS.next());
	}
	
	/**
	 * Adds a project if it doesn't already exist and returns the ID of the added project
	 * @param project the project to be added
	 * @return the ID of the project after it's been added
	 * @throws SQLException
	 */
	public int addProject(Project project) throws SQLException{
		if(find(project)){
			//System.out.println("Cannot add duplicate value");
			return 0;
		}
		String sql = "INSERT INTO Project (Name, Records_per_image, first_y_coordinate, record_height) "
				+ "values (?, ?, ?, ?);";
		PreparedStatement stmt = parent.getConnection().prepareStatement(sql);
		stmt.setString(1, project.getProjectinfo().getName());
		stmt.setString(2, Integer.toString(project.getProjectinfo().getRecords_per_image()));
		stmt.setString(3, Integer.toString(project.getProjectinfo().getFirst_y_coord()));
		stmt.setString(4, Integer.toString(project.getProjectinfo().getRecord_height()));
		stmt.executeUpdate();
		//System.out.println("Inserted new member into tables");
		stmt.close();
		
		sql = "SELECT * FROM Project WHERE Name = ? AND Records_per_image = ?";
		stmt = parent.getConnection().prepareStatement(sql);
		stmt.setString(1, project.getProjectinfo().getName());
		stmt.setString(2, Integer.toString(project.getProjectinfo().getRecords_per_image()));
		
		ResultSet rs = stmt.executeQuery();
		rs.next();
		
		return rs.getInt(1);
	}
	
	public void setParent(Database db){
		this.parent = db;
	}
	
	public static void main (String args[]) throws ServerException, SQLException{
		Database db = new Database();
		ProjectDAO pd = new ProjectDAO();
		pd.setParent(db);
		db.startTransaction();
		db.loadDefaultTables();
		db.endTransaction(true);
		db.startTransaction();
		ProjectInfo pi = new ProjectInfo("name");
		pi.setID(1);
		pi.setFirst_y_coord(1);
		pi.setRecord_height(1);
		pi.setRecords_per_image(1);
		Project project = new Project();
		project.setProjectinfo(pi);
		pd.addProject(project);
		db.endTransaction(true);
	}

}
