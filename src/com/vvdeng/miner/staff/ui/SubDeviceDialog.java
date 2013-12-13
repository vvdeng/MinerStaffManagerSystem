package com.vvdeng.miner.staff.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.vvdeng.miner.constant.ReaderType;
import com.vvdeng.miner.staff.dao.CardReaderDAO;
import com.vvdeng.miner.staff.dao.SubDeviceDAO;
import com.vvdeng.miner.staff.entity.CardReader;
import com.vvdeng.miner.staff.entity.InfoItem;
import com.vvdeng.miner.staff.entity.SubDevice;
import com.vvdeng.miner.staff.filter.CardReaderFilter;
import com.vvdeng.miner.staff.utils.GlobalData;
import com.vvdeng.miner.staff.utils.InfoItemUtil;
import com.vvdeng.miner.staff.utils.UIUtil;
import com.vvdeng.miner.staff.utils.Util;

public class SubDeviceDialog extends JDialog {

	public SubDeviceDialog(StaffManagerFrame owner, SubDevice subDevice) {
		super(owner, "分站设置", true);
		cardReaderDAO = new CardReaderDAO();
		this.owner = owner;
		this.subDevice = subDevice;
		setSize(WIDTH, HEIGHT);
		UIUtil.setCenterPositoin(owner, this);
		setLayout(new GridBagLayout());

		defPanel = new DefPanel();

		GraphicPanel graphicPanel = new GraphicPanel();
		add(defPanel, new GBC(0, 0).setWeight(100, 30).setFillBoth());
		add(graphicPanel, new GBC(0, 1).setWeight(100, 70).setFillBoth());
		defPanel.fillSubDeviceFields(subDevice);
	}

	class DefPanel extends JPanel {
		public DefPanel() {
			subDeviceDAO = new SubDeviceDAO();
			setLayout(new GridBagLayout());
			tabbedDefPanel = new JTabbedPane();
			// 分站定义
			JPanel subDeviceDefPanel = new JPanel();
			subDeviceDefPanel.setLayout(new GridBagLayout());
			JLabel subDeviceNameLabel = new JLabel("名  称");
			subDeviceNameTxt = new JTextField();
			subDeviceNameTxt.setPreferredSize(new Dimension(100, 20));
			JLabel subDeviceTypeLabel = new JLabel("类  型");
			subDeviceTypeCombo = new JComboBox(InfoItemUtil.getInfoItem(
					InfoItem.TYPE.SUBDEVICE_TYPE).toArray());
			subDeviceTypeCombo.setPreferredSize(new Dimension(100, 20));
			JLabel subDeviceAddrLabel = new JLabel("地址码");
			subDeviceAddrTxt = new JTextField();
			subDeviceAddrTxt.setPreferredSize(new Dimension(100, 20));
			JLabel subDeviceRegionLabel = new JLabel("区  域");
			subDeviceRegionCombo = new JComboBox(InfoItemUtil.getInfoItem(
					InfoItem.TYPE.REGION).toArray());
			subDeviceRegionCombo.setPreferredSize(new Dimension(100, 20));
			JLabel subDeviceDescriptionLabel = new JLabel("描  述");
			subDeviceDescriptionTxt = new JTextArea();
			subDeviceDescriptionTxt.setColumns(30);
			subDeviceDescriptionTxt.setRows(5);
			subDeviceDescriptionTxt.setLineWrap(true);
			JPanel subDeviceButtonPanel = new JPanel();
			subDeviceButtonPanel.setLayout(new FlowLayout(FlowLayout.CENTER,
					10, 0));
			subDeviceButtonPanel.add(UIUtil.makeButton("编辑", null,
					new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {
							enableSubDeviceFields();

						}
					}));
			subDeviceButtonPanel.add(UIUtil.makeButton("保存", null,
					new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {
							if(!checkSubDeviceFields()){
								return;
							}
							SubDevice subDevice = new SubDevice();
							if(SubDeviceDialog.this.subDevice!=null){
								subDevice.setId(SubDeviceDialog.this.subDevice.getId());
							}
							subDevice
									.setName(subDeviceNameTxt.getText().trim());
							subDevice.setDeviceId(Integer.parseInt(subDeviceAddrTxt.getText().trim()));
							subDevice
									.setDescription(subDeviceDescriptionTxt.getText().trim());
							subDevice.setTypeId(((InfoItem) subDeviceTypeCombo
									.getSelectedItem()).getId());
							subDevice.setType(((InfoItem) subDeviceTypeCombo
									.getSelectedItem()).getValue());
							subDevice
									.setRegionId(((InfoItem) subDeviceRegionCombo
											.getSelectedItem()).getId());
							subDevice.setRegion(((InfoItem) subDeviceRegionCombo
									.getSelectedItem()).getValue());
							subDeviceDAO.save(subDevice);
							GlobalData.subDeviceMap.put(subDevice.getDeviceId(), subDevice);
							JOptionPane.showMessageDialog(SubDeviceDialog.this,
									"保存成功");
							owner.refreshSubDeviceTable();
						}
					}));

			subDeviceDefPanel.add(subDeviceNameLabel,
					new GBC(0, 0).setAnchor(GridBagConstraints.EAST));
			subDeviceDefPanel.add(subDeviceNameTxt, new GBC(1, 0).setInsets(3));
			subDeviceDefPanel.add(subDeviceTypeLabel,
					new GBC(2, 0).setAnchor(GridBagConstraints.EAST));
			subDeviceDefPanel.add(subDeviceTypeCombo,
					new GBC(3, 0).setInsets(3));
			subDeviceDefPanel.add(subDeviceAddrLabel,
					new GBC(0, 1).setAnchor(GridBagConstraints.EAST));
			subDeviceDefPanel.add(subDeviceAddrTxt, new GBC(1, 1).setInsets(3));
			subDeviceDefPanel.add(subDeviceRegionLabel,
					new GBC(2, 1).setAnchor(GridBagConstraints.EAST));
			subDeviceDefPanel.add(subDeviceRegionCombo,
					new GBC(3, 1).setInsets(3));
			subDeviceDefPanel.add(subDeviceDescriptionLabel,
					new GBC(4, 0).setAnchor(GridBagConstraints.EAST));
			subDeviceDefPanel.add(new JScrollPane(subDeviceDescriptionTxt), new GBC(5,
					0, 1, 2).setInsets(3));
			subDeviceDefPanel.add(subDeviceButtonPanel, new GBC(0, 2, 6, 1)
					.setFillH().setInsets(10, 0, 0, 0));

			// 端口定义
			JPanel portDefPanel = new JPanel();
			portDefPanel.setLayout(new GridBagLayout());
			// setBackground(Color.RED);
			JLabel portNumLabel = new JLabel("端口号");
			portNumValLabel = new JLabel();
			// portNumValLabel.setPreferredSize(new Dimension(100, 25));
			// portNumValLabel.setBorder(BorderFactory.createEtchedBorder());
			JLabel readerNameLabel = new JLabel("名  称");
			readerNameTxt = new JTextField();
			readerNameTxt.setPreferredSize(new Dimension(100, 20));
			JLabel readerTypeLabel = new JLabel("类  型");
			readerTypeCombo = new JComboBox(ReaderType.values());
			readerTypeCombo.setPreferredSize(new Dimension(100, 20));
			JLabel readerRegionLabel = new JLabel("区  域");
			readerRegionCombo = new JComboBox(InfoItemUtil.getInfoItem(
					InfoItem.TYPE.REGION).toArray());
			readerRegionCombo.setPreferredSize(new Dimension(100, 20));
			JLabel readerDescriptionLabel = new JLabel("描  述");
			readerDescriptionTxt = new JTextArea();
			readerDescriptionTxt.setColumns(30);
			readerDescriptionTxt.setRows(5);
			readerDescriptionTxt.setLineWrap(true);
			JPanel readerButtonPanel = new JPanel();
			readerButtonPanel
					.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0));
			readerButtonPanel.add(UIUtil.makeButton("编辑", null,
					new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {
							enableReaderFields();
						}
					}));
			readerButtonPanel.add(UIUtil.makeButton("保存", null,
					new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {
							checkReaderFields();
							CardReader reader;
							if(portMap.containsKey(selPortNum)){
								reader=portMap.get(selPortNum);
							}
							else{
								reader = new CardReader();
							}
							reader.setDeviceId(subDevice.getDeviceId());
							reader.setPortNum(new Integer(portNumValLabel
									.getText()));
							reader.setName(readerNameTxt.getText());
							reader.setDescription(readerDescriptionTxt.getText());
							reader.setTypeId(((InfoItem) readerTypeCombo
									.getSelectedItem()).getId());
							reader.setType(((InfoItem) readerTypeCombo
									.getSelectedItem()).getValue());
							reader.setRegionId(((long)((ReaderType) readerRegionCombo
									.getSelectedItem()).getId()));
							reader.setRegion(((ReaderType) readerRegionCombo
									.getSelectedItem()).getDesc());
							cardReaderDAO.save(reader);
							GlobalData.cardReaderMap.put(reader.getReaderId(), reader);
							JOptionPane.showMessageDialog(SubDeviceDialog.this,
									"保存成功");
							// TODO 考虑直接替换缓存
							initPorts(subDevice);
						}
					}));
			readerButtonPanel.add(UIUtil.makeButton("移除", null,
					new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {
							CardReader selCardReader = portMap.get(new Integer(
									portNumValLabel.getText()));
							if (selCardReader != null) {
								int choice = JOptionPane.showConfirmDialog(
										SubDeviceDialog.this, "确定要移除选定的端口定义吗	",
										"确认对话框", JOptionPane.YES_NO_OPTION);
								if (choice == JOptionPane.YES_OPTION) {
									GlobalData.cardReaderMap.remove(selCardReader).getReaderId();
									cardReaderDAO.delete(selCardReader);
									clearReaderFields();
									initPorts(subDevice);
								}
							}

						}
					}));
			portDefPanel.add(portNumLabel,
					new GBC(0, 0).setAnchor(GridBagConstraints.EAST));
			portDefPanel.add(portNumValLabel, new GBC(1, 0).setInsets(3));
			portDefPanel.add(readerNameLabel,
					new GBC(0, 1).setAnchor(GridBagConstraints.EAST));
			portDefPanel.add(readerNameTxt, new GBC(1, 1).setInsets(3));
			portDefPanel.add(readerTypeLabel,
					new GBC(2, 0).setAnchor(GridBagConstraints.EAST));
			portDefPanel.add(readerTypeCombo, new GBC(3, 0).setInsets(3));
			portDefPanel.add(readerRegionLabel,
					new GBC(2, 1).setAnchor(GridBagConstraints.EAST));
			portDefPanel.add(readerRegionCombo, new GBC(3, 1).setInsets(3));
			portDefPanel.add(readerDescriptionLabel,
					new GBC(4, 0).setAnchor(GridBagConstraints.EAST));
			portDefPanel.add(new JScrollPane(readerDescriptionTxt),
					new GBC(5, 0, 1, 2).setInsets(3));
			portDefPanel.add(readerButtonPanel, new GBC(0, 2, 6, 1).setFillH()
					.setInsets(10, 0, 0, 0));
			tabbedDefPanel.addTab("分站定义", subDeviceDefPanel);
			tabbedDefPanel.addTab("端口定义", portDefPanel);

			add(tabbedDefPanel, new GBC(GBC.FULL));
			initPorts(subDevice);
		}

		public void setSellectedTab(int index) {
			tabbedDefPanel.setSelectedIndex(index);
		}

		public void fillSubDeviceFields(SubDevice subDevice) {
			if (subDevice == null) {
				clearSubDeviceFields();
			} else {
				disableSubDeviceFields();
				subDeviceNameTxt.setText(subDevice.getName());
				subDeviceAddrTxt.setText(subDevice.getDeviceId().toString());
				subDeviceDescriptionTxt.setText(subDevice.getDescription());
				subDeviceTypeCombo.setSelectedItem(new InfoItem(subDevice
						.getTypeId()));
				subDeviceRegionCombo.setSelectedItem(new InfoItem(subDevice
						.getRegionId()));
			}
		}

		public void clearSubDeviceFields() {
			subDeviceNameTxt.setText("");
			subDeviceTypeCombo.setSelectedIndex(0);
			subDeviceRegionCombo.setSelectedIndex(0);
			subDeviceDescriptionTxt.setText("");
		}

		public boolean checkSubDeviceFields() {
			if (subDeviceNameTxt.getText().trim().isEmpty()) {
				JOptionPane.showMessageDialog(SubDeviceDialog.this, "名称不能为空");
				return false;
			}
			if(!Util.checkDigit(subDeviceAddrTxt.getText().trim())){
				JOptionPane.showMessageDialog(SubDeviceDialog.this, "地址码应为整数");
				return false;
			}
			Long id=owner.getSubDeviceDAO().findByDeviceId(Integer.parseInt(subDeviceAddrTxt.getText()));
			if(id!=null&&!id.equals(subDevice.getId())){
				JOptionPane.showMessageDialog(SubDeviceDialog.this, "该地址分站已存在");
				return false;
			}
			return true;
		}

		public void enableSubDeviceFields() {
			subDeviceNameTxt.setEnabled(true);
			subDeviceAddrTxt.setEditable(true);
			subDeviceTypeCombo.setEnabled(true);
			subDeviceRegionCombo.setEnabled(true);
			subDeviceDescriptionTxt.setEnabled(true);
		}

		public void disableSubDeviceFields() {
			subDeviceNameTxt.setEnabled(false);
			subDeviceAddrTxt.setEditable(false);
			subDeviceTypeCombo.setEnabled(false);
			subDeviceRegionCombo.setEnabled(false);
			subDeviceDescriptionTxt.setEnabled(false);
		}

		public void fillReaderFields(Integer portNum) {
			CardReader reader = portMap.get(portNum);
			if (reader == null) {
				clearReaderFields();
				enableReaderFields();
				selPortNum=0;
				portNumValLabel.setText(portNum.toString());
			} else {
				disableReaderFields();
				selPortNum=portNum;
				portNumValLabel.setText(portNum.toString());
				readerNameTxt.setText(reader.getName());
				readerDescriptionTxt.setText(reader.getDescription());
				readerTypeCombo
						.setSelectedItem(ReaderType.values()[reader
						             						.getTypeId().intValue()]);
				readerRegionCombo.setSelectedItem(new InfoItem(reader.getRegionId()));
			}
		}

		public void clearReaderFields() {
			readerNameTxt.setText("");
			readerTypeCombo.setSelectedIndex(0);
			readerRegionCombo.setSelectedIndex(0);
			readerDescriptionTxt.setText("");
		}

		public boolean checkReaderFields() {
			if (readerNameTxt.getText().trim().isEmpty()) {
				JOptionPane.showMessageDialog(SubDeviceDialog.this, "名称不能为空");
				return false;
			}
			return true;
		}

		public void enableReaderFields() {
			readerNameTxt.setEnabled(true);
			readerTypeCombo.setEnabled(true);
			readerRegionCombo.setEnabled(true);
			readerDescriptionTxt.setEnabled(true);
		}

		public void disableReaderFields() {
			readerNameTxt.setEnabled(false);
			readerTypeCombo.setEnabled(false);
			readerRegionCombo.setEnabled(false);
			readerDescriptionTxt.setEnabled(false);
		}

		public void initPorts(SubDevice subDevice) {
			if (subDevice != null) {

				CardReaderFilter filter = new CardReaderFilter();
				filter.setDeviceId(subDevice.getId());
				List<CardReader> readerList = cardReaderDAO
						.queryByFilter(filter);
				for (CardReader cardReader : readerList) {
					portMap.put(cardReader.getPortNum(), cardReader);
				}
				fillReaderFields(1);
			}
		}

		JTextField subDeviceNameTxt;
		JTextField subDeviceAddrTxt;
		JComboBox subDeviceTypeCombo;
		JComboBox subDeviceRegionCombo;
		JTextArea subDeviceDescriptionTxt;
		Integer selPortNum;
		JLabel portNumValLabel;
		JTextField readerNameTxt;
		JComboBox readerTypeCombo;
		JComboBox readerRegionCombo;
		JTextArea readerDescriptionTxt;

		private JTabbedPane tabbedDefPanel;
		private SubDeviceDAO subDeviceDAO;
		private final Integer[] PORT_NUM = { 1, 2, 3, 4, 5, 6, 7, 8 };
		private Map<Integer, CardReader> portMap = new HashMap<Integer, CardReader>();

	}

	class GraphicPanel extends JPanel {
		public GraphicPanel() {
			surfaceX = (PANEL_WIDTH - SURFACE_WIDTH) / 2;
			// surfaceY=(PANEL_HEIGHT-SURFACE_HEIGHT)/2;
			surfaceY = 0;
			initPortList();
			this.addMouseMotionListener(new MouseMotionListener() {

				@Override
				public void mouseMoved(MouseEvent e) {
					// TODO Auto-generated method stub
					drawPortList(e);
				}

				@Override
				public void mouseDragged(MouseEvent e) {
					// TODO Auto-generated method stub

				}
			});
			this.addMouseListener(new MouseListener() {

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
					// TODO Auto-generated method stub
					int i;
					for (i = 0; i < portList.size(); i++) {
						PortCircle pc = portList.get(i);
						if (pc.getCirle().contains(e.getPoint())) {
							// pc.setSelleted(true);
							for (int j = 0; j < portList.size(); j++) {
								portList.get(j).setSelleted(
										j == i ? true : false);
							}
							defPanel.fillReaderFields(i + 1);
							break;
						}

					}
					drawPortList(e);
					setSellectedTab(1);

				}
			});
		}

		public void drawPortList(MouseEvent e) {
			for (int i = 0; i < portList.size(); i++) {
				PortCircle pc = portList.get(i);
				if (pc.isSelleted()) {
					pc.setFillColor(PortCircle.SEL_COLOR);
				} else if (pc.getCirle().contains(e.getPoint())) {
					pc.setFillColor(PortCircle.MOVE_COLOR);
				} else {

					pc.setFillColor(PortCircle.NORMAL_COLOR);
				}
			}
			repaint();
		}

		@Override
		protected void paintComponent(Graphics g) {
			// TODO Auto-generated method stub
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g;
			Rectangle2D.Float surFace = new Rectangle2D.Float(surfaceX,
					surfaceY, SURFACE_WIDTH, SURFACE_HEIGHT);

			g2.setPaint(Color.BLACK);
			g2.draw(surFace);
			g2.setPaint(Color.RED);
			g2.fill(surFace);

			for (int i = 0; i < portList.size(); i++) {
				PortCircle pc = portList.get(i);
				g2.setPaint(pc.getFillColor());
				g2.fill(pc.getCirle());
			}

		}

		public void initPortList() {
			int radius, margingX, margingY;
			if ((SURFACE_WIDTH - (PORT_COL + 1) * PORT_MARGING) / PORT_COL < (SURFACE_HEIGHT - (PORT_ROW + 1)
					* PORT_MARGING)
					/ PORT_ROW) {
				radius = (SURFACE_WIDTH - (PORT_COL + 1) * PORT_MARGING)
						/ PORT_COL;
				margingX = PORT_MARGING;
				margingY = (SURFACE_HEIGHT - radius * PORT_ROW)
						/ (PORT_ROW + 1);
			} else {
				radius = (SURFACE_HEIGHT - (PORT_ROW + 1) * PORT_MARGING)
						/ PORT_ROW;
				margingY = PORT_MARGING;
				margingX = (SURFACE_WIDTH - radius * PORT_COL) / (PORT_COL + 1);
			}
			portList = new ArrayList<PortCircle>();
			int posX = surfaceX + margingX, posY = surfaceY + margingY, initPortNum = 1;
			for (int i = 0; i < PORT_ROW; i++) {
				for (int j = 0; j < PORT_COL; j++) {
					PortCircle pc = new PortCircle();
					pc.setPortNum(initPortNum++);
					pc.setX(posX + (margingX + radius) * j);
					pc.setY(posY + (margingY + radius) * i);
					pc.setRadius(radius);

					pc.createCirle();
					portList.add(pc);
				}
			}

		}

		private List<PortCircle> portList;
		private int surfaceX;
		private int surfaceY;
		private static final int PANEL_WIDTH = SubDeviceDialog.WIDTH;
		private static final int PANEL_HEIGHT = SubDeviceDialog.HEIGHT / 2;
		private static final int SURFACE_WIDTH = 400;
		private static final int SURFACE_HEIGHT = 150;
		private static final int PORT_MARGING = 10;
		public static final int PORT_ROW = 2;
		public static final int PORT_COL = 4;
	}

	public void setSellectedTab(int index) {
		defPanel.setSellectedTab(index);
	}

	public void fillPortInfo() {

	}

	public static final int WIDTH = 600;
	public static final int HEIGHT = 400;

	private SubDevice subDevice;
	private DefPanel defPanel;
	private StaffManagerFrame owner;
	private CardReaderDAO cardReaderDAO;

}

class PortCircle {
	private int portNum;
	private int x;
	private int y;
	private int radius;
	private boolean selleted;
	private Color fillColor;

	private Ellipse2D.Float cirle;

	public PortCircle() {
		this.fillColor = NORMAL_COLOR;

	}

	public int getPortNum() {
		return portNum;
	}

	public void setPortNum(int portNum) {
		this.portNum = portNum;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getRadius() {
		return radius;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

	public boolean isSelleted() {
		return selleted;
	}

	public void setSelleted(boolean selleted) {
		this.selleted = selleted;
	}

	public Color getFillColor() {
		return fillColor;
	}

	public void setFillColor(Color fillColor) {
		this.fillColor = fillColor;
	}

	public Ellipse2D.Float getCirle() {
		return cirle;
	}

	public void setCirle(Ellipse2D.Float cirle) {
		this.cirle = cirle;
	}

	public void createCirle() {
		this.cirle = new Ellipse2D.Float(x, y, radius, radius);
	}

	public static final Color NORMAL_COLOR = Color.WHITE;
	public static final Color SEL_COLOR = Color.GREEN;
	public static final Color MOVE_COLOR = Color.YELLOW;
}
