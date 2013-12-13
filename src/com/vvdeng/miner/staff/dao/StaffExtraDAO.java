package com.vvdeng.miner.staff.dao;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.vvdeng.miner.staff.entity.PositionLog;
import com.vvdeng.miner.staff.entity.StaffExtra;
import com.vvdeng.miner.staff.filter.StaffExtraFilter;
import com.vvdeng.miner.staff.utils.HibernateUtil;

/* model
 Session session=HibernateUtil.getSessionFactory().openSession();
 Transaction tx=null;
 try {
 tx=session.beginTransaction();

 tx.commit();
 } catch (HibernateException e) {
 // TODO: handle exception
 tx.rollback();
 e.printStackTrace();
 }
 finally{
 session.close();
 }
 */
public class StaffExtraDAO {
	public void save(StaffExtra staffExtra) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			session.saveOrUpdate(staffExtra);
			tx.commit();
		} catch (HibernateException e) {
			// TODO: handle exception
			tx.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
	}

	public void update(StaffExtra staffExtra) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			session.update(staffExtra);
			tx.commit();
		} catch (HibernateException e) {
			// TODO: handle exception
			tx.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
	}

	public StaffExtra fingById(Long id) {
		StaffExtra result = null;
		Session session = HibernateUtil.getSessionFactory().openSession();
		result = (StaffExtra) session.get(StaffExtra.class, id);
		session.close();
		return result;
	}

	public void delete(Long id) {

		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			StaffExtra staffExtra = (StaffExtra) session.get(
					StaffExtra.class, id);
			session.delete(staffExtra);
			tx.commit();
		} catch (HibernateException e) {
			// TODO: handle exception
			tx.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}

	}

	public List<StaffExtra> listAll() {
		List<StaffExtra> result = null;
		Session session = HibernateUtil.getSessionFactory().openSession();
		try {

			Query query = session.createQuery("from StaffExtra");
			result = query.list();

		} catch (HibernateException e) {
			// TODO: handle exception

			e.printStackTrace();
		} finally {
			session.close();
		}
		return result;

	}
	public Long queryCount(StaffExtraFilter filter) {
		Long result = null;
		String hqlStr="select count(*) from StaffExtra";
		StringBuilder conditionSb=new StringBuilder();
		if(filter.getState()!=null){
			conditionSb.append(" and state="+filter.getState());
		}
		if(filter.getLastArriveTime()!=null){
			conditionSb.append(" and lastArriveTime>="+filter.getLastArriveTime());
		}
		if(conditionSb.length()>0){
			conditionSb.replace(0, 4, " where");
		}
		hqlStr+=conditionSb.toString();
		Session session = HibernateUtil.getSessionFactory().openSession();
		try {

			Query query = session.createQuery(hqlStr);
			result =(Long) query.uniqueResult();

		} catch (HibernateException e) {
			// TODO: handle exception

			e.printStackTrace();
		} finally {
			session.close();
		}
		return result;

	}


	public void updateAll(List<StaffExtra> staffExtraList) {

		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			for (StaffExtra staffExtra : staffExtraList) {

				
				session.update(staffExtra);

			}
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
