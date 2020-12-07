package com.demo.btvideo.data;

import androidx.paging.ListenableFuturePagingSource;

import com.demo.btvideo.net.LoadDataInterface;
import com.google.common.base.Function;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.HttpException;

public class BaseDataSource<T> extends ListenableFuturePagingSource<Integer, T> {
	private ExecutorService pool= Executors.newCachedThreadPool();

	LoadDataInterface<T> loadDataInterface;

	public BaseDataSource(LoadDataInterface<T> loadDataInterface){
		this.loadDataInterface=loadDataInterface;
	}
	@NotNull
	@Override
	public ListenableFuture<LoadResult<Integer, T>> loadFuture(@NotNull LoadParams<Integer> params) {
		Integer nextPageNumber = params.getKey();
		if (nextPageNumber == null) {
			nextPageNumber = 1;//从第1页开始加载
		}
		Integer finalNextPageNumber = nextPageNumber;
		ListenableFuture<LoadResult<Integer, T>> pageFuture = Futures
				.transform(loadDataInterface.load(finalNextPageNumber), (Function<List<T>, LoadResult.Page<Integer, T>>) input -> {
	//				这里传入的三个参数中,刚才请求的数据,第二个参数为请求的上一页的页数,当为null时不再加载上一页,第三个参数则是下一页,后两个参数不介绍,自行了解
//						return new LoadResult.Error(new Throwable(""));
					if (input==null){
						input=new ArrayList<>();
					}
					return new LoadResult.Page<>(input,finalNextPageNumber==1?null:finalNextPageNumber-1,input.isEmpty()?null:finalNextPageNumber+1);
				}, pool);

		ListenableFuture<LoadResult<Integer,T>> partialLoadResultFuture = Futures.catching(
				pageFuture, HttpException.class,
				LoadResult.Error::new, pool);

		return Futures.catching(partialLoadResultFuture,
				IOException.class, LoadResult.Error::new, pool);
	}
}
