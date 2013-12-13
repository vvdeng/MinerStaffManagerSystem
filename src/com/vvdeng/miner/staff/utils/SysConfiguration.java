package com.vvdeng.miner.staff.utils;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;



public class SysConfiguration {
	public static final String FILE_PATH = "init.properties";
	public static final String DBCONFIG_FILE_PATH = "db.properties";
	public static final int SYS_STATE_RUNNING = 1;
	public static final int SYS_STATE_REBOOT = 2;
	public static int stationNum=4;
	public static String commPort;
	public static Integer baudRate;
	
	public static Integer isCountLimited;
	public static Integer limitCount;
	public static Integer limitTime;
	public static Integer timeOutTime;
	public static Integer standardWorkTime = 8;
	public static Integer ledContentChangeTime;
	public static String dbUrl;
	public static String dbIp;
	public static Integer dbPort;
	public static String dbUserName;
	public static String dbPwd;
	public static String dbName = "minerlamp";
	public static String backupFileName = "智能管理系统数据备份.dat";
	public static Image sysIcon = null;
	public static String clazz0 = "0点";
	public static String clazz8 = "8点";
	public static String clazz16 = "16点";
	public static String tempDir = "temp/";
	public static int sysState=SYS_STATE_REBOOT;
	public static int timeInteval=10000;//默认十秒
	//开始通讯标志，用户登录成功后才开始系统通信
	public static boolean commStarted=false;
	public static void init() {
		Properties props = PropertiesUtil.getProperties("init.properties");
		Properties dbProps = PropertiesUtil.getProperties("db.properties");
		commPort = props.get("commPort").toString();
		baudRate = new Integer(props.get("baudRate").toString());
	
		isCountLimited = new Integer(props.get("isCountLimited").toString());
	
		limitCount = new Integer(props.get("limitCount").toString());
		limitTime = new Integer(props.get("limitTime").toString());
		timeOutTime = new Integer(props.get("timeOutTime").toString());
		ledContentChangeTime = new Integer(props.get("ledContentChangeTime")
				.toString());
		parse(dbProps.get("hibernate.connection.url").toString());
		dbUserName = dbProps.get("hibernate.connection.username").toString();
		dbPwd = dbProps.get("hibernate.connection.password").toString();
		try {
			sysIcon = ImageIO.read(new File("images/minerlamp.png"));

		} catch (IOException e) {

			e.printStackTrace();
		}
		timeInteval=calTimeInteval(stationNum);
	}

	public static void parse(String url) {
		Pattern pattern = Pattern.compile(".*//(.*):(\\d*)/.*");
		Matcher matcher = pattern.matcher(url);

		if (matcher.find()) {
			dbIp = matcher.group(1);
			dbPort = new Integer(matcher.group(2));
		}
	}

	public static String makeDbUrl(String ip, Integer port) {
		return new StringBuilder().append("jdbc:mysql://").append(ip)
				.append(":").append(port).append("/minerlamp").toString();
	}
	public static int calTimeInteval(int size){
		int result=10000;
		switch (size) {
		case 0:
		case 1:
			result=6000;			
			break;
		case 2:
			result=3000;
			break;
		case 3:
			result=2000;
			break;
		case 4:
			result=1500;
			break;
		case 5:
			result=1200;
		default:
			result=1000; //1秒
			break;
		}
		return result;
	}
	public static void main(String[] args) {
		init();
	}
}
