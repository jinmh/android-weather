package com.demo.wanpeng.weather.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.demo.wanpeng.base.BaseApplication;
import com.demo.wanpeng.weather.model.City;
import com.demo.wanpeng.weather.model.County;
import com.demo.wanpeng.weather.model.Province;

public class WeatherDB {

	private static final String DB_NAME="weather";
	
	private static final int VERSION=1;
	
	private static WeatherDB weatherDB;
	
	private SQLiteDatabase db;
	
	
	private WeatherDB(){
		WeatherOpenHelper dbHelper = new WeatherOpenHelper(BaseApplication.getContext(), DB_NAME, null, VERSION);
		db=dbHelper.getWritableDatabase();
	}
	
	
	public synchronized static WeatherDB getInstance(){
		if (weatherDB == null){
			weatherDB = new WeatherDB();
		}

		return weatherDB;
	}
	
	public void clearData(){
		db.execSQL("delete from province;");
		db.execSQL("delete from city;");
		db.execSQL("delete from county;");
		
	}
	
	public void saveProvince(Province province){
		if(province !=null){
			ContentValues values = new ContentValues();
			
			values.put("province_name", province.getProvinceName());
			values.put("province_code", province.getProvinceCode());

			
			db.insert("province", null, values);
		}
	}

	
	
	public void saveCity(City city){
		if(city !=null){
			ContentValues values = new ContentValues();
			
			values.put("city_name", city.getCityName());
			values.put("city_code", city.getCityCode());
			values.put("province_id",city.getProvinceId());
			
			db.insert("city", null, values);
		}
	}
	
	
	public void saveCounty(County county){
		if(county !=null){
			ContentValues values = new ContentValues();
			
			values.put("county_name", county.getCountyName());
			values.put("county_code", county.getCountyCode());
			values.put("city_id",county.getCityId());
			
			db.insert("county", null, values);
		}
	}
	
	
	
	

	
	
	public List<Province> loadProvinces(){
		List<Province> provinceList = new ArrayList<Province>();
		Cursor cursor = db.query("province",null,null,null,null,null,null);
		if(cursor.moveToFirst()){
			do{
				Province province = new Province();
				province.setId(cursor.getInt(cursor.getColumnIndex("id")));
				province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
				province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
				
				provinceList.add(province);
			}while(cursor.moveToNext());
		}
		
		return provinceList;
	}
	
	
	
	public List<City> loadCitys(int provinceId){
		List<City> cityList = new ArrayList<City>();
		Cursor cursor = db.query("city",null," province_id = ? ",new String[]{String.valueOf(provinceId)},null,null,null);
		if(cursor.moveToFirst()){
			do{
				City city = new City();
				city.setId(cursor.getInt(cursor.getColumnIndex("id")));
				city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
				city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
				city.setProvinceId(cursor.getInt(cursor.getColumnIndex("province_id")));
				
				cityList.add(city);
			}while(cursor.moveToNext());
		}
		
		return cityList;
	}
	
	
	
	
	
	public List<County> loadCountys(int cityId){
		List<County> countyList = new ArrayList<County>();
		Cursor cursor = db.query("county",null," city_id = ?",new String[]{String.valueOf(cityId)},null,null,null);
		if(cursor.moveToFirst()){
			do{
				County county = new County();
				county.setId(cursor.getInt(cursor.getColumnIndex("id")));
				county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
				county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
				county.setCityId(cursor.getInt(cursor.getColumnIndex("city_id")));
				
				countyList.add(county);
			}while(cursor.moveToNext());
		}
		
		return countyList;
	}	
	
	
	
	/**
	 * 解析web接口返回的省份数据，写入数据库表
	 * @param response
	 */
	public boolean handleProvinceResponse(String response){
		boolean result=false;
		
		if(response!=null){
			String[] provinces = response.split(",");
			if(provinces!=null && provinces.length>0){
				for(String p : provinces){
					String[] parray = p.split("\\|");
					if(parray.length==2){
						Province province = new Province();
						province.setProvinceCode(parray[0]);
						province.setProvinceName(parray[1]);
						
						saveProvince(province);
						result = true;
					}
				}
			}
		}
		return result;
	}
	
	
	/**
	 * 解析web接口返回的城市数据，写入数据库表
	 * @param response
	 */
	public boolean handleCityResponse(String response,int provinceId){
		boolean result=false;
		
		if(response!=null){
			String[] citys = response.split(",");
			if(citys!=null && citys.length>0){
				for(String p : citys){
					String[] parray = p.split("\\|");
					if(parray.length==2){
						City city = new City();
						city.setCityCode(parray[0]);
						city.setCityName(parray[1]);
						city.setProvinceId(provinceId);
						
						saveCity(city);
						result=true;
					}
				}
			}	
		}
		return result;
	}
	
	
	/**
	 * 解析web接口返回的区县数据，写入数据库表
	 * @param response
	 */
	public boolean handleCountyResponse(String response,int cityId){
		boolean result=false;
		
		if(response!=null){
			String[] countys = response.split(",");
			if(countys!=null && countys.length>0){
				for(String p : countys){
					String[] parray = p.split("\\|");
					if(parray.length==2){
						County county = new County();
						county.setCountyCode(parray[0]);
						county.setCountyName(parray[1]);
						county.setCityId(cityId);
						
						saveCounty(county);
						result=true;
					}
				}
			}	
		}
		
		return result;
	}	
	
		
	
}
