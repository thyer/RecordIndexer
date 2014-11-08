package server.databaseaccess;

import java.rmi.ServerException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import shared.modelclasses.Value;

public class ValueDAO {
	Database parent;
	
	/**
	 * Searches the Values table based on a data string and the fieldID attributed with it
	 * @param data the String value to search for
	 * @param fieldID the foreign key to search for
	 * @return The Value, if found, else null
	 * @throws SQLException 
	 */
	public Value getValue(String data, int fieldID) throws SQLException{
		String sql = "SELECT *"
				+ " FROM Value"
				+ " WHERE data = ? AND field_id = ?";
		PreparedStatement stmt = parent.getConnection().prepareStatement(sql);
		stmt.setString(1, data.toUpperCase());
		stmt.setString(2, Integer.toString(fieldID));
		ResultSet rs = stmt.executeQuery();
		if(rs.next()){
			Value value = new Value(rs.getString(3));
			value.setID(rs.getInt(1));
			value.setField_id(rs.getInt(4));
			value.setBatch_id(rs.getInt(2));
			value.setRec_num(rs.getInt(5));
			return value;
		}
		else{
			return null;
		}
	}
	
	/**
	 * Searches the Values table based on a data string and the fieldID attributed with it
	 * @param data the String value to search for
	 * @param fieldID the foreign key to search for
	 * @return Value[] an array of values that match the search term
	 * @throws SQLException 
	 */
	public Value[] getValues(String data, int fieldID) throws SQLException{
		String sql = "SELECT *"
				+ " FROM Value"
				+ " WHERE data = ? AND field_id = ?";
		PreparedStatement stmt = parent.getConnection().prepareStatement(sql);
		stmt.setString(1, data.toUpperCase());
		stmt.setString(2, Integer.toString(fieldID));
		ResultSet rs = stmt.executeQuery();
		int size = 0;
		while(rs.next()){
			size++;
		}
		Value [] values = new Value[size];
		
		rs = stmt.executeQuery();
		size=0;
		while(rs.next()){
			Value value = new Value(rs.getString(3));
			value.setID(rs.getInt(1));
			value.setField_id(rs.getInt(4));
			value.setBatch_id(rs.getInt(2));
			value.setRec_num(rs.getInt(5));
			values[size] = value;
			++size;
		}
		if(values.length>0){
			return values;
		}
		else{
			return null;
		}

	}
	
	/**
	 * Determines whether a value with identical characteristics already exists
	 * @param v the value to be searched for
	 * @return true if the value is found, false if not
	 * @throws SQLException
	 */
	public boolean findValue(Value v) throws SQLException{
		String sql = "SELECT * "
				+ "FROM Value "
				+ "WHERE data = ? AND field_id = ? AND batch_id = ? AND rec_num = ?";
		PreparedStatement stmt = parent.getConnection().prepareStatement(sql);
		stmt.setString(1, v.getData());
		stmt.setString(2, Integer.toString(v.getField_id()));
		stmt.setString(3, Integer.toString(v.getBatch_id()));
		stmt.setString(4, Integer.toString(v.getRec_num()));
		ResultSet rs = stmt.executeQuery();
		return (rs.next());
	}
	
	/**
	 * Adds a value if the value does not already exist
	 * @param v the value to be added
	 * @throws SQLException
	 */
	public void addValue (Value v) throws SQLException{
		if(findValue(v)){
			//System.out.println("Value already exists. Will not duplicate");
		}
		else{
			String sql = "INSERT INTO Value (data, field_id, batch_id, rec_num) values (?, ?, ?, ?);";
			PreparedStatement stmt = parent.getConnection().prepareStatement(sql);
			stmt.setString(1, v.getData().toUpperCase());
			stmt.setString(2, Integer.toString(v.getField_id()));
			stmt.setString(3, Integer.toString(v.getBatch_id()));
			stmt.setString(4, Integer.toString(v.getRec_num()));
			stmt.executeUpdate();
		}
	}
	
	public void setParent(Database db){
		this.parent = db;
	}
	
	public static void main (String args[]) throws ServerException, SQLException{
		Database db = new Database();
		ValueDAO vd = new ValueDAO();
		vd.setParent(db);
		db.setValueDAO(vd);
		db.startTransaction();
		db.loadDefaultTables();
		db.endTransaction(true);
		db.startTransaction();
		Value v = new Value("newvalue");
		vd.addValue(v);
		db.endTransaction(true);
		db.startTransaction();
		vd.addValue(v);
		db.endTransaction(true);
	}
}
