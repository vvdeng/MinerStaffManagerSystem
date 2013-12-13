package com.vvdeng.miner.staff.ui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.DefaultMutableTreeNode;

import org.hibernate.Hibernate;
import org.jdesktop.swingx.JXDatePicker;

import com.vvdeng.miner.staff.entity.InfoItem;
import com.vvdeng.miner.staff.entity.Staff;
import com.vvdeng.miner.staff.filter.StaffFilter;
import com.vvdeng.miner.staff.interfaces.DepTreeVisitable;
import com.vvdeng.miner.staff.utils.SysConfiguration;
import com.vvdeng.miner.staff.utils.UIUtil;
import com.vvdeng.miner.staff.utils.Util;

public class StaffInfoDialog extends JDialog implements DepTreeVisitable {
	public StaffInfoDialog( StaffManagerFrame owner,  Staff pStaff) {
		super(owner, "员工基本信息", true);
		this.owner=owner;
		this.staff=pStaff;
		setIconImage(SysConfiguration.sysIcon);
		setSize(WIDTH, HEIGHT);
		UIUtil.setCenterPositoin(owner, this);
		setResizable(false);
		setLayout(new GridBagLayout());
		JLabel cardIdLabel = new JLabel("标识卡号：");
		cardIdTxt = new JTextField();
		cardIdTxt.setPreferredSize(new Dimension(130, 20));
		JLabel workIdLabel = new JLabel("工号：");
		workIdTxt = new JTextField();
		workIdTxt.setPreferredSize(new Dimension(130, 20));
		JLabel nameLabel = new JLabel("姓名：");
		nameTxt = new JTextField();
		nameTxt.setPreferredSize(new Dimension(130, 20));
		JLabel sexLabel = new JLabel("性别：");
		ButtonGroup sexGroup = new ButtonGroup();
		maleBtn = new JRadioButton("男");
		maleBtn.setSelected(true);
		femaleBtn = new JRadioButton("女");
		sexGroup.add(maleBtn);
		sexGroup.add(femaleBtn);
		JPanel sexPanel = new JPanel();
		sexPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		sexPanel.add(maleBtn);
		sexPanel.setPreferredSize(new Dimension(130, 20));
		sexPanel.add(femaleBtn);
		JLabel dateLabel = new JLabel("出生年月：");
		datePicker = new JXDatePicker();
		datePicker.setFormats(Util.dateFormat);
		datePicker.setPreferredSize(new Dimension(130, 20));
		JLabel certifyLabel = new JLabel("  证件号码：");
		certifyTxt = new JTextField();
		certifyTxt.setPreferredSize(new Dimension(130, 20));
		JLabel eduLabel = new JLabel("教育程度：");
		eduCombo = new JComboBox(owner.getInfoItemDAO()
				.queryByType(InfoItem.TYPE.EDUCATION).toArray());

		eduCombo.setPreferredSize(new Dimension(130, 20));

		JLabel depLabel = new JLabel("部门：");
		final JPanel depPanel=new JPanel();
		depPanel.setLayout(new FlowLayout(FlowLayout.LEFT,0,0));
		depPanel.setPreferredSize(new Dimension(130, 21));
		depTxt=new JTextField();
		depTxt.setPreferredSize(new Dimension(84, 20));
		depTxt.setEnabled(false);
		depBtn=UIUtil.makeButton("选择", null, new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Point pos=UIUtil.calPosition(depTxt);
				DepartmentTreeDialog departmentTreeDialog=new DepartmentTreeDialog(StaffInfoDialog.this,pos.x, pos.y+depTxt.getHeight());
			//	departmentTreeDialog.setLocation(depPanel.getLocation().x, depPanel.getLocation().y);
				departmentTreeDialog.setVisible(true);
				
			}
		});
		depBtn.setPreferredSize(new Dimension(44, 20));
		depPanel.add(depTxt);
		depPanel.add(depBtn);
	
		JLabel workTypeLabel = new JLabel("工种：");
		workTypeCombo = new JComboBox(owner.getInfoItemDAO()
				.queryByType(InfoItem.TYPE.WORK_TYPE).toArray());
		workTypeCombo.setPreferredSize(new Dimension(130, 20));
		JLabel clazzLabel = new JLabel("班次：");
		clazzCombo = new JComboBox(owner.getInfoItemDAO()
				.queryByType(InfoItem.TYPE.CLAZZ).toArray());
		clazzCombo.setPreferredSize(new Dimension(130, 20));
		JLabel phoneLabel = new JLabel("联系电话：");
		phoneTxt = new JTextField();
		phoneTxt.setPreferredSize(new Dimension(130, 20));

		imageLabel = new JLabel();
		// picLabel.setBorder(BorderFactory.createEtchedBorder());
		imageLabel.setPreferredSize(new Dimension(IMAGE_WIDTH, IMAGE_HEIGHT));
		try {
			makeImageLabel(new FileInputStream(nullImageFilePath));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		openPicBtn = UIUtil.makeButton("打开照片", null, new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser=new JFileChooser(".");
				fileChooser.setAcceptAllFileFilterUsed(false);
				fileChooser.addChoosableFileFilter(new FileFilter() {
					
					@Override
					public String getDescription() {
						// TODO Auto-generated method stub
						return "JPG格式图像文件";
					}
					
					@Override
					public boolean accept(File f) {
						if(f.getName().endsWith("jpg")){
							return true;
						}
						return false;
					}
				});
				int result=fileChooser.showOpenDialog(StaffInfoDialog.this);
				if(result==JFileChooser.APPROVE_OPTION){
					String filePath=fileChooser.getSelectedFile().getAbsolutePath();
					try {
						makeImageLabel(new FileInputStream(filePath));
					} catch (FileNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				
			}
		});
		clearPicBtn = new JButton("清除照片");
		JPanel picAreaPanel = new JPanel();
		picAreaPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 15));
		picAreaPanel.setPreferredSize(new Dimension(IMAGE_WIDTH + 50,
				IMAGE_HEIGHT + 50));
		picAreaPanel.add(imageLabel);
		picAreaPanel.add(openPicBtn);
		picAreaPanel.add(clearPicBtn);

		JLabel addrLabel = new JLabel("工作地点：");
		addrTxt = new JTextField();
		addrTxt.setPreferredSize(new Dimension(180, 20));

		JPanel commandPanel = new JPanel();
		commandPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 0));
		JButton editBtn = UIUtil.makeButton("编辑", null, new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				enableFields();
			}
		});
		JButton saveBtn = UIUtil.makeButton("保存", null, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(!checkFileds()){
					return;
				}
				Integer cardId=Integer.parseInt(cardIdTxt.getText());
				Long workId = Long.parseLong(workIdTxt.getText());
				String name = nameTxt.getText();
				int sex = maleBtn.isSelected() ? 0 : 1;
				String birthDate = Util.formatDate(datePicker.getDate());
				String certificateNo = certifyTxt.getText();
				Long eduLevelId = ((InfoItem) eduCombo.getSelectedItem())
						.getId();
				String eduLevel = ((InfoItem) eduCombo.getSelectedItem())
						.getValue();
				String phone = phoneTxt.getText();
				String address = addrTxt.getText();
				Long professionId = ((InfoItem) workTypeCombo.getSelectedItem())
						.getId();
				String profession = ((InfoItem) workTypeCombo.getSelectedItem())
						.getValue();
				
				String department = depTxt.getText();
				Long clazzId = ((InfoItem) clazzCombo.getSelectedItem())
						.getId();
				String clazz = ((InfoItem) clazzCombo.getSelectedItem())
						.getValue();
				Blob imageBlob=null;

				try {

					BufferedImage bu = new BufferedImage(IMAGE_WIDTH,
							IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);
					ByteArrayOutputStream imageStream = new ByteArrayOutputStream();
					Graphics graphics = bu.getGraphics();
					graphics.drawImage(image, 0, 0, IMAGE_WIDTH, IMAGE_HEIGHT,
							null);
					boolean resultWrite = ImageIO.write(bu, "png", imageStream);
			
					imageStream.close();
		
					imageBlob = Hibernate.createBlob(imageStream.toByteArray());

				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
				if(staff!=null){
					staff.refresh(cardId,workId, name, sex, birthDate, certificateNo, eduLevelId, eduLevel, phone, address, professionId, profession, departmentId,dep1Id,dep2Id,dep3Id, department, clazzId, clazz, imageBlob);
					StaffInfoDialog.this.owner.getStaffDAO().update(staff);
				}else{
				Staff newStaff = new Staff(cardId,workId, name, sex, birthDate,
						certificateNo, eduLevelId, eduLevel, phone, address,
						professionId, profession, departmentId,dep1Id,dep2Id,dep3Id, department,
						clazzId, clazz, imageBlob);
				StaffInfoDialog.this.owner.getStaffDAO().save(newStaff);
				}
				JOptionPane.showMessageDialog(StaffInfoDialog.this, "保存成功！");
				StaffInfoDialog.this.owner.refreshStaffInfoTable();
			}
		});
		JButton delBtn = UIUtil.makeButton("清空", null, new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				clearFields();
				
			}
		});
		JButton exitBtn = UIUtil.makeButton("退出", null, new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				StaffInfoDialog.this.dispose();
			}
		});
		commandPanel.add(editBtn);
		commandPanel.add(saveBtn);
		commandPanel.add(delBtn);
		commandPanel.add(exitBtn);
		add(cardIdLabel,
				new GBC(0, 0).setWeight(0, 100).setAnchor(
						GridBagConstraints.EAST));
		add(cardIdTxt, new GBC(1, 0).setWeight(0, 100).setInsets(0, 0, 0, 10));
		add(workIdLabel,
				new GBC(2, 0).setWeight(0, 100).setAnchor(
						GridBagConstraints.EAST));
		add(workIdTxt, new GBC(3, 0).setWeight(0, 100).setInsets(0, 0, 0, 10));
		add(nameLabel,
				new GBC(0, 1).setWeight(0, 100).setAnchor(
						GridBagConstraints.EAST));
		add(nameTxt, new GBC(1, 1).setWeight(0, 100).setInsets(0, 0, 0, 10));

		add(sexLabel,
				new GBC(2, 1).setWeight(0, 100).setAnchor(
						GridBagConstraints.EAST));
		add(sexPanel, new GBC(3, 1).setWeight(0, 100).setInsets(0, 0, 0, 10));
		add(dateLabel,
				new GBC(0, 2).setWeight(0, 100).setAnchor(
						GridBagConstraints.EAST));
		add(datePicker, new GBC(1, 2).setWeight(0, 100).setInsets(0, 0, 0, 10));

		add(certifyLabel,
				new GBC(2, 2).setWeight(0, 100).setAnchor(
						GridBagConstraints.EAST));
		add(certifyTxt, new GBC(3, 2).setWeight(0, 100).setInsets(0, 0, 0, 10));
		add(eduLabel,
				new GBC(0, 3).setWeight(0, 100).setAnchor(
						GridBagConstraints.EAST));
		add(eduCombo, new GBC(1, 3).setWeight(0, 100).setInsets(0, 0, 0, 10));

		add(depLabel,
				new GBC(2, 3).setWeight(0, 100).setAnchor(
						GridBagConstraints.EAST));
		add(depPanel, new GBC(3, 3).setWeight(0, 100).setInsets(0, 0, 0, 10));
		add(workTypeLabel,
				new GBC(0, 4).setWeight(0, 100).setAnchor(
						GridBagConstraints.EAST));
		add(workTypeCombo,
				new GBC(1, 4).setWeight(0, 100).setInsets(0, 0, 0, 10));

		add(clazzLabel,
				new GBC(2, 4).setWeight(0, 100).setAnchor(
						GridBagConstraints.EAST));
		add(clazzCombo, new GBC(3, 4).setWeight(0, 100).setInsets(0, 0, 0, 10));
		add(phoneLabel,
				new GBC(0, 5).setWeight(0, 100).setAnchor(
						GridBagConstraints.EAST));
		add(phoneTxt, new GBC(1, 5).setWeight(0, 100).setInsets(0, 0, 0, 10));

		add(picAreaPanel,
				new GBC(4, 0, 1, 5).setWeight(100, 100).setAnchor(
						GridBagConstraints.NORTH));

		add(addrLabel, new GBC(2, 5).setAnchor(GridBagConstraints.EAST));
		add(addrTxt, new GBC(3, 5, 3, 1).setWeight(0, 100).setFillH()
				.setAnchor(GridBagConstraints.WEST).setInsets(0, 0, 0, 10));

		add(commandPanel, new GBC(0, 6, 5, 1).setWeightFull().setFillH()
				.setAnchor(GridBagConstraints.CENTER).setInsets(20));
		
		fillFields(staff);
	}

	public void fillFields(Staff staff) {
		if (staff != null) {
			disableFields();
			cardIdTxt.setText(staff.getCardId().toString());
			workIdTxt.setText(staff.getWorkId().toString());
			nameTxt.setText(staff.getName());
			if (staff.getSex() == Staff.MALE) {
				maleBtn.setSelected(true);
			} else {
				femaleBtn.setSelected(true);
			}
			if (staff.getBirthDate() != null && !staff.getBirthDate().isEmpty()) {
				datePicker.setDate(Util.parseDate(staff.getBirthDate()));
			}
			certifyTxt.setText(staff.getCertificateNo());
			eduCombo.setSelectedItem(new InfoItem(staff.getEduLevelId()));
			depTxt.setText(staff.getDepartment());
			departmentId=staff.getDepartmentId();
			workTypeCombo.setSelectedItem(new InfoItem(staff.getProfessionId()));
			clazzCombo.setSelectedItem(new InfoItem(staff.getClazzId()));
			phoneTxt.setText(staff.getPhone());
			addrTxt.setText(staff.getAddress());
			Blob imageBlob = staff.getImage();
			if (imageBlob != null) {
				try {
					makeImageLabel(imageBlob.getBinaryStream());
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		} else {
			clearFields();
		}
	}
	public void disableFields(){
		cardIdTxt.setEnabled(false);
		workIdTxt.setEnabled(false);
		nameTxt.setEnabled(false);
		maleBtn.setEnabled(false);
		femaleBtn.setEnabled(false);
		datePicker.setEnabled(false);
		certifyTxt.setEnabled(false);
		eduCombo.setEnabled(false);
		depBtn.setEnabled(false);
		workTypeCombo.setEnabled(false);
		clazzCombo.setEnabled(false);
		phoneTxt.setEnabled(false);
		addrTxt.setEnabled(false);
		openPicBtn.setEnabled(false);
		clearPicBtn.setEnabled(false);
	}
	public void enableFields(){
		cardIdTxt.setEnabled(true);
		workIdTxt.setEnabled(true);
		nameTxt.setEnabled(true);
		maleBtn.setEnabled(true);
		femaleBtn.setEnabled(true);
		datePicker.setEnabled(true);
		certifyTxt.setEnabled(true);
		eduCombo.setEnabled(true);
		depBtn.setEnabled(true);
		workTypeCombo.setEnabled(true);
		clazzCombo.setEditable(true);
		phoneTxt.setEnabled(true);
		addrTxt.setEnabled(true);
		openPicBtn.setEnabled(true);
		clearPicBtn.setEnabled(true);
	}
	public void clearFields() {
		cardIdTxt.setText("");
		workIdTxt.setText("");
		nameTxt.setText("");
		maleBtn.setSelected(true);
		datePicker.setDate(null);
		certifyTxt.setText("");
		eduCombo.setSelectedIndex(0);
		depTxt.setText("");
		dep1Id=dep2Id=dep3Id=departmentId=0L;
		workTypeCombo.setSelectedIndex(0);
		clazzCombo.setSelectedIndex(0);
		phoneTxt.setText("");
		addrTxt.setText("");
		try {
			makeImageLabel(new FileInputStream(nullImageFilePath));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean checkFileds() {
		if(cardIdTxt.getText().isEmpty()){
			JOptionPane.showMessageDialog(StaffInfoDialog.this, "标识卡号不能为空");
			return false;
		}
		if(!Util.checkDigit(cardIdTxt.getText())){
			JOptionPane.showMessageDialog(StaffInfoDialog.this, "标识卡号格式不正确");
			return false;
		}
		else if(Integer.parseInt(cardIdTxt.getText().trim())>8000){
			JOptionPane.showMessageDialog(StaffInfoDialog.this, "标识卡号范围为（0-8000）");
			return false;
		}
		
		if(workIdTxt.getText().isEmpty()){
			JOptionPane.showMessageDialog(StaffInfoDialog.this, "工号不能为空");
			return false;
		}
		if(!Util.checkDigit(workIdTxt.getText())){
			JOptionPane.showMessageDialog(StaffInfoDialog.this, "工号输入格式不正确");
			return false;
		}
		if(nameTxt.getText().isEmpty()){
			JOptionPane.showMessageDialog(StaffInfoDialog.this, "姓名不能为空");
			return false;
		}
		if(!certifyTxt.getText().isEmpty()&&!Util.checkDigit(certifyTxt.getText())){
			JOptionPane.showMessageDialog(StaffInfoDialog.this, "证件号码输入格式不正确");
			return false;
		}
		if(!phoneTxt.getText().isEmpty()&&!Util.checkDigit(phoneTxt.getText())){
			JOptionPane.showMessageDialog(StaffInfoDialog.this, "电话号码输入格式不正确");
			return false;
		}
		
		Long staffId=owner.getStaffDAO().findByCardId(Integer.parseInt(cardIdTxt.getText()));
		if(staffId!=null&&!staffId.equals(staff.getId())){
			JOptionPane.showMessageDialog(StaffInfoDialog.this, "该标识卡号已存在");
			return false;
		}
		return true;
	}
	public void setDepInfo(DefaultMutableTreeNode selNode){
		if(selNode!=null){
			InfoItem selDep=(InfoItem)selNode.getUserObject();
			departmentId=selDep.getId();
			depTxt.setText(selDep.getValue());
			if(selDep.getLevel()==3){
				dep3Id=selDep.getId();
				DefaultMutableTreeNode parentNode=(DefaultMutableTreeNode)selNode.getParent();
				InfoItem parentDep=(InfoItem)parentNode.getUserObject();
				dep3Id=parentDep.getId();
				dep1Id=parentDep.getParentId();
			}
			else if(selDep.getLevel()==2){
				dep3Id=null;
				dep2Id=selDep.getId();
				dep1Id=selDep.getParentId();
			}
			else if(selDep.getLevel()==1){
				dep3Id=dep2Id=null;
				dep1Id=selDep.getId();
			}
			else{
				dep3Id=dep2Id=dep1Id=null;
			}
		}
	}
	public void makeImageLabel(InputStream is) {
		try {
			image = ImageIO.read(is);
		} catch (IOException e) {

			e.printStackTrace();
		}
		image = image.getScaledInstance(IMAGE_WIDTH, IMAGE_HEIGHT,
				Image.SCALE_SMOOTH);
		imageLabel.setIcon(new ImageIcon(image));
	}

	public static final int IMAGE_HEIGHT = 160;
	public static final int IMAGE_WIDTH = 120;
	public static final int WIDTH = 620;
	public static final int HEIGHT = 340;
	private StaffManagerFrame owner;
	private Staff staff;
	private JTextField cardIdTxt;
	private JTextField workIdTxt;
	private JTextField nameTxt;
	private JRadioButton maleBtn;
	private JRadioButton femaleBtn;
	private JXDatePicker datePicker;
	private JTextField certifyTxt;
	private JComboBox eduCombo;
	public Image image;
	private JLabel imageLabel;
	private JButton openPicBtn;
	private JButton clearPicBtn;
	private JTextField depTxt;
	private Long departmentId;
	private Long dep1Id;
	private Long dep2Id;
	private Long dep3Id;
	private JButton depBtn;
	private JComboBox workTypeCombo;
	private JComboBox clazzCombo;
	private JTextField phoneTxt;
	private JTextField addrTxt;
	public String nullImageFilePath = "images/staff_image_null.jpg";

}
