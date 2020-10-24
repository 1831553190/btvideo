package com.demo.btvideo.view.fragment;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.demo.btvideo.R;
import com.demo.btvideo.model.Msg;
import com.demo.btvideo.utils.NetWorkUtils;
import com.demo.btvideo.utils.Propertys;
import com.demo.btvideo.view.activity.LoginActivity;
import com.demo.btvideo.view.activity.ServerURL;
import com.demo.btvideo.viewmodel.DataViewModel;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class FragmentLogin extends Fragment {

	View mainView;
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


	private static class Holder {
		private static FragmentLogin instance = new FragmentLogin();
	}

	public static FragmentLogin getInstance() {
		return FragmentLogin.Holder.instance;
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		mainView=inflater.inflate(R.layout.fragment_login,container,false);
		ButterKnife.bind(this,mainView);
		progressDialog = new ProgressDialog(getContext());
		progressDialog.setCancelable(true);
		preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
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

		return mainView;
	}

	@OnClick(R.id.btnUserLogin)
	public void login(View view) {
		username = edituser.getText().toString();
		password = editpasswd.getText().toString();
		if (username.equals("") || password.equals("")) {
			Snackbar.make(view.findViewById(android.R.id.content), "请输入完整内容！", Snackbar.LENGTH_SHORT).show();
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
					Snackbar.make(view.findViewById(android.R.id.content), "连接失败，请检查网络连接。", Snackbar.LENGTH_SHORT).show();
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
							Toast.makeText(getContext(), msg.getMessage(), Toast.LENGTH_SHORT).show();
							getActivity().finish();
						});
					} else {
						handler.post(()->{
							progressDialog.dismiss();
							view.setEnabled(true);
							Toast.makeText(getContext(), msg.getMessage(), Toast.LENGTH_SHORT).show();

//							Snackbar.make(view.findViewById(android.R.id.content), msg.getMessage(), Snackbar.LENGTH_LONG).show();
						});
					}
				} else {
					handler.post(()->{

						Toast.makeText(getContext(), "服务器响应错误,请稍后重试", Toast.LENGTH_SHORT).show();

//						Snackbar.make(view.findViewById(android.R.id.content), "服务器响应错误,请稍后重试", Snackbar.LENGTH_LONG).show();
						view.setEnabled(true);
						progressDialog.dismiss();
					});
				}
			}
		});

	}

	@OnClick(R.id.btnUserRegister)
	public void toRegister(){
		ViewModelProviders.of(getActivity()).get(DataViewModel.class).update().setValue(0);
	}
}