package com.vvdeng.miner.staff.dao;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.vvdeng.miner.staff.entity.PositionLog;
import com.vvdeng.miner.staff.filter.PositionLogFilter;
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
public class PositionLogDAO {
	public void save(PositionLog positionLog) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			session.save(positionLog);
			tx.commit();
		} catch (HibernateException e) {
			// TODO: handle exception
			tx.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
	}

	public void saveList(List<PositionLog> positionLogList) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
		/*	for (int i = positionLogList.size()-1; i >= 0; i--) {
				PositionLog positionLog=positionLogList.remove(i);
				session.save(positionLog);
			}
		*/
			for (PositionLog positionLog : positionLogList) {
				session.saveOrUpdate(positionLog);
			}
			positionLogList.clear();
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

/*	public List<Object[]> listAllObjects() {
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
*/	public List<Object[]> listRealtimeReaders() {
		List<Object[]> result = null;
		Session session = HibernateUtil.getSessionFactory().openSession();
		try {

			Query query = session
					.createSQLQuery("SELECT  reader_id ,COUNT(*) FROM position_log pl WHERE  exist=1 and event=0 GROUP BY reader_id ;");

			result = query.list();

		} catch (HibernateException e) {
			// TODO: handle exception

			e.printStackTrace();
		} finally {
			session.close();
		}
		return result;

	}
	public List<PositionLog> listAll() {
		List<PositionLog> result = null;
		Session session = HibernateUtil.getSessionFactory().openSession();
		try {

			Query query = session
					.createQuery("FROM PositionLog as l1 WHERE  l1.event=0 and l1.exist=1 and l1.arriveTime=(SELECT MAX(arriveTime) FROM PositionLog  as l2 WHERE l1.cardId=l2.cardId and l2.event=0 and l2.exist=1  )");

			result = query.list();

		} catch (HibernateException e) {
			// TODO: handle exception

			e.printStackTrace();
		} finally {
			session.close();
		}
		return result;

	}
	public List<Object[]> queryByCondition(PositionLogFilter filter){
		List<Object[]> result=null;
		Session session = HibernateUtil.getSessionFactory().openSession();
		String hqlStr="select cardId,readerId,arriveTime,eventDesc from PositionLog";
		StringBuilder hqlSB=new StringBuilder();
		if(filter.getCardId()!=null){
			hqlSB.append(" and cardId="+filter.getCardId());
		}
		if(filter.getReaderId()!=null){
			hqlSB.append(" and readerId="+filter.getReaderId());
		}
		if(filter.getStartTime()!=null){
			hqlSB.append(" and arriveTime>="+filter.getStartTime());
		}
		if(filter.getEndTime()!=null){
			hqlSB.append(" and arriveTime<="+filter.getEndTime());
		}
		if(filter.getEvent()!=null){
			hqlSB.append(" and event="+filter.getEvent());
		}
		if(filter.getExist()!=null){
			hqlSB.append(" and exist="+filter.getExist());
		}
		if(hqlSB.length()>0){
			hqlSB.replace(0, 4, " where");
		}
		hqlSB.append(" order by arriveTime asc");
		hqlStr+=hqlSB.toString();
		
		try {

			Query query = session
					.createQuery(hqlStr);

			result = query.list();

		} catch (HibernateException e) {
			// TODO: handle exception

			e.printStackTrace();
		} finally {
			session.close();
		}
		return result;
	}
	public List<Object[]> queryByConditionJoinStaff(PositionLogFilter filter){
		List<Object[]> result=null;
		Session session = HibernateUtil.getSessionFactory().openSession();
		String sqlStr=" SELECT p.card_id,s.name,s.department,p.arrive_time,p.event_desc FROM position_log AS p LEFT OUTER JOIN staff AS s ON p.card_id=s.card_id ";
		StringBuilder sqlSB=new StringBuilder();
		if(filter.getCardId()!=null){
			sqlSB.append(" and p.card_id="+filter.getCardId());
		}
		if(filter.getReaderId()!=null){
			sqlSB.append(" and p.reader_id="+filter.getReaderId());
		}
		if(filter.getStartTime()!=null){
			sqlSB.append(" and p.arrive_time>="+filter.getStartTime());
		}
		if(filter.getEndTime()!=null){
			sqlSB.append(" and p.arrive_time<="+filter.getEndTime());
		}
		if(filter.getEvent()!=null){
			sqlSB.append(" and p.event="+filter.getEvent());
		}
		if(filter.getExist()!=null){
			sqlSB.append(" and p.exist="+filter.getExist());
		}
	
		if(sqlSB.length()>0){
			sqlSB.replace(0, 4, " where");
		}

		sqlSB.append(" order by p.arrive_time asc");
		sqlStr+=sqlSB.toString();
		
		try {

			Query query = session
					.createSQLQuery(sqlStr);

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
