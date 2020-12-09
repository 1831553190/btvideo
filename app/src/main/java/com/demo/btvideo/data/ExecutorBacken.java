package com.demo.btvideo.data;

import com.demo.btvideo.model.Comment;
import com.demo.btvideo.model.Msg;
import com.demo.btvideo.model.PageInfo;
import com.demo.btvideo.net.LoadDataInterface;
import com.demo.btvideo.net.NetInterface;
import com.demo.btvideo.utils.NetWorkUtils;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import retrofit2.Response;
import retrofit2.Retrofit;

//获取评论列表
public class ExecutorBacken implements LoadDataInterface<Comment> {


	int onjId;
	ListeningExecutorService executorService;

	public ExecutorBacken(ListeningExecutorService executorService,int onjId) {
		this.onjId = onjId;
		this.executorService=executorService;
	}

	@Override
	public ListenableFuture<List<Comment>> load(int pageNum) {
		return executorService.submit(() -> {
			Retrofit retrofit = NetWorkUtils.getRetrofit();
			NetInterface netInterface = retrofit.create(NetInterface.class);
			Response<Msg<JsonElement>> infos = netInterface.getCommentList(pageNum, onjId).execute();
			if (infos.body() != null&&infos.body().getCode()==200) {
				JsonElement element=infos.body().getData();
				PageInfo<Comment> data=new Gson().fromJson(element,new TypeToken<PageInfo<Comment>>(){}.getType());
				return data.getList();
			} else {
				return new ArrayList<>();
			}
		});
	}




}
