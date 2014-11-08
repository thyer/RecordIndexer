package server.databaseaccess;

import java.rmi.ServerException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import shared.modelclasses.Batch;
import shared.modelclasses.Project;
import shared.modelclasses.Record;

public class BatchDAO {
	private Database parent;
	public BatchDAO(){
		
	}
	
	public void setParent(Database db){
		this.parent = db;
	}
	
	public Batch getBatch(Project prj) throws SQLException{
		int ID;
		String image_file_path;
		int project_id=prj.getProjectinfo().getID();
		int state;
		
		String sql = "SELECT *"
				+ "FROM Batch "
				+ "WHERE project_id = ? AND state = 0"; 
		PreparedStatement stmt = parent.getConnection().prepareStatement(sql); 
		stmt.setString(1, Integer.toString(project_id));
		ResultSet keyRS = stmt.executeQuery();

		if(!keyRS.next()){
			return null;
		}

		ID = keyRS.getInt(1);
		image_file_path = keyRS.getString(2);
		project_id = keyRS.getInt(3);
		state = keyRS.getInt(4);
		Batch result = new Batch();
			result.setID(ID);
			result.setImage_file_path(image_file_path);
			result.setProject_id(project_id);
			result.setState(state);
		
		return result;
	}
	
	/**
	 * Returns a Batch object with a given ID
	 * @param id the ID to be searched for
	 * @return The Batch object if found, else null
	 * @throws SQLException 
	 */
	public Batch getBatch(int id) throws SQLException{
		int ID = id;
		String image_file_path;
		int project_id;
		int state;
		
		String sql = "SELECT *"
				+ "FROM Batch "
				+ "WHERE ID = ?"; 
		PreparedStatement stmt = parent.getConnection().prepareStatement(sql); 
		stmt.setString(1, Integer.toString(ID));
		ResultSet keyRS = stmt.executeQuery();

		if(!keyRS.next()){
			return null;
		}

		ID = keyRS.getInt(1);
		image_file_path = keyRS.getString(2);
		project_id = keyRS.getInt(3);
		state = keyRS.getInt(4);
		Batch result = new Batch();
			result.setID(ID);
			result.setImage_file_path(image_file_path);
			result.setProject_id(project_id);
			result.setState(state);
		
		return result;
	}
	
	/**
	 * Privately removes a batch from the Batch table
	 * @param batch the batch to be removed
	 * @throws SQLException
	 */
	private void removeBatch(Batch batch) throws SQLException{
		String sql = "DELETE FROM Batch WHERE image_file_path = ? AND project_id = ?;"; 
		PreparedStatement stmt = parent.getConnection().prepareStatement(sql);
		stmt.setString(1, batch.getImage_file_path());
		stmt.setString(2, Integer.toString(batch.getProject_id()));
		
		try{
			stmt.executeUpdate();
		} catch (SQLException e){
			System.out.println("***Problem executing remove***");
			e.printStackTrace();
		}
		
	}
	
	private int addBatch(Batch batch) throws SQLException{
		if(batch.getID()==0){
			String sql = "INSERT INTO Batch (image_file_path, project_id, state) values (?, ?, ?);";
			PreparedStatement stmt = parent.getConnection().prepareStatement(sql);
			stmt.setString(1, batch.getImage_file_path());
			stmt.setString(2, Integer.toString(batch.getProject_id()));
			stmt.setString(3, Integer.toString(batch.getState()));
			stmt.executeUpdate();
			//System.out.println("Inserted new member into tables");
			//stmt.close();
			
			sql = "SELECT * FROM Batch WHERE image_file_path = ?";
			stmt = parent.getConnection().prepareStatement(sql);
			stmt.setString(1, batch.getImage_file_path());
			ResultSet rs = stmt.executeQuery();
			rs.next();
			
			return rs.getInt(1);
		}
		else{
			String sql = "INSERT INTO Batch (ID, image_file_path, project_id, state) values (?, ?, ?, ?);";
			PreparedStatement stmt = parent.getConnection().prepareStatement(sql);
			stmt.setString(1, Integer.toString(batch.getID()));
			stmt.setString(2, batch.getImage_file_path());
			stmt.setString(3, Integer.toString(batch.getProject_id()));
			stmt.setString(4, Integer.toString(batch.getState()));
			stmt.executeUpdate();
			
			return batch.getID();
		}
		
	}
	
	/**
	 * Searches the database for a given batch object. Returns true if found and false if not
	 * @param batch the batch to be searched for
	 * @return true if an existing batch object is found with the same image file path and project ID.
	 * @throws SQLException
	 */
	public boolean find(Batch batch) throws SQLException{
		int ID = batch.getID();
		String image_file_path = batch.getImage_file_path();
		int project_id = batch.getProject_id();
		int state = batch.getState();
		String sql = "SELECT *"
				+ "FROM Batch "
				+ "WHERE image_file_path = ? AND project_id = ?"; 
		
		String secondSQL = "SELECT * FROM Batch WHERE image_file_path = ? AND project_id = ?";
		PreparedStatement secondStmt = parent.getConnection().prepareStatement(secondSQL);
		secondStmt.setString(1, "string");
		secondStmt.setString(2, "1");
		ResultSet rs = secondStmt.executeQuery();
		
		
		PreparedStatement stmt = parent.getConnection().prepareStatement(sql); 
		stmt.setString(1, image_file_path);
		stmt.setString(2, Integer.toString(project_id));
		ResultSet keyRS = stmt.executeQuery();

		return (keyRS.next());
	}
	
	/**
	 * Updates a batch in the database with a new one
	 * Searches the database for a batch with the same image_file_path, deletes it, and adds the new one
	 * If no such batch is found, the new batch will just be added
	 * @param batch the updated Batch object
	 */
	public int updateBatch(Batch batch){
		Connection connection = parent.getConnection();
		if(connection==null){
			System.out.println("Could not update batch. No valid connection available");
			return 0 ;
		}
		
		try {
			if(find(batch)){
				//System.out.println("Found existing batch, removing from table");
				removeBatch(batch);
			}
			return addBatch(batch);

		} catch (SQLException e) {
			System.out.println("Problem updating batch");
			e.printStackTrace();
			return 0;
		} 

		
	}
	
	
	public static void main (String args[]) throws ServerException, SQLException{
		Database db = new Database();
		db.startTransaction();
		db.loadDefaultTables();
		BatchDAO bd = new BatchDAO();
		bd.setParent(db);
		Batch batch = new Batch();
		batch.setID(1);
		batch.setImage_file_path("string");
		batch.setProject_id(1);
		batch.setState(0);
		bd.updateBatch(batch);
		db.endTransaction(true);
		db.startTransaction();
		bd.updateBatch(batch);
		db.endTransaction(true);
		db.startTransaction();
		batch.setImage_file_path("rootfoldersomewhere");
		bd.updateBatch(batch);
		//bd.getRecords(1);
		db.endTransaction(true);
	}
	
}
