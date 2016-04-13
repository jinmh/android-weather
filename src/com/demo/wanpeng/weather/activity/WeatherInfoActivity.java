package com.demo.wanpeng.weather.activity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.demo.wanpeng.R;
import com.demo.wanpeng.base.BaseActivity;
import com.demo.wanpeng.base.BaseUtils;
import com.demo.wanpeng.base.LogUtil;
import com.demo.wanpeng.base.http.RemoteService;
import com.demo.wanpeng.base.http.RequestCallBack;

public class WeatherInfoActivity extends BaseActivity{
	private static final String TAG="WeatherInfoActivity";
			
	private LinearLayout weatherInfoLayout;
	
	private TextView cityNameTextView;
	
	private TextView publishTextView;
	
	private TextView weatherDespTextView;
	
	private TextView currentDateTextView;
	
	private TextView temp1TextView;
	
	private TextView temp2TextView;
	
	private String weatherCode;
	
	
	
	@Override
	protected void initVariables() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void initViews() {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.weather_info);
		
		weatherInfoLayout = (LinearLayout)findViewById(R.id.weather_info_layout);
		cityNameTextView =(TextView)findViewById(R.id.city_name);
		publishTextView = (TextView)findViewById(R.id.publish_text);
		weatherDespTextView =(TextView)findViewById(R.id.weather_desp);
		currentDateTextView=(TextView)findViewById(R.id.current_date);
		temp1TextView =(TextView)findViewById(R.id.temp1);
		temp2TextView=(TextView)findViewById(R.id.temp2);
	
		
	}

	@Override
	protected void initData() {	
		String countyCode = getIntent().getStringExtra("county_code");
		
		if(countyCode!=null && !"".equals(countyCode.trim())){
			publishTextView.setText("同步中...");			
			weatherInfoLayout.setVisibility(View.INVISIBLE);
			cityNameTextView.setVisibility(View.INVISIBLE);
			queryWeatherInfo(countyCode);
		}else{
			showLocalWeather();
		}		
	}

	
	
	private void queryWeatherInfo(String countyCode){
		queryFromServer("countyCode",countyCode);
	}
	
	
	private void showLocalWeather(){
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		
		cityNameTextView.setText(preferences.getString("city_name", ""));
		temp1TextView.setText(preferences.getString("temp2", ""));
		temp2TextView.setText(preferences.getString("temp1", ""));
		publishTextView.setText(preferences.getString("publish_time", ""));
		weatherDespTextView.setText(preferences.getString("weather_desp", ""));
		currentDateTextView.setText(preferences.getString("current_date", ""));	
		
		weatherInfoLayout.setVisibility(View.VISIBLE);
		cityNameTextView.setVisibility(View.VISIBLE);
	}
	
	
	
	private void queryFromServer(final String codeType,final String codeValue){
		final ProgressDialog dialog = BaseUtils.createProgressDialog(this, "加载中...");
		dialog.show();
		
		if("countyCode".equals(codeType)){
			RequestCallBack callBack = new AbstractRequestCallBack(){
				@Override
				public void onSuccess(String content){
					handleWeatherCodeResponse(content);
					dialog.dismiss();
				}
			};
			RemoteService.getInstance().invoke(this, "http://www.weather.com.cn/data/list3/city"+codeValue+".xml", callBack);
		}else if("weatherCode".equals(codeType)){
			RequestCallBack callBack = new AbstractRequestCallBack(){
				@Override
				public void onSuccess(String content){
					try {
						handleWeatherInfoResponse(content);
						showLocalWeather();
					} catch (JSONException e) {
						LogUtil.e(TAG, e.getMessage());
					}
					dialog.dismiss();
				}
			};
			RemoteService.getInstance().invoke(this, "http://www.weather.com.cn/data/cityinfo/"+codeValue+".html", callBack);			
		}
		
		
		dialog.dismiss();
	}
	
	
	private void handleWeatherCodeResponse(String response){
		if(response!=null){
			String[] parray = response.split("\\|");
			if(parray.length==2){
				weatherCode=parray[1];
				if(weatherCode!=null){
					queryFromServer("weatherCode",weatherCode);
				}
			}	
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
