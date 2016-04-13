package com.demo.wanpeng.base;

import java.util.ArrayList;
import java.util.List;


public class ActivityCollector {
	public static List<BaseActivity> activityList= new ArrayList<BaseActivity>();
	
	public static void addActivity(BaseActivity activity){
		if(!activityList.contains(activity)){
			activityList.add(activity);
		}
	}
	
	public static void removeActivity(BaseActivity activity){
		activityList.remove(activity);
	}
	
	
	public static void finishAll(){
		for(BaseActivity activity : activityList){
			if(!activity.isFinishing()){
				activity.finish();
			}
		}
	}
}
