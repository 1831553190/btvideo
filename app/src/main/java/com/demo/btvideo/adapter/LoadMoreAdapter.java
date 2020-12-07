package com.demo.btvideo.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.paging.LoadState;
import androidx.paging.LoadStateAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.demo.btvideo.R;

import org.jetbrains.annotations.NotNull;

public class LoadMoreAdapter extends LoadStateAdapter<LoadMoreAdapter.MyHolder> {


	RetryCallback callback;
	MyHolder holder;

	public LoadMoreAdapter(RetryCallback callback) {
		this.callback = callback;
	}

	@Override
	public void onBindViewHolder(@NotNull MyHolder myHolder, @NotNull LoadState loadState) {
		if (loadState instanceof LoadState.NotLoading){
			myHolder.progressBar.setVisibility(View.GONE);
			myHolder.btnReload.setVisibility(View.GONE);
		}else if (loadState instanceof LoadState.Error){
			myHolder.progressBar.setVisibility(View.GONE);
			myHolder.btnReload.setVisibility(View.VISIBLE);
		}else if (loadState instanceof LoadState.Loading){
			myHolder.progressBar.setVisibility(View.VISIBLE);
			myHolder.btnReload.setVisibility(View.GONE);
		}


		if (callback!=null){
			myHolder.btnReload.setOnClickListener(v->{
				callback.retry();
			});
		}

	}




	@NotNull
	@Override
	public MyHolder onCreateViewHolder(@NotNull ViewGroup viewGroup, @NotNull LoadState loadState) {
		MyHolder holder= new MyHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.index_footer,viewGroup,false));
		this.holder=holder;
		return holder;
	}
	public interface RetryCallback{
		void retry();
	}


	class MyHolder extends RecyclerView.ViewHolder{
		private ProgressBar progressBar;
		Button btnReload;
		public MyHolder(@NonNull View itemView) {
			super(itemView);
			progressBar=itemView.findViewById(R.id.item_wan_footer_pb);
			btnReload=itemView.findViewById(R.id.item_wan_footer_bt_retry);
		}
	}
}




