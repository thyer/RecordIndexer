package shared.communication;
import shared.modelclasses.*;

/**
 * The data container class for Get_Sample_Image parameters
 * @author thyer
 *
 */
public class Get_Sample_Batch_Params {
	Credentials creds;
	int projectID;
	
	public Get_Sample_Batch_Params(Credentials credentials, int prjID){
		creds = credentials;
		projectID = prjID;
	}

	public Credentials getCreds() {
		return creds;
	}

	public void setCreds(Credentials creds) {
		this.creds = creds;
	}

	public int getProjectID() {
		return projectID;
	}

	public void setProjectID(int projectID) {
		this.projectID = projectID;
	}
	
	
	

}
