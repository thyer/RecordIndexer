package server.dataimporter;

import java.io.*;
import java.rmi.ServerException;
import java.sql.SQLException;

import javax.xml.parsers.*;

import org.apache.commons.io.FileUtils;
import org.w3c.dom.*;

import server.databaseaccess.Database;
import shared.modelclasses.*;

public class DataImporter {
	
	public static void handleUsers(NodeList userList) throws ServerException, SQLException{
		Database db = new Database();
		for (int i=0; i<userList.getLength(); ++i) {
			//Obtains relevant information
			String username = ((NodeList) userList.item(i)).item(1).getTextContent();
			String password = ((NodeList) userList.item(i)).item(3).getTextContent();
			String firstname = ((NodeList) userList.item(i)).item(5).getTextContent();
			String lastname = ((NodeList) userList.item(i)).item(7).getTextContent();
			String email = ((NodeList) userList.item(i)).item(9).getTextContent();
			int records = Integer.parseInt(((NodeList) userList.item(i)).item(11).getTextContent());

			//uses relevant information to create User and add to database
			Credentials creds = new Credentials(username, password);
			UserInfo ui = new UserInfo(firstname, lastname, email);
			User user = new User(creds, ui);
			user.setRecordCount(records);
			user.setCurrentBatch(0);
			db.startTransaction();
			db.getUserDAO().addNewUser(user);
			db.endTransaction(true);
			}
	}
	
	public static void handleProjects(NodeList projectList) throws ServerException, SQLException{
		Database db = new Database();
		for(int i=0; i<projectList.getLength(); ++i){
			//Pulls relevant information for project
			Element projectElement = (Element) projectList.item(i);
			String title = ((NodeList) projectList.item(i)).item(1).getTextContent();
			int recordsPerImage = Integer.parseInt(((NodeList) projectList.item(i)).item(3).getTextContent());
			int firstY = Integer.parseInt(((NodeList) projectList.item(i)).item(5).getTextContent());
			int record_height = Integer.parseInt(((NodeList) projectList.item(i)).item(7).getTextContent());
			ProjectInfo pi = new ProjectInfo(title);
			pi.setFirst_y_coord(firstY);
			pi.setRecord_height(record_height);
			pi.setRecords_per_image(recordsPerImage);
			Project project = new Project();
			project.setProjectinfo(pi);
			
			//adding new project to database
			db.startTransaction();
			int projectID = db.getProjectDAO().addProject(project);
			db.endTransaction(true);
					
			//adds the fields associated with each project
			NodeList fieldList = projectElement.getElementsByTagName("field");
			handleFields(fieldList, projectID);
			//adds the batches associated with each project
			NodeList batchList = projectElement.getElementsByTagName("image");
			handleBatches(batchList, projectID);
		}
	}
	
	public static void handleFields(NodeList fieldList, int project_id) throws ServerException, SQLException{
		Database db = new Database();

		for(int i=0; i<fieldList.getLength(); ++i){
			//pulls relevant information to create each field with
			Element field = (Element) fieldList.item(i);
			String title = (field.getElementsByTagName("title")).item(0).getTextContent();
			int xcoord = Integer.parseInt(((field.getElementsByTagName("xcoord")).item(0).getTextContent()));
			int width = Integer.parseInt((field.getElementsByTagName("width")).item(0).getTextContent());
			
			//extra measure of safety here, ensures these items are found before adding
			String helphtml = "";
			if(field.getElementsByTagName("helphtml").item(0)!=null){
				helphtml = field.getElementsByTagName("helphtml").item(0).getTextContent();
			}
			String knowndata = "";
			if(field.getElementsByTagName("knowndata").item(0)!=null){
				knowndata = (field.getElementsByTagName("knowndata")).item(0).getTextContent();
			}
			
			//creates the field object and adds to database
			Field newField = new Field(title);
			newField.setField_help_file_path(helphtml);
			newField.setKnown_data_path(knowndata);
			newField.setNum(i+1);
			newField.setProjectID(project_id);
			newField.setWidth(width);
			newField.setXcoordinate(xcoord);
			db.startTransaction();
			db.getFieldDAO().updateField(newField);
			db.endTransaction(true);
		}
	}
	
	public static void handleBatches(NodeList batchList, int project_id) throws ServerException, SQLException{
		Database db = new Database();
		
		for(int i=0; i<batchList.getLength(); ++i){
			//pulls the relevant information for each batch object
			Element batch = (Element) batchList.item(i);
			String image = (batch.getElementsByTagName("file")).item(0).getTextContent();
			
			//adds the batch and gathers some metadata for each record
			Batch b = new Batch();
			b.setImage_file_path(image);
			//System.out.println("Adding image: " + image);
			b.setProject_id(project_id);
			b.setState(0);
			db.startTransaction();
			int batch_id = db.getBatchDAO().updateBatch(b);
			db.endTransaction(true);
			
			//counts number of records per batch and passes through to handleRecords method
			NodeList recordList = batch.getElementsByTagName("record");
			for(int j=0; j<recordList.getLength(); ++j){
				Element record = (Element)recordList.item(j);
				NodeList valueList = record.getElementsByTagName("value");
				handleRecords(valueList, project_id, batch_id, j+1);
			}
			
		}
	}
	
	public static void handleRecords(NodeList valueList, int project_id, int batch_id, int rec_num) throws ServerException, SQLException{
		Database db = new Database();
		for(int i=0; i<valueList.getLength(); ++i){
			db.startTransaction();
			Field[] f = db.getFieldDAO().getFields(project_id);
			db.endTransaction(true);
			int offset = f[0].getID();
			//pulls the relevant information
			Value value = new Value(valueList.item(i).getTextContent());
			value.setBatch_id(batch_id);
			value.setRec_num(rec_num);
			value.setField_id(i+offset);
			
			//adds new value to the database
			db.startTransaction();
			db.getValueDAO().addValue(value);
			db.endTransaction(true);
		}
	}
	
	public static void main(String[] args) throws Exception {
		//first grabbing and copying all basic files
		File file = new File(args[0]);
		String sourcePath = file.getParent();
		File source = new File(sourcePath);
		File destination = new File("downloadFiles");
		FileUtils.cleanDirectory(destination);
		FileUtils.copyDirectory(source, destination);
		
		//Initializes database, loads basic tables
		Database db = new Database();
		db.startTransaction();
		db.loadDefaultTables();
		db.endTransaction(true);

		//Sets up the parser, gives a document to work with
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = builder.parse(file);
			
		//Pulls out elements, handles each in a separate method
		NodeList userList = doc.getElementsByTagName("user");
		handleUsers(userList);
		NodeList projectList = doc.getElementsByTagName("project");
		handleProjects(projectList);
		
		//System.out.println("Successfully imported the file: " + args[0]);
	}
	
}
