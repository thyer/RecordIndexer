package client.GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.PopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import shared.communication.Batch_Result;
import shared.modelclasses.Field;

import com.puppycrawl.tools.checkstyle.gui.AbstractCellEditor;

import client.BatchManager.BatchState;

public class TabbedEntryPane extends JTabbedPane {
	private BatchState batchstate;
	private JScrollPane formPanel;
	private JScrollPane tablePanel;
	private JTable table;
	private JPanel form;
	private JPanel currentEntryForm;
	private JList<String> recordList;
	private List<JPanel> entryFormList;
	
	public TabbedEntryPane (BatchState bs){
		//basic setup
		super();
		batchstate = bs;
		batchstate.addListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				update();
			}
		});

		//if a user restores a session, there'll be data to grab, otherwise this leave the table blank for now
		if(getBasicTable()!=null){
			table = getBasicTable();
			tablePanel = new JScrollPane(table);
		}
		//same with the form
		if(getBasicForm()!=null){
			formPanel = new JScrollPane(getBasicForm());
		}
		this.add("Table Entry", tablePanel);
		this.add("Form Entry", formPanel);
	}
	
	public void update(){	//if the table/form are empty and there's information for them, it'll load both now
		if(tablePanel==null && getBasicTable()!=null && getBasicForm()!=null){
			initializeEntryForms();
		}
		
		//updates the current cell in the table
		table.changeSelection(batchstate.getCurrentCell().getRow()-1, batchstate.getCurrentCell().getColumn(), false, false);
		repaint();
	}
	
	//initializes the entry methods and adds them to the indexing window
	private void initializeEntryForms(){
		table = getBasicTable();
		tablePanel = new JScrollPane(table);
		form = getBasicForm();
		formPanel = new JScrollPane(form);
		this.removeAll();
		this.add("Table Entry", tablePanel);
		this.add("Form Entry", formPanel);
	}
	
	public JTable getBasicTable(){
		//if there's nothing there, don't make the table
		if(batchstate.getBatchInfo()==null){
			return null;
		}
		
		//getting column names, data info
		Field[] fieldarray = batchstate.getBatchInfo().getField_array();
		String[] columnNames = new String[fieldarray.length + 1];
		for(int i=0; i<columnNames.length; ++i){
			if(i==0){
				columnNames[i] = "Record #";
			}
			else{
				columnNames[i] = fieldarray[i-1].getTitle();
			}
		}
		
		//loading data, structuring table
		Object[][] basicdata = batchstate.getIndexedData();
		final JTable basicTable = new JTable(basicdata, columnNames){
			public boolean isCellEditable(int row, int column){  
		        return (column>0);  
		      } 
		};
		
		//adding quality control listener
		basicTable.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getButton()==3){																//checks if right click happened
					final int row = basicTable.rowAtPoint(e.getPoint());
					final int column = basicTable.columnAtPoint(e.getPoint());
					if(!batchstate.checkCell(column, batchstate.getIndexedData()[row][column])){	//checks if the click happened on a red cell
						JPopupMenu popup = new JPopupMenu("");
						JMenuItem menu = new JMenuItem("See Suggestions");							//creates popup menu
						menu.addActionListener(new ActionListener(){
							@Override
							public void actionPerformed(ActionEvent e){								//adds listener to popup menu to give suggestions
								String text = (String) basicTable.getModel().getValueAt(row, column);
								String[] suggestion = batchstate.getSpellingSuggestions(column, text);
								SuggestionWindow suggestionWindow = new SuggestionWindow(suggestion, batchstate, row, column);
								
								//setup
								suggestionWindow.setSize(new Dimension(300,200));
								suggestionWindow.setModal(true);
								suggestionWindow.setResizable(false);
								suggestionWindow.setLocationRelativeTo(null);
								suggestionWindow.setVisible(true);
							}
						});
						popup.add(menu);
						popup.show(e.getComponent(),e.getX(), e.getY());
					}
				}	
			}
			
		});
		
		//finalization
		basicTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		basicTable.setCellSelectionEnabled(true);
		TableColumnModel columnModel = basicTable.getColumnModel();
		for (int i = 1; i < basicTable.getModel().getColumnCount(); ++i) {
			TableColumn column = columnModel.getColumn(i);
			column.setCellRenderer(new ClientCellRenderer());
		}
		return basicTable;
	}
	
	public JPanel getBasicForm(){
		//if there's nothing there, don't make the form
		if(batchstate.getBatchInfo()==null){
			return null;
		}

		//setting up list
		int recNum = batchstate.getBatchInfo().getNum_records();
		String[] recordNumbers = new String[recNum];
		for(int i=0; i<recNum; ++i){
			recordNumbers[i]="" + (i+1) + "       ";
		}
		recordList = new JList<String>(recordNumbers);
		
		//setting up labels and field
		currentEntryForm = new JPanel();
		Batch_Result br = batchstate.getBatchInfo();
		currentEntryForm.setLayout(new GridLayout(br.getNum_fields(),1));
		
		//creating list of entryForms
		entryFormList = new ArrayList<JPanel>();
		for(int pane=0; pane<recordNumbers.length; ++pane){
			final int paneIndex = pane;
			JPanel temp = new JPanel();
			temp.setLayout(new GridLayout(br.getNum_fields(),1));
			for(int i=0; i<br.getNum_fields(); ++i){
				final int fieldIndex = i;
				JLabel label = new JLabel(br.getField_array()[i].getTitle());
				final JTextField field = new JTextField(20);
				field.addFocusListener(new FocusAdapter(){
					@Override 
					public void focusGained(FocusEvent e){
						if(batchstate.getCurrentCell().getRow()!=paneIndex+1 || batchstate.getCurrentCell().getColumn()!=fieldIndex+1){
							batchstate.setCurrentCell(paneIndex+1, fieldIndex+1);
							batchstate.update();
							field.requestFocus();
						}
						field.setBackground(new Color(208, 223, 210));
					}

					@Override
					public void focusLost(FocusEvent e) {
						batchstate.setIndexedData(paneIndex, fieldIndex+1, field.getText());
						//field.setBackground(Color.WHITE);
						batchstate.update();
						//System.out.println("new data indexed");
					}
					
				});
				batchstate.addListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent e) {
						String data;
						try{
							data = batchstate.getIndexedData()[paneIndex][fieldIndex+1];				//controls for null fields
						}catch (Exception e1){
							data=null;
						}
						if(data!=null && !data.equals("")){
							field.setText(data);
						}
						
						//controls highlight color
						if(batchstate.getCurrentCell().getRow()==paneIndex+1 && batchstate.getCurrentCell().getColumn()==fieldIndex+1){
							field.setBackground(new Color(208, 223, 210)); //highlights field green when it's the current field
						}
						else{
							if(batchstate.checkCell(fieldIndex+1, field.getText())){
								field.setBackground(Color.WHITE);			//else it's white
							}
							else{
								field.setBackground(Color.RED);				//unless it's spelled wrong, then we change it to red
							}
							
						}
					}
				});
				temp.add(label);
				temp.add(field);	
			}
			entryFormList.add(temp);
		}
		currentEntryForm.add(entryFormList.get(0));  //defaults to the first one
		recordList.setSelectedIndex(0);
		
		//adding listener to list so it selects the right entryForm
		recordList.addMouseListener(new MouseAdapter(){	
			@Override
			public void mouseReleased(MouseEvent e){
				int index = recordList.getSelectedIndex();
				changeForm(index);
				batchstate.setCurrentCell(recordList.getSelectedIndex()+1, batchstate.getCurrentCell().getColumn());
				batchstate.update();
			}
		});
		
		//adding batchstate listeners to update form
		batchstate.addListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				recordList.setSelectedIndex(batchstate.getCurrentCell().getRow()-1);
				changeForm(recordList.getSelectedIndex());
			}
		});
		
		//adding everything to the UI
		JPanel basicform = new JPanel();
		basicform.setLayout(new BorderLayout());
		basicform.add(recordList, BorderLayout.WEST);
		basicform.add(currentEntryForm, BorderLayout.CENTER);
		
		return basicform;
	}
	
	public void changeForm(int index){
		currentEntryForm.removeAll();
		currentEntryForm.add(entryFormList.get(index));
		recordList.setSelectedIndex(index);
	}

	@SuppressWarnings("serial")
	class ClientCellRenderer extends JLabel implements TableCellRenderer {

		
		//borrowed from ColorTable
		private Border unselectedBorder = BorderFactory.createLineBorder(Color.BLACK, 0);
		private Border selectedBorder = BorderFactory.createLineBorder(Color.BLACK, 1);

		public ClientCellRenderer() {
			setOpaque(true);
			setFont(getFont().deriveFont(12.0f));
		}

		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
		
			//If the cell is selected, updates the current cell everywhere else and highlights accordingly
			if (isSelected || hasFocus) {		
				if(batchstate.getCurrentCell().getRow()!=row+1 || batchstate.getCurrentCell().getColumn()!=column){
					batchstate.setCurrentCell(row+1, column);
					batchstate.update();
				}
				this.setBorder(selectedBorder);
				this.setBackground(new Color(208, 223, 210));
			}
			
			//If the cell isn't selected, it changes the background according to word correctness
			else {
				this.setBorder(unselectedBorder);
				if(batchstate.checkCell(column, (String)value)){
					this.setBackground(Color.WHITE);			//cell is white if text is empty or correct
				}
				else{
					this.setBackground(Color.RED);				//cell is red if text returns incorrect
				}
			}
			
			this.setText((String)value);
			
			return this;
		}

	}
}
