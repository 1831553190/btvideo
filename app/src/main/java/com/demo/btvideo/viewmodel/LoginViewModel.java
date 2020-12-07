package com.demo.btvideo.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.demo.btvideo.model.VideoInfo;


public class LoginViewModel extends ViewModel {
	MutableLiveData<Integer> val;



	private static class Holder {
		public static final DataViewModel INSTANCE = new DataViewModel();
	}

	public static DataViewModel getInstance() {
		return Holder.INSTANCE;
	}


	public MutableLiveData<Integer> update(){
		if (val==null){
			val=new MutableLiveData<>();
		}
		return val;
	}


}
