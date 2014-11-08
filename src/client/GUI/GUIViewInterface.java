package client.GUI;

import servertester.views.ServerOp;

public interface GUIViewInterface {
	
	void setHost(String value);
	String getHost();
	
	void setPort(String value);
	String getPort();
	
	void setOperation(Operations value);
	Operations getOperation();
	
	void setParameterNames(String[] value);
	String[] getParameterNames();
	
	void setParameterValues(String[] value);
	String[] getParameterValues();
	
	void setRequest(String value);
	String getRequest();
	
	void setResponse(String value);
	String getResponse();

}

