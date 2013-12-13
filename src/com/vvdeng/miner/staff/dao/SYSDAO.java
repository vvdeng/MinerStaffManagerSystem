package com.vvdeng.miner.staff.dao;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.vvdeng.miner.staff.utils.DAOUtil;
import com.vvdeng.miner.staff.utils.HibernateUtil;

public class SYSDAO {
	private static SYSDAO sysDAO=new SYSDAO();
	public static SYSDAO getSYSDAO(){
		return sysDAO;
	}
	public void clearAllData(){
		
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx=null;
		try {
			tx=session.beginTransaction();
			Query query=session.createSQLQuery("delete from position_log");
			query.executeUpdate();
			query=session.createSQLQuery("delete from station_log");
			query.executeUpdate();
			query=session.createSQLQuery("update staff_extra set state=0 and last_arrive_time=null and last_leave_time=null");
			query.executeUpdate();
			
			tx.commit();
			
		} catch (HibernateException e) {
			// TODO: handle exception
			tx.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
		
		
	}
}
