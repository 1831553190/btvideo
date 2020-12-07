package com.demo.btvideo.data;

import com.demo.btvideo.model.Collection;
import com.demo.btvideo.model.Msg;
import com.demo.btvideo.model.PageInfo;
import com.demo.btvideo.model.UserMessage;
import com.demo.btvideo.net.LoadDataInterface;
import com.demo.btvideo.net.NetInterface;
import com.demo.btvideo.utils.NetWorkUtils;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;
import retrofit2.Retrofit;

public class CollectionDataSource implements LoadDataInterface<Collection> {

	ListeningExecutorService executorService;

	public CollectionDataSource(ListeningExecutorService executorService) {
		this.executorService=executorService;
	}

	@Override
	public ListenableFuture<List<Collection>> load(int pageNum) {
		return executorService.submit(() -> {
			Retrofit retrofit = NetWorkUtils.getRetrofit();
			NetInterface netInterface = retrofit.create(NetInterface.class);
			Response<Msg<PageInfo<Collection>>> infos = netInterface.getCollectionList(pageNum,20).execute();
			if (infos.body() != null&&infos.body().getCode()==200) {
				PageInfo<Collection> datas=infos.body().getData();
//                    JsonElement element=infos.body().getData();
//                    PageInfo<Comment> data=new Gson().fromJson(element,new TypeToken<PageInfo<Comment>>(){}.getType());
				return datas.getList();
			} else {
				return new ArrayList<>();
			}
		});
	}
}
