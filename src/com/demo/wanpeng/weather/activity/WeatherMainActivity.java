package com.demo.wanpeng.weather.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.demo.wanpeng.R;
import com.demo.wanpeng.base.BaseActivity;
import com.demo.wanpeng.base.BaseUtils;
import com.demo.wanpeng.base.http.RemoteService;
import com.demo.wanpeng.base.http.RequestCallBack;
import com.demo.wanpeng.weather.db.WeatherDB;
import com.demo.wanpeng.weather.model.City;
import com.demo.wanpeng.weather.model.County;
import com.demo.wanpeng.weather.model.Province;

public class WeatherMainActivity extends BaseActivity {

	private ListView listView;
	
	private TextView titleTextView;
	
	private WeatherDB weatherDB=WeatherDB.getInstance();
	
	private ArrayAdapter<String> adapter;
	
	private List<String> dataList = new ArrayList<String>();
	
	private static final int LEVEL_PROVINCE = 0;
	
	private static final int LEVEL_CITY = 1;
	
	private static final int LEVEL_COUNTY = 2;
	
	private int currentLevel;
	
	private List<Province> provinceList;
	
	private List<City> cityList;
	
	private List<County> countyList;
	
	private Province selectedProvince;
	
	private City selectedCity;
	
	private County selectedCounty;
	
	
	
	@Override
	protected void initVariables() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void initViews() {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.weather_area);
	
		listView=(ListView)this.findViewById(R.id.area_list_view);
		titleTextView=(TextView)this.findViewById(R.id.title_text);
		
		adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,dataList);
		listView.setAdapter(adapter);
		
		listView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				if(currentLevel == LEVEL_PROVINCE){
					selectedProvince = provinceList.get(position);
					queryCitys(selectedProvince.getId());
				}else if(currentLevel == LEVEL_CITY){
					selectedCity = cityList.get(position);
					queryCountys(selectedCity.getId());
				}else if(currentLevel == LEVEL_COUNTY){
					selectedCounty = countyList.get(position);
					Intent intent = new Intent(WeatherMainActivity.this,WeatherInfoActivity.class);
					intent.putExtra("county_code", selectedCounty.getCountyCode());
					
					startActivity(intent);
				}
				
			}
			
		});
	}

	@Override
	protected void initData() {
		//weatherDB.clearData();		
		queryProvinces();
	}
	
	
	@Override
	public void onBackPressed(){
		if(currentLevel==LEVEL_COUNTY){
			queryCitys(selectedProvince.getId());
		}else if(currentLevel==LEVEL_CITY){		
			queryProvinces();
		}else{
			finish();
		}
	}
	
	
	
	private void queryFromServer(final String code,final String type,final int parentId){
		final ProgressDialog dialog = BaseUtils.createProgressDialog(this, "加载中...");
		dialog.show();
		
		if("province".equals(type)){
			RequestCallBack callBack = new AbstractRequestCallBack(){
				@Override
				public void onSuccess(String content){
					boolean result = weatherDB.handleProvinceResponse(content);
					if(result){
						queryProvinces();
					}
					dialog.dismiss();
				}
			};
			RemoteService.getInstance().invoke(this, "http://www.weather.com.cn/data/list3/city.xml", callBack);
		}else if("city".equals(type)){
			RequestCallBack callBack = new AbstractRequestCallBack(){
				@Override
				public void onSuccess(String content){

					boolean result = weatherDB.handleCityResponse(content,parentId);
					if(result){
						queryCitys(parentId);
					}
					dialog.dismiss();
				}
			};
			RemoteService.getInstance().invoke(this, "http://www.weather.com.cn/data/list3/city"+code+".xml", callBack);
			
		}else if("county".equals(type)){
			RequestCallBack callBack = new AbstractRequestCallBack(){
				@Override
				public void onSuccess(String content){
					boolean result = weatherDB.handleCountyResponse(content,parentId);
					if(result){
						queryCountys(parentId);
					}
					dialog.dismiss();
				}
			};
			RemoteService.getInstance().invoke(this, "http://www.weather.com.cn/data/list3/city"+code+".xml", callBack);
			
		}
		dialog.dismiss();
	}
	
	private void queryProvinces(){
		provinceList = weatherDB.loadProvinces();
		if(provinceList.size()>0){
			dataList.clear();
			for(Province p: provinceList){
				dataList.add(p.getProvinceName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleTextView.setText("中国");
			currentLevel = LEVEL_PROVINCE;
		}else{
			queryFromServer(null,"province",0);
		}
	}

	private void queryCitys(int provinceId){
		cityList = weatherDB.loadCitys(provinceId);
		if(cityList.size()>0){
			dataList.clear();
			for(City p: cityList){
				dataList.add(p.getCityName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleTextView.setText(selectedProvince.getProvinceName());
			currentLevel = LEVEL_CITY;
		}else{
			queryFromServer(selectedProvince.getProvinceCode(),"city",provinceId);
		}
		
	}
	
	private void queryCountys(int cityId){
		countyList = weatherDB.loadCountys(cityId);
		if(countyList.size()>0){
			dataList.clear();
			for(County p: countyList){
				dataList.add(p.getCountyName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleTextView.setText(selectedCity.getCityName());
			currentLevel = LEVEL_COUNTY;
		}else{
			queryFromServer(selectedCity.getCityCode(),"county",cityId);
		}
		
	}
}
