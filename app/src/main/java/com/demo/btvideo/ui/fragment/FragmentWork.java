package com.demo.btvideo.ui.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.CombinedLoadStates;
import androidx.paging.LoadState;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.demo.btvideo.R;
import com.demo.btvideo.adapter.CollectionAdapter;
import com.demo.btvideo.adapter.LoadMoreAdapter;
import com.demo.btvideo.adapter.UploadAdapter;
import com.demo.btvideo.model.Collection;
import com.demo.btvideo.model.VideoInfo;
import com.demo.btvideo.viewmodel.DataLoaderViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class FragmentWork extends Fragment {

	View mainView;
	@BindView(R.id.user_list)
	RecyclerView recyclerView;
	@BindView(R.id.refreshUserData)
	SwipeRefreshLayout swipeRefreshLayout;
	private SharedPreferences pref;

	private static class Holder {
		private static FragmentWork instance = new FragmentWork();
	}

	public static FragmentWork getInstance() {
		return FragmentWork.Holder.instance;
	}


	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		mainView = inflater.inflate(R.layout.layout_list_usercenter, container, false);
		ButterKnife.bind(this, mainView);
		pref= PreferenceManager.getDefaultSharedPreferences(getContext());
		UploadAdapter uploadAdapter = new UploadAdapter(new DiffUtil.ItemCallback<VideoInfo>() {
			@Override
			public boolean areItemsTheSame(@NonNull VideoInfo oldItem, @NonNull VideoInfo newItem) {
				return oldItem.getId() == newItem.getId();
			}

			@Override
			public boolean areContentsTheSame(@NonNull VideoInfo oldItem, @NonNull VideoInfo newItem) {
				return oldItem.getTitle().equals(newItem.getTitle());
			}
		});

		LoadMoreAdapter header=new LoadMoreAdapter(uploadAdapter::retry);
		LoadMoreAdapter footer=new LoadMoreAdapter(uploadAdapter::retry);
		DataLoaderViewModel dataLoaderViewModel = new ViewModelProvider(this).get(DataLoaderViewModel.class);
		recyclerView.setAdapter(uploadAdapter.withLoadStateHeaderAndFooter(header,footer));
		dataLoaderViewModel.getUploadVideo(pref.getString("userNow","")).observe(getViewLifecycleOwner(), collectionPagingData -> {
			uploadAdapter.submitData(getLifecycle(), collectionPagingData);
		});
		swipeRefreshLayout.setOnRefreshListener(()->{
			uploadAdapter.refresh();
//			swipeRefreshLayout.setRefreshing(false);
		});
		uploadAdapter.addLoadStateListener(new Function1<CombinedLoadStates, Unit>() {
			@Override
			public Unit invoke(CombinedLoadStates loadStates) {
				if (uploadAdapter.getItemCount()==0) {
					header.setLoadState(loadStates.getRefresh());
				}
				if (loadStates.getRefresh()  instanceof LoadState.NotLoading){
					swipeRefreshLayout.setRefreshing(false);
				}
				if (loadStates.getRefresh() instanceof LoadState.Loading){
					if (uploadAdapter.getItemCount()==0){
						header.setLoadState(loadStates.getRefresh());
					}
				}
//				if (loadStates.getRefresh() instanceof LoadState.NotLoading){
//					if (collectionAdapter.getItemCount()==0){
//
//					}
//				}
				return null;
			}
		});

		return mainView;
	}
}
