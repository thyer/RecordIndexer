package shared.communication;

import java.util.ArrayList;

public class Search_Result {
	public class SearchInfo{
		private int batchID;
		private String imageURL;
		private int rec_num;
		private int fieldID;
		
		public SearchInfo(int batch, String URL, int record, int field){
			batchID = batch; 
			imageURL = URL;
			rec_num = record;
			fieldID = field;
		}

		public int getBatchID() {
			return batchID;
		}

		public void setBatchID(int batchID) {
			this.batchID = batchID;
		}

		public String getImageURL() {
			return imageURL;
		}

		public void setImageURL(String imageURL) {
			this.imageURL = imageURL;
		}

		public int getRec_num() {
			return rec_num;
		}

		public void setRec_num(int rec_num) {
			this.rec_num = rec_num;
		}

		public int getFieldID() {
			return fieldID;
		}

		public void setFieldID(int fieldID) {
			this.fieldID = fieldID;
		}
		
		
	}
	
	ArrayList<SearchInfo> searchresults = new ArrayList<SearchInfo>();
	public Search_Result (){
		
	}
	public void addResults(int batch, String URL, int record, int field){
		searchresults.add(new SearchInfo(batch,URL,record,field));
	}
	public ArrayList<SearchInfo> getInfo(){
		return searchresults;
	}

}
