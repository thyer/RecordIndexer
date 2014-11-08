package shared.communication;

import shared.modelclasses.Field;

public class Batch_Result {
	private int batchID;
	private int projectID;
	private Image_URL image_path;
	private int first_y_coord;
	private int record_height;
	private int num_records;
	private int num_fields;
	private Field[] field_array;
	
	public Batch_Result(int batch_ID, int prjID, Image_URL path, int y, int r_height, int n_records, int n_fields){
		batchID = batch_ID;
		projectID = prjID;
		image_path = path;
		first_y_coord = y; 
		record_height = r_height;
		num_records = n_records;
		num_fields = n_fields;
	}

	
	public int getBatchID() {
		return batchID;
	}


	public void setBatchID(int batchID) {
		this.batchID = batchID;
	}


	public Field[] getField_array() {
		return field_array;
	}

	public void setField_array(Field[] field_array) {
		this.field_array = field_array;
	}

	public int getProjectID() {
		return projectID;
	}

	public void setProjectID(int projectID) {
		this.projectID = projectID;
	}

	public Image_URL getImage_path() {
		return image_path;
	}

	public void setImage_path(Image_URL image_path) {
		this.image_path = image_path;
	}

	public int getFirst_y_coord() {
		return first_y_coord;
	}

	public void setFirst_y_coord(int first_y_coord) {
		this.first_y_coord = first_y_coord;
	}

	public int getRecord_height() {
		return record_height;
	}

	public void setRecord_height(int record_height) {
		this.record_height = record_height;
	}

	public int getNum_records() {
		return num_records;
	}

	public void setNum_records(int num_records) {
		this.num_records = num_records;
	}

	public int getNum_fields() {
		return num_fields;
	}

	public void setNum_fields(int num_fields) {
		this.num_fields = num_fields;
	}
	
	

}
