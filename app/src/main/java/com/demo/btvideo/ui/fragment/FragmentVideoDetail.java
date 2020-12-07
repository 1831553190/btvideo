package com.demo.btvideo.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.CombinedLoadStates;
import androidx.paging.PagingData;
import androidx.paging.PagingDataAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.demo.btvideo.R;
import com.demo.btvideo.adapter.LoadMoreAdapter;
import com.demo.btvideo.model.Comment;
import com.demo.btvideo.net.ServerURL;
import com.demo.btvideo.utils.GlideCircleBorderTransform;
import com.demo.btvideo.viewmodel.DataViewModel;
import com.demo.btvideo.viewmodel.DataLoaderViewModel;

import org.jetbrains.annotations.NotNull;

import butterknife.BindView;
import butterknife.ButterKnife;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class FragmentVideoDetail extends Fragment {
	View mainView;

	@BindView(R.id.text_video_title)
	TextView textTitle;
	@BindView(R.id.text_video_user)
	TextView textUser;
	@BindView(R.id.text_video_description)
	TextView textDesxription;

	@BindView(R.id.img_video_cover)
	ImageView userPrifile;


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
							textUser.setText(videoInfo.getUser().getUsername());
							textDesxription.setText(videoInfo.getDescription());
							Glide.with(getContext())
									.load(ServerURL.MAIN_URL+videoInfo.getUser().getHeadImage())
									.transform(new GlideCircleBorderTransform(2, Color.WHITE))
									.transition(DrawableTransitionOptions.withCrossFade())
									.into(userPrifile);
						}
				);
		return mainView;
	}



}
