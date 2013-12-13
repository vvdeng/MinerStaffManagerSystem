package com.vvdeng.miner.staff.entity;

import java.sql.Blob;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "staff")
public class Staff {
	private Long id;
	private Integer cardId;
	private Long workId;
	private String name;
	private Integer sex;
	private String birthDate;
	private String certificateNo;
	private Long eduLevelId;
	private String eduLevel;
	private String phone;
	private String address;

	private Long professionId;
	private String profession;
	private Long departmentId;
	private Long dep1Id;
	private Long dep2Id;
	private Long dep3Id;
	private String department;
	private Long clazzId;
	private String clazz;
	private Blob image;
	public static final int MALE = 0;
	public static final int FEMAlE = 1;

	public Staff() {

	}

	public Staff(Integer cardId,Long workId, String name, int sex, String birthDate,
			String certificateNo, Long eduLevelId, String eduLevel,
			String phone, String address, Long professionId, String profession,
			Long departmentId, Long dep1Id, Long dep2Id, Long dep3Id,
			String department, Long clazzId, String clazz, Blob image) {
		this.cardId=cardId;
		this.workId = workId;
		this.name = name;
		this.sex = sex;
		this.birthDate = birthDate;
		this.certificateNo = certificateNo;
		this.eduLevelId = eduLevelId;
		this.eduLevel = eduLevel;
		this.phone = phone;
		this.address = address;

		this.professionId = professionId;
		this.profession = profession;
		this.departmentId = departmentId;
		this.dep1Id = dep1Id;
		this.dep2Id = dep2Id;
		this.dep3Id = dep3Id;
		this.department = department;
		this.clazzId = clazzId;
		this.clazz = clazz;
		this.image = image;
	}

	public void refresh(Integer cardId,Long workId, String name, int sex, String birthDate,
			String certificateNo, Long eduLevelId, String eduLevel,
			String phone, String address, Long professionId, String profession,
			Long departmentId, Long dep1Id, Long dep2Id, Long dep3Id,
			String department, Long clazzId, String clazz, Blob image) {
		this.cardId=cardId;
		this.workId = workId;
		this.name = name;
		this.sex = sex;
		this.birthDate = birthDate;
		this.certificateNo = certificateNo;
		this.eduLevelId = eduLevelId;
		this.eduLevel = eduLevel;
		this.phone = phone;
		this.address = address;

		this.professionId = professionId;
		this.profession = profession;
		this.departmentId = departmentId;
		this.dep1Id = dep1Id;
		this.dep2Id = dep2Id;
		this.dep3Id = dep3Id;
		this.department = department;
		this.clazzId = clazzId;
		this.clazz = clazz;
		this.image = image;
	}

	@Id
	@GeneratedValue
	@Column(name = "ID")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(name = "name", nullable = false)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	@Column(name="card_id",nullable=false)
	public Integer getCardId(){
		return cardId;
	}

	public void setCardId(Integer cardId){
		this.cardId=cardId;
	}
	@Column(name = "work_id", nullable = false)
	public Long getWorkId() {
		return workId;
	}

	public void setWorkId(Long workId) {
		this.workId = workId;
	}

	@Column(name = "sex")
	public Integer getSex() {
		return sex;
	}

	public void setSex(Integer sex) {
		this.sex = sex;
	}

	@Column(name = "birth_date")
	public String getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(String birthDate) {
		this.birthDate = birthDate;
	}

	@Column(name = "certificate_no")
	public String getCertificateNo() {
		return certificateNo;
	}

	public void setCertificateNo(String certificateNo) {
		this.certificateNo = certificateNo;
	}

	@Column(name = "edu_level_id")
	public Long getEduLevelId() {
		return eduLevelId;
	}

	public void setEduLevelId(Long eduLevelId) {
		this.eduLevelId = eduLevelId;
	}

	@Column(name = "edu_level")
	public String getEduLevel() {
		return eduLevel;
	}

	public void setEduLevel(String eduLevel) {
		this.eduLevel = eduLevel;
	}

	@Column(name = "phone")
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	@Column(name = "address")
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Column(name = "profession_id")
	public Long getProfessionId() {
		return professionId;
	}

	public void setProfessionId(Long professionId) {
		this.professionId = professionId;
	}

	@Column(name = "profession")
	public String getProfession() {
		return profession;
	}

	public void setProfession(String profession) {
		this.profession = profession;
	}

	@Column(name = "department_id")
	public Long getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(Long departmentId) {
		this.departmentId = departmentId;
	}

	@Column(name = "dep1_id")
	public Long getDep1Id() {
		return dep1Id;
	}

	public void setDep1Id(Long dep1Id) {
		this.dep1Id = dep1Id;
	}

	@Column(name = "dep2_id")
	public Long getDep2Id() {
		return dep2Id;
	}

	public void setDep2Id(Long dep2Id) {
		this.dep2Id = dep2Id;
	}

	@Column(name = "dep3_id")
	public Long getDep3Id() {
		return dep3Id;
	}

	public void setDep3Id(Long dep3Id) {
		this.dep3Id = dep3Id;
	}

	@Column(name = "department")
	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	@Column(name = "clazz_id")
	public Long getClazzId() {
		return clazzId;
	}

	public void setClazzId(Long clazzId) {
		this.clazzId = clazzId;
	}

	@Column(name = "clazz")
	public String getClazz() {
		return clazz;
	}

	public void setClazz(String clazz) {
		this.clazz = clazz;
	}

	@Column(name = "image")
	public Blob getImage() {
		return image;
	}

	public void setImage(Blob image) {
		this.image = image;
	}

	@Override
	public String toString() {
		return name;
	}

}
