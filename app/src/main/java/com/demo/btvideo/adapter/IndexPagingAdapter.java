package com.demo.btvideo.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.util.ObjectsCompat;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.demo.btvideo.R;
import com.demo.btvideo.model.NetworkState;
import com.demo.btvideo.model.VideoInfo;
import com.demo.btvideo.ui.activity.VideoDetialsActivity;

public class IndexPagingAdapter extends PagedListAdapter<VideoInfo, RecyclerView.ViewHolder> {

	private final String TAG = "IndexAdapter";
	private NetworkState mNetworkState;
	private OnRetryListener mRetryListener;
	private String mErrorMsg;

	OnItemClick onItemClick;


	public interface OnItemClick {
		void onItemClick(View v, int pos);
	}

	public void setOnItemClickListener(OnItemClick onItemClick) {
		this.onItemClick = onItemClick;
	}


	public IndexPagingAdapter(@NonNull DiffUtil.ItemCallback<VideoInfo> diffCallback) {
		super(diffCallback);
		mNetworkState = NetworkState.create();
	}

	public void addRetryListener(@NonNull OnRetryListener listener) {
		mRetryListener = listener;
	}

	public void setErrorMsg(@NonNull String msg) {
		this.mErrorMsg = msg;
	}

	@NonNull
	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		if (viewType == R.layout.index_footer) {
			View item = LayoutInflater.from(parent.getContext())
					.inflate(R.layout.index_footer, parent, false);
			return new FooterHolder(item, mRetryListener);
		} else {
			View item = LayoutInflater.from(parent.getContext())
					.inflate(R.layout.item_video_info, parent, false);
			return new WanHolder(item);
		}
	}

	@Override
	public int getItemViewType(int position) {
		if (hasExtraRow() && position == getItemCount() - 1) {
			// loading
			return R.layout.index_footer;
		} else {
			return R.layout.item_video_info;
		}
	}

	@Override
	public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
		if (footer(holder)) {
			return;
		}

		if (holder instanceof WanHolder) {
			WanHolder wanHolder = (WanHolder) holder;
			VideoInfo videoInfo = getItem(position);
			if (videoInfo == null) {
				return;
			}

			Context context = wanHolder.itemView.getContext();
			// 标题
			wanHolder.tvTitle.setText(videoInfo.getTitle());
			Glide.with(holder.itemView.getContext())
					.load(getItem(position).getCoverImage())
					.transition(DrawableTransitionOptions.withCrossFade())
					.error(R.mipmap.imglogin)
					.into(wanHolder.cover);

//			if (onItemClick!=null){
			holder.itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(context, VideoDetialsActivity.class);
					intent.putExtra("videoInfo",getItem(position));
					context.startActivity(intent);
				}
			});
//			}
			// 时间
//			wanHolder.tvTime.setText(videoInfo.getNiceDate());
			// 来自
//			String uer = TextUtils.isEmpty(videoInfo.getAuthor()) ? videoInfo.getShareUser() : videoInfo.getAuthor();
//			String from = context.getResources().getString(R.string.wan_item_from, uer);
//			wanHolder.tvUser.setText(from);
//			// 分类
//			String cls = videoInfo.getSuperChapterName() + "/" + videoInfo.getChapterName();
//			String classification = context.getResources().getString(R.string.wan_item_class, cls);
//			wanHolder.tvClass.setText(classification);
		}
	}

	private boolean footer(@NonNull RecyclerView.ViewHolder holder) {
		if (holder instanceof FooterHolder) {
			FooterHolder footerHolder = (FooterHolder) holder;
			if (mNetworkState.isLoadFailed()) {
				footerHolder.pb.setVisibility(View.GONE);
				footerHolder.bt.setVisibility(View.VISIBLE);
				footerHolder.tvMsg.setVisibility(View.VISIBLE);
				footerHolder.tvMsg.setText(mErrorMsg);
			} else {
				footerHolder.pb.setVisibility(View.VISIBLE);
				footerHolder.bt.setVisibility(View.GONE);
				footerHolder.tvMsg.setVisibility(View.GONE);
			}

			return true;
		}

		return false;
	}

	@Override
	public int getItemCount() {
		return super.getItemCount() + (hasExtraRow() ? 1 : 0);
	}

	/**
	 * 根据网络请求状态结果控制展示 Footer UI
	 */
	public void setNetworkState(int state) {
		Log.d(TAG, "loading state = " + state);
		int preState = mNetworkState.getState();
		boolean oldEx = hasExtraRow();
		Log.d(TAG, "oldEx = " + oldEx);
		mNetworkState.setState(state);
		boolean newEx = hasExtraRow();
		Log.d(TAG, "newEx = " + newEx);
		if (!ObjectsCompat.equals(oldEx, newEx)) {
			// 说明 Footer UI 需要切换，滑倒了最底部
			if (oldEx) {
				notifyItemRemoved(super.getItemCount());
			} else {
				notifyItemInserted(super.getItemCount());
			}
		} else if (newEx && !ObjectsCompat.equals(preState, state)) {
			// 失败时
			notifyItemChanged(getItemCount() - 1);
		}
	}

	private boolean hasExtraRow() {
		return !mNetworkState.isIdle() && !mNetworkState.isLoaded();
	}

	private static class FooterHolder extends RecyclerView.ViewHolder {

		private ProgressBar pb;
		private TextView tvMsg;
		private Button bt;


		private FooterHolder(@NonNull View itemView, @NonNull OnRetryListener listener) {
			super(itemView);
			pb = itemView.findViewById(R.id.item_wan_footer_pb);
			tvMsg = itemView.findViewById(R.id.item_wan_footer_tv_msg);
			bt = itemView.findViewById(R.id.item_wan_footer_bt_retry);
			bt.setOnClickListener((v) -> {
				listener.onRetry();
			});
		}
	}

	/**
	 * 点击重试按钮监听
	 */
	public interface OnRetryListener {
		void onRetry();
	}

	private static class WanHolder extends RecyclerView.ViewHolder {

		private ImageView cover;
		private TextView tvTitle;
		private TextView tvUser;
		private TextView tvClass;
		private TextView tvTime;

		private WanHolder(@NonNull View itemView) {
			super(itemView);
			tvTitle = itemView.findViewById(R.id.text_introduceOfVideo);
			cover = itemView.findViewById(R.id.img_videoPicture);

//			tvUser = itemView.findViewById(R.id.item_wan_tv_user_name);
//			tvClass = itemView.findViewById(R.id.item_wan_tv_classification);
//			tvTime = itemView.findViewById(R.id.item_wan_tv_time);
		}
	}
}