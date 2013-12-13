package com.vvdeng.miner.staff.comm;

public class CmdObject {
	public static final int CMD_SYN_TIME=1;
	public static final int CMD_CALL_STAFF=2;
	public static final int CMD_CANCEL_CALL_STAFF=3;
	public static final int QUERY_OFFLINE_READER_DATA=4;
	public static final int QUERY_OFFLINE_STATION_DATA=5;
	public static final int CMD_CLEAR_ALL_DATA=6;
	private int cmdType;
	private byte[] extraData;
	public int getCmdType() {
		return cmdType;
	}
	public void setCmdType(int cmdType) {
		this.cmdType = cmdType;
	}
	public byte[] getExtraData() {
		return extraData;
	}
	public void setExtraData(byte[] extraData) {
		this.extraData = extraData;
	}
	

}
