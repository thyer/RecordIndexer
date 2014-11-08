package server;

import org.junit.* ;

import shared.modelclasses.*;
import static org.junit.Assert.* ;

public class ServerUnitTests {
	
	@Before
	public void setup() {
	}
	
	@After
	public void teardown() {
	}
	
	@Test
	public void create_model_objects() {
		Batch batch = new Batch();
		batch.setImage_file_path("newImage");
		assertTrue(batch.getID()==0);
		assertTrue(batch.getProject_id()==0);
		assertFalse(batch.getImage_file_path()==null);
		
		Value value = new Value("data");
		assertTrue(value.getData()=="data");
		assertTrue(value.getBatch_id()==0);
		
		Credentials creds = new Credentials("", "");
		UserInfo ui = new UserInfo("", "", "");
		User user = new User(creds, ui);
		assertTrue(user.getCreds().getUsername()=="");
		creds.setUsername("new Username");
		assertFalse(user.getCreds().getUsername()=="");
	}

	public static void main(String[] args) {
		
		String[] testClasses = new String[] {
				"server.ServerUnitTests", 
				"server.database.BatchDAOTest",
				"server.database.FieldDAOTest",
				"server.database.ProjectDAOTest",
				"server.database.UserDAOTest",
				"server.database.ValueDAOTest", 
				"server.facade.ServerFacadeTest"
		};

		org.junit.runner.JUnitCore.main(testClasses);
	}
	
}

