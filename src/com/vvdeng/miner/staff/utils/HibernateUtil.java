package com.vvdeng.miner.staff.utils;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;

import com.vvdeng.miner.staff.entity.CardNewestPosition;
import com.vvdeng.miner.staff.entity.CardReader;
import com.vvdeng.miner.staff.entity.InfoItem;
import com.vvdeng.miner.staff.entity.PositionLog;
import com.vvdeng.miner.staff.entity.Staff;
import com.vvdeng.miner.staff.entity.StaffExtra;
import com.vvdeng.miner.staff.entity.StationLog;
import com.vvdeng.miner.staff.entity.SubDevice;
import com.vvdeng.miner.staff.entity.User;



public class HibernateUtil {
	private static SessionFactory sessionFactory;
	static {
		try {
			sessionFactory = new AnnotationConfiguration()
					.addProperties(
							PropertiesUtil
									.getProperties(SysConfiguration.DBCONFIG_FILE_PATH))
					.configure().addPackage("com.vv.miner.staff.entity")
					.addAnnotatedClass(User.class)
					.addAnnotatedClass(Staff.class)
					.addAnnotatedClass(InfoItem.class)
					.addAnnotatedClass(SubDevice.class)
					.addAnnotatedClass(CardReader.class)
					.addAnnotatedClass(PositionLog.class)
					.addAnnotatedClass(CardNewestPosition.class)
					.addAnnotatedClass(StationLog.class)
					.addAnnotatedClass(StaffExtra.class)
					.buildSessionFactory();
			;
		} catch (Throwable ex) {
			throw new ExceptionInInitializerError(ex);
		}
	}

	public static SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public static void shutdown() {
		getSessionFactory().close();
	}
}