package com.demo.btvideo.utils;

import android.util.ArrayMap;

import com.demo.btvideo.AppController;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetWorkUtils {
	private static OkHttpClient httpClient;
	private static MediaType JSON = MediaType.parse("application/json; charset=utf-8");
	private static Gson g;
	private static Retrofit retrofit;


	static {
		if (httpClient == null) {
			httpClient = new OkHttpClient.Builder()
					.connectTimeout(30, TimeUnit.SECONDS)
					.build();
		}
	}

	public static void httpPost(String url, String data, Callback callback) {
		RequestBody requestBody1 = RequestBody.create(data, JSON);
		Request request = new Request.Builder()
				.url(url)
				.post(requestBody1)
				.build();
		httpClient.newCall(request).enqueue(callback);
	}


	public static Response httpPostFormBody(String url, String page) throws IOException {
		RequestBody requestBody1 = new FormBody.Builder()
				.add("pageNum", page)
				.build();
		Request request = new Request.Builder()
				.url(url)
				.post(requestBody1)
				.build();
		return httpClient.newCall(request).execute();
	}

	public static Response httpPost(String url, String data) throws IOException {

		RequestBody requestBody1 = RequestBody.create(data, JSON);
		Request request = new Request.Builder()
				.url(url)
				.post(requestBody1)
				.build();
		return httpClient.newCall(request).execute();
	}

	public static void httpGet(String url, String data, Callback callback) {
		RequestBody requestBody1 = RequestBody.create(data, JSON);
		Request request = new Request.Builder()
				.url(url)
				.get()
				.build();
		httpClient.newCall(request).enqueue(callback);
	}

	public static void updateAuth(String token){
		OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
		httpClientBuilder.addInterceptor(new Interceptor() {
			@Override
			public Response intercept(Interceptor.Chain chain) throws IOException {
				Request original = chain.request();
				Request.Builder requestBuilder = original.newBuilder()
						.header("Authorization", "Bearer "+token)
//						.header("Accept", "application/json")
						.method(original.method(), original.body());

				Request request = requestBuilder.build();
				return chain.proceed(request);
			}
		});
		httpClient=httpClientBuilder.build();
		Gson gson=new GsonBuilder()
				.serializeNulls().create();
		retrofit = new Retrofit.Builder()
				.baseUrl(ServerURL.MAIN_URL)
				.addConverterFactory(GsonConverterFactory.create(gson))
				.client(httpClientBuilder.build())
				.build();
	}

	public static Retrofit getRetrofit() {
		if (retrofit == null) {
			retrofit = new Retrofit.Builder()
					.baseUrl(ServerURL.MAIN_URL)
					.addConverterFactory(GsonConverterFactory.create())
					.build();
		}
		return retrofit;
	}


	public static boolean isJson(String json) {
		try {
			new Gson().fromJson(json, Object.class);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}


}
