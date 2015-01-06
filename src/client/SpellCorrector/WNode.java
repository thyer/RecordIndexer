package client.SpellCorrector;

public class WNode implements Trie.Node {
	private int count;
	private WNode[] children = new WNode[28];
	
	public WNode(){
		count = 0;
	}
	
	@Override
	public int getValue() {
		// TODO Auto-generated method stub
		return count;
	}

	public void countPlus() {
		++count;
	}

	public WNode getChild(int index) {
		return children[index];
	}

	public void addChild(int index) {
		this.children[index] = new WNode();
	}

	@Override
	public String toString(){
		return ""+count;
		
	}
	
	@Override
	public int hashCode(){
		return count;
		
	}
	
	@Override
	public boolean equals(Object o){
		if(this==o){
			return true;
		}
		else if(o==null){
			return false;
		}
		else if(this.getClass()!=o.getClass()){
			return false;
		}
		else{
			WNode t = (WNode) o;
			if(t.count!=this.count){
				return false;
			}
			return true;
		}
		
	}
}
