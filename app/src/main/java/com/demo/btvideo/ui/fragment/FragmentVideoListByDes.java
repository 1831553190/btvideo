package com.demo.btvideo.ui.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.CombinedLoadStates;
import androidx.paging.LoadState;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.demo.btvideo.R;
import com.demo.btvideo.adapter.LoadMoreAdapter;
import com.demo.btvideo.adapter.VideoSingleListAdapter;
import com.demo.btvideo.model.VideoInfo;
import com.demo.btvideo.viewmodel.DataLoaderViewModel;
import com.demo.btvideo.viewmodel.DataViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;


//作品页面
public class FragmentVideoListByDes extends Fragment {

	View mainView;
	@BindView(R.id.user_list)
	RecyclerView recyclerView;
	@BindView(R.id.refreshUserData)
	SwipeRefreshLayout swipeRefreshLayout;
	private SharedPreferences pref;

	private static class Holder {
		private static FragmentVideoListByDes instance = new FragmentVideoListByDes();
	}

	public static FragmentVideoListByDes getInstance() {
		return FragmentVideoListByDes.Holder.instance;
	}


	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		mainView = inflater.inflate(R.layout.layout_list_usercenter, container, false);
		ButterKnife.bind(this, mainView);
		pref = PreferenceManager.getDefaultSharedPreferences(getContext());
		VideoSingleListAdapter uploadAdapter = new VideoSingleListAdapter(new DiffUtil.ItemCallback<VideoInfo>() {
			@Override
			public boolean areItemsTheSame(@NonNull VideoInfo oldItem, @NonNull VideoInfo newItem) {
				return oldItem.getId() == newItem.getId();
			}

			@Override
			public boolean areContentsTheSame(@NonNull VideoInfo oldItem, @NonNull VideoInfo newItem) {
				return oldItem.getTitle().equals(newItem.getTitle());
			}
		});

		LoadMoreAdapter header = new LoadMoreAdapter(uploadAdapter::retry);
		LoadMoreAdapter footer = new LoadMoreAdapter(uploadAdapter::retry);
		DataLoaderViewModel dataLoaderViewModel = new ViewModelProvider(this).get(DataLoaderViewModel.class);
		recyclerView.setAdapter(uploadAdapter.withLoadStateHeaderAndFooter(header, footer));


		new ViewModelProvider(requireActivity()).get(DataViewModel.class).loadVideoData().observe(getViewLifecycleOwner(), new Observer<Pair<String,Integer>>() {
			@Override
			public void onChanged(Pair<String,Integer> pair) {
				Log.d("TAG", " " + pair.first);
//				if (pair.second==1){
					dataLoaderViewModel.getUploadVideoD(pair.first).removeObservers(getViewLifecycleOwner());
					dataLoaderViewModel.getUploadVideoD(pair.first).observe(getViewLifecycleOwner(), collectionPagingData -> {
						uploadAdapter.submitData(getLifecycle(), collectionPagingData);
					});
//				}
			}
		});
		swipeRefreshLayout.setOnRefreshListener(() -> {
			uploadAdapter.refresh();
//			swipeRefreshLayout.setRefreshing(false);
		});
		uploadAdapter.addLoadStateListener(new Function1<CombinedLoadStates, Unit>() {
			@Override
			public Unit invoke(CombinedLoadStates loadStates) {
				if (uploadAdapter.getItemCount() == 0) {
					header.setLoadState(loadStates.getRefresh());
				}
				if (loadStates.getRefresh() instanceof LoadState.NotLoading) {
					swipeRefreshLayout.setRefreshing(false);
				}
				if (loadStates.getRefresh() instanceof LoadState.Loading) {
					if (uploadAdapter.getItemCount() == 0) {
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
//		uploadAdapter.setOnItemClickListener(new UploadAdapter.OnItemClick() {
//			@Override
//			public void onItemClick(View view, VideoInfo videoInfo, int pos) {
//				Intent intent=new Intent(getContext(), VideoDetialsActivity.class);
//				intent.putExtra("videoId",videoInfo.getId());
//				intent.putExtra("videoInfo",videoInfo);
////				intent.putExtra("msgId",item.getId());
////                ActivityOptions options = ActivityOptions
////                        .makeSceneTransitionAnimation(MessageActivity.this, view, "upload_cover");
//				startActivity(intent);
//			}
//		});

		return mainView;
	}
}
