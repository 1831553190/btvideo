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
import com.demo.btvideo.net.ServerURL;

import org.jetbrains.annotations.NotNull;

public class AttentionAdapter extends PagingDataAdapter<Attention, BaseHolder> {

	OnItemClick onItemClick;
	public AttentionAdapter(@NotNull DiffUtil.ItemCallback<Attention> diffCallback) {
		super(diffCallback);
	}
	public interface OnItemClick{
		void onItemClick(View view, Attention attention, int pos);
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
		Attention attention=getItem(position);
		if (attention!=null&&attention.getUp()!=null){
			if (attention.getUp().getUsername()!=null){
				holder.title.setText(attention.getUp().getUsername());

			}
			holder.label.setText(attention.getUp().getAccount());
			Glide.with(holder.itemView.getContext())
					.load(ServerURL.MAIN_URL+attention.getUp().getHeadImage())
					.transition(DrawableTransitionOptions.withCrossFade())
					.into(holder.cover);


			holder.itemView.setOnClickListener(v -> {
				if (onItemClick!=null){
					onItemClick.onItemClick(holder.cover,attention,position);
				}
			});
		}
	}
}
