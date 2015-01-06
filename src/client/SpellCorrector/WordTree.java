package client.SpellCorrector;

import java.util.ArrayList;

public class WordTree implements Trie {
	
	private WNode root;
	private int nodeCount;
	private int wordCount;
	
	
	public WordTree(){
		root = new WNode();
		nodeCount=1;
		wordCount=0;
	}
	
	/**
	 * Adds the specified word to the trie (if necessary) and increments the word's frequency count
	 * 
	 * @param word The word being added to the trie
	 */
	@Override
	public void add(String word) {
		for(int i=0; i<word.length(); ++i){
			StringBuilder sb = new StringBuilder (word);
			if(!Character.isLetter(sb.charAt(i)) && sb.charAt(i)!='-' && sb.charAt(i)!=' '){
				System.out.println("Word rejected: " + word);
				return;
			}
		}
		word = word.toLowerCase();
		WNode current = root;
		char c;
		for (int i = 0; i<word.length(); ++i){
			c = word.charAt(i);
			int index = c-'a';
			if(c=='-'){
				index=26;
			}
			else if(c==' '){
				index=27;
			}
			else{
				//do nothing, it's a regular letter
			}
			if(current.getChild(index)==null){
				current.addChild(index);
				++nodeCount;
				
			}
			current = current.getChild(index);
		}
		if(current.getValue()==0){
			++wordCount;
		}
		current.countPlus();
		
	}
	/**
	 * Searches the trie for the specified word
	 * 
	 * @param word The word being searched for
	 * 
	 * @return A reference to the trie node that represents the word,
	 * 			or null if the word is not in the trie
	 */
	@Override
	public Node find(String word) {
		WNode current = root;
		word = word.toLowerCase();
		char c;
		for (int i=0; i<word.length();++i){
			c = word.charAt(i);
			int index = c-'a';
			if(c=='-'){
				index=26;
			}
			else if(c==' '){
				index=27;
			}
			else{
				//do nothing
			}
			if(current.getChild(index)==null){
				return null;
			}
			else{
				current = current.getChild(index);
			}
		}
		if(current.getValue()==0)
			return null;
		else
			return current;
	}
	/**
	 * Returns the number of unique words in the trie
	 * 
	 * @return The number of unique words in the trie
	 */
	@Override
	public int getWordCount() {
		return wordCount;
	}
	/**
	 * Returns the number of nodes in the trie
	 * 
	 * @return The number of nodes in the trie
	 */
	@Override
	public int getNodeCount() {
		return nodeCount;
	}
	
	/**
	 * The toString specification is as follows:
	 * For each word, in alphabetical order:
	 * <word> <count>\n
	 */
	@Override
	public String toString(){
		ArrayList<Word> words = new ArrayList<Word>();
		StringBuilder sb = new StringBuilder("");
		rString(root, words, sb);
		
		for(int i=0; i<words.size(); ++i){
			sb.append(words.get(i).toString());
		}
		
		return sb.toString();
		
	}
	private void rString (WNode current, ArrayList<Word> al, StringBuilder sb){
		if (current.getValue()>0){
			Word temp = new Word(sb.toString(), current.getValue());
			al.add(temp);
				//System.out.println("New Word added to arrayList: \n\t" + temp.getWord() + 
					//"\n\tFrequency: " + temp.getFrequency());
		}
		for(int i=0; i<26; ++i){
			if(current.getChild(i)!=null){
				char c = 'a';
				c+=i;
				sb.append(c);
				rString(current.getChild(i), al, sb);
			}
		}
		if(sb.length()>0){
			sb.deleteCharAt(sb.length()-1);
		}
	}
	
	@Override
	public int hashCode(){
		return this.toString().hashCode();
		
	}
	
	@Override
	public boolean equals(Object o){
		if(o==null){
			return false;
		}
		else if(this.getClass() != o.getClass()){
			return false;
		}
		WordTree other = (WordTree) o;
		if(this.getWordCount() != other.getWordCount() || this.getNodeCount() != other.getNodeCount()){
			return false;
		}
		else if(this.toString().equals(other.toString())){
			return true;
		}
		else
			return false;
		
	}

}
