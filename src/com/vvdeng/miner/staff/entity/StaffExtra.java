package com.vvdeng.miner.staff.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "staff_extra")
public class StaffExtra {
	public static final int ON_GROUND=0;
	public static final int IN_MINER=1;
	public static final int NOT_IN_TABLE=0;
	public static final int IN_TABLE=1;
	private Long staffId;
	private Integer CardId;

	private Integer state=ON_GROUND;
	
	private Long lastArriveTime;
	private Long lastLeaveTime;
	@Id
	@Column(name = "staff_id")
	public Long getStaffId() {
		return staffId;
	}

	public void setStaffId(Long staffId) {
		this.staffId = staffId;
	}

	
	@Column(name = "card_id")
	public Integer getCardId() {
		return CardId;
	}

	public void setCardId(Integer cardId) {
		CardId = cardId;
	}
	@Column(name = "state")
	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}
	@Column(name = "last_arrive_time")
	public Long getLastArriveTime() {
		return lastArriveTime;
	}

	public void setLastArriveTime(Long lastArriveTime) {
		this.lastArriveTime = lastArriveTime;
	}
	@Column(name = "last_leave_time")
	public Long getLastLeaveTime() {
		return lastLeaveTime;
	}

	public void setLastLeaveTime(Long lastLeaveTime) {
		this.lastLeaveTime = lastLeaveTime;
	}


}
