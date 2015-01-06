package client.SpellCorrector;

import java.io.IOException;
import java.util.ArrayList;

import client.BatchManager.BatchState;
import client.SpellCorrector.SpellCorrector.NoSimilarWordFoundException;

/**
 * Spell Facade is a barrier between my batch state and the spellcheckers. I created it because it allows us to manage 
 * which field we're checking a little easier by creating an array of spellcheckers upon download. SpellFacade manages
 * the correction/suggestion requests by checking which field the text occurs in and then using the correct SpellChecker
 * @author thyer
 *
 */
public class SpellFacade {
	private BatchState batchstate;
	private ArrayList<SpellChecker> spellcheckers;
	private String[] knownData;
	
	public SpellFacade(BatchState bs){
		batchstate=bs;
		spellcheckers = new ArrayList<SpellChecker>();
		knownData = new String[batchstate.getBatchInfo().getField_array().length];
		
		for(int i=0; i<knownData.length; ++i){
			knownData[i]=batchstate.getBatchInfo().getField_array()[i].getKnown_data_path();
		}
		initializeSpellCheckers();
	}
	
	/**
	 * Creates a SpellChecker object for each field in the current batch of the BatchState
	 * Arranges these objects into an indexed array for easily referenced access
	 */
	public void initializeSpellCheckers(){
		for(String s : knownData){
			SpellChecker tempChecker = new SpellChecker();
			try {
				if(!s.equals("")){		//ensures that there's known data for this field
					s = "http://" + batchstate.getHost() + ":" + batchstate.getPort() + "/downloadFiles/" + s;
					tempChecker.useDictionary(s);
					System.out.println("New active spellchecker created on data : " + s);
				}
				else{					//if there's no known data, the SpellChecker is deactivated and always returns true
					tempChecker.setActivated(false);
					System.out.println("New inactive spellchecker created, data: " +s);
				}	
			} catch (Exception e) {
				tempChecker.setActivated(false);	
			}
			spellcheckers.add(tempChecker);
		}
	}

	/**
	 * Determines whether a word is in the known data set
	 * Returns true if the text is recognized by the SpellChecker and returns the same word or if the spellchecker isn't active
	 * Returns false if another word is recognized
	 * @param fieldID
	 * @param text
	 * @return
	 * @throws NoSimilarWordFoundException
	 */
	public boolean checkWord(int fieldID, String text) throws NoSimilarWordFoundException{
		if(spellcheckers.get(fieldID).suggestSimilarWord(text).equals(text)){
			return true;
		}
		return false;
	}
	
	public String getSuggestion(int fieldID, String text) throws NoSimilarWordFoundException{
		return spellcheckers.get(fieldID).suggestSimilarWord(text);
	}
	
	public String[] getSuggestions(int fieldID, String text) throws NoSimilarWordFoundException{
		return spellcheckers.get(fieldID).getSimilarWords(text);
	}

}
