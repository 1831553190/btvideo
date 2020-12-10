package com.demo.btvideo.adapter;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.util.Log;
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


public class VideoSingleListAdapter extends PagingDataAdapter<VideoInfo, VideoSingleListAdapter.Holder> {


	public VideoSingleListAdapter(@NotNull DiffUtil.ItemCallback<VideoInfo> diffCallback) {
		super(diffCallback);
	}


	@Override
	public int getItemViewType(int position) {
		return super.getItemViewType(position);
	}

	@NonNull
	@Override
	public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

		return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_linear_videolist,parent,false));
	}

	@Override
	public void onBindViewHolder(@NonNull Holder holder, int position) {
		VideoInfo videoInfo=getItem(position);

			holder.title.setText(videoInfo.getTitle());
			holder.playNum.setText(String.valueOf(videoInfo.getWatchNum()));
			holder.acount.setText(videoInfo.getUserAccount());
			holder.content.setText(videoInfo.getDescription());
			Glide.with(holder.itemView.getContext())
					.load(ServerURL.MAIN_URL+videoInfo.getCoverImage())
					.transition(DrawableTransitionOptions.withCrossFade())
					.error(R.mipmap.load404)
					.into(holder.videoCover);
			Glide.with(holder.itemView.getContext())
					.load(ServerURL.MAIN_URL+videoInfo.getUser().getHeadImage())
					.transition(DrawableTransitionOptions.withCrossFade())
					.error(R.mipmap.load404)
					.into(holder.userCover);

			holder.itemView.setOnClickListener(v -> {
				Intent intent=new Intent(v.getContext(), VideoDetialsActivity.class);
				intent.putExtra("videoInfo",getItem(position));
				intent.putExtra("videoId",getItem(position).getId());
				ActivityOptions options = ActivityOptions
						.makeSceneTransitionAnimation(scanForActivity(holder.itemView.getContext()), holder.videoCover, "cover");
				v.getContext().startActivity(intent,options.toBundle());
			});
	}

	class Holder extends RecyclerView.ViewHolder {
		TextView title,playNum,acount,content;
		ImageView videoCover,userCover;
		public Holder(@NonNull View itemView) {
			super(itemView);
			title=itemView.findViewById(R.id.search_title);
			playNum=itemView.findViewById(R.id.search_playnum);
			acount=itemView.findViewById(R.id.search_user);
			content=itemView.findViewById(R.id.search_content);
			videoCover =itemView.findViewById(R.id.searchc_cover);
			userCover =itemView.findViewById(R.id.search_usercover);
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
