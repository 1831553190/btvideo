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
import com.demo.btvideo.adapter.FunsAdapter;
import com.demo.btvideo.adapter.LoadMoreAdapter;
import com.demo.btvideo.model.Attention;
import com.demo.btvideo.model.Funs;
import com.demo.btvideo.viewmodel.DataLoaderViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;


//粉丝列表界面
public class FunsActivity extends AppCompatActivity {

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
        emptyMsg.setText("您没有任何人关注");
        baseTitle.setText("我的粉丝");
        ImageView backImg=findViewById(R.id.img_back);
        backImg.setOnClickListener(v -> {
            finish();
        });

        FunsAdapter funsAdapter = new FunsAdapter(new DiffUtil.ItemCallback<Funs>() {
            @Override
            public boolean areItemsTheSame(@NonNull Funs oldItem, @NonNull Funs newItem) {
                return oldItem.getId() == newItem.getId();
            }

            @Override
            public boolean areContentsTheSame(@NonNull Funs oldItem, @NonNull Funs newItem) {
                return oldItem.getFans().getUsername().equals(newItem.getFans().getUsername());
            }
        });
        LoadMoreAdapter loadMoreAdapter=new LoadMoreAdapter(funsAdapter::retry);
        LoadMoreAdapter loadHeader=new LoadMoreAdapter(funsAdapter::retry);
        swipeRefreshLayout.setOnRefreshListener(funsAdapter::refresh);
        swipeRefreshLayout.setRefreshing(true);
        recyclerView.setAdapter(funsAdapter.withLoadStateFooter(loadMoreAdapter));
        new DataLoaderViewModel().loadFunsData().observe(this, commentPagingData ->
                funsAdapter.submitData(getLifecycle(),commentPagingData));
        funsAdapter.addLoadStateListener(combinedLoadStates -> {
            if (funsAdapter.getItemCount()==0){
                loadHeader.setLoadState(combinedLoadStates.getRefresh());
            }
            if (combinedLoadStates.getRefresh() instanceof LoadState.NotLoading||
                    combinedLoadStates.getRefresh() instanceof  LoadState.Error){
                swipeRefreshLayout.setRefreshing(false);
            }
            if (combinedLoadStates.getRefresh() instanceof LoadState.NotLoading&&funsAdapter.getItemCount()==0){
                emptyMsg.setVisibility(View.VISIBLE);
            }else{
                emptyMsg.setVisibility(View.GONE);
            }
            return null;
        });

        funsAdapter.setOnItemClickListener(new FunsAdapter.OnItemClick() {
            @Override
            public void onItemClick(View view, Funs item, int  pos) {
                Intent intent=new Intent(FunsActivity.this, UploadHistoryActivity.class);
                intent.putExtra("account",item.getFansAccount());
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



