package com.demo.wanpeng.weather.receiver;

import com.demo.wanpeng.weather.service.AutoUpdateWeatherService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AutoUpdateWeatherReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent i = new Intent(context,AutoUpdateWeatherService.class);
		context.startService(i);
	}

}
