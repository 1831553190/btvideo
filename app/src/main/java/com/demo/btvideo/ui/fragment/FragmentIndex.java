package com.demo.btvideo.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.LoadState;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.demo.btvideo.R;
import com.demo.btvideo.adapter.LoadMoreAdapter;
import com.demo.btvideo.adapter.VideoListAdapter;
import com.demo.btvideo.model.VideoInfo;
import com.demo.btvideo.viewmodel.DataLoaderViewModel;
import com.demo.btvideo.viewmodel.LoginViewModel;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;


//首页界面
public class FragmentIndex extends Fragment {


	View mainView;
	@BindView(R.id.index_recycler)
	RecyclerView recyclerView;
	@BindView(R.id.index_reflash)
	SwipeRefreshLayout swipeRefreshLayout;

	private static class Holder {
		private static FragmentIndex instance = new FragmentIndex();
	}

	public static FragmentIndex getInstance() {
		return FragmentIndex.Holder.instance;
	}

	VideoListAdapter videoListAdapter;
	ExecutorService pool = Executors.newCachedThreadPool();


	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		mainView = inflater.inflate(R.layout.fragment_main, container, false);
		ButterKnife.bind(this, mainView);
		videoListAdapter = new VideoListAdapter(new DiffUtil.ItemCallback<VideoInfo>() {
			@Override
			public boolean areItemsTheSame(@NonNull VideoInfo oldItem, @NonNull VideoInfo newItem) {
				return oldItem.getId() == newItem.getId();
			}

			@Override
			public boolean areContentsTheSame(@NonNull VideoInfo oldItem, @NonNull VideoInfo newItem) {
				return oldItem.getTitle().equals(newItem.getTitle());
			}
		});
		LoadMoreAdapter loadMoreAdapt=new LoadMoreAdapter(() -> videoListAdapter.retry());
		LoadMoreAdapter headerAdapter=new LoadMoreAdapter(() -> videoListAdapter.retry());
		recyclerView.setAdapter(videoListAdapter.withLoadStateHeaderAndFooter(headerAdapter,loadMoreAdapt));
		swipeRefreshLayout.setOnRefreshListener(() -> {
			videoListAdapter.refresh();
			swipeRefreshLayout.setRefreshing(false);
		});
		videoListAdapter.addLoadStateListener(combinedLoadStates -> {
			if (combinedLoadStates.getRefresh() instanceof LoadState.Loading||combinedLoadStates.getRefresh() instanceof LoadState.Error) {
				if (videoListAdapter.getItemCount() == 0) {
					headerAdapter.setLoadState(combinedLoadStates.getRefresh());
				}
			}
			return null;
		});
		LoginViewModel.getInstance().refreshVideo().observe(getViewLifecycleOwner(), new Observer<Integer>() {
			@Override
			public void onChanged(Integer integer) {
				videoListAdapter.refresh();
			}
		});
		DataLoaderViewModel loadMoreViewModel=new ViewModelProvider(this).get(DataLoaderViewModel.class);
		loadMoreViewModel.getPaging().observe(getViewLifecycleOwner(), videoInfoPagingData ->
				videoListAdapter.submitData(getLifecycle(),videoInfoPagingData));   //提交数据到Adapter
		return mainView;
	}



}

