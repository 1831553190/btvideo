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
import com.demo.btvideo.model.Funs;
import com.demo.btvideo.model.Funs;
import com.demo.btvideo.net.ServerURL;

import org.jetbrains.annotations.NotNull;

public class FunsAdapter extends PagingDataAdapter<Funs, BaseHolder> {

	OnItemClick onItemClick;
	public FunsAdapter(@NotNull DiffUtil.ItemCallback<Funs> diffCallback) {
		super(diffCallback);
	}
	public interface OnItemClick{
		void onItemClick(View view, Funs funs, int pos);
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
		Funs item=getItem(position);
		if (item!=null&&item.getFans()!=null){
			if (item.getFans().getUsername()!=null){
				holder.title.setText(item.getFans().getUsername());
			}
			holder.label.setText(item.getFans().getAccount());
			Glide.with(holder.itemView.getContext())
					.load(ServerURL.MAIN_URL+item.getFans().getHeadImage())
					.transition(DrawableTransitionOptions.withCrossFade())
					.into(holder.cover);


			holder.itemView.setOnClickListener(v -> {
				if (onItemClick!=null){
					onItemClick.onItemClick(holder.cover,item,position);
				}
			});
		}
	}
}
