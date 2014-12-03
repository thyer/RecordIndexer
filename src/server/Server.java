package server;

import java.io.*;
import java.net.*;
import java.rmi.ServerException;
import java.util.logging.*;

import com.sun.net.httpserver.*;

import server.facade.*;

public class Server {
	
	private static int SERVER_PORT_NUMBER;
	private static final int MAX_WAITING_CONNECTIONS = 10;
	
	private static Logger logger;
	
	static {
		try {
			initLog();
		}
		catch (IOException e) {
			System.out.println("Could not initialize log: " + e.getMessage());
		}
	}
	
	private static void initLog() throws IOException {
		
		Level logLevel = Level.FINE;
		
		logger = Logger.getLogger("damocles"); 
		logger.setLevel(logLevel);
		logger.setUseParentHandlers(false);
		
		Handler consoleHandler = new ConsoleHandler();
		consoleHandler.setLevel(logLevel);
		consoleHandler.setFormatter(new SimpleFormatter());
		logger.addHandler(consoleHandler);

		FileHandler fileHandler = new FileHandler("log.txt", false);
		fileHandler.setLevel(logLevel);
		fileHandler.setFormatter(new SimpleFormatter());
		logger.addHandler(fileHandler);
	}

	
	private HttpServer server;
	
	private Server() {
		return;
	}
	
	private void run() {
		
		logger.info("Initializing Model");
		
		try {
			ServerFacade.initialize();		
		}
		catch (ServerException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
			return;
		}
		
		logger.info("Initializing HTTP Server");
		
		try {
			server = HttpServer.create(new InetSocketAddress(SERVER_PORT_NUMBER),
											MAX_WAITING_CONNECTIONS);
		} 
		catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage(), e);			
			return;
		}

		server.setExecutor(null); // use the default executor
		
		server.createContext("/validateUser", validateUserHandler);
		server.createContext("/getProjects", getProjectsHandler);
		server.createContext("/getSampleImage", getSampleImageHandler);
		server.createContext("/downloadBatch", downloadBatchHandler);
		server.createContext("/submitBatch", submitBatchHandler);
		server.createContext("/getFields", getFieldsHandler);
		server.createContext("/search", searchHandler);
		server.createContext("/downloadFile", downloadFileHandler);
		
		logger.info("Starting HTTP Server");

		server.start();
	}

	private ValidateUserHandler validateUserHandler = new ValidateUserHandler();
	private GetProjectsHandler getProjectsHandler = new GetProjectsHandler();
	private GetSampleImageHandler getSampleImageHandler = new GetSampleImageHandler();
	private DownloadBatchHandler downloadBatchHandler = new DownloadBatchHandler();
	private SubmitBatchHandler submitBatchHandler = new SubmitBatchHandler();
	private GetFieldsHandler getFieldsHandler = new GetFieldsHandler();
	private SearchHandler searchHandler = new SearchHandler();
	private DownloadFileHandler downloadFileHandler = new DownloadFileHandler();
	
	public static void main(String[] args) {
		if(args.length>0){
			SERVER_PORT_NUMBER = Integer.parseInt(args[0]);
		}
		else{
			SERVER_PORT_NUMBER = 8081;
		}
		new Server().run();
		logger.info("Server running on port: " + SERVER_PORT_NUMBER);
	}

}
