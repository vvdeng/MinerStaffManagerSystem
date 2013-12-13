package com.vvdeng.miner.staff.ui.model;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.vvdeng.miner.staff.entity.StaffExtra;
import com.vvdeng.miner.staff.utils.GlobalData;

public class StaffInfoTableModel extends AbstractTableModel {
	public static final String[] HEADER = { "���", "id", "����", "����", "����", "����",
			"����", "����", "��ϵ�绰", "��Ҫ�����ص�", "��ǰλ��", "��켣" };
	private List<Object[]> staffArr;

	public StaffInfoTableModel(List<Object[]> staffArr) {
		this.staffArr = staffArr;
	}

	public void setModel(List<Object[]> staffArr) {
		this.staffArr = staffArr;
	}

	@Override
	public int getRowCount() {
		// TODO Auto-generated method stub
		return staffArr == null ? 0 : staffArr.size();
	}

	@Override
	public int getColumnCount() {
		// TODO Auto-generated method stub
		return HEADER.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		// TODO Auto-generated method stub
		if (columnIndex == 0) {
			return rowIndex + 1;
		}

		else if (columnIndex <= 9) {
			return staffArr.get(rowIndex)[columnIndex - 1];
		} else if (columnIndex == 10) {
			if (GlobalData.staffExtraMap.containsKey(staffArr.get(rowIndex)[1])
					&& GlobalData.staffExtraMap.get(staffArr.get(rowIndex)[1])
							.getState().equals(StaffExtra.IN_MINER)) {
				return "����";
			} else {
				return "����";
			}
		} else if (columnIndex == 11) {
			return "�鿴";
		}

		return "";
	}

	@Override
	public String getColumnName(int index) {
		return HEADER[index];
	}
}
