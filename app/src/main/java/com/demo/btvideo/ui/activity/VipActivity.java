package com.demo.btvideo.ui.activity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.demo.btvideo.R;



//充值界面
public class VipActivity extends BaseActivity {
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_vip);
		ImageView backImg=findViewById(R.id.img_back);
		backImg.setOnClickListener(v -> {
			finish();
		});
	}
}
