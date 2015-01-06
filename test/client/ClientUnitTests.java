package client;

import java.io.IOException;

import org.junit.*;

import server.Server;
import client.BatchManager.BatchState;
import client.SpellCorrector.SpellChecker;
import client.SpellCorrector.SpellCorrector.NoSimilarWordFoundException;
import client.SpellCorrector.SpellFacade;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class ClientUnitTests {
	SpellChecker sc;
	Server server;
	
	@Before
	public void setup() {
		sc = new SpellChecker();
		server.main(new String[]{"8081"});
	}
	
	@After
	public void teardown() {
	}
	
	@Test
	public void load_dictionary() {	
		try {
			sc.useDictionary("http://localhost:8081/downloadFiles/knowndata/ethnicities.txt");
		} catch (IOException e) {
			assertTrue(false);
		}
	}
	
	@Test
	public void recognizesGoodWords() {	
		try {
			sc.useDictionary("http://localhost:8081/downloadFiles/knowndata/ethnicities.txt");
		} catch (IOException e) {
			assertTrue(false);
		}
		String goodString = "ASIAN";
		String goodString2 = "black";
		String goodString3 = "WhItE";
		try {
			assertEquals(goodString, sc.suggestSimilarWord(goodString));
			assertEquals(goodString2, sc.suggestSimilarWord(goodString2));
			assertEquals(goodString3, sc.suggestSimilarWord(goodString3));
		} catch (NoSimilarWordFoundException e) {
			assertTrue(false); //means no word was found, bad exception
		}
	}
	
	@Test
	public void specialCharacters() {	
		try {
			sc.useDictionary("http://localhost:8081/downloadFiles/knowndata/ethnicities.txt");
		} catch (IOException e) {
			assertTrue(false);
		}
		String goodString = "NATIVE HAWAIIAN";
		String goodString2 = "ALASKA NATIVE";
		String badString = "WH ITE";
		try {
			assertEquals(goodString, sc.suggestSimilarWord(goodString));
			assertEquals(goodString2, sc.suggestSimilarWord(goodString2));
			assertThat(badString, not(sc.suggestSimilarWord(badString)));
		} catch (NoSimilarWordFoundException e) {
			assertTrue(false); //means no word was found, bad exception
		}
	}
	
	@Test
	public void failsBadWords() {	
		try {
			sc.useDictionary("http://localhost:8081/downloadFiles/knowndata/ethnicities.txt");
		} catch (IOException e) {
			assertTrue(false);
		}
		String badString = "Asia";
		String badString2 = "lack";
		String badString3 = "Hispaic";
		try {
			assertThat(badString, not(sc.suggestSimilarWord(badString)));
			assertThat(badString2, not(sc.suggestSimilarWord(badString2)));
			assertThat(badString3, not(sc.suggestSimilarWord(badString3)));
		} catch (NoSimilarWordFoundException e) {
			assertTrue(false); //means no word was found, bad exception
		}
	}
	
	@Test
	public void correctlyThrowsException(){
		try {
			sc.useDictionary("http://localhost:8081/downloadFiles/knowndata/ethnicities.txt");
		} catch (IOException e) {
			assertTrue(false);
		}
		
		String badString = " ";
		String badString2 = "Super terrible string";
		String badString3 = "I've even got wrong characters in here";
		try {
			assertThat(badString, not(sc.suggestSimilarWord(badString)));
			assertThat(badString2, not(sc.suggestSimilarWord(badString2)));
			assertThat(badString3, not(sc.suggestSimilarWord(badString3)));
			assertFalse(true);
		} catch (NoSimilarWordFoundException e) {
			assertTrue(true); //means no word was found, bad exception
		}
	}

	public static void main(String[] args) {

		String[] testClasses = new String[] {
				"client.ClientUnitTests",
				"client.CommunicatorTests.ClientCommunicatorTests"
		};

		org.junit.runner.JUnitCore.main(testClasses);
	}
}

