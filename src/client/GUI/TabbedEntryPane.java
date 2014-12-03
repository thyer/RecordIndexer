package client.GUI;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import shared.modelclasses.Field;

import com.puppycrawl.tools.checkstyle.gui.AbstractCellEditor;

import client.BatchManager.BatchState;

public class TabbedEntryPane extends JTabbedPane {
	private BatchState batchstate;
	private JPanel formPanel;
	private DataTableModel tableModel;
	private JScrollPane tablePanel;
	
	public TabbedEntryPane (BatchState bs){
		super();
		batchstate = bs;
		batchstate.addListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				update();
			}
		});
		formPanel = new JPanel();

		
		if(getBasicTable()!=null){
			System.out.println("Adding table in constructor of TabbedEntryPane");
			tablePanel = new JScrollPane(getBasicTable());
		}
		this.add("Table Entry", tablePanel);
		this.add("Form Entry", formPanel);
	}
	
	public void update(){
		if(tablePanel==null && getBasicTable()!=null){
			System.out.println("Adding table in update method of TabbedEntryPane");
			tablePanel = new JScrollPane(getBasicTable());
			this.removeAll();
			this.add("Table Entry", tablePanel);
			this.add("Form Entry", formPanel);
		}
		
		revalidate();
		repaint();
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
		Object[][] basicdata = batchstate.getIndexedData();

		JTable basicTable = new JTable(basicdata, columnNames){
			public boolean isCellEditable(int row, int column){  
		        return (column>0);  
		      } 
		};
		basicTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		basicTable.setCellSelectionEnabled(true);
		return basicTable;
	}

}
