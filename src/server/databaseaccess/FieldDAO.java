package server.databaseaccess;
import java.rmi.ServerException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import shared.modelclasses.*;

public class FieldDAO {
	private Database parent;
	public FieldDAO(){
		
	}
	
	public void setParent(Database db){
		this.parent = db;
	}
	
	/**
	 * Searches for a field with the given title
	 * @param title the title for which to search
	 * @return The field, if found, else null
	 * @throws SQLException 
	 */
	public Field getField(String title) throws SQLException{
		String sql = "SELECT ID, project_id, field_help_file_path, known_data_file_path, xcoord, width, num, title "
				+ "FROM Field "
				+ "WHERE title = ?"; 
		PreparedStatement stmt = parent.getConnection().prepareStatement(sql); 
		stmt.setString(1, title);
		ResultSet keyRS = stmt.executeQuery();
		if(!keyRS.next()){
			return null;
		}
		Field field = new Field(title);
		field.setID(keyRS.getInt("ID"));
		field.setProjectID(keyRS.getInt("project_id"));
		field.setField_help_file_path(keyRS.getString("field_help_file_path"));
		field.setKnown_data_path(keyRS.getString("known_data_file_path"));
		field.setXcoordinate(keyRS.getInt("xcoord"));
		field.setWidth(keyRS.getInt("width"));
		field.setNum(keyRS.getInt("num"));
		field.setTitle(keyRS.getString("title"));

		return field;
	}
	
	/**
	 * Searches for a field with the given project_id
	 * @param project_id the ID to search for
	 * @return All fields, if found, else null
	 * @throws SQLException 
	 */
	public Field[] getFields(int project_id) throws SQLException{
		String sql = "SELECT ID, project_id, field_help_file_path, known_data_file_path, xcoord, width, num, title "
				+ "FROM Field "
				+ "WHERE project_id = ?"; 
		PreparedStatement stmt = parent.getConnection().prepareStatement(sql); 
		stmt.setString(1, Integer.toString(project_id));
		ResultSet keyRS = stmt.executeQuery();
		int i=0;
		while(keyRS.next()){
			++i;
		}
		keyRS = stmt.executeQuery();
		if(i==0){
			return null;
		}
		Field[] fields = new Field[i];
		i=0;
		while(keyRS.next()){
			Field field = new Field("");
			field.setID(keyRS.getInt("ID"));
			field.setProjectID(keyRS.getInt("project_id"));
			field.setField_help_file_path(keyRS.getString("field_help_file_path"));
			field.setKnown_data_path(keyRS.getString("known_data_file_path"));
			field.setXcoordinate(keyRS.getInt("xcoord"));
			field.setWidth(keyRS.getInt("width"));
			field.setNum(keyRS.getInt("num"));
			field.setTitle(keyRS.getString("title"));
			fields[i]=field;
			++i;
		}


		return fields;
	}
	
	public void updateField(Field field){
		Connection connection = parent.getConnection();
		if(connection==null){
			System.out.println("Could not update field. No valid connection available");
			return;
		}
		
		try {
			if(find(field)){
				//System.out.println("Found existing field, removing from table");
				removeField(field);
			}
			addField(field);

		} catch (SQLException e) {
			System.out.println("Problem updating field");
			e.printStackTrace();
		} 

		
	}
	
	private void removeField(Field field) throws SQLException{
		String sql = "DELETE FROM Field WHERE title = ? AND project_id = ?;"; 
		PreparedStatement stmt = parent.getConnection().prepareStatement(sql);
		stmt.setString(1, field.getTitle());
		stmt.setString(2, Integer.toString(field.getProjectID()));
		
		try{
			stmt.executeUpdate();
		} catch (SQLException e){
			System.out.println("***Problem executing remove***");
			e.printStackTrace();
		}
		
	}
	
	private void addField(Field field) throws SQLException{
		String sql = "INSERT INTO Field (project_id, field_help_file_path, known_data_file_path, xcoord, width, num, title) "
																							+ "values (?, ?, ?, ?, ?, ?, ?);";
		PreparedStatement stmt = parent.getConnection().prepareStatement(sql);
		stmt.setString(1, Integer.toString(field.getProjectID()));
		stmt.setString(2, (field.getField_help_file_path()));
		stmt.setString(3, field.getKnown_data_path());
		stmt.setString(4, Integer.toString(field.getXcoordinate()));
		stmt.setString(5, Integer.toString(field.getWidth()));
		stmt.setString(6, Integer.toString(field.getNum()));
		stmt.setString(7, (field.getTitle()));
		stmt.executeUpdate();
		//System.out.println("Inserted new member into tables");
		stmt.close();
	}
	
	/**
	 * Searches the database for a given batch object. Returns true if found and false if not
	 * @param batch the batch to be searched for
	 * @return true if an existing batch object is found with the same image file path and project ID.
	 * @throws SQLException
	 */
	private boolean find(Field field) throws SQLException{
		String sql = "SELECT *"
				+ "FROM Field "
				+ "WHERE title = ? AND project_id = ?"; 
				
		PreparedStatement stmt = parent.getConnection().prepareStatement(sql); 
		stmt.setString(1, field.getTitle());
		stmt.setString(2, Integer.toString(field.getProjectID()));
		ResultSet keyRS = stmt.executeQuery();

		return (keyRS.next());
	}
	

	public static void main (String args[]) throws ServerException, SQLException{
		Database db = new Database();

		FieldDAO fd = new FieldDAO();
		fd.setParent(db);
		Field field = new Field("title");
		field.setID(1);
		field.setProjectID(1);
		field.setField_help_file_path("fieldhelp file path");
		field.setKnown_data_path("known path");
		field.setXcoordinate(10);
		field.setWidth(20);
		field.setNum(20);
		db.startTransaction();
		db.loadDefaultTables();
		fd.updateField(field);
		db.endTransaction(false);
		db.startTransaction();
		fd.updateField(field);
		db.endTransaction(true);
		field.setField_help_file_path("Dantley");
		db.startTransaction();
		fd.updateField(field);
		db.endTransaction(true);

	}
}
