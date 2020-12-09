package com.demo.btvideo.data;

import com.demo.btvideo.model.Attention;
import com.demo.btvideo.model.Collection;
import com.demo.btvideo.model.Msg;
import com.demo.btvideo.model.PageInfo;
import com.demo.btvideo.net.LoadDataInterface;
import com.demo.btvideo.net.NetInterface;
import com.demo.btvideo.utils.NetWorkUtils;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;
import retrofit2.Retrofit;


//获取关注用户的列表数据
public class AttentionDataSource implements LoadDataInterface<Attention> {

	ListeningExecutorService executorService;

	public AttentionDataSource(ListeningExecutorService executorService) {
		this.executorService=executorService;
	}

	@Override
	public ListenableFuture<List<Attention>> load(int pageNum) {
		return executorService.submit(() -> {
			Retrofit retrofit = NetWorkUtils.getRetrofit();
			NetInterface netInterface = retrofit.create(NetInterface.class);
			Response<Msg<PageInfo<Attention>>> infos = netInterface.getAttentionList(pageNum,20).execute();
			if (infos.body() != null&&infos.body().getCode()==200) {
				PageInfo<Attention> datas=infos.body().getData();
				return datas.getList();
			} else {
				return new ArrayList<>();
			}
		});
	}
}
