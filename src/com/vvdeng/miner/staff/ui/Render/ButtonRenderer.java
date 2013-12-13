package com.vvdeng.miner.staff.ui.Render;

import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class ButtonRenderer implements TableCellRenderer {
	private JPanel panel;

	private JButton button;

	public ButtonRenderer() {
		panel = new JPanel();

		
		FlowLayout flowLayout=new FlowLayout();
		flowLayout.setHgap(0);
		flowLayout.setVgap(0);
		panel.setLayout(flowLayout);
		button = new JButton();

		panel.add(button);
	}

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {

		button.setText(value == null ? "" : String.valueOf(value));

		return panel;
	}

}