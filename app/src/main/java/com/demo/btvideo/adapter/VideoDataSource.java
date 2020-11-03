package com.demo.btvideo.adapter;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.paging.PageKeyedDataSource;

import com.demo.btvideo.model.Msg;
import com.demo.btvideo.model.NetworkState;
import com.demo.btvideo.model.PageInfo;
import com.demo.btvideo.model.VideoInfo;
import com.demo.btvideo.net.NetInterface;
import com.demo.btvideo.utils.NetWorkUtils;
import com.demo.btvideo.viewmodel.MainViewModel;

import java.util.HashMap;
import java.util.List;

import retrofit2.Retrofit;

public class VideoDataSource extends PageKeyedDataSource<Integer, VideoInfo> {

	private static final String TAG = VideoDataSource.class.getSimpleName();

	private MainViewModel mainViewModel;

	private boolean mIsLoadInitial = true;
	Handler handler=new Handler(Looper.getMainLooper());

	public VideoDataSource(MainViewModel mainViewModel) {
		this.mainViewModel = mainViewModel;
	}

	/**
	 * 初始化数据: 第 1 页
	 */
	@Override
	public void loadInitial(@NonNull LoadInitialParams<Integer> params,
	                        @NonNull LoadInitialCallback<Integer, VideoInfo> callback) {
		load(0, new LoadCallback() {
			@Override
			public void onSuccess(@NonNull List<VideoInfo> list) {
				callback.onResult(list, 0, 1);
			}

			@Override
			public void onFailed(@NonNull String msg) {
				Log.d(TAG, msg);
				mainViewModel.postRetry(() -> loadInitial(params, callback));
			}
		});
	}

	/**
	 * 上滑: 前一页
	 */
	@Override
	public void loadBefore(@NonNull LoadParams<Integer> params,
	                       @NonNull PageKeyedDataSource.LoadCallback<Integer, VideoInfo> callback) {
		// 如果 loadInitial 从 0 开始，这个方法忽略
	}

	/**
	 * 下滑: 后一页
	 */
	@Override
	public void loadAfter(@NonNull LoadParams<Integer> params,
	                      @NonNull PageKeyedDataSource.LoadCallback<Integer, VideoInfo> callback) {
		int page = params.key;
		load(page, new LoadCallback() {
			@Override
			public void onSuccess(@NonNull List<VideoInfo> list) {
				callback.onResult(list, page + 1);
			}

			@Override
			public void onFailed(@NonNull String msg) {
				Log.d(TAG, msg);
				mainViewModel.postRetry(() -> loadAfter(params, callback));
			}
		});
	}

	private void load(@IntRange(from = 0) int page, @NonNull LoadCallback callback) {



		if (!mIsLoadInitial) {
			mainViewModel.postNetworkState(NetworkState.RUNNING);
		}

		mIsLoadInitial = false;
		HashMap<String,Integer> map=new HashMap<>();
		map.put("page",page);
		Retrofit retrofit=NetWorkUtils.getRetrofit();
		NetInterface netInterface=retrofit.create(NetInterface.class);
		netInterface.getPageInfo(page).enqueue(new retrofit2.Callback<Msg<PageInfo>>() {
			@Override
			public void onResponse(retrofit2.Call<Msg<PageInfo>> call, retrofit2.Response<Msg<PageInfo>> response) {
				handler.post(()->{
					if (response.isSuccessful()){
						Msg<PageInfo> msg=response.body();
						assert msg != null;
						if (msg.getCode()==200){
							mainViewModel.postNetworkState(NetworkState.SUCCESS);
							List<VideoInfo> videoInfoList=msg.getData().getList();
							callback.onSuccess(videoInfoList);
						}else{
							mainViewModel.postNetworkState(NetworkState.FAILED);
							mainViewModel.postErrorMsg(msg.getMessage());
							callback.onFailed(msg.getMessage());
						}
					}else{
						mainViewModel.postNetworkState(NetworkState.FAILED);
						mainViewModel.postErrorMsg("请求错误");
						callback.onFailed("请求错误");
					}
				});

			}

			@Override
			public void onFailure(retrofit2.Call<Msg<PageInfo>> call, Throwable t) {
				mainViewModel.postNetworkState(NetworkState.FAILED);
				mainViewModel.postErrorMsg("请求错误");
				callback.onFailed("请求错误");
			}
		});
	}

	private interface LoadCallback {
		void onSuccess(@NonNull List<VideoInfo> list);

		void onFailed(@NonNull String msg);
	}
}
