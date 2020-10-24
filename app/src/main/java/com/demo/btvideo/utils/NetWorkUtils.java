package com.demo.btvideo.utils;

import com.google.gson.Gson;

import java.util.concurrent.TimeUnit;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class NetWorkUtils {
	private static OkHttpClient httpClient;
	private static MediaType JSON = MediaType.parse("application/json; charset=utf-8");
	private static Gson g;


	static {
		if (httpClient==null){
			httpClient=new OkHttpClient.Builder()
					.connectTimeout(30, TimeUnit.SECONDS)
					.build();
		}
	}

	public static void httpPost(String url, String data, Callback callback){

		RequestBody requestBody1=RequestBody.create(data,JSON);
		Request request=new Request.Builder()
				.url(url)
				.post(requestBody1)
				.build();
		httpClient.newCall(request).enqueue(callback);
	}

	public static void httpGet(String url, String data, Callback callback){
		RequestBody requestBody1=RequestBody.create(data,JSON);
		Request request=new Request.Builder()
				.url(url)
				.get()
				.build();
		httpClient.newCall(request).enqueue(callback);
	}


	public static boolean isJson(String json){
		try{
			new Gson().fromJson(json,Object.class);
			return true;
		}catch (Exception e){
			e.printStackTrace();
			return false;
		}
	}

}
