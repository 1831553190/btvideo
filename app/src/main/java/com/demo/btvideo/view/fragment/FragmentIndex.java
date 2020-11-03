package com.demo.btvideo.view.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleCoroutineScope;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.CombinedLoadStates;
import androidx.paging.LoadState;
import androidx.paging.LoadStateAdapter;
import androidx.paging.LoadStates;
import androidx.paging.LoadType;
import androidx.paging.PagingData;
import androidx.paging.rxjava2.PagingRx;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.demo.btvideo.R;
import com.demo.btvideo.adapter.VideoListAdapter;
import com.demo.btvideo.model.VideoInfo;
import com.demo.btvideo.viewmodel.LoadMoreViewModel;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.AutoDisposeConverter;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class FragmentIndex extends Fragment {


	View mainView;
	@BindView(R.id.index_recycler)
	RecyclerView recyclerView;
	@BindView(R.id.index_reflash)
	SwipeRefreshLayout swipeRefreshLayout;

	private boolean mIsLoadInitial = true;
	private Runnable mRetryAction;
	Handler handler = new Handler(Looper.getMainLooper());

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
		LoadMoreAdapter loadMoreAdapt=new LoadMoreAdapter();
		videoListAdapter.withLoadStateFooter(loadMoreAdapt);
		recyclerView.setAdapter(videoListAdapter);
		swipeRefreshLayout.setOnRefreshListener(() -> {
			videoListAdapter.refresh();
			swipeRefreshLayout.setRefreshing(false);
		});
//		LoadMoreViewModel testViewModel = new LoadMoreViewModel();

		LoadMoreViewModel loadMoreViewModel=new ViewModelProvider(this).get(LoadMoreViewModel.class);
		loadMoreViewModel.getPaging().observe(getViewLifecycleOwner(), videoInfoPagingData ->
				videoListAdapter.submitData(getLifecycle(),videoInfoPagingData));
		return mainView;
	}

	class LoadMoreAdapter extends LoadStateAdapter<MyHolder>{



		@Override
			public void onBindViewHolder(@NotNull MyHolder myHolder, @NotNull LoadState loadState) {
//			if (loadState instanceof LoadState.Error){
				Log.d("loaf", "loadState: ");
				myHolder.progressBar.setVisibility(View.VISIBLE);
//			}
		}



		@Override
		public int getStateViewType(@NotNull LoadState loadState) {
			return -1;
		}

		@Override
		public boolean displayLoadStateAsItem(@NotNull LoadState loadState) {
			return true;
		}

		@NotNull
		@Override
		public MyHolder onCreateViewHolder(@NotNull ViewGroup viewGroup, @NotNull LoadState loadState) {
			Log.d("TAG", "onCreateViewHolder: loadmore");
			return new MyHolder(LayoutInflater.from(getContext()).inflate(R.layout.index_footer,viewGroup,false));
		}
	}

	class MyHolder extends RecyclerView.ViewHolder{
		private ProgressBar progressBar;
		public MyHolder(@NonNull View itemView) {
			super(itemView);
			progressBar=itemView.findViewById(R.id.item_wan_footer_pb);
		}
	}
}

