package client.GUI;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import shared.communication.*;
import shared.communication.Field_Result.FieldInfo;
import shared.communication.Search_Result.SearchInfo;
import client.communicator.ClientCommunicator;

public class SearchPanel extends JPanel{
	private JPanel fieldPanel;
	private JPanel searchPanel;
	private JPanel resultPanel;
	private JTextField searchterms;
	private SearchFrame parent;
	private JComboBox<String> imageResults;
	
	public SearchPanel(SearchFrame sf) {
		super();
		parent = sf;
		setLayout(new GridLayout(4,1));
		searchterms = new JTextField(40);
		
		//sets up the field pane
		fieldPanel = new JPanel();
		fieldPanel.setToolTipText("These are the fields you can search against");
		
		LoginInfo logininfo = parent.getLoginInfo();
		ClientCommunicator cc = new ClientCommunicator(logininfo.getLocalhost(), logininfo.getPort());
		try{
			GetProjects_Result gpr = cc.getProjects(logininfo.getCreds());
			final int FIELDSWIDTH = Math.max(gpr.getProjects().length, 10);
			fieldPanel.setLayout(new GridLayout(gpr.getProjects().length/10+1,gpr.getProjects().length));
			for(int i=0; i<gpr.getProjects().length; ++i){
				JPanel fieldpanel = new JPanel();
				
				Get_Sample_Batch_Params gsbp = new Get_Sample_Batch_Params(logininfo.getCreds(), i+1);
				Field_Result fr = cc.getFields(gsbp);
				fieldpanel.setLayout(new GridLayout(fr.getFields().size()+1,1));
				fieldpanel.add(new JLabel("Project: " + gpr.getProjects()[i].getID()));
				for(FieldInfo fi : fr.getFields()){
					String name = fi.getField_Title();
					JRadioButton rb = new JRadioButton(name);
					fieldpanel.add(rb);	
					rb.addActionListener(new ActionListener(){
						@Override
						public void actionPerformed(ActionEvent arg0){
							//todo: find out how to record the radio button's text field as part of a list
						}
					});
				}
				
				
				fieldPanel.add(fieldpanel);
			}
			add(fieldPanel);
			
			
		} catch(Exception e){
			e.printStackTrace();
		}
		
		//sets up the search terms field
		searchPanel = new JPanel();
		JButton _searchButton = new JButton("Search");
		searchPanel.setLayout(new FlowLayout());;
		searchPanel.add(new JLabel("Search terms, comma-separated"));
		searchPanel.add(searchterms);
		searchPanel.add(_searchButton);
		add(searchPanel);
		
		_searchButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				search();
			}
		});

		
		resultPanel = new JPanel();

		imageResults = new JComboBox<String>();
		resultPanel.add(imageResults);
		JButton viewButton = new JButton("View Image");
		resultPanel.add(viewButton);
		add(resultPanel);
	}
	
	public void search(){
		int tempFieldID=0;
		ArrayList<Integer> fieldIDList = new ArrayList<Integer>();
		
		//jumps through the radio buttons, figures out which are checked, and adds those fieldIDS to a list
		for(Component c : fieldPanel.getComponents()){
			JPanel temp = new JPanel();
			if(c.getClass() == temp.getClass()){
				for(Component d : ((JPanel)c).getComponents()){
					JRadioButton j = new JRadioButton();
					if(d.getClass() == j.getClass()){
						++tempFieldID;
						j = (JRadioButton) d;
						if(j.isSelected()){
							fieldIDList.add(tempFieldID);
						}
					}
				}
			}
		}
		
		//grabs all the search terms and parses them out according to commas
		List<String> searchTermList = Arrays.asList(searchterms.getText().split(",",-1));
		
		//packaging data
		String[] searchterms = new String[searchTermList.size()];
		for(int i=0; i<searchterms.length; ++i){
			searchterms[i] = searchTermList.get(i);
		}
		int[] fieldterms = new int[fieldIDList.size()];
		for(int i=0; i<fieldterms.length; ++i){
			fieldterms[i] = fieldIDList.get(i);
		}
		
		//sending everything across the wire
		LoginInfo logininfo = parent.getLoginInfo();
		Search_Params sp = new Search_Params(logininfo.getCreds(), fieldterms, searchterms);
		ClientCommunicator cc = new ClientCommunicator(logininfo.getLocalhost(), logininfo.getPort());
		
		//pulling everything back
		Set<String> images = new TreeSet<String>();
		try{
			Search_Result sr = cc.search(sp);
			imageResults.removeAllItems();
			for(SearchInfo si : sr.getInfo()){
				images.add(si.getImageURL());
			}
			for(String s : images){
				imageResults.addItem(s);
			}
		}catch(Exception e){
			System.out.println("Huge problem");
		}
		
	}
	
	public void loadImage(){
		
	}

}
