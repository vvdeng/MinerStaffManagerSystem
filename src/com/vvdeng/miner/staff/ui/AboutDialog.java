package com.vvdeng.miner.staff.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.vvdeng.miner.staff.utils.SysConfiguration;
import com.vvdeng.miner.staff.utils.Util;

class AboutDialog extends JDialog {
	public AboutDialog(JFrame owner) {
		super(owner, "����", true);
		setIconImage(SysConfiguration.sysIcon);
		// add HTML label to center
		setLayout(new GridBagLayout());
		add(new JLabel(new ImageIcon("resources/about.png")), new GBC(0, 0));
		add(new JLabel(
				"<html><h3><i>������Ա����ϵͳ</i></h3><hr>&nbsp;&nbsp;&nbsp;&nbsp;μ�Ϲ��Ƽ����޹�˾</html>"),
				new GBC(1, 0));

		JButton ok = new JButton("ȷ��");
		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				// setVisible(false);
				dispose();
			}
		});

		JPanel panel = new JPanel();
		panel.add(ok);
		add(panel, new GBC(0, 1, 2, 1).setAnchor(GridBagConstraints.CENTER));

		setResizable(false);
		setSize(300, 150);
		setLocation(Util.calculatePosition(owner, this));
	}
}
