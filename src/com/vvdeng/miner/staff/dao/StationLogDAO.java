package com.vvdeng.miner.staff.dao;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.vvdeng.miner.staff.entity.PositionLog;
import com.vvdeng.miner.staff.entity.StationLog;
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
public class StationLogDAO {
	public void save(StationLog stationLog) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			session.save(stationLog);
			tx.commit();
		} catch (HibernateException e) {
			// TODO: handle exception
			tx.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
	}

	public void update(PositionLog positionLog) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			session.update(positionLog);
			tx.commit();
		} catch (HibernateException e) {
			// TODO: handle exception
			tx.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
	}

	public PositionLog fingById(Long id) {
		PositionLog result = null;
		Session session = HibernateUtil.getSessionFactory().openSession();
		result = (PositionLog) session.get(PositionLog.class, id);
		session.close();
		return result;
	}

	public void delete(Long id) {

		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			PositionLog positionLog = (PositionLog) session.get(
					PositionLog.class, id);
			session.delete(positionLog);
			tx.commit();
		} catch (HibernateException e) {
			// TODO: handle exception
			tx.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}

	}

	public List<Object[]> listAllFields() {
		List<Object[]> result = null;
		Session session = HibernateUtil.getSessionFactory().openSession();
		try {

			Query query = session
					.createQuery("select id,name,state from PositionLog");
			result = query.list();

		} catch (HibernateException e) {
			// TODO: handle exception

			e.printStackTrace();
		} finally {
			session.close();
		}
		return result;

	}

	public List<Object[]> listAll() {
		List<Object[]> result = null;
		Session session = HibernateUtil.getSessionFactory().openSession();
		try {

			Query query = session
					.createSQLQuery("SELECT  card_id,reader_id FROM position_log l1 WHERE  arrive_time=(SELECT MAX(arrive_time) FROM position_log l2 WHERE l1.card_id=l2.card_id );");

			result = query.list();

		} catch (HibernateException e) {
			// TODO: handle exception

			e.printStackTrace();
		} finally {
			session.close();
		}
		return result;

	}
}
