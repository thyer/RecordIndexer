package shared.communication;

public class ValidateUser_Result {
	
	private boolean output;
	private String firstName;
	private String lastName;
	private int num_records;
	
	/**
	 * Constructor for the result container class
	 * @param b true/false whether the user was validated
	 * @param fn the user's first name
	 * @param ln the user's last name
	 * @param num the number of records the user has indexed
	 */
	
	public ValidateUser_Result(boolean b, String fn, String ln, int num){
		output = b;
		firstName = fn;
		lastName = ln;
		num_records = num;		
	}

	public boolean isOutput() {
		return output;
	}

	public void setOutput(boolean output) {
		this.output = output;
	}

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

	public int getNum_records() {
		return num_records;
	}

	public void setNum_records(int num_records) {
		this.num_records = num_records;
	}
	
	

}
