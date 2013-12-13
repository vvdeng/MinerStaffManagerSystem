package com.vvdeng.miner.staff.ui.model;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.vvdeng.miner.staff.utils.GlobalData;

public class RealtimeReaderTableModel extends AbstractTableModel {
    public static final String[] HEADER={"序号","编号","名称","区域","当前人数","详情"};
	private List<Object[]> realtimeReaderList;
	public RealtimeReaderTableModel(List<Object[]> realtimeReaderList){
    	this.realtimeReaderList=realtimeReaderList;
    }
	public void setModel(List<Object[]> realtimeReaderList){
		this.realtimeReaderList=realtimeReaderList;
	}
	@Override
	public int getRowCount() {
		// TODO Auto-generated method stub
		if(realtimeReaderList==null){
			return 0;
		}
		return realtimeReaderList.size();
	}

	@Override
	public int getColumnCount() {
		// TODO Auto-generated method stub
		return HEADER.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		// TODO Auto-generated method stub
		switch (columnIndex) {
		case 0:
			return rowIndex+1;
		
		case 1:
			 return realtimeReaderList.get(rowIndex)[0];
		case 2:
			 return GlobalData.cardReaderMap.get(realtimeReaderList.get(rowIndex)[0]).getName();
		case 3:
			return GlobalData.cardReaderMap.get(realtimeReaderList.get(rowIndex)[0]).getRegion();
		case 4:
			 return realtimeReaderList.get(rowIndex)[1];
		case 5:
			return "查看";
		
		}
		return "";
		
	}
	@Override
	public String getColumnName(int index){
		return HEADER[index];
	}
}
