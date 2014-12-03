package client.GUI;

import java.awt.EventQueue;

import server.dataimporter.DataImporter;
import client.BatchManager.BatchState;

public class RecordIndexerGUI {
	
	public static void main(String[] args) {
		final String host;
		final int port;
		if(args.length==2){
			host = args[0];
			port = Integer.parseInt(args[1]);
		}
		else{
			host = "localhost";
			port = 8081;
		}
		if(host.equals("localhost") & port == 8081){
			DataImporter di = new DataImporter();
		}
		EventQueue.invokeLater(
				new Runnable() {
					public void run() {
						BatchState bs = new BatchState(host,port);
					}
				}
		);

	}

}
