package com.demo.btvideo.view.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.demo.btvideo.MainActivity;
import com.demo.btvideo.R;
import com.demo.btvideo.User;
import com.demo.btvideo.model.Msg;
import com.demo.btvideo.utils.NetWorkUtils;
import com.demo.btvideo.utils.Propertys;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

	@BindView(R.id.username)
	EditText edituser;
	@BindView(R.id.password)
	EditText editpasswd;

	@BindView(R.id.remPasswd)
	CheckBox checkBox;

	private String username;
	private String password;
	SharedPreferences preferences;
	ProgressDialog progressDialog;
	Handler handler = new Handler(Looper.getMainLooper());

	ExecutorService executorService = Executors.newCachedThreadPool();


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
		setContentView(R.layout.activity_login);
		ButterKnife.bind(this);
		progressDialog = new ProgressDialog(this);
		progressDialog.setCancelable(true);
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		checkBox.setChecked(preferences.getBoolean("isrem", false));
		if (checkBox.isChecked()) {
			edituser.setText(preferences.getString("username", ""));
			editpasswd.setText(preferences.getString("passwd", ""));
			Log.d("password", "onCreate: " + password);
		}
		checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
			preferences.edit().putBoolean("isrem", isChecked).apply();
			if (!isChecked) {
				preferences.edit().putString("username", "").apply();
				preferences.edit().putString("passwd", "").apply();
			}
		});
	}

	@OnClick(R.id.btnUserLogin)
	public void login(View view) {
		username = edituser.getText().toString();
		password = editpasswd.getText().toString();
		if (username.equals("") || password.equals("")) {
			Snackbar.make(findViewById(android.R.id.content), "请输入完整内容！", Snackbar.LENGTH_SHORT).show();
			return;
		}
		view.setEnabled(false);
		progressDialog.setTitle("登陆中...");
		progressDialog.show();
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put(Propertys.USERNAME, username);
			jsonObject.put(Propertys.PASSWORD, password);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		NetWorkUtils.httpPost(ServerURL.LOGIN_URL, jsonObject.toString(), new Callback() {
			@Override
			public void onFailure(@NotNull Call call, @NotNull IOException e) {
				handler.post(() -> {
					Snackbar.make(findViewById(android.R.id.content), "连接失败，请检查网络连接。", Snackbar.LENGTH_SHORT).show();
					progressDialog.dismiss();
					view.setEnabled(true);
				});
			}

			@Override
			public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
				String resp = response.body().string();
				if (NetWorkUtils.isJson(resp)) {
					Msg msg = new Gson().fromJson(resp, Msg.class);
					if (msg.getCode() == 200) {
						if (checkBox.isChecked()) {
							preferences.edit().putString("username", username).apply();
							preferences.edit().putString("passwd", password).apply();
						}
						handler.post(() -> {
							progressDialog.dismiss();
							view.setEnabled(true);
							Toast.makeText(LoginActivity.this, msg.getMessage(), Toast.LENGTH_SHORT).show();
							finish();
						});
					} else {
                        handler.post(()->{
                            progressDialog.dismiss();
                            view.setEnabled(true);
                            Snackbar.make(findViewById(android.R.id.content), msg.getMessage(), Snackbar.LENGTH_LONG).show();
                        });
					}
				} else {
					handler.post(()->{
                        Snackbar.make(findViewById(android.R.id.content), "服务器响应错误,请稍后重试", Snackbar.LENGTH_LONG).show();
                        view.setEnabled(true);
                        progressDialog.dismiss();
                    });
//                    Toast.makeText(LoginActivity.this, "服务器响应错误,请稍后重试", Toast.LENGTH_SHORT).show();
				}
			}
		});

	}
}
