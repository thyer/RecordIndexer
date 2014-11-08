package shared.communication;

public class Image_URL {
	/**
	 * The data container class holding a url to an image
	 */
	String url_path;
	
	public Image_URL(String url){
		url_path = url;
	}

	public String getUrl_path() {
		return url_path;
	}

	public void setUrl_path(String url_path) {
		this.url_path = url_path;
	}

	
}
