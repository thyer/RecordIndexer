package client.BatchManager;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import shared.communication.Batch_Result;
import shared.communication.Batch_Submit_Params;
import shared.communication.GetProjects_Result;
import shared.communication.Get_Sample_Batch_Params;
import shared.communication.ValidateUser_Result;
import shared.modelclasses.Credentials;
import shared.modelclasses.Value;
import client.GUI.DownloadDialog;
import client.GUI.IndexingWindow;
import client.GUI.LoginWindow;
import client.SpellCorrector.SpellCorrector.NoSimilarWordFoundException;
import client.SpellCorrector.SpellFacade;
import client.communicator.ClientCommunicator;
import client.communicator.ClientException;

public class BatchState {
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
	private SpellFacade spellfacade;
	
	/**
	 * Constructor for the batch state, takes in a host and port, then sets up managerial information for the index and login window
	 * @param h the host name
	 * @param p the port number
	 */
	public BatchState(String h, int p){
		currentCell = new Cell(1,1);
		host = h;
		port = p;
		currentbatch = null;
		repaintlisteners = new ArrayList<ActionListener>();
		
		//begins management process for login and index windows
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

	/**
	 * Validates the user's credentials. If the credentials are valid, closes the login window and opens the index window
	 * If credentials are invalid or the connection fails, shows dialog box notifying the user
	 */
	public void validateUser(){
		creds = login.getCreds();
		
		//Attempts to validate user based on credentials
		try {
			ValidateUser_Result vur = cc.validateUser(creds);
			if(vur.isOutput()){
				String welcome = "Welcome " + vur.getFirstName() + "\n";
				welcome+="You have indexed " + vur.getNum_records() + " records";
				JOptionPane.showMessageDialog(login, welcome);
				login.setVisible(false);
				index.setVisible(true);
			}
			else{
				JOptionPane.showMessageDialog(login, "Failed login - bad credentials"); //Will reject bad credentials
			}
		} catch (ClientException e) {
			JOptionPane.showMessageDialog(login, "Failed login - bad connection");		//Will reject if connection failed
		}
		
		//Attempts to restore a previous session
		try {
			restoreSession();
		} catch (IOException e) {
			System.out.println("No save file found...no problem!");
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
			return url;
		} catch (Exception e) {
			//Do nothing, this means it's not available yet
		}
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
	
	/**
	 * Checks a cell to verify whether the word is recognized by SpellFacade
	 * @param column tells us which SpellChecker in SpellFacade to use
	 * @param text the text to be checked
	 * @return true if SpellFacade recognizes the word, else false
	 */
	public boolean checkCell(int column, String text){
		if(text.equals("")){
			return true;
		}
		else{
			try {
				return spellfacade.checkWord(column-1, text);
			} catch (NoSimilarWordFoundException e) {
				return false;
			}
		}
	}
	
	/**
	 * Gives all recommendations which are edit2 distance away from the text
	 * @param column tells SpellFacade which field we're checking
	 * @param text the text to give recommendations for
	 * @return
	 */
	public String[] getSpellingSuggestions(int column, String text){
		String[] output;
		try {
			output = spellfacade.getSuggestions(column-1, text);
		} catch (NoSimilarWordFoundException e) {		//handles the possibility that SpellChecker thinks you're an idiot
			output = new String[1];
			output[0] = "";								//returns an empty string.
		}
		return output;
	}

	/**
	 * Calls the client communicator's download methods. This only happens when an actual state change happens to the batch
	 * If the user restores an earlier session, this method is not called.
	 * @param prjID the Project ID for which the batch will be downloaded
	 * @return true if the batch successfully downloads, false otherwise
	 */
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
				for(int j=1; j<indexedData[i].length; ++j){
					indexedData[i][j] = "";
				}
			}
			index.setButtonBarEnabled(true);
			spellfacade = new SpellFacade(this);
			
			update();
			

		} catch (Exception e) {
			e.printStackTrace();		//this occurs if a user tries to download another batch
			return false;
		}
		return true;
	}
	
	public String[][] getIndexedData(){
		return indexedData;
	}
	
	public void setIndexedData(int index1, int index2, String text){
		indexedData[index1][index2]=text;
	}
	
	public Batch_Result getBatchInfo(){
		return currentbatch;
	}
	
	/**
	 * Saves the index/image information if applicable, plus any window size/location data
	 * The save file is titled "savefile_<username>.xml
	 * @throws IOException thrown if the file cannot be saved
	 */
	public void save() throws IOException{ //throws an IO exception if the file can't be saved
		BatchSaveObject bso = new BatchSaveObject();
		
		//Saves index/image information if relevant
		if(currentbatch==null){
			System.out.println("No indexing data to be saved");
		}
		else{
			bso.setIndexedData(indexedData);
			bso.setInverted(!index.invertImage());
			index.invertImage();
			bso.setBatchinfo(currentbatch);
			bso.setHighlights(!index.toggleHighlights());
			index.toggleHighlights();
			bso.setZoom(index.getZoom());
			bso.setxTranslate(index.getXTranslate());
			bso.setyTranslate(index.getYTranslate());
		}
		
		//Saves session window data
		bso.setLeftRightLocation(index.getLeftRightSliderPosition());
		bso.setTopBottomLocation(index.getTopBottomSliderPosition());
		bso.setWindowPosition(index.getLocation());
		System.out.println("Window position being set to: " + bso.getWindowPosition());
		bso.setWindowSize(index.getSize());
		
		bso.setCreds(creds);
		
		//Writing information to file
		XStream xStream = new XStream(new DomDriver());
		OutputStream outFile = new BufferedOutputStream(new FileOutputStream("savefile_" + creds.getUsername() + ".xml")); //Makes a xml file with the person's username
		xStream.toXML(bso, outFile); // This writes your batchstate to the outputFile;
		outFile.close(); //close the writer
	}
	
	public void restoreSession() throws IOException{ //will throw an exception if the file isn't found, needs to be handled when called
		
		//Pulling info from file
		System.out.println("Restoring previous session");
		XStream xStream = new XStream(new DomDriver()); 
		InputStream modFile	= new BufferedInputStream(new FileInputStream("savefile_" + creds.getUsername() + ".xml")); //find the file with the given username
		BatchSaveObject bso = (BatchSaveObject) xStream.fromXML(modFile); //Read that batchstate back in to the exact form it was before
		
		
		//Restores batch information, if relevant
		if(bso.getBatchinfo()!=null){
			currentbatch=bso.getBatchinfo();
			indexedData=bso.getIndexedData();
			index.setButtonBarEnabled(true);
			String url_path = currentbatch.getImage_path().getUrl_path();
			URL url = new URL("http://" + host + ":" + port + "/downloadFiles/" + url_path);
			image = ImageIO.read(url);
			spellfacade = new SpellFacade(this);
			index.setXTranslate(bso.getxTranslate());
			index.setYTranslate(bso.getyTranslate());
			update();
			
			while(index.invertImage()!=bso.isInverted()){
				//this will run a maximum of twice
			}
			while(index.toggleHighlights()!=bso.isHighlights()){
				//this will also run a maximum of twice
			}
			index.setZoom(bso.getZoom());
		}
		else{
			System.out.println("No batch info to be restored");
		}
		
		//Restores session window information
		index.setSize(bso.getWindowSize());
		index.setLocation(bso.getWindowPosition());
		index.setTopBottomSliderPosition(bso.getTopBottomLocation());
		index.setLeftRightSliderPosition(bso.getLeftRightLocation());
		System.out.println("Restored window positionb being set to: " + index.getLocation());
		JOptionPane.showMessageDialog(index, "Your previous session has been restored");
		
		modFile.close();
	}
	
	/**
	 * Calls the submit method on the client communicator based on the indexing data present in the window
	 * Once submission is complete, begins a new session for the user
	 */
	public void submit(){
		//general data
		int batchID = this.getBatchInfo().getBatchID();
		ArrayList<Value> valueList = new ArrayList<Value>();
		Batch_Submit_Params bsp;
		
		//input data
		for(int i=0; i<indexedData.length; ++i){
			for(int j=1; j<indexedData[i].length; ++j){
				Value temp = new Value(indexedData[i][j]);
				if(temp.getData()==null){
					temp.setData("");
				}
				temp.setBatch_id(batchID);
				temp.setRec_num(i+1);
				temp.setField_id(j-1);
				valueList.add(temp);
			}
		}
		Value[] values = new Value[valueList.size()];
		for(int i=0; i<valueList.size(); ++i){
			values[i]=valueList.get(i);
		}
		
		//preparing for transmission
		bsp = new Batch_Submit_Params(creds, batchID, values);
		try {
			if(!cc.submitBatch(bsp)){
				System.out.println("Error in submitting batch");
			}
			else{
				//Do nothing, this is successful
			}
		} catch (ClientException e) {
			e.printStackTrace();
			System.out.println("Client error thrown while trying to submit batch");
		}
		
		startNewSession();
	}
	
	/**
	 * Disposes the existing index window and creates a new one, then calls save to reflect the new session
	 */
	public void startNewSession(){
		index.dispose();
		indexedData=null;
		currentbatch=null;
		index = new IndexingWindow(this);
		index.setSize(1800,1000);
		image = null;
		currentCell = new Cell(1,1);
		cc = new ClientCommunicator(host, port);
		JOptionPane.showMessageDialog(login, "Thank you for your submission");
		login.setVisible(false);
		index.setVisible(true);
		try {
			save();	//this ensures that if a user submits, then exits, that the submitted session won't be restored
		} catch (IOException e) {
			System.out.println("Saving failure occurred in method BatchState::startNewSession.");
		}
	}
	
	/**
	 * Saves, then ends the current session
	 * @throws IOException if save doesn't happen right
	 */
	public void logout() throws IOException{
		save();
		index.dispose();
		indexedData=null;
		currentbatch=null;
		index = new IndexingWindow(this);
		index.setSize(1800,1000);
		image = null;
		currentCell = new Cell(1,1);
		index.setVisible(false);
		login.setVisible(true);
	}
	
	public void exit(){
		index.dispose();
		login.dispose();
	}
	
	public void update(){
		
		for(int i=0; i<repaintlisteners.size(); ++i){
			repaintlisteners.get(i).actionPerformed(new ActionEvent (repaintlisteners.get(i),port,host));
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
	
	public int getPort(){
		return port;
	}
	
	public String getHost(){
		return host;
	}
	
	/**
	 * Public interior class designed to help manage current cell location
	 * @author thyer
	 *
	 */
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
