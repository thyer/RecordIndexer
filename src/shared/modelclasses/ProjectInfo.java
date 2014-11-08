package shared.modelclasses;

public class ProjectInfo {
	private int ID;
	private String name;
	private int records_per_image = -1;
	private int first_y_coord = -1;
	private int record_height = -1;
	
	public ProjectInfo(String n){
		name = n;
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getRecords_per_image() {
		return records_per_image;
	}

	public void setRecords_per_image(int records_per_image) {
		this.records_per_image = records_per_image;
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
	
	
}
