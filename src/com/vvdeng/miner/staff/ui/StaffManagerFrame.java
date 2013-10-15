package com.vvdeng.miner.staff.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.api.skin.CremeSkin;

import com.vvdeng.miner.staff.comm.SerialComm;
import com.vvdeng.miner.staff.dao.InfoItemDAO;
import com.vvdeng.miner.staff.dao.StaffDAO;
import com.vvdeng.miner.staff.dao.SubDeviceDAO;
import com.vvdeng.miner.staff.entity.InfoItem;
import com.vvdeng.miner.staff.entity.Staff;
import com.vvdeng.miner.staff.entity.SubDevice;
import com.vvdeng.miner.staff.entity.User;
import com.vvdeng.miner.staff.filter.StaffFilter;
import com.vvdeng.miner.staff.interfaces.DepTreeVisitable;
import com.vvdeng.miner.staff.ui.tablemodel.DeviceTableModel;
import com.vvdeng.miner.staff.ui.tablemodel.StaffInfoTableModel;
import com.vvdeng.miner.staff.utils.SysConfiguration;
import com.vvdeng.miner.staff.utils.UIUtil;
import com.vvdeng.miner.staff.utils.Util;

public class StaffManagerFrame extends JFrame implements DepTreeVisitable {

	public StaffManagerFrame() {
		SysConfiguration.init();
		serialComm=new SerialComm(this);
		infoItemDAO = new InfoItemDAO();
		staffDAO = new StaffDAO();
		subDeviceDAO = new SubDeviceDAO();
		setTitle("人员管理系统");
		try {
			setIconImage(ImageIO.read(new File("images/minerlamp.png")));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// setExtendedState(MAXIMIZED_BOTH);
		setSize(UIUtil.getScreenSize(this));
		setLook();
		buildMenubar();
		buildToolbar();
		buildMainPanel();
		buildCommPanel();
		// userLogin();

	}

	private void setLook() {
		UIManager.getDefaults().put("TreeUI", BasicTreeUI.class.getName());
		SubstanceLookAndFeel.setSkin(new CremeSkin());

		JFrame.setDefaultLookAndFeelDecorated(true);
		SwingUtilities.updateComponentTreeUI(this);
	}

	private void buildMenubar() {
		JMenuBar bar = new JMenuBar();
		JMenu fileMenu = new JMenu("文件");
		JMenuItem exitItem = new JMenuItem("退出");
		exitItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				logout();

			}
		});
		fileMenu.add(exitItem);
		JMenu sysMenu = new JMenu("系统");
		JMenuItem sysSettingItem=new JMenuItem("系统设置");
		sysSettingItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				systemSettingShow();
				
			}
		});
		sysMenu.add(sysSettingItem);
		JMenu toolMenu = new JMenu("工具");
		JMenu reportMenu = new JMenu("报表");
		JMenu helpMenu = new JMenu("帮助");
		JMenuItem aboutItem=new JMenuItem("关于");
		aboutItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				about();
				
			}
		});
		helpMenu.add(aboutItem);
		bar.add(fileMenu);
		bar.add(sysMenu);
		bar.add(toolMenu);
		bar.add(reportMenu);
		bar.add(helpMenu);
		setJMenuBar(bar);

	}

	private void buildToolbar() {
		JToolBar bar = new JToolBar();

		
		JButton sys1Button = UIUtil.createToolBarButton("系统设置", new ImageIcon(
				"images/toolbar_syssetting.png"),new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						systemSettingShow();
						
					}
				});
		JButton sysButton = UIUtil.createToolBarButton("退出系统", new ImageIcon(
				"images/toolbar_exit.png"),new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				logout();
				
			}
		});
		JButton refreshButton = UIUtil.createToolBarButton("刷新", new ImageIcon(
				"images/toolbar_exit.png"),new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				serialComm.startReqStationsInfo();
				
			}
		});
		JButton synchButton = UIUtil.createToolBarButton("时钟同步", new ImageIcon(
				"images/clock.png"),new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				serialComm.startSynchronizeTime(Util.getCurrentTimeBytes());
				
			}
		});
		JButton callStaffButton = UIUtil.createToolBarButton("呼叫", new ImageIcon(
				"images/toolbar_exit.png"),new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				byte []staffIdArr=new byte[161];
				staffIdArr[0]=80;
				for(int i=1;i<=80;i++)
				{	
					staffIdArr[2*i-1]=0;
					staffIdArr[2*i]=(byte)i;
				}	

			
				serialComm.callStaff(staffIdArr);
				
			}
		});
		JButton cancelCallStaffButton = UIUtil.createToolBarButton("取消呼叫", new ImageIcon(
				"images/toolbar_exit.png"),new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				serialComm.cancelCallStaff();
				
			}
		});
		JButton retreiveReaderDataStaffButton = UIUtil.createToolBarButton("数据恢复", new ImageIcon(
				"images/toolbar_exit.png"),new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				serialComm.retreiveReaderData();
				
			}
		});
		bar.add(sys1Button);
		bar.add(sysButton);
		bar.add(refreshButton);
		bar.add(synchButton);
		bar.add(callStaffButton);
		bar.add(cancelCallStaffButton);
		bar.add(retreiveReaderDataStaffButton);
		bar.setFloatable(false);
		bar.setOpaque(true);
		add(bar, BorderLayout.NORTH);

	}

	private void systemSettingShow() {
		SysSettingDialog sysSettingDialog = new SysSettingDialog(
				StaffManagerFrame.this);
		sysSettingDialog.setVisible(true);
	}

	private void logout() {
		LogoutDialog logoutDialog=new LogoutDialog(StaffManagerFrame.this);
		logoutDialog.setVisible(true);
	}

	private void about() {
		AboutDialog aboutDialog=new AboutDialog(StaffManagerFrame.this);
		aboutDialog.setVisible(true);
	}

	private void buildMainPanel() {
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridBagLayout());
		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new GridBagLayout());
		leftPanel.setBackground(Color.WHITE);
		JTabbedPane tabbedPanel = new JTabbedPane();
		JPanel devicePanel = new JPanel();
		devicePanel.setLayout(new GridBagLayout());
		JPanel deviceButtonPanel = new JPanel();
		deviceButtonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 2, 2));
		JButton addDeviceBtn = UIUtil.makeButton("添加", null,
				new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						SubDeviceDialog subDeviceDialog = new SubDeviceDialog(
								StaffManagerFrame.this, null);
						subDeviceDialog.setVisible(true);
					}
				});
		addDeviceBtn.setPreferredSize(new Dimension(44, 20));
		JButton editDeviceBtn = UIUtil.makeButton("编辑", null,
				new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						if (deviceTable.getSelectedRowCount() == 0) {
							JOptionPane.showMessageDialog(
									StaffManagerFrame.this, "请选择一个分站");
						} else {
							editDeviceInfo();
						}
					}
				});
		editDeviceBtn.setPreferredSize(new Dimension(44, 20));
		JButton delDeviceBtn = UIUtil.makeButton("删除", null,
				new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						if (deviceTable.getSelectedRowCount() == 0) {
							JOptionPane.showMessageDialog(
									StaffManagerFrame.this, "请选择一个分站");
						} else {
							int choice = JOptionPane.showConfirmDialog(
									StaffManagerFrame.this, "确定要删除所选的分站吗？",
									"确认对话框", JOptionPane.YES_NO_OPTION);
							if (choice == JOptionPane.YES_OPTION) {
								int selRow = deviceTable.getSelectedRow();
								Long selId = (Long) deviceTable.getModel()
										.getValueAt(selRow, 1);
								subDeviceDAO.delete(selId);
								JOptionPane.showMessageDialog(
										StaffManagerFrame.this, "删除成功");
								refreshSubDeviceTable();

							}
						}

					}
				});
		delDeviceBtn.setPreferredSize(new Dimension(44, 20));
		deviceButtonPanel.add(addDeviceBtn);
		deviceButtonPanel.add(editDeviceBtn);
		deviceButtonPanel.add(delDeviceBtn);
		devicePanel.setBackground(Color.WHITE);
		JPanel orgPanel = new JPanel();
		orgPanel.setLayout(new GridBagLayout());
		orgPanel.setBackground(Color.WHITE);
		tabbedPanel.addTab("分站信息", devicePanel);
		tabbedPanel.addTab("组织架构", orgPanel);
		leftPanel.add(tabbedPanel, new GBC(GBC.FULL));

		deviceTableModel = new DeviceTableModel(subDeviceDAO.listAllFields());
		deviceTable = new CenterJTable(deviceTableModel);
		deviceTable.setFillsViewportHeight(true);
		deviceTable.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent e) {
				if (e.getClickCount() >= 2) {
					editDeviceInfo();
				}
			}
		});
		deviceTable.setBackground(Color.WHITE);
		deviceTable.removeColumn(deviceTable.getColumnModel().getColumn(1));

		TableColumnModel deviceColumnModel = deviceTable.getColumnModel();
		deviceColumnModel.getColumn(0).setPreferredWidth(50);
		deviceColumnModel.getColumn(1).setPreferredWidth(100);
		deviceColumnModel.getColumn(2).setPreferredWidth(250);
		devicePanel.add(deviceButtonPanel, new GBC(0, 0).setWeight(100, 0)
				.setFillH());
		devicePanel.add(new JScrollPane(deviceTable),
				new GBC(0, 1).setWeight(100, 100).setFillBoth());

		// 组织架构面板
		final DefaultTreeModel treeModel = new DefaultTreeModel(Util.getDepTree());
		final JTree orgTree = new JTree(treeModel);
		orgTree.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() >= 2) {
					TreePath selPath = orgTree.getSelectionPath();
					DefaultMutableTreeNode selNode = (DefaultMutableTreeNode) selPath
							.getLastPathComponent();
					InfoItem selInfoItem = (InfoItem) selNode.getUserObject();
					StaffFilter filter = new StaffFilter();
					filter.setDepId(selInfoItem.getId());
					filter.setDepLevel(selInfoItem.getLevel());
					queryStaffInfo(filter);
				}

			}
		});
		JPanel orgButtonPanel=new JPanel();
		orgButtonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 2, 2));
		JButton refreshBtn=UIUtil.makeButton("刷新", null, new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				//Util.clearDepTree();
				treeModel.setRoot(Util.getDepTree());
				orgTree.updateUI();
				
			}
		});
		orgButtonPanel.add(refreshBtn);
		orgPanel.add(orgButtonPanel,new GBC(0,0).setWeight(100, 0).setFillH());
		// orgTree.setBackground(Color.WHITE);
		orgPanel.add(new JScrollPane(orgTree), new GBC(0,1).setWeightFull().setFillBoth());

		JPanel rightPanel = new JPanel();
		JTabbedPane rightTabbedPanel = new JTabbedPane();
		// 员工信息面板
		JPanel staffInfoPanel = new JPanel();
		staffInfoPanel.setLayout(new GridBagLayout());
		JPanel staffQueryPanel = new JPanel();
		staffQueryPanel.setLayout(new GridBagLayout());
		staffQueryPanel.setPreferredSize(new Dimension(staffInfoPanel
				.getWidth(), 25));
		JLabel workIdLabel = new JLabel("工号：");
		final JTextField workIdTxt = new JTextField();
		workIdTxt.setPreferredSize(new Dimension(120, 20));
		JLabel nameLabel = new JLabel("姓名：");
		final JTextField nameTxt = new JTextField();
		nameTxt.setPreferredSize(new Dimension(120, 20));
		JPanel depPanel = new JPanel();
		depPanel.setPreferredSize(new Dimension(130, 21));
		depPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		JLabel depLabel = new JLabel("部门：");

		depTxt = new JTextField();
		depTxt.setPreferredSize(new Dimension(84, 20));
		JButton depBtn = UIUtil.makeButton("选择", null, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Point pos = UIUtil.calPosition(depTxt);
				DepartmentTreeDialog departmentTreeDialog = new DepartmentTreeDialog(
						StaffManagerFrame.this, pos.x, pos.y
								+ depTxt.getHeight());
				departmentTreeDialog.setVisible(true);

			}
		});
		depBtn.setPreferredSize(new Dimension(44, 20));
		depPanel.add(depTxt);
		depPanel.add(depBtn);
		JLabel workTypeLabel = new JLabel("工种：");
		final JComboBox workTypeCombo = new JComboBox(infoItemDAO.queryByType(
				InfoItem.TYPE.WORK_TYPE).toArray());
		workTypeCombo.setPreferredSize(new Dimension(120, 20));
		JLabel clazzLabel = new JLabel("班次：");
		final JComboBox clazzCombo = new JComboBox(infoItemDAO.queryByType(
				InfoItem.TYPE.CLAZZ).toArray());
		clazzCombo.setPreferredSize(new Dimension(120, 20));
		staffQueryPanel.setBorder(BorderFactory.createEtchedBorder());
		staffQueryPanel.add(workIdLabel, new GBC(0, 0).setWeight(0, 0)
				.setInsets(0, 30, 0, 0));
		staffQueryPanel.add(workIdTxt,
				new GBC(1, 0).setWeight(0, 0).setInsets(0, 0, 0, 30));
		staffQueryPanel.add(nameLabel, new GBC(2, 0).setWeight(0, 0));
		staffQueryPanel.add(nameTxt,
				new GBC(3, 0).setWeight(0, 0).setInsets(0, 0, 0, 30));
		staffQueryPanel.add(depLabel, new GBC(4, 0).setWeight(0, 0));
		staffQueryPanel.add(depPanel,
				new GBC(5, 0).setWeight(0, 0).setInsets(0, 0, 0, 30));
		staffQueryPanel.add(workTypeLabel, new GBC(6, 0).setWeight(0, 0));
		staffQueryPanel.add(workTypeCombo, new GBC(7, 0).setWeight(0, 0)
				.setInsets(0, 0, 0, 30));
		staffQueryPanel.add(clazzLabel, new GBC(8, 0).setWeight(0, 0));
		staffQueryPanel.add(clazzCombo, new GBC(9, 0).setWeight(0, 0)
				.setInsets(0, 0, 0, 30));
		staffQueryPanel.add(
				UIUtil.makeButton("查询", null, new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						StaffFilter filter = new StaffFilter();
						if (!workIdTxt.getText().trim().isEmpty()
								&& Util.checkDigit(workIdTxt.getText())) {
							filter.setWorkId(new Long(workIdTxt.getText()
									.trim()));
						}
						if (!nameTxt.getText().trim().isEmpty()) {
							filter.setName(nameTxt.getText().trim());
						}
						if (queryDepLevel != null && !queryDepLevel.equals(0)) {
							filter.setDepId(queryDepId);
							filter.setDepLevel(queryDepLevel);
						}
						if (clazzCombo.getSelectedIndex() != 0) {
							filter.setClazzId(((InfoItem) clazzCombo
									.getSelectedItem()).getId());
						}
						if (workTypeCombo.getSelectedIndex() != 0) {
							filter.setWorkTypeId(((InfoItem) workTypeCombo
									.getSelectedItem()).getId());
						}
						queryStaffInfo(filter);
					}

				}),
				new GBC(10, 0).setWeight(100, 0).setAnchor(
						GridBagConstraints.CENTER));
		JPanel staffEditPanel = new JPanel();
		staffEditPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		staffEditPanel.add(UIUtil.makeButton("添加", null, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				StaffInfoDialog staffInfoDialog = new StaffInfoDialog(
						StaffManagerFrame.this, null);
				staffInfoDialog.setVisible(true);

			}
		}));
		staffEditPanel.add(UIUtil.makeButton("编辑", null, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (staffInfoTable.getSelectedRowCount() == 0) {
					JOptionPane.showMessageDialog(StaffManagerFrame.this,
							"请选定待编辑的人员信息");
				} else {
					editStaffInfo();
				}

			}
		}));
		staffEditPanel.add(UIUtil.makeButton("删除", null, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (staffInfoTable.getSelectedRowCount() == 0) {
					JOptionPane.showMessageDialog(StaffManagerFrame.this,
							"请选定待编辑的人员信息");
				} else {
					int choice = JOptionPane.showConfirmDialog(
							StaffManagerFrame.this, "选定要删除选定的人员信息吗", "",
							JOptionPane.YES_NO_OPTION);
					if (choice == JOptionPane.YES_OPTION) {
						delStaffInfo();
						refreshStaffInfoTable();
					}
				}

			}
		}));
		staffInfoTableModel = new StaffInfoTableModel(
				staffDAO.listStaffFields());
		staffInfoTable = new CenterJTable(staffInfoTableModel);
		staffInfoTable.setFillsViewportHeight(true);
		TableColumnModel columnModel = staffInfoTable.getColumnModel();
		TableColumn idColumn = columnModel.getColumn(1);
		staffInfoTable.removeColumn(idColumn);
		staffInfoTable.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent e) {
				if (e.getClickCount() >= 2) {
					editStaffInfo();
				}
			}
		});
		staffInfoPanel.add(staffQueryPanel, new GBC(0, 0).setWeight(100, 30)
				.setFillBoth().setInsets(10, 0, 10, 0));

		staffInfoPanel.add(new JScrollPane(staffInfoTable), new GBC(0, 1)
				.setWeightFull().setFillBoth());
		staffInfoPanel.add(staffEditPanel, new GBC(0, 2).setWeight(100, 0)
				.setFillH());
		rightTabbedPanel.addTab("员工基本信息", staffInfoPanel);

		JSplitPane splitPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				leftPanel, rightTabbedPanel);
		splitPanel.setDividerLocation(300);
		splitPanel.setDividerSize(5);

		JPanel commPanel = new JPanel();
		commPanel.setPreferredSize(new Dimension(mainPanel.getWidth(), 200));
		commPanel.setBorder(BorderFactory.createEtchedBorder());
		commPanel.setLayout(new BorderLayout());
		JTextArea commInfoTextArea = new JTextArea();

		commPanel.add(commInfoTextArea, BorderLayout.CENTER);
		mainPanel.add(
				splitPanel,
				new GBC(0, 0).setWeight(100, 100).setFill(
						GridBagConstraints.BOTH));
		mainPanel.add(
				commPanel,
				new GBC(0, 1).setWeight(100, 0).setFill(
						GridBagConstraints.HORIZONTAL));
		add(mainPanel, BorderLayout.CENTER);
	}

	private void editDeviceInfo() {
		int selRow = deviceTable.getSelectedRow();
		Long id = (Long) deviceTable.getModel().getValueAt(selRow, 1);
		SubDevice selSubDevice = subDeviceDAO.fingById(id);
		SubDeviceDialog subDeviceDialog = new SubDeviceDialog(
				StaffManagerFrame.this, selSubDevice);
		subDeviceDialog.setVisible(true);
	}

	private void editStaffInfo() {
		int selRow = staffInfoTable.getSelectedRow();
		Long id = (Long) staffInfoTable.getModel().getValueAt(selRow, 1);

		Staff selStaff = staffDAO.finbById(id);

		StaffInfoDialog staffInfoDialog = new StaffInfoDialog(
				StaffManagerFrame.this, selStaff);
		staffInfoDialog.setVisible(true);

	}

	private void delStaffInfo() {
		Long id = (Long) staffInfoTable.getModel().getValueAt(
				staffInfoTable.getSelectedRow(), 1);
		staffDAO.deleteStaff(id);

	}

	private void queryStaffInfo(StaffFilter filter) {
		staffInfoTableModel.setModel(staffDAO.queryStaffInfo(filter));
		staffInfoTableModel.fireTableDataChanged();
	}

	public void refreshStaffInfoTable() {
		staffInfoTableModel.setModel(staffDAO.listStaffFields());
		staffInfoTableModel.fireTableDataChanged();

	}

	public void refreshSubDeviceTable() {
		deviceTableModel.setModel(subDeviceDAO.listAllFields());
		deviceTableModel.fireTableDataChanged();

	}

	public void setDepInfo(DefaultMutableTreeNode selNode) {
		InfoItem selItem = (InfoItem) selNode.getUserObject();
		queryDep = selItem.getValue();
		queryDepId = selItem.getId();
		queryDepLevel = selItem.getLevel();
		depTxt.setText(queryDep);
	}

	private void buildCommPanel() {

	}

	public void userLogin() {
		LoginDialog loginDialog = new LoginDialog(StaffManagerFrame.this);
		loginDialog.setVisible(true);
	}
    public void startCommIfPossible(){
    	
    	new Timer(5*1000,new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if(SysConfiguration.commStarted){
					
				}
			}
		});
    }
	public User getCurrentUser() {
		return currentUser;
	}

	public void setCurrentUser(User currentUser) {
		this.currentUser = currentUser;
	}

	public InfoItemDAO getInfoItemDAO() {
		return infoItemDAO;
	}

	public void setInfoItemDAO(InfoItemDAO infoItemDAO) {
		this.infoItemDAO = infoItemDAO;
	}

	public StaffDAO getStaffDAO() {
		return staffDAO;
	}

	public void setStaffDAO(StaffDAO staffDAO) {
		this.staffDAO = staffDAO;
	}
	class BackgroundActivity extends SwingWorker<Void, Integer> {

		public BackgroundActivity() {

		}

		protected Void doInBackground() throws Exception {

			while (true) {
				synchronized (this) {
					wait();
				
				}
			}

		}

		protected void process(List<Integer> list) {
			for (Integer value : list) {
				switch (value.intValue()) {
				case 0:
				
					break;
				case 1:
					
					break;
				default:
					break;
				}
			}

		}

		protected void done() {

		}

	}
	private User currentUser;
	private InfoItemDAO infoItemDAO;
	private SubDeviceDAO subDeviceDAO;
	private StaffDAO staffDAO;
	private DeviceTableModel deviceTableModel;
	private JTable deviceTable;
	private StaffInfoTableModel staffInfoTableModel;
	private JTable staffInfoTable;
	private Long queryDepId;
	private Integer queryDepLevel;
	private String queryDep;
	private JTextField depTxt;
	private SerialComm serialComm;
}
