package com.demo.btvideo.adapter;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.paging.PagingDataAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.demo.btvideo.R;
import com.demo.btvideo.model.VideoInfo;
import com.demo.btvideo.net.ServerURL;
import com.demo.btvideo.ui.activity.VideoDetialsActivity;

import org.jetbrains.annotations.NotNull;


public class VideoListAdapter extends PagingDataAdapter<VideoInfo, VideoListAdapter.Holder> {


	public VideoListAdapter(@NotNull DiffUtil.ItemCallback<VideoInfo> diffCallback) {
		super(diffCallback);
	}


	@Override
	public int getItemViewType(int position) {
		return super.getItemViewType(position);
	}

	@NonNull
	@Override
	public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video_info,parent,false));
	}

	@Override
	public void onBindViewHolder(@NonNull Holder holder, int position) {
		VideoInfo videoInfo=getItem(position);

			holder.title.setText(videoInfo.getTitle());
			holder.playNum.setText(String.valueOf(videoInfo.getWatchNum()));
			holder.like.setText(String.valueOf(videoInfo.getPraiseNum()));
			holder.label.setText(videoInfo.getLabels());
			Glide.with(holder.itemView.getContext())
					.load(ServerURL.MAIN_URL+videoInfo.getCoverImage())
					.transition(DrawableTransitionOptions.withCrossFade())
					.error(R.mipmap.load404)
					.into(holder.cover);

			holder.itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent=new Intent(v.getContext(), VideoDetialsActivity.class);
					intent.putExtra("videoInfo",getItem(position));
					intent.putExtra("videoId",getItem(position).getId());
					ActivityOptions options = ActivityOptions
							.makeSceneTransitionAnimation(scanForActivity(holder.itemView.getContext()), holder.cover, "cover");
					v.getContext().startActivity(intent,options.toBundle());
				}
			});
	}

	class Holder extends RecyclerView.ViewHolder {
		TextView title,playNum,like,label;
		ImageView cover;
		public Holder(@NonNull View itemView) {
			super(itemView);
			title=itemView.findViewById(R.id.text_introduceOfVideo);
			playNum=itemView.findViewById(R.id.text_numberOfPlayer);
			like=itemView.findViewById(R.id.text2_numberOfLove);
			label=itemView.findViewById(R.id.text_videoClass);
			cover=itemView.findViewById(R.id.img_videoPicture);
		}
	}

	private static Activity scanForActivity(Context cont) {
		if (cont == null)
			return null;
		else if (cont instanceof Activity)
			return (Activity) cont;
		else if (cont instanceof ContextWrapper)
			return scanForActivity(((ContextWrapper) cont).getBaseContext());

		return null;
	}

}
