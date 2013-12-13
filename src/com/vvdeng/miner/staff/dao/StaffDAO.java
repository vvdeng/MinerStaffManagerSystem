package com.vvdeng.miner.staff.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.util.HSSFColor.TAN;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.vvdeng.miner.staff.entity.Staff;
import com.vvdeng.miner.staff.entity.StaffExtra;
import com.vvdeng.miner.staff.filter.StaffFilter;
import com.vvdeng.miner.staff.utils.GlobalData;
import com.vvdeng.miner.staff.utils.HibernateUtil;

public class StaffDAO {
	public StaffDAO() {
		staffExtraDAO = new StaffExtraDAO();
	}

	public Long save(Staff staff) {

		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction = null;
		Long staffId = null;
		try {
			transaction = session.beginTransaction();
			staffId = (Long) session.save(staff);
			
			if (!GlobalData.staffExtraMap.containsKey(staff.getCardId())) {
				StaffExtra staffExtra = new StaffExtra();
				staffExtra.setStaffId(staffId);
				staffExtra.setCardId(staff.getCardId());
				staffExtraDAO.save(staffExtra);
				GlobalData.staffExtraMap.put(staff.getCardId(), staffExtra);
			}
			transaction.commit();
		} catch (HibernateException e) {
			transaction.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
		return staffId;
	}

	public void update(Staff staff) {

		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction = null;

		try {
			transaction = session.beginTransaction();
			// session.merge(staff);
			session.update(staff);
			if (!GlobalData.staffExtraMap.containsKey(staff.getCardId())) {
				StaffExtra staffExtra = new StaffExtra();
				staffExtra.setStaffId(staff.getId());
				staffExtra.setCardId(staff.getCardId());
				staffExtraDAO.save(staffExtra);
				GlobalData.staffExtraMap.put(staff.getCardId(), staffExtra);
			}
			transaction.commit();
		} catch (HibernateException e) {
			transaction.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}

	}

	@SuppressWarnings("unchecked")
	public List<Object[]> listStaffFields() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction = null;
		List<Object[]> staffList = new ArrayList<Object[]>();
		try {
			// transaction = session.beginTransaction();
			staffList = session
					.createQuery(
							"select id,cardId, workId,name,department,profession,clazz,phone,address from Staff")
					.list();

			// transaction.commit();

		} catch (HibernateException e) {
			// transaction.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
		return staffList;
	}

	public Staff finbById(Long id) {
		Staff result = null;
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction = null;
		try {
			transaction = session.beginTransaction();
			result = (Staff) session.get(Staff.class, id);
			transaction.commit();
		} catch (HibernateException e) {
			transaction.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
		return result;
	}
	public Long findByCardId(Integer cardId) {
		Long result=null;
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction = null;
		try {
			transaction = session.beginTransaction();
			Query query = session.createQuery("select id  from Staff where cardId="+cardId);
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
	public List<Object[]> queryStaffInfo(StaffFilter filter) {
		List<Object[]> result = new ArrayList<Object[]>();
		StringBuilder clause = new StringBuilder(
				"select id,cardId, workId,name,sex,birthDate,department,profession,clazz,phone,address from Staff ");
		StringBuilder condition = new StringBuilder();
		if (filter.getWorkId() != null) {
			condition.append(" and workId=" + filter.getWorkId());
		}
		if (filter.getName() != null) {
			condition.append(" and name like '%" + filter.getName() + "%'");
		}
		if (filter.getDepId() != null) {
			switch (filter.getDepLevel()) {
			case 1:
				condition.append(" and dep1Id=" + filter.getDepId());
				break;
			case 2:
				condition.append(" and dep2Id=" + filter.getDepId());
				break;
			case 3:
				condition.append(" and dep3Id=" + filter.getDepId());
				break;
			default:
				break;
			}
		}
		if (filter.getWorkTypeId() != null) {
			condition.append(" and professionId=" + filter.getWorkTypeId());
		}
		if (filter.getClazzId() != null) {
			condition.append(" and clazzId=" + filter.getClazzId());
		}
		if (condition.length() > 0) {
			clause.append(condition.replace(0, 4, " where"));// and �滻Ϊ where
		}
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction = null;
		try {
			transaction = session.beginTransaction();
			Query query = session.createQuery(clause.toString());
			result = query.list();
			transaction.commit();
		} catch (HibernateException e) {
			transaction.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	public List<Staff> listStaffs() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction = null;
		List<Staff> staffs = new ArrayList<Staff>();
		try {
			transaction = session.beginTransaction();
			staffs = session.createQuery("from Staff").list();
			for (Staff staff : staffs) {
				System.out.println(staff.getName());
			}
			transaction.commit();

		} catch (HibernateException e) {
			transaction.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
		return staffs;
	}

	public void deleteStaff(Long staffId) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction = null;
		try {
			transaction = session.beginTransaction();
			Staff staff = (Staff) session.get(Staff.class, staffId);
			session.delete(staff);
			transaction.commit();
		} catch (HibernateException e) {
			transaction.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
	}

	public void deleteStaff(Long rackId, Long lampNo, Integer state,
			Integer action) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction = null;
		try {
			transaction = session.beginTransaction();
			Query query = session
					.createQuery("delete from Staff where rackId=:rackId and lampNo=:lampNo");

			query.setLong("rackId", rackId);
			query.setLong("lampNo", lampNo);
			query.executeUpdate();

			transaction.commit();
		} catch (HibernateException hibernateException) {
			transaction.rollback();
			hibernateException.printStackTrace();
		} finally {
			session.close();
		}

	}

	public void deleteStaffInCurrentSession(Long rackId, Long lampNo,
			Session session) {

		Query query = session
				.createQuery("delete from Staff where rackId=:rackId and lampNo=:lampNo");

		query.setLong("rackId", rackId);
		query.setLong("lampNo", lampNo);
		query.executeUpdate();

	}

	public List<Object[]> queryStaffInfoModel(Integer modelType, Long workId,
			String name, Long professionId, Long departmentId, Long rackId,
			Long lampNo) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		StringBuilder sb = new StringBuilder(
				"select staff.workId,staff.name,staff.rackId,staff.lampNo,staff.profession,staff.department from Staff staff ");

		List<Object[]> result = new ArrayList<Object[]>();
		StringBuilder querySb = new StringBuilder();
		if (workId != null && workId != 0L) {
			querySb.append("staff.workId=" + workId);
		}
		if (name != null && !name.trim().isEmpty()) {
			if (querySb.length() > 0) {
				querySb.append(" and ");
			}
			querySb.append("staff.name='" + name + "'");
		}
		if (professionId != null && professionId != 0L) {
			if (querySb.length() > 0) {
				querySb.append(" and ");
			}
			querySb.append("staff.professionId=" + professionId);
		}
		if (departmentId != null && departmentId != 0L) {
			if (querySb.length() > 0) {
				querySb.append(" and ");
			}
			querySb.append("staff.departmentId=" + departmentId);
		}
		if (rackId != null && rackId != 0L) {
			if (querySb.length() > 0) {
				querySb.append(" and ");
			}
			querySb.append("staff.rackId=" + rackId);
		}
		if (lampNo != null && lampNo != 0L) {
			if (querySb.length() > 0) {
				querySb.append(" and ");
			}
			querySb.append("staff.lampNo=" + lampNo);
		}

		if (querySb.length() > 0) {
			sb.append(" where ").append(querySb);
		}
		Query query = session.createQuery(sb.toString());
		result = query.list();
		session.close();
		return result;
	}

	public void deleteAllInCurrentSession(Session session) {
		Query query = session.createQuery("delete from Staff");
		query.executeUpdate();
	}

	private StaffExtraDAO staffExtraDAO;
}
