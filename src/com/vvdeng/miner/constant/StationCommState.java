package com.vvdeng.miner.constant;

import java.util.HashMap;
import java.util.Map;

public enum StationCommState {
	CONNECT(0,"ͨѶ��"),BLOCK(1,"ͨѶ����"),NORMAL(2,"ͨѶ����"),EXCEP(3,"ͨѶ�ж�"),DC(4,"ֱ������"),AC(5,"��������");
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
