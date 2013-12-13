package com.vvdeng.miner.constant;

import java.util.HashMap;
import java.util.Map;

public enum StationCommState {
	CONNECT(0,"通讯中"),BLOCK(1,"通讯阻塞"),NORMAL(2,"通讯正常"),EXCEP(3,"通讯中断"),DC(4,"直流供电"),AC(5,"交流供电");
	private  StationCommState(int id,String desc){
		this.id=id;
		this.desc=desc;
		
		
	}
	private int id;
	private String desc;
	public int getId() {
		return id;
	}

	public String getDesc() {
		return desc;
	}
	public String toString(){
		return desc;
	}


	
	

}
