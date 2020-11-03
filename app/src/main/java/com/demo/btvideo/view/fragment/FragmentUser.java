package com.demo.btvideo.view.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.room.Room;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.demo.btvideo.AppController;
import com.demo.btvideo.R;
import com.demo.btvideo.dao.AppDatabase;
import com.demo.btvideo.model.User;
import com.demo.btvideo.model.Msg;
import com.demo.btvideo.net.NetInterface;
import com.demo.btvideo.utils.NetWorkUtils;
import com.demo.btvideo.view.activity.LoginActivity;
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
import java.io.InputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentUser extends Fragment {

	private static FragmentUser fragmentUser;
	View mainView;
	@BindView(R.id.img_userHeadPic)
	ImageView imgHead;
	@BindView(R.id.text_username)
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
						textUserId.setText(result.getUsername());
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
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("*/*");  // 选择文件类型
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		startActivityForResult( Intent.createChooser(intent, "Select a File to Upload"), 200);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 200) {
			if (data != null) {
				ProgressDialog progressDialog = new ProgressDialog(getContext());
				progressDialog.setTitle("上传中");
				progressDialog.show();
				NetInterface netInterface = NetWorkUtils.getRetrofit().create(NetInterface.class);
				MultipartBody.Builder builder = new MultipartBody.Builder()
						.setType(MultipartBody.FORM);
				RequestBody body = null;
				try {
					InputStream in=getContext().getContentResolver().openInputStream(data.getData());
					File file=getPictureFile(getContext(),in);
					body = RequestBody.create(file, MediaType.parse("multipart/form-data"));
					builder.addFormDataPart("headImage", file.getName(), body);
					MultipartBody.Part requestBody=builder.build().parts().get(0);
					netInterface.uploadHeadImage(requestBody).enqueue(new Callback<Msg<String>>() {
						@Override
						public void onResponse(Call<Msg<String>> call, Response<Msg<String>> response) {
							new Handler(Looper.getMainLooper()).post(()->{
								Msg stringMsg=response.body();
								if (response.isSuccessful()&&stringMsg!=null){
									if (stringMsg.getCode()==200){
										Toast.makeText(getContext(), "上传成功", Toast.LENGTH_SHORT).show();
										Glide.with(getContext())
												.load(file)
												.skipMemoryCache(true)
												.diskCacheStrategy(DiskCacheStrategy.NONE)
												.error(R.mipmap.imglogin)
												.into(imgHead);
										pool.execute(()->{
											User user=database.userDao().getUser();
											File saveImgFile=saveCover(user,file);
											user.setHeadImage(saveImgFile.getAbsolutePath());
											database.userDao().insertAll(user);
										});
									}else{
										Toast.makeText(getContext(), stringMsg.getMessage(), Toast.LENGTH_SHORT).show();
									}
								}
								progressDialog.dismiss();
							});
						}

						@Override
						public void onFailure(Call<Msg<String>> call, Throwable t) {
							new Handler(Looper.getMainLooper()).post(progressDialog::dismiss);
						}
					});
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
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
			BufferedInputStream bis=new BufferedInputStream(in);
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
