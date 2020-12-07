package com.demo.btvideo.ui.fragment;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.room.Room;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.signature.ObjectKey;
import com.demo.btvideo.AppController;
import com.demo.btvideo.R;
import com.demo.btvideo.dao.AppDatabase;
import com.demo.btvideo.model.User;
import com.demo.btvideo.net.ServerURL;
import com.demo.btvideo.statement.StateGuest;
import com.demo.btvideo.ui.activity.LoginActivity;
import com.demo.btvideo.ui.activity.MessageActivity;
import com.demo.btvideo.ui.activity.SettingActivity;
import com.demo.btvideo.ui.activity.UploadHeadActivity;
import com.demo.btvideo.ui.activity.UserInfoCenterActivity;
import com.demo.btvideo.ui.view.SettingItem;
import com.demo.btvideo.utils.Tool;
import com.demo.btvideo.viewmodel.LoginViewModel;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FragmentUser extends Fragment {

	private static FragmentUser fragmentUser;
	View mainView;
	@BindView(R.id.img_userHeadPic)
	ImageView imgHead;
	@BindView(R.id.text_username)
	TextView textUsername;
	@BindView(R.id.text_userid)
	TextView textUserId;

	@BindView(R.id.guestView)
	ConstraintLayout guestView;
	@BindView(R.id.user_info_center)
	ConstraintLayout userInfoCenter;


	ExecutorService pool = Executors.newCachedThreadPool();
	ListeningExecutorService executorService = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());


	public static FragmentUser getInstance() {
		if (fragmentUser == null) {
			synchronized (FragmentUser.class) {
				if (fragmentUser == null) {
					fragmentUser = new FragmentUser();
				}
			}
		}
		return fragmentUser;
	}


	Handler handler = new Handler(Looper.getMainLooper());
	AppDatabase database;
	SharedPreferences preferences;


	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		mainView = inflater.inflate(R.layout.fragment_user, container, false);
		ButterKnife.bind(this, mainView);
		preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
		database = Room.databaseBuilder(getContext().getApplicationContext(), AppDatabase.class, "user")
				.fallbackToDestructiveMigration().build();
		ListenableFuture<User> listenableFuture = executorService.submit(new Callable<User>() {
			@Override
			public User call() throws Exception {
				return database.userDao().getUser(preferences.getString("userNow", "-1"));
			}
		});
		Futures.addCallback(listenableFuture, new FutureCallback<User>() {
			@Override
			public void onSuccess(@NullableDecl User result) {
				if (result != null) {
					handler.post(() -> {
						textUsername.setText(result.getUsername());
						textUserId.setText(result.getAccount());
						File file = new File(result.getHeadImage());
						try {
							FileInputStream inputStream = new FileInputStream(file);
							Glide.with(getContext())
									.load(result.getHeadImage())
									.error(R.mipmap.ic_launcher)
									.into(imgHead);
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						}

					});
				}
			}

			@Override
			public void onFailure(Throwable t) {
				t.printStackTrace();
			}
		}, executorService);

		LoginViewModel.getInstance().update().observe(getViewLifecycleOwner(), new Observer<Integer>() {
			@Override
			public void onChanged(Integer integer) {
				boolean l = AppController.getInstance().isLogin();
				guestView.setVisibility(l ? View.GONE : View.VISIBLE);
				userInfoCenter.setVisibility(l ? View.VISIBLE : View.GONE);
				executorService.submit(() -> {
					User user = database.userDao().getUser(preferences.getString("userNow", "-1"));
					handler.post(() -> {
						if (user != null) {
							String userImg = user.getHeadImage();
							if (userImg != null) {
								Glide.with(getActivity().getApplicationContext())
										.load(userImg)
										.signature(new ObjectKey(String.valueOf(System.currentTimeMillis())))
										.transition(DrawableTransitionOptions.withCrossFade())
										.error(R.mipmap.load404)
										.into(imgHead);
							}

						}
					});
				});
			}
		});
		LoginViewModel.getInstance().userLiveData().observe(getViewLifecycleOwner(), new Observer<User>() {
			@Override
			public void onChanged(User user) {

				pool.execute(() -> {
					if (user.getHeadImage() == null) {
						user.setHeadImage(defaultCover(user));
					} else {
						try {
							File file1 = Glide.with(getContext())
									.load(ServerURL.MAIN_URL+user.getHeadImage())
									.downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
									.get();
							File cover = Tool.saveCover(getContext(),user, file1);
							user.setHeadImage(cover.getAbsolutePath());
							handler.post(() -> {
								Glide.with(getContext())
										.load(user.getHeadImage())
										.transition(DrawableTransitionOptions.withCrossFade())
										.error(R.mipmap.load404)
										.into(imgHead);
								textUsername.setText(user.getUsername());
								textUserId.setText(user.getAccount());
							});
						} catch (ExecutionException e) {
							e.printStackTrace();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}

					database.userDao().insertAll(user);
				});
				boolean l = AppController.getInstance().isLogin();
				guestView.setVisibility(l ? View.GONE : View.VISIBLE);
				userInfoCenter.setVisibility(l ? View.VISIBLE : View.GONE);
			}
		});
		return mainView;
	}


	@OnClick(R.id.pref_history)
	public void watchHistory(){

	}


	@OnClick(R.id.pref_collection)
	public void watchCollection(){

	}

	@OnClick(R.id.pref_publish)
	public void watchPublish(){

	}




	@OnClick(R.id.pref_msg)
	public void watchMsg(){

	}


	@OnClick(R.id.pref_favourites)
	public void watchFavourite(){

	}

	@OnClick(R.id.pref_funs)
	public void watchFuns(){

	}

	@OnClick(R.id.pref_pay)
	public void gotoPay(){

	}
	@OnClick(R.id.pref_settings)
	public void gotoSetting(){
		startActivity(new Intent(getContext(), SettingActivity.class));
	}


	@OnClick(R.id.pref_about)
	public void gotoAbout(){

	}



	@OnClick(R.id.img_login)
	public void toLogin() {
		startActivity(new Intent(getContext(), LoginActivity.class));
	}

	@OnClick(R.id.img_userHeadPic)
	public void upload(View view) {
		Intent intent = new Intent(getContext(), UploadHeadActivity.class);
		ActivityOptions options = ActivityOptions
				.makeSceneTransitionAnimation(getActivity(), view, "upload_cover");
		getContext().startActivity(intent, options.toBundle());
	}

	@OnClick(R.id.btn_logout)
	public void btnLogout() {
		PreferenceManager.getDefaultSharedPreferences(getContext())
				.edit().remove("token").remove("userNow").apply();
		AppController.getInstance().setLogin(new StateGuest());
		AppController.getInstance().updateAuth("");
		startActivity(new Intent(getContext(), LoginActivity.class));
		boolean l = AppController.getInstance().isLogin();
		guestView.setVisibility (l ? View.GONE : View.VISIBLE);
		userInfoCenter.setVisibility(l ? View.VISIBLE : View.GONE);
	}

	@OnClick({R.id.pref_msg,R.id.layout_message})
	public void gotoMessage(View v) {
		startActivity(new Intent(getContext(), MessageActivity.class));
	}
	@OnClick(R.id.user_info_center)
	public void userInfo(){
		startActivity(new Intent(getContext(), UserInfoCenterActivity.class));
	}


	private String defaultCover(User user) {
		String storePath = getContext().getFilesDir().getAbsolutePath();
		File appdir = new File(storePath);
		if (!appdir.exists()) {
			appdir.mkdir();
		}
		String fileName = user.getAccount() + ".png";
		return new File(appdir, fileName).getAbsolutePath();
	}



}
