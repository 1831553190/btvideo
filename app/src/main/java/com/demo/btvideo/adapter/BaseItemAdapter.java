package com.demo.btvideo.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.paging.PagingDataAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.demo.btvideo.model.BaseItem;

import org.jetbrains.annotations.NotNull;

import kotlinx.coroutines.CoroutineDispatcher;

public class BaseItemAdapter extends PagingDataAdapter<BaseItem<?>, BaseItemAdapter.Holder> {


	public BaseItemAdapter(@NotNull DiffUtil.ItemCallback<BaseItem<?>> diffCallback) {
		super(diffCallback);
	}

	@NonNull
	@Override
	public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		return null;
	}

	@Override
	public void onBindViewHolder(@NonNull Holder holder, int position) {

	}

	class Holder  extends RecyclerView.ViewHolder{
		public Holder(@NonNull View itemView) {
			super(itemView);

		}
	}
}
