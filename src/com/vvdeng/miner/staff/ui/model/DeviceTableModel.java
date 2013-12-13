package com.vvdeng.miner.staff.ui.model;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.vvdeng.miner.constant.StationCommState;
import com.vvdeng.miner.staff.entity.SubDevice;

public class DeviceTableModel extends AbstractTableModel {
    public static final String[] HEADER={"±àºÅ","id","Ãû³Æ","×´Ì¬"};
	private List<SubDevice> deviceList;
	public DeviceTableModel(List<SubDevice> deviceList){
    	this.deviceList=deviceList;
    }
	public void setModel(List<SubDevice> deviceList){
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
		SubDevice subDevice=deviceList.get(rowIndex);
		switch (columnIndex) {
		case 0:
			return rowIndex+1;
		case 1:
			return subDevice.getId();
		case 2:
			return subDevice.getName();
		case 3:
			return StationCommState.values()[subDevice.getState()].getDesc();
		
		
		}
		
		return "";
	}
	@Override
	public String getColumnName(int index){
		return HEADER[index];
	}
	public static Integer testCount=0;
}
