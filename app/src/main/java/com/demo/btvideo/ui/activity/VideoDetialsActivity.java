package com.demo.btvideo.ui.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.room.Room;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.demo.btvideo.AppController;
import com.demo.btvideo.R;
import com.demo.btvideo.dao.AppDatabase;
import com.demo.btvideo.model.Msg;
import com.demo.btvideo.model.VideoInfo;
import com.demo.btvideo.net.NetInterface;
import com.demo.btvideo.net.ServerURL;
import com.demo.btvideo.ui.fragment.FragmentComment;
import com.demo.btvideo.utils.NetWorkUtils;
import com.demo.btvideo.ui.fragment.FragmentVideoDetail;
import com.demo.btvideo.viewmodel.DataViewModel;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.tabs.TabLayout;

import java.util.HashMap;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tcking.github.com.giraffeplayer2.GiraffePlayer;
import tcking.github.com.giraffeplayer2.PlayerListener;
import tcking.github.com.giraffeplayer2.PlayerManager;
import tcking.github.com.giraffeplayer2.VideoView;
import top.limuyang2.shadowlayoutlib.ShadowConstraintLayout;
import tv.danmaku.ijk.media.player.IjkTimedText;



//视频详情界面
public class VideoDetialsActivity extends BaseActivity {

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
	@BindView(R.id.edit_send_comment)
	EditText editSendComment;
	@BindView(R.id.shadowConstraintLayout)
	ShadowConstraintLayout shadowConstraintLayout;



	VideoInfo videoInfo = null;
	int videoId = -1;
	int pos = -1;
	private boolean isResume = false;
	String msgId = "";

	@BindView(R.id.videodetail_tab)
	TabLayout tabLayout;
	@BindView(R.id.videoDetail_viewpager)
	ViewPager viewPager;
	private ObjectAnimator objectAnimator;
	private AppDatabase database;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video_detail);
		ButterKnife.bind(this);
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		Intent intent = getIntent();
		DataViewModel dataViewModel = new ViewModelProvider(this).get(DataViewModel.class);
		if (intent != null) {
			int videoId = intent.getIntExtra("videoId", -1);
			if (videoId != -1) {
				dataViewModel.loadVideoInfo(String.valueOf(videoId), msgId).observe(this, new Observer<VideoInfo>() {
					@Override
					public void onChanged(VideoInfo videoInfo) {
						title_video.setText(videoInfo.getTitle());
						Glide.with(VideoDetialsActivity.this)
								.load(ServerURL.MAIN_URL + videoInfo.getCoverImage())
								.into(videoView.getCoverView());
						if (isResume && PlayerManager.getInstance().getCurrentPlayer() != null && PlayerManager.getInstance().getCurrentPlayer().isPlaying()) {
							pos = PlayerManager.getInstance().getCurrentPlayer().getCurrentPosition();
							videoView.videoInfo(PlayerManager.getInstance().getCurrentPlayer().getVideoInfo());
							PlayerManager.getInstance().getCurrentPlayer().release();
							videoView.getPlayer().start();
							GiraffePlayer player = videoView.getPlayer();
							player.seekTo(pos);
							pos = -1;
//			videoView.getPlayer().setDisplayModel(GiraffePlayer.DISPLAY_FULL_WINDOW);
//			videoView.setVideoPath(url1).getPlayer();
						} else {
							videoView.setVideoPath(ServerURL.MAIN_URL +videoInfo.getVideoUrl()).getPlayer();
						}
						VideoDetialsActivity.this.videoInfo = videoInfo;
					}
				});
				msgId = "";
			}
		}



		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle("");
		FragmentComment fragmentComment = FragmentComment.getInstance();
		viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
			@NonNull
			@Override
			public Fragment getItem(int position) {
				return position == 0 ? FragmentVideoDetail.getInstance() : fragmentComment;
			}

			@Nullable
			@Override
			public CharSequence getPageTitle(int position) {
				return position == 0 ? "简介" : "评论";
			}

			@Override
			public int getCount() {
				return 2;
			}
		});
		tabLayout.setupWithViewPager(viewPager);

		viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

			}

			@Override
			public void onPageSelected(int position) {
				if (position == 0) {
					hide(shadowConstraintLayout);
				} else {
					showView(shadowConstraintLayout);
				}
			}

			@Override
			public void onPageScrollStateChanged(int state) {

			}
		});

		AppBarLayout.LayoutParams appLayoutParams = (AppBarLayout.LayoutParams) collapsingToolbarLayout.getLayoutParams();

//		if (videoInfo!=null){
//			title_video.setText(videoInfo.getTitle());
//			VideoInfo finalVideoInfo = videoInfo;
////			Executors.newCachedThreadPool().execute(()->{
////				new ViewModelProvider(this).get(DataViewModel.class).videoData().postValue(finalVideoInfo);
////			});
//		}
//		url1="http://localhost:8080/video/eswd/0bd526c5-10d0-40f4-93e9-db59d9aa19de/oceans.mp4" ;
		//有部分视频加载有问题，这个视频是有声音显示不出图像的，没有解决http://fzkt-biz.oss-cn-hangzhou.aliyuncs.com/vedio/2f58be65f43946c588ce43ea08491515.mp4


		database = Room.databaseBuilder(this.getApplicationContext(), AppDatabase.class, "user")
				.fallbackToDestructiveMigration().build();
		videoView.setPlayerListener(new PlayerListener() {
			@Override
			public void onPrepared(GiraffePlayer giraffePlayer) {
				Log.d("TAG", "onPrepared: ");
				if (videoInfo!=null){
					Executors.newCachedThreadPool().execute(()-> database.userDao().insertAll(videoInfo));
				}
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
				appLayoutParams.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS_COLLAPSED | AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL);

				if (wakeLock != null) {
					if (wakeLock.isHeld()) {
						wakeLock.release();
					}
				}
			}

			@Override
			public void onSeekComplete(GiraffePlayer giraffePlayer) {
				appLayoutParams.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS_COLLAPSED | AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL);

			}

			@Override
			public boolean onError(GiraffePlayer giraffePlayer, int what, int extra) {
				appLayoutParams.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS_COLLAPSED | AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL);

				return false;
			}

			@Override
			public void onPause(GiraffePlayer giraffePlayer) {
				appLayoutParams.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS_COLLAPSED | AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL);
				if (wakeLock != null) {
					if (wakeLock.isHeld()) {
						wakeLock.release();
					}
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
				if (newModel == GiraffePlayer.DISPLAY_FLOAT) {
					appLayoutParams.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS_COLLAPSED | AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL);
				} else {
					appLayoutParams.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_NO_SCROLL);
					if (oldModel == GiraffePlayer.DISPLAY_FLOAT) {
						if (isDestroyed()) {
							Intent i = new Intent(getApplicationContext(), VideoDetialsActivity.class);
							i.setFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
							i.putExtra("videoInfo", videoInfo);
							i.putExtra("resume", true);
							recreate();
							startActivity(i);
						}
					}
				}

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
				appLayoutParams.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS_COLLAPSED | AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL);

			}
		});


	}


	private void showView(View view) {
//		if (objectAnimator.isRunning()){
//			objectAnimator.setStartDelay(100);
//		}
		objectAnimator = ObjectAnimator.ofFloat(view, "translationY", 100, 0);
		objectAnimator.setInterpolator(new DecelerateInterpolator());
		objectAnimator.setDuration(100);
		objectAnimator.addListener(new AnimatorListenerAdapter() {

			@Override
			public void onAnimationStart(Animator animation) {
				super.onAnimationEnd(animation);
				view.setVisibility(View.VISIBLE);
			}
		});
		objectAnimator.start();
	}

	private void hide(View view) {
		objectAnimator = ObjectAnimator.ofFloat(view, "translationY", 0, 80);
		objectAnimator.setInterpolator(new AccelerateInterpolator());
		objectAnimator.setDuration(100);
		objectAnimator.addListener(new AnimatorListenerAdapter() {

			@Override
			public void onAnimationEnd(Animator animation) {
				super.onAnimationEnd(animation);
				view.setVisibility(View.INVISIBLE);
//                    bottomView.setVisibility(View.GONE);
			}
		});
		objectAnimator.start();
	}

	@OnClick(R.id.btn_send)
	public void sendComment() {
		if (AppController.getInstance().isLogin()) {
			if (videoInfo != null) {
				String content = editSendComment.getText().toString();
				if (content.isEmpty()) {
					Toast.makeText(this, "请输入评论内容", Toast.LENGTH_SHORT).show();
				} else {
					NetInterface netInterface = NetWorkUtils.getRetrofit().create(NetInterface.class);
					HashMap<String, String> data = new HashMap<>();
					data.put("objectId", String.valueOf(videoInfo.getId()));
					data.put("announcerAccount", videoInfo.getUserAccount());
					data.put("content", content);
					netInterface.sendComment(data).enqueue(new Callback<Msg>() {
						@Override
						public void onResponse(Call<Msg> call, Response<Msg> response) {

							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									if (response.body().getCode() == 200) {
										Toast.makeText(VideoDetialsActivity.this, "评论成功!", Toast.LENGTH_SHORT).show();
										editSendComment.setText("");
										new ViewModelProvider(getViewModelStore(), new ViewModelProvider.NewInstanceFactory())
												.get(DataViewModel.class).updateComment().setValue(200);
									} else {
										Toast.makeText(VideoDetialsActivity.this, "发送失败!", Toast.LENGTH_SHORT).show();
									}
								}
							});
						}

						@Override
						public void onFailure(Call<Msg> call, Throwable t) {
							runOnUiThread(() -> Toast.makeText(VideoDetialsActivity.this, "发送失败!", Toast.LENGTH_SHORT).show());
						}
					});
				}
			} else {
				Toast.makeText(this, "未获取到视频信息", Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
			startActivity(new Intent(this, LoginActivity.class));
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
		if (PlayerManager.getInstance().getCurrentPlayer()!=null&&
				PlayerManager.getInstance().getCurrentPlayer().getDisplayModel() == GiraffePlayer.DISPLAY_FLOAT) {
			PlayerManager.getInstance().onBackPressed();
			finish();
		} else {
			if (!PlayerManager.getInstance().onBackPressed()) {
				try {
					if (videoView.getPlayer().isPlaying()) {
						videoView.getPlayer().release();
					}
				}catch (Exception e){
//					super.onBackPressed();
				}
				super.onBackPressed();
			}else {
				super.onBackPressed();
			}
		}
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				super.onBackPressed();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onStop() {
		super.onStop();
//		videoView.getPlayer().stop();
		if (wakeLock != null) {
			if (wakeLock.isHeld()) {
				wakeLock.release();
			}
		}
	}

	public void btnLogin(View view) {
		startActivity(new Intent(this, LoginActivity.class));
	}
}