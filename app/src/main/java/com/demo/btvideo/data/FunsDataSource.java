package com.demo.btvideo.data;

import com.demo.btvideo.model.Attention;
import com.demo.btvideo.model.Funs;
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

//获取粉丝列表数据
public class FunsDataSource implements LoadDataInterface<Funs> {

	ListeningExecutorService executorService;

	public FunsDataSource(ListeningExecutorService executorService) {
		this.executorService=executorService;
	}

	@Override
	public ListenableFuture<List<Funs>> load(int pageNum) {
		return executorService.submit(() -> {
			Retrofit retrofit = NetWorkUtils.getRetrofit();
			NetInterface netInterface = retrofit.create(NetInterface.class);
			Response<Msg<PageInfo<Funs>>> infos = netInterface.getFunsList(pageNum,"20").execute();
			if (infos.body() != null&&infos.body().getCode()==200) {
				PageInfo<Funs> datas=infos.body().getData();
//                    JsonElement element=infos.body().getData();
//                    PageInfo<Comment> data=new Gson().fromJson(element,new TypeToken<PageInfo<Comment>>(){}.getType());
				return datas.getList();
			} else {
				return new ArrayList<>();
			}
		});
	}
}
