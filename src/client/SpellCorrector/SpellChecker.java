package client.SpellCorrector;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.TreeSet;

public class SpellChecker implements SpellCorrector{
	private boolean activated = true;	
	private WordTree wt = new WordTree();
	
	/**
	 * Tells this <code>SpellCorrector</code> to use the given file as its dictionary
	 * for generating suggestions. 
	 * @param dictionaryFileName File containing the words to be used
	 * @throws IOException If the file cannot be read
	 */
	@Override
	public void useDictionary(String dictionaryFileName) throws IOException {
		URL url = new URL(dictionaryFileName);
		Scanner scan = new Scanner (url.openStream());
		List<String> words = Arrays.asList(scan.nextLine().split(",",-1));
		for(String s : words){
			wt.add(s);
		}
		scan.close();
		
	}
	/**
	 * Suggest a word from the dictionary that most closely matches
	 * <code>inputWord</code>
	 * @param inputWord
	 * @return The suggestion
	 * @throws NoSimilarWordFoundException If no similar word is in the dictionary
	 */
	@Override
	public String suggestSimilarWord(String inputWord)
			throws NoSimilarWordFoundException {
		if(!activated){
			return inputWord;
		}
		for(int i=0; i<inputWord.length(); ++i){
			StringBuilder sb = new StringBuilder (inputWord);
			if(!Character.isLetter(sb.charAt(i)) && sb.charAt(i)!='-' && sb.charAt(i)!=' '){
				throw new NoSimilarWordFoundException();
			}
		}
		if(wt.find(inputWord)!=null){
			return inputWord;
		}
		else{
			ArrayList <String> words = new ArrayList<String>();
			edit1(inputWord, words);
			Word winner = new Word("");
			for(int i=0; i<words.size(); ++i){
				WNode q = (WNode) wt.find(words.get(i));
				if(q!=null){
					if(q.getValue()>winner.getFrequency()){
						winner = new Word(words.get(i), q.getValue());
					}
					else if(q.getValue()==winner.getFrequency() & 
							words.get(i).compareTo(winner.getWord())<0){
						winner = new Word(words.get(i), q.getValue());
					}
				}
			}
			if (winner.getFrequency()>0){
				return winner.getWord();
			}
			else
			{
				ArrayList <String> words2 = new ArrayList<String>();
				for(int j=0; j<words.size();++j){
					edit1(words.get(j), words2);
				}
				for(int i=0; i<words2.size(); ++i){
					WNode q = (WNode) wt.find(words2.get(i));
					if(q!=null){
						if(q.getValue()>winner.getFrequency()){
							winner = new Word(words2.get(i), q.getValue());
						}
						else if(q.getValue()==winner.getFrequency() & 
								words2.get(i).compareTo(winner.getWord())>0){
							winner = new Word(words2.get(i), q.getValue());
						}
					}
				}
			}
			if (winner.getFrequency()>0){
				return winner.getWord();
			}
			else{
				throw new NoSimilarWordFoundException();
			}
		}
	}
	
	public String[] getSimilarWords(String inputWord) throws NoSimilarWordFoundException{
		if(!activated){
			return new String[]{
					inputWord
			};
		}
		for(int i=0; i<inputWord.length(); ++i){
			StringBuilder sb = new StringBuilder (inputWord);
			if(!Character.isLetter(sb.charAt(i)) && sb.charAt(i)!='-' && sb.charAt(i)!=' '){
				throw new NoSimilarWordFoundException();
			}
		}
		if(wt.find(inputWord)!=null){
			return new String[]{
					inputWord
			};
		}
		else{
			ArrayList <String> words = new ArrayList<String>();
			edit1(inputWord, words);
			ArrayList <String> words2 = new ArrayList<String>();
			for(int j=0; j<words.size();++j){
				edit1(words.get(j), words2);
			}
			TreeSet <String> outputWords = new TreeSet<String>();
			for(int i=0; i<words2.size(); ++i){
				WNode q = (WNode) wt.find(words2.get(i));
				if(q!=null){
					outputWords.add(words2.get(i).toUpperCase());
				}
			}
			if(outputWords.size()>0){
				String[] output = new String[outputWords.size()];
				int i=0;
				for(String s : outputWords){
					output[i]=s;
					++i;
				}
				return output;
			}
			else{
				throw new NoSimilarWordFoundException();
			}
		}
	}
	
	private void edit1(String word, ArrayList<String> edits){
		edits.add(word);
		for(int i=0; i<word.length(); ++i){		//deletion
			StringBuilder temp = new StringBuilder(word);
			temp.deleteCharAt(i);
			edits.add(temp.toString());	
		}
		
		for(int i=0; i<word.length()-1; ++i){ 	//transposition
			StringBuilder temp = new StringBuilder(word);
			char t;
			t=temp.charAt(i);
			temp.deleteCharAt(i);
			temp.insert(i+1, t);
			edits.add(temp.toString());	
		}
		
		for(int i=0; i<word.length(); ++i){		//alteration
			for(int j=0; j<26; ++j){
				StringBuilder temp = new StringBuilder(word);
				char t = 'a';
				t+=j;
				temp.deleteCharAt(i);
				temp.insert(i,t);
				edits.add(temp.toString());
			}
		}
		
		for(int i=0; i<word.length()+1; ++i){ 	//insertion
			for(int j=0; j<26; ++j){
				StringBuilder temp = new StringBuilder(word);
				char t = 'a';
				t+=j;
				temp.insert(i,t);
				edits.add(temp.toString());
			}
		}
	}
	
	public boolean isActivated() {
		return activated;
	}
	
	public void setActivated(boolean activated) {
		this.activated = activated;
	}

}
