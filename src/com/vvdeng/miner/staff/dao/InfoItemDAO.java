package com.vvdeng.miner.staff.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Example;

import com.vvdeng.miner.staff.entity.InfoItem;
import com.vvdeng.miner.staff.utils.HibernateUtil;

public class InfoItemDAO {
	public Long save(String name, Integer type,String value) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction = null;
		Long infoItemId = null;
		try {
			transaction = session.beginTransaction();
			InfoItem infoItem = new InfoItem();
			infoItem.setName(name);
			infoItem.setType(type);
			infoItem.setValue(value);
			infoItemId = (Long) session.save(infoItem);
			transaction.commit();
		} catch (HibernateException e) {
			transaction.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
		return infoItemId;
	}
	public Long save(InfoItem infoItem) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction = null;
		Long infoItemId = null;
		try {
			transaction = session.beginTransaction();
			infoItemId = (Long) session.save(infoItem);
			transaction.commit();
		} catch (HibernateException e) {
			transaction.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
		return infoItemId;
	}
	public void update(InfoItem infoItem) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction = null;
	
		try {
			transaction = session.beginTransaction();
			session.update(infoItem);
			transaction.commit();
		} catch (HibernateException e) {
			transaction.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
		
	}
	@SuppressWarnings("unchecked")
	public List<InfoItem> listInfoItems() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction = null;
		List<InfoItem> infoItems = new ArrayList<InfoItem>();
		try {
			transaction = session.beginTransaction();
			infoItems = session.createQuery("from InfoItem").list();
			for (InfoItem infoItem : infoItems) {
				System.out.println(infoItem.getName());
			}
			transaction.commit();

		} catch (HibernateException e) {
			transaction.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
		return infoItems;
	}
	public List<InfoItem> getDepList(){
		Session session =HibernateUtil.getSessionFactory().openSession();
		Transaction transaction=null;
		List<InfoItem> result=new ArrayList<InfoItem>();
		try{
			transaction=session.beginTransaction();
			result=session.createQuery("from InfoItem where type="+InfoItem.TYPE.DEPARTMENET.getVal()+" order by level desc, id asc").list();
		}catch (HibernateException e) {
			transaction.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
		return result;
	}
	public InfoItem findById(Long id){
		InfoItem result=null;
		Session session=HibernateUtil.getSessionFactory().openSession();
		result=(InfoItem)session.get(InfoItem.class, id);
		return result;
	}
	public void delete(InfoItem infoItem) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction = null;
		
		try {
			transaction = session.beginTransaction();
			session.delete(infoItem);
			transaction.commit();
		} catch (HibernateException e) {
			transaction.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
		
	}
	public void updateInfoItem(Long infoItemId, String infoItemName) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction = null;
		try {
			transaction = session.beginTransaction();
			InfoItem infoItem = (InfoItem) session.get(InfoItem.class, infoItemId);
			infoItem.setName(infoItemName);
			transaction.commit();
		} catch (HibernateException e) {
			transaction.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
	}

	public void deleteInfoItem(Long infoItemId) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction = null;
		try {
			transaction = session.beginTransaction();
			InfoItem infoItem = (InfoItem) session.get(InfoItem.class, infoItemId);
			session.delete(infoItem);
			transaction.commit();
		} catch (HibernateException e) {
			transaction.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
	}

	public List<InfoItem> queryByType(InfoItem.TYPE type) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<InfoItem> result=new ArrayList<InfoItem>();

		InfoItem infoItem = new InfoItem();
		infoItem.setType(type.getVal());
		
		Criteria criteria = session.createCriteria(InfoItem.class);
		criteria.add(Example.create(infoItem));
		result = criteria.list();

		session.close();
		return result;
	}
	public List<Object[]> listAllFieldsByType(InfoItem.TYPE type){
		List<Object[]> result=null;
		Session session = HibernateUtil.getSessionFactory().openSession();
		try {
			
			Query query=session.createQuery("select id,value,description from InfoItem where type="+type.getVal());
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
