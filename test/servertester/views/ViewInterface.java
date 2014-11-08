package servertester.views;

import servertester.views.ServerOp;

public interface ViewInterface {
	
	void setHost(String value);
	String getHost();
	
	void setPort(String value);
	String getPort();
	
	void setOperation(ServerOp value);
	ServerOp getOperation();
	
	void setParameterNames(String[] value);
	String[] getParameterNames();
	
	void setParameterValues(String[] value);
	String[] getParameterValues();
	
	void setRequest(String value);
	String getRequest();
	
	void setResponse(String value);
	String getResponse();

}

