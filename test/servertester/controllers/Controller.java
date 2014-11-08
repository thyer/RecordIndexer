package servertester.controllers;

import java.util.*;

import client.GUI.GUIViewInterface;
import client.communicator.ClientCommunicator;
import client.communicator.ClientException;
import servertester.views.*;
import shared.communication.*;
import shared.communication.Field_Result.FieldInfo;
import shared.communication.Search_Result.SearchInfo;
import shared.modelclasses.*;

public class Controller implements IController {

	private GUIViewInterface _view;
	
	public Controller() {
		return;
	}
	
	public GUIViewInterface getView() {
		return _view;
	}
	
	public void setView(GUIViewInterface value) {
		_view = value;
	}
	
	// IController methods
	//
	
	@Override
	public void initialize() {
		getView().setHost("localhost");
		getView().setPort("8080");
		operationSelected();
	}

	@Override
	public void operationSelected() {
		ArrayList<String> paramNames = new ArrayList<String>();
		paramNames.add("User");
		paramNames.add("Password");
		
		switch (getView().getOperation()) {
		case VALIDATE_USER:
			break;
		case GET_PROJECTS:
			break;
		case GET_SAMPLE_IMAGE:
			paramNames.add("Project");
			break;
		case DOWNLOAD_BATCH:
			paramNames.add("Project");
			break;
		case GET_FIELDS:
			paramNames.add("Project");
			break;
		case SUBMIT_BATCH:
			paramNames.add("Batch");
			paramNames.add("Record Values");
			break;
		case SEARCH:
			paramNames.add("Fields");
			paramNames.add("Search Values");
			break;
		default:
			assert false;
			break;
		}
		
		getView().setRequest("");
		getView().setResponse("");
		getView().setParameterNames(paramNames.toArray(new String[paramNames.size()]));
	}

	@Override
	public void executeOperation() {
		switch (getView().getOperation()) {
		case VALIDATE_USER:
			validateUser();
			break;
		case GET_PROJECTS:
			getProjects();
			break;
		case GET_SAMPLE_IMAGE:
			getSampleImage();
			break;
		case DOWNLOAD_BATCH:
			downloadBatch();
			break;
		case GET_FIELDS:
			getFields();
			break;
		case SUBMIT_BATCH:
			submitBatch();
			break;
		case SEARCH:
			search();
			break;
		default:
			assert false;
			break;
		}
	}
	
	
	private void validateUser(){
		try{
			String[] args = getView().getParameterValues();
			Credentials creds = new Credentials(args[0], args[1]);
			String port = getView().getPort();
			ClientCommunicator cc = new ClientCommunicator(getView().getHost(), Integer.parseInt(port));
			ValidateUser_Result vur = cc.validateUser(creds);
			if(vur.isOutput()){
				getView().setResponse("TRUE" + "\n" + vur.getFirstName()+"\n" + vur.getLastName() + "\n" + vur.getNum_records());
				
			}
			else{
				getView().setResponse("FALSE\n");
			}
			
		} catch(ClientException e){
			getView().setResponse("FAILED\n");
		}

	}
	
	private void getProjects() {
		try{
			String[] args = getView().getParameterValues();
			Credentials creds = new Credentials(args[0], args[1]);
			String port = getView().getPort();
			ClientCommunicator cc = new ClientCommunicator(getView().getHost(), Integer.parseInt(port));
			GetProjects_Result gpr = cc.getProjects(creds);
			ProjectInfo[] pi = gpr.getProjects();
			String output = "";
			//System.out.println("Size of returned projects: " + pi.length);
			for(ProjectInfo p : pi){
				output +=p.getID() + "\n";
				output +=p.getName() + "\n";

			}
			getView().setResponse(output);
		} catch(ClientException e){
			getView().setResponse("FAILED\n");
		}
	}
	
	private void getSampleImage() {
		try{
			String[] args = getView().getParameterValues();
			Credentials creds = new Credentials(args[0], args[1]);
			String port = getView().getPort();
			int prjID = Integer.parseInt(args[2]);
			Get_Sample_Batch_Params gsbp = new Get_Sample_Batch_Params(creds, prjID);
			ClientCommunicator cc = new ClientCommunicator(getView().getHost(), Integer.parseInt(port));
			Image_URL imageURL = cc.getSampleImage(gsbp);
			String output = "http://" + getView().getHost() + ":" + getView().getPort() + "/downloadFiles/";
			output +=imageURL.getUrl_path();
			getView().setResponse(output);
		} catch(Exception e){
			getView().setResponse("FAILED\n");
			e.printStackTrace();
		}
	}
	
	private void downloadBatch() {
		try{
			getView().setResponse("");
			String[] args = getView().getParameterValues();
			Credentials creds = new Credentials(args[0], args[1]);
			String port = getView().getPort();
			int prjID = Integer.parseInt(args[2]);
			Get_Sample_Batch_Params gsbp = new Get_Sample_Batch_Params(creds, prjID);
			ClientCommunicator cc = new ClientCommunicator(getView().getHost(), Integer.parseInt(port));
			
			Batch_Result br = cc.downloadBatch(gsbp);
				String output = "";
				output += br.getBatchID() + "\n";
				output += br.getProjectID() + "\n";
				String urlbase = "http://" + getView().getHost() + ":" + getView().getPort() + "/downloadFiles/";
				output += urlbase + br.getImage_path().getUrl_path() + "\n";
				output += br.getFirst_y_coord() + "\n";
				output += br.getRecord_height() + "\n";
				output += br.getNum_records() + "\n";
				output += br.getNum_fields() + "\n";
			Field[] fields = br.getField_array();
			for(Field f : fields){
				output += f.getID() + "\n";
				output += f.getNum() + "\n";
				output += f.getTitle() + "\n";
				output += urlbase + f.getField_help_file_path() + "\n";
				output += f.getXcoordinate() + "\n";
				output += f.getWidth() + "\n";
				if(f.getKnown_data_path()!=null && !f.getKnown_data_path().equals("")){
					output += urlbase + f.getKnown_data_path() + "\n";
				}
			}
			getView().setResponse(output);
			
		} catch(Exception e){
			getView().setResponse("FAILED\n");
			e.printStackTrace();
		}
	}
	
	private void getFields() {
		try{
			String[] args = getView().getParameterValues();
			Credentials creds = new Credentials(args[0], args[1]);
			String port = getView().getPort();
			String prjID = "";
			String output = "";

			if(args[2].equals("0")){
				getView().setResponse("FAILED\n");
				return;
			}
			else if(args[2].equals("")){
				prjID="" + 0;
			}
			else{
				prjID = args[2];
			}
			Get_Sample_Batch_Params gsbp = new Get_Sample_Batch_Params(creds, Integer.parseInt(prjID));
			ClientCommunicator cc = new ClientCommunicator(getView().getHost(), Integer.parseInt(port));
			Field_Result fr = cc.getFields(gsbp);
			
			ArrayList<FieldInfo> fields = fr.getFields();
			for(FieldInfo fi : fields){
				output +=fi.getProject_ID() + "\n";
				output +=fi.getField_ID() + "\n";
				output +=fi.getField_Title() + "\n";
			}

			
			getView().setResponse(output);
			
			
		} catch(Exception e){
			getView().setResponse("FAILED\n");
			e.printStackTrace();
		}
	}
	
	private void submitBatch() {
		try{
			//getting parameters
			String[] args = getView().getParameterValues();
			Credentials creds = new Credentials(args[0], args[1]);
			String port = getView().getPort();
			int bID = Integer.parseInt(args[2]);
			String input = args[3];
			
			//parsing through the values, adding to arrayList
			ArrayList<Value> valueList = new ArrayList<Value>();
			List<String> recordStrings = Arrays.asList(input.split(";",-1));
			int i=0;
			for(String s : recordStrings){
				++i;
				List<String> valueStrings = Arrays.asList(s.split(",",-1));
				int j=0;
				for(String v : valueStrings){
					System.out.print(v + " ");
					Value temp = new Value(v);
					temp.setRec_num(i);
					temp.setBatch_id(bID);
					temp.setField_id(j);	//this is going to get handled in the Server Facade
					valueList.add(temp);
					++j;
				}
				//System.out.println();
			}
			//System.out.println();
			//converting arrayList to array
			Value[] values = new Value[valueList.size()];
			for(int index=0; index<valueList.size(); ++index){
				values[index]=valueList.get(index);
			}

			//sending everything across the wire
			Batch_Submit_Params bsp = new Batch_Submit_Params(creds, bID, values);
			ClientCommunicator cc = new ClientCommunicator(getView().getHost(), Integer.parseInt(port));
			boolean b = cc.submitBatch(bsp);
			if(b){
				getView().setResponse("TRUE");
			}
			else{
				getView().setResponse("FAILED");
			}
			
			
			
		} catch(Exception e){
			getView().setResponse("FAILED\n");
			e.printStackTrace();
		}
	}
	
	private void search() {
		
		try{
			//getting parameters
			String[] args = getView().getParameterValues();
			Credentials creds = new Credentials(args[0], args[1]);
			String port = getView().getPort();
			String fields = args[2];
			String search = args[3];
			
			//packaging up fieldIDs
			List<String> fieldList = Arrays.asList(fields.split(",",-1));
			int[] fieldterms = new int[fieldList.size()];
			for(int i=0; i<fieldList.size(); ++i){
				fieldterms[i]=Integer.parseInt(fieldList.get(i));
			}
			
			//packaging up search terms
			List<String> termList = Arrays.asList(search.split(",",-1));
			String[] searchterms = new String[termList.size()];
			for(int i=0; i<termList.size(); ++i){
				searchterms[i]=termList.get(i);
			}


			//sending everything across the wire
			Search_Params sp = new Search_Params(creds, fieldterms, searchterms);
			ClientCommunicator cc = new ClientCommunicator(getView().getHost(), Integer.parseInt(port));
			
			//getting it all back, unwrapping
			Search_Result sr = cc.search(sp);
			ArrayList<SearchInfo> results = sr.getInfo();
			String output = "";
			String urlbase = "http://" + getView().getHost() + ":" + getView().getPort() + "/downloadFiles/";
			for(SearchInfo si : results){
				output +=si.getBatchID() + "\n";
				output +=urlbase + si.getImageURL() + "\n";
				output +=si.getRec_num() + "\n";
				output +=si.getFieldID() + "\n";
			}
			getView().setResponse(output);
			
			
		} catch(Exception e){
			getView().setResponse("FAILED\n");
			e.printStackTrace();
		}
	}

}

