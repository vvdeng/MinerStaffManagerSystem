package com.vvdeng.miner.staff.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.vvdeng.miner.staff.ui.DepartmentTreeDialog;

@Entity
@Table(name = "info_item")
public class InfoItem {
	private Long id;
	private String name;
	private Integer type;
	private String value;
	private Integer level;
	private Long parentId;
	private String description;
//	public static int EDUCATION_ITEM = 0;
//	public static int WORK_TYPE_ITEM = 1;
//	public static int DEPARTMENT_ITEM = 2;
//	public static int CLAZZ_ITEM = 3;
//	public static int RIGION_ITEM=4;
//	public static int SUB_DEVICE_ITEM=5;
//	public static int READER_ITEM=6;
//	public static String EDUCATION_ITEM_DESC = "�����̶�";
//	public static String WORK_TYPE_ITEM_DESC = "����";
//	public static String DEPARTMENT_ITEM_DESC = "����";
//	public static String CLAZZ_ITEM_DESC="���";
//	public static String RIGION_ITEM_DESC="����";
//	public static String SUB_DEVICE_ITEM_DESC="��վ����";
//	public static String READER_ITEM_TYPE="����������";
	public InfoItem(){
		
	}
	public InfoItem(Long id){
		this.id=id;
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

	@Column(name = "type")
	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	@Column(name = "value")
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	@Column(name="level")
	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}
	@Column(name="parent_id")
	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}
	@Column(name="description")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	public  void copy(InfoItem other){
		if(other!=null){
			this.id=other.id;
			this.name=other.name;
			this.type=other.type;
			this.value=other.value;
			this.level=other.level;
			this.parentId=other.parentId;
			this.description=other.description;
		}
	}
	@Override
	public String toString() {
		return value;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj==null){
			return false;
		}
		return this.id.equals(((InfoItem)obj).getId());
	}
	public enum TYPE{
		EDUCATION(0,"�����̶�"),
		WORK_TYPE(1,"����"),
		DEPARTMENET(2,"����"),
		CLAZZ(3,"���"),
		REGION(4,"����"),
		SUBDEVICE_TYPE(5,"��վ����"),
		READER_TYPE(6,"����������");
		 private TYPE(int val, String desc){
			this.val=val;
			this.desc=desc;
		}
		 public int getVal(){
			 return val;
		 }
		 public String getDesc(){
			 return desc;
		 }
		 public String toString(){
			 return desc;
		 }
		 private int val;
		 private String desc;
	}

}
