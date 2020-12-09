package com.demo.btvideo.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.demo.btvideo.model.User;
import com.demo.btvideo.model.VideoInfo;


public class LoginViewModel extends ViewModel {
	MutableLiveData<Integer> val;
	MutableLiveData<Integer> refreshVideo;
	MutableLiveData<User> userLiveData;
	MutableLiveData<Integer> notifyData;


	private static class Holder {
		public static final LoginViewModel INSTANCE = new LoginViewModel();
	}

	public static LoginViewModel getInstance() {
		return Holder.INSTANCE;
	}


	public MutableLiveData<Integer> update(){
		if (val==null){
			val=new MutableLiveData<>();
		}
		return val;
	}
	public MutableLiveData<Integer> refreshVideo(){
		if (refreshVideo==null){
			refreshVideo=new MutableLiveData<>();
		}
		return refreshVideo;
	}

	public MutableLiveData<User> userLiveData(){
		if (userLiveData==null){
			userLiveData=new MutableLiveData<>();
		}
		return userLiveData;
	}
	public MutableLiveData<Integer> notifyData(){
		if (notifyData==null){
			notifyData=new MutableLiveData<>();
		}
		return notifyData;
	}




}
