package com.demo.btvideo.ui.activity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.DrawableUtils;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Observer;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.bumptech.glide.signature.ObjectKey;
import com.demo.btvideo.AppController;
import com.demo.btvideo.R;
import com.demo.btvideo.dao.AppDatabase;
import com.demo.btvideo.model.Msg;
import com.demo.btvideo.model.User;
import com.demo.btvideo.net.NetInterface;
import com.demo.btvideo.net.ServerURL;
import com.demo.btvideo.ui.fragment.FragmentCollection;
import com.demo.btvideo.ui.fragment.FragmentFavourite;
import com.demo.btvideo.ui.fragment.FragmentIndex;
import com.demo.btvideo.ui.fragment.FragmentWork;
import com.demo.btvideo.utils.BlurTransformation;
import com.demo.btvideo.utils.NetWorkUtils;
import com.demo.btvideo.viewmodel.LoginViewModel;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import top.limuyang2.shadowlayoutlib.ShadowFrameLayout;

public class UserInfoCenterActivity extends AppCompatActivity {

	private static final String TAG = "UserInfoCenterActivity";
	//	@BindView(R.id.mToolbar)
//	Toolbar toolbar;
	@BindView(R.id.centerListLayout)
	ViewPager viewPager;
	@BindView(R.id.tab_userCenter)
	TabLayout tabUserCenter;
	@BindView(R.id.userAppbarLayot)
	AppBarLayout appBarLayout;
	@BindView(R.id.coverBack)
	ShadowFrameLayout shadowFrameLayout;
	@BindView(R.id.collLayout)
	CollapsingToolbarLayout collLayout;


	@BindView(R.id.user_cover)
	ImageView cover;


	ExecutorService pool= Executors.newCachedThreadPool();


	String[] titles=new String[]{"作品","收藏","关注"};
	Handler handler=new Handler(Looper.getMainLooper());
	private SharedPreferences preference;
	AppDatabase database;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
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
				getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE);
			}
		}
		setContentView(R.layout.activity_userinfo);
		ButterKnife.bind(this);
		viewPager.setOffscreenPageLimit(3);
		viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager(),FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
			@NonNull
			@Override
			public Fragment getItem(int position) {
				if (position==0){
					return FragmentWork.getInstance();
				}else if (position==1){
					return FragmentCollection.getInstance();
				}else {
					return FragmentFavourite.getInstance();
				}
			}
			@Override
			public int getCount() {
				return 3;
			}

//			@Override
//			public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
//
//			}

			@Nullable
			@Override
			public CharSequence getPageTitle(int position) {
				return titles[position];
			}
		});

		tabUserCenter.setupWithViewPager(viewPager);
		database = Room.databaseBuilder(this, AppDatabase.class, "user")
				.fallbackToDestructiveMigration().build();

		preference= PreferenceManager.getDefaultSharedPreferences(this);
		pool.execute(()->{
			NetInterface netInterface=NetWorkUtils.getRetrofit().create(NetInterface.class);
			netInterface.getUserInfo().enqueue(new Callback<Msg<JsonElement>>() {
				@Override
				public void onResponse(Call<Msg<JsonElement>> call, Response<Msg<JsonElement>> response) {
					if (response.isSuccessful()&&response.body().getCode()==200){
						String strUserInfo=response.body().getData().getAsJsonObject().toString();
						User user=new Gson().fromJson(strUserInfo,User.class);
						Log.d(TAG, "resp"+user.getHeadImage());
						handler.post(()->{
							if (!isDestroyed()){
								Glide.with(UserInfoCenterActivity.this)
										.load(ServerURL.MAIN_URL+user.getHeadImage())
										.transition(DrawableTransitionOptions.withCrossFade())
										.signature(new ObjectKey(String.valueOf(System.currentTimeMillis())))
										.apply(RequestOptions.bitmapTransform(new BlurTransformation(UserInfoCenterActivity. this,25,8)))
										.into(new SimpleTarget<Drawable>() {
											@Override
											public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
//						drawable.setCrossFadeEnabled(true);
												appBarLayout.setBackground(resource);
											}
										});

								Glide.with(UserInfoCenterActivity.this)
										.load(ServerURL.MAIN_URL+user.getHeadImage())
										.signature(new ObjectKey(String.valueOf(System.currentTimeMillis())))
										.transition(DrawableTransitionOptions.withCrossFade())
										.into(cover);
							}
						});
					}
				}

				@Override
				public void onFailure(Call<Msg<JsonElement>> call, Throwable t) {
					handler.post(()->{
						Toast.makeText(UserInfoCenterActivity.this, "连接失败", Toast.LENGTH_SHORT).show();
					});
				}
			});
		});
		LoginViewModel.getInstance().update().observe(this, new Observer<Integer>() {
			@Override
			public void onChanged(Integer integer) {
				pool.submit(() -> {
					User user = database.userDao().getUser(preference.getString("userNow", "-1"));
					handler.post(() -> {
						if (user != null) {
							String userImg = user.getHeadImage();
							if (userImg != null) {
								Glide.with(UserInfoCenterActivity.this.getApplicationContext())
										.load(userImg)
										.signature(new ObjectKey(String.valueOf(System.currentTimeMillis())))
										.transition(DrawableTransitionOptions.withCrossFade())
										.error(R.mipmap.load404)
										.into(cover);
								Glide.with(UserInfoCenterActivity.this)
										.load(userImg)
										.transition(DrawableTransitionOptions.withCrossFade())
										.signature(new ObjectKey(String.valueOf(System.currentTimeMillis())))
										.apply(RequestOptions.bitmapTransform(new BlurTransformation(UserInfoCenterActivity. this,25,8)))
										.into(new SimpleTarget<Drawable>() {
											@Override
											public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
//						drawable.setCrossFadeEnabled(true);
												appBarLayout.setBackground(resource);
											}
										});
							}

						}
					});
				});
			}
		});
//		TransitionDrawable  drawable=new TransitionDrawable();
//		drawable.setCrossFadeEnabled(true);

//		appBarLayout.setBackground();

//		setSupportActionBar(toolbar);

	}

	@OnClick(R.id.user_cover)
	public void gotoCover(){
		Intent intent = new Intent(this, UploadHeadActivity.class);
		ActivityOptions options = ActivityOptions
				.makeSceneTransitionAnimation(this, cover, "upload_cover");
		startActivity(intent, options.toBundle());
	}
}
