package com.vvdeng.miner.staff.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="card_newest_position")
public class CardNewestPosition {
	private Long cardId;
	private Long subDeviceId;
	private Long readerId;
	@Id
	@Column(name="id")
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
	@Column(name="reader_id")
	public Long getReaderId() {
		return readerId;
	}
	public void setReaderId(Long readerId) {
		this.readerId = readerId;
	}
}
