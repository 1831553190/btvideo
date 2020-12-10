package com.demo.btvideo.ui.fragment;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.CombinedLoadStates;
import androidx.paging.PagingData;
import androidx.paging.PagingDataAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.demo.btvideo.AppController;
import com.demo.btvideo.R;
import com.demo.btvideo.UploadService;
import com.demo.btvideo.adapter.LoadMoreAdapter;
import com.demo.btvideo.model.Comment;
import com.demo.btvideo.model.Msg;
import com.demo.btvideo.model.VideoInfo;
import com.demo.btvideo.net.NetInterface;
import com.demo.btvideo.net.ServerURL;
import com.demo.btvideo.ui.activity.LoginActivity;
import com.demo.btvideo.ui.activity.MainActivity;
import com.demo.btvideo.ui.activity.UploadHistoryActivity;
import com.demo.btvideo.utils.GlideCircleBorderTransform;
import com.demo.btvideo.utils.NetWorkUtils;
import com.demo.btvideo.viewmodel.DataViewModel;
import com.demo.btvideo.viewmodel.DataLoaderViewModel;
import com.google.gson.JsonElement;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.os.Environment.DIRECTORY_DOWNLOADS;
import static android.os.Environment.DIRECTORY_MOVIES;
import static android.text.TextUtils.isEmpty;


//视频详情页
public class FragmentVideoDetail extends Fragment {
	View mainView;

	@BindView(R.id.text_video_title)
	TextView textTitle;
	@BindView(R.id.text_video_user)
	TextView textUser;
	@BindView(R.id.text_video_description)
	TextView textDesxription;
	@BindView(R.id.text_praiseNum)
	TextView textPraiseNum;
	@BindView(R.id.text_collectioinNum)
	TextView textCollectioinNum;

	@BindView(R.id.img_video_cover)
	ImageView userPrifile;
	NetInterface netInterface;

	@BindView(R.id.img_praise)
	ImageView imgPraise;
	@BindView(R.id.img_favorite)
	ImageView imgFavorite;
	@BindView(R.id.text_dltime)
	TextView dlText;

	@BindView(R.id.btn_Attaction)
	Button btnAttenction;
	VideoInfo videoInfo = null;

	Handler handler = new Handler(Looper.getMainLooper());
	UploadService.Binder binder;

	private static class Holder {
		private static FragmentVideoDetail instance = new FragmentVideoDetail();
	}

	public static FragmentVideoDetail getInstance() {
		return FragmentVideoDetail.Holder.instance;
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		mainView = inflater.inflate(R.layout.fragment_video_detail, container, false);
		ButterKnife.bind(this, mainView);
		DataViewModel dataViewModel = new ViewModelProvider(requireActivity()).get(DataViewModel.class);
		dataViewModel.loadVideoInfo("", "").observe(getViewLifecycleOwner(), new Observer<VideoInfo>() {
			@Override
			public void onChanged(VideoInfo videoInfoD) {
				videoInfo = videoInfoD;
				textTitle.setText(videoInfoD.getTitle());
				textUser.setText(videoInfoD.getUser().getUsername());
				textDesxription.setText(videoInfoD.getDescription());
				Glide.with(getContext())
						.load(ServerURL.MAIN_URL + videoInfoD.getUser().getHeadImage())
						.transform(new GlideCircleBorderTransform(2, Color.WHITE))
						.transition(DrawableTransitionOptions.withCrossFade())
						.into(userPrifile);

				processView(videoInfo);
			}
		});
		netInterface = NetWorkUtils.getRetrofit().create(NetInterface.class);

		Intent intent=new Intent(getContext(),UploadService.class);
		getContext().startService(intent);
		getContext().bindService(intent,serviceConnection,Context.BIND_AUTO_CREATE);
		return mainView;
	}


	@Override
	public void onDestroy() {
		super.onDestroy();
		Objects.requireNonNull(getContext()).unbindService(serviceConnection);
	}
	public void processView(VideoInfo videoInfo) {
		handler.post(() -> {
			if (videoInfo.getPraiseStatus() != null && videoInfo.getPraiseStatus().equals("1")) {
				imgPraise.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.color_yes)));
			} else {
				imgPraise.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.color_no)));
			}
			if (videoInfo.getCollectStatus() != null && videoInfo.getCollectStatus().equals("1")) {
				imgFavorite.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.color_yell)));
			} else {
				imgFavorite.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.color_no)));
			}
			if (videoInfo.getUser().getAttentionStatus() != null && videoInfo.getUser().getAttentionStatus().equals("1")) {
				btnAttenction.setText("已关注");
			} else {
				btnAttenction.setText("+关注");
			}
			textPraiseNum.setText(MessageFormat.format("({0} 次点赞)", videoInfo.getPraiseNum()));
			textCollectioinNum.setText(MessageFormat.format("({0} 次收藏)", videoInfo.getCollectNum()));
			dlText.setText(MessageFormat.format("缓存视频",""));
		});


	}

	ServiceConnection serviceConnection=new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			binder= (UploadService.Binder) service;
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {

		}
	};
	@OnClick(R.id.layoutPraise)
	public void preise() {
		if (AppController.getInstance().isLogin()) {
			if (videoInfo != null) {
				HashMap<String, String> body = new HashMap<>();
				body.put("videoId", String.valueOf(videoInfo.getId()));
				body.put("praiseNum", String.valueOf(videoInfo.getPraiseNum()));
				if (videoInfo.getPraiseStatus() == null || videoInfo.getPraiseStatus().equals("2")) {
					body.put("praiseStatus", "1");
				} else {
					body.put("praiseStatus", "2");

				}
				netInterface.praiseVideo(body).enqueue(new Callback<Msg<JsonElement>>() {
					@Override
					public void onResponse(Call<Msg<JsonElement>> call, Response<Msg<JsonElement>> response) {
						if (response.body() != null) {
							videoInfo.setPraiseNum(response.body().getData().getAsJsonObject().get("praiseNum").getAsInt());
							videoInfo.setPraiseStatus(response.body().getData().getAsJsonObject().get("praiseStatus").getAsString());
							if (response.isSuccessful() && response.body().getCode() == 200) {
								if (videoInfo.getPraiseStatus().equals("1")) {
									Toast.makeText(getContext(), "点赞成功", Toast.LENGTH_SHORT).show();
								} else {
									Toast.makeText(getContext(), "已取消点赞", Toast.LENGTH_SHORT).show();
								}
							} else {
								Toast.makeText(getContext(), "点赞失败", Toast.LENGTH_SHORT).show();
							}
							processView(videoInfo);
						} else {
							Toast.makeText(getContext(), "点赞失败", Toast.LENGTH_SHORT).show();
						}
					}

					@Override
					public void onFailure(Call<Msg<JsonElement>> call, Throwable t) {
						handler.post(() -> {
							Toast.makeText(getContext(), "连接失败", Toast.LENGTH_SHORT).show();
						});
					}
				});
			} else {
				Toast.makeText(getContext(), "获取视频信息失败", Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(getContext(), "请先登录", Toast.LENGTH_SHORT).show();
			startActivity(new Intent(getContext(), LoginActivity.class));
		}
	}

	@OnClick(R.id.layoutCollectoin)
	public void collection() {
		if (AppController.getInstance().isLogin()) {
			if (videoInfo != null) {
				HashMap<String, String> body = new HashMap<>();
				if (videoInfo.getCollectStatus() == null || videoInfo.getCollectStatus().equals("2")) {
					body.put("collectStatus", "1");
				} else {
					body.put("collectStatus", "2");
				}
				body.put("videoId", String.valueOf(videoInfo.getId()));
				body.put("collectNum", String.valueOf(videoInfo.getCollectNum()));
				netInterface.collectVideo(body).enqueue(new Callback<Msg<JsonElement>>() {
					@Override
					public void onResponse(Call<Msg<JsonElement>> call, Response<Msg<JsonElement>> response) {
						if (response.body() != null) {
							if (response.isSuccessful() && response.body().getCode() == 200) {
								String collStatus = response.body().getData().getAsJsonObject().get("collectStatus").getAsString();
								videoInfo.setCollectNum(response.body().getData().getAsJsonObject().get("collectNum").getAsInt());
								videoInfo.setCollectStatus(collStatus);
								if (collStatus.equals("1")) {
									Toast.makeText(getContext(), "收藏成功", Toast.LENGTH_SHORT).show();
								} else {
									Toast.makeText(getContext(), "取消收藏成功", Toast.LENGTH_SHORT).show();
								}
								processView(videoInfo);
							} else {
								Toast.makeText(getContext(), "收藏失败", Toast.LENGTH_SHORT).show();
							}
						} else {
							Toast.makeText(getContext(), "收藏失败", Toast.LENGTH_SHORT).show();
						}
					}

					@Override
					public void onFailure(Call<Msg<JsonElement>> call, Throwable t) {
						handler.post(() -> {
							Toast.makeText(getContext(), "连接失败", Toast.LENGTH_SHORT).show();
						});
					}
				});

			} else {
				Toast.makeText(getContext(), "获取视频信息失败", Toast.LENGTH_SHORT).show();
			}

		} else {
			Toast.makeText(getContext(), "请先登录", Toast.LENGTH_SHORT).show();
			startActivity(new Intent(getContext(), LoginActivity.class));
		}

	}

	@OnClick(R.id.btn_Attaction)
	public void follow() {
		if (AppController.getInstance().isLogin()) {
			if (videoInfo != null) {
				HashMap<String, String> body = new HashMap<>();
				body.put("followAccount", String.valueOf(videoInfo.getUserAccount()));
				body.put("attentionNum", String.valueOf(videoInfo.getUser().getFansNum()));
				if (videoInfo.getUser().getAttentionStatus() == null || videoInfo.getUser().getAttentionStatus().equals("2")) {
					body.put("status", "1");
				} else {
					body.put("status", "2");
				}
				netInterface.followUser(body).enqueue(new Callback<Msg<JsonElement>>() {
					@Override
					public void onResponse(Call<Msg<JsonElement>> call, Response<Msg<JsonElement>> response) {
						if (response.body() != null) {
							if (response.isSuccessful() && response.body().getCode() == 200) {
								videoInfo.getUser().setFansNum(response.body().getData().getAsJsonObject().get("attentionNum").getAsInt());
								videoInfo.getUser().setAttentionStatus(response.body().getData().getAsJsonObject().get("status").getAsString());

								if (videoInfo.getUser().getAttentionStatus().equals("1")) {
									Toast.makeText(getContext(), "关注成功", Toast.LENGTH_SHORT).show();
								} else {
									Toast.makeText(getContext(), "已取消关注", Toast.LENGTH_SHORT).show();
								}
							} else {
								Toast.makeText(getContext(), "关注失败", Toast.LENGTH_SHORT).show();
							}
							processView(videoInfo);
						} else {
							Toast.makeText(getContext(), "关注失败", Toast.LENGTH_SHORT).show();
						}
					}

					@Override
					public void onFailure(Call<Msg<JsonElement>> call, Throwable t) {
						handler.post(() -> {
							Toast.makeText(getContext(), "连接失败", Toast.LENGTH_SHORT).show();
						});
					}
				});
			} else {
				Toast.makeText(getContext(), "获取视频信息失败", Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(getContext(), "请先登录", Toast.LENGTH_SHORT).show();
			startActivity(new Intent(getContext(), LoginActivity.class));
		}
	}

	@OnClick(R.id.layoutdownload)
	public void downloadVideo(){
		if (videoInfo!=null){
			Toast.makeText(getContext(), "缓存中", Toast.LENGTH_SHORT).show();
			binder.downloadVideo(ServerURL.MAIN_URL+videoInfo.getVideoUrl(),videoInfo.getTitle());
//			downFileFromServiceToPublicDir(ServerURL.MAIN_URL+videoInfo.getVideoUrl(),getContext());
		}
	}

	@OnClick({R.id.img_video_cover})
	public void gotoUploadHistory(){
		if (videoInfo!=null){
			Intent intent=new Intent(getContext(), UploadHistoryActivity.class);
			intent.putExtra("account",videoInfo.getUserAccount());
			startActivity(intent);
		}
	}



}
