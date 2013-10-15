package com.vvdeng.miner.staff;

import java.awt.EventQueue;

import javax.swing.JFrame;

import com.vvdeng.miner.staff.ui.StaffManagerFrame;
import com.vvdeng.miner.staff.utils.DAOUtil;

public class StaffManagerApp {
	public static void main(String[] args) {
		sysInit();
		EventQueue.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				StaffManagerFrame staffFrame = new StaffManagerFrame();
				staffFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				staffFrame.setVisible(true);
				staffFrame.userLogin();
			}
		});

	}

	public static void sysInit() {
		DAOUtil.init();
	}
}
