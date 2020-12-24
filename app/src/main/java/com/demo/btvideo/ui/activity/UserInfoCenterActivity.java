package com.demo.btvideo.ui.activity;

import android.animation.ArgbEvaluator;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Observer;
import androidx.room.Room;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.bumptech.glide.signature.ObjectKey;
import com.demo.btvideo.R;
import com.demo.btvideo.dao.AppDatabase;
import com.demo.btvideo.model.Msg;
import com.demo.btvideo.model.User;
import com.demo.btvideo.net.NetInterface;
import com.demo.btvideo.net.ServerURL;
import com.demo.btvideo.ui.fragment.FragmentAttention;
import com.demo.btvideo.ui.fragment.FragmentCollection;
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
import top.limuyang2.shadowlayoutlib.ShadowConstraintLayout;
import top.limuyang2.shadowlayoutlib.ShadowFrameLayout;


//用户中心
public class UserInfoCenterActivity extends BaseActivity {

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
	ShadowConstraintLayout shadowFrameLayout;
	@BindView(R.id.collLayout)
	CollapsingToolbarLayout collLayout;


	@BindView(R.id.user_cover)
	ImageView cover;
	@BindView(R.id.mToolBar)
	Toolbar toolbar;

	@BindView(R.id.baseActivity_title)
	TextView baseTitle;

	@BindView(R.id.text_menu_editinfo)
	TextView textEdit;
	@BindView(R.id.img_back)
	ImageView imgBack;
	@BindView(R.id.centerusername)
	TextView uname;
	@BindView(R.id.centersign)
	TextView usign;


	ExecutorService pool = Executors.newCachedThreadPool();


	String[] titles = new String[]{"个人作品", "收藏", "关注"};
	Handler handler = new Handler(Looper.getMainLooper());
	private SharedPreferences preference;
	AppDatabase database;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_userinfo);

		ButterKnife.bind(this);
		setSupportActionBar(toolbar);
		getSupportActionBar().setTitle("");
		ArgbEvaluator argbEvaluator = new ArgbEvaluator();
//		AppBarLayout.LayoutParams layoutParams = (AppBarLayout.LayoutParams) collLayout.getLayoutParams();
//		layoutParams.setScrollInterpolator(new DecelerateInterpolator());
		appBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
			float offset = Math.abs(verticalOffset) * 1.0f / appBarLayout.getTotalScrollRange();
			if (offset > 1.0f) {
				offset = 1.0f;
			}
			int color = (int) argbEvaluator.evaluate(offset, Color.parseColor("#AAFFFFFF"), getResources().getColor(R.color.colorPrimary));
			int titleColor = (int) argbEvaluator.evaluate(offset, Color.parseColor("#00FFFFFF"), Color.WHITE);
			int titleColor1 = (int) argbEvaluator.evaluate(offset, Color.BLACK, Color.WHITE);
			int imgColor = (int) argbEvaluator.evaluate(offset, Color.TRANSPARENT, Color.WHITE);
			toolbar.setBackgroundColor(color);
			toolbar.setTitleTextColor(titleColor);
			baseTitle.setTextColor(titleColor);
			textEdit.setTextColor(titleColor1);
			imgBack.setImageTintList(ColorStateList.valueOf(imgColor));


		});
//		toolbar.setTitle("个人中心");
		baseTitle.setText("个人中心");
		viewPager.setOffscreenPageLimit(3);
		viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
			@NonNull
			@Override
			public Fragment getItem(int position) {
				if (position == 0) {
					return FragmentWork.getInstance();
				} else if (position == 1) {
					return FragmentCollection.getInstance();
				} else {
					return FragmentAttention.getInstance();
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
		preference = PreferenceManager.getDefaultSharedPreferences(this);
		pool.execute(() -> {
			NetInterface netInterface = NetWorkUtils.getRetrofit().create(NetInterface.class);
			netInterface.getUserInfo().enqueue(new Callback<Msg<JsonElement>>() {
				@Override
				public void onResponse(Call<Msg<JsonElement>> call, Response<Msg<JsonElement>> response) {
					if (response.isSuccessful() && response.body().getCode() == 200) {
						String strUserInfo = response.body().getData().getAsJsonObject().toString();
						User user = new Gson().fromJson(strUserInfo, User.class);
						Log.d(TAG, "resp" + user.getHeadImage());
						handler.post(() -> {
							if (!isDestroyed()) {
								uname.setText(user.getUsername());
								if (user.getSignature()!=null){
									usign.setText(user.getSignature());
								}
								Glide.with(UserInfoCenterActivity.this)
										.load(ServerURL.MAIN_URL + user.getHeadImage())
										.transition(DrawableTransitionOptions.withCrossFade())
										.signature(new ObjectKey(String.valueOf(System.currentTimeMillis())))
										.apply(RequestOptions.bitmapTransform(new BlurTransformation(UserInfoCenterActivity.this, 25, 8)))
										.error(R.mipmap.load404)
										.into(new SimpleTarget<Drawable>() {
											@Override
											public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
//						drawable.setCrossFadeEnabled(true);
												appBarLayout.setBackground(resource);
											}
										});

								Glide.with(UserInfoCenterActivity.this)
										.load(ServerURL.MAIN_URL + user.getHeadImage())
										.signature(new ObjectKey(String.valueOf(System.currentTimeMillis())))
										.error(R.mipmap.load404)
										.transition(DrawableTransitionOptions.withCrossFade())
										.into(cover);
							}
						});
					}
				}

				@Override
				public void onFailure(Call<Msg<JsonElement>> call, Throwable t) {
					handler.post(() -> {
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
							uname.setText(user.getUsername());
							if (user.getSignature()!=null){
								usign.setText(user.getSignature());
							}
							if (userImg != null) {
								Glide.with(UserInfoCenterActivity.this.getApplicationContext())
										.load(ServerURL.MAIN_URL+userImg)
										.signature(new ObjectKey(String.valueOf(System.currentTimeMillis())))
										.transition(DrawableTransitionOptions.withCrossFade())
										.error(R.mipmap.load404)
										.into(cover);
								Glide.with(UserInfoCenterActivity.this)
										.load(ServerURL.MAIN_URL+userImg)
										.transition(DrawableTransitionOptions.withCrossFade())
										.signature(new ObjectKey(String.valueOf(System.currentTimeMillis())))
										.apply(RequestOptions.bitmapTransform(new BlurTransformation(UserInfoCenterActivity.this, 25, 8)))
										.error(R.mipmap.load404)
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
	public void gotoCover() {
		Intent intent = new Intent(this, UploadHeadActivity.class);
		ActivityOptions options = ActivityOptions
				.makeSceneTransitionAnimation(this, cover, "upload_cover");
		startActivity(intent, options.toBundle());
	}

	@OnClick(R.id.img_back)
	public void btnBack() {
		finish();
	}

	@OnClick(R.id.text_menu_editinfo)
	public void gotoEdit() {
		startActivity(new Intent(this, EditInfoActivity.class));
	}

}
