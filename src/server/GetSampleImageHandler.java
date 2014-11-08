package server;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.rmi.ServerException;

import server.facade.ServerFacade;
import shared.communication.Get_Sample_Batch_Params;
import shared.communication.Image_URL;
import shared.communication.ValidateUser_Result;
import shared.modelclasses.Credentials;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class GetSampleImageHandler implements HttpHandler {

	private XStream xmlStream = new XStream(new DomDriver());
	
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		
		try {
			Get_Sample_Batch_Params params = (Get_Sample_Batch_Params)xmlStream.fromXML(exchange.getRequestBody());
			String result = ServerFacade.getSampleImage(params.getCreds(), params.getProjectID());
			Image_URL output = new Image_URL(result);
			exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
			xmlStream.toXML(output, exchange.getResponseBody());
			exchange.getResponseBody().close();
		}
		catch (Exception e) {
			exchange.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, -1);
			return;
		}
		
		exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
	}

}