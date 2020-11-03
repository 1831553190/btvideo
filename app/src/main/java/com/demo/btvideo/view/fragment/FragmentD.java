package com.demo.btvideo.view.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.demo.btvideo.R;
import com.demo.btvideo.adapter.DataSourceFactory;
import com.demo.btvideo.adapter.IndexPagingAdapter;
import com.demo.btvideo.model.VideoInfo;
import com.demo.btvideo.viewmodel.MainViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FragmentD extends Fragment {

	private static FragmentD fragmentIndex;

	public static FragmentD getInstance(){
		if (fragmentIndex==null){
			synchronized (FragmentD.class){
				if (fragmentIndex==null){
					fragmentIndex=new FragmentD();
				}
			}
		}
		return fragmentIndex;
	}

	View mainView;
	@BindView(R.id.index_recycler)
	RecyclerView recyclerView;
	@BindView(R.id.index_reflash)
	SwipeRefreshLayout swipeRefreshLayout;
	@BindView(R.id.paging_pb)
	ContentLoadingProgressBar mPb;


//	IndexAdapter indexAdapter;
	IndexPagingAdapter pagingAdapter;
	List<VideoInfo> videoInfos;
	private boolean mIsLoadInitial = true;
	private Runnable mRetryAction;
	Handler handler=new Handler(Looper.getMainLooper());
	ViewModelProvider provider;
	MainViewModel mainViewModel;
	PagedList.Config pagedListConfig;
	ExecutorService executorService= Executors.newCachedThreadPool();






	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		mainView=inflater.inflate(R.layout.fragment_main,container,false);
		ButterKnife.bind(this,mainView);
		videoInfos=new ArrayList<>();
		provider = ViewModelProviders.of(this);
		mainViewModel = provider.get(MainViewModel.class);


		pagingAdapter=new IndexPagingAdapter(new DiffUtil.ItemCallback<VideoInfo>() {
			@Override
			public boolean areItemsTheSame(@NonNull VideoInfo oldItem, @NonNull VideoInfo newItem) {
				return oldItem.getId()==newItem.getId();
			}

			@Override
			public boolean areContentsTheSame(@NonNull VideoInfo oldItem, @NonNull VideoInfo newItem) {
				return oldItem.getTitle().equals(newItem.getTitle());
			}
		});
		pagingAdapter.addRetryListener(() -> {
			if (mRetryAction == null) {
				return;
			}
			executorService.execute(mRetryAction);
		});
		recyclerView.setAdapter(pagingAdapter);
		swipeRefreshLayout.setOnRefreshListener(() -> {
			pagingAdapter.submitList(null);
			initProvider();
			swipeRefreshLayout.setRefreshing(false);
		});
		mainViewModel.errorMsgLiveData().observe(getActivity(), pagingAdapter::setErrorMsg);
		mainViewModel.retryLiveData().observe(getActivity(), action -> mRetryAction = action);
		mainViewModel.networkStateLiveData().observe(getActivity(), pagingAdapter::setNetworkState);
		pagedListConfig = new PagedList.Config.Builder()
				// 分页加载的数量
				.setPageSize(20)
				// 是否启动PlaceHolders
				.setEnablePlaceholders(false)
				.build();
		initProvider();
		return mainView;
	}


	private void initProvider() {

		LiveData<PagedList<VideoInfo>> wanLiveData =
				new LivePagedListBuilder<>(new DataSourceFactory(mainViewModel), pagedListConfig).build();
		wanLiveData.observe(getActivity(), list -> {
			pagingAdapter.submitList(list);
			if (mIsLoadInitial) {
				mPb.setVisibility(View.GONE);
				mIsLoadInitial = false;
				Log.e("TAG", "-->" + list.size());
			}
		});
	}

}
