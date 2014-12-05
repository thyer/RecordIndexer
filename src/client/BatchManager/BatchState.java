package client.BatchManager;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import shared.communication.Batch_Result;
import shared.communication.GetProjects_Result;
import shared.communication.Get_Sample_Batch_Params;
import shared.communication.Image_URL;
import shared.communication.ValidateUser_Result;
import shared.modelclasses.Credentials;
import shared.modelclasses.Field;
import client.GUI.DownloadDialog;
import client.GUI.IndexingWindow;
import client.GUI.LoginWindow;
import client.communicator.ClientCommunicator;
import client.communicator.ClientException;

public class BatchState {
	private boolean testing = false;
	private ClientCommunicator cc;
	private LoginWindow login;
	private IndexingWindow index;
	private String host;
	private int port;
	private Credentials creds;
	private BufferedImage image;
	private BufferedImage helpImage;
	private transient List<ActionListener> repaintlisteners;
	private Batch_Result currentbatch;
	private String [][] indexedData;
	private Cell currentCell;
	
	public BatchState(String h, int p){
		currentCell = new Cell(1,1);
		host = h;
		port = p;
		currentbatch = null;
		repaintlisteners = new ArrayList<ActionListener>();
		if(testing){
			indexedData = new String[4][5];
			for(int i=0; i<4; ++i){
				for (int j=0; j<5; ++j){
					if(j==0){
						indexedData[i][j] = "" + i;
					}
					else{
						indexedData[i][j] = "Data" + i + "" + j;
					}
				}
			}
		}
		
		login = new LoginWindow(this);
		login.setLocationRelativeTo(null);
		login.setResizable(false);
		login.setVisible(true);
		index = new IndexingWindow(this);
		index.setSize(1800,1000);
		image = null;
		currentCell = new Cell(1,1);
		cc = new ClientCommunicator(host, port);
	}
	
	public void validateUser(){
		creds = login.getCreds();
		try {
			ValidateUser_Result vur = cc.validateUser(creds);
			if(vur.isOutput()){
				String welcome = "Welcome " + vur.getFirstName() + "\n";
				welcome+="You have indexed " + vur.getNum_records() + " records";
				JOptionPane.showMessageDialog(login, welcome);
				login.setVisible(false);
				index.setVisible(true);
				if(testing){
					String testing = "http://thinkios.com/wp-content/uploads/2012/08/beta-testing-redsn0w.jpg";
					try {
						image = ImageIO.read(new URL(testing));
						System.out.println("image set to : " + testing);
						update();
					} catch (Exception e) {
						e.printStackTrace();
					}
					index.setButtonBarEnabled(true);
					
					update();
				}
			}
			else{
				JOptionPane.showMessageDialog(login, "Failed login - bad credentials");
			}
		} catch (ClientException e) {
			JOptionPane.showMessageDialog(login, "Failed login - bad address");
		}
	}
	
	public void setImage(BufferedImage i){
		image = i;
	}
	
	public BufferedImage getImage(){
		return image;
	}
	
	public URL getHelpURL(){
		try {
			String path = getBatchInfo().getField_array()[currentCell.getColumn()-1].getField_help_file_path();
			URL url = new URL("http://" + host + ":" + port + "/downloadFiles/" + path);
			//System.out.println("Help image url: " + url.toString());
			return url;
		} catch (Exception e) {
			// TODO Auto-generated catch block
		}
		//System.out.println("Returning invalid help Image");
		return null;
	}
		
	
	public Cell getCurrentCell(){
		return currentCell;
	}
	
	public void setCurrentCell(int row, int column){
		currentCell = new Cell(row, column);
	}
	
	public String[] getProjects(){
		String[] output = null;
		try {
			GetProjects_Result gpr = cc.getProjects(creds);
			output = new String[gpr.getProjects().length];
			for(int i=0; i<output.length; ++i){
				output[i] = gpr.getProjects()[i].getName();
			}
			
			
		} catch (ClientException e) {
			JOptionPane.showMessageDialog(login, "GetProjects failed");
		}
		
		return output;
	}

	public void openDownloadDialog(){
		DownloadDialog dd = new DownloadDialog(this);
		dd.setLocationRelativeTo(null);
		dd.setSize(450,250);
		dd.setResizable(false);
		dd.setModal(true);
		dd.setVisible(true);
	}
	
	public void addListener(ActionListener al){
		repaintlisteners.add(al);
	}
	
	public boolean downloadBatch(int prjID){
		Get_Sample_Batch_Params gsbp = new Get_Sample_Batch_Params(creds, prjID);
		try {
			Batch_Result br = cc.downloadBatch(gsbp);
			String url_path = br.getImage_path().getUrl_path();
			URL url = new URL("http://" + host + ":" + port + "/downloadFiles/" + url_path);
			image = ImageIO.read(url);
			currentbatch = br;
			indexedData = new String[br.getNum_records()][br.getNum_fields()+1]; //adding on extra column for rec_num
			for(int i=0; i<indexedData.length; ++i){
				indexedData[i][0] = "" + (i+1);
			}
			index.setButtonBarEnabled(true);
			update();

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public String[][] getIndexedData(){
		return indexedData;
	}
	
	public Batch_Result getBatchInfo(){
		if(testing){
			Batch_Result tester = new Batch_Result(1, 1, new Image_URL("path"), 1, 1, 1, 1);
			tester.setRecord_height(20);
			tester.setField_array(new Field[]{
					new Field("Field1"), new Field("Field2"), new Field("Field3"), new Field("Field4")
			});
			return tester;
		}
		return currentbatch;
	}
	
	public void save(){
		//writes all data for the session to a file based on the username
		//CSV perhaps?
	}
	
	public void restoreSession(){
		//searches for file based on username
		//if found, reads all info in file and writes it into the fields
		//else, do nothing
	}
	
	public void logout(){
		//save user state
		index.setVisible(false);
		login.setVisible(true);
	}
	
	public void exit(){
		index.dispose();
		login.dispose();
	}
	
	public void update(){
		for (ActionListener al : repaintlisteners){
			al.actionPerformed(new ActionEvent (al, port, host));
		}
	}

	public BufferedImage getSampleImage(int i) {
		Get_Sample_Batch_Params gsbp = new Get_Sample_Batch_Params(creds, i);
		URL url = null;
		BufferedImage image = null;
		String url_path = "http://" + host + ":" + port + "/downloadFiles/";
		try {
			url_path = url_path + cc.getSampleImage(gsbp).getUrl_path();
			url = new URL(url_path);
			image = ImageIO.read(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return image; 

		
	}
	
	public class Cell{
		private int row;
		private int column;
		public Cell(int x, int y){
			row = x;
			column = y;
		}
		
		public int getRow(){
			return row;
		}
		
		public int getColumn(){
			return column;
		}
	}
}
