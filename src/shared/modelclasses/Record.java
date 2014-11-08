package shared.modelclasses;

/**
 * The data container class for a record
 * @author thyer
 *
 */
public class Record {
	private int ID;
	private int rec_number;
	private int batch_id;
	
	public Record (){
		
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public int getRec_number() {
		return rec_number;
	}

	public void setRec_number(int rec_number) {
		this.rec_number = rec_number;
	}

	public int getBatch_id() {
		return batch_id;
	}

	public void setBatch_id(int batch_id) {
		this.batch_id = batch_id;
	}
	

}
