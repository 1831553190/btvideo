package com.demo.btvideo.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import com.demo.btvideo.adapter.CollectionAdapter;
import com.demo.btvideo.adapter.LoadMoreAdapter;
import com.demo.btvideo.adapter.UploadAdapter;
import com.demo.btvideo.model.Collection;
import com.demo.btvideo.model.VideoInfo;
import com.demo.btvideo.viewmodel.DataLoaderViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;


//上传历史界面
public class UploadHistoryActivity extends BaseActivity {

    @BindView(R.id.list_message)
    RecyclerView recyclerView;
    @BindView(R.id.msg_empty)
    TextView emptyMsg;

    @BindView(R.id.refreshMessage)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.baseActivity_title)
    TextView baseTitle;
    String account="";


    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mseeage_list);
        ButterKnife.bind(this);
        emptyMsg.setText("无上传记录");
        baseTitle.setText("上传记录");
        ImageView backImg=findViewById(R.id.img_back);
        backImg.setOnClickListener(v -> {
            finish();
        });

        Intent intent=getIntent();
        if (intent!=null){
            account=intent.getStringExtra("account");
        }
        if (account==null||account.equals("")){
            account=PreferenceManager.getDefaultSharedPreferences(this).getString("userNow","");
        }
        UploadAdapter uploadAdapter = new UploadAdapter(new DiffUtil.ItemCallback<VideoInfo>() {
            @Override
            public boolean areItemsTheSame(@NonNull VideoInfo oldItem, @NonNull VideoInfo newItem) {
                return oldItem.getId() == newItem.getId();
            }

            @Override
            public boolean areContentsTheSame(@NonNull VideoInfo oldItem, @NonNull VideoInfo newItem) {
                return oldItem.getTitle().equals(newItem.getTitle());
            }
        });
        LoadMoreAdapter loadMoreAdapter=new LoadMoreAdapter(uploadAdapter::retry);
        LoadMoreAdapter loadHeader=new LoadMoreAdapter(uploadAdapter::retry);
        swipeRefreshLayout.setOnRefreshListener(uploadAdapter::refresh);
        swipeRefreshLayout.setRefreshing(true);
        recyclerView.setAdapter(uploadAdapter.withLoadStateFooter(loadMoreAdapter));
        new DataLoaderViewModel().getUploadVideo(account).observe(this, commentPagingData -> uploadAdapter.submitData(getLifecycle(),commentPagingData));
        uploadAdapter.addLoadStateListener(combinedLoadStates -> {
            if (uploadAdapter.getItemCount()==0){
                loadHeader.setLoadState(combinedLoadStates.getRefresh());
            }
            if (combinedLoadStates.getRefresh() instanceof LoadState.NotLoading||
                    combinedLoadStates.getRefresh() instanceof  LoadState.Error){
                swipeRefreshLayout.setRefreshing(false);
            }
            if (combinedLoadStates.getRefresh() instanceof LoadState.NotLoading&&uploadAdapter.getItemCount()==0){
                emptyMsg.setVisibility(View.VISIBLE);
            }else{
                emptyMsg.setVisibility(View.GONE);
            }
            return null;
        });

        uploadAdapter.setOnItemClickListener(new UploadAdapter.OnItemClick() {
            @Override
            public void onItemClick(View view, VideoInfo videoInfo, int pos) {
                Intent intent=new Intent(UploadHistoryActivity.this, VideoDetialsActivity.class);
                intent.putExtra("videoId",videoInfo.getId());
                intent.putExtra("videoInfo",videoInfo);
//				intent.putExtra("msgId",item.getId());
//                ActivityOptions options = ActivityOptions
//                        .makeSceneTransitionAnimation(MessageActivity.this, view, "upload_cover");
                startActivity(intent);
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



