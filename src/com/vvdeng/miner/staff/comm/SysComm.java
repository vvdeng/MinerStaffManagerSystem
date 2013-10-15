package com.vvdeng.miner.staff.comm;

public class SysComm {
	public static final int SEQ = 0;
	public static int currentStationIndex = 1;

	public static void reqCurrentStationInfo() {

	}

	public static void reqNextStationInfo() {
		currentStationIndex++;
		reqCurrentStationInfo();
	}

	public static void startReqStationsInfo() {
		currentStationIndex=1;
		reqCurrentStationInfo();
	}

	public static void endReqStationsInfo() {
		currentStationIndex=1;
	}

	public static void send(int data) {

	}
}
