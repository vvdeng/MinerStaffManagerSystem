package com.vvdeng.miner.staff.ui;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.jdesktop.swingx.JXDatePicker;

import com.vvdeng.miner.staff.filter.PositionLogFilter;
import com.vvdeng.miner.staff.ui.model.TrackTableModel;
import com.vvdeng.miner.staff.utils.UIUtil;
import com.vvdeng.miner.staff.utils.Util;

public class TrackDialog extends JDialog {
	public TrackDialog(StaffManagerFrame owner,final PositionLogFilter filter){
		
		super(owner, "��켣", true);
		this.owner=owner;
		
		System.out.println("Track cardId="+filter.getCardId());
		
		setSize(WIDTH, HEIGHT);
		setResizable(false);
		UIUtil.setCenterPositoin(owner, this);
		setLayout(new GridBagLayout());
		JPanel queryPanel=new JPanel();
		queryPanel.setPreferredSize(new Dimension(WIDTH-6,30));
	//	queryPanel.setSize(new Dimension(500,100));
		queryPanel.setMinimumSize(new Dimension(WIDTH-6,30));
		queryPanel.setBorder(BorderFactory.createEtchedBorder());
		queryPanel.setLayout(new GridBagLayout());
		JLabel startTimeLable=new JLabel("��ʼʱ��");
		startTimePicker = new JXDatePicker();
		startTimePicker.setFormats(Util.dateFormat);
		startTimePicker.setPreferredSize(new Dimension(130, 20));
		
		JLabel endTimeLable=new JLabel("����ʱ��");
		endTimePicker = new JXDatePicker();
		endTimePicker.setFormats(Util.dateFormat);
		endTimePicker.setPreferredSize(new Dimension(130, 20));
		JButton queryBtn=new JButton("��ѯ");
		queryBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				PositionLogFilter lFilter=new PositionLogFilter();
				lFilter.setCardId(filter.getCardId());
				if(startTimePicker.getDate()!=null){
					lFilter.setStartTime(Util.getDateLong(startTimePicker.getDate()));
				}
				if(endTimePicker.getDate()!=null){
					lFilter.setEndTime(Util.getDateLong(endTimePicker.getDate()));
				}
				trackTableModel.setModel(TrackDialog.this.owner.getPositionLogDAO().queryByCondition(lFilter));
				trackTableModel.fireTableDataChanged();
				
			}
		});
		queryPanel.add(startTimeLable,new GBC(0,0));
		queryPanel.add(startTimePicker,new GBC(1,0).setInsets(0, 10, 0, 40));
		queryPanel.add(endTimeLable,new GBC(2,0));
		queryPanel.add(endTimePicker,new GBC(3,0).setInsets(0, 10, 0, 40));
		queryPanel.add(queryBtn,new GBC(4,0));
		trackTableModel = new TrackTableModel(
				owner.getPositionLogDAO().queryByCondition(filter));
		JTable resultTable= new CenterJTable(trackTableModel);
		resultTable.setFillsViewportHeight(true);
		
		JScrollPane resultPanel=new JScrollPane(resultTable);
		add(queryPanel,new GBC(0, 0).setFillH().setWeight(100, 0).setInsets(1));
		add(resultPanel,new GBC(0, 1).setFillBoth().setWeightFull().setInsets(1));
		this.addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent e) {
				// TODO Auto-generated method stub
				
				super.windowClosing(e);
			}
		});
	
	}
	
	public static final int WIDTH=800;
	public static final int HEIGHT=500;
	private StaffManagerFrame owner;
	private JXDatePicker startTimePicker;
	private JXDatePicker endTimePicker;
	private TrackTableModel trackTableModel;
}
