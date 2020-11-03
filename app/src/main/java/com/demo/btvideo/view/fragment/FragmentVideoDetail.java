package com.demo.btvideo.view.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.paging.PagingData;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.demo.btvideo.R;
import com.demo.btvideo.model.Comment;
import com.demo.btvideo.model.VideoInfo;
import com.demo.btvideo.viewmodel.DataViewModel;
import com.demo.btvideo.viewmodel.LoadMoreViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import tcking.github.com.giraffeplayer2.GiraffePlayer;
import tcking.github.com.giraffeplayer2.PlayerManager;

import static androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE;

public class FragmentVideoDetail extends Fragment {
	View mainView;
	@BindView(R.id.video_detail_comment_recycler)
	RecyclerView recyclerView;
	@BindView(R.id.text_video_title)
	TextView textTitle;
	@BindView(R.id.text_video_user)
	TextView textUser;
	@BindView(R.id.text_video_description)
	TextView textDesxription;


	private static class Holder {
		private static FragmentVideoDetail instance = new FragmentVideoDetail();
	}

	public static FragmentVideoDetail getInstance() {
		return FragmentVideoDetail.Holder.instance;
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		mainView = inflater.inflate(R.layout.fragment_video_detail, container, false);
		ButterKnife.bind(this, mainView);
		new ViewModelProvider(getActivity()).get(DataViewModel.class)
				.videoData()
				.observe(getViewLifecycleOwner(), videoInfo -> {
							textTitle.setText(videoInfo.getTitle());
							textUser.setText(videoInfo.getUsername());
							textDesxription.setText(videoInfo.getDescription());
						}
				);

		new LoadMoreViewModel().getCommnet(0).observe(getViewLifecycleOwner(), new Observer<PagingData<Comment>>() {
			@Override
			public void onChanged(PagingData<Comment> commentPagingData) {

			}
		});
		recyclerView.addItemDecoration(new DividerItemDecoration(mainView.getContext(),DividerItemDecoration.VERTICAL));

//		recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//			@Override
//			public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
//				super.onScrollStateChanged(recyclerView, newState);
//				if (newState == SCROLL_STATE_IDLE) {
//						if (((LinearLayoutManager)recyclerView.getLayoutManager()).findFirstVisibleItemPosition() == 0) {//No visible part > 50%,play float
//							PlayerManager.getInstance().getCurrentPlayer().setDisplayModel(GiraffePlayer.DISPLAY_NORMAL);
//						} else {
//							PlayerManager.getInstance().getCurrentPlayer().setDisplayModel(GiraffePlayer.DISPLAY_FLOAT);
//						}
//
//				}
//			}
//		});
		recyclerView.setAdapter(new RecyclerView.Adapter() {
			@NonNull
			@Override
			public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
				Log.d("TAG", "onCreateViewHolder: +create");
				return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment,parent,false));
			}

			@Override
			public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

			}

			@Override
			public int getItemCount() {
				return 100;
			}

			class Holder extends RecyclerView.ViewHolder {

				public Holder(@NonNull View itemView) {
					super(itemView);
				}
			}
		});


		return mainView;
	}
}
