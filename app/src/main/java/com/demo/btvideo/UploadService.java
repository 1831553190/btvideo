package com.demo.btvideo;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.demo.btvideo.model.Msg;
import com.demo.btvideo.net.NetInterface;
import com.demo.btvideo.ui.activity.UploadVideoAcivity;
import com.demo.btvideo.utils.NetWorkUtils;
import com.demo.btvideo.utils.UploadRequestBody;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadService extends Service {


	public Binder binder = new Binder();

	Handler handler = new Handler(Looper.getMainLooper());
	NotificationManager notificationManager;
	Notification notification;

	UpdateLoadPregress updateLoadPregress;
	final static String ACTION_CANCEL="com.myservice.action.ACTION_CANCEL";


	public interface UpdateLoadPregress {
		void getProgress(long progress);

		void getMax(long max);
	}

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	public class Binder extends android.os.Binder {
		private HashMap<Integer,Call<Msg<String>>> listCall=new HashMap<>();
		Call<Msg<String>> msgCall;

		public HashMap<Integer,Call<Msg<String>>> getListCall(){
			return listCall;
		}

		public void addCall(Call<Msg<String>> call){
			listCall.put(listCall.size(),call);
		}

		public void cancelCall(int id){
			Call<Msg<String>> call=listCall.get(id);
			if (call!=null&&!call.isCanceled()){
				call.cancel();
			}
			listCall.remove(id);
		}

		public void uploadVideo(Uri img, Uri video, HashMap<String, String> data) {
			DecimalFormat decimalFormat = new DecimalFormat("##.##");
			notification = createNotification(70, "上传中");
			notificationManager.notify(70, notification);
			Random random
					=new Random();
			int id=random.nextInt();

			UploadRequestBody imageBody = new UploadRequestBody(id,img, MediaType.parse("multipart/form-data"), getApplicationContext(), new UploadRequestBody.UploadProgress() {
				@Override
				public void getPercent(int id,int percent) {
					notification = createNotification(percent, "上传图片中");
					notificationManager.notify(70, notification);
				}

				@Override
				public void getProgress(int id,long progress) {
					if (updateLoadPregress != null) {
						updateLoadPregress.getProgress(progress);
					}
				}
			});
			UploadRequestBody videoBody = new UploadRequestBody(id,video, MediaType.parse("multipart/form-data"), getApplicationContext(), new UploadRequestBody.UploadProgress() {
				@Override
				public void getPercent(int id,int percent) {
					notification = createNotification(percent, "上传视频中");
					notificationManager.notify(70, notification);
				}

				@Override
				public void getProgress(int id,long progress) {
					if (updateLoadPregress != null) {
						updateLoadPregress.getProgress(progress);
					}
				}
			});
			MultipartBody.Builder builder = new MultipartBody.Builder()
					.setType(MultipartBody.FORM);

			builder.addFormDataPart("imageFile", "head.png", imageBody);
			builder.addFormDataPart("videoFile", "video.mp4", videoBody);
			List<MultipartBody.Part> requestBody = builder.build().parts();

			msgCall = NetWorkUtils.getRetrofit().create(NetInterface.class).uploadVideo(requestBody, data);
			addCall(msgCall);

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
				startForegroundService(new Intent(getApplicationContext(), UploadService.class));
			}
			startForeground(70, notification);

			msgCall.enqueue(new Callback<Msg<String>>() {
				@Override
				public void onResponse(Call<Msg<String>> call, Response<Msg<String>> response) {
					if (response.isSuccessful() && response.body() != null) {
						Msg<String> msg = response.body();
						handler.post(() -> {
							stopForeground(true);
							if (msg.getCode() == 200) {
								Toast.makeText(getApplicationContext(), "上传成功!", Toast.LENGTH_SHORT).show();
								finishNotification(id,"上传成功", "视频上传成功");
							} else {
								Toast.makeText(getApplicationContext(), "上传失败", Toast.LENGTH_SHORT).show();
								finishNotification(id,"上传失败", "视频上传失败");
							}
						});
					}
				}

				@Override
				public void onFailure(Call<Msg<String>> call, Throwable t) {
					t.printStackTrace();
					handler.post(() -> {
						Toast.makeText(getApplicationContext(), "上传失败!" + t.getMessage(), Toast.LENGTH_SHORT).show();
						finishNotification(id,"上传失败", "视频上传失败");
						stopForeground(false);
					});
				}
			});
		}

	}


	public Notification createNotification(int progress, String title) {
		Notification.Builder builder = new Notification.Builder(getApplicationContext());
		builder.setContentTitle(title)
				.setTicker(title)
				.setWhen(java.lang.System.currentTimeMillis())
				.setSmallIcon(R.mipmap.ic_launcher)
				.setAutoCancel(false)
				.setOngoing(true)
				.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
				.setPriority(Notification.PRIORITY_LOW)
				.setProgress(100, progress, false);
		Intent intentCancel=new Intent(this,UploadService.class);
		intentCancel.setAction(ACTION_CANCEL);
		builder.addAction(new Notification.Action(R.mipmap.ic_launcher,"取消",
				PendingIntent.getService(getApplicationContext(),400,intentCancel
						,PendingIntent.FLAG_UPDATE_CURRENT)));
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
			NotificationChannel notificationChannel = new NotificationChannel("70",
					"uploadVideo", NotificationManager.IMPORTANCE_LOW);
			notificationChannel.enableLights(true);
			notificationChannel.setLightColor(Color.RED);
			notificationChannel.setShowBadge(true);
			notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
			builder.setChannelId("70");
			if (notificationManager != null) {
				notificationManager.createNotificationChannel(notificationChannel);
			}
		}
		Notification notification = builder.build();
		return notification;
	}

	public Notification finishNotification(int id,String title, String content) {
		Notification.Builder builder = new Notification.Builder(getApplicationContext());
		builder.setContentTitle(title)
				.setTicker(title)
				.setWhen(java.lang.System.currentTimeMillis())
				.setSmallIcon(R.mipmap.ic_launcher)
				.setAutoCancel(false)
				.setOngoing(false)
				.setPriority(Notification.PRIORITY_LOW)
				.setContentText(content);
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
			NotificationChannel notificationChannel = new NotificationChannel("70",
					"uploadVideo", NotificationManager.IMPORTANCE_LOW);
			builder.setChannelId("70");
			notificationChannel.enableLights(true);
			notificationChannel.setLightColor(Color.RED);
			notificationChannel.setShowBadge(true);
			notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
			if (notificationManager != null) {
				notificationManager.createNotificationChannel(notificationChannel);
			}
		}
		Notification notification = builder.build();
		notificationManager.notify(70, notification);
		return notification;
	}


	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent!=null&& Objects.equals(intent.getAction(), ACTION_CANCEL)){
			if (binder.msgCall!=null){
				if (!binder.msgCall.isCanceled()){
					binder.msgCall.cancel();
				}
			}
		}
		return super.onStartCommand(intent, flags, startId);
	}


	@Override
	public void onCreate() {
		super.onCreate();
		notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	}
}
