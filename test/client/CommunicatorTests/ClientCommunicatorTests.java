package client.CommunicatorTests;

import static org.junit.Assert.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import client.communicator.*;
import server.dataimporter.DataImporter;
import shared.communication.*;
import shared.modelclasses.Credentials;
import shared.modelclasses.Value;

public class ClientCommunicatorTests {
	private ClientCommunicator cc;
	

	@Before
	public void setUp() throws Exception {
		cc = new ClientCommunicator("localhost", 8081);
	}

	@After
	public void tearDown() throws Exception {
		cc=null;
	}

	@Test
	public void ValidateUser() throws Exception {
		Credentials creds = new Credentials("test1","test1");
		ValidateUser_Result result = cc.validateUser(creds);
		assertTrue(result.isOutput());
		
		creds = new Credentials("test1","test2");
		result = cc.validateUser(creds);
		assertFalse(result.isOutput());
		
		creds = new Credentials("","");
		result = cc.validateUser(creds);
		assertFalse(result.isOutput());
		
		creds = new Credentials("$AAA","!@#$%^!@%^");
		result = cc.validateUser(creds);
		assertFalse(result.isOutput());
	}
	
	@Test
	public void testGetProjects() throws Exception 
	{
		Credentials creds = new Credentials("test1","test1");
		GetProjects_Result result = cc.getProjects(creds);
		assertTrue(result.getProjects()!=null);
		assertTrue(result.getProjects().length==3);
		
		creds = new Credentials("test2","test1");
		try{
			result = cc.getProjects(creds);
			assertTrue(false);
		}catch(Exception e){
			assertTrue(true);
		}
		
	}
	
	@Test
	public void testGetSampleImage() throws Exception {
		DataImporter di = new DataImporter();
		String [] in = new String[]{
				"/users/guest/t/thyer/Desktop/Records/Records.xml"
		};
		di.main(in);
		Credentials creds = new Credentials("test1","test1");
		Get_Sample_Batch_Params gsbp = new Get_Sample_Batch_Params(creds,1);
		Image_URL result = cc.getSampleImage(gsbp);
		System.out.println(result.getUrl_path());
		assertEquals(result.getUrl_path(),"images/1890_image0.png");

		gsbp.setProjectID(2);
		result = cc.getSampleImage(gsbp);
		assertEquals(result.getUrl_path(), "images/1900_image0.png"); 
		
		gsbp.setProjectID(20000);
		try{
			result = cc.getSampleImage(gsbp);
			assertTrue(false);
		} catch(Exception e){
			assertTrue(true);
		}
		
		creds.setPassword("badpassword");
		try{
			result = cc.getSampleImage(gsbp);
			assertTrue(false);
		} catch(Exception e){
			assertTrue(true);
		}
	}
	
	@Test
	public void testDownloadBatch() throws Exception{
		DataImporter di = new DataImporter();
		String [] in = new String[]{
				"/users/guest/t/thyer/Desktop/Records/Records.xml"
		};
		di.main(in);
		Credentials creds = new Credentials("sheila", "parker");
		Get_Sample_Batch_Params gsbp = new Get_Sample_Batch_Params(creds, 1);
		Batch_Result br = cc.downloadBatch(gsbp);
		assertTrue(br.getBatchID()>0 & br.getBatchID()<30);
		
		creds.setPassword("badpw");
		try{
			gsbp.setCreds(creds);
			br = cc.downloadBatch(gsbp);
			assertTrue(false);
		} catch(Exception e){
			assertTrue(true);
		}
		
	}
	
	@Test
	public void testSubmitBatch() throws Exception{
		DataImporter di = new DataImporter();
		String [] in = new String[]{
				"/users/guest/t/thyer/Desktop/Records/Records.xml"
		};
		di.main(in);
		Credentials creds = new Credentials ("sheila","parker");					//first setting up to make sure she's got a batch
		Get_Sample_Batch_Params gsbp = new Get_Sample_Batch_Params(creds, 1);
		Batch_Result br = cc.downloadBatch(gsbp);
		assertTrue(br.getBatchID()>0 & br.getBatchID()<30);
		Value [] values = new Value[8];
		for(int i=0; i<8; ++i){
			Value v = new Value("test" + (i+1));
			v.setBatch_id(br.getBatchID());
			v.setField_id(i);
			v.setRec_num(i/4 + 1);
			values[i]=v;
		}
		
		Batch_Submit_Params bsp = new Batch_Submit_Params(creds, br.getBatchID(), values);
		assertTrue(cc.submitBatch(bsp));
		
		creds.setPassword("badpw");
		try{
			gsbp.setCreds(creds);
			br = cc.downloadBatch(gsbp);
			assertTrue(false);
		} catch(Exception e){
			assertTrue(true);
		}
	}
	
	@Test
	public void testGetFields() throws Exception{
		DataImporter di = new DataImporter();
		String [] in = new String[]{
				"/users/guest/t/thyer/Desktop/Records/Records.xml"
		};
		di.main(in);
		Credentials creds = new Credentials ("sheila","parker");
		Get_Sample_Batch_Params gsbp = new Get_Sample_Batch_Params(creds, 1);
		Field_Result fr = cc.getFields(gsbp);
		assertTrue(fr.getFields().size()==4);
		
		gsbp.setProjectID(0);											//0 is a special case handled by the controller
		fr=cc.getFields(gsbp);
		assertTrue(fr.getFields().size()==13);							//get all fields, no project specified

		creds.setPassword("badpw");
		try{
			gsbp.setCreds(creds);
			fr=cc.getFields(gsbp);
			assertTrue(false);
		} catch(Exception e){
			assertTrue(true);
		}
	}
	
	@Test
	public void testSearch() throws Exception{
		DataImporter di = new DataImporter();
		String [] in = new String[]{
				"/users/guest/t/thyer/Desktop/Records/Records.xml"
		};
		di.main(in);
		Credentials creds = new Credentials ("sheila","parker");
		int[] ints = new int[]{											//single search
				10
		};
		String[] strings = new String[]{
				"Perez"
		};
		Search_Params sp = new Search_Params(creds, ints, strings);
		Search_Result sr = cc.search(sp);
		assertTrue(sr.getInfo().size()==1);
		
		ints[0]=0;														//bad fields
		sr = cc.search(sp);
		assertTrue(sr.getInfo().size()==0);
		
		ints[0]=13;														//multi-dimensional search
		strings[0]="WHITE";
		sr = cc.search(sp);
		assertTrue(sr.getInfo().size()==22);
		
		strings[0]="whIte";												//upper/lower case doesn't matter
		sr = cc.search(sp);
		assertTrue(sr.getInfo().size()==22);
		
		creds.setPassword("badpw");										//bad credentials
		try{
			sp.setCreds(creds);
			sr=cc.search(sp);
			assertTrue(false);
		} catch(Exception e){
			assertTrue(true);
		}
	}
	
	@Test
	public void testGet() throws Exception{
		try{
			cc.doGet("badPath");
			assertTrue(false);
		}catch(Exception e){
			assertTrue(true);
		}
	}
}
