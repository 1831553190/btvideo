package com.demo.btvideo.ui.activity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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


//消息界面
public class MessageActivity extends BaseActivity {

    @BindView(R.id.list_message)
    RecyclerView recyclerView;
    @BindView(R.id.msg_empty)
    TextView emptyMsg;

    @BindView(R.id.refreshMessage)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.baseActivity_title)
    TextView baseTitle;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mseeage_list);
        ButterKnife.bind(this);
        ImageView backImg=findViewById(R.id.img_back);
        backImg.setOnClickListener(v -> {
            finish();
        });
        emptyMsg.setText("您没有任何消息");
        baseTitle.setText("我的消息");
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
        LoadMoreAdapter loadMoreAdapter=new LoadMoreAdapter(userMessageAdapter::retry);
        LoadMoreAdapter loadHeader=new LoadMoreAdapter(userMessageAdapter::retry);
//        new LoadMoreViewModel().getMessage().observe(this, new Observer<PagingData<Comment>>() {
//            @Override
//            public void onChanged(PagingData<Comment> commentPagingData) {
//                commentListAdapter.submitData(getLifecycle(),commentPagingData);
//            }
//        });
        swipeRefreshLayout.setOnRefreshListener(userMessageAdapter::refresh);
        swipeRefreshLayout.setRefreshing(true);
        recyclerView.setAdapter(userMessageAdapter.withLoadStateFooter(loadMoreAdapter));
        new DataLoaderViewModel().getMessage().observe(this, commentPagingData -> userMessageAdapter.submitData(getLifecycle(),commentPagingData));
        userMessageAdapter.addLoadStateListener(combinedLoadStates -> {
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
        });

        userMessageAdapter.setOnItemClickListener(new UserMessageAdapter.OnItemClick() {
            @Override
            public void onItemClidk(View view, UserMessage item, int  pos) {
                Intent intent=new Intent(MessageActivity.this, VideoDetialsActivity.class);
                intent.putExtra("videoId",item.getVideo().getId());
                intent.putExtra("videoInfo",item.getVideo());
                intent.putExtra("msgId",item.getId());
//                ActivityOptions options = ActivityOptions
//                        .makeSceneTransitionAnimation(MessageActivity.this, view, "upload_cover");
                startActivity(intent);
                finish();
            }
        });
//        swipeRefreshLayout.setOnRefreshListener(()->{
//            commentListAdapter.refresh();
//            swipeRefreshLayout.setRefreshing(false);
//        });

    }



//
//    class CommentListAdapter extends PagingDataAdapter<Comment, CHolder> {
//
//        public CommentListAdapter(@NotNull DiffUtil.ItemCallback<Comment> diffCallback) {
//            super(diffCallback);
//        }
//
//        @NonNull
//        @Override
//        public CHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//            return new CHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment,parent,false));
//        }
//
//        @Override
//        public void onBindViewHolder(@NonNull CHolder holder, int position) {
//            Comment item=getItem(position);
//            holder.itemView.setOnClickListener((v -> {
//                Intent intent=new Intent();
//                intent.putExtra("videoId",item.getObjectId());
//                intent.putExtra("msgId",item.getId());
//                startActivity(new Intent(holder.itemView.getContext(),VideoDetialsActivity.class));
//                finish();
//            }));
//            if (item!=null){
//                holder.title.setText(item.getSubscriber().getUsername());
//                holder.content.setText(item.getContent());
//                holder.sendTime.setText(item.getSendTime());
//                Glide.with(MessageActivity.this)
//                        .load(item.getSubscriber().getHeadImage())
//                        .transition(DrawableTransitionOptions.withCrossFade())
//                        .transform(new GlideCircleBorderTransform(0, Color.TRANSPARENT))
//                        .into(holder.profile);
//
//            }
//
//        }
//
//    }
//
//
//    class CHolder extends RecyclerView.ViewHolder {
//
//        TextView title,content,sendTime;
//        ImageView profile;
//        public CHolder(@NonNull View itemView) {
//            super(itemView);
//            title=itemView.findViewById(R.id.item_comment_username);
//            content=itemView.findViewById(R.id.item_comment_content);
//            profile=itemView.findViewById(R.id.comment_profile);
//            sendTime=itemView.findViewById(R.id.item_comment_time);
//        }
//    }


}



