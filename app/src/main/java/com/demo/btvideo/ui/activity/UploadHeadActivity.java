package com.demo.btvideo.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.room.Room;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.demo.btvideo.R;
import com.demo.btvideo.dao.AppDatabase;
import com.demo.btvideo.model.Msg;
import com.demo.btvideo.model.User;
import com.demo.btvideo.net.NetInterface;
import com.demo.btvideo.utils.NetWorkUtils;
import com.demo.btvideo.utils.UploadRequestBody;
import com.demo.btvideo.viewmodel.LoginViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadHeadActivity extends AppCompatActivity {

	@BindView(R.id.img_upload_cover)
	ImageView uploadImage;

	ExecutorService pool = Executors.newCachedThreadPool();
	Handler handler = new Handler(Looper.getMainLooper());
	AppDatabase database;
	private BottomSheetDialog bottomSheetDialog;
	private BottomSheetBehavior<View> mDialogBehavior;




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
				getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
			}
		}
		setContentView(R.layout.layout_upload_head);
		ButterKnife.bind(this);
		database = Room.databaseBuilder(this, AppDatabase.class, "user").build();
		pool.execute(() -> {
			User user = database.userDao().getUser();
			handler.post(() -> {
				Glide.with(this)
						.load(user.getHeadImage())
						.skipMemoryCache(true)
						.diskCacheStrategy(DiskCacheStrategy.NONE)
						.into(uploadImage);
			});


		});

	}


	private void showSheetDialog1() {
		View view = View.inflate(UploadHeadActivity.this, R.layout.bottom_sheet_layout, null);
		view.setOnClickListener(v -> {
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType("*/*");  // 选择文件类型
			intent.addCategory(Intent.CATEGORY_OPENABLE);
			startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"), 200);
		});
		bottomSheetDialog = new BottomSheetDialog(UploadHeadActivity.this);
		bottomSheetDialog.setContentView(view);
		mDialogBehavior = BottomSheetBehavior.from((View) view.getParent());
//		mDialogBehavior.setPeekHeight(100);
		bottomSheetDialog.show();

	}

	@OnLongClick(R.id.img_upload_cover)
	public void replaceCover() {
		showSheetDialog1();
	}

	@OnClick(R.id.img_upload_cover)
	public void imgClick() {
		ActivityCompat.finishAfterTransition(this);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		bottomSheetDialog.cancel();
		if (requestCode == 200) {
			if (data != null) {
//				ProgressDialog progressDialog = new ProgressDialog(this);
//				progressDialog.setTitle("上传中");
//				progressDialog.show();
				Toast.makeText(UploadHeadActivity.this,"上传中",Toast.LENGTH_SHORT).show();
				NetInterface netInterface = NetWorkUtils.getRetrofit().create(NetInterface.class);

				MultipartBody.Builder builder = new MultipartBody.Builder()
						.setType(MultipartBody.FORM);
				UploadRequestBody body = new UploadRequestBody(data.getData(), MediaType.parse("multipart/form-data"), this, (progress) -> {
					Log.d("TAG", "onActivityResult: "+progress);
				});

				try {
					InputStream in = getContentResolver().openInputStream(data.getData());
					File file = getPictureFile(this, in);
//					body = RequestBody.create(file, MediaType.parse("multipart/form-data"));
					builder.addFormDataPart("headImage", "head.png", body);
					MultipartBody.Part requestBody = builder.build().parts().get(0);
					netInterface.uploadHeadImage(requestBody).enqueue(new Callback<Msg<String>>() {
						@Override
						public void onResponse(Call<Msg<String>> call, Response<Msg<String>> response) {
							new Handler(Looper.getMainLooper()).post(() -> {
								Msg stringMsg = response.body();
								if (response.isSuccessful() && stringMsg != null) {
									if (stringMsg.getCode() == 200) {
										Toast.makeText(UploadHeadActivity.this, "上传成功", Toast.LENGTH_SHORT).show();
										Glide.with(UploadHeadActivity.this)
												.load(file)
												.transition(DrawableTransitionOptions.withCrossFade())
												.skipMemoryCache(true)
												.diskCacheStrategy(DiskCacheStrategy.NONE)
												.error(R.mipmap.imglogin)
												.into(uploadImage);
										pool.execute(() -> {
											User user = database.userDao().getUser();
											File saveImgFile = saveCover(user, file);
											user.setHeadImage(saveImgFile.getAbsolutePath());
											database.userDao().insertAll(user);
											LoginViewModel.getInstance().update().postValue(0);
										});
									} else {
										Toast.makeText(UploadHeadActivity.this, stringMsg.getMessage(), Toast.LENGTH_SHORT).show();
									}
								}
//								progressDialog.dismiss();
//								bottomSheetDialog.cancel();
							});
						}

						@Override
						public void onFailure(Call<Msg<String>> call, Throwable t) {
//							new Handler(Looper.getMainLooper()).post(()->{
////								bottomSheetDialog.cancel();
////								progressDialog.dismiss();
//							});
							Toast.makeText(UploadHeadActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
						}
					});
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
	}


	private File saveCover(User user, File of) {
		String storePath = getFilesDir().getAbsolutePath();
		File appdir = new File(storePath);
		if (!appdir.exists()) {
			appdir.mkdir();
		}
		String userHead = user.getHeadImage();
		String fileName = user.getAccount() + userHead.substring(userHead.lastIndexOf("."), user.getHeadImage().length());
		File file = new File(appdir, fileName);
		try {
			FileOutputStream fos = new FileOutputStream(file);
			FileInputStream fis = new FileInputStream(of);
			BufferedInputStream bis = new BufferedInputStream(fis);
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


	public static File getPictureFile(Context context, InputStream in) {
		String storePath = context.getFilesDir().getAbsolutePath();
		File appDir = new File(storePath);
		if (!appDir.exists()) {
			appDir.mkdir();
		}
		String fileName = System.currentTimeMillis() + ".jpg";
		File file = new File(appDir, fileName);
		try {
			FileOutputStream fos = new FileOutputStream(file);
//			FileInputStream fis = new FileInputStream(fileDescriptor.getFileDescriptor());
			BufferedInputStream bis = new BufferedInputStream(in);
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


	@Override
	public void onBackPressed() {
		ActivityCompat.finishAfterTransition(this);
//		super.onBackPressed();
	}
}
