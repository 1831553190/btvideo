package com.demo.btvideo.data;

import com.demo.btvideo.model.Comment;
import com.demo.btvideo.model.Msg;
import com.demo.btvideo.model.PageInfo;
import com.demo.btvideo.model.VideoInfo;
import com.demo.btvideo.net.LoadDataInterface;
import com.demo.btvideo.net.NetInterface;
import com.demo.btvideo.utils.NetWorkUtils;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.gson.JsonObject;

import java.util.List;
import java.util.concurrent.Callable;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoadVideoInfo implements LoadDataInterface<VideoInfo> {
	ListeningExecutorService executorService;

	public LoadVideoInfo(ListeningExecutorService executorService) {
		this.executorService=executorService;
	}

	@Override
	public ListenableFuture<List<VideoInfo>> load(int pageNum) {
		return executorService.submit(new Callable<List<VideoInfo>>() {
			@Override
			public List<VideoInfo> call() throws Exception {
				Retrofit retrofit = NetWorkUtils.getRetrofit();
				NetInterface netInterface = retrofit.create(NetInterface.class);
				Response<Msg<PageInfo<VideoInfo>>> infos = netInterface.getPageInfo(pageNum, 20).execute();
				if (infos.body() != null) {
					return infos.body().getData().getList();
				} else {
					return null;
				}
			}
		});
	}
}
