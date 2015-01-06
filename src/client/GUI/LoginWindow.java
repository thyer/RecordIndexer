package client.GUI;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import client.BatchManager.BatchState;
import shared.modelclasses.Credentials;

public class LoginWindow extends JFrame{
	private JTextField usernameText;
	private JPasswordField passwordText;
	private String host;
	private int port;
	private BatchState batchstate;
	
	public LoginWindow(BatchState bs){
		//setup
		super();
		batchstate = bs;
		setTitle("Record Indexer - Login");
		setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//user panel
		JPanel user = new JPanel();
		user.add(new JLabel("Username:"));
		usernameText = new JTextField(30);
		usernameText.setText("test1");
		passwordText = new JPasswordField(30);
		passwordText.setText("test1");
		user.add(usernameText);
		add(user);
		
		//password panel
		JPanel pass = new JPanel();
		pass.add(new JLabel("Password"));
		pass.add(passwordText);
		add(pass);
		
		//buttonbar
		JPanel buttonbar = new JPanel();
		
		//login button
		JButton loginButton = new JButton("Login");
		buttonbar.add(loginButton);
		loginButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				batchstate.validateUser();
			}
		});
		
		//cancel button
		JButton cancelButton = new JButton("Cancel");
		buttonbar.add(cancelButton);
		cancelButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				dispose();
			}
		});
		
		//finalization
		add(buttonbar);
		pack();
	}
	
	public Credentials getCreds(){
		Credentials creds = new Credentials(usernameText.getText(), new String(passwordText.getPassword()));
		return creds;
	}

}
