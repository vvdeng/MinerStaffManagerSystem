package com.vvdeng.miner.staff.ui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.vvdeng.miner.staff.dao.InfoItemDAO;
import com.vvdeng.miner.staff.entity.InfoItem;
import com.vvdeng.miner.staff.utils.UIUtil;

public class InfoItemInfoDialog extends JDialog {
	public InfoItemInfoDialog(final SysSettingDialog owner,
			final InfoItem infoItem, final InfoItem.TYPE type,final InfoItem parentItem,final InfoItem editedItem) {
		super(owner, "设置", true);
		setSize(new Dimension(WIDTH, HEIGHT));
		infoItemDAO = owner.getInfoItemDAO();
		UIUtil.setCenterPositoin(owner, this);
		setLayout(new GridBagLayout());
		JLabel nameLabel = new JLabel("名称");
		nameTxt = new JTextField();
		nameTxt.setPreferredSize(new Dimension(120, 20));
		JLabel propLabel = new JLabel("属性");
		JLabel descLabel = new JLabel("说明");
		descTxt = new JTextArea();
		descTxt.setPreferredSize(new Dimension(120, 100));
		descTxt.setLineWrap(true);
		descTxt.setBorder(BorderFactory.createEtchedBorder());
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
		JButton editBtn = UIUtil.makeButton("保存", null, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (checkFields()) {
					if (infoItem == null) {
						InfoItem newItem = new InfoItem();
						
						newItem.setType(type.getVal());
						
						newItem.setName(type.getDesc());
						newItem.setValue(nameTxt.getText().trim());
						newItem.setDescription(descTxt.getText().trim());
						if(parentItem!=null){
							newItem.setParentId(parentItem.getId());
							newItem.setLevel(parentItem.getLevel()+1);
						}
						else
						{
							newItem.setParentId(0L);
							newItem.setLevel(1);
						}
						infoItemDAO.save(newItem);
						if(editedItem!=null){
							editedItem.copy(newItem);
						}
					
					}else{
						
						
						
						infoItem.setValue(nameTxt.getText().trim());
						infoItem.setDescription(descTxt.getText().trim());
						infoItemDAO.update(infoItem);
					}
					InfoItemInfoDialog.this.dispose();
				}

			}
		});
		JButton cancelBtn = UIUtil.makeButton("取消", null,
				new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO Auto-generated method stub
						InfoItemInfoDialog.this.dispose();
					}
				});
		buttonPanel.add(editBtn);
		buttonPanel.add(cancelBtn);
		add(nameLabel, new GBC(0, 0).setWeight(0, 0));
		add(nameTxt, new GBC(1, 0).setFillH().setInsets(0, 5, 0, 10));
		add(descLabel, new GBC(0, 1).setWeight(0, 1));
		add(descTxt, new GBC(0, 2, 2, 1).setWeight(100, 100).setFillBoth()
				.setInsets(5));
		add(buttonPanel, new GBC(0, 3, 2, 1).setWeight(100, 0).setFillBoth()
				.setAnchor(GridBagConstraints.CENTER).setInsets(5, 0, 10, 0));
		if (infoItem != null) {
			fillFields(infoItem);
		}
	}
	public InfoItemInfoDialog(final SysSettingDialog owner,
			final InfoItem infoItem, final InfoItem.TYPE type){
		this(owner,infoItem,type,null,null);
	}
	private boolean checkFields() {
		return true;
	}

	private void fillFields(InfoItem infoItem) {
		nameTxt.setText(infoItem.getValue());
		descTxt.setText(infoItem.getDescription());
	}

	private JTextField nameTxt;
	private JTextArea descTxt;
	public static final int HEIGHT = 300;
	public static final int WIDTH = 180;
	private InfoItemDAO infoItemDAO;
}
