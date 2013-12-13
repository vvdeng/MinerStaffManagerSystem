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
import java.util.ArrayList;
import java.util.Date;
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

import com.vvdeng.miner.constant.PositionEvent;
import com.vvdeng.miner.constant.StationCommState;
import com.vvdeng.miner.staff.comm.CmdObject;
import com.vvdeng.miner.staff.comm.SerialComm;
import com.vvdeng.miner.staff.dao.InfoItemDAO;
import com.vvdeng.miner.staff.dao.PositionLogDAO;
import com.vvdeng.miner.staff.dao.SYSDAO;
import com.vvdeng.miner.staff.dao.StaffDAO;
import com.vvdeng.miner.staff.dao.StaffExtraDAO;
import com.vvdeng.miner.staff.dao.StationLogDAO;
import com.vvdeng.miner.staff.dao.SubDeviceDAO;
import com.vvdeng.miner.staff.entity.InfoItem;
import com.vvdeng.miner.staff.entity.PositionLog;
import com.vvdeng.miner.staff.entity.Staff;
import com.vvdeng.miner.staff.entity.StationLog;
import com.vvdeng.miner.staff.entity.SubDevice;
import com.vvdeng.miner.staff.entity.User;
import com.vvdeng.miner.staff.filter.PositionLogFilter;
import com.vvdeng.miner.staff.filter.StaffFilter;
import com.vvdeng.miner.staff.interfaces.DepTreeVisitable;
import com.vvdeng.miner.staff.ui.Render.ButtonRenderer;
import com.vvdeng.miner.staff.ui.model.DeviceTableModel;
import com.vvdeng.miner.staff.ui.model.RealtimeReaderTableModel;
import com.vvdeng.miner.staff.ui.model.StaffInfoTableModel;
import com.vvdeng.miner.staff.utils.DAOUtil;
import com.vvdeng.miner.staff.utils.GlobalData;
import com.vvdeng.miner.staff.utils.SysConfiguration;
import com.vvdeng.miner.staff.utils.UIUtil;
import com.vvdeng.miner.staff.utils.Util;

public class StaffManagerFrame extends JFrame implements DepTreeVisitable {

	public StaffManagerFrame() {
		SysConfiguration.init();
		serialComm = new SerialComm(this);
		infoItemDAO = new InfoItemDAO();
		staffDAO = new StaffDAO();
		subDeviceDAO = new SubDeviceDAO();
		positionLogDAO = new PositionLogDAO();
		stationLogDAO = new StationLogDAO();
		staffExtraDAO = new StaffExtraDAO();
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
		JMenuItem sysSettingItem = new JMenuItem("系统设置");
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
		JMenuItem aboutItem = new JMenuItem("关于");
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
				"images/6-1.png"), new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				systemSettingShow();

			}
		});
		JButton sysButton = UIUtil.createToolBarButton("退出系统", new ImageIcon(
				"images/2-1.png"), new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				logout();

			}
		});
		JButton refreshButton = UIUtil.createToolBarButton("刷新", new ImageIcon(
				"images/3-1.png"), new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				serialComm.startReqStationsInfo();

			}
		});
		JButton synchButton = UIUtil.createToolBarButton("时钟同步", new ImageIcon(
				"images/7-1.png"), new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// serialComm.startSynchronizeTime(Util.getCurrentTimeBytes());
				CmdObject cmdObject = new CmdObject();
				cmdObject.setCmdType(CmdObject.CMD_SYN_TIME);
				GlobalData.cmdQueue.add(cmdObject);

			}
		});
		JButton callStaffButton = UIUtil.createToolBarButton("呼叫",
				new ImageIcon("images/1-1.png"), new ActionListener() {
					// 最多呼叫12个人
					@Override
					public void actionPerformed(ActionEvent e) {
						CallStaffDialog callStaffDialog = new CallStaffDialog(
								StaffManagerFrame.this);
						callStaffDialog.setVisible(true);

					}
				});
		JButton cancelCallStaffButton = UIUtil.createToolBarButton("取消呼叫",
				new ImageIcon("images/1-0.png"), new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						CmdObject cmdObject = new CmdObject();
						cmdObject.setCmdType(CmdObject.CMD_CALL_STAFF);

						cmdObject.setExtraData(new byte[] { 0 });
						GlobalData.cmdQueue.add(cmdObject);
						// serialComm.cancelCallStaff();

					}
				});
		JButton retreiveReaderDataStaffButton = UIUtil.createToolBarButton(
				"数据重置", new ImageIcon("images/4-1.png"), new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {

						// serialComm.retreiveReaderData();
						CmdObject cmdObject = new CmdObject();
						cmdObject.setCmdType(CmdObject.CMD_CLEAR_ALL_DATA);
						GlobalData.cmdQueue.add(cmdObject);

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
		LogoutDialog logoutDialog = new LogoutDialog(StaffManagerFrame.this);
		logoutDialog.setVisible(true);
	}

	private void about() {
		AboutDialog aboutDialog = new AboutDialog(StaffManagerFrame.this);
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
								SubDevice subDevice = subDeviceDAO
										.fingById(selId);
								if (subDevice != null) {
									subDeviceDAO.delete(subDevice);
									GlobalData.subDeviceMap.remove(subDevice
											.getDeviceId());
								}
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

		deviceTableModel = new DeviceTableModel(new ArrayList(
				GlobalData.subDeviceMap.values()));
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
		final DefaultTreeModel treeModel = new DefaultTreeModel(
				Util.getDepTree());
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
		JPanel orgButtonPanel = new JPanel();
		orgButtonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 2, 2));
		JButton refreshBtn = UIUtil.makeButton("刷新", null,
				new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						// Util.clearDepTree();
						treeModel.setRoot(Util.getDepTree());
						orgTree.updateUI();

					}
				});
		orgButtonPanel.add(refreshBtn);
		orgPanel.add(orgButtonPanel, new GBC(0, 0).setWeight(100, 0).setFillH());
		// orgTree.setBackground(Color.WHITE);
		orgPanel.add(new JScrollPane(orgTree), new GBC(0, 1).setWeightFull()
				.setFillBoth());

		JPanel rightPanel = new JPanel();
		JTabbedPane rightTabbedPanel = new JTabbedPane();
		// 实时位置面板
		JPanel realtimeReaderPanel = new JPanel();
		realtimeReaderPanel.setLayout(new GridBagLayout());
		realtimeReaderTableModel = new RealtimeReaderTableModel(
				positionLogDAO.listRealtimeReaders());
		realTimeTable = new CenterJTable(realtimeReaderTableModel);
		TableColumn detailColumn = realTimeTable.getColumnModel().getColumn(5);
		detailColumn.setCellRenderer(new ButtonRenderer());
		// JButton moreBtn=new JButton("查看");
		// TableCellEditor operaCelllEditor=new DefaultCellEditor(checkBox);

		realTimeTable.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent e) {
				System.out.println(" realTimeTable selected Column="
						+ realTimeTable.getSelectedColumn());
				if (e.getClickCount() >= 1
						&& realTimeTable.getSelectedColumn() == 5) {

					int selRow = realTimeTable.getSelectedRow();
					Integer selReaderId = (Integer) realTimeTable.getModel()
							.getValueAt(selRow, 1);
					PositionLogFilter filter = new PositionLogFilter();
					filter.setReaderId(selReaderId);

					filter.setExist(PositionLog.EXIST);
					filter.setEvent(PositionEvent.NORMAL.getId());
					ReaderRealtimeDetailDialog readerRealtimeDetailDialog = new ReaderRealtimeDetailDialog(
							StaffManagerFrame.this, filter);
					readerRealtimeDetailDialog.setVisible(true);

				}
			}
		});
		realTimeTable.setFillsViewportHeight(true);

		realtimeReaderPanel.add(new JScrollPane(realTimeTable), new GBC(0, 0)
				.setWeightFull().setFillBoth());

		// 活动轨迹面板
		// JPanel posTrackPanel = new JPanel();
		// posTrackPanel.setLayout(new GridBagLayout());
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
		TableColumn operaColumn = columnModel.getColumn(11);
		operaColumn.setCellRenderer(new ButtonRenderer());
		// JButton moreBtn=new JButton("查看");
		// TableCellEditor operaCelllEditor=new DefaultCellEditor(checkBox);
		staffInfoTable.removeColumn(idColumn);
		staffInfoTable.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent e) {
				System.out.println(" staffInfoTable selected Column="
						+ staffInfoTable.getSelectedColumn());
				if (e.getClickCount() >= 1
						&& staffInfoTable.getSelectedColumn() == 10) {

					int selRow = staffInfoTable.getSelectedRow();
					Integer selCardId = (Integer) staffInfoTable.getModel()
							.getValueAt(selRow, 2);
					PositionLogFilter filter = new PositionLogFilter();
					filter.setCardId(selCardId);
					filter.setStartTime(Util.getCurrentDateLong());
					TrackDialog trackDialog = new TrackDialog(
							StaffManagerFrame.this, filter);
					trackDialog.setVisible(true);

				} else if (e.getClickCount() >= 2
						&& staffInfoTable.getSelectedColumn() >= 0) {

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

		rightTabbedPanel.addTab("实时位置信息", realtimeReaderPanel);
		// rightTabbedPanel.addTab("活动轨迹信息", posTrackPanel);
		rightTabbedPanel.addTab("员工基本信息", staffInfoPanel);

		JSplitPane splitPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				leftPanel, rightTabbedPanel);
		splitPanel.setDividerLocation(300);
		splitPanel.setDividerSize(5);

		JPanel commPanel = new JPanel();
		
		commPanel.setPreferredSize(new Dimension(mainPanel.getWidth(), 200));
		commPanel.setBorder(BorderFactory.createEtchedBorder());
		commPanel.setLayout(new GridBagLayout());
		commInfoTextArea = new JTextArea();
		
		commInfoTextArea.setBackground(new Color(235, 234, 225));
		commInfoTextArea.setEditable(false);
		commInfoTextArea.setLineWrap(true);
		JScrollPane commInfoScollPanel=new JScrollPane(commInfoTextArea);
		commInfoScollPanel.setAutoscrolls(false);
		JPanel staPanel = new JPanel();
		staPanel.setLayout(new GridBagLayout());
		staTotalLabel = new JLabel();
		staTodayLabel = new JLabel();
		staTotalLabel.setHorizontalAlignment(JLabel.CENTER);
		staTotalLabel.setVerticalAlignment(JLabel.BOTTOM);

		staTodayLabel.setHorizontalAlignment(JLabel.CENTER);
		staTodayLabel.setVerticalAlignment(JLabel.TOP);

		staPanel.add(staTotalLabel,
				new GBC(0, 0).setFillBoth().setWeight(100, 100));
		staPanel.add(staTodayLabel,
				new GBC(0, 1).setFillBoth().setWeight(100, 100));
		commPanel.add(commInfoScollPanel,
				new GBC(0, 0).setFillV().setWeight(70, 100).setFillBoth());
		commPanel.add(staPanel, new GBC(1, 0).setFillV().setWeight(30, 100)
				.setFillBoth());
		mainPanel.add(
				splitPanel,
				new GBC(0, 0).setWeight(100, 100).setFill(
						GridBagConstraints.BOTH));
		mainPanel.add(
				commPanel,
				new GBC(0, 1).setWeight(100, 0).setFill(
						GridBagConstraints.HORIZONTAL));
		add(mainPanel, BorderLayout.CENTER);
		refreshStaffInMinerStaLabel();

		backgroundActivity = new BackgroundActivity();
		backgroundActivity.execute();
		GlobalData.swingTimer = new Timer(SysConfiguration.timeInteval,
				new TimerActoin());
		GlobalData.swingTimer.start();

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
		staffExtraDAO.delete(id);

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
		deviceTableModel.setModel(new ArrayList(GlobalData.subDeviceMap
				.values()));
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

	public void startCommIfPossible() {

		new Timer(5 * 1000, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (SysConfiguration.commStarted) {

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

	public SubDeviceDAO getSubDeviceDAO() {
		return subDeviceDAO;
	}

	public void setSubDeviceDAO(SubDeviceDAO subDeviceDAO) {
		this.subDeviceDAO = subDeviceDAO;
	}

	public StaffDAO getStaffDAO() {
		return staffDAO;
	}

	public void setStaffDAO(StaffDAO staffDAO) {
		this.staffDAO = staffDAO;
	}

	public PositionLogDAO getPositionLogDAO() {
		return positionLogDAO;
	}

	public void setPositionLogDAO(PositionLogDAO positionLogDAO) {
		this.positionLogDAO = positionLogDAO;
	}

	public StaffExtraDAO getStaffExtraDAO() {
		return staffExtraDAO;
	}

	public void setStaffExtraDAO(StaffExtraDAO staffExtraDAO) {
		this.staffExtraDAO = staffExtraDAO;
	}

	public SerialComm getSerialComm() {
		return serialComm;
	}

	public void setSerialComm(SerialComm serialComm) {
		this.serialComm = serialComm;
	}

	public void notifyActivity() {
		synchronized (backgroundActivity) {
			backgroundActivity.notify();
		}
	}

	public void refreshStaffInMinerStaLabel() {
		DAOUtil.setStaffInMinerSta();
		staTotalLabel.setText("当前井下人数：" + GlobalData.staffInMinerTotalCount);
		staTodayLabel.setText("今日下井人数：" + GlobalData.staffInMinerTodayCount);
	}

	class TimerActoin implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println("current time=" + new Date());
			if (serialComm.commState != serialComm.STATE_NOTHING) {
				System.out.println("TimerAction commState="
						+ serialComm.commState);

				SubDevice subDevice = GlobalData.subDeviceMap
						.get(GlobalData.currentStationIndex);
				
				if (subDevice != null) {
					System.out.println("[TimeAction] 分站"
							+ subDevice.getDeviceId() + " state="+subDevice.getState());
					if (subDevice.getState().equals(StationCommState.EXCEP.getId())) {
						serialComm.commState = serialComm.STATE_NOTHING;
						StationLog stationLog = new StationLog();
						stationLog.setArriveTime(new Date().getTime());
						stationLog.setSubDeviceId(subDevice.getDeviceId());
						stationLog.setEvent(StationCommState.EXCEP.getId());

						stationLogDAO.save(stationLog);
						GlobalData.currentBgActivityState = BackgroundActivity.STATE_ONE_REFRESHED;
						GlobalData.msgQueue.add("分站"
								+ subDevice.getDeviceId() + StationCommState.values()[subDevice.getState()].getDesc());
						
						notifyActivity();
					}
				}

				else if (GlobalData.commStateNotNothingCount++ <= 5) {
					return;
				} else {
					GlobalData.commStateNotNothingCount = 0;
					serialComm.commState = serialComm.STATE_NOTHING;
				}
			}
			// 没有下发命令时巡检数据
			if (GlobalData.cmdQueue.isEmpty()) {
				System.out.println(" query station state");
				serialComm.reqNextStationInfo();

			} else {
				System.out.println("execute cmd");

				CmdObject cmdObject = GlobalData.cmdQueue.poll();
				switch (cmdObject.getCmdType()) {
				case CmdObject.CMD_SYN_TIME:
					GlobalData.msgQueue.add("时钟同步");
					serialComm.startSynchronizeTime(Util.getCurrentTimeBytes());
					break;
				case CmdObject.CMD_CLEAR_ALL_DATA:
					serialComm.commState = SerialComm.STATE_CLEARING_ALLDATA;
					SYSDAO.getSYSDAO().clearAllData();
					DAOUtil.initData();
					// trick 补救分站状态刷新
					deviceTableModel.setModel(new ArrayList(
							GlobalData.subDeviceMap.values()));
					deviceTableModel.fireTableDataChanged();
					GlobalData.msgQueue.add("数据重置完毕");
					// JOptionPane.showMessageDialog(null, "数据重置完毕");
					serialComm.commState = SerialComm.STATE_NOTHING;
					GlobalData.currentStationIndex = 0;
					GlobalData.currentBgActivityState = StaffManagerFrame.BackgroundActivity.STATE_TOTAL_REFRESHED;
					notifyActivity();
					break;
				case CmdObject.CMD_CALL_STAFF:
					GlobalData.msgQueue.add("人员呼叫/取消呼叫");
					serialComm.callStaff(cmdObject.getExtraData());
					// case CmdObject.CMD_CANCEL_CALL_STAFF:
					// serialComm.cancelCallStaff();
				default:
					break;
				}
			}

		}

	}

	public class BackgroundActivity extends SwingWorker<Void, Integer> {
		public static final int STATE_TOTAL_REFRESHED = 1;
		public static final int STATE_ONE_REFRESHED = 2;
		public static final int STATE_CALL_STAFF = 3;
		public static final int STATE_CALL_ALL_STAFF = 4;
		public static final int STATE_CANCELCALL_STAFF = 5;
		public BackgroundActivity() {

		}

		protected Void doInBackground() throws Exception {

			while (true) {
				synchronized (this) {
					wait();
					switch (GlobalData.currentBgActivityState) {
					case BackgroundActivity.STATE_TOTAL_REFRESHED:
						GlobalData.posLogList
								.addAll(GlobalData.normalPosLogList);
						GlobalData.posLogList
								.addAll(GlobalData.unusualPosLogList);
						GlobalData.normalPosLogList.clear();
						GlobalData.unusualPosLogList.clear();
						DAOUtil.positionLogDAO.saveList(GlobalData.posLogList);
						DAOUtil.staffExtraDAO
								.updateAll(GlobalData.entryOrExitStaffExtraList);
						publish(BackgroundActivity.STATE_TOTAL_REFRESHED);
						break;
					case BackgroundActivity.STATE_ONE_REFRESHED:
						publish(BackgroundActivity.STATE_ONE_REFRESHED);
						break;
					default:
						break;
					}

				}
			}

		}

		protected void process(List<Integer> list) {
			for (Integer value : list) {
				System.out.println(" Activity process value=" + value);
				switch (value.intValue()) {
				case BackgroundActivity.STATE_TOTAL_REFRESHED:
					System.out
							.println("[SwingWorker endReqStationsInfo][cardNewestPosMap.size=]"
									+ GlobalData.cardNewestPosMap.size()
									+ " [unusualPosLogList.size]="
									+ GlobalData.unusualPosLogList.size());
					realtimeReaderTableModel.setModel(positionLogDAO
							.listRealtimeReaders());
					realtimeReaderTableModel.fireTableDataChanged();
					deviceTableModel.fireTableDataChanged();
					refreshStaffInMinerStaLabel();
					// 可考虑切换至对应显示面板时才进行如下更新
					staffInfoTableModel.fireTableDataChanged();
					break;
				case BackgroundActivity.STATE_ONE_REFRESHED:
			/*		System.out.println("部分更新");
					SubDevice subDevice = GlobalData.subDeviceMap
							.get(GlobalData.currentStationIndex);
					//current值已经不对 变为下一个
					System.out.println("BackgroundActivity currentStationIndex="+GlobalData.currentStationIndex+" device exist?"+(subDevice!=null));
					if (subDevice != null) {
						System.out.println("分站"
								+ subDevice.getDeviceId()
								+ StationCommState.values()[subDevice
										.getState()].getDesc());
						commInfoTextArea.append("分站"
								+ subDevice.getDeviceId()
								+ StationCommState.values()[subDevice
										.getState()].getDesc() + "\n");
					}
					break;
			*/
				default:
					break;
				}
				while(!GlobalData.msgQueue.isEmpty()){
					commInfoTextArea.append("  ");
					commInfoTextArea.append(GlobalData.msgQueue.poll());
					commInfoTextArea.append("\n");
				}
			}

		}

		protected void done() {

		}

	}

	class BackgroundActivity2 extends SwingWorker<Void, Integer> {

		public BackgroundActivity2() {

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
	private PositionLogDAO positionLogDAO;
	private StationLogDAO stationLogDAO;
	private StaffExtraDAO staffExtraDAO;
	private DeviceTableModel deviceTableModel;
	private JTable deviceTable;
	private StaffInfoTableModel staffInfoTableModel;
	private JTable staffInfoTable;
	private RealtimeReaderTableModel realtimeReaderTableModel;
	private JTable realTimeTable;
	private Long queryDepId;
	private Integer queryDepLevel;
	private String queryDep;
	private JTextField depTxt;
	private SerialComm serialComm;
	private BackgroundActivity backgroundActivity;
	private JLabel staTotalLabel;
	private JLabel staTodayLabel;
	//TODO 清空可显示范围外的 commInfoTextArea的内容
	private JTextArea commInfoTextArea;

}
