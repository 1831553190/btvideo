package com.demo.btvideo.viewmodel;


import android.util.Log;
import android.util.Pair;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.demo.btvideo.model.Msg;
import com.demo.btvideo.model.User;
import com.demo.btvideo.model.VideoInfo;
import com.demo.btvideo.net.NetInterface;
import com.demo.btvideo.utils.NetWorkUtils;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class DataViewModel extends ViewModel {
	MutableLiveData<Integer> val;
	MutableLiveData<VideoInfo> videoInfoMutableLiveData;
	MutableLiveData<Integer> updataComment;
	MutableLiveData<User> userLiveData;
	MutableLiveData<VideoInfo> videoInfoData;
	MutableLiveData<Pair<String,Integer>> mainkey;


	public MutableLiveData<Integer> update(){
		if (val==null){
			val=new MutableLiveData<>();
		}
		return val;
	}
	public MutableLiveData<Pair<String,Integer>> loadVideoData(){
		if (mainkey==null){
			mainkey=new MutableLiveData<>();
		}
		return mainkey;
	}




	public MutableLiveData<VideoInfo> videoData(){
		if (videoInfoMutableLiveData==null){
			videoInfoMutableLiveData=new MutableLiveData<>();
		}
		return videoInfoMutableLiveData;
	}
	public MutableLiveData<VideoInfo> loadVideoInfo(String videoId,String msgId){
		if (videoInfoData==null){
			videoInfoData=new MutableLiveData<>();
		}
		if (!videoId.equals("")){
			if (msgId.equals("")){
				funLoadVideoInfo(videoId);
			}else{
				funLoadVideoInfo(videoId,msgId);
			}
		}
		return videoInfoData;
	}

	public MutableLiveData<Integer> updateComment(){
		if (updataComment==null){
			updataComment=new MutableLiveData<>();
		}
		return updataComment;
	}

	public MutableLiveData<User> userLiveData(){
		if (userLiveData==null){
			userLiveData=new MutableLiveData<>();
		}
		return userLiveData;
	}


	public void funLoadVideoInfo(String videoId){
		NetInterface netInterface= NetWorkUtils.getRetrofit().create(NetInterface.class);
		netInterface.getVideoInfo(videoId,20).enqueue(new Callback<Msg<VideoInfo>>() {
			@Override
			public void onResponse(Call<Msg<VideoInfo>> call, Response<Msg<VideoInfo>> response) {
				if (response.isSuccessful()&&response.body().getCode()==200){
					VideoInfo videoInfo=response.body().getData();
					if (videoInfo!=null){
						videoInfoData.postValue(videoInfo);
					}
				}
			}

			@Override
			public void onFailure(Call<Msg<VideoInfo>> call, Throwable t) {
				Log.d("TAG", "onFailure: load videoInfo error");
			}
		});
	}
	public void funLoadVideoInfo(String videoId,String msgId){
		HashMap<String,String> body=new HashMap<>();
		body.put("id",msgId);
		NetInterface netInterface= NetWorkUtils.getRetrofit().create(NetInterface.class);
		netInterface.getVideoInfo(videoId,body,20).enqueue(new Callback<Msg<VideoInfo>>() {
			@Override
			public void onResponse(Call<Msg<VideoInfo>> call, Response<Msg<VideoInfo>> response) {
				if (response.isSuccessful()&&response.body().getCode()==200){
					VideoInfo videoInfo=response.body().getData();
					if (videoInfo!=null){
						videoInfoData.postValue(videoInfo);
					}
				}
			}

			@Override
			public void onFailure(Call<Msg<VideoInfo>> call, Throwable t) {
				Log.d("TAG", "onFailure: load videoInfo error");
			}
		});
	}
}
