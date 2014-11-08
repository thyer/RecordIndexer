package shared.communication;

import shared.modelclasses.Credentials;
import shared.modelclasses.Value;

public class Batch_Submit_Params {
	private Credentials creds;
	private int batchID;
	private Value[] field_values;
	
	public Batch_Submit_Params (Credentials user_creds, int batch, Value[] values){
		creds = user_creds;
		batchID = batch;
		field_values = values;
	}

	public Credentials getCreds() {
		return creds;
	}

	public void setCreds(Credentials creds) {
		this.creds = creds;
	}

	public int getBatchID() {
		return batchID;
	}

	public void setBatchID(int batchID) {
		this.batchID = batchID;
	}

	public Value[] getField_values() {
		return field_values;
	}

	public void setField_values(Value[] field_values) {
		this.field_values = field_values;
	}
	
	
}
