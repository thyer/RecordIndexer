package shared.modelclasses;
/**
 * 
 * @author thyer
 *
 */
public class Credentials {
	private String username;
	private String password;
	/**
	 * 
	 * @param u the username
	 * @param p the password
	 */
	public Credentials (String u, String p){
		username = u;
		password = p;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
