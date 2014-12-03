package server.facade;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.ServerException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;

import server.databaseaccess.Database;
import server.dataimporter.DataImporter;
import shared.communication.Batch_Result;
import shared.communication.Batch_Submit_Params;
import shared.communication.Download_Params;
import shared.communication.Download_Result;
import shared.communication.Field_Result;
import shared.communication.GetProjects_Result;
import shared.communication.Image_URL;
import shared.communication.Search_Params;
import shared.communication.Search_Result;
import shared.communication.Search_Result.SearchInfo;
import shared.communication.ValidateUser_Result;
import shared.modelclasses.*;

public class ServerFacade {
	static Database db;
	static Logger logger = Logger.getLogger("damocles"); 
	
	public static void initialize() throws ServerException{
		logger.info("Initializing server facade");
		db = new Database();

	}
	
	public static void firebomb() throws ServerException, SQLException{
		logger.severe("Warning, throwing firebomb in server facade!");
		db.startTransaction();
		db.loadDefaultTables();
		db.endTransaction(true);
	}

	
	public static ValidateUser_Result validateUser(Credentials creds) throws ServerException{
		try{
			db.startTransaction();
			boolean b = db.getUserDAO().validateUser(creds);
			db.endTransaction(true);
			ValidateUser_Result v = new ValidateUser_Result(b, null, null, 0);
			if(!b){
				v.setOutput(b);
				return v;
			}
			db.startTransaction();
			v.setFirstName(db.getUserDAO().getUser(creds).getUserinfo().getFirstName());
			v.setLastName(db.getUserDAO().getUser(creds).getUserinfo().getLastName());
			v.setNum_records(db.getUserDAO().getUser(creds).getRecordCount());
			db.endTransaction(true);
			return v;
		}catch(Exception e){
			db.endTransaction(false);
			throw new ServerException(e.getMessage(), e);
		}
	}
	
	public static GetProjects_Result getProjects(Credentials creds) throws ServerException{
		try{
			db.startTransaction();
			if(!db.getUserDAO().validateUser(creds)){
				throw new ServerException("Invalid credentials");
			}
			
			//starting the uber annoying process of wrapping the data
			Project[] projects = db.getProjectDAO().getAllProjects();
			db.endTransaction(true);
			
			//pulling out project info from each project
			ProjectInfo[] output = new ProjectInfo[projects.length];
			int i=0;
			for(Project p : projects){
				output[i]=p.getProjectinfo();
				++i;
			}
			
			//and now wrapping project info into the results object
			GetProjects_Result em = new GetProjects_Result();
			em.setProjects(output);
			return em;
		}catch(Exception e){
			db.endTransaction(false);
			throw new ServerException(e.getMessage(), e);
		}
	}
	
	public static String getSampleImage(Credentials creds, int project_id) throws ServerException{
		try{
			logger.info("Entering getSampleImage method in server facade");
			db.startTransaction();
			if(!db.getUserDAO().validateUser(creds)){
				throw new ServerException("Invalid credentials");
			}
			if(db.getProjectDAO().getProject(project_id)==null){
				throw new ServerException("No such project");
			}
			ProjectInfo pi = new ProjectInfo("");
			pi.setID(project_id);
			Project prj = new Project();
			prj.setProjectinfo(pi);

			
			String output = db.getBatchDAO().getBatch(prj).getImage_file_path();
			db.endTransaction(true);
			return output;
		}catch(Exception e){
			db.endTransaction(false);
			throw new ServerException(e.getMessage(), e);
		}
	}
	
	public static Batch_Result downloadBatch(Credentials creds, int project_id) throws ServerException{
		try{
			logger.info("Entering download batch method with credentials\n\tUsername: " + creds.getUsername() + "\n\tProject: " + project_id);
			db.startTransaction();
			if(!db.getUserDAO().validateUser(creds)){
				throw new ServerException("Invalid credentials");
			}
			
			if(db.getUserDAO().getUser(creds).getCurrentBatch()!=0){
				throw new ServerException("Already got a batch checked out");
			}
			
			//getting stuff packed together to send back
			ProjectInfo pi = new ProjectInfo("");
			pi.setID(project_id);
			Project prj = new Project();
			prj.setProjectinfo(pi);
			db.endTransaction(true);
			db.startTransaction();
			
			//getting batch, updating database to reflect that it's checked out
			Batch batch = db.getBatchDAO().getBatch(prj);
			batch.setState(1);
			db.getBatchDAO().updateBatch(batch);
			db.endTransaction(true);
			db.startTransaction();
			
			//finalizing packaged info
			prj = db.getProjectDAO().getProject(project_id);
			pi = prj.getProjectinfo();
			Field[] fields = db.getFieldDAO().getFields(pi.getID());
			Image_URL image = new Image_URL(batch.getImage_file_path());
			Batch_Result output = new Batch_Result(batch.getID(), project_id, image, pi.getFirst_y_coord(), 
					pi.getRecord_height(), pi.getRecords_per_image(), fields.length);
			output.setField_array(fields);
			
			//updating user's checkout info:
			User user = db.getUserDAO().getUser(creds);
			user.setCurrentBatch(batch.getID());
			db.getUserDAO().updateUser(user);
			
			//ending transaction successfully, returning output
			db.endTransaction(true);
			return output;
		}catch(Exception e){
			e.printStackTrace();
			db.endTransaction(false);
			throw new ServerException(e.getMessage(), e);
		}
	}
	
	public static boolean submitBatch(Batch_Submit_Params bsp) throws ServerException{
		try{
			logger.info("Entering download batch method with credentials\n\tUsername: " + bsp.getCreds().getUsername() + "\n\tBatch: " + bsp.getBatchID());
			db.startTransaction();
			if(!db.getUserDAO().validateUser(bsp.getCreds())){
				throw new ServerException("Invalid credentials");
			}
			
			if(bsp.getBatchID()!=db.getUserDAO().getUser(bsp.getCreds()).getCurrentBatch()){
				System.out.println("Mismatch!");
				System.out.println("Batch passed in: " + bsp.getBatchID());
				System.out.println("User's current batch: " + db.getUserDAO().getUser(bsp.getCreds()).getCurrentBatch());
				throw new ServerException("User doesn't own that batch!");
			}
			
			
			//updating batch information
			int batch_id = bsp.getBatchID();
			Batch to_update = db.getBatchDAO().getBatch(batch_id);
			to_update.setState(2);
			db.getBatchDAO().updateBatch(to_update);
			
			//updating values
			System.out.println("Adding values in server facade line 181");
			Field firstField = db.getFieldDAO().getFields(db.getBatchDAO().getBatch(batch_id).getProject_id())[0];
			int minimumfieldID = firstField.getID();  //this deals with how I handled the field IDs in the controller
			if(bsp.getField_values().length==0){
				System.out.println("Warning: batch submission updates no values");
			}
			for(Value v : bsp.getField_values()){
				v.setField_id(v.getField_id() + minimumfieldID); //adds the minimum field value to the cardinal value of the field on input
				System.out.println("In server facade, I'm adding the value with the data: " + v.getData());
				db.getValueDAO().addValue(v);
			}
			
			//updating user info
			User user = db.getUserDAO().getUser(bsp.getCreds());
			int add = 0;
			Batch batch = db.getBatchDAO().getBatch(bsp.getBatchID());
			Project project = db.getProjectDAO().getProject(batch.getProject_id());
			add = project.getProjectinfo().getRecords_per_image();
			user.setRecordCount(user.getRecordCount() + add);
			user.setCurrentBatch(0);
			db.getUserDAO().updateUser(user);
			
			//ending transaction, returning true
			db.endTransaction(true);
			return true;
		}catch(Exception e){
			logger.severe("Throwing error in ServerFacade.submitBatch");
			db.endTransaction(false);
			return false;
		}
	}
	
	public static Field_Result getFields(Credentials creds, String project_id) throws ServerException{
		try{
			Field_Result output = new Field_Result();
			db.startTransaction();
			if(!db.getUserDAO().validateUser(creds)){
				throw new ServerException("Invalid credentials");
			}
			else if(project_id.equals("")){
				Project[] all_projects = db.getProjectDAO().getAllProjects();
				for(Project p : all_projects){
					Field[] fields = db.getFieldDAO().getFields(p.getProjectinfo().getID());
					for(Field f : fields){
						output.addField(f.getProjectID(), f.getID(), f.getTitle());
					}
				}
			}
			else{
				try{
					int prjID = Integer.parseInt(project_id);
					Field[] fields = db.getFieldDAO().getFields(prjID);
					for(Field f : fields){
						output.addField(f.getProjectID(), f.getID(), f.getTitle());
					}
				} catch(NumberFormatException e){
					db.endTransaction(false);
					throw new ServerException(e.getMessage(), e);
				}
			}
			
			db.endTransaction(true);
			return output;
		}catch(Exception e){
			db.endTransaction(false);
			throw new ServerException(e.getMessage(), e);
		}
	}
	
	public static Search_Result search(Search_Params sp) throws ServerException{
		Search_Result output = new Search_Result();
		try{
			db.startTransaction();
			if(!db.getUserDAO().validateUser(sp.getCreds())){
				throw new ServerException("Invalid credentials");
			}
			
			ArrayList<Value> values = new ArrayList<Value>();
			int [] field_IDs = sp.getFieldIDs();
			String [] toSearch = sp.getToSearch();
			for(String data : toSearch){
				for(int fieldID : field_IDs){
					Value [] temp = db.getValueDAO().getValues(data, fieldID);
					if(temp!=null){
						for(Value v : temp){
							if(v!=null){
								values.add(v);
							}
						}
					}
		
				}
			}
			for(Value v : values){
				Batch batch = db.getBatchDAO().getBatch(v.getBatch_id());
				output.addResults(v.getBatch_id(), batch.getImage_file_path(), v.getRec_num(), v.getField_id());
			}
			
			db.endTransaction(true);
			return output;
		}catch(Exception e){
			db.endTransaction(false);
			throw new ServerException(e.getMessage(), e);
		}
	}
	
	public static Download_Result downloadFile(Download_Params dp) throws IOException{
		InputStream is = new FileInputStream(dp.getUrl());
		byte[] result = null;
		result = IOUtils.toByteArray(is);
		is.close();
		return new Download_Result(result);
	}
	
	public static void main(String args[]) throws Exception{
//		System.out.print("ServerFacade test commencing...");
//		ServerFacade sf = new ServerFacade();
//		sf.initialize();
//		System.out.println("initiated");
//		System.out.print("Loading test data...");
//		DataImporter di = new DataImporter();
//		String [] loader = new String[1];
//		loader[0]="/users/guest/t/thyer/Desktop/Records.xml";
//		di.main(loader);
//		System.out.println("imported");
//		Credentials creds = new Credentials("sheila", "parker");
//		
//		int [] ids = new int[]{
//				1,2,3,4,5,17,24
//		};
//		String[] searchterms = new String[]{
//				"FOX", "19", "MILES", "WHITE"
//		};
//		Search_Params sp = new Search_Params(creds, ids, searchterms);
//		Search_Result sr = sf.search(sp);
//		for(SearchInfo si : sr.getInfo()){
//			System.out.println("\n\tObject:");
//			System.out.println("Batch ID: "+si.getBatchID());
//			System.out.println("Field ID: "+si.getFieldID());
//			System.out.println("Image URL: "+si.getImageURL());
//			System.out.println("Record#: "+si.getRec_num()+"\n\n");
//		}
//		
//		
//		System.out.println("ServerFacade test concluding");
	}

}
