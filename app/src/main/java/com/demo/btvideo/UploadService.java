package com.demo.btvideo;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
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
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadService extends Service {


	public Binder binder=new Binder();

	Handler handler=new Handler(Looper.getMainLooper());
	NotificationManager notificationManager;
	Notification notification;

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	public class Binder extends android.os.Binder{

		public void uploadVideo(Uri img,Uri video, HashMap<String,String> data){
			DecimalFormat decimalFormat=new DecimalFormat("##.##");
			notification = createNotification(70,"上传中");
			notificationManager.notify(70,notification);

			UploadRequestBody imageBody = new UploadRequestBody(img, MediaType.parse("multipart/form-data"), getApplicationContext(), (progress) -> {
				notification=createNotification((int) (progress*100),"上传图片中");
				notificationManager.notify(70,notification);
			});
			UploadRequestBody videoBody = new UploadRequestBody(video, MediaType.parse("multipart/form-data"), getApplicationContext(), (progress) -> {
				Log.d("TAG", "uploadVideo: "+decimalFormat.format(progress*100));
				notification=createNotification((int) (progress*100),"上传视频中");
				notificationManager.notify(70,notification);

			});
			MultipartBody.Builder builder = new MultipartBody.Builder()
					.setType(MultipartBody.FORM);

			builder.addFormDataPart("imageFile", "head.png", imageBody);
			builder.addFormDataPart("videoFile", "video.mp4", videoBody);
			List<MultipartBody.Part> requestBody = builder.build().parts();


			NetInterface netInterface=NetWorkUtils.getRetrofit().create(NetInterface.class);
			Call<Msg<String>> msgCall= netInterface.uploadVideo(requestBody, data);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
				startForegroundService(new Intent(getApplicationContext(),UploadService.class));
			}
			startForeground(70,notification);
			msgCall.enqueue(new Callback<Msg<String>>() {
				@Override
				public void onResponse(Call<Msg<String>> call, Response<Msg<String>> response) {
					if (response.isSuccessful() && response.body() != null) {
						Msg<String> msg = response.body();
						handler.post(() -> {
							stopForeground(true);
							if (msg.getCode() == 200) {
								Toast.makeText(getApplicationContext(), "上传成功!", Toast.LENGTH_SHORT).show();
								finishNotification("上传成功","视频上传成功");
							} else {
								Toast.makeText(getApplicationContext(), "上传失败", Toast.LENGTH_SHORT).show();
								finishNotification("上传失败","视频上传失败");
							}
						});
					}
				}

				@Override
				public void onFailure(Call<Msg<String>> call, Throwable t) {
					t.printStackTrace();
					handler.post(()->{
						Toast.makeText(getApplicationContext(), "上传失败!" + t.getMessage(), Toast.LENGTH_SHORT).show();
						finishNotification("上传失败","视频上传失败");
						stopForeground(false);
					});
				}
			});
		}

	}


	public Notification createNotification(int progress,String title){
		Notification.Builder builder=new Notification.Builder(getApplicationContext());
		builder.setContentTitle(title)
				.setTicker(title)
				.setWhen(java.lang.System.currentTimeMillis())
				.setSmallIcon(R.mipmap.ic_launcher)
				.setAutoCancel(false)
				.setOngoing(true)
				.setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher))
				.setPriority(Notification.PRIORITY_LOW)
				.setProgress(100,progress,false);

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
		Notification notification=builder.build();
		return notification;
	}

		public Notification finishNotification(String title,String content){
		Notification.Builder builder=new Notification.Builder(getApplicationContext());
		builder.setContentTitle(title)
				.setTicker(title)
				.setWhen(java.lang.System.currentTimeMillis())
				.setSmallIcon(R.mipmap.ic_launcher)
				.setAutoCancel(false)
				.setOngoing(true)
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
		Notification notification=builder.build();
		notificationManager.notify(70,notification);
		stopForeground(false);
		return notification;
	}


	@Override
	public void onCreate() {
		super.onCreate();
		notificationManager= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	}

}
