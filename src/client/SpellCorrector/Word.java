package client.SpellCorrector;

public class Word {
	private String word;
	private int frequency;
	
	public Word(String s){
		word = s;
		frequency=0;
	}
	
	public Word (String s, int i){
		word = s;
		frequency = i;
	}

	public String getWord() {
		return word;
	}


	public int getFrequency() {
		return frequency;
	}

	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}
	
	public String toString(){
		return word + " " + frequency +"\n";
	}
}
