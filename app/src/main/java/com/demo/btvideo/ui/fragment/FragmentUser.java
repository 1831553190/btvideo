package com.demo.btvideo.ui.fragment;

import android.app.ActivityOptions;
import android.content.Intent;
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
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.target.Target;
import com.demo.btvideo.AppController;
import com.demo.btvideo.R;
import com.demo.btvideo.dao.AppDatabase;
import com.demo.btvideo.model.User;
import com.demo.btvideo.ui.activity.LoginActivity;
import com.demo.btvideo.ui.activity.UploadHeadActivity;
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
	ExecutorService pool= Executors.newCachedThreadPool();
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


	Handler handler=new Handler(Looper.getMainLooper());
	AppDatabase database;


	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		mainView = inflater.inflate(R.layout.fragment_user, container, false);
		ButterKnife.bind(this, mainView);
		database = Room.databaseBuilder(getContext().getApplicationContext(),AppDatabase.class,"user")
				.fallbackToDestructiveMigration().build();
		final ListenableFuture<User> listenableFuture = executorService.submit(new Callable<User>() {
			@Override
			public User call() throws Exception {
				return database.userDao().getUser();
			}
		});
		Futures.addCallback(listenableFuture, new FutureCallback<User>() {
			@Override
			public void onSuccess(@NullableDecl User result) {
				if (result!=null){
					handler.post(()->{

						textUsername.setText(result.getUsername());
						textUserId.setText(result.getAccount());
						File file=new File(result.getHeadImage());

						try {
							FileInputStream inputStream=new FileInputStream(file);
							Glide.with(getContext())
									.load(file.getAbsolutePath())
									.skipMemoryCache(true)
									.diskCacheStrategy(DiskCacheStrategy.NONE)
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
		},executorService);


		LoginViewModel.getInstance().update().observe(getViewLifecycleOwner(), new Observer<Integer>() {
			@Override
			public void onChanged(Integer integer) {
				boolean l = AppController.getInstance().isLogin();
				guestView.setVisibility(l ? View.GONE : View.VISIBLE);
				executorService.submit(()->{
					User user=database.userDao().getUser();
					handler.post(()->{
						if (user!=null){
							String userImg=user.getHeadImage();
							if (userImg!=null){
								Glide.with(getContext())
										.load(userImg)
										.transition(DrawableTransitionOptions.withCrossFade())
										.skipMemoryCache(true)
										.diskCacheStrategy(DiskCacheStrategy.NONE)
										.error(R.mipmap.imglogin)
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

				pool.execute(()->{
					try {
						File file1=Glide.with(getContext())
								.load(user.getHeadImage())
								.downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
								.get();
						File cover=saveCover(user,file1);
						user.setHeadImage(cover.getAbsolutePath());
						database.userDao().insertAll(user);
						handler.post(()->{
							Glide.with(getContext())
									.load(cover)
									.transition(DrawableTransitionOptions.withCrossFade())
									.skipMemoryCache(true)
									.diskCacheStrategy(DiskCacheStrategy.NONE)
									.error(R.mipmap.imglogin)
									.into(imgHead);
						});
					} catch (ExecutionException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				});
				boolean l = AppController.getInstance().isLogin();
				guestView.setVisibility(l ? View.GONE : View.VISIBLE);
			}
		});
		return mainView;
	}

	@OnClick(R.id.img_login)
	public void toLogin() {
		startActivity(new Intent(getContext(), LoginActivity.class));
	}

	@OnClick(R.id.img_userHeadPic)
	public void upload(View view) {
		Intent intent=new Intent(getContext(), UploadHeadActivity.class);
		ActivityOptions options = ActivityOptions
				.makeSceneTransitionAnimation(getActivity(), view, "upload_cover");
		getContext().startActivity(intent,options.toBundle());

	}

	@OnClick(R.id.btn_logout)
	public void btnLogout(){
		PreferenceManager.getDefaultSharedPreferences(getContext())
				.edit().remove("token").apply();
		startActivity(new Intent(getContext(),LoginActivity.class));
	}

	private File saveCover(User user, File of) {
		String storePath = getContext().getFilesDir().getAbsolutePath();
		File appdir=new File(storePath);
		if (!appdir.exists()) {
			appdir.mkdir();
		}
		String userHead=user.getHeadImage();
		String fileName = user.getAccount() + userHead.substring(userHead.lastIndexOf("."),user.getHeadImage().length());
		File file = new File(appdir, fileName);
		try {
			FileOutputStream fos = new FileOutputStream(file);
			FileInputStream fis = new FileInputStream(of);
			BufferedInputStream bis=new BufferedInputStream(fis);
			byte[] buffer = new byte[1024];
			int byteRead;
			while (-1 != (byteRead = bis.read(buffer))) {
				fos.write(buffer, 0, byteRead);
			}
			bis.close();
			fos.flush();
			fos.close();
			return file;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}



}
