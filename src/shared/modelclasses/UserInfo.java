package shared.modelclasses;

/**
 * 
 * @author thyer
 * @param firstname the user's first name
 * @param lastName the user's last name
 * @param email the user's email address
 *
 */
public class UserInfo {
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	private String firstName;
	private String lastName;
	private String email;
	/**
	 * 
	 * @param fn the user's first name
	 * @param ln the user's last name
	 * @param e the user's email
	 */
	public UserInfo (String fn, String ln, String e){
		firstName = fn;
		lastName = ln;
		email = e;
	}

}
