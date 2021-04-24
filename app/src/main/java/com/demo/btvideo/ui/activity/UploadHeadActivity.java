package com.demo.btvideo.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
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
import com.bumptech.glide.signature.ObjectKey;
import com.demo.btvideo.R;
import com.demo.btvideo.dao.AppDatabase;
import com.demo.btvideo.model.Msg;
import com.demo.btvideo.model.User;
import com.demo.btvideo.net.NetInterface;
import com.demo.btvideo.net.ServerURL;
import com.demo.btvideo.utils.NetWorkUtils;
import com.demo.btvideo.utils.Tool;
import com.demo.btvideo.utils.UploadRequestBody;
import com.demo.btvideo.viewmodel.LoginViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Callable;
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



//头像界面
public class UploadHeadActivity extends BaseActivity {

	@BindView(R.id.img_upload_cover)
	ImageView uploadImage;

	ExecutorService pool = Executors.newCachedThreadPool();
	Handler handler = new Handler(Looper.getMainLooper());
	AppDatabase database;
	private BottomSheetDialog bottomSheetDialog;
	private BottomSheetBehavior<View> mDialogBehavior;
	SharedPreferences preferences;
//	ListeningExecutorService listenableFuture= MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());
	ExecutorService executorService=Executors.newCachedThreadPool();

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_upload_head);
		ButterKnife.bind(this);
		preferences= PreferenceManager.getDefaultSharedPreferences(this);
		database = Room.databaseBuilder(this, AppDatabase.class, "user").build();
		pool.execute(() -> {
			User user = database.userDao().getUser(preferences.getString("userNow","-1"));
			handler.post(() -> {
				if (user!=null&&user.getHeadImage()!=null){
					Glide.with(getApplicationContext())
							.load(ServerURL.MAIN_URL+user.getHeadImage())
							.signature(new ObjectKey(System.currentTimeMillis()))
							.into(uploadImage);
				}
			});


		});

	}


	private void showSheetDialog1() {
		View view = View.inflate(UploadHeadActivity.this, R.layout.bottom_sheet_layout, null);
		view.setOnClickListener(v -> {
//			Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//			intent.setType("*/*");  // 选择文件类型
//			intent.addCategory(Intent.CATEGORY_OPENABLE);
//			startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"), 200);
			Intent intent = new Intent(Intent.ACTION_CHOOSER);
//创建相机Intent
			Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			captureIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION |Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//将相机Intent以数组形式放入Intent.EXTRA_INITIAL_INTENTS
			intent.putExtra(Intent.EXTRA_INITIAL_INTENTS,new Intent(captureIntent));
//创建相册Intent
			Intent albumIntent = new Intent(Intent.ACTION_PICK, null);
			albumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
//将相册Intent放入Intent.EXTRA_INTENT
			intent.putExtra(Intent.EXTRA_INTENT, albumIntent);
			startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"), 200);

		});
		bottomSheetDialog = new BottomSheetDialog(UploadHeadActivity.this,R.style.BottomSheetDialog);
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
				int id=0;
				UploadRequestBody body = new UploadRequestBody(id,data.getData(), MediaType.parse("multipart/form-data"), this, new UploadRequestBody.UploadProgress() {

					@Override
					public void getPercent(int id, int percent) {

					}

					@Override
					public void getProgress(int id, long progress) {

					}
				});

				try {
					InputStream in = getContentResolver().openInputStream(data.getData());
					File file = Tool.getPictureFile(this, in);
//					body = RequestBody.create(file, MediaType.parse("multipart/form-data"));
					builder.addFormDataPart("headImage", "head.png", body);
					MultipartBody.Part requestBody = builder.build().parts().get(0);
					netInterface.uploadHeadImage(requestBody).enqueue(new Callback<Msg<String>>() {
						@Override
						public void onResponse(Call<Msg<String>> call, Response<Msg<String>> response) {
							Executors.newCachedThreadPool().submit(()->{
								Glide.get(getApplicationContext()).clearDiskCache();
							});
							new Handler(Looper.getMainLooper()).post(() -> {
								Msg stringMsg = response.body();
								if (response.isSuccessful() && stringMsg != null) {
									if (stringMsg.getCode() == 200) {
										Toast.makeText(UploadHeadActivity.this, "上传成功", Toast.LENGTH_SHORT).show();
										if (!isDestroyed()){
											Glide.get(getApplicationContext()).clearMemory();
//											Glide.with(UploadHeadActivity.this)
//													.load(file)
//													.transition(DrawableTransitionOptions.withCrossFade())
//													.error(R.mipmap.load404)
//													.into(uploadImage);
										}
										pool.execute(() -> {
											User user = database.userDao().getUser(preferences.getString("userNow","-1"));
//											File saveImgFile = Tool.saveCover(UploadHeadActivity.this,user, file);
//											user.setHeadImage(saveImgFile.getAbsolutePath());
											handler.post(()->{
												Glide.with(getApplicationContext())
														.load(ServerURL.MAIN_URL+user.getHeadImage())
														.signature(new ObjectKey(System.currentTimeMillis()))
														.into(uploadImage);
											});
											database.userDao().insertAll(user);
											LoginViewModel.getInstance().update().postValue(0);
										});
									} else {
										Toast.makeText(UploadHeadActivity.this, stringMsg.getMessage(), Toast.LENGTH_SHORT).show();
									}
								}
							});
						}

						@Override
						public void onFailure(Call<Msg<String>> call, Throwable t) {
							t.printStackTrace();
							new Handler(Looper.getMainLooper()).post(()->{
								bottomSheetDialog.cancel();
////								progressDialog.dismiss();
								Toast.makeText(UploadHeadActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
							});
						}
					});
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
	}


//
//	private void updateCover(File file){
//		executorService.submit(()->{
//			saveCover(file);
//		});
//	}


	@Override
	public void onBackPressed() {
		ActivityCompat.finishAfterTransition(this);
//		super.onBackPressed();
	}
}
