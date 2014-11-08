package shared.communication;

import shared.modelclasses.ProjectInfo;

/**
 * Contains the result for getProjects in the Client Communicator
 * @author thyer
 *
 */
public class GetProjects_Result {
	ProjectInfo [] projects;
	public GetProjects_Result(){
		projects = null;
	}
	
	public ProjectInfo[] getProjects() {
		return projects;
	}
	public void setProjects(ProjectInfo[] projects) {
		this.projects = projects;
	}
	

}
