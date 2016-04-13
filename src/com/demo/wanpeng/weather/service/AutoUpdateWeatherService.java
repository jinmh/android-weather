package com.demo.wanpeng.weather.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import com.demo.wanpeng.base.LogUtil;
import com.demo.wanpeng.base.http.RemoteService;
import com.demo.wanpeng.base.http.RequestCallBack;
import com.demo.wanpeng.weather.receiver.AutoUpdateWeatherReceiver;

public class AutoUpdateWeatherService extends Service {
	private static final String TAG="AutoUpdateWeatherService";
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	
	@Override
	public int onStartCommand(Intent intent,int flags,int startId){
		new Thread(new Runnable(){

			@Override
			public void run() {
				updateWeather();				
			}
			
		});
		AlarmManager manager = (AlarmManager)getSystemService(ALARM_SERVICE);
		Intent i = new Intent(this,AutoUpdateWeatherReceiver.class);
		PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
		manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,SystemClock.elapsedRealtime()+8*60*60*1000,pi);
		
		return super.onStartCommand(intent, flags, startId);
	}
	
	
	
	
	private void updateWeather(){
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		String weatherCode = preferences.getString("weather_code", "");
		
		if(weatherCode!=null && !"".equals(weatherCode)){
			RequestCallBack callBack = new RequestCallBack(){
				@Override
				public void onSuccess(String content){
					try {
						handleWeatherInfoResponse(content);
					} catch (JSONException e) {
						LogUtil.e(TAG, e.getMessage());
					}
				}

				@Override
				public void onFail(String errorMessage) {
					LogUtil.e(TAG, errorMessage);
				}

				@Override
				public void onCookieExpired() {
					
				}
			};
			RemoteService.getInstance().invoke("http://www.weather.com.cn/data/cityinfo/"+weatherCode+".html", callBack);					
		}
	}
	
	
	
	private void handleWeatherInfoResponse(String response) throws JSONException{
		JSONObject jsonObject = new JSONObject(response);
		JSONObject weatherInfo = jsonObject.getJSONObject("weatherinfo");
		
		if(weatherInfo!=null){
			SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
			editor.putBoolean("city_selected",true);
			editor.putString("city_name", weatherInfo.getString("city"));
			editor.putString("weather_code", weatherInfo.getString("cityid"));
			editor.putString("temp1", weatherInfo.getString("temp1"));
			editor.putString("temp2",weatherInfo.getString("temp2"));
			editor.putString("weather_desp", weatherInfo.getString("weather"));
			editor.putString("publish_time","今天 "+weatherInfo.getString("ptime")+" 发布");
			editor.putString("current_date",new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒",Locale.CHINA).format(new Date()));
			
			editor.commit();
		}
		
	}		
}
