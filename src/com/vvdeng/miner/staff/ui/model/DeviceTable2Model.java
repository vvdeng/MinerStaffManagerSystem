package com.vvdeng.miner.staff.ui.model;

import java.util.List;

import javax.swing.table.AbstractTableModel;

public class DeviceTable2Model extends AbstractTableModel {
    public static final String[] HEADER={"±àºÅ","id","Ãû³Æ","×´Ì¬"};
	private List<Object[]> deviceList;
	public DeviceTable2Model(List<Object[]> deviceList){
    	this.deviceList=deviceList;
    }
	public void setModel(List<Object[]> deviceList){
		this.deviceList=deviceList;
	}
	@Override
	public int getRowCount() {
		// TODO Auto-generated method stub
		return deviceList.size();
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
		return deviceList.get(rowIndex)[columnIndex-1];
	}
	@Override
	public String getColumnName(int index){
		return HEADER[index];
	}
}
