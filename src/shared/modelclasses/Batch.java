package shared.modelclasses;
/**
 * The data container class for a batch
 * @author thyer
 *
 */
public class Batch {
	private int ID = 0;
	private String image_file_path;
	private int project_id;
	private int state;
	
	public Batch(){
		state = 0;
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public String getImage_file_path() {
		return image_file_path;
	}

	public void setImage_file_path(String image_file_path) {
		this.image_file_path = image_file_path;
	}

	public int getProject_id() {
		return project_id;
	}

	public void setProject_id(int project_id) {
		this.project_id = project_id;
	}
	/**
	 * 
	 * @return returns the current state of the object 
	 * where 0 is batch needing checkout, 1 is checked-out batch, and 2 is completed batch
	 */
	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

}
