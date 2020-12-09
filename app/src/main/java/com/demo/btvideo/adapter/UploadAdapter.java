package com.demo.btvideo.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.paging.PagingDataAdapter;
import androidx.recyclerview.widget.DiffUtil;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.demo.btvideo.R;
import com.demo.btvideo.model.Attention;
import com.demo.btvideo.model.Collection;
import com.demo.btvideo.model.VideoInfo;
import com.demo.btvideo.net.ServerURL;

import org.jetbrains.annotations.NotNull;

public class UploadAdapter extends PagingDataAdapter<VideoInfo, BaseHolder> {

	OnItemClick onItemClick;

	public UploadAdapter(@NotNull DiffUtil.ItemCallback<VideoInfo> diffCallback) {
		super(diffCallback);
	}
	public interface OnItemClick{
		void onItemClick(View view, VideoInfo videoInfo, int pos);
	}

	public void setOnItemClickListener(OnItemClick itemClickListener){
		this.onItemClick=itemClickListener;
	}

	@NonNull
	@Override
	public BaseHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View v=LayoutInflater.from(parent.getContext()).inflate(R.layout.item_baseitem,parent,false);
		return new BaseHolder(v);
	}

	@Override
	public void onBindViewHolder(@NonNull BaseHolder holder, int position) {
		VideoInfo videoInfo=getItem(position);
		if (videoInfo!=null){
			if (videoInfo.getTitle()!=null){
				holder.title.setText(videoInfo.getTitle());

			}
			holder.label.setText(videoInfo.getDescription());
			Glide.with(holder.itemView.getContext())
					.load(ServerURL.MAIN_URL+videoInfo.getCoverImage())
					.transition(DrawableTransitionOptions.withCrossFade())
					.into(holder.cover);
			holder.itemView.setOnClickListener(v -> {
				if (onItemClick!=null){
					onItemClick.onItemClick(holder.cover,videoInfo,position);
				}
			});
		}
	}
}
