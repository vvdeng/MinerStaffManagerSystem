package com.vvdeng.miner.staff.ui;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

public class CenterJTable extends JTable {
	public CenterJTable(TableModel tableModel){
		super(tableModel);
	}
	public TableCellRenderer getCellRenderer(int row, int column) {
		TableCellRenderer renderer = super.getCellRenderer(row, column);
		if (renderer instanceof JLabel) {
			((JLabel) renderer).setHorizontalAlignment(JLabel.CENTER);
		}
		return renderer;
	}

}
