package client.GUI;

import static servertester.views.Constants.DOUBLE_VSPACE;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class SearchFrame extends JFrame{
	CredentialsPanel cp;
	SearchPanel sp;
	
	public SearchFrame(){
		super();
		setTitle("Record Indexer - Server Tester");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));		
		add(Box.createRigidArea(DOUBLE_VSPACE));
		cp = new CredentialsPanel(this);
		add(cp);
		pack();
		
	}
	
	public void openSearchOptions(boolean b){
		if(b){
			//System.out.println("Perform further");
			sp = new SearchPanel(this);
			add(sp);
			pack();
		}
		else{
			//System.out.println("Fail user, ask reprompt");
			JOptionPane.showMessageDialog(this, "Login failed. Please try again.");
		}
	}
	
	public LoginInfo getLoginInfo(){
		return cp.getLoginInfo();
	}

}
