package com.vvdeng.miner.staff.ui.model;

import java.util.List;

import javax.swing.AbstractListModel;

public class StaffListModel extends AbstractListModel {

	public StaffListModel(List list){
		modelList=list;
	}
	@Override
	public int getSize() {
		// TODO Auto-generated method stub
		return modelList==null?0:modelList.size();
	}

	@Override
	public Object getElementAt(int index) {
		// TODO Auto-generated method stub
		return modelList==null?null:modelList.get(index);
	}
	public void removeItemAt(int index){
		modelList.remove(index);
		fireIntervalRemoved(this, index, index);
	}
	public void addItem(Object item){
		int size=modelList.size();
		modelList.add(item);
		fireIntervalAdded(this, size, size);
	}
	public List getModelList() {
		return modelList;
	}
	public void setModelList(List modelList) {
		this.modelList = modelList;
	}
	List modelList;

}
