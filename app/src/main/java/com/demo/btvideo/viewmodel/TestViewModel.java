package com.demo.btvideo.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TestViewModel<T> extends ViewModel {
	public MutableLiveData<T> data;

	public MutableLiveData<T> getData(){
		if (data==null){
			data=new MutableLiveData<>();
		}
		return data;
	}
}
