package com.vvdeng.miner.constant;

import java.util.HashMap;
import java.util.Map;

public enum ReaderType {
	NORMAL(0,"��ͨ"),ENTRY(1,"���"),EXIT(2,"����"),IMPORTANT(3,"�ص�����"),DANGER(4,"��Σ����");
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
