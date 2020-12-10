package com.demo.btvideo.ui.fragment;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
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
import com.demo.btvideo.model.Attention;
import com.demo.btvideo.model.Collection;
import com.demo.btvideo.model.Msg;
import com.demo.btvideo.model.PageInfo;
import com.demo.btvideo.model.User;
import com.demo.btvideo.model.UserMessage;
import com.demo.btvideo.net.NetInterface;
import com.demo.btvideo.net.ServerURL;
import com.demo.btvideo.statement.StateGuest;
import com.demo.btvideo.ui.activity.AboutActivity;
import com.demo.btvideo.ui.activity.AttctionActivity;
import com.demo.btvideo.ui.activity.CollectionActivity;
import com.demo.btvideo.ui.activity.FunsActivity;
import com.demo.btvideo.ui.activity.LoginActivity;
import com.demo.btvideo.ui.activity.MessageActivity;
import com.demo.btvideo.ui.activity.PlayHistoryActivity;
import com.demo.btvideo.ui.activity.SettingActivity;
import com.demo.btvideo.ui.activity.UploadHeadActivity;
import com.demo.btvideo.ui.activity.UploadHistoryActivity;
import com.demo.btvideo.ui.activity.UserInfoCenterActivity;
import com.demo.btvideo.ui.activity.VipActivity;
import com.demo.btvideo.ui.view.SettingItem;
import com.demo.btvideo.utils.NetWorkUtils;
import com.demo.btvideo.utils.Tool;
import com.demo.btvideo.viewmodel.LoginViewModel;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

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
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import io.reactivex.functions.Function3;
import io.reactivex.schedulers.Schedulers;



//首页-用户界面
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

	//用户消息数
	@BindView(R.id.text_userMsg)
	TextView textMsg;
	//用户收藏数
	@BindView(R.id.user_textCollect)
	TextView textCollectNum;
	//关注数
	@BindView(R.id.text_userFollow)
	TextView textFollow;


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
				LoginViewModel.getInstance().notifyData().postValue(200);
				return database.userDao().getUser(preferences.getString("userNow", "-1"));
			}
		});
		Futures.addCallback(listenableFuture, new FutureCallback<User>() {
			@Override
			public void onSuccess(@NullableDecl User result) {
				if (result != null) {
					handler.post(() -> {
						textUsername.setText(result.getUsername());
						if (result.getSignature() == null) {
							textUserId.setText("这个家伙很懒,什么都没留下");
						} else {
							textUserId.setText(result.getSignature());
						}
//						File file = new File(result.getHeadImage());
//						try {
//							FileInputStream inputStream = new FileInputStream(file);
							Glide.with(getContext())
									.load(ServerURL.MAIN_URL+result.getHeadImage())
									.signature(new ObjectKey(String.valueOf(System.currentTimeMillis())))
									.error(R.mipmap.load404)
									.into(imgHead);
//						} catch (FileNotFoundException e) {
//							e.printStackTrace();
//						}
					});
				}
			}

			@Override
			public void onFailure(Throwable t) {
				t.printStackTrace();
			}
		}, executorService);

		viewModelObserver();

		return mainView;
	}


	@OnClick(R.id.pref_history)
	public void watchHistory() {
		startActivity(new Intent(getContext(), PlayHistoryActivity.class));

	}


	@OnClick({R.id.pref_collection, R.id.collectLayout})
	public void watchCollection() {
		startActivity(new Intent(getContext(), CollectionActivity.class));
	}

	@OnClick(R.id.pref_publish)
	public void watchPublish() {
		startActivity(new Intent(getContext(), UploadHistoryActivity.class));
	}

	@OnClick({R.id.pref_favourites, R.id.layoutAttction})
	public void watchFavourite() {
		startActivity(new Intent(getContext(), AttctionActivity.class));
	}

	@OnClick(R.id.pref_funs)
	public void watchFuns() {
		startActivity(new Intent(getContext(), FunsActivity.class));

	}

	@OnClick(R.id.pref_pay)
	public void gotoPay() {
		startActivity(new Intent(getContext(), VipActivity.class));

	}

	@OnClick(R.id.pref_settings)
	public void gotoSetting() {
		startActivity(new Intent(getContext(), SettingActivity.class));
	}


	@OnClick(R.id.pref_about)
	public void gotoAbout() {
		startActivity(new Intent(getContext(), AboutActivity.class));
	}


	private void viewModelObserver() {
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
										.load(ServerURL.MAIN_URL+userImg)
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
//						try {
//							File file1 = Glide.with(getContext())
//									.load(ServerURL.MAIN_URL + user.getHeadImage())
//									.downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
//									.get();
//							File cover = Tool.saveCover(getContext(), user, file1);
//							user.setHeadImage(cover.getAbsolutePath());
							handler.post(() -> {
								Glide.with(getContext())
										.load(ServerURL.MAIN_URL+user.getHeadImage())
										.transition(DrawableTransitionOptions.withCrossFade())
										.signature(new ObjectKey(String.valueOf(System.currentTimeMillis())))
										.error(R.mipmap.load404)
										.into(imgHead);
								textUsername.setText(user.getUsername());
								textUserId.setText(user.getSignature());
							});
//						} catch (ExecutionException e) {
//							e.printStackTrace();
//						} catch (InterruptedException e) {
//							e.printStackTrace();
//						}
					}

					database.userDao().insertAll(user);
				});
				handler.post(() -> {
					boolean l = AppController.getInstance().isLogin();
					guestView.setVisibility(l ? View.GONE : View.VISIBLE);
					userInfoCenter.setVisibility(l ? View.VISIBLE : View.GONE);
				});
			}
		});

		LoginViewModel.getInstance().notifyData().observe(getViewLifecycleOwner(), new Observer<Integer>() {
			@Override
			public void onChanged(Integer integer) {
				loadIndexData();
			}
		});


	}


	@Override
	public void onResume() {
		super.onResume();
		loadIndexData();
	}

	private void loadIndexData() {
		NetInterface netInterface = NetWorkUtils.getRetrofit().create(NetInterface.class);
		Observable<Msg<PageInfo<UserMessage>>> msgObs = netInterface.getMessageList("1").subscribeOn(Schedulers.io());
//		Observable<Msg<JsonElement>> userInfo = netInterface.getUserInfoOb().subscribeOn(Schedulers.io());

		Observable<Msg<PageInfo<Collection>>> collObs = netInterface.getCollectionList("1").subscribeOn(Schedulers.io());
		Observable<Msg<PageInfo<Attention>>> favoObs = netInterface.getAttentionList("1").subscribeOn(Schedulers.io());


		Observable.zip(msgObs, collObs, favoObs, (pageInfoMsg, pageInfoMsg2, pageInfoMsg3) -> {
			handler.post(() -> {
				textMsg.setText(String.valueOf(pageInfoMsg.getData().getTotal()));
				textCollectNum.setText(String.valueOf(pageInfoMsg2.getData().getTotal()));
				textFollow.setText(String.valueOf(pageInfoMsg3.getData().getTotal()));
//				User user=new Gson().fromJson(uif.getData(),User.class);
//				if (user!=null){
//					LoginViewModel.getInstance().userLiveData().postValue(user);
//				}

			});
			Log.d("TAG", "subscribe: " + pageInfoMsg.getData().getTotal());
			Log.d("TAG", "subscribe: " + pageInfoMsg2.getData().getTotal());
			Log.d("TAG", "subscribe: " + pageInfoMsg3.getData().getTotal());
			return null;
		}).subscribe(new io.reactivex.Observer<Object>() {
			@Override
			public void onSubscribe(Disposable d) {

			}

			@Override
			public void onNext(Object o) {

			}

			@Override
			public void onError(Throwable e) {
				e.printStackTrace();
				System.out.println("error");
			}

			@Override
			public void onComplete() {

			}
		});

//		Observable.zip(msgObs, collObs, new BiFunction<Msg<PageInfo<UserMessage>>, Msg<PageInfo<Collection>>, Object>() {
//			@Override
//			public Object apply(Msg<PageInfo<UserMessage>> pageInfoMsg, Msg<PageInfo<Collection>> pageInfoMsg2) throws Exception {
//				handler.post(()->{
//					textMsg.setText(String.valueOf(pageInfoMsg.getData().getTotal()));
//					textCollectNum.setText(String.valueOf(pageInfoMsg2.getData().getTotal()));
//				});
//				Log.d("TAG", "subscruibe: " + pageInfoMsg.getData().getTotal());
//				Log.d("TAG", "subscribe: " + pageInfoMsg2.getData().getTotal());
//				return null;
//			}
//		}).subscribe(new io.reactivex.Observer<Object>() {
//			@Override
//			public void onSubscribe(Disposable d) {
//
//			}
//
//			@Override
//			public void onNext(Object o) {
//
//			}
//
//			@Override
//			public void onError(Throwable e) {
//
//			}
//
//			@Override
//			public void onComplete() {
//
//			}
//		});

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
		guestView.setVisibility(l ? View.GONE : View.VISIBLE);
		userInfoCenter.setVisibility(l ? View.VISIBLE : View.GONE);
	}

	@OnClick({R.id.pref_msg, R.id.layout_message})
	public void gotoMessage(View v) {
		startActivity(new Intent(getContext(), MessageActivity.class));
	}

	@OnClick(R.id.user_info_center)
	public void userInfo() {
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
