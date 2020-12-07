package com.demo.btvideo.ui.activity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.paging.CombinedLoadStates;
import androidx.paging.LoadState;
import androidx.paging.PagingData;
import androidx.paging.PagingDataAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.demo.btvideo.R;
import com.demo.btvideo.adapter.LoadMoreAdapter;
import com.demo.btvideo.adapter.UserMessageAdapter;
import com.demo.btvideo.model.Comment;
import com.demo.btvideo.model.UserMessage;
import com.demo.btvideo.utils.GlideCircleBorderTransform;
import com.demo.btvideo.viewmodel.DataLoaderViewModel;

import org.jetbrains.annotations.NotNull;

import butterknife.BindView;
import butterknife.ButterKnife;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class MessageActivity extends AppCompatActivity {

    @BindView(R.id.list_message)
    RecyclerView recyclerView;
    @BindView(R.id.msg_empty)
    TextView emptyMsg;

    @BindView(R.id.refreshMessage)
    SwipeRefreshLayout swipeRefreshLayout;

//    @BindView(R.id.refreshCommentList)
//    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int flagTranslucentStatus = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            int flagTranslucentNavigation = WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                WindowManager.LayoutParams attributes = window.getAttributes();
                attributes.flags |= flagTranslucentNavigation;
                window.setAttributes(attributes);
                getWindow().setStatusBarColor(Color.TRANSPARENT);
            } else {
                Window window = getWindow();
                WindowManager.LayoutParams attributes = window.getAttributes();
                attributes.flags |= flagTranslucentStatus | flagTranslucentNavigation;
                window.setAttributes(attributes);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }
        setContentView(R.layout.mseeage_list);
        ButterKnife.bind(this);

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
        LoadMoreAdapter loadMoreAdapter=new LoadMoreAdapter(commentListAdapter::retry);
        LoadMoreAdapter loadHeader=new LoadMoreAdapter(commentListAdapter::retry);
//        new LoadMoreViewModel().getMessage().observe(this, new Observer<PagingData<Comment>>() {
//            @Override
//            public void onChanged(PagingData<Comment> commentPagingData) {
//                commentListAdapter.submitData(getLifecycle(),commentPagingData);
//            }
//        });
        UserMessageAdapter userMessageAdapter=new UserMessageAdapter(new DiffUtil.ItemCallback<UserMessage>() {
            @Override
            public boolean areItemsTheSame(@NonNull UserMessage oldItem, @NonNull UserMessage newItem) {
                return oldItem.getId()==newItem.getId();
            }

            @Override
            public boolean areContentsTheSame(@NonNull UserMessage oldItem, @NonNull UserMessage newItem) {
                return oldItem.getContent().equals(newItem.getContent());
            }
        });
        swipeRefreshLayout.setOnRefreshListener(userMessageAdapter::refresh);
        swipeRefreshLayout.setRefreshing(true);


        recyclerView.setAdapter(userMessageAdapter.withLoadStateHeaderAndFooter(loadHeader,loadMoreAdapter));
        new DataLoaderViewModel().getMessage().observe(this, new Observer<PagingData<UserMessage>>() {
            @Override
            public void onChanged(PagingData<UserMessage> commentPagingData) {
                userMessageAdapter.submitData(getLifecycle(),commentPagingData);
            }
        });
        userMessageAdapter.addLoadStateListener(new Function1<CombinedLoadStates, Unit>() {
            @Override
            public Unit invoke(CombinedLoadStates combinedLoadStates) {
                if (userMessageAdapter.getItemCount()==0){
                    loadHeader.setLoadState(combinedLoadStates.getRefresh());
                }
                if (combinedLoadStates.getRefresh() instanceof LoadState.NotLoading||
                        combinedLoadStates.getRefresh() instanceof  LoadState.Error){
                    swipeRefreshLayout.setRefreshing(false);
                }
                if (combinedLoadStates.getRefresh() instanceof LoadState.NotLoading&&userMessageAdapter.getItemCount()==0){
                    emptyMsg.setVisibility(View.VISIBLE);
                }else{
                    emptyMsg.setVisibility(View.GONE);
                }
                return null;
            }
        });

//        swipeRefreshLayout.setOnRefreshListener(()->{
//            commentListAdapter.refresh();
//            swipeRefreshLayout.setRefreshing(false);
//        });

    }




    class CommentListAdapter extends PagingDataAdapter<Comment, CHolder> {

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
                Glide.with(MessageActivity.this)
                        .load(item.getSubscriber().getHeadImage())
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



