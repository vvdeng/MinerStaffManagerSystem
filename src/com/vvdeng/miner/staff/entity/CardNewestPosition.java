package com.vvdeng.miner.staff.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="card_newest_position")
public class CardNewestPosition {
	private Integer cardId;
	private Integer subDeviceId;
	private Integer readerId;
	@Id
	@Column(name="id")
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
}
