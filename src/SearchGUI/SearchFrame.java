package SearchGUI;


import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class SearchFrame extends JFrame{
	CredentialsPanel cp;
	SearchPanel sp;
	
	public SearchFrame(){
		super();
		setTitle("Record Indexer - Server Tester");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));		
		cp = new CredentialsPanel(this);
		add(cp);
		pack();
	}
	
	public void openSearchOptions(boolean b){
		if(b){
			sp = new SearchPanel(this);
			add(sp);
			pack();
		}
		else{
			JOptionPane.showMessageDialog(this, "Login failed. Please try again.");
		}
	}
	
	public LoginInfo getLoginInfo(){
		return cp.getLoginInfo();
	}

}
