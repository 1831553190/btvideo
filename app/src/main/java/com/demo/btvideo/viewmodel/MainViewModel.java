package com.demo.btvideo.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MainViewModel extends AndroidViewModel {
	private MutableLiveData<Integer> mNetworkStateLiveData = new MutableLiveData<>();
	private MutableLiveData<String> mErrorMsgLiveData = new MutableLiveData<>();
	private MutableLiveData<Runnable> mRetryLiveData = new MutableLiveData<>();

	public MainViewModel(@NonNull Application application) {
		super(application);
	}

	public void postNetworkState(int state) {
		mNetworkStateLiveData.postValue(state);
	}

	public void postErrorMsg(@NonNull String msg) {
		mErrorMsgLiveData.postValue(msg);
	}

	public void postRetry(@NonNull Runnable action) {
		mRetryLiveData.postValue(action);
	}

	public MutableLiveData<Integer> networkStateLiveData() {
		return mNetworkStateLiveData;
	}

	public MutableLiveData<String> errorMsgLiveData() {
		return mErrorMsgLiveData;
	}

	public MutableLiveData<Runnable> retryLiveData() {
		return mRetryLiveData;
	}

	@Override
	protected void onCleared() {
		super.onCleared();
	}
}