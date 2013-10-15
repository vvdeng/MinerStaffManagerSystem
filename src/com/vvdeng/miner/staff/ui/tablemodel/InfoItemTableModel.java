package com.vvdeng.miner.staff.ui.tablemodel;

import java.util.List;

import javax.swing.table.AbstractTableModel;

public class InfoItemTableModel extends AbstractTableModel {
    public static final String[] HEADER={"±àºÅ","id","Ãû³Æ","ËµÃ÷"};
	private List<Object[]> infoItemList;
	public InfoItemTableModel(List<Object[]> infoItemList){
    	this.infoItemList=infoItemList;
    }
	public void setModel(List<Object[]> infoItemList){
		this.infoItemList=infoItemList;
	}
	@Override
	public int getRowCount() {
		// TODO Auto-generated method stub
		return infoItemList.size();
	}

	@Override
	public int getColumnCount() {
		// TODO Auto-generated method stub
		return HEADER.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		// TODO Auto-generated method stub
		if(columnIndex==0){
			return rowIndex+1;
		}
		return infoItemList.get(rowIndex)[columnIndex-1];
	}
	@Override
	public String getColumnName(int index){
		return HEADER[index];
	}
}
