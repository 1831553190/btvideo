package com.demo.btvideo.statement;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.demo.btvideo.ui.activity.LoginActivity;
import com.demo.btvideo.ui.activity.UploadVideoAcivity;

public class StateGuest implements StateUser {

	@Override
	public void uploadVideo(Context context) {
		context.startActivity(new Intent(context, LoginActivity.class));
		Toast.makeText(context, "请先登录!", Toast.LENGTH_SHORT).show();
	}


}
