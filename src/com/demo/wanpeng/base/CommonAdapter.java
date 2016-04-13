package com.demo.wanpeng.base;

import java.util.ArrayList;

import android.widget.BaseAdapter;


public abstract class CommonAdapter<T> extends BaseAdapter {
	protected  ArrayList<T> entityList;
	protected  BaseActivity context;
	
	public CommonAdapter(ArrayList<T> entityList,BaseActivity context){
		this.entityList=entityList;
		this.context=context;
	}
	
	@Override
	public int getCount() {

		return entityList.size();
		
	}

	@Override
	public Object getItem(int position) {
		return entityList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}



	
}
