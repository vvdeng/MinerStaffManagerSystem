package com.vvdeng.miner.staff.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vvdeng.miner.staff.dao.InfoItemDAO;
import com.vvdeng.miner.staff.entity.InfoItem;

public class InfoItemUtil {
	private static InfoItemDAO infoItemDAO=new InfoItemDAO();
	private static Map<Integer,List<InfoItem>> itemListMap=new HashMap<Integer, List<InfoItem>>();
	public static List<InfoItem> getInfoItem(InfoItem.TYPE type){
		List<InfoItem> result=itemListMap.get(type);
		if(result==null){
			result=infoItemDAO.queryByType(type);
			itemListMap.put(type.getVal(), result);
		}
		return result;
	}
}
