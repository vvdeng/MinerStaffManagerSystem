package com.vvdeng.miner.constant;

import java.util.HashMap;
import java.util.Map;

public enum PositionEvent {
	NORMAL(0,"ͨ��"),ALERT(1,"����"),VOID2(2,""),VOID_3(3,""),BAT_LOW(4,"Ƿѹ"),LEAVE(5,"�뿪");
	private  PositionEvent(int id,String desc){
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


	
	

}
