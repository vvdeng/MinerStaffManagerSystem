package com.vvdeng.miner.staff.dao;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.vvdeng.miner.staff.entity.SubDevice;
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
public class SubDeviceDAO {
	public void save(SubDevice subDevice) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = null;
		try {
			System.out.println("SubDeviceDAO before Save id="+subDevice.getId());
			tx = session.beginTransaction();
			session.saveOrUpdate(subDevice);
			tx.commit();
			System.out.println("SubDeviceDAO after Save id="+subDevice.getId());
		} catch (HibernateException e) {
			// TODO: handle exception
			tx.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
	}
	public SubDevice fingById(Long id) {
		SubDevice result=null;
		Session session = HibernateUtil.getSessionFactory().openSession();
		try {
			
			result=(SubDevice)session.get(SubDevice.class, id);
			
		} catch (HibernateException e) {
			// TODO: handle exception
		
			e.printStackTrace();
		} finally {
			session.close();
		}
		return result;
	}
	public Long findByDeviceId(Integer deviceId) {
		Long result=null;
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction = null;
		try {
			transaction = session.beginTransaction();
			Query query = session.createQuery("select id  from SubDevice where deviceId="+deviceId);
			result =(Long) query.uniqueResult();
			transaction.commit();
		} catch (HibernateException e) {
			transaction.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}

		return result;
	}
	public void delete(SubDevice subDevice) {

		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = null;
		try {
			tx=session.beginTransaction();
			session.delete(subDevice);
			tx.commit();
		} catch (HibernateException e) {
			// TODO: handle exception
			tx.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
		
	}
	public void delete(Long id) {

		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = null;
		try {
			tx=session.beginTransaction();
			SubDevice subDevice=(SubDevice)session.get(SubDevice.class, id);
			session.delete(subDevice);
			tx.commit();
		} catch (HibernateException e) {
			// TODO: handle exception
			tx.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
		
	}
	public List<Object[]> listAllFields(){
		List<Object[]> result=null;
		Session session = HibernateUtil.getSessionFactory().openSession();
		try {
			
			Query query=session.createQuery("select id,name,state from SubDevice");
			result=query.list();
			
		} catch (HibernateException e) {
			// TODO: handle exception
		
			e.printStackTrace();
		} finally {
			session.close();
		}
		return result;
		
	}
	public List<SubDevice> listAll(){
		List<SubDevice> result=null;
		Session session = HibernateUtil.getSessionFactory().openSession();
		try {
			
			Query query=session.createQuery("from SubDevice");
			result=query.list();
			
		} catch (HibernateException e) {
			// TODO: handle exception
		
			e.printStackTrace();
		} finally {
			session.close();
		}
		return result;
		
	}
}
