package com.demo.btvideo.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.demo.btvideo.AppController;
import com.demo.btvideo.R;
import com.demo.btvideo.model.VideoInfo;
import com.demo.btvideo.net.NetInterface;
import com.demo.btvideo.utils.NetWorkUtils;
import com.demo.btvideo.ui.fragment.FragmentVideoDetail;
import com.demo.btvideo.viewmodel.DataViewModel;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import tcking.github.com.giraffeplayer2.GiraffePlayer;
import tcking.github.com.giraffeplayer2.PlayerListener;
import tcking.github.com.giraffeplayer2.PlayerManager;
import tcking.github.com.giraffeplayer2.VideoView;
import tv.danmaku.ijk.media.player.IjkTimedText;

public class VideoDetialsActivity extends AppCompatActivity {

	private PowerManager.WakeLock wakeLock;
	View rootView;


	@BindView(R.id.mainToolBar)
	Toolbar toolbar;
	@BindView(R.id.collapsintLayout)
	CollapsingToolbarLayout collapsingToolbarLayout;
	@BindView(R.id.video_appbar)
	AppBarLayout appBarLayout;
	@BindView(R.id.title_video)
	TextView title_video;
	@BindView(R.id.play_video_view)
	VideoView videoView;




	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			int flagTranslucentStatus = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
			int flagTranslucentNavigation = WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				Window window = getWindow();
				WindowManager.LayoutParams attributes = window.getAttributes();
				attributes.flags |= flagTranslucentStatus;
				window.setAttributes(attributes);
				getWindow().setStatusBarColor(Color.TRANSPARENT);
			} else {
				Window window = getWindow();
				WindowManager.LayoutParams attributes = window.getAttributes();
				attributes.flags |= flagTranslucentStatus ;
				window.setAttributes(attributes);
			}
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE);
			}
		}

		setContentView(R.layout.activity_video_detail);
		ButterKnife.bind(this);
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);

		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle("");
		getSupportFragmentManager().beginTransaction().replace(R.id.video_detail_fragment, FragmentVideoDetail.getInstance()).commit();
		AppBarLayout.LayoutParams appLayoutParams= (AppBarLayout.LayoutParams) collapsingToolbarLayout.getLayoutParams();

		Intent intent=getIntent();
		String url1 = "";
		VideoInfo videoInfo=null;
		if (intent!=null){
			videoInfo = (VideoInfo) intent.getSerializableExtra("videoInfo");
			if (videoInfo!=null){
				url1=videoInfo.getVideoUrl();
			}
		}



		if (videoInfo!=null){
			title_video.setText(videoInfo.getTitle());
			VideoInfo finalVideoInfo = videoInfo;
			Executors.newCachedThreadPool().execute(()->{
				new ViewModelProvider(this).get(DataViewModel.class).videoData().postValue(finalVideoInfo);
			});
		}
//		url1="http://localhost:8080/video/eswd/0bd526c5-10d0-40f4-93e9-db59d9aa19de/oceans.mp4" ;

		//有部分视频加载有问题，这个视频是有声音显示不出图像的，没有解决http://fzkt-biz.oss-cn-hangzhou.aliyuncs.com/vedio/2f58be65f43946c588ce43ea08491515.mp4

			Glide.with(this)
					.load(videoInfo.getCoverImage())
					.into(videoView.getCoverView());


		videoView.setPlayerListener(new PlayerListener() {
			@Override
			public void onPrepared(GiraffePlayer giraffePlayer) {

			}

			@Override
			public void onBufferingUpdate(GiraffePlayer giraffePlayer, int percent) {

			}

			@Override
			public boolean onInfo(GiraffePlayer giraffePlayer, int what, int extra) {
				return false;
			}

			@Override
			public void onCompletion(GiraffePlayer giraffePlayer) {
				if (wakeLock != null) {
					wakeLock.release();
				}
			}

			@Override
			public void onSeekComplete(GiraffePlayer giraffePlayer) {

			}

			@Override
			public boolean onError(GiraffePlayer giraffePlayer, int what, int extra) {
				return false;
			}

			@Override
			public void onPause(GiraffePlayer giraffePlayer) {
				appLayoutParams.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS_COLLAPSED| AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL);
				if (wakeLock != null) {
					wakeLock.release();
				}
			}

			@Override
			public void onRelease(GiraffePlayer giraffePlayer) {

			}

			@Override
			public void onStart(GiraffePlayer giraffePlayer) {
				appLayoutParams.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_NO_SCROLL);
				/**常亮*/

				wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "liveTAG");
				wakeLock.acquire();

			}

			@Override
			public void onTargetStateChange(int oldState, int newState) {

			}

			@Override
			public void onCurrentStateChange(int oldState, int newState) {

			}

			@Override
			public void onDisplayModelChange(int oldModel, int newModel) {

			}

			@Override
			public void onPreparing(GiraffePlayer giraffePlayer) {
				appLayoutParams.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_NO_SCROLL);

			}

			@Override
			public void onTimedText(GiraffePlayer giraffePlayer, IjkTimedText text) {

			}

			@Override
			public void onLazyLoadProgress(GiraffePlayer giraffePlayer, int progress) {

			}

			@Override
			public void onLazyLoadError(GiraffePlayer giraffePlayer, String message) {

			}
		});
		videoView.setVideoPath(url1).getPlayer();

	}


	@OnClick(R.id.btn_send)
	public void setComment(){
		if (AppController.getInstance().isLogin()){
			NetInterface netInterface=NetWorkUtils.getRetrofit().create(NetInterface.class);
			netInterface.getCommentList(1,1);
		}else{
			startActivity(new Intent(this,LoginActivity.class));
		}
	}




	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		PlayerManager.getInstance().onConfigurationChanged(newConfig);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
	}

	@Override
	public void onBackPressed() {
		if (!PlayerManager.getInstance().onBackPressed()){
			finish();
		}
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		switch (item.getItemId()){
			case android.R.id.home:
				super.onBackPressed();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onStop() {
		super.onStop();
		videoView.getPlayer().stop();
	}

	public void btnLogin(View view) {
		startActivity(new Intent(this,LoginActivity.class));
	}
}