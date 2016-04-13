package com.demo.wanpeng.base.http;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.demo.wanpeng.base.BaseActivity;

public class RemoteService {
	private static RemoteService service = null;

	private RemoteService() {

	}

	public static synchronized RemoteService getInstance() {
		if (RemoteService.service == null) {
			RemoteService.service = new RemoteService();
		}
		return RemoteService.service;
	}

	public void invoke(final BaseActivity activity, final String apiKey,final List<RequestParameter> params, final RequestCallBack callBack) {
		final URLData urlData = UrlConfigManager.findURL(activity, apiKey);
		if (urlData.getMockClass() != null) {
			try {
				MockService mockService = (MockService) Class.forName(
						urlData.getMockClass()).newInstance();
				String strResponse = mockService.getJsonData();

				final Response responseInJson = JSON.parseObject(strResponse,
						Response.class);
				if (callBack != null) {
					if (responseInJson.isError()) {
						callBack.onFail(responseInJson.getErrorMessage());
					} else {
						callBack.onSuccess(responseInJson.getResult());
					}
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		} else {
			HttpRequest request = activity.getRequestManager().createRequest(urlData, params, callBack);
			DefaultThreadPool.getInstance().execute(request);
		}
	}
	
	
	public void invoke(final BaseActivity activity, final String url, final RequestCallBack callBack) {
		URLData urlData=new URLData();
		urlData.setUrl(url);
		urlData.setNetType(HttpRequest.REQUEST_GET);
		
		HttpRequest request = activity.getRequestManager().createRequest(urlData, null, callBack);
		DefaultThreadPool.getInstance().execute(request);		
	}
	
	
	public void invoke(final String url, final RequestCallBack callBack) {
		URLData urlData=new URLData();
		urlData.setUrl(url);
		urlData.setNetType(HttpRequest.REQUEST_GET);

		HttpRequest request = new HttpRequest(urlData, null,callBack);
		DefaultThreadPool.getInstance().execute(request);		
	}
		
}