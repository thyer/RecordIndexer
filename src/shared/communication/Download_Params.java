package shared.communication;

public class Download_Params {
	private String url;
	
	public Download_Params(String url) 
	{
		this.url = url;
	}


	public String getUrl() {
		return url;
	}


	public void setUrl(String url) {
		this.url = url;
	}
}
