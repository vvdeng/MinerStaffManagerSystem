package com.vvdeng.miner.staff.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.TableColumnModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.jdesktop.swingx.StackLayout;

import com.vvdeng.miner.staff.comm.SerialComm;
import com.vvdeng.miner.staff.dao.InfoItemDAO;
import com.vvdeng.miner.staff.dao.StaffDAO;
import com.vvdeng.miner.staff.dao.UserDAO;
import com.vvdeng.miner.staff.entity.InfoItem;
import com.vvdeng.miner.staff.entity.User;
import com.vvdeng.miner.staff.ui.model.InfoItemTableModel;
import com.vvdeng.miner.staff.utils.HibernateUtil;
import com.vvdeng.miner.staff.utils.PropertiesUtil;
import com.vvdeng.miner.staff.utils.SysConfiguration;
import com.vvdeng.miner.staff.utils.UIUtil;
import com.vvdeng.miner.staff.utils.Util;

public class SysSettingDialog extends JDialog {
	public SysSettingDialog(StaffManagerFrame owner) {
		this(owner, 0);
	}

	public SysSettingDialog(StaffManagerFrame owner, int selectedPanelNo) {
		super(owner, "设置", true);
		setIconImage(SysConfiguration.sysIcon);
		this.owner = owner;
		infoItemDAO = new InfoItemDAO();
		setLayout(new GridBagLayout());
		JLabel descLabel = new JLabel(new ImageIcon(
				"resources/settingTitle.png"));
		add(descLabel,
				new GBC(0, 0).setWeight(100, 0).setFill(
						GridBagConstraints.HORIZONTAL));
		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new GridBagLayout());
		List nodeList = new ArrayList();
		DefaultMutableTreeNode commNode = new DefaultMutableTreeNode("串口通信设置");
		nodeList.add(commNode);
		DefaultMutableTreeNode dbNode = new DefaultMutableTreeNode("数据库设置");
		nodeList.add(dbNode);

		DefaultMutableTreeNode userNode = new DefaultMutableTreeNode("用户设置");
		nodeList.add(userNode);
		DefaultMutableTreeNode regionNode = new DefaultMutableTreeNode("区域设置");
		nodeList.add(regionNode);
		DefaultMutableTreeNode workTypeNode = new DefaultMutableTreeNode("工种设置");
		nodeList.add(workTypeNode);
		DefaultMutableTreeNode educationNode = new DefaultMutableTreeNode(
				"教育程度设置");
		nodeList.add(educationNode);
		DefaultMutableTreeNode clazzNode = new DefaultMutableTreeNode("班次设置");
		nodeList.add(clazzNode);
		DefaultMutableTreeNode subDeviceTypeNode = new DefaultMutableTreeNode(
				"分站类型设置");
		nodeList.add(subDeviceTypeNode);
		DefaultMutableTreeNode readerTypeNode = new DefaultMutableTreeNode(
				"读卡器类型设置");
		nodeList.add(readerTypeNode);
		DefaultMutableTreeNode depNode = new DefaultMutableTreeNode("部门设置");
		nodeList.add(depNode);
		DefaultMutableTreeNode timeoutNode = new DefaultMutableTreeNode("超时设置");
		nodeList.add(timeoutNode);
		final JTree functionTree = new JTree(nodeList.toArray());
		functionTree.setRowHeight(30);

		// functionTree.setBackground(Color.white);
		int mode = TreeSelectionModel.SINGLE_TREE_SELECTION;
		functionTree.getSelectionModel().setSelectionMode(mode);

		functionTree.setPreferredSize(new Dimension(150, 400));
		leftPanel.add(
				functionTree,
				new GBC(0, 0).setWeight(100, 100).setFill(
						GridBagConstraints.BOTH));
		JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new StackLayout());
		final StackPanel commPanel = new CommPanel();
		commPanel.setVisible(true);
		final StackPanel dbPanel = new Dbanel();

		final StackPanel userPanel = new UserPanel();

		final StackPanel usePanel = new UsePanel();

		final StackPanel regionPanel = new InfoItemPanel(InfoItem.TYPE.REGION);

		final StackPanel workTypePanel = new InfoItemPanel(
				InfoItem.TYPE.WORK_TYPE);

		final StackPanel educationPanel = new InfoItemPanel(
				InfoItem.TYPE.EDUCATION);

		final StackPanel clazzPanel = new InfoItemPanel(InfoItem.TYPE.CLAZZ);

		final StackPanel subDeviceTypePanel = new InfoItemPanel(
				InfoItem.TYPE.SUBDEVICE_TYPE);

		final StackPanel readerTypePanel = new InfoItemPanel(
				InfoItem.TYPE.READER_TYPE);
		final StackPanel depPanel = new InfoItemTreePanel(
				InfoItem.TYPE.DEPARTMENET);
		final StackPanel timeoutPanel = new TimeoutPanel();

		rightPanel.add(commPanel, StackLayout.TOP);
		rightPanel.add(dbPanel, StackLayout.TOP);

		rightPanel.add(userPanel, StackLayout.TOP);
		rightPanel.add(usePanel, StackLayout.TOP);
		rightPanel.add(regionPanel, StackLayout.TOP);
		rightPanel.add(workTypePanel, StackLayout.TOP);
		rightPanel.add(educationPanel, StackLayout.TOP);
		rightPanel.add(clazzPanel, StackLayout.TOP);
		rightPanel.add(subDeviceTypePanel, StackLayout.TOP);
		rightPanel.add(readerTypePanel, StackLayout.TOP);
		rightPanel.add(depPanel, StackLayout.TOP);
		rightPanel.add(timeoutPanel, StackLayout.TOP);

		functionTree.addTreeSelectionListener(new TreeSelectionListener() {

			@Override
			public void valueChanged(TreeSelectionEvent e) {
				TreePath path = functionTree.getSelectionPath();
				if (path == null) {
					return;
				}
				DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) path
						.getLastPathComponent();
				String desc = (selectedNode.toString());
				if (desc.equals("串口通信设置")) {
					commPanel.showMe();

				} else if (desc.equals("数据库设置")) {
					dbPanel.showMe();

				} else if (desc.equals("用户设置")) {
					userPanel.showMe();

				} else if (desc.equals("区域设置")) {
					regionPanel.showMe();
				} else if (desc.equals("工种设置")) {
					workTypePanel.showMe();
				} else if (desc.equals("教育程度设置")) {
					educationPanel.showMe();
				} else if (desc.equals("班次设置")) {
					clazzPanel.showMe();
				} else if (desc.equals("分站类型设置")) {
					subDeviceTypePanel.showMe();
				} else if (desc.equals("读卡器类型设置")) {
					readerTypePanel.showMe();
				} else if (desc.equals("部门设置")) {
					depPanel.showMe();
				} else if (desc.equals("超时设置")) {
					timeoutPanel.showMe();

				}

			}
		});
		functionTree.setSelectionRow(selectedPanelNo);
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				false, leftPanel, rightPanel);
		add(splitPane,
				new GBC(0, 1).setWeight(100, 100).setFill(
						GridBagConstraints.BOTH));

		setResizable(false);
		setSize(480, 400);
		setLocation(Util.calculatePosition(owner, this));
		splitPane.setDividerLocation(0.30);
	}

	class CommPanel extends StackPanel {
		public CommPanel() {
			setLayout(new GridBagLayout());
			JPanel panel = new JPanel();
			panel.setBorder(BorderFactory.createTitledBorder("通信设置"));
			panel.setLayout(new GridBagLayout());
			panel.add(new JLabel("选择串口："), new GBC(0, 0).setWeight(0, 0));
			panel.add(new JLabel("  波特率："), new GBC(0, 1).setWeight(0, 0));
			commNoCombo = new JComboBox( SerialComm.portNameList.toArray() );
			commNoCombo.setSelectedItem(SysConfiguration.commPort);
			commNoCombo.setPreferredSize(new Dimension(180, 20));
			rateCombo = new JComboBox(new Integer[] { 2400, 4800, 9600 });
			rateCombo.setSelectedItem(SysConfiguration.baudRate);
			rateCombo.setPreferredSize(new Dimension(180, 20));
			panel.add(commNoCombo,
					new GBC(1, 0).setWeight(100, 0).setInsets(0, 3, 0, 0));
			panel.add(rateCombo,
					new GBC(1, 1).setWeight(100, 0).setInsets(5, 3, 0, 0));
			panel.add(Util.makePrivButton(new ButtonAction("保存", null) {

				@Override
				public void actionPerformed(ActionEvent e) {
					int selection = JOptionPane.showConfirmDialog(
							SysSettingDialog.this, "确定要保存吗", "提示",
							JOptionPane.YES_NO_OPTION);
					if (selection == JOptionPane.YES_OPTION) {
						boolean portChanged = false;
						String commPort = (String) commNoCombo
								.getSelectedItem();
						Integer baudRate = (Integer) rateCombo
								.getSelectedItem();
						PropertiesUtil.writeProperties(
								SysConfiguration.FILE_PATH, "commPort",
								commPort.toString());
						PropertiesUtil.writeProperties(
								SysConfiguration.FILE_PATH, "baudRate",
								baudRate.toString());
						if (SysConfiguration.commPort != commPort
								|| SysConfiguration.baudRate != baudRate) {
							portChanged = true;
						}
						SysConfiguration.commPort = commPort;
						SysConfiguration.baudRate = baudRate;
						JOptionPane.showMessageDialog(SysSettingDialog.this,
								"保存成功");
						if (portChanged) {
							JOptionPane.showMessageDialog(
									SysSettingDialog.this, "通信配置已更改，请重启智能管理系统");
							System.exit(0);
						}
					}

				}
			}, owner.getCurrentUser()), new GBC(0, 2, 2, 1).setWeight(100, 100)
					.setAnchor(GridBagConstraints.NORTHEAST).setInsets(25));
			add(panel, Util.fillParentPanel());
		}

		private JComboBox commNoCombo;
		private JComboBox rateCombo;
	}

	class Dbanel extends StackPanel {
		public Dbanel() {
			setLayout(new GridBagLayout());
			JPanel panel = new JPanel();
			panel.setBorder(BorderFactory.createTitledBorder("数据库设置"));
			panel.setLayout(new GridBagLayout());
			panel.add(new JLabel("数据库IP："), new GBC(0, 0).setWeight(0, 0));
			panel.add(new JLabel("    端口："), new GBC(0, 1).setWeight(0, 0));
			panel.add(new JLabel("  用户名："), new GBC(0, 2).setWeight(0, 0));
			panel.add(new JLabel("    密码："), new GBC(0, 3).setWeight(0, 0));
			ipTxt = new JTextField();
			ipTxt.setText(SysConfiguration.dbIp);
			ipTxt.setPreferredSize(new Dimension(180, 20));
			portTxt = new JFormattedTextField(Util.getIntegerNumberFormat());
			portTxt.setValue(SysConfiguration.dbPort);
			portTxt.setPreferredSize(new Dimension(180, 20));
			userTxt = new JTextField();
			userTxt.setText(SysConfiguration.dbUserName);
			userTxt.setPreferredSize(new Dimension(180, 20));
			pwdTxt = new JPasswordField();
			pwdTxt.setText(SysConfiguration.dbPwd);
			pwdTxt.setPreferredSize(new Dimension(180, 20));
			panel.add(ipTxt,
					new GBC(1, 0).setWeight(100, 0).setInsets(0, 3, 0, 0));
			panel.add(portTxt,
					new GBC(1, 1).setWeight(100, 0).setInsets(5, 3, 0, 0));
			panel.add(userTxt,
					new GBC(1, 2).setWeight(100, 0).setInsets(5, 3, 0, 0));
			panel.add(pwdTxt,
					new GBC(1, 3).setWeight(100, 0).setInsets(5, 3, 0, 0));
			panel.add(Util.makePrivButton(new ButtonAction("保存", null) {

				@Override
				public void actionPerformed(ActionEvent e) {
					int selection = JOptionPane.showConfirmDialog(
							SysSettingDialog.this, "确定要保存吗", "提示",
							JOptionPane.YES_NO_OPTION);
					if (selection == JOptionPane.YES_OPTION) {
						String dbIp = ipTxt.getText();
						Integer dbPort = new Integer(portTxt.getText());
						String dbUserName = userTxt.getText();
						String dbPwd = pwdTxt.getText();
						PropertiesUtil.writeProperties(
								SysConfiguration.DBCONFIG_FILE_PATH,
								"hibernate.connection.url",
								SysConfiguration.makeDbUrl(dbIp, dbPort));
						PropertiesUtil.writeProperties(
								SysConfiguration.DBCONFIG_FILE_PATH,
								"hibernate.connection.username", dbUserName);
						PropertiesUtil.writeProperties(
								SysConfiguration.DBCONFIG_FILE_PATH,
								"hibernate.connection.password", dbPwd);

						SysConfiguration.dbIp = dbIp;
						SysConfiguration.dbPort = dbPort;
						SysConfiguration.dbUserName = dbUserName;
						SysConfiguration.dbPwd = dbPwd;

						JOptionPane.showMessageDialog(SysSettingDialog.this,
								"保存成功");
					}

				}
			}, owner.getCurrentUser()), new GBC(0, 4, 2, 1).setWeight(100, 100)
					.setAnchor(GridBagConstraints.NORTHEAST).setInsets(25));
			add(panel, Util.fillParentPanel());
		}

		private JTextField ipTxt;
		private JFormattedTextField portTxt;
		private JTextField userTxt;
		private JPasswordField pwdTxt;

	}

	class RackPanel extends StackPanel {
		public RackPanel() {

			setLayout(new GridBagLayout());
			JPanel panel = new JPanel();
			panel.setBorder(BorderFactory.createTitledBorder("充电架设置"));
			panel.setLayout(new GridBagLayout());
			panel.add(new JLabel("        充电架数："), new GBC(0, 0)
					.setWeight(0, 0).setAnchor(GridBagConstraints.WEST));
			panel.add(new JLabel("充电架充电座行数："), new GBC(0, 1).setWeight(0, 0)
					.setAnchor(GridBagConstraints.WEST));
			panel.add(new JLabel("充电架充电座列数："), new GBC(0, 2).setWeight(0, 0)
					.setAnchor(GridBagConstraints.WEST));
			panel.add(new JLabel("初始化进度："), new GBC(0, 4).setWeight(100, 0)
					.setAnchor(GridBagConstraints.WEST));
			rackCountTxt = new JFormattedTextField(
					Util.getIntegerNumberFormat());

			rackCountTxt.setPreferredSize(new Dimension(180, 20));
			unitRowCountTxt = new JFormattedTextField(
					Util.getIntegerNumberFormat());

			unitRowCountTxt.setPreferredSize(new Dimension(180, 20));
			unitColumnCountTxt = new JFormattedTextField(
					Util.getIntegerNumberFormat());

			unitColumnCountTxt.setPreferredSize(new Dimension(180, 20));

			panel.add(rackCountTxt,
					new GBC(1, 0).setWeight(100, 0).setInsets(0, 3, 0, 0));
			panel.add(unitRowCountTxt, new GBC(1, 1).setWeight(100, 0)
					.setInsets(5, 3, 0, 0));
			panel.add(unitColumnCountTxt, new GBC(1, 2).setWeight(100, 0)
					.setInsets(5, 3, 0, 0));
			panel.add(Util.makePrivButton(new ButtonAction("保存设置", null) {

				@Override
				public void actionPerformed(ActionEvent e) {
					int selection = JOptionPane.showConfirmDialog(
							SysSettingDialog.this, "确定要保存吗", "提示",
							JOptionPane.YES_NO_OPTION);
					if (selection == JOptionPane.YES_OPTION) {
						Integer rackCount = new Integer(rackCountTxt.getText());

						Integer rackRow = new Integer(unitRowCountTxt.getText());
						Integer rackColumn = new Integer(unitColumnCountTxt
								.getText());
						PropertiesUtil.writeProperties(
								SysConfiguration.FILE_PATH, "rackCount",
								rackCount.toString());
						PropertiesUtil.writeProperties(
								SysConfiguration.FILE_PATH, "rackRow",
								rackRow.toString());
						PropertiesUtil.writeProperties(
								SysConfiguration.FILE_PATH, "rackColumn",
								rackColumn.toString());

						JOptionPane.showMessageDialog(SysSettingDialog.this,
								"保存成功");
					}
				}
			}, owner.getCurrentUser()), new GBC(0, 3, 2, 1).setWeight(100, 0)
					.setAnchor(GridBagConstraints.EAST).setInsets(5, 3, 0, 5));
			initProgressBar = new JProgressBar(0, 1);

			initProgressBar.setMinimum(0);
			initProgressBar.setMaximum(100);
			initProgressBar.setValue(0);
			initProgressBar.setStringPainted(true);

			panel.add(initProgressBar,
					new GBC(0, 5, 3, 1).setWeight(100, 0).setInsets(5, 3, 0, 0)
							.setFill(GridBagConstraints.HORIZONTAL));
			panel.add(Util.makePrivButton(new ButtonAction("初始化", null) {

				@Override
				public void actionPerformed(ActionEvent e) {
					int selection = JOptionPane.showConfirmDialog(
							SysSettingDialog.this, "确定要初始化吗？该操作会清空数据库", "提示",
							JOptionPane.YES_NO_OPTION);
					if (selection == JOptionPane.YES_OPTION) {
						activity = new SimulatedActivity(100);
						activity.execute();
						SysConfiguration.sysState = SysConfiguration.SYS_STATE_REBOOT;
					}
				}
			}, owner.getCurrentUser()), new GBC(0, 6, 3, 1).setWeight(100, 0)
					.setAnchor(GridBagConstraints.EAST).setInsets(5, 3, 0, 5));
			JPanel rackNamingPanel = new JPanel();
			rackNamingPanel
					.setBorder(BorderFactory.createTitledBorder("充电架命名"));
			rackNamingPanel.setLayout(new GridBagLayout());
			rackNamingPanel.add(new JLabel("充电架序号："),
					new GBC(0, 0).setWeight(0, 0));
			rackNamingPanel.add(new JLabel("充电架编号："),
					new GBC(0, 1).setWeight(0, 1));
			sequenceNoTxt = new JFormattedTextField(
					Util.getIntegerNumberFormat());

			sequenceNoTxt.setPreferredSize(new Dimension(180, 20));
			serialNoTxt = new JFormattedTextField(Util.getIntegerNumberFormat());

			serialNoTxt.setPreferredSize(new Dimension(180, 20));
			rackNamingPanel.add(sequenceNoTxt, new GBC(1, 0).setWeight(100, 0)
					.setInsets(0, 1, 0, 0));
			rackNamingPanel.add(serialNoTxt, new GBC(1, 1).setWeight(100, 0)
					.setInsets(0, 1, 0, 0));
			rackNamingPanel.add(
					Util.makePrivButton(new ButtonAction("保存", null) {

						@Override
						public void actionPerformed(ActionEvent e) {
							int selection = JOptionPane.showConfirmDialog(
									SysSettingDialog.this, "确定要保存吗", "提示",
									JOptionPane.YES_NO_OPTION);
							if (selection == JOptionPane.YES_OPTION) {
								Integer rackSequence = new Integer(
										sequenceNoTxt.getText());

								Integer rackSerial = new Integer(serialNoTxt
										.getText());

								PropertiesUtil.writeProperties(
										SysConfiguration.FILE_PATH,
										"rackSequence", rackSequence.toString());
								PropertiesUtil.writeProperties(
										SysConfiguration.FILE_PATH,
										"rackSerial", rackSerial.toString());

								JOptionPane.showMessageDialog(
										SysSettingDialog.this, "保存成功");
							}

						}
					}, owner.getCurrentUser()),
					new GBC(0, 2, 2, 1).setWeight(100, 100)
							.setAnchor(GridBagConstraints.NORTHEAST)
							.setInsets(5, 3, 0, 5));
			// TODO 充电架命名无意义，不显示，用空面板占位
			// panel.add(rackNamingPanel, new GBC(0, 7, 3, 1).setWeight(100,
			// 100)
			// .setFill(GridBagConstraints.BOTH));
			panel.add(new JPanel(), new GBC(0, 7, 3, 1).setWeight(100, 100)
					.setFill(GridBagConstraints.BOTH));
			add(panel, Util.fillParentPanel());
		}

		class SimulatedActivity extends SwingWorker<Void, Integer> {

			public SimulatedActivity(int t) {
				current = 0;
				target = t;
			}

			protected Void doInBackground() throws Exception {

				// while (current < target) {
				// Thread.sleep(100);
				// current++;
				// publish(current);
				// }
				int rackCount = new Integer(rackCountTxt.getText());
				int unitCount = new Integer(unitRowCountTxt.getText())
						* new Integer(unitColumnCountTxt.getText());
				// LampUnitDAO lampUnitDAO = new LampUnitDAO();
				// LampRackDAO lampRackDAO = new LampRackDAO();
				StaffDAO staffDAO = new StaffDAO();
				// ChargingLogDAO chargingLogDAO = new ChargingLogDAO();

				Session session = HibernateUtil.getSessionFactory()
						.openSession();
				Transaction transaction = null;
				try {
					Thread.sleep(100);
					current += 20;
					publish(current);

					transaction = session.beginTransaction();
					// lampRackDAO.batchSaveInCurrentSession(rackCount,
					// session);
					Thread.sleep(100);
					current += 30;
					publish(current);
					// lampUnitDAO.batchSaveInCurrentSession(rackCount,
					// unitCount,
					// session);
					staffDAO.deleteAllInCurrentSession(session);
					// chargingLogDAO.deleteAllInCurrentSession(session);
					Thread.sleep(100);
					current += 50;
					publish(current);
					transaction.commit();

				} catch (HibernateException hibernateException) {
					transaction.rollback();
					hibernateException.printStackTrace();
				} catch (InterruptedException ie) {
					ie.printStackTrace();
				} finally {
					session.close();
				}

				return null;
			}

			protected void process(List<Integer> chunks) {
				for (Integer chunk : chunks) {

					initProgressBar.setValue(chunk);
				}
			}

			protected void done() {
				JOptionPane.showMessageDialog(SysSettingDialog.this,
						"初始化成功，请重启管理系统");
				System.exit(0);
			}

			private int current;
			private int target;
		}

		private JFormattedTextField rackCountTxt;
		private JFormattedTextField unitRowCountTxt;
		private JFormattedTextField unitColumnCountTxt;
		private JProgressBar initProgressBar;

		private JFormattedTextField sequenceNoTxt;
		private JFormattedTextField serialNoTxt;
		private SimulatedActivity activity;
	}

	class UserPanel extends StackPanel {
		public UserPanel() {
			userDAO = new UserDAO();
			setLayout(new GridBagLayout());
			JPanel panel = new JPanel();
			panel.setBorder(BorderFactory.createTitledBorder("用户设置"));
			panel.setLayout(new GridBagLayout());
			panel.add(new JLabel("      用户名："), new GBC(0, 0).setWeight(0, 0));
			panel.add(new JLabel("        口令："), new GBC(0, 1).setWeight(0, 0));
			panel.add(new JLabel("    重复口令："), new GBC(0, 2).setWeight(0, 0));
			panel.add(new JLabel("    用户类型："), new GBC(0, 3).setWeight(0, 0));
			panel.add(new JLabel("当前用户列表："), new GBC(0, 5, 2, 1)
					.setWeight(0, 0).setAnchor(GridBagConstraints.WEST));
			userTxt = new JTextField();
			userTxt.setPreferredSize(new Dimension(180, 20));
			pwdTxt = new JPasswordField();
			pwdTxt.setPreferredSize(new Dimension(180, 20));
			rpwdTxt = new JPasswordField();
			rpwdTxt.setPreferredSize(new Dimension(180, 20));
			typeCombo = new JComboBox(new String[] { "普通用户", "管理员" });
			typeCombo.setPreferredSize(new Dimension(180, 20));

			panel.add(
					userTxt,
					new GBC(1, 0).setWeight(100, 0)
							.setFill(GridBagConstraints.HORIZONTAL)
							.setInsets(0, 3, 0, 20));
			panel.add(
					pwdTxt,
					new GBC(1, 1).setWeight(100, 0)
							.setFill(GridBagConstraints.HORIZONTAL)
							.setInsets(5, 3, 0, 20));
			panel.add(
					rpwdTxt,
					new GBC(1, 2).setWeight(100, 0)
							.setFill(GridBagConstraints.HORIZONTAL)
							.setInsets(5, 3, 0, 20));
			panel.add(
					typeCombo,
					new GBC(1, 3).setWeight(100, 0)
							.setFill(GridBagConstraints.HORIZONTAL)
							.setInsets(5, 3, 0, 20));
			JPanel buttonPanel = new JPanel();
			FlowLayout layout = new FlowLayout(FlowLayout.RIGHT);
			layout.setVgap(10);
			buttonPanel.setLayout(layout);
			buttonPanel.add(Util.makePrivButton(new ButtonAction("新增", null) {

				@Override
				public void actionPerformed(ActionEvent e) {
					userId = null;

					userTxt.setText("");
					userTxt.setEnabled(true);
					pwdTxt.setText("");
					rpwdTxt.setText("");
					typeCombo.setSelectedIndex(0);
				}
			}, owner.getCurrentUser()));
			buttonPanel.add(Util.makePrivButton(new ButtonAction("删除用户", null) {

				@Override
				public void actionPerformed(ActionEvent e) {
					int selection = JOptionPane.showConfirmDialog(
							SysSettingDialog.this, "确定要删除吗", "提示",
							JOptionPane.YES_NO_OPTION);
					if (selection == JOptionPane.YES_OPTION) {

						User selectedUser = (User) userList.getSelectedValue();
						if (selectedUser == null) {
							JOptionPane.showMessageDialog(
									SysSettingDialog.this, "请先选择要删除的用户");
							return;
						}
						if (selectedUser.getName().equals("admin")) {
							JOptionPane.showMessageDialog(
									SysSettingDialog.this,
									"admin为系统保留用户，不允许被删除");
							return;
						}
						userDAO.deleteUser(selectedUser.getId());
						JOptionPane.showMessageDialog(SysSettingDialog.this,
								"删除成功");
						userList.setListData(userDAO.listUsers().toArray());
					}
				}
			}, owner.getCurrentUser()));
			buttonPanel.add(Util.makePrivButton(new ButtonAction("保存修改", null) {

				@Override
				public void actionPerformed(ActionEvent e) {
					int selection = JOptionPane.showConfirmDialog(
							SysSettingDialog.this, "确定要保存吗", "提示",
							JOptionPane.YES_NO_OPTION);
					if (selection == JOptionPane.YES_OPTION) {

						if (userId == null
								&& userDAO.queryByName(userTxt.getText()) != null) {
							JOptionPane.showMessageDialog(
									SysSettingDialog.this, "该用户名已存在");
							return;
						}
						if (pwdTxt.getText().startsWith(" ")
								|| pwdTxt.getText().endsWith(" ")) {
							JOptionPane.showMessageDialog(
									SysSettingDialog.this, "密码首尾请不要包含空格");
							return;
						}
						if (!pwdTxt.getText().trim()
								.equals(rpwdTxt.getText().trim())) {
							System.out.println("");
							JOptionPane.showMessageDialog(
									SysSettingDialog.this, "两次输入密码不一致");
							return;
						}
						userDAO.save(userId, userTxt.getText(),
								pwdTxt.getText(), typeCombo.getSelectedIndex());
						JOptionPane.showMessageDialog(SysSettingDialog.this,
								"保存成功");
						if (userId != null) {
							owner.getCurrentUser().setPwd(pwdTxt.getText());
						}
						userList.setListData(userDAO.listUsers().toArray());
					}
				}
			}, owner.getCurrentUser()));
			panel.add(buttonPanel, new GBC(0, 4, 2, 1).setWeight(100, 0)
					.setAnchor(GridBagConstraints.EAST).setInsets(5));

			JPanel userListPanel = new JPanel();
			userListPanel.setLayout(new GridBagLayout());
			userList = new JList(userDAO.listUsers().toArray());
			userList.addListSelectionListener(new ListSelectionListener() {

				@Override
				public void valueChanged(ListSelectionEvent e) {
					User selectUser = (User) userList.getSelectedValue();
					if (selectUser != null) {
						userId = selectUser.getId();
						userTxt.setText(selectUser.getName());
						userTxt.setEnabled(false);
						pwdTxt.setText(selectUser.getPwd());
						typeCombo.setSelectedIndex(selectUser.getPriv());
					}

				}
			});

			userListPanel
					.add(new JScrollPane(userList), Util.fillParentPanel());
			panel.add(userListPanel, new GBC(0, 6, 2, 1).setWeight(100, 100)
					.setFill(GridBagConstraints.BOTH));
			add(panel, Util.fillParentPanel());

		}

		private Long userId;
		private JTextField userTxt;
		private JPasswordField pwdTxt;
		private JPasswordField rpwdTxt;
		private JComboBox typeCombo;
		private JList userList;
		private UserDAO userDAO;
	}

	class InfoItemPanel extends StackPanel {

		public InfoItemPanel(final InfoItem.TYPE type) {
			setLayout(new GridBagLayout());
			JPanel panel = new JPanel();
			panel.setLayout(new GridBagLayout());
			panel.setBorder(BorderFactory.createTitledBorder(type.getDesc()
					+ "设置"));

			JPanel tablePanel = new JPanel();
			tablePanel.setLayout(new BorderLayout());
			// 加入表格组建时swing的splitpanel有问题，预设下父组件的尺寸
			tablePanel.setPreferredSize(new Dimension(200, 200));
			tableModel = new InfoItemTableModel(
					infoItemDAO.listAllFieldsByType(type));
			table = new JTable(tableModel);
			TableColumnModel columnModel = table.getColumnModel();
			// 隐藏Id列
			table.removeColumn(columnModel.getColumn(1));
			columnModel.getColumn(0).setPreferredWidth(50);
			columnModel.getColumn(1).setPreferredWidth(50);
			columnModel.getColumn(2).setPreferredWidth(100);

			table.setFillsViewportHeight(true);
			tablePanel.add(new JScrollPane(table));
			JPanel buttonPanel = new JPanel();
			buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
			JButton addBtn = UIUtil.makeButton("添加", null,
					new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {
							InfoItemInfoDialog infoItemInfoDialog = new InfoItemInfoDialog(
									SysSettingDialog.this, null, type);
							infoItemInfoDialog.setVisible(true);
							refreshTable(type);
						}
					});
			JButton editBtn = UIUtil.makeButton("编辑", null,
					new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {
							if (table.getSelectedRowCount() == 0) {
								JOptionPane.showMessageDialog(
										SysSettingDialog.this, "请选择待编辑的项目");
								return;
							}
							int selRow = table.getSelectedRow();
							Long id = (Long) tableModel.getValueAt(selRow, 1);
							InfoItem infoItem = infoItemDAO.findById(id);
							InfoItemInfoDialog infoItemInfoDialog = new InfoItemInfoDialog(
									SysSettingDialog.this, infoItem, type);
							infoItemInfoDialog.setVisible(true);
							refreshTable(type);
						}
					});
			JButton delBtn = UIUtil.makeButton("删除", null,
					new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {
							if (table.getSelectedRowCount() == 0) {
								JOptionPane.showMessageDialog(
										SysSettingDialog.this, "请选择待删除的项目");
								return;
							}
							int choice = JOptionPane.showConfirmDialog(
									SysSettingDialog.this, "确定要删除选择的项目吗",
									"确认对话框", JOptionPane.YES_NO_OPTION);
							if (choice == JOptionPane.YES_OPTION) {
								int selRow = table.getSelectedRow();
								Long id = (Long) tableModel.getValueAt(selRow,
										1);
								InfoItem infoItem = infoItemDAO.findById(id);
								infoItemDAO.delete(infoItem);
								refreshTable(type);
							}
						}
					});
			buttonPanel.add(addBtn);
			buttonPanel.add(editBtn);
			buttonPanel.add(delBtn);
			panel.add(buttonPanel, new GBC(0, 0).setWeight(100, 0).setFillH());
			panel.add(tablePanel, new GBC(0, 1).setWeightFull().setFillBoth());
			add(panel, Util.fillParentPanel());
		}

		private void refreshTable(InfoItem.TYPE type) {
			tableModel.setModel(infoItemDAO.listAllFieldsByType(type));
			tableModel.fireTableDataChanged();
		}

		private InfoItemTableModel tableModel;
		private JTable table;
	}

	class InfoItemTreePanel extends StackPanel {

		public InfoItemTreePanel(final InfoItem.TYPE type) {
			setLayout(new GridBagLayout());
			JPanel panel = new JPanel();
			panel.setLayout(new GridBagLayout());
			panel.setBorder(BorderFactory.createTitledBorder(type.getDesc()
					+ "设置"));

			JPanel treePanel = new JPanel();
			treePanel.setLayout(new BorderLayout());

			treePanel.setPreferredSize(new Dimension(200, 200));
			DefaultMutableTreeNode root = Util.getDepTree();
			treeModel = new DefaultTreeModel(root);
			tree = new JTree(treeModel);
			tree.setExpandsSelectedPaths(true);
			treePanel.add(new JScrollPane(tree));
			JPanel buttonPanel = new JPanel();
			buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
			JButton addBtn = UIUtil.makeButton("添加", null,
					new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {
							TreePath selPath = tree.getSelectionPath();
							if (selPath == null) {
								JOptionPane.showMessageDialog(
										SysSettingDialog.this, "请先选择父级部门");
								return;

							}
							int selRow = tree.getSelectionRows()[0];
							DefaultMutableTreeNode selNode = (DefaultMutableTreeNode) selPath
									.getLastPathComponent();

							InfoItem selItem = (InfoItem) selNode
									.getUserObject();
							if (selItem.getLevel() > 2) {
								JOptionPane.showMessageDialog(
										SysSettingDialog.this,
										"本系统目前只支持不超过三级组织架构");
								return;
							}
							InfoItem addedItem = new InfoItem();
							InfoItemInfoDialog infoItemInfoDialog = new InfoItemInfoDialog(
									SysSettingDialog.this, null, type, selItem,
									addedItem);
							infoItemInfoDialog.setVisible(true);
							// refreshTree(type);
							if (addedItem.getId() != null) {
								DefaultMutableTreeNode addedNode=new DefaultMutableTreeNode(addedItem);
						//		selNode.add(new DefaultMutableTreeNode(
						//				addedItem));
								treeModel.insertNodeInto(addedNode, selNode,selNode.getChildCount());
						//		TreeNode[] treeNodes=treeModel.getPathToRoot(addedNode);
						//		TreePath treePath=new TreePath(treeNodes);
						//		tree.scrollPathToVisible(selPath);
								Util.clearDepTree();
							}

						}
					});
			JButton editBtn = UIUtil.makeButton("编辑", null,
					new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {
							DefaultMutableTreeNode selNode = (DefaultMutableTreeNode) tree
									.getLastSelectedPathComponent();
							if (selNode == null) {
								JOptionPane.showMessageDialog(
										SysSettingDialog.this, "请选择待编辑的项目");
								return;
							}

							InfoItem infoItem = (InfoItem) selNode
									.getUserObject();
							InfoItemInfoDialog infoItemInfoDialog = new InfoItemInfoDialog(
									SysSettingDialog.this, infoItem,
									InfoItem.TYPE.REGION);
							infoItemInfoDialog.setVisible(true);
							treeModel.nodeChanged(selNode);
							Util.clearDepTree();
							//	refreshTree(type);
						}
					});
			JButton delBtn = UIUtil.makeButton("删除", null,
					new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {
							DefaultMutableTreeNode selNode = (DefaultMutableTreeNode) tree
									.getLastSelectedPathComponent();
							if (selNode == null) {
								JOptionPane.showMessageDialog(
										SysSettingDialog.this, "请选择待删除的项目");
								return;
							}
							int choice = JOptionPane.showConfirmDialog(
									SysSettingDialog.this, "确定要删除选择的项目吗",
									"确认对话框", JOptionPane.YES_NO_OPTION);
							if (choice == JOptionPane.YES_OPTION) {

								InfoItem infoItem = (InfoItem) selNode
										.getUserObject();
								infoItemDAO.delete(infoItem);
								treeModel.removeNodeFromParent(selNode);
								Util.clearDepTree();
							}
						}
					});
			buttonPanel.add(addBtn);
			buttonPanel.add(editBtn);
			buttonPanel.add(delBtn);
			panel.add(buttonPanel, new GBC(0, 0).setWeight(100, 0).setFillH());
			panel.add(treePanel, new GBC(0, 1).setWeightFull().setFillBoth());
			add(panel, Util.fillParentPanel());
		}

		private void refreshTree(InfoItem.TYPE type) {
			Util.clearDepTree();
			tree.setModel(new DefaultTreeModel(Util.getDepTree()));
			tree.updateUI();
		}

		private JTree tree;
		private DefaultTreeModel treeModel;
	}

	class UsePanel extends StackPanel {
		public UsePanel() {
			setLayout(new GridBagLayout());
			JPanel panel = new JPanel();
			panel.setBorder(BorderFactory.createTitledBorder("充电架使用设置"));
			panel.setLayout(new GridBagLayout());
			panel.add(new JLabel("充电次数限制："), new GBC(0, 1).setWeight(0, 0));
			panel.add(new JLabel("使用时长限制："), new GBC(0, 2).setWeight(0, 0));
			panel.add(new JLabel("次  "), new GBC(2, 1).setWeight(0, 0)
					.setInsets(5, 0, 0, 0));
			panel.add(new JLabel("小时"), new GBC(2, 2).setWeight(0, 0)
					.setInsets(5, 0, 0, 0));
			ButtonGroup buttonGroup = new ButtonGroup();
			countLimitRadioButton = new JRadioButton("限制使用次数");
			countLimitRadioButton
					.setSelected(SysConfiguration.isCountLimited == 0);
			buttonGroup.add(countLimitRadioButton);

			timeLimitRadioButton = new JRadioButton("限制使用时长");
			timeLimitRadioButton
					.setSelected(SysConfiguration.isCountLimited == 1);
			buttonGroup.add(timeLimitRadioButton);

			countLimitTxt = new JFormattedTextField(
					Util.getIntegerNumberFormat());
			countLimitTxt.setValue(SysConfiguration.limitCount);
			countLimitTxt.setPreferredSize(new Dimension(150, 20));
			timeLimitTxt = new JFormattedTextField(
					Util.getIntegerNumberFormat());
			timeLimitTxt.setValue(SysConfiguration.limitTime);
			timeLimitTxt.setPreferredSize(new Dimension(150, 20));

			panel.add(countLimitRadioButton, new GBC(0, 0).setWeight(0, 0)
					.setInsets(0, 0, 0, 5));
			panel.add(timeLimitRadioButton, new GBC(1, 0).setWeight(0, 0)
					.setInsets(0, 5, 0, 0));
			panel.add(countLimitTxt,
					new GBC(1, 1).setWeight(100, 0).setInsets(5, 3, 0, 0));
			panel.add(timeLimitTxt,
					new GBC(1, 2).setWeight(100, 0).setInsets(5, 3, 0, 0));

			panel.add(Util.makePrivButton(new ButtonAction("保存设置", null) {

				@Override
				public void actionPerformed(ActionEvent e) {
					int selection = JOptionPane.showConfirmDialog(
							SysSettingDialog.this, "确定要保存吗", "提示",
							JOptionPane.YES_NO_OPTION);
					if (selection == JOptionPane.YES_OPTION) {
						Integer isCountLimited = countLimitRadioButton
								.isSelected() ? 0 : 1;
						Integer limitCount = new Integer(countLimitTxt
								.getText());
						Integer limitTime = new Integer(timeLimitTxt.getText());
						PropertiesUtil.writeProperties(
								SysConfiguration.FILE_PATH, "isCountLimited",
								isCountLimited.toString());
						PropertiesUtil.writeProperties(
								SysConfiguration.FILE_PATH, "limitCount",
								limitCount.toString());
						PropertiesUtil.writeProperties(
								SysConfiguration.FILE_PATH, "limitTime",
								limitTime.toString());
						SysConfiguration.isCountLimited = isCountLimited;
						SysConfiguration.limitCount = limitCount;
						SysConfiguration.limitTime = limitTime;
						JOptionPane.showMessageDialog(SysSettingDialog.this,
								"保存成功");
					}

				}
			}, owner.getCurrentUser()), new GBC(0, 3, 3, 1).setWeight(100, 100)
					.setAnchor(GridBagConstraints.NORTHEAST).setInsets(25));
			add(panel, Util.fillParentPanel());
		}

		private JRadioButton countLimitRadioButton;
		private JRadioButton timeLimitRadioButton;
		private JFormattedTextField countLimitTxt;
		private JFormattedTextField timeLimitTxt;
	}

	/*
	 * class PositionPanel extends StackPanel { public PositionPanel() {
	 * infoItemDAO = new InfoItemDAO(); setLayout(new GridBagLayout()); JPanel
	 * panel = new JPanel();
	 * panel.setBorder(BorderFactory.createTitledBorder("岗位设置"));
	 * panel.setLayout(new GridBagLayout()); panel.add(new JLabel("工种列表："), new
	 * GBC(0, 0).setWeight(100, 0) .setAnchor(GridBagConstraints.WEST));
	 * workTypeList = new JList(infoItemDAO.queryByType(
	 * InfoItem.WORK_TYPE_ITEM).toArray());
	 * 
	 * panel.add(new JScrollPane(workTypeList), new GBC(0, 1, 1, 2)
	 * .setWeight(100, 100).setFill(GridBagConstraints.BOTH));
	 * panel.add(Util.makePrivButton(new ButtonAction("添加工种", null) {
	 * 
	 * @Override public void actionPerformed(ActionEvent e) { String input =
	 * JOptionPane.showInputDialog( SysSettingDialog.this, "添加工种");
	 * infoItemDAO.save(InfoItem.WORK_TYPE_ITEM_DESC, InfoItem.WORK_TYPE_ITEM,
	 * input); workTypeList.setListData(infoItemDAO.queryByType(
	 * InfoItem.WORK_TYPE_ITEM).toArray()); JOptionPane
	 * .showMessageDialog(SysSettingDialog.this, "添加成功");
	 * 
	 * } }, owner.getCurrentUser()), new GBC(1, 1).setWeight(0, 100)
	 * .setAnchor(GridBagConstraints.SOUTH).setInsets(5));
	 * panel.add(Util.makePrivButton(new ButtonAction("删除工种", null) {
	 * 
	 * @Override public void actionPerformed(ActionEvent e) { int selection =
	 * JOptionPane.showConfirmDialog( SysSettingDialog.this, "确定要删除吗", "提示",
	 * JOptionPane.YES_NO_OPTION); if (selection == JOptionPane.YES_OPTION) {
	 * InfoItem selInfoItem = (InfoItem) workTypeList .getSelectedValue(); if
	 * (selInfoItem == null) { JOptionPane.showMessageDialog(
	 * SysSettingDialog.this, "请先选择部门"); return; }
	 * infoItemDAO.deleteInfoItem(selInfoItem.getId());
	 * workTypeList.setListData(infoItemDAO.queryByType(
	 * InfoItem.WORK_TYPE_ITEM).toArray()); }
	 * 
	 * } }, owner.getCurrentUser()), new GBC(1, 2).setWeight(0, 0)
	 * .setAnchor(GridBagConstraints.SOUTH).setInsets(5));
	 * 
	 * panel.add(new JLabel("当期队组列表："), new GBC(0, 3).setWeight(100, 0)
	 * .setAnchor(GridBagConstraints.WEST)); departmentList = new
	 * JList(infoItemDAO.queryByType( InfoItem.DEPARTMENT_ITEM).toArray());
	 * 
	 * panel.add(new JScrollPane(departmentList), new GBC(0, 4, 1, 2)
	 * .setWeight(100, 100).setFill(GridBagConstraints.BOTH));
	 * panel.add(Util.makePrivButton(new ButtonAction("添加队组", null) {
	 * 
	 * @Override public void actionPerformed(ActionEvent e) { String input =
	 * JOptionPane.showInputDialog( SysSettingDialog.this, "添加队组");
	 * infoItemDAO.save(InfoItem.DEPARTMENT_ITEM_DESC, InfoItem.DEPARTMENT_ITEM,
	 * input); departmentList.setListData(infoItemDAO.queryByType(
	 * InfoItem.DEPARTMENT_ITEM).toArray()); JOptionPane
	 * .showMessageDialog(SysSettingDialog.this, "添加成功"); } },
	 * owner.getCurrentUser()), new GBC(1, 4).setWeight(0, 100)
	 * .setAnchor(GridBagConstraints.SOUTH).setInsets(5));
	 * panel.add(Util.makePrivButton(new ButtonAction("删除队组", null) {
	 * 
	 * @Override public void actionPerformed(ActionEvent e) { int selection =
	 * JOptionPane.showConfirmDialog( SysSettingDialog.this, "确定要删除吗", "提示",
	 * JOptionPane.YES_NO_OPTION); if (selection == JOptionPane.YES_OPTION) {
	 * InfoItem selInfoItem = (InfoItem) departmentList .getSelectedValue(); if
	 * (selInfoItem == null) { JOptionPane.showMessageDialog(
	 * SysSettingDialog.this, "请先选择队组"); return; }
	 * infoItemDAO.deleteInfoItem(selInfoItem.getId());
	 * departmentList.setListData(infoItemDAO.queryByType(
	 * InfoItem.DEPARTMENT_ITEM).toArray()); } } }, owner.getCurrentUser()), new
	 * GBC(1, 5).setWeight(0, 0)
	 * .setAnchor(GridBagConstraints.SOUTH).setInsets(5));
	 * 
	 * add(panel, Util.fillParentPanel()); }
	 * 
	 * private JList workTypeList; private JList departmentList; private
	 * InfoItemDAO infoItemDAO; }
	 */
	class TimeoutPanel extends StackPanel {
		public TimeoutPanel() {
			setLayout(new GridBagLayout());
			JPanel panel = new JPanel();
			panel.setBorder(BorderFactory.createTitledBorder("超时设置"));
			panel.setLayout(new GridBagLayout());
			panel.add(new JLabel("超时时间长度："), new GBC(0, 0).setWeight(0, 0));

			panel.add(new JLabel("小时"), new GBC(2, 0).setWeight(0, 0)
					.setAnchor(GridBagConstraints.WEST).setInsets(5, 0, 0, 0));

			timeoutTxt = new JFormattedTextField(Util.getIntegerNumberFormat());
			timeoutTxt.setValue(SysConfiguration.timeOutTime);
			timeoutTxt.setPreferredSize(new Dimension(150, 20));

			panel.add(timeoutTxt,
					new GBC(1, 0).setWeight(100, 0).setInsets(5, 3, 0, 0));

			panel.add(Util.makePrivButton(new ButtonAction("保存设置", null) {

				@Override
				public void actionPerformed(ActionEvent e) {
					int selection = JOptionPane.showConfirmDialog(
							SysSettingDialog.this, "确定要保存吗", "提示",
							JOptionPane.YES_NO_OPTION);
					if (selection == JOptionPane.YES_OPTION) {
						Integer timeOutTime = new Integer(timeoutTxt.getText());

						PropertiesUtil.writeProperties(
								SysConfiguration.FILE_PATH, "timeOutTime",
								timeOutTime.toString());

						SysConfiguration.timeOutTime = timeOutTime;

						JOptionPane.showMessageDialog(SysSettingDialog.this,
								"保存成功");
					}
				}
			}, owner.getCurrentUser()), new GBC(0, 2, 3, 1).setWeight(100, 100)
					.setAnchor(GridBagConstraints.NORTHEAST).setInsets(25));
			add(panel, Util.fillParentPanel());
		}

		private JFormattedTextField timeoutTxt;
	}

	public InfoItemDAO getInfoItemDAO() {
		return infoItemDAO;
	}

	public void setInfoItemDAO(InfoItemDAO infoItemDAO) {
		this.infoItemDAO = infoItemDAO;
	}

	private StaffManagerFrame owner;
	private InfoItemDAO infoItemDAO;

}