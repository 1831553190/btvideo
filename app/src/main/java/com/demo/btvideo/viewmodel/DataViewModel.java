package com.demo.btvideo.viewmodel;


import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DataViewModel extends ViewModel {
	MutableLiveData<Integer> val;

	public MutableLiveData<Integer> update(){
		if (val==null){
			val=new MutableLiveData<>();
		}
		return val;
	}




}
