package com.vvdeng.miner.staff.ui.model;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.vvdeng.miner.staff.entity.CardReader;
import com.vvdeng.miner.staff.utils.GlobalData;
import com.vvdeng.miner.staff.utils.Util;

public class ReaderRealtimeDetailTableModel extends AbstractTableModel {
	public static final String[] HEADER = { "序号",  "卡号", "姓名", "部门", "到达时间", "事件" };
	private List<Object[]> readRealtimeDetailList;

	public ReaderRealtimeDetailTableModel(List<Object[]> readRealtimeDetailList) {
		this.readRealtimeDetailList = readRealtimeDetailList;
	}

	public void setModel(List<Object[]> readRealtimeDetailList) {
		this.readRealtimeDetailList = readRealtimeDetailList;
	}

	@Override
	public int getRowCount() {
		// TODO Auto-generated method stub
		return readRealtimeDetailList == null ? 0 : readRealtimeDetailList.size();
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
			return rowIndex + 1;
		case 1:
			return readRealtimeDetailList.get(rowIndex)[0];
		case 2:
			return readRealtimeDetailList.get(rowIndex)[1];
		case 3: //名称
			return readRealtimeDetailList.get(rowIndex)[2];
		case 4: //位置
			
			return Util.formatDateTime(new Date(((BigInteger)readRealtimeDetailList.get(rowIndex)[3]).longValue()));
			
		case 5:
			return readRealtimeDetailList.get(rowIndex)[4]; 
		
		}
		
		return "";
	}

	@Override
	public String getColumnName(int index) {
		return HEADER[index];
	}
}
