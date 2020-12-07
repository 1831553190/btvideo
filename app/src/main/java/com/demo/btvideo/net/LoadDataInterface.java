package com.demo.btvideo.net;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.List;

public interface LoadDataInterface<T> {
	ListenableFuture<List<T>>load(int pageNum);
}
