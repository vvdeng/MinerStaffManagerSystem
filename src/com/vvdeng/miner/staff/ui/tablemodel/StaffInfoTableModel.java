package com.vvdeng.miner.staff.ui.tablemodel;

import java.util.List;

import javax.swing.table.AbstractTableModel;

public class StaffInfoTableModel extends AbstractTableModel {
    public static final String[] HEADER={"���","id","����","����","�Ա�","��������","����","����","����","��ϵ�绰","��Ҫ�����ص�"};
	private List<Object[]> staffArr;
	public StaffInfoTableModel(List<Object[]>  staffArr){
    	this.staffArr=staffArr;
    }
	public void setModel(List<Object[]>  staffArr){
		this.staffArr=staffArr;
	}
	@Override
	public int getRowCount() {
		// TODO Auto-generated method stub
		return staffArr==null?0:staffArr.size();
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
		if(columnIndex==4){//�Ա�
			return (Integer)staffArr.get(rowIndex)[columnIndex-1]==0?"��":"Ů";
		}
		return staffArr.get(rowIndex)[columnIndex-1];
	}
	@Override
	public String getColumnName(int index){
		return HEADER[index];
	}
}
