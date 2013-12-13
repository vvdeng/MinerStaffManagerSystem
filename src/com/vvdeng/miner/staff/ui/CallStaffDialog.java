package com.vvdeng.miner.staff.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;

import com.vvdeng.miner.staff.comm.CmdObject;
import com.vvdeng.miner.staff.dao.StaffDAO;
import com.vvdeng.miner.staff.entity.Staff;
import com.vvdeng.miner.staff.ui.model.StaffListModel;
import com.vvdeng.miner.staff.utils.GlobalData;
import com.vvdeng.miner.staff.utils.Util;

public class CallStaffDialog extends JDialog {
	public CallStaffDialog(StaffManagerFrame owner) {
		super(owner);
		this.owner = owner;
		staffDAO = owner.getStaffDAO();
		setSize(WIDTH, HEIGHT);
		setTitle("人员呼叫");
		setResizable(false);
		setLocation(Util.calculatePosition(owner, this));
		build();
	}

	public void build() {
		setLayout(new GridBagLayout());
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new GridBagLayout());
		topPanel.setBorder(BorderFactory.createEtchedBorder());
		topPanel.setPreferredSize(new Dimension(328, 25));
		add(topPanel, new GBC(0, 0, 3, 1).setFillH().setInsets(5, 0, 5, 0));
		ButtonGroup buttonGroup = new ButtonGroup();
		final JRadioButton allBtn = new JRadioButton("全部呼叫");
		final JRadioButton partBtn = new JRadioButton("选择呼叫");
		buttonGroup.add(allBtn);
		buttonGroup.add(partBtn);
		topPanel.add(allBtn,
				new GBC(0, 0).setAnchor(GBC.WEST).setInsets(0, 0, 0, 10));
		topPanel.add(partBtn,
				new GBC(1, 0).setAnchor(GBC.WEST).setInsets(0, 0, 0, 0));
		topPanel.add(new JLabel("（最多选择12个员工）"),
				new GBC(2, 0).setAnchor(GBC.WEST).setWeight(100, 0));

		JPanel selPanel = new JPanel();
		selPanel.setBorder(BorderFactory.createTitledBorder("员工列表"));
		selPanel.setPreferredSize(new Dimension(150, 250));
		selPanel.setLayout(new GridBagLayout());
		JPanel arrowPanel = new JPanel();
		arrowPanel.setPreferredSize(new Dimension(32, 250));

		arrowPanel.setLayout(new GridBagLayout());
		JPanel targetPanel = new JPanel();
		targetPanel.setBorder(BorderFactory.createTitledBorder("呼叫员工列表"));
		targetPanel.setPreferredSize(new Dimension(150, 250));
		targetPanel.setLayout(new GridBagLayout());
		add(selPanel, new GBC(0, 1).setFillBoth().setInsets(0));
		add(arrowPanel, new GBC(1, 1).setFillV());
		add(targetPanel, new GBC(2, 1).setFillBoth());
		selStaffList = new JList();
		selModel = new StaffListModel(staffDAO.listStaffs());
		selStaffList.setModel(selModel);
		JScrollPane selScrollPane = new JScrollPane(selStaffList);

		selPanel.add(selScrollPane, new GBC(0, 0).setFillBoth().setWeightFull());
		JButton arrowRBtn = new JButton("->");
		arrowRBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				for (int index : selStaffList.getSelectedIndices()) {

					Object item = selModel.getElementAt(index);

					selModel.removeItemAt(index);
					targetModel.addItem(item);

				}

			}
		});
		arrowRBtn.setPreferredSize(new Dimension(30, 20));
		JButton arrowLBtn = new JButton("<-");
		arrowLBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				for (int index : targetStaffList.getSelectedIndices()) {

					Object item = targetModel.getElementAt(index);

					targetModel.removeItemAt(index);
					selModel.addItem(item);

				}

			}
		});
		arrowLBtn.setPreferredSize(new Dimension(30, 20));
		arrowPanel.add(arrowRBtn, new GBC(0, 0).setAnchor(GBC.CENTER));
		arrowPanel.add(arrowLBtn, new GBC(0, 1).setAnchor(GBC.CENTER));

		targetStaffList = new JList();
		targetStaffList.setBackground(Color.WHITE);
		targetModel = new StaffListModel(new ArrayList());
		targetStaffList.setModel(targetModel);
		JScrollPane targetScrollPane = new JScrollPane(targetStaffList);

		targetPanel.add(targetScrollPane, new GBC(0, 0).setFillBoth()
				.setWeightFull());

		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new FlowLayout());
		JButton okBtn = new JButton("确定");
		okBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				List<Staff> tList = targetModel.getModelList();
				byte[] staffIdArr = null;
				CmdObject cmdObject=new CmdObject();
				cmdObject.setCmdType(CmdObject.CMD_CALL_STAFF);
				if (allBtn.isSelected()) {
					staffIdArr = new byte[3];
					staffIdArr[0] = 1;
					staffIdArr[1] = 0x7f;
					staffIdArr[2] = 0x7f;
					

				} else if(partBtn.isSelected()) {
					int size = tList.size();
					for (Object item : targetModel.getModelList()) {
						Staff staff = (Staff) item;
						System.out.println("staff:" + " name="
								+ staff.getName() + " cardNo="
								+ staff.getCardId());

					}
					/*
					 * byte []staffIdArr=new byte[25]; staffIdArr[0]=12; for(int
					 * i=1;i<=12;i++) { staffIdArr[2*i-1]=0;
					 * staffIdArr[2*i]=(byte)i; }
					 */
					staffIdArr = new byte[size * 2 + 1];
					staffIdArr[0] = (byte) size;
					for (int i = 0; i < size; i++) {
						Integer cardNo = tList.get(i).getCardId();
						staffIdArr[2 * i + 1] = (byte) (cardNo / 256);
						staffIdArr[2 * i + 2] = (byte) (cardNo % 256);
					}
				}
				else{
					JOptionPane.showMessageDialog(CallStaffDialog.this, "请选择呼叫类型");
					return;
				}
				cmdObject.setExtraData(staffIdArr);
				GlobalData.cmdQueue.add(cmdObject);
				dispose();

			}
		});
		JButton cancelBtn = new JButton("取消");
		cancelBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				dispose();
			}
		});
		bottomPanel.add(okBtn);
		bottomPanel.add(cancelBtn);

		add(bottomPanel, new GBC(0, 2, 3, 1).setFillH());
	}

	public static final int WIDTH = 400;
	public static final int HEIGHT = 360;
	private StaffManagerFrame owner;
	private StaffDAO staffDAO;
	private JList targetStaffList;
	private JList selStaffList;
	private StaffListModel selModel;
	private StaffListModel targetModel;

}
