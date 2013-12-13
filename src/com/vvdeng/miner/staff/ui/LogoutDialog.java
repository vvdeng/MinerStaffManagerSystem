package com.vvdeng.miner.staff.ui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.KeyStroke;

import com.vvdeng.miner.staff.utils.SysConfiguration;
import com.vvdeng.miner.staff.utils.Util;

class LogoutDialog extends JDialog {
	public LogoutDialog(final StaffManagerFrame
			owner) {
		super(owner, "ע��", true);
		setIconImage(SysConfiguration.sysIcon);
		setLayout(new GridBagLayout());
		JPanel panel = new JPanel();
		add(panel,
				new GBC(0, 0).setWeight(100, 100).setFill(
						GridBagConstraints.BOTH));
		panel.setLayout(new GridBagLayout());
		panel.add(
				new JLabel(new ImageIcon("resources/logout.png")),
				new GBC(0, 0, GridBagConstraints.REMAINDER, 1).setFill(
						GridBagConstraints.HORIZONTAL).setWeight(100, 0));

		panel.add(new JLabel("  ���룺"), new GBC(0, 1).setWeight(100, 0)
				.setAnchor(GridBagConstraints.EAST));
	    ActionListener logoutActionListener=new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (owner.getCurrentUser() == null
						|| (pwdTxt.getText() != null && pwdTxt.getText()
								.equals(owner.getCurrentUser().getPwd()))) {
					owner.setCurrentUser(null);
			//		owner.toolbarEnabled(false);
			//		owner.toolbarAndMenuEnabled(false);
			//		owner.showRightPanel(false);
					
				} else {
					JOptionPane.showMessageDialog(owner, "�������벻��ȷ");
				}
				//dispose();
				System.exit(0);
				//owner.dispose();
				
			}
		};
		pwdTxt = new JPasswordField();
		// pwdTxt.setText("       ");
		pwdTxt.setPreferredSize(new Dimension(185, 20));
		 pwdTxt.registerKeyboardAction(logoutActionListener,
	              KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false),
	              JComponent.WHEN_FOCUSED);
		panel.add(
				pwdTxt,
				new GBC(1, 1, 2, 1).setWeight(100, 0)
						.setAnchor(GridBagConstraints.WEST).setInsets(5));
		JButton logoutBtn = new JButton("�˳�");
		logoutBtn.addActionListener(logoutActionListener);
	
		JButton exitBtn = new JButton("ȡ��");
		exitBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				// setVisible(false);
				LogoutDialog.this.dispose();
			}
		});
		panel.add(logoutBtn, new GBC(1, 3).setInsets(5, 5, 5, 0));
		panel.add(exitBtn, new GBC(2, 3).setInsets(5, 0, 5, 5));
		
		setResizable(false);
		setSize(320, 200);
		setLocation(Util.calculatePosition(owner, this));
		pack();
	}

	private JPasswordField pwdTxt;
}
