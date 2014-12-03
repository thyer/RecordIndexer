package SearchGUI;

import shared.modelclasses.Credentials;

public class LoginInfo {
	private Credentials creds;
	private String localhost;
	private int port;
	
	public LoginInfo(Credentials c, String lh, int p){
		creds = c;
		localhost = lh;
		port = p;
	}

	public Credentials getCreds() {
		return creds;
	}

	public void setCreds(Credentials creds) {
		this.creds = creds;
	}

	public String getLocalhost() {
		return localhost;
	}

	public void setLocalhost(String localhost) {
		this.localhost = localhost;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

}
