package shared.modelclasses;
/**
 * The data container class for the value of a particular field
 * @author thyer
 *
 */
public class Value {
	private int ID;
	private int batch_id;
	private String data;
	private int field_id;
	private int rec_num;
	

	/**
	 * Initializes a Value object with data already inside
	 * @param input the data for the field
	 */
	public Value(String input){
		data = input;
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public int getBatch_id() {
		return batch_id;
	}

	public void setBatch_id(int record_id) {
		this.batch_id = record_id;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public int getField_id() {
		return field_id;
	}

	public void setField_id(int field_id) {
		this.field_id = field_id;
	}
	
	public int getRec_num() {
		return rec_num;
	}

	public void setRec_num(int rec_num) {
		this.rec_num = rec_num;
	}
	
	
}
