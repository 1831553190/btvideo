package com.demo.btvideo.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
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
import androidx.paging.PagingData;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.demo.btvideo.R;
import com.demo.btvideo.adapter.CollectionAdapter;
import com.demo.btvideo.adapter.LoadMoreAdapter;
import com.demo.btvideo.model.Collection;
import com.demo.btvideo.model.Comment;
import com.demo.btvideo.ui.activity.CollectionActivity;
import com.demo.btvideo.ui.activity.VideoDetialsActivity;
import com.demo.btvideo.viewmodel.DataLoaderViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;



//用户中心-收藏界面
public class FragmentCollection extends Fragment {

	View mainView;
	@BindView(R.id.user_list)
	RecyclerView recyclerView;
	@BindView(R.id.refreshUserData)
	SwipeRefreshLayout swipeRefreshLayout;

	private static class Holder {
		private static FragmentCollection instance = new FragmentCollection();
	}

	public static FragmentCollection getInstance() {
		return FragmentCollection.Holder.instance;
	}


	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		mainView = inflater.inflate(R.layout.layout_list_usercenter, container, false);
		ButterKnife.bind(this, mainView);
		CollectionAdapter collectionAdapter = new CollectionAdapter(new DiffUtil.ItemCallback<Collection>() {
			@Override
			public boolean areItemsTheSame(@NonNull Collection oldItem, @NonNull Collection newItem) {
				return oldItem.getId() == newItem.getId();
			}

			@Override
			public boolean areContentsTheSame(@NonNull Collection oldItem, @NonNull Collection newItem) {
				return oldItem.getData().getTitle().equals(newItem.getData().getTitle());
			}
		});
		LoadMoreAdapter header=new LoadMoreAdapter(collectionAdapter::retry);
		LoadMoreAdapter footer=new LoadMoreAdapter(collectionAdapter::retry);
		DataLoaderViewModel dataLoaderViewModel = new ViewModelProvider(this).get(DataLoaderViewModel.class);
		recyclerView.setAdapter(collectionAdapter.withLoadStateHeaderAndFooter(header,footer));
		dataLoaderViewModel.getCollections().observe(getViewLifecycleOwner(), collectionPagingData -> {
			collectionAdapter.submitData(getLifecycle(), collectionPagingData);
		});
		swipeRefreshLayout.setOnRefreshListener(()->{
			collectionAdapter.refresh();
		});
		collectionAdapter.addLoadStateListener(new Function1<CombinedLoadStates, Unit>() {
			@Override
			public Unit invoke(CombinedLoadStates loadStates) {
				if (collectionAdapter.getItemCount()==0) {
					header.setLoadState(loadStates.getRefresh());
				}
				if (loadStates.getRefresh()  instanceof LoadState.NotLoading){
					swipeRefreshLayout.setRefreshing(false);
				}
				if (loadStates.getRefresh() instanceof LoadState.Loading){
					if (collectionAdapter.getItemCount()==0){
						header.setLoadState(loadStates.getRefresh());
					}
				}
				return null;
			}
		});

		collectionAdapter.setOnItemClickListener(new CollectionAdapter.OnItemClick() {
			@Override
			public void onItemClick(View view, Collection item, int  pos) {
				Intent intent=new Intent(getContext(), VideoDetialsActivity.class);
				intent.putExtra("videoId",item.getVideoId());
				intent.putExtra("videoInfo",item.getData());
//				intent.putExtra("msgId",item.getId());
//                ActivityOptions options = ActivityOptions
//                        .makeSceneTransitionAnimation(MessageActivity.this, view, "upload_cover");
				startActivity(intent);
//				getActivity().finish();
			}
		});

		return mainView;
	}
}
