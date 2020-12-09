package com.demo.btvideo.adapter;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.UserManager;
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
import com.demo.btvideo.model.UserMessage;
import com.demo.btvideo.model.VideoInfo;
import com.demo.btvideo.net.ServerURL;
import com.demo.btvideo.ui.activity.VideoDetialsActivity;

import org.jetbrains.annotations.NotNull;


public class UserMessageAdapter extends PagingDataAdapter<UserMessage, UserMessageAdapter.Holder> {

	OnItemClick onItemClick;


	public UserMessageAdapter(@NotNull DiffUtil.ItemCallback<UserMessage> diffCallback) {
		super(diffCallback);
	}


	public interface OnItemClick{
		void onItemClidk(View view,UserMessage userMessage,int pos);
	}

	public void setOnItemClickListener(OnItemClick itemClickListener){
		this.onItemClick=itemClickListener;
	}


	@Override
	public int getItemViewType(int position) {
		return super.getItemViewType(position);
	}

	@NonNull
	@Override
	public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_baseitem,parent,false));
	}

	@Override
	public void onBindViewHolder(@NonNull Holder holder, int position) {
		UserMessage userMessage=getItem(position);

			holder.title.setText(userMessage.getSubscriberAccount());
			holder.content.setText(String.valueOf(userMessage.getContent()));
			Glide.with(holder.itemView.getContext())
					.load(ServerURL.MAIN_URL+userMessage.getVideo().getCoverImage())
					.transition(DrawableTransitionOptions.withCrossFade())
					.error(R.mipmap.load404)
					.into(holder.cover);

			holder.itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (onItemClick!=null){
						onItemClick.onItemClidk(holder.cover,userMessage,position);
					}
				}
			});
	}

	class Holder extends RecyclerView.ViewHolder {
		TextView title,content;
		ImageView cover;
		public Holder(@NonNull View itemView) {
			super(itemView);
			title=itemView.findViewById(R.id.baseitem_title);
			content=itemView.findViewById(R.id.baseitem_content);
			cover=itemView.findViewById(R.id.baseitem_img);
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
