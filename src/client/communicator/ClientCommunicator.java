package client.communicator;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.io.IOUtils;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import shared.communication.*;
import shared.modelclasses.*;

public class ClientCommunicator {
	
	private static String SERVER_HOST;
	private static int SERVER_PORT;
	private static String URL_PREFIX;
	private static final String HTTP_GET = "GET";
	private static final String HTTP_POST = "POST";

	private XStream xmlStream;

	public ClientCommunicator() {
		xmlStream = new XStream(new DomDriver());
		SERVER_HOST = "localhost";
		SERVER_PORT = 8080;
	}
	
	public ClientCommunicator(String host, int port){
		xmlStream = new XStream(new DomDriver());
		SERVER_HOST = host;
		SERVER_PORT = port;
	}

		
	private Object doPost(String urlPath, Object postData) throws ClientException {
		assert urlPath !=null;
		assert postData !=null;
		
		try {
			URL_PREFIX = "http://" + SERVER_HOST + ":" + SERVER_PORT;
			URL url = new URL(URL_PREFIX + urlPath);
//			System.out.println("************************************");
//			System.out.println("URL: " + url.toString());
//			System.out.println("Post body details: ");
//			System.out.println("\tAddress: " + postData.toString());
//			System.out.println("\tClass: " + postData.getClass());
//			System.out.println("\tHash Code: " + postData.hashCode());
//			System.out.println("************************************");
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
//			System.out.println("Connection successfully opened - line 53");
			connection.setRequestMethod(HTTP_POST);
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setRequestProperty("Accept",  "html/text");
			connection.connect();
//			System.out.println("Connect method successfully called on connection - line 59");
			xmlStream.toXML(postData, connection.getOutputStream());
//			System.out.println("XML Stream successfully brought data back - line 61");
			//connection.getOutputStream().close();
			if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				throw new ClientException(String.format("doPost failed: %s (http code %d)",
						urlPath, connection.getResponseCode()));
			}
			else{
//				System.out.println("HTTP is good");
				Object o = (Object) xmlStream.fromXML(connection.getInputStream());
				return o;
			}
		}
		catch (IOException e) {
			throw new ClientException(String.format("doPost failed: %s", e.getMessage()), e);
		}
	}
	
	public byte[] doGet(String urlPath) throws ClientException
	{
		byte[] result = null;
		try
		{
			URL_PREFIX = "http://" + SERVER_HOST + ":" + SERVER_PORT;
			URL url = new URL(urlPath);
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			connection.setRequestMethod("GET");
			connection.setDoOutput(true);
			connection.connect();
			
			if(connection.getResponseCode() == HttpURLConnection.HTTP_OK) //200
			{
				InputStream response = connection.getInputStream();
				result = IOUtils.toByteArray(response);
				response.close();
			}
		}
		catch(Exception e)
		{
			throw new ClientException(e);
		}
		
		return result;
	}
	
	/**
	 * Validates a user and returns a ValidateUser_Result object
	 * @param creds the credentials to validate
	 * @return a ValidateUser_Result object containing all user info and a boolean stating whether the user exists
	 * @throws ClientException 
	 */
	public ValidateUser_Result validateUser(Credentials creds) throws ClientException{
		return (ValidateUser_Result)doPost("/validateUser", creds);
	}
	
	/**
	 * Gets the projects of a given user
	 * @param creds the credentials of that user
	 * @return a Result object containing an array of project information which is null if the user doesn't exist
	 */
	public GetProjects_Result getProjects(Credentials creds) throws ClientException{
		return (GetProjects_Result)doPost("/getProjects", creds);
	}
	
	/**
	 * Gets a sample image URL returned as a Image_URL object
	 * @param creds_prjID an object containing the credentials and requested project ID of the user
	 * @return a Result object containing a URL if found and null if an error occurs
	 */
	public Image_URL getSampleImage (Get_Sample_Batch_Params creds_prjID) throws ClientException{
		return (Image_URL)doPost("/getSampleImage", creds_prjID);
	}
	
	/**
	 * Gets a Batch_Result object which contains all the data needed for a Batch
	 * @param creds_prjID an object containing credentials and the requested project ID of the user
	 * @return a Result object containing all Batch Info along with its associated fields
	 */
	public Batch_Result downloadBatch (Get_Sample_Batch_Params creds_prjID) throws ClientException{
		return (Batch_Result)doPost("/downloadBatch", creds_prjID);
	}
	
	/**
	 * Returns a boolean value depending on whether or not the submission was successful
	 * @param submitted_info a data container object containing the values to be submitted
	 * @return true if the submission is successful else false
	 */
	public boolean submitBatch(Batch_Submit_Params submitted_info) throws ClientException{
		return (boolean)doPost("/submitBatch", submitted_info);
	}
	
	/**
	 * Returns a Field_Result Object containing info for all fields in a project
	 * @param creds_prjID the user's credentials and projectID requested
	 * @return a Field_Result object every field's Project ID, Field ID, and Title. 
	 * @throws ClientException 
	 */
	public Field_Result getFields(Get_Sample_Batch_Params creds_prjID) throws ClientException{
		return (Field_Result)doPost("/getFields", creds_prjID);
	}
	
	/**
	 * Returns a search result object. Individual bits of information must be accessed with the get methods
	 * @param params user credentials, field IDs, and search values
	 * @return an Array of SearchInfo objects. Access each element by using the get methods. Returns null if there's an error or no such values
	 * @throws ClientException 
	 */
	public Search_Result search(Search_Params params) throws ClientException{
		return (Search_Result)doPost("/search", params);
	}
	
	public static void main(String args[]) throws ClientException{
		ClientCommunicator cc = new ClientCommunicator();
		Credentials creds = new Credentials("test1", "test1");
		System.out.println("hi");
		System.out.println(cc.validateUser(creds).toString());
		return;
	}

}
