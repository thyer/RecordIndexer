package shared.modelclasses;


/**
 * The container class for a user
 * @author thyer
 *
 */
public class User {
	private static int nextId = 1;
	private int ID;
	private Credentials creds;
	private UserInfo userinfo;
	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public Credentials getCreds() {
		return creds;
	}

	public void setCreds(Credentials creds) {
		this.creds = creds;
	}

	public UserInfo getUserinfo() {
		return userinfo;
	}

	public void setUserinfo(UserInfo userinfo) {
		this.userinfo = userinfo;
	}

	public int getRecordCount() {
		return recordCount;
	}

	public void setRecordCount(int recordCount) {
		this.recordCount = recordCount;
	}

	public int getCurrentBatch() {
		return currentBatch;
	}

	public void setCurrentBatch(int currentBatch) {
		this.currentBatch = currentBatch;
	}

	private int recordCount;
	private int currentBatch;
	
	/**
	 * Initializes a new user with credentials, information, an incremented and unique ID.
	 * Automatically sets the current batch and record count at zero
	 * @param credentials the user's credentials (username and password)
	 * @param userinformation the user's information (first and last name, email)
	 */
	public User (Credentials credentials, UserInfo userinformation){
		ID = nextId;
		nextId++;
		creds = credentials;
		userinfo=userinformation;
		currentBatch = 0;
		recordCount = 0;
	}

	

}
