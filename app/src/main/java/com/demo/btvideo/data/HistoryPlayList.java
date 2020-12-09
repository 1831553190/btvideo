package com.demo.btvideo.data;

import android.content.Context;
import android.preference.PreferenceManager;

import androidx.room.Room;

import com.demo.btvideo.dao.AppDatabase;
import com.demo.btvideo.model.Funs;
import com.demo.btvideo.model.Msg;
import com.demo.btvideo.model.PageInfo;
import com.demo.btvideo.model.VideoInfo;
import com.demo.btvideo.net.LoadDataInterface;
import com.demo.btvideo.net.NetInterface;
import com.demo.btvideo.utils.NetWorkUtils;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;
import retrofit2.Retrofit;


//从数据库获取历史播放记录列表数据
public class HistoryPlayList implements LoadDataInterface<VideoInfo> {

	private AppDatabase database;
	Context context;
	ListeningExecutorService executorService;

	public HistoryPlayList(ListeningExecutorService executorService, Context context) {
		this.executorService=executorService;
		this.context=context;
		database = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "user")
				.fallbackToDestructiveMigration().build();
	}

	@Override
	public ListenableFuture<List<VideoInfo>> load(int pageNum) {
		return executorService.submit(() -> {
			List<VideoInfo> list=database.userDao().loadVideoHistory((pageNum-1)*20,20);
			return list;
		});
	}
}
