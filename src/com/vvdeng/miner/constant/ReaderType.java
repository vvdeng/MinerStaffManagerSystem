package com.vvdeng.miner.constant;

import java.util.HashMap;
import java.util.Map;

public enum ReaderType {
	NORMAL(0,"普通"),ENTRY(1,"入口"),EXIT(2,"出口"),IMPORTANT(3,"重点区域"),DANGER(4,"高危区域");
	private  ReaderType(int id,String desc){
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
