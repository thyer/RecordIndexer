package server;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;

import server.facade.ServerFacade;
import shared.communication.Batch_Result;
import shared.communication.Download_Params;
import shared.communication.Download_Result;
import shared.communication.Get_Sample_Batch_Params;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class DownloadFileHandler implements HttpHandler {

	private XStream xmlStream = new XStream(new DomDriver());
	
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		
		String url;
		try 
		{
			url = new File("").getAbsolutePath() + exchange.getRequestURI().getPath();
			Download_Result result = null;
			Download_Params params= new Download_Params(url);
			result = ServerFacade.downloadFile(params);
			OutputStream response = exchange.getResponseBody();
			exchange.sendResponseHeaders(200, 0);
			response.write(result.getFileBytes());
			response.close();
		} 
		catch (Exception e)
		{
			e.printStackTrace();
			return;
		}
	}
}
