package com.demo.wanpeng.base.http;

public interface RequestCallBack
{
	public void onSuccess(String content);

	public void onFail(String errorMessage);

	public void onCookieExpired();
}
