package com.demo.btvideo.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.paging.LoadState;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.demo.btvideo.R;
import com.demo.btvideo.adapter.AttentionAdapter;
import com.demo.btvideo.adapter.CollectionAdapter;
import com.demo.btvideo.adapter.LoadMoreAdapter;
import com.demo.btvideo.model.Attention;
import com.demo.btvideo.model.Collection;
import com.demo.btvideo.viewmodel.DataLoaderViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;



//关注界面
public class AttctionActivity extends BaseActivity {

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
        emptyMsg.setText("您没有关注任何人");
        baseTitle.setText("我的关注");
        ImageView backImg=findViewById(R.id.img_back);
        backImg.setOnClickListener(v -> {
            finish();
        });

        AttentionAdapter attentionAdapter = new AttentionAdapter(new DiffUtil.ItemCallback<Attention>() {
            @Override
            public boolean areItemsTheSame(@NonNull Attention oldItem, @NonNull Attention newItem) {
                return oldItem.getId() == newItem.getId();
            }

            @Override
            public boolean areContentsTheSame(@NonNull Attention oldItem, @NonNull Attention newItem) {
                return oldItem.getUp().getUsername().equals(newItem.getUp().getUsername());
            }
        });
        LoadMoreAdapter loadMoreAdapter=new LoadMoreAdapter(attentionAdapter::retry);
        LoadMoreAdapter loadHeader=new LoadMoreAdapter(attentionAdapter::retry);
        swipeRefreshLayout.setOnRefreshListener(attentionAdapter::refresh);
        swipeRefreshLayout.setRefreshing(true);
        recyclerView.setAdapter(attentionAdapter.withLoadStateFooter(loadMoreAdapter));
        new DataLoaderViewModel().getAttention().observe(this, commentPagingData ->
                attentionAdapter.submitData(getLifecycle(),commentPagingData));
        attentionAdapter.addLoadStateListener(combinedLoadStates -> {
            if (attentionAdapter.getItemCount()==0){
                loadHeader.setLoadState(combinedLoadStates.getRefresh());
            }
            if (combinedLoadStates.getRefresh() instanceof LoadState.NotLoading||
                    combinedLoadStates.getRefresh() instanceof  LoadState.Error){
                swipeRefreshLayout.setRefreshing(false);
            }
            if (combinedLoadStates.getRefresh() instanceof LoadState.NotLoading&&attentionAdapter.getItemCount()==0){
                emptyMsg.setVisibility(View.VISIBLE);
            }else{
                emptyMsg.setVisibility(View.GONE);
            }
            return null;
        });

        attentionAdapter.setOnItemClickListener(new AttentionAdapter.OnItemClick() {
            @Override
            public void onItemClick(View view, Attention item, int  pos) {
                Intent intent=new Intent(AttctionActivity.this, UploadHistoryActivity.class);
                intent.putExtra("account",item.getFollowAccount());
//                ActivityOptions options = ActivityOptions
//                        .makeSceneTransitionAnimation(MessageActivity.this, view, "upload_cover");
                startActivity(intent);
//                finish();
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



