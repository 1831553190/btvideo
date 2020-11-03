package com.demo.btvideo.viewmodel;


import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.demo.btvideo.model.User;
import com.demo.btvideo.model.VideoInfo;

public class DataViewModel extends ViewModel {
	MutableLiveData<Integer> val;
	MutableLiveData<VideoInfo> videoInfoMutableLiveData;
	MutableLiveData<User> userLiveData;

	public MutableLiveData<Integer> update(){
		if (val==null){
			val=new MutableLiveData<>();
		}
		return val;
	}
	public MutableLiveData<User> userLiveData(){
		if (userLiveData==null){
			userLiveData=new MutableLiveData<>();
		}
		return userLiveData;
	}


	public MutableLiveData<VideoInfo> videoData(){
		if (videoInfoMutableLiveData==null){
			videoInfoMutableLiveData=new MutableLiveData<>();
		}
		return videoInfoMutableLiveData;
	}

}
