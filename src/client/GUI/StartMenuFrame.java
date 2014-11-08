package client.GUI;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import servertester.controllers.*;
import servertester.views.*;
import static servertester.views.Constants.*;

@SuppressWarnings("serial")
public class StartMenuFrame extends JFrame implements GUIViewInterface {
	
	private IController _controller;
	private JTextField _hostText;
	private JTextField _portText;
	private JTextField _usernameText;
	private JTextField _passwordText;
	
	public StartMenuFrame() {
		super();
		
		_controller = new Controller();
		((Controller)_controller).setView(this);
		setTitle("Search GUI - Login");
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		setLayout(new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS));		
		
		add(Box.createRigidArea(DOUBLE_VSPACE));
		
		JPanel hostPanel = new JPanel();
		hostPanel.add(new JLabel("Host:"));
		hostPanel.add(Box.createRigidArea(SINGLE_HSPACE));
		_hostText = new JTextField(20);
		hostPanel.add(_hostText);
		add(hostPanel);
		add(Box.createRigidArea(SINGLE_VSPACE));
		
		JPanel portPanel = new JPanel();
		portPanel.add(new JLabel("Port:"));
		portPanel.add(Box.createRigidArea(SINGLE_HSPACE));
		_portText = new JTextField(20);
		portPanel.add(_portText);
		add(portPanel);
		add(Box.createRigidArea(SINGLE_VSPACE));
		
		JPanel usernamePanel = new JPanel();
		usernamePanel.add(new JLabel("User:"));
		usernamePanel.add(Box.createRigidArea(SINGLE_HSPACE));
		_usernameText = new JTextField(20);
		usernamePanel.add(_usernameText);
		add(usernamePanel);
		add(Box.createRigidArea(SINGLE_VSPACE));
		
		JPanel passwordPanel = new JPanel();
		passwordPanel.add(new JLabel("Pass:"));
		passwordPanel.add(Box.createRigidArea(SINGLE_HSPACE));
		_passwordText = new JTextField(20);
		passwordPanel.add(_passwordText);
		add(passwordPanel);
		add(Box.createRigidArea(SINGLE_VSPACE));
		
		JButton login = new JButton("Login");
		add(login);
		add(Box.createRigidArea(SINGLE_VSPACE));
		
		
		login.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				getController().executeOperation();
			}
		});
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent arg0) {
				// TODO Auto-generated method stub
			
			}		
		});
		
		pack();
		
		setMinimumSize(getPreferredSize());
	}
	
	
	public void setController(IController value) {
		_controller = value;
	}
	
	public IController getController(){
		return _controller;
	}


	@Override
	public void setHost(String value) {
		
	}


	@Override
	public String getHost() {
		return _hostText.getText();
	}


	@Override
	public void setPort(String value) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public String getPort() {
		// TODO Auto-generated method stub
		return _portText.getText();
	}


	@Override
	public void setOperation(Operations value) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public Operations getOperation() {
		return Operations.VALIDATE_USER;
	}


	@Override
	public void setParameterNames(String[] value) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public String[] getParameterNames() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void setParameterValues(String[] value) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public String[] getParameterValues() {
		String[] output = new String[2];
		output[0] = _usernameText.getText();
		output[1] = _passwordText.getText();
		return output;
	}


	@Override
	public void setRequest(String value) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public String getRequest() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void setResponse(String value) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public String getResponse() {
		// TODO Auto-generated method stub
		return null;
	}




}