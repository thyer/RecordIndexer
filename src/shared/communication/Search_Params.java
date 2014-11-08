package shared.communication;

import shared.modelclasses.Credentials;

public class Search_Params {
	Credentials creds;
	int [] fieldIDs;
	String[] toSearch;
	
	public Search_Params (Credentials user, int[] ids, String[] searchValues){
		creds = user;
		fieldIDs = ids;
		toSearch = searchValues;
	}

	public Credentials getCreds() {
		return creds;
	}

	public void setCreds(Credentials creds) {
		this.creds = creds;
	}

	public int[] getFieldIDs() {
		return fieldIDs;
	}

	public void setFieldIDs(int[] fieldIDs) {
		this.fieldIDs = fieldIDs;
	}

	public String[] getToSearch() {
		return toSearch;
	}

	public void setToSearch(String[] toSearch) {
		this.toSearch = toSearch;
	}
	
	

}
