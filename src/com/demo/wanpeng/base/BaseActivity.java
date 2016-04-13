package com.demo.wanpeng.base;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;

import com.demo.wanpeng.base.http.RequestCallBack;
import com.demo.wanpeng.base.http.RequestManager;

public abstract class BaseActivity extends Activity {

	private static final String TAG = "BaseActivity";
	/**
	 * 请求列表管理器
	 */
	protected RequestManager requestManager = null;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestManager = new RequestManager();
		
		//跟踪当前创建的Activity类型
		Log .d(TAG,getClass().getSimpleName());
		ActivityCollector.addActivity(this);
		
		initVariables();
		initViews();
		initData();
		
	}
	

	/*
	 * 初始化页面变量
	 */
	protected abstract void initVariables();
	
	
	/*
	 * 初始化页面控件
	 */
	protected abstract void initViews();
	
	
	/*
	 * 加载页面所需数据
	 */
	protected abstract void initData();
	
	
	protected void onDestroy() {
		/**
		 * 在activity销毁的时候同时设置停止请求，停止线程请求回调
		 */
		if (requestManager != null) {
			requestManager.cancelRequest();
		}
		super.onDestroy();
		ActivityCollector.removeActivity(this);		
	}

	
	protected void onPause() {
		/**
		 * 在activity停止的时候同时设置停止请求，停止线程请求回调
		 */
		if (requestManager != null) {
			requestManager.cancelRequest();
		}
		super.onPause();
	}

	public RequestManager getRequestManager() {
		return requestManager;
	}	

	
	
	public abstract class AbstractRequestCallBack implements RequestCallBack{
		public abstract void onSuccess(String content);

		public void onCookieExpired(){
			new AlertDialog.Builder(BaseActivity.this).setTitle("cookie失效").setMessage("cookie无效")
				.setPositiveButton("确定",null).show();
			
		}
		
		public void onFail(String errorMessage){
			new AlertDialog.Builder(BaseActivity.this).setTitle("系统错误").setMessage(errorMessage)
				.setPositiveButton("确定",null).show();
			
		}
	}
}
