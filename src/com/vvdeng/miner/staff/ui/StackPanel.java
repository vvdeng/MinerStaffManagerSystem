package com.vvdeng.miner.staff.ui;

import java.awt.Component;

import javax.swing.JPanel;

public class StackPanel extends JPanel {
	public StackPanel(){
		super();
		setVisible(false);
	}
	public void showMe() {

		
		for (Component component : getParent().getComponents()) {
			if (this==component) {
				
				component.setVisible(true);
			} else {
			
				component.setVisible(false);
			}
		}
	}

}
