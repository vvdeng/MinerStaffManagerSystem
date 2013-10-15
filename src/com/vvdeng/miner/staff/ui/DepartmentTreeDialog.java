package com.vvdeng.miner.staff.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import com.vvdeng.miner.staff.interfaces.DepTreeVisitable;
import com.vvdeng.miner.staff.utils.UIUtil;
import com.vvdeng.miner.staff.utils.Util;

public class DepartmentTreeDialog extends JDialog {
	public DepartmentTreeDialog(final StaffInfoDialog owner,int locationX,int locationY){

		super(owner,"组织架构",true);
		setSize(WIDTH,HEIGHT);
		setLocation(locationX,locationY);
		layout(owner);
	}
	public DepartmentTreeDialog(final StaffManagerFrame owner,int locationX,int locationY){

		super(owner,"组织架构",true);
		setSize(WIDTH,HEIGHT);
		setLocation(locationX,locationY);
		layout(owner);
	}
	public void layout(final DepTreeVisitable v){
		final JTree depTree=new JTree(Util.getDepTree());
		depTree.setBackground(Color.WHITE);
		add(new JScrollPane(depTree),BorderLayout.CENTER);
		JPanel buttonPanel=new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER,5,3));
		buttonPanel.add(UIUtil.makeButton("确定", null, new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(depTree.getSelectionCount()==0){
					JOptionPane.showMessageDialog(DepartmentTreeDialog.this, "请选择");
					return;
				}
				v.setDepInfo((DefaultMutableTreeNode)depTree.getLastSelectedPathComponent());
				DepartmentTreeDialog.this.dispose();
			}
		}));
		buttonPanel.add(UIUtil.makeButton("取消", null, new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				DepartmentTreeDialog.this.dispose();
				
			}
		}));
		add(buttonPanel,BorderLayout.SOUTH);
	}
	public static final int HEIGHT=300;
	public static final int WIDTH=180;
}
