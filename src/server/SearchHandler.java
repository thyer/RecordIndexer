package server;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.rmi.ServerException;

import server.facade.ServerFacade;
import shared.communication.Field_Result;
import shared.communication.Get_Sample_Batch_Params;
import shared.communication.Search_Params;
import shared.communication.Search_Result;
import shared.modelclasses.Credentials;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class SearchHandler implements HttpHandler {

	private XStream xmlStream = new XStream(new DomDriver());
	
	@Override
	public void handle(HttpExchange exchange) throws IOException {

		try {
			Search_Params params = (Search_Params)xmlStream.fromXML(exchange.getRequestBody());
			Search_Result result = ServerFacade.search(params);
			exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
			xmlStream.toXML(result, exchange.getResponseBody());
			exchange.getResponseBody().close();
		}
		catch (Exception e) {
			exchange.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, -1);
			return;
		}
		
		exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
	}

}