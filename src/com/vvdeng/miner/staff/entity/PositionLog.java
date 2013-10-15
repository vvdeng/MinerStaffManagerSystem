package com.vvdeng.miner.staff.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "position_log")
public class PositionLog {
	private Long id;
	private Long cardId;
	private Long subDeviceId;
	private Long readerId;
	private Long arriveTime;
	private Long stayTime;
	@Id
	@GeneratedValue
	@Column(name="id")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@Column(name="card_id")
	public Long getCardId() {
		return cardId;
	}
	public void setCardId(Long cardId) {
		this.cardId = cardId;
	}
	@Column(name="sub_device_id")
	public Long getSubDeviceId() {
		return subDeviceId;
	}
	public void setSubDeviceId(Long subDeviceId) {
		this.subDeviceId = subDeviceId;
	}
	@Column(name="read_id")
	public Long getReaderId() {
		return readerId;
	}
	public void setReaderId(Long readerId) {
		this.readerId = readerId;
	}
	@Column(name="arrive_time")
	public Long getArriveTime() {
		return arriveTime;
	}
	public void setArriveTime(Long arriveTime) {
		this.arriveTime = arriveTime;
	}
	@Column(name="stay_time")
	public Long getStayTime() {
		return stayTime;
	}
	public void setStayTime(Long stayTime) {
		this.stayTime = stayTime;
	}
}
