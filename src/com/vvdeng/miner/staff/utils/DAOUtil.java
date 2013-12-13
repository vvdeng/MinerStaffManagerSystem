package com.vvdeng.miner.staff.utils;

import java.util.List;

import com.vvdeng.miner.constant.PositionEvent;
import com.vvdeng.miner.constant.ReaderType;
import com.vvdeng.miner.constant.StationCommState;
import com.vvdeng.miner.staff.comm.SerialComm;
import com.vvdeng.miner.staff.dao.CardReaderDAO;
import com.vvdeng.miner.staff.dao.PositionLogDAO;
import com.vvdeng.miner.staff.dao.StaffExtraDAO;
import com.vvdeng.miner.staff.dao.StationLogDAO;
import com.vvdeng.miner.staff.dao.SubDeviceDAO;
import com.vvdeng.miner.staff.entity.CardReader;
import com.vvdeng.miner.staff.entity.PositionLog;
import com.vvdeng.miner.staff.entity.StaffExtra;
import com.vvdeng.miner.staff.entity.StationLog;
import com.vvdeng.miner.staff.entity.SubDevice;
import com.vvdeng.miner.staff.filter.StaffExtraFilter;

public class DAOUtil {
	public static PositionLogDAO positionLogDAO;
	public static StationLogDAO stationLogDAO;
	public static SubDeviceDAO subDeviceDAO;
	public static CardReaderDAO cardReaderDAO;
	public static StaffExtraDAO staffExtraDAO;

	public static void init() {
		positionLogDAO = new PositionLogDAO();
		stationLogDAO = new StationLogDAO();
		subDeviceDAO = new SubDeviceDAO();
		cardReaderDAO = new CardReaderDAO();
		staffExtraDAO = new StaffExtraDAO();
		initData();
	}

	public static void initData() {
		GlobalData.normalPosLogList.clear();
		GlobalData.unusualPosLogList.clear();
		GlobalData.entryOrExitStaffExtraList.clear();
		GlobalData.cardNewestPosMap.clear();
		GlobalData.cardReaderMap.clear();
		GlobalData.subDeviceMap.clear();
		GlobalData.staffExtraMap.clear();
//		setStaffInMinerSta();
		List<PositionLog> readerNewestPosList = positionLogDAO.listAll();
		List<SubDevice> subDeviceList = subDeviceDAO.listAll();
		List<CardReader> cardReaderList = cardReaderDAO.listAll();
		List<StaffExtra> staffExtraList = staffExtraDAO.listAll();
		System.out.println("readerNewestPosList size()="
				+ readerNewestPosList.size() + " subDeviceList size()="
				+ subDeviceList.size() + " cardReaderList size()="
				+ cardReaderList.size() + " staffExtraList size()="
				+ staffExtraList.size());
		// List<Object[]>
		// readerNewestPositionList=positionLogDAO.listAllObjects();
		// System.out.println("readerNewestPositionList size()="+readerNewestPositionList.size());
		// for (Object[] objects : readerNewestPositionList) {
		// GlobalData.cardNewestPositionMap.put(new Long(objects[0].toString()),
		// new Long(objects[1].toString()));
		// }
		for (PositionLog positionLog : readerNewestPosList) {
			GlobalData.cardNewestPosMap.put(positionLog.getCardId(),
					positionLog);
		}
		for (CardReader cardReader : cardReaderList) {
			GlobalData.cardReaderMap.put(cardReader.getReaderId(), cardReader);
		}
		for (SubDevice subDevice : subDeviceList) {
			GlobalData.subDeviceMap.put(subDevice.getDeviceId(), subDevice);
		}
		for (StaffExtra staffExtra : staffExtraList) {
			GlobalData.staffExtraMap.put(staffExtra.getCardId(), staffExtra);
		}
	}
	public static void setStaffInMinerSta(){
		StaffExtraFilter filter=new StaffExtraFilter();
		filter.setState(StaffExtra.IN_MINER);
		GlobalData.staffInMinerTotalCount=staffExtraDAO.queryCount(filter);
		filter.setLastArriveTime(Util.getCurrentDateLong());
		GlobalData.staffInMinerTodayCount=staffExtraDAO.queryCount(filter);
	}
	public static void parseState(byte[] oriArr) {
		int arrIndex = 0, readerDataIndex;
		Integer readerId;
		Integer readerType = 0;
		;
		Integer subDeviceId;
		Long arriveTime;
		Integer cardId;
		int totalCount, staffCount, eventId;
		if (oriArr.length <= 8) {
			// 数据不正确
			return;
		}
		StationLog stationLog = new StationLog();
		stationLog.setArriveTime(Util.parseDate(oriArr[0], oriArr[1],
				oriArr[2], oriArr[3], oriArr[4], oriArr[5]).getTime());
		stationLog.setSubDeviceId(new Integer(oriArr[6]));
		stationLog.setEvent(new Integer(oriArr[7]));
		if (stationLog.getEvent() != SerialComm.STATION_OK) {

			System.out.println("station not ready stationState="
					+ stationLog.getEvent());
			return;
		}
		stationLogDAO.save(stationLog);
		GlobalData.msgQueue.add("分站"
				+ stationLog.getSubDeviceId()
				+ StationCommState.values()[stationLog
						.getEvent()].getDesc());
		SubDevice subDevice=GlobalData.subDeviceMap.get(stationLog.getSubDeviceId());
		if(subDevice!=null){
			subDevice.setState(stationLog.getEvent());
		}
		readerDataIndex = 8;

		subDeviceId = stationLog.getSubDeviceId();
		readerId = new Integer(subDeviceId * 100 + oriArr[readerDataIndex]);
		if (GlobalData.cardReaderMap.get(readerId) != null) {
			readerType = GlobalData.cardReaderMap.get(readerId).getTypeId()
					.intValue();
		}
		if (oriArr[readerDataIndex + 1] != SerialComm.READER_TIMEOUT_SYM) {
			arriveTime = (Util.parseDate(oriArr[readerDataIndex + 1],
					oriArr[readerDataIndex + 2], oriArr[readerDataIndex + 3],
					oriArr[readerDataIndex + 4], oriArr[readerDataIndex + 5],
					oriArr[readerDataIndex + 6]).getTime());
			totalCount = oriArr[readerDataIndex + 7];
			staffCount = oriArr[readerDataIndex + 8];
			readerDataIndex = readerDataIndex + 9;
			// PositionLog positionLog =null;
			GlobalData.unusualCardIdSet.clear();
			for (int i = 0; i < totalCount; i++) {
				eventId = oriArr[readerDataIndex++];
				cardId = ((int) oriArr[readerDataIndex++]) * 128;
				cardId += oriArr[readerDataIndex++];
				PositionLog positionLog = new PositionLog();
				positionLog.setArriveTime(arriveTime);
				positionLog.setCardId(cardId);
				positionLog.setSubDeviceId(subDeviceId);
				positionLog.setReaderId(readerId);
				System.out.println("parseState eventId=" + eventId
						+ " eventDesc="
						+ PositionEvent.values()[eventId].getDesc());
				positionLog.setEvent(eventId);
				positionLog.setEventDesc(PositionEvent.values()[eventId]
						.getDesc());
				if (readerType.equals(ReaderType.EXIT.getId())) {
					positionLog.setExist(PositionLog.OUT);
				} else {
					positionLog.setExist(PositionLog.EXIST);

				}/*
				 * if (eventId==PositionEvent.NORMAL.getId()) { //
				 * 还需要判断下分站号和读卡器端口号是否有变化 if
				 * (!GlobalData.cardNewestPosMap.containsKey(cardId) ||
				 * (!GlobalData.cardNewestPosMap.get(cardId)
				 * .getReaderId().equals(readerId) &&
				 * GlobalData.cardNewestPosMap .get(cardId).getArriveTime() <=
				 * arriveTime)) {
				 * if(GlobalData.cardNewestPosMap.containsKey(cardId))
				 * {System.out
				 * .println(" old readerid="+GlobalData.cardNewestPosMap
				 * .get(cardId) .getReaderId()+" new readerId="+readerId);
				 * }GlobalData.cardNewestPosMap.put(cardId, positionLog);
				 * GlobalData.unusualPosLogList.add(positionLog); } } else {
				 * System.out.println(" not normal");
				 * GlobalData.unusualPosLogList.add(positionLog); }
				 */
				if (eventId == PositionEvent.NORMAL.getId()) {
					StaffExtra staffExtra = GlobalData.staffExtraMap
							.get(cardId);
					if (staffExtra != null) {
						if (staffExtra.getState().equals(StaffExtra.ON_GROUND)) {
							if (readerType.equals(ReaderType.ENTRY.getId())) {

								staffExtra.setLastArriveTime(arriveTime);
								staffExtra.setState(StaffExtra.IN_MINER);
								GlobalData.entryOrExitStaffExtraList
										.add(staffExtra);
							}
						} else {
							if (readerType.equals(ReaderType.EXIT.getId())) {

								staffExtra.setLastLeaveTime(arriveTime);
								staffExtra.setState(StaffExtra.ON_GROUND);
								GlobalData.entryOrExitStaffExtraList
										.add(staffExtra);
							}
						}
					}
					System.out.println("GlobalData.cardNewestPosMap.containsKey"+GlobalData.cardNewestPosMap.containsKey(cardId)+" cardId="+cardId);
					if (GlobalData.cardNewestPosMap.containsKey(cardId)) {

						PositionLog oldPositionLog = GlobalData.cardNewestPosMap
								.get(cardId);
						System.out.println(" old readerid="
								+ oldPositionLog.getReaderId()
								+ " new readerId=" + readerId);
						if (oldPositionLog.getReaderId().equals(readerId)) {
							// 停留时间过久判断
						} else {
							if (oldPositionLog.getArriveTime() < arriveTime) {
								oldPositionLog.setExist(PositionLog.OUT);
								GlobalData.normalPosLogList.add(oldPositionLog);
								GlobalData.normalPosLogList.add(positionLog);
								GlobalData.cardNewestPosMap.put(cardId,
										positionLog);
							}
						}
					} else {
						GlobalData.normalPosLogList.add(positionLog);
						GlobalData.cardNewestPosMap.put(cardId, positionLog);
					}

				} else {
					System.out.println(" not normal");
					if (!GlobalData.unusualCardIdSet.contains(cardId)) {
						GlobalData.unusualCardIdSet.add(cardId);
						GlobalData.unusualPosLogList.add(positionLog);
						GlobalData.msgQueue.add("标识卡"
								+positionLog.getCardId()
								+ PositionEvent.values()[positionLog.getEvent()].getDesc());

					}

				}
			}
		} else {
		}
		

	}
}
