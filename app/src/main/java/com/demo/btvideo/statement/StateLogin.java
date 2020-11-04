package com.demo.btvideo.statement;

import android.content.Context;
import android.content.Intent;

import com.demo.btvideo.ui.activity.UploadVideoAcivity;

public class StateLogin implements StateUser {

	@Override
	public void uploadVideo(Context context) {
		context.startActivity(new Intent(context, UploadVideoAcivity.class));
	}


}
