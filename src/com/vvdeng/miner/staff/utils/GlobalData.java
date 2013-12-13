package com.vvdeng.miner.staff.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import javax.swing.Timer;

import com.vvdeng.miner.staff.comm.CmdObject;
import com.vvdeng.miner.staff.entity.CardReader;
import com.vvdeng.miner.staff.entity.PositionLog;
import com.vvdeng.miner.staff.entity.StaffExtra;
import com.vvdeng.miner.staff.entity.SubDevice;

public class GlobalData {
	public static int commStateNotNothingCount=0;
	public static int currentStationIndex=0;
	public static int currentBgActivityState;
	public static long staffInMinerTotalCount=0;
	public static long staffInMinerTodayCount=0;
//	public static Map<Long,Long> cardNewestPositionMap=new HashMap<Long,Long>();
	public static Map<Integer,StaffExtra> staffExtraMap=new HashMap<Integer, StaffExtra>();
	public static Map<Integer,SubDevice> subDeviceMap=new HashMap<Integer,SubDevice>();
	public static Map<Integer,CardReader> cardReaderMap=new HashMap<Integer,CardReader>();
	public static Map<Integer,PositionLog> cardNewestPosMap=new HashMap<Integer,PositionLog>();
	public static List<PositionLog>  posLogList=new ArrayList<PositionLog>();
	public static List<StaffExtra> entryOrExitStaffExtraList=new ArrayList<StaffExtra>();
	public static List<PositionLog>  normalPosLogList=new ArrayList<PositionLog>();
	public static List<PositionLog> unusualPosLogList=new ArrayList<PositionLog>();
	public static Set<Integer> unusualCardIdSet=new HashSet<Integer>();
	public static Map<Integer,byte[]> stationStateMap=new HashMap<Integer, byte[]>();
//	public static Map<String,byte[]>cmdMap=new HashMap<String, byte[]>();
	public static BlockingQueue<CmdObject> cmdQueue = new ArrayBlockingQueue<CmdObject>(10);
	public static BlockingQueue<CmdObject> msgQueue = new ArrayBlockingQueue<CmdObject>(20);
	public static Timer swingTimer;
}
