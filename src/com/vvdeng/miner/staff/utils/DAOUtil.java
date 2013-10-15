package com.vvdeng.miner.staff.utils;

import java.util.List;

import com.vvdeng.miner.staff.dao.PositionLogDAO;

public class DAOUtil {
	public static PositionLogDAO positionLogDAO;
	public static void init(){
		positionLogDAO=new PositionLogDAO();
		initData();
	}
	public static void initData(){
		List<Object[]> readerNewestPositionList=positionLogDAO.listAll();
		System.out.println("readerNewestPositionList size()="+readerNewestPositionList.size());
		for (Object[] objects : readerNewestPositionList) {
			GlobalData.cardNewestPositionMap.put(new Long(objects[0].toString()), new Long(objects[1].toString()));
		}
	}
}
