package com.demo.btvideo.ui.activity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.demo.btvideo.R;
import com.demo.btvideo.ui.fragment.FragmentLogin;
import com.demo.btvideo.ui.fragment.FragmentRegister;
import com.demo.btvideo.viewmodel.DataViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;



//登录界面,用来装配两个登陆注册的碎片
public class LoginActivity extends BaseActivity {


	@BindView(R.id.loginViewPager)
	ViewPager viewPager;


	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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


		new ViewModelProvider(this,new ViewModelProvider.NewInstanceFactory()).get(DataViewModel.class).update().observe(this, new Observer<Integer>() {
			@Override
			public void onChanged(Integer integer) {
				viewPager.setCurrentItem(integer);
			}
		});
	}
}
