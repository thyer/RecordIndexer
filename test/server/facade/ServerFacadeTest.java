package server.facade;

import static org.junit.Assert.*;

import java.rmi.ServerException;
import java.sql.SQLException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import server.dataimporter.DataImporter;
import shared.communication.Batch_Result;
import shared.communication.Batch_Submit_Params;
import shared.communication.GetProjects_Result;
import shared.communication.Search_Params;
import shared.communication.Search_Result;
import shared.communication.Search_Result.SearchInfo;
import shared.communication.ValidateUser_Result;
import shared.modelclasses.Credentials;
import shared.modelclasses.Value;

public class ServerFacadeTest {
	ServerFacade sf;
	
	@Before
	public void setup() throws Exception {
		System.out.print("ServerFacade test commencing...");
		sf = new ServerFacade();
		sf.initialize();
		System.out.println("initiated");
		System.out.print("Loading test data...");
		DataImporter di = new DataImporter();
		String [] loader = new String[1];
		loader[0]="/users/guest/t/thyer/Desktop/Records.xml";
		di.main(loader);
		System.out.println("imported");
	}
	
	@After
	public void teardown() throws Exception {
		System.out.println("ServerFacade test concluding");
		sf.firebomb();
		DataImporter di = new DataImporter();
		String [] loader = new String[1];
		loader[0]="/users/guest/t/thyer/Desktop/Records.xml";
		di.main(loader);
	}
	
	@Test
	public void search() throws ServerException {
		System.out.println("Testing search");
		Credentials creds = new Credentials("sheila", "parker");
		
		int [] ids = new int[]{
				10,22,11
		};
		String[] searchterms = new String[]{
				"FOX", "19", "RUSSEL", "WHITE"
		};
		Search_Params sp = new Search_Params(creds, ids, searchterms);
		Search_Result sr = sf.search(sp);
		assertTrue(sr.getInfo().size()==2);
		ids[0]=13;
		searchterms[3] = "BLACK";
		sr = sf.search(sp);
		assertTrue(sr.getInfo().size()==26);
	}
	
	@Test
	public void validateUser() throws ServerException{
		System.out.println("Testing user validation");
		Credentials creds = new Credentials("sheila", "parker");
		ValidateUser_Result vr = sf.validateUser(creds);
		assertTrue(vr.getFirstName().equalsIgnoreCase("Sheila"));
	}
	
	@Test
	public void getSampleImage() throws ServerException{
		System.out.println("Testing sample image");
		Credentials creds = new Credentials("sheila", "parker");
		String result = sf.getSampleImage(creds, 2);
		System.out.println(result);
		assertTrue(result!=null & !result.equals(""));
		assertTrue(result.contains("1900"));
	}
	
	@Test
	public void getProjects() throws ServerException{
		System.out.println("Testing get projects");
		Credentials creds = new Credentials("sheila", "parker");
		GetProjects_Result gr = sf.getProjects(creds);
		assertTrue(gr.getProjects().length==3);
	}
	
	@Test
	public void downloadBatch() throws ServerException{
		System.out.println("Testing download batch");
		Credentials creds = new Credentials("sheila", "parker");
		int projectID = 1;
		Batch_Result br = sf.downloadBatch(creds, projectID);
		assertTrue(br.getProjectID()==projectID);
		assertTrue(br.getField_array().length>0);
	}
	
	@Test
	public void submitBatch() throws ServerException{
		System.out.println("Testing submit batch");
		Credentials creds = new Credentials("sheila", "parker");
		Value[] values = new Value[4];
		for(int i=0; i<4; ++i){
			Value v = new Value("1");
			v.setData("hi" + i);
			v.setBatch_id(1);
			v.setField_id(i);
			v.setRec_num(1);
			values[i] = v;
		}

		Batch_Submit_Params bsp = new Batch_Submit_Params(creds, 1, values);
		try{
			sf.submitBatch(bsp);
			assertTrue(false);
		}
		catch(ServerException e){
			assertTrue(true);
		}
		
		Batch_Result br = sf.downloadBatch(creds, 1);
		for(int i=0; i<4; ++i){
			Value v = new Value("1");
			v.setData("hi" + i);
			v.setBatch_id(br.getBatchID());
			v.setField_id(i);
			v.setRec_num(1);
			values[i] = v;
		}
		bsp = new Batch_Submit_Params(creds, br.getBatchID(), values);
		assertTrue(sf.submitBatch(bsp));
	}
}
