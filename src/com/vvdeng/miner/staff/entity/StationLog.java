package com.vvdeng.miner.staff.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "station_log")
public class StationLog {
	private Long id;
	private Integer subDeviceId;

	private Long arriveTime;
	private Integer event;
	private String eventDesc;
	@Id
	@GeneratedValue
	@Column(name="id")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	@Column(name="sub_device_id")
	public Integer getSubDeviceId() {
		return subDeviceId;
	}
	public void setSubDeviceId(Integer subDeviceId) {
		this.subDeviceId = subDeviceId;
	}

	@Column(name="arrive_time")
	public Long getArriveTime() {
		return arriveTime;
	}
	public void setArriveTime(Long arriveTime) {
		this.arriveTime = arriveTime;
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

}
