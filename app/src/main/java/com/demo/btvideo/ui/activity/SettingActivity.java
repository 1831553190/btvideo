package com.demo.btvideo.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.demo.btvideo.AppController;
import com.demo.btvideo.R;
import com.demo.btvideo.statement.StateGuest;
import com.demo.btvideo.ui.view.SettingItem;
import com.demo.btvideo.viewmodel.LoginViewModel;

import java.io.File;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;



//设置界面
public class SettingActivity extends BaseActivity {


	@BindView(R.id.sett_test)
	SettingItem settingItem;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		ButterKnife.bind(this);
		ImageView backImg=findViewById(R.id.img_back);
		backImg.setOnClickListener(v -> {
			finish();
		});
	}

	public void btnLogout(View view) {
		PreferenceManager.getDefaultSharedPreferences(this)
				.edit().remove("token").remove("userNow").apply();
		AppController.getInstance().setLogin(new StateGuest());
		AppController.getInstance().updateAuth("");
		startActivity(new Intent(this, LoginActivity.class));
		LoginViewModel.getInstance().update().postValue(0);
		finish();
	}

	@OnClick(R.id.sett_test)
	public void settTest(){
		Executors.newCachedThreadPool().submit(()->{
			Glide.get(getApplicationContext()).clearDiskCache();
		});
		Glide.get(getApplicationContext()).clearMemory();
		clearAllCache(this);
		Toast.makeText(this, "清除缓存成功", Toast.LENGTH_SHORT).show();
	}

	@OnClick(R.id.goto_github)
	public void gotoGithub(){
		Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/1831553190/btvideo"));
		startActivity(intent);
	}
	@OnClick(R.id.b_chat)
	public void bchat(){
		String url="mqqwpa://im/chat?chat_type=wpa&uin=1831553190";
		Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		try {
			startActivity(intent);
		} catch (Exception e) {
			url="http://wpa.qq.com/msgrd?v=3&uin=1831553190&site=qq&menu=yes";
			intent.setData(Uri.parse(url));
			startActivity(intent);
//				Toast.makeText(this, "未安装手机QQ或安装的版本不支持",Toast.LENGTH_SHORT).show();
		}
	}
	public static void clearAllCache(Context context) {
		deleteDir(context.getCacheDir());
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			deleteDir(context.getExternalCacheDir());
		}
	}

	private static boolean deleteDir(File dir) {
		if (dir != null && dir.isDirectory()) {
			String[] children = dir.list();
			int size = 0;
			if (children != null) {
				size = children.length;
				for (int i = 0; i < size; i++) {
					boolean success = deleteDir(new File(dir, children[i]));
					if (!success) {
						return false;
					}
				}
			}

		}
		if (dir == null) {
			return true;
		} else {

			return dir.delete();
		}
	}
}
