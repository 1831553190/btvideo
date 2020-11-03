package com.demo.btvideo.adapter;

import androidx.annotation.Nullable;
import androidx.paging.ListenableFuturePagingSource;
import androidx.paging.PagingSource;

import com.demo.btvideo.model.Comment;
import com.demo.btvideo.model.Msg;
import com.demo.btvideo.model.PageInfo;
import com.demo.btvideo.model.VideoInfo;
import com.demo.btvideo.net.NetInterface;
import com.demo.btvideo.utils.NetWorkUtils;
import com.google.common.base.Function;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import kotlin.jvm.functions.Function0;
import retrofit2.HttpException;
import retrofit2.Response;

public class CommentDataSource extends ListenableFuturePagingSource<Integer, Comment> {
	private ExecutorService pool= Executors.newCachedThreadPool();
	private ListeningExecutorService executorService=MoreExecutors.listeningDecorator(pool);

	int objId;

	public CommentDataSource(int objId){
		this.objId=objId;
	}
	@NotNull
	@Override
	public ListenableFuture<LoadResult<Integer, Comment>> loadFuture(@NotNull LoadParams<Integer> params) {
		Integer nextPageNumber = params.getKey();
		if (nextPageNumber == null) {
			nextPageNumber = 0;//从第0页开始加载
		}
		Integer finalNextPageNumber = nextPageNumber;
		ListenableFuture<LoadResult<Integer, Comment>> pageFuture =Futures
				.transform(executorService.submit(new Callable<List<Comment>>() {
			@Override
			public List<Comment> call() throws Exception {
				//这里处理耗时操作,比如网络请求数据,数据库数据加载
				NetInterface netInterface = NetWorkUtils.getRetrofit().create(NetInterface.class);
				Response<Msg<List<Comment>>> infos = netInterface.getCommentList(finalNextPageNumber,objId).execute();
				return infos.body().getData();
			}
		}), new Function<List<Comment>, LoadResult.Page<Integer, Comment>>() {
			@NotNull
			@Override
			public LoadResult.Page<Integer, Comment> apply(@Nullable List<Comment> input) {
//				这里传入的三个参数中,刚才请求的数据,第二个参数为请求的上一页的页数,当为null时不再加载上一页,第三个参数则是下一页,后两个参数不介绍,自行了解
				return new LoadResult.Page<>(input,finalNextPageNumber==0?null:finalNextPageNumber-1,input.isEmpty()?null:finalNextPageNumber+1);
			}
		}, pool);

		ListenableFuture<LoadResult<Integer,Comment>> partialLoadResultFuture = Futures.catching(
				pageFuture, HttpException.class,
				LoadResult.Error::new, pool);

		return Futures.catching(partialLoadResultFuture,
				IOException.class, LoadResult.Error::new, pool);
	}
}
