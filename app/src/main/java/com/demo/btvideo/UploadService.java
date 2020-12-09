package com.demo.btvideo;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.demo.btvideo.model.Msg;
import com.demo.btvideo.net.NetInterface;
import com.demo.btvideo.net.ServerURL;
import com.demo.btvideo.ui.activity.UploadVideoAcivity;
import com.demo.btvideo.utils.NetWorkUtils;
import com.demo.btvideo.utils.UploadRequestBody;
import com.demo.btvideo.viewmodel.LoginViewModel;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.os.Environment.DIRECTORY_MOVIES;


//视频上传服务
public class UploadService extends Service {


	public Binder binder = new Binder();

	Handler handler = new Handler(Looper.getMainLooper());
	NotificationManager notificationManager;
	Notification notification;

	UpdateLoadPregress updateLoadPregress;
	final static String ACTION_CANCEL = "com.myservice.action.ACTION_CANCEL";
	Observable<File> ofile;


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
		private HashMap<Integer, Call<Msg<String>>> listCall = new HashMap<>();
		Call<Msg<String>> msgCall;

		public HashMap<Integer, Call<Msg<String>>> getListCall() {
			return listCall;
		}

		public void addCall(Call<Msg<String>> call) {
			listCall.put(listCall.size(), call);
		}

		public void cancelCall(int id) {
			Call<Msg<String>> call = listCall.get(id);
			if (call != null && !call.isCanceled()) {
				call.cancel();
			}
			listCall.remove(id);
		}

		public void uploadVideo(Uri img, Uri video, HashMap<String, String> data) {
			DecimalFormat decimalFormat = new DecimalFormat("##.##");
			notification = createNotification(70, "上传中");
			notificationManager.notify(70, notification);
			Random random
					= new Random();
			int id = random.nextInt();

			UploadRequestBody imageBody = new UploadRequestBody(id, img, MediaType.parse("multipart/form-data"), getApplicationContext(), new UploadRequestBody.UploadProgress() {
				@Override
				public void getPercent(int id, int percent) {
					notification = createNotification(percent, "上传图片中");
					notificationManager.notify(70, notification);
				}

				@Override
				public void getProgress(int id, long progress) {
					if (updateLoadPregress != null) {
						updateLoadPregress.getProgress(progress);
					}
				}
			});
			UploadRequestBody videoBody = new UploadRequestBody(id, video, MediaType.parse("multipart/form-data"), getApplicationContext(), new UploadRequestBody.UploadProgress() {
				@Override
				public void getPercent(int id, int percent) {
					notification = createNotification(percent, "上传视频中");
					notificationManager.notify(70, notification);
				}

				@Override
				public void getProgress(int id, long progress) {
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
								finishNotification(id, "上传成功", "视频上传成功");
								LoginViewModel.getInstance().refreshVideo().postValue(0);
							} else {
								Toast.makeText(getApplicationContext(), "上传失败", Toast.LENGTH_SHORT).show();
								finishNotification(id, "上传失败", "视频上传失败");
							}
						});
					}
				}

				@Override
				public void onFailure(Call<Msg<String>> call, Throwable t) {
					t.printStackTrace();
					handler.post(() -> {
						Toast.makeText(getApplicationContext(), "上传失败!" + t.getMessage(), Toast.LENGTH_SHORT).show();
						finishNotification(id, "上传失败", "视频上传失败");
						stopForeground(false);
					});
				}
			});
		}

		public void downloadVideo(String url, String fileName) {
			downFileFromServiceToPublicDir(url, getApplicationContext(), fileName);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
				startForegroundService(new Intent(getApplicationContext(), UploadService.class));
			}
			notification=createNotification(0,"缓存中");
			startForeground(70, notification);
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
		Intent intentCancel = new Intent(this, UploadService.class);
		intentCancel.setAction(ACTION_CANCEL);
		builder.addAction(new Notification.Action(R.mipmap.ic_launcher, "取消",
				PendingIntent.getService(getApplicationContext(), 400, intentCancel
						, PendingIntent.FLAG_UPDATE_CURRENT)));
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

	public Notification finishNotification(int id, String title, String content) {
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
		if (intent != null && Objects.equals(intent.getAction(), ACTION_CANCEL)) {
			if (binder.msgCall != null) {
				if (!binder.msgCall.isCanceled()) {
					binder.msgCall.cancel();
				}
			}
			if (ofile!=null){

			}
		}
		return super.onStartCommand(intent, flags, startId);
	}


	@Override
	public void onCreate() {
		super.onCreate();
		notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	}


	public void downFileFromServiceToPublicDir(String downPathUrl, Context context, String fileName) {
		handler.post(() -> {
			Toast.makeText(context, "缓存中", Toast.LENGTH_SHORT).show();
		});
		if (Build.VERSION.SDK_INT >= 29) {//android 10
			downUnKnowFileFromService(downPathUrl, context, fileName);//返回的是uri
		} else {
			downUnKnowFileFromService(downPathUrl, fileName);
		}
	}

	/**
	 * @date: 2019/8/2 0002
	 * @author: gaoxiaoxiong
	 * @description:获取外部存储目录下的 fileName的文件夹路径
	 **/
	public String getPublickDiskFileDir(Context context, String fileName) {
		String cachePath = null;
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
				|| !Environment.isExternalStorageRemovable()) {//此目录下的是外部存储下的私有的fileName目录
			cachePath = context.getExternalFilesDir(fileName).getAbsolutePath();  //mnt/sdcard/Android/data/com.my.app/files/fileName
		} else {
			cachePath = context.getFilesDir().getPath() + "/" + fileName;        //data/data/com.my.app/files
		}
		File file = new File(cachePath);
		if (!file.exists()) {
			file.mkdirs();
		}
		return file.getAbsolutePath();  //mnt/sdcard/Android/data/com.my.app/files/fileName
	}


	private void downUnKnowFileFromService(final String downPathUrl, final Context context, String fileName) {
		Observable<Uri> oUri=Observable.just(downPathUrl).subscribeOn(Schedulers.io()).map(s -> {
			Uri uri = null;
			try {
				URL url = new URL(downPathUrl);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setConnectTimeout(30 * 1000);
				InputStream is = conn.getInputStream();
//				String fileName = videoInfo.getTitle() + ".mp4";
				ContentValues contentValues = new ContentValues();
				contentValues.put(MediaStore.Downloads.DISPLAY_NAME, fileName);
				contentValues.put(MediaStore.Downloads.MIME_TYPE, "video/mp4");
				contentValues.put(MediaStore.Downloads.DATE_TAKEN, System.currentTimeMillis());
				uri = context.getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues);
				BufferedInputStream inputStream = new BufferedInputStream(is);
				OutputStream os = context.getContentResolver().openOutputStream(uri);
				if (os != null) {
					byte[] buffer = new byte[1024];
					int len;
					int total = 0;
					int contentLeng = conn.getContentLength();
					while ((len = inputStream.read(buffer)) != -1) {
						os.write(buffer, 0, len);
						total += len;
						notificationManager.notify(70, createNotification((int) (total * 100 / contentLeng),"下载中 "+fileName));
					}
				}
				os.flush();
				inputStream.close();
				is.close();
				os.close();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return uri;
		});
		oUri.subscribe(new io.reactivex.Observer<Uri>() {
			@Override
			public void onSubscribe(Disposable d) {

			}

			@Override
			public void onNext(Uri uri) {

			}

			@Override
			public void onError(Throwable e) {
				handler.post(() -> {
					Toast.makeText(context, "缓存失败", Toast.LENGTH_SHORT).show();
					finishNotification(70,"缓存失败","视频缓存失败");
				});
				e.printStackTrace();
			}

			@Override
			public void onComplete() {
				handler.post(() -> {
					Toast.makeText(context, "缓存成功", Toast.LENGTH_SHORT).show();
					finishNotification(70,"缓存成功","视频下载成功");

				});
			}
		});

	}

	/**
	 * @date :2020/3/17 0017
	 * @author : gaoxiaoxiong
	 * @description:下载文件到DIRECTORY_DOWNLOADS，适用于android<=9
	 **/
	private void downUnKnowFileFromService(final String downPathUrl, String fileName) {
		ofile = Observable.just(downPathUrl).subscribeOn(Schedulers.newThread()).map(s -> {
			File file = null;
			URL url = new URL(downPathUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(30 * 1000);
			InputStream is = conn.getInputStream();
//				String fileName = videoInfo.getTitle();
			file = new File(getPublickDiskFileDirAndroid9(DIRECTORY_MOVIES), fileName);
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}

			if (!file.exists()) {
				file.createNewFile();
			}
			FileOutputStream fos = new FileOutputStream(file);
			BufferedInputStream bis = new BufferedInputStream(is);
			byte[] buffer = new byte[1024];
			int len;
			long total = 0;
			long contentLeng = conn.getContentLength();
			while ((len = bis.read(buffer)) != -1) {
				fos.write(buffer, 0, len);
				total += len;
				notificationManager.notify(70, createNotification((int) (total * 100 / contentLeng),"下载中 "+fileName));
			}
			return file;
		});
		ofile.subscribe(new io.reactivex.Observer<File>() {
			@Override
			public void onSubscribe(Disposable d) {

			}

			@Override
			public void onNext(File file) {

			}

			@Override
			public void onError(Throwable e) {
				handler.post(() -> {
					Toast.makeText(getApplicationContext(), "缓存失败", Toast.LENGTH_SHORT).show();
					finishNotification(70,"缓存失败","视频缓存失败");
				});
				e.printStackTrace();
			}

			@Override
			public void onComplete() {
				handler.post(() -> {
					Toast.makeText(getApplicationContext(), "缓存成功", Toast.LENGTH_SHORT).show();
					finishNotification(70,"缓存成功","视频下载成功");
				});
			}
		});
	}

	/**
	 * @date :2020/3/17 0017
	 * @author : gaoxiaoxiong
	 * @description:获取公共目录，注意，只适合android9.0以下的
	 **/
	public String getPublickDiskFileDirAndroid9(String fileDir) {
		String filePath = null;
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
				|| !Environment.isExternalStorageRemovable()) {
			filePath = Environment.getExternalStoragePublicDirectory(fileDir).getPath();
		}
		File file = new File(filePath);
		if (!file.exists()) {
			file.mkdirs();
		}
		return file.getAbsolutePath();
	}
}
