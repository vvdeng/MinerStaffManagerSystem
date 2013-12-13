package com.vvdeng.miner.staff.ui.model;

import java.util.Date;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.vvdeng.miner.staff.entity.CardReader;
import com.vvdeng.miner.staff.utils.GlobalData;
import com.vvdeng.miner.staff.utils.Util;

public class TrackTableModel extends AbstractTableModel {
	public static final String[] HEADER = { "ÐòºÅ",  "¿¨ºÅ", "¶Á¿¨Æ÷±àºÅ", "¶Á¿¨Æ÷Ãû³Æ",
			"¶Á¿¨Æ÷Î»ÖÃ", "µ½´ïÊ±¼ä", "ÊÂ¼þ" };
	private List<Object[]> trackArr;

	public TrackTableModel(List<Object[]> trackArr) {
		this.trackArr = trackArr;
	}

	public void setModel(List<Object[]> trackArr) {
		this.trackArr = trackArr;
	}

	@Override
	public int getRowCount() {
		// TODO Auto-generated method stub
		return trackArr == null ? 0 : trackArr.size();
	}

	@Override
	public int getColumnCount() {
		// TODO Auto-generated method stub
		return HEADER.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		// TODO Auto-generated method stub
		CardReader reader=GlobalData.cardReaderMap.get(trackArr.get(rowIndex)[1]);
        switch (columnIndex) {
		case 0:
			return rowIndex + 1;
		case 1:
			return trackArr.get(rowIndex)[0];
		case 2:
			return trackArr.get(rowIndex)[1];
		case 3: //Ãû³Æ
			if(reader!=null){
				return reader.getName();
			}
			else {
				return "";
			}
		case 4: //Î»ÖÃ
			if(reader!=null){
				return reader.getRegion();
			}
			else {
				return "";
			}
		case 5:
			 return Util.formatDateTime(new Date((Long)trackArr.get(rowIndex)[2]));
		case 6:
			 return trackArr.get(rowIndex)[3];
		}
		
		return "";
	}

	@Override
	public String getColumnName(int index) {
		return HEADER[index];
	}
}
