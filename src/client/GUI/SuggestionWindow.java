package client.GUI;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import client.BatchManager.BatchState;

public class SuggestionWindow extends JDialog{
	private String [] suggestions;
	private BatchState batchstate;
	private JList<String> listBox;
	private int row;
	private int column;
	
	public SuggestionWindow(String[] suggestion, BatchState bs, final int row, final int column){
		//setup
		this.suggestions=suggestion;
		this.batchstate=bs;
		this.row=row;
		this.column=column;
		this.setTitle("Suggestions");
		this.setLayout(new BorderLayout());
		
		//word list box
		JPanel listPane = new JPanel();
		listBox = new JList<String>(suggestions);
		listBox.setSelectedIndex(0);
		listBox.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane scrollPane = new JScrollPane(listBox);

		
		//buttons
		JPanel buttonPane = new JPanel();
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				close();
			}
			
		});
		JButton useSuggestionButton = new JButton("Use Suggestion");
		useSuggestionButton.addActionListener(new ActionListener(){				//adds a listener to the "Use Suggestion" button

			@Override
			public void actionPerformed(ActionEvent e) {
				String newText = suggestions[listBox.getSelectedIndex()];
				batchstate.setIndexedData(row, column, newText);				//if a suggestion is chosen, it'll update in the table and form
				batchstate.update();
				close();														//then the SuggestionWindow closes
			}
			
		});
		
		//adding everything
		listPane.add(scrollPane);
		buttonPane.add(cancelButton);
		buttonPane.add(useSuggestionButton);
		this.add(listPane, BorderLayout.CENTER);
		this.add(buttonPane, BorderLayout.SOUTH);
	}
	
	public void close(){
		this.dispose();
	}

}
