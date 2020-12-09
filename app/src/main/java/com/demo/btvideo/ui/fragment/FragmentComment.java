package com.demo.btvideo.ui.fragment;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.CombinedLoadStates;
import androidx.paging.PagingData;
import androidx.paging.PagingDataAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.demo.btvideo.R;
import com.demo.btvideo.adapter.LoadMoreAdapter;
import com.demo.btvideo.model.Comment;
import com.demo.btvideo.model.VideoInfo;
import com.demo.btvideo.net.ServerURL;
import com.demo.btvideo.utils.GlideCircleBorderTransform;
import com.demo.btvideo.viewmodel.DataLoaderViewModel;
import com.demo.btvideo.viewmodel.DataViewModel;

import org.jetbrains.annotations.NotNull;

import butterknife.BindView;
import butterknife.ButterKnife;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;


//视频详情-评价界面
public class FragmentComment extends Fragment {

	@BindView(R.id.video_detail_comment_recycler)
	RecyclerView recyclerView;
	@BindView(R.id.refreshCommentList)
	SwipeRefreshLayout swipeRefreshLayout;

	View mainView;
	private static class Holder {
		private static FragmentComment instance = new FragmentComment();
	}

	public static FragmentComment getInstance() {
		return FragmentComment.Holder.instance;
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		mainView=inflater.inflate(R.layout.fragment_comment,container,false);
		ButterKnife.bind(this,mainView);

		recyclerView.addItemDecoration(new DividerItemDecoration(mainView.getContext(),DividerItemDecoration.VERTICAL));
		CommentListAdapter commentListAdapter=new CommentListAdapter(new DiffUtil.ItemCallback<Comment>() {
			@Override
			public boolean areItemsTheSame(@NonNull Comment oldItem, @NonNull Comment newItem) {
				return oldItem.getId()==newItem.getId();
			}

			@Override
			public boolean areContentsTheSame(@NonNull Comment oldItem, @NonNull Comment newItem) {
				return oldItem.getContent().equals(newItem.getContent());
			}
		});

		DataViewModel dataViewModel = new ViewModelProvider(requireActivity()).get(DataViewModel.class);
		dataViewModel.loadVideoInfo("", "").observe(getViewLifecycleOwner(), new Observer<VideoInfo>() {
			@Override
			public void onChanged(VideoInfo videoInfo) {
				new DataLoaderViewModel().getCommnet(videoInfo.getId()).observe(getViewLifecycleOwner(), new Observer<PagingData<Comment>>() {
					@Override
					public void onChanged(PagingData<Comment> commentPagingData) {
						commentListAdapter.submitData(getLifecycle(),commentPagingData);
					}
				});
			}
		});
		LoadMoreAdapter loadMoreAdapter=new LoadMoreAdapter(commentListAdapter::retry);

		recyclerView.setAdapter(commentListAdapter.withLoadStateFooter(loadMoreAdapter));

		commentListAdapter.addLoadStateListener(new Function1<CombinedLoadStates, Unit>() {
			@Override
			public Unit invoke(CombinedLoadStates combinedLoadStates) {
				loadMoreAdapter.setLoadState(combinedLoadStates.getRefresh());
				return null;
			}
		});

		swipeRefreshLayout.setOnRefreshListener(()->{
			commentListAdapter.refresh();
			swipeRefreshLayout.setRefreshing(false);
		});

		new ViewModelProvider(getActivity(),new ViewModelProvider.NewInstanceFactory())
				.get(DataViewModel.class).updateComment().observe(getViewLifecycleOwner(), new Observer<Integer>() {
			@Override
			public void onChanged(Integer integer) {
				commentListAdapter.refresh();
			}
		});


		return mainView;
	}



	class CommentListAdapter extends PagingDataAdapter<Comment,CHolder> {

		public CommentListAdapter(@NotNull DiffUtil.ItemCallback<Comment> diffCallback) {
			super(diffCallback);
		}

		@NonNull
		@Override
		public CHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
			Log.d("TAG", "onCreateViewHolder: +create");
			return new CHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment,parent,false));
		}

		@Override
		public void onBindViewHolder(@NonNull CHolder holder, int position) {

			Comment item=getItem(position);
			if (item!=null){
				holder.title.setText(item.getSubscriber().getUsername());
				holder.content.setText(item.getContent());
				holder.sendTime.setText(item.getSendTime());
				Glide.with(getContext())
						.load(ServerURL.MAIN_URL+item.getSubscriber().getHeadImage())
						.transition(DrawableTransitionOptions.withCrossFade())
						.transform(new GlideCircleBorderTransform(0, Color.TRANSPARENT))
						.into(holder.profile);

			}
		}

	}


	class CHolder extends RecyclerView.ViewHolder {

		TextView title,content,sendTime;
		ImageView profile;
		public CHolder(@NonNull View itemView) {
			super(itemView);
			title=itemView.findViewById(R.id.item_comment_username);
			content=itemView.findViewById(R.id.item_comment_content);
			profile=itemView.findViewById(R.id.comment_profile);
			sendTime=itemView.findViewById(R.id.item_comment_time);
		}
	}
}
