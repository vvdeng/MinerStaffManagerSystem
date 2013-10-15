package com.vvdeng.miner.staff.utils;

import java.awt.GridBagConstraints;
import java.awt.Point;
import java.awt.Window;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.vvdeng.miner.staff.dao.InfoItemDAO;
import com.vvdeng.miner.staff.entity.InfoItem;
import com.vvdeng.miner.staff.entity.User;
import com.vvdeng.miner.staff.ui.GBC;

public class Util {
	public static Pattern pattern=Pattern.compile("\\d+");
	public static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	public static DateFormat dateTimeFormat = new SimpleDateFormat(
			"yyyy-MM-dd hh:mm:ss");
	public static DateFormat dateTimeFormat4CreateFile = new SimpleDateFormat(
			"yyyyMMddhhmmss");
	private static List<InfoItem> depList;
	public static String formatDate(Date date) {
		if (date == null) {
			return null;
		}
		return dateFormat.format(date);
	}

	public static String formatDateTime(Date date) {
		return formatDateTime(date, dateTimeFormat);
	}

	public static String formatDateTime(Date date, DateFormat df) {
		if (date == null) {
			return null;
		}
		return df.format(date);
	}

	public static Date parseDate(String dateStr) {
		Date date = new Date();
		if (dateStr != null) {
			try {
				date = dateFormat.parse(dateStr);
			} catch (ParseException e) {

				e.printStackTrace();
			}
		}
		return date;
	}

	public static Point calculatePosition(Window parent, JDialog dialog) {
		int x = parent.getX() + (parent.getWidth() - dialog.getWidth()) / 2;
		int y = parent.getY() + (parent.getHeight() - dialog.getHeight()) / 2;
		Point p = new Point(x, y);
		return p;

	}

	public static JButton makeButton(Action action) {
		JButton button = new JButton(action);
		return button;
	}

	public static JButton makePrivButton(Action action, User user) {
		JButton button = new JButton(action);
		if (user == null || user.getPriv() == User.READ_ONLY) {
			button.setEnabled(false);
		}
		return button;
	}

	public static GBC fillParentPanel() {
		return new GBC(0, 0).setWeight(100, 100).setFill(
				GridBagConstraints.BOTH);
	}

	public static List<Long> makeArray(Long num) {
		List<Long> numList = new ArrayList<Long>();
		for (int i = 1; i <= num; i++) {
			numList.add(new Long(i));
		}
		return numList;
	}

	public static int backup(String url, String name, String pwd,
			String dbName, String file) throws IOException,
			InterruptedException {
		// “mysqldump -u username -pPassword --opt database_name >
		// direction/backup_name.sql”
		int result = -1;
		String str = "mysqldump -u " + name + " -p" + pwd + " --opt   "
				+ dbName + " >  \"" + file + "\"";
		System.out.println(str);
		Runtime rt = Runtime.getRuntime();
		Process process = rt.exec("cmd /c" + str);
		result = process.waitFor();
		System.out.println(result);
		return result;
	}
	public static boolean checkDigit(String str){
		Matcher matcher=pattern.matcher(str);
		if(matcher.matches()){
		 return true;
		}
		else{
			return false;
		}
	}
	public static int load(String url, String name, String pwd, String dbName,
			String file) throws IOException, InterruptedException {
		int result = -1;
		// “mysql -u Username -pPassword database_name < back_up_dir ”
		String str = "mysql -u " + name + " -p" + pwd + " " + dbName
				+ "  <  \"" + file + "\"";
		System.out.println(str);
		Runtime rt = Runtime.getRuntime();
		Process process = rt.exec("cmd /c" + str);
		result = process.waitFor();
		System.out.println(result);

		return result;

	}

	public static NumberFormat getIntegerNumberFormat() {
		NumberFormat nf = NumberFormat.getIntegerInstance();
		nf.setGroupingUsed(false);
		return nf;
	}

	public static void createExcelFile(String fileName, String title,
			TableModel cellContentModel) {

		try {
			if (cellContentModel == null || cellContentModel.getRowCount() == 0) {
				return;
			}
			HSSFWorkbook wb = new HSSFWorkbook();
			HSSFSheet sheet = wb.createSheet(title);
			for (int i = 0; i < cellContentModel.getColumnCount(); i++) {
				String columnName = cellContentModel.getColumnName(i);
				if (columnName.contains("日期") || columnName.contains("时间")) {
					sheet.setColumnWidth(i, 5000);
				}
			}
			HSSFRow row = sheet.createRow(0);
			for (int i = 0; i < cellContentModel.getColumnCount(); i++) {
				createCell(wb, row, i, cellContentModel.getColumnName(i));
			}
			for (int i = 0; i < cellContentModel.getRowCount(); i++) {
				row = sheet.createRow(i + 1);
				for (int j = 0; j < cellContentModel.getColumnCount(); j++) {
					Object value = cellContentModel.getValueAt(i, j);
					if (value == null) {
						value = new String();
					}
					if (Date.class.isInstance(value)) {
						value = formatDateTime((Date) value);
					}
					createCell(wb, row, j, value.toString());
				}
			}
			File file = new File(SysConfiguration.tempDir + fileName
					+ formatDateTime(new Date(), dateTimeFormat4CreateFile)
					+ ".xls");
			FileOutputStream fileOut;
			fileOut = new FileOutputStream(file);
			wb.write(fileOut);
			fileOut.close();
			Runtime runtime = Runtime.getRuntime();
			runtime.exec("cmd /c \"" + file.getAbsolutePath() + "\"");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void createCell(HSSFWorkbook wb, HSSFRow row, int col,
			String val) {
		HSSFCell cell = row.createCell(col);
		cell.setCellValue(val);
		HSSFCellStyle cellstyle = wb.createCellStyle();
		cellstyle.setAlignment(HSSFCellStyle.ALIGN_CENTER_SELECTION);
		cellstyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		// cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
		cell.setCellStyle(cellstyle);
	}
	public static DefaultMutableTreeNode getDepTree(){
		DefaultMutableTreeNode root=new DefaultMutableTreeNode("选择");
		if(depList==null){
			depList=new InfoItemDAO().getDepList();
		}
		System.out.println("depList.size()="+depList.size());
		Map<Long,ArrayList<DefaultMutableTreeNode>> depNodeListMap=new HashMap<Long,ArrayList<DefaultMutableTreeNode>>();
		for (InfoItem dep : depList) {
			System.out.println("depId="+dep.getId());
			DefaultMutableTreeNode depNode=new DefaultMutableTreeNode(dep);
			if(dep.getParentId()!=null&&dep.getParentId()!=0L){
				ArrayList depNodeList=depNodeListMap.get(dep.getParentId());
				if(depNodeList==null){
					depNodeList=new ArrayList<InfoItem>();
					depNodeListMap.put(dep.getParentId(), depNodeList);
				}
				depNodeList.add(depNode);
			}
			else{
				root=depNode;
			}
			ArrayList<DefaultMutableTreeNode> chilrenNodeList=depNodeListMap.get(dep.getId());
			if(chilrenNodeList!=null){
				for (DefaultMutableTreeNode childNode : chilrenNodeList) {
					depNode.add(childNode);
				}
			}
			
		}
		
		return root;
	}
	public static void clearDepTree(){
		depList=null;
	}
	public static byte[] getCurrentTimeBytes(){
		byte [] result=new byte[8];
		Calendar c=Calendar.getInstance();
		result[0]=(byte)(c.get(Calendar.YEAR)%100);
		result[1]=(byte)(c.get(Calendar.MONTH)+1);//calendar 月份从零开始
		result[2]=(byte)(c.get(Calendar.DAY_OF_MONTH)&0xff);
		result[3]=(byte)(c.get(Calendar.WEEK_OF_MONTH)&0xff);
		result[4]=(byte)(c.get(Calendar.HOUR_OF_DAY)&0xff);
		result[5]=(byte)(c.get(Calendar.MINUTE)&0xff);
		result[6]=(byte)(c.get(Calendar.SECOND)&0xff);
	//	result[7]=(byte)(c.get(Calendar.WEEK_OF_MONTH)&0xff);
		result[7]=0;
		return result;
	}
}
