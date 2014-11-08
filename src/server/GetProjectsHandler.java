package server;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.rmi.ServerException;

import server.facade.ServerFacade;
import shared.communication.GetProjects_Result;
import shared.modelclasses.Credentials;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class GetProjectsHandler implements HttpHandler {

	private XStream xmlStream = new XStream(new DomDriver());
	
	@Override
	public void handle(HttpExchange exchange) throws IOException {

		Credentials creds = (Credentials)xmlStream.fromXML(exchange.getRequestBody());
		
		try {
			GetProjects_Result result = ServerFacade.getProjects(creds);
			exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
			xmlStream.toXML(result, exchange.getResponseBody());
			exchange.getResponseBody().close();
		}
		catch (Exception e) {
			exchange.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, -1);
			return;
		}
	}

}