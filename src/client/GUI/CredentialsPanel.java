package client.GUI;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import shared.communication.*;
import shared.modelclasses.Credentials;
import client.communicator.ClientCommunicator;
import client.communicator.ClientException;


@SuppressWarnings("serial")
public class CredentialsPanel extends JPanel{
	
	private JTextField hostText;
	private JTextField portText;
	private JTextField userText;
	private JPasswordField passwordText;
	private JButton _loginButton;
	private SearchFrame parent;

	public CredentialsPanel(SearchFrame sf){
		super();
		
		parent = sf;
		add(new JLabel("Host:"));
		add(Box.createRigidArea(new Dimension(0,2)));
		hostText = new JTextField(12);
		hostText.setText("localhost");
		add(hostText);
		add(new JLabel("Port:"));
		add(Box.createRigidArea(new Dimension(0,2)));
		portText = new JTextField(8);
		portText.setText("8081");
		add(portText);
		add(new JLabel("Username:"));
		add(Box.createRigidArea(new Dimension(0,2)));
		userText = new JTextField(10);
		userText.setText("sheila");
		add(userText);
		add(new JLabel("Password:"));
		add(Box.createRigidArea(new Dimension(0,2)));
		passwordText = new JPasswordField(10);
		passwordText.setText("parker");
		add(passwordText);
		_loginButton = new JButton("Login");
		add(_loginButton);
		
		_loginButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				validateUser();
			}
		});
	}
	
	private void validateUser(){
		try{
			ClientCommunicator cc = new ClientCommunicator(hostText.getText(), Integer.parseInt(portText.getText()));
			ValidateUser_Result vur = cc.validateUser(new Credentials(userText.getText(), new String(passwordText.getPassword())));
			if(vur.isOutput()){
				parent.openSearchOptions(true);
				hostText.setEnabled(false);
				portText.setEnabled(false);
				userText.setEnabled(false);
				passwordText.setEnabled(false);
				_loginButton.setVisible(false);
			}
			else{
				throw new ClientException();
			}
		}catch(Exception e){
			parent.openSearchOptions(false);
		}
	}
	
	public LoginInfo getLoginInfo(){
		Credentials creds = new Credentials(userText.getText(), new String(passwordText.getPassword()));
		LoginInfo output = new LoginInfo(creds, hostText.getText(),Integer.parseInt(portText.getText()));
		return output;
	}

}
