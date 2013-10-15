package com.vvdeng.miner.staff.dao;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.vvdeng.miner.staff.entity.CardReader;
import com.vvdeng.miner.staff.filter.CardReaderFilter;
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
public class CardReaderDAO {
	public void save(CardReader cardReader) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			session.save(cardReader);
			tx.commit();
		} catch (HibernateException e) {
			// TODO: handle exception
			tx.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
	}
	public CardReader fingById(Long id) {
		CardReader result=null;
		Session session = HibernateUtil.getSessionFactory().openSession();
		try {
			
			result=(CardReader)session.get(CardReader.class, id);
			
		} catch (HibernateException e) {
			// TODO: handle exception
		
			e.printStackTrace();
		} finally {
			session.close();
		}
		return result;
	}
	public void delete(Long id) {

		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = null;
		try {
			tx=session.beginTransaction();
			CardReader cardReader=(CardReader)session.get(CardReader.class, id);
			session.delete(cardReader);
			tx.commit();
		} catch (HibernateException e) {
			// TODO: handle exception
			tx.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
		
	}
	public void delete(CardReader cardReader) {

		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = null;
		try {
			tx=session.beginTransaction();
			session.delete(cardReader);
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
			
			Query query=session.createQuery("select id,name,state from CardReader");
			result=query.list();
			
		} catch (HibernateException e) {
			// TODO: handle exception
		
			e.printStackTrace();
		} finally {
			session.close();
		}
		return result;
		
	}
	public List<CardReader> queryByFilter(CardReaderFilter filter){
		List<CardReader> result=null;
		StringBuilder clause=new StringBuilder("from CardReader ");
		StringBuilder condition=new StringBuilder();
		if(filter!=null){
			if(filter.getDeviceId()!=null);
			condition.append(" and deviceId="+filter.getDeviceId());
		}
		if(condition.length()>0){
			clause.append(condition.replace(0, 4, " where"));
		}
		Session session = HibernateUtil.getSessionFactory().openSession();
		try {
			
			Query query=session.createQuery(clause.toString());
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
