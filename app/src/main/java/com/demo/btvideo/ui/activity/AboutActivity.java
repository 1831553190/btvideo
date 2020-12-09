package com.demo.btvideo.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.demo.btvideo.BuildConfig;
import com.demo.btvideo.R;


//关于界面
public class AboutActivity extends AppCompatActivity {
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
		setContentView(R.layout.activity_about);
		TextView textVersion=findViewById(R.id.textVersion);
		textVersion.setText(BuildConfig.VERSION_NAME);
		TextView contact=findViewById(R.id.textConteant);
		ImageView backImg=findViewById(R.id.img_back);
		backImg.setOnClickListener(v -> {
			finish();
		});
		contact.setOnClickListener(v -> {
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
		});
	}
	public static boolean isSpecialApplInstalled(Context context, String packageName) {
		PackageManager packageManager = context.getPackageManager();
		try {
			packageManager.getPackageInfo(packageName, 0);
			return true;
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		return false;
	}
}
