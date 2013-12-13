package com.vvdeng.miner.staff.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "position_log")
public class PositionLog {
	public static final int EXIST=1;
	public static final int OUT=0;
	private Long id;
	private Integer cardId;
	private Integer subDeviceId;
	private Integer readerId;
	private Long arriveTime;
	private Long stayTime;
	private Integer event;
	private String eventDesc;
	private Integer exist;
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
	public Integer getCardId() {
		return cardId;
	}
	public void setCardId(Integer cardId) {
		this.cardId = cardId;
	}
	@Column(name="sub_device_id")
	public Integer getSubDeviceId() {
		return subDeviceId;
	}
	public void setSubDeviceId(Integer subDeviceId) {
		this.subDeviceId = subDeviceId;
	}
	@Column(name="reader_id")
	public Integer getReaderId() {
		return readerId;
	}
	public void setReaderId(Integer readerId) {
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
	public Integer getEvent() {
		return event;
	}
	public void setEvent(Integer event) {
		this.event = event;
	}
	@Column(name="event_desc")
	public String getEventDesc() {
		return eventDesc;
	}
	public void setEventDesc(String eventDesc) {
		this.eventDesc = eventDesc;
	}
	public Integer getExist() {
		return exist;
	}
	public void setExist(Integer exist) {
		this.exist = exist;
	}
}
