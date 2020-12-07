package com.demo.btvideo.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.demo.btvideo.AppController;
import com.demo.btvideo.R;
import com.demo.btvideo.statement.StateGuest;
import com.demo.btvideo.ui.view.SettingItem;
import com.demo.btvideo.viewmodel.LoginViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingActivity extends AppCompatActivity {


	@BindView(R.id.sett_test)
	SettingItem settingItem;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		ButterKnife.bind(this);
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

	}
}
