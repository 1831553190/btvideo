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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.demo.btvideo.R;
import com.demo.btvideo.User;
import com.demo.btvideo.model.Msg;
import com.demo.btvideo.utils.NetWorkUtils;
import com.demo.btvideo.utils.Propertys;
import com.demo.btvideo.view.fragment.FragmentLogin;
import com.demo.btvideo.view.fragment.FragmentRegister;
import com.demo.btvideo.viewmodel.DataViewModel;
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


	@BindView(R.id.loginViewPager)
	ViewPager viewPager;


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
		viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
			@NonNull
			@Override
			public Fragment getItem(int position) {
				if (position==0){
					return FragmentRegister.getInstance();
				}else {
					return FragmentLogin.getInstance();
				}
			}

			@Override
			public int getCount() {
				return 2;
			}
		});
		viewPager.setCurrentItem(1);
		ViewModelProviders.of(this).get(DataViewModel.class).update().observe(this, new Observer<Integer>() {
			@Override
			public void onChanged(Integer integer) {
				viewPager.setCurrentItem(integer);
			}
		});



	}
}
