package com.demo.btvideo.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
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
import com.demo.btvideo.adapter.AttentionAdapter;
import com.demo.btvideo.adapter.LoadMoreAdapter;
import com.demo.btvideo.model.Collection;
import com.demo.btvideo.model.Attention;
import com.demo.btvideo.ui.activity.AttctionActivity;
import com.demo.btvideo.ui.activity.UploadHistoryActivity;
import com.demo.btvideo.ui.activity.VideoDetialsActivity;
import com.demo.btvideo.viewmodel.DataLoaderViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;


//用户中心的关注界面
public class FragmentAttention extends Fragment {

	View mainView;
	@BindView(R.id.user_list)
	RecyclerView recyclerView;
	@BindView(R.id.refreshUserData)
	SwipeRefreshLayout swipeRefreshLayout;

	private static class Holder {
		private static FragmentAttention instance = new FragmentAttention();
	}

	public static FragmentAttention getInstance() {
		return FragmentAttention.Holder.instance;
	}


	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		mainView = inflater.inflate(R.layout.layout_list_usercenter, container, false);
		ButterKnife.bind(this, mainView);
		AttentionAdapter attentionAdapter = new AttentionAdapter(new DiffUtil.ItemCallback<Attention>() {
			@Override
			public boolean areItemsTheSame(@NonNull Attention oldItem, @NonNull Attention newItem) {
				return oldItem.getId() == newItem.getId();
			}

			@Override
			public boolean areContentsTheSame(@NonNull Attention oldItem, @NonNull Attention newItem) {
				return oldItem.getUp().getUsername().equals(newItem.getUp().getUsername());
			}
		});

		LoadMoreAdapter header=new LoadMoreAdapter(attentionAdapter::retry);
		LoadMoreAdapter footer=new LoadMoreAdapter(attentionAdapter::retry);
		DataLoaderViewModel dataLoaderViewModel = new ViewModelProvider(this).get(DataLoaderViewModel.class);
		recyclerView.setAdapter(attentionAdapter.withLoadStateHeaderAndFooter(header,footer));
		dataLoaderViewModel.getAttention().observe(getViewLifecycleOwner(), collectionPagingData -> {
			attentionAdapter.submitData(getLifecycle(), collectionPagingData);
		});
		swipeRefreshLayout.setOnRefreshListener(()->{
			attentionAdapter.refresh();
//			swipeRefreshLayout.setRefreshing(false);
		});
		attentionAdapter.addLoadStateListener(new Function1<CombinedLoadStates, Unit>() {
			@Override
			public Unit invoke(CombinedLoadStates loadStates) {
				if (attentionAdapter.getItemCount()==0) {
					header.setLoadState(loadStates.getRefresh());
				}
				if (loadStates.getRefresh()  instanceof LoadState.NotLoading){
					swipeRefreshLayout.setRefreshing(false);
				}
				if (loadStates.getRefresh() instanceof LoadState.Loading){
					if (attentionAdapter.getItemCount()==0){
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
		attentionAdapter.setOnItemClickListener(new AttentionAdapter.OnItemClick() {
			@Override
			public void onItemClick(View view, Attention item, int  pos) {
				Intent intent=new Intent(getContext(), UploadHistoryActivity.class);
				intent.putExtra("account",item.getFollowAccount());
//                ActivityOptions options = ActivityOptions
//                        .makeSceneTransitionAnimation(MessageActivity.this, view, "upload_cover");
				startActivity(intent);
//                finish();
			}
		});
		return mainView;
	}
}
