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
import com.demo.btvideo.adapter.CollectionAdapter;
import com.demo.btvideo.adapter.LoadMoreAdapter;
import com.demo.btvideo.model.Collection;
import com.demo.btvideo.viewmodel.DataLoaderViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;



//收藏界面
public class CollectionActivity extends AppCompatActivity {

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
        ImageView backImg=findViewById(R.id.img_back);
        backImg.setOnClickListener(v -> {
            finish();
        });
        emptyMsg.setText("无收藏");
        baseTitle.setText("我的收藏");
//        CommentListAdapter commentListAdapter=new CommentListAdapter(new DiffUtil.ItemCallback<Comment>() {
//            @Override
//            public boolean areItemsTheSame(@NonNull Comment oldItem, @NonNull Comment newItem) {
//                return oldItem.getId()==newItem.getId();
//            }
//
//            @Override
//            public boolean areContentsTheSame(@NonNull Comment oldItem, @NonNull Comment newItem) {
//                return oldItem.getContent().equals(newItem.getContent());
//            }
//        });
        CollectionAdapter collectionAdapter=new CollectionAdapter(new DiffUtil.ItemCallback<Collection>() {
            @Override
            public boolean areItemsTheSame(@NonNull Collection oldItem, @NonNull Collection newItem) {
                return oldItem.getId()==newItem.getId();
            }

            @Override
            public boolean areContentsTheSame(@NonNull Collection oldItem, @NonNull Collection newItem) {
                return oldItem.getData().getId()==newItem.getData().getId();
            }
        });
        LoadMoreAdapter loadMoreAdapter=new LoadMoreAdapter(collectionAdapter::retry);
        LoadMoreAdapter loadHeader=new LoadMoreAdapter(collectionAdapter::retry);
//        new LoadMoreViewModel().getMessage().observe(this, new Observer<PagingData<Comment>>() {
//            @Override
//            public void onChanged(PagingData<Comment> commentPagingData) {
//                commentListAdapter.submitData(getLifecycle(),commentPagingData);
//            }
//        });
        swipeRefreshLayout.setOnRefreshListener(collectionAdapter::refresh);
        swipeRefreshLayout.setRefreshing(true);
        recyclerView.setAdapter(collectionAdapter.withLoadStateFooter(loadMoreAdapter));
        new DataLoaderViewModel().getCollections().observe(this, commentPagingData -> collectionAdapter.submitData(getLifecycle(),commentPagingData));
        collectionAdapter.addLoadStateListener(combinedLoadStates -> {
            if (collectionAdapter.getItemCount()==0){
                loadHeader.setLoadState(combinedLoadStates.getRefresh());
            }
            if (combinedLoadStates.getRefresh() instanceof LoadState.NotLoading||
                    combinedLoadStates.getRefresh() instanceof  LoadState.Error){
                swipeRefreshLayout.setRefreshing(false);
            }
            if (combinedLoadStates.getRefresh() instanceof LoadState.NotLoading&&collectionAdapter.getItemCount()==0){
                emptyMsg.setVisibility(View.VISIBLE);
            }else{
                emptyMsg.setVisibility(View.GONE);
            }
            return null;
        });

        collectionAdapter.setOnItemClickListener(new CollectionAdapter.OnItemClick() {
            @Override
            public void onItemClick(View view, Collection item, int  pos) {
                Intent intent=new Intent(CollectionActivity.this, VideoDetialsActivity.class);
                intent.putExtra("videoId",item.getVideoId());
                intent.putExtra("videoInfo",item.getData());
                intent.putExtra("msgId",item.getId());
//                ActivityOptions options = ActivityOptions
//                        .makeSceneTransitionAnimation(MessageActivity.this, view, "upload_cover");
                startActivity(intent);
//                finish();
            }
        });

    }



}



