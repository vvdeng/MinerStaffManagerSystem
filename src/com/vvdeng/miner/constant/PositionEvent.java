package com.vvdeng.miner.constant;

import java.util.HashMap;
import java.util.Map;

public enum PositionEvent {
	NORMAL(0,"Í¨¹ý"),ALERT(1,"ºô½Ð"),VOID2(2,""),VOID_3(3,""),BAT_LOW(4,"Ç·Ñ¹"),LEAVE(5,"Àë¿ª");
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
