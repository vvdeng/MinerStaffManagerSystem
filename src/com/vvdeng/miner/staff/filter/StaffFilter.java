package com.vvdeng.miner.staff.filter;

public class StaffFilter {
	private Long workId;
	private String name;
	private Long depId;
	private Integer depLevel;
	private Long workTypeId;
	private Long clazzId;
	public Long getWorkId() {
		return workId;
	}
	public void setWorkId(Long workId) {
		this.workId = workId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Long getDepId() {
		return depId;
	}
	public void setDepId(Long depId) {
		this.depId = depId;
	}
	public Integer getDepLevel() {
		return depLevel;
	}
	public void setDepLevel(Integer depLevel) {
		this.depLevel = depLevel;
	}
	public Long getWorkTypeId() {
		return workTypeId;
	}
	public void setWorkTypeId(Long workTypeId) {
		this.workTypeId = workTypeId;
	}
	public Long getClazzId() {
		return clazzId;
	}
	public void setClazzId(Long clazzId) {
		this.clazzId = clazzId;
	}

}
