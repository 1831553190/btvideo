package com.demo.btvideo.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.bumptech.glide.signature.ObjectKey;
import com.demo.btvideo.AppController;
import com.demo.btvideo.R;
import com.demo.btvideo.dao.AppDatabase;
import com.demo.btvideo.model.Msg;
import com.demo.btvideo.model.User;
import com.demo.btvideo.net.NetInterface;
import com.demo.btvideo.net.ServerURL;
import com.demo.btvideo.statement.StateGuest;
import com.demo.btvideo.ui.view.BaseEditItem;
import com.demo.btvideo.ui.view.SettingItem;
import com.demo.btvideo.utils.BlurTransformation;
import com.demo.btvideo.utils.NetWorkUtils;
import com.demo.btvideo.viewmodel.LoginViewModel;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.io.File;
import java.util.HashMap;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


//编辑用户信息界面
public class EditInfoActivity extends BaseActivity {

	@BindView(R.id.account_username)
	BaseEditItem baseUsername;

	@BindView(R.id.account_name)
	BaseEditItem baseName;

	@BindView(R.id.account_phone)
	BaseEditItem basePhone;

	@BindView(R.id.account_mail)
	BaseEditItem baseMail;

	@BindView(R.id.account_sign)
	BaseEditItem baseSign;
	User userinfo;

	Handler handler = new Handler(Looper.getMainLooper());
	private AppDatabase appdatabase;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_editinfo);
		ButterKnife.bind(this);
		baseName.setEditable(false);
		baseUsername.setEditable(false);
		basePhone.setInputType(InputType.TYPE_CLASS_PHONE);
		baseMail.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
		ImageView backImg = findViewById(R.id.img_back);
		backImg.setOnClickListener(v -> {
			finish();
		});
		appdatabase = Room.databaseBuilder(this.getApplicationContext(), AppDatabase.class, "user")
				.fallbackToDestructiveMigration().build();
//		String account=PreferenceManager.getDefaultSharedPreferences(this).getString("userNow","");
		NetInterface netInterface = NetWorkUtils.getRetrofit().create(NetInterface.class);
		netInterface.getUserInfo().enqueue(new Callback<Msg<JsonElement>>() {
			@Override
			public void onResponse(Call<Msg<JsonElement>> call, Response<Msg<JsonElement>> response) {
				if (response.isSuccessful() && response.body().getCode() == 200) {
					JsonElement strUserInfo = response.body().getData();
					User user = new Gson().fromJson(strUserInfo, User.class);
					userinfo = user;
					updateView(user);
				}
			}

			@Override
			public void onFailure(Call<Msg<JsonElement>> call, Throwable t) {
				handler.post(() -> {
					Toast.makeText(EditInfoActivity.this, "连接失败", Toast.LENGTH_SHORT).show();
				});
			}
		});
	}

	private void updateView(User user) {
		handler.post(() -> {
			if (user.getAccount() != null) {
				baseUsername.setContent(user.getUsername());
			}
			if (user.getUsername() != null) {
				baseName.setContent(user.getAccount());
			}
			if (user.getMail() != null) {
				baseMail.setContent(user.getMail() );

			}
			if (user.getPhone() != null) {
				basePhone.setContent(user.getPhone() );

			}
			if (user.getSignature() != null) {
				baseSign.setContent(user.getSignature());
			}

		});

	}

	@OnClick(R.id.btn_SaveEdit)
	public void saveEdit() {
		if (userinfo != null) {
			HashMap<String, String> body = new HashMap<>();
			body.put("phone", basePhone.getContent());
			body.put("signature", baseSign.getContent());
			body.put("mail", baseMail.getContent());
			NetInterface netInterface = NetWorkUtils.getRetrofit().create(NetInterface.class);
			netInterface.modifyUserInfo(body).enqueue(new Callback<Msg<JsonElement>>() {
				@Override
				public void onResponse(Call<Msg<JsonElement>> call, Response<Msg<JsonElement>> response) {
					if (response.body() != null) {
						if (response.isSuccessful() && response.body().getCode() == 200) {
							handler.post(() -> {
								userinfo.setPhone(basePhone.getContent());
								userinfo.setSignature(baseSign.getContent());
								userinfo.setMail(baseMail.getContent());
//								User user = new Gson().fromJson(response.body().getData(), User.class);
								Executors
										.newCachedThreadPool()
										.execute(() -> {

											//提示其他界面更新用户信息
											LoginViewModel.getInstance().userLiveData().postValue(userinfo);
											appdatabase.userDao().insertAll(userinfo);
										});
							});
							Toast.makeText(EditInfoActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
							finish();
						} else {

							Toast.makeText(EditInfoActivity.this, "修改失败", Toast.LENGTH_SHORT).show();
						}
					} else {
						Toast.makeText(EditInfoActivity.this, "修改失败", Toast.LENGTH_SHORT).show();
					}


				}

				@Override
				public void onFailure(Call<Msg<JsonElement>> call, Throwable t) {
					handler.post(() -> {
						Toast.makeText(EditInfoActivity.this, "修改失败", Toast.LENGTH_SHORT).show();
					});
				}
			});
		} else {
			Toast.makeText(EditInfoActivity.this, "获取用户信息失败", Toast.LENGTH_SHORT).show();
		}
	}

}
