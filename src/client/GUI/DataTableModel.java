package client.GUI;

import javax.swing.table.AbstractTableModel;

public class DataTableModel extends AbstractTableModel{
	private String[][] data;
	private String[] columns;
	
	public DataTableModel(String[][] indexedData){
		this.data = indexedData;
		columns = new String[this.getColumnCount()];
	}

	@Override
	public int getRowCount() {
		return data.length;
	}

	@Override
	public int getColumnCount() {
		return data[0].length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return data[rowIndex][columnIndex];
	}
	
	@Override
	public boolean isCellEditable(int row, int column) {
		if(row>=1){
			return true;
		}
		else{
			return false;
		}
	}
	
	public void setColumnName(int column, String name){
		columns[column] = name;
	}
	
	@Override
	public String getColumnName(int column) {
		if(column ==0){
			return "Record";
		}
		return columns[column];
	}
	
	@Override
	public void setValueAt(Object value, int row, int column) {
		
		if (row >= 0 && row < getRowCount() && column >= 0
				&& column < getColumnCount()) {
			data[row][column] = (String) value;
			
			this.fireTableCellUpdated(row, column);
			
		} else {
			throw new IndexOutOfBoundsException();
		}		
	}
	
	

}
