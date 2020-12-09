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
import com.demo.btvideo.model.Collection;
import com.demo.btvideo.net.ServerURL;

import org.jetbrains.annotations.NotNull;

public class CollectionAdapter extends PagingDataAdapter<Collection, BaseHolder> {

	OnItemClick onItemClick;
	public CollectionAdapter(@NotNull DiffUtil.ItemCallback<Collection> diffCallback) {
		super(diffCallback);
	}
	public interface OnItemClick{
		void onItemClick(View view, Collection collection, int pos);
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
		Collection collection=getItem(position);
		if (collection!=null&&collection.getData()!=null){
			if (collection.getData().getTitle()!=null){
				holder.title.setText(collection.getData().getTitle());

			}
			holder.label.setText(collection.getCollectTime());
			Glide.with(holder.itemView.getContext())
					.load(ServerURL.MAIN_URL+collection.getData().getCoverImage())
					.transition(DrawableTransitionOptions.withCrossFade())
					.into(holder.cover);


			holder.itemView.setOnClickListener(v -> {
				if (onItemClick!=null){
					onItemClick.onItemClick(holder.cover,collection,position);
				}
			});
		}
	}
}
