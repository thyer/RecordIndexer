package shared.modelclasses;
/**
 * The data container class for fields
 * @author thyer
 *
 */
public class Field {
	private int ID;
	private int projectID;
	private String field_help_file_path;
	private String known_data_path;
	private int xcoordinate;
	private int width;
	private int num;
	private String title;
	
	public Field(String t){
		title = t;
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public int getProjectID() {
		return projectID;
	}

	public void setProjectID(int projectID) {
		this.projectID = projectID;
	}

	public String getField_help_file_path() {
		return field_help_file_path;
	}

	public void setField_help_file_path(String field_help_file_path) {
		this.field_help_file_path = field_help_file_path;
	}

	public String getKnown_data_path() {
		return known_data_path;
	}

	public void setKnown_data_path(String known_data_path) {
		this.known_data_path = known_data_path;
	}

	public int getXcoordinate() {
		return xcoordinate;
	}

	public void setXcoordinate(int xcoordinate) {
		this.xcoordinate = xcoordinate;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}
