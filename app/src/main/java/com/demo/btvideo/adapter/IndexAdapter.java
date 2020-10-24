package com.demo.btvideo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.demo.btvideo.R;
import com.demo.btvideo.model.VideoInfo;

import java.util.List;

public class IndexAdapter extends RecyclerView.Adapter<IndexAdapter.Holder> {

	List<VideoInfo> videoInfoList;
	Context context;

	public IndexAdapter(List<VideoInfo> videoInfoList, Context context) {
		this.videoInfoList = videoInfoList;
		this.context = context;
	}

	@NonNull
	@Override
	public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view= LayoutInflater.from(context).inflate(R.layout.item_video_info,parent,false);
		return new Holder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull Holder holder, int position) {
		Glide.with(context).load(videoInfoList.get(position).getCoverImage())
				.crossFade()
				.into(holder.coverImg);
	}

	@Override
	public int getItemCount() {
		return videoInfoList.size();
	}

	class Holder extends RecyclerView.ViewHolder{
		ImageView coverImg;

		public Holder(@NonNull View itemView) {
			super(itemView);
			coverImg=itemView.findViewById(R.id.img_videoPicture);
		}
	}
}
