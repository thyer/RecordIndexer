package shared.communication;

import java.util.ArrayList;

public class Field_Result {
	private ArrayList<FieldInfo> fieldinformation = new ArrayList<FieldInfo>();
	
	/**
	 * Adds a new field's information to the arraylist
	 * @param prj the project ID
	 * @param id the field ID
	 * @param title the field title
	 */
	public void addField(int prj, int id, String title){
		FieldInfo fi = new FieldInfo(prj, id, title);
		fieldinformation.add(fi);
	}
	
	/**
	 * Returns an arraylist of field information. Each element will need to be further drilled into to get to the primitive data
	 * @return an ArrayList containing FieldInfo objects
	 */
	public ArrayList<FieldInfo> getFields(){
		return fieldinformation;
	}
	
	public class FieldInfo{
		private int field_ID;
		private String field_Title;
		private int project_ID;
		public FieldInfo(int prjID, int ID, String title){
			project_ID = prjID;
			field_ID = ID;
			field_Title = title;
		}
		public int getField_ID() {
			return field_ID;
		}
		public void setField_ID(int field_ID) {
			this.field_ID = field_ID;
		}
		public String getField_Title() {
			return field_Title;
		}
		public void setField_Title(String field_Title) {
			this.field_Title = field_Title;
		}
		public int getProject_ID() {
			return project_ID;
		}
		public void setProject_ID(int project_ID) {
			this.project_ID = project_ID;
		}
	}
}
