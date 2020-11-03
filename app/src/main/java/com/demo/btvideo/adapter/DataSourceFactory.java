package com.demo.btvideo.adapter;

import androidx.paging.DataSource;

import com.demo.btvideo.model.VideoInfo;
import com.demo.btvideo.viewmodel.MainViewModel;

public class DataSourceFactory extends DataSource.Factory<Integer, VideoInfo> {

	private MainViewModel mainViewModel;

	public DataSourceFactory(MainViewModel wanVm) {
		mainViewModel = wanVm;
	}

	@Override
	public DataSource<Integer, VideoInfo> create() {
		return new VideoDataSource(mainViewModel);
	}
}
