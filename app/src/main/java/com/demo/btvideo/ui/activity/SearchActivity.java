package com.demo.btvideo.ui.activity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.demo.btvideo.R;
import com.demo.btvideo.ui.fragment.FragmentVideoListByAccount;
import com.demo.btvideo.ui.fragment.FragmentVideoListByDes;
import com.demo.btvideo.ui.fragment.FragmentVideoListByLabel;
import com.demo.btvideo.ui.fragment.FragmentVideoListByTitle;
import com.demo.btvideo.viewmodel.DataViewModel;
import com.google.android.material.tabs.TabLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchActivity extends AppCompatActivity {


	@BindView(R.id.viewpager_search)
	ViewPager viewPager;

	@BindView(R.id.search_tab)
	TabLayout tabLayout;
	@BindView(R.id.search_text)
	EditText editText;

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
				getWindow().setStatusBarColor(Color.WHITE);
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
		setContentView(R.layout.activity_search);
		ButterKnife.bind(this);
		tabLayout.setupWithViewPager(viewPager, false);
		viewPager.setOffscreenPageLimit(4);
		viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
			@NonNull
			@Override
			public Fragment getItem(int position) {
				switch (position) {
					case 0:
						return FragmentVideoListByTitle.getInstance();
					case 1:
						return FragmentVideoListByDes.getInstance();
					case 2:
						return FragmentVideoListByAccount.getInstance();
					case 3:
						return FragmentVideoListByLabel.getInstance();
					default:
						return FragmentVideoListByTitle.getInstance();
				}
			}

			@Override
			public int getCount() {
				return 4;
			}

			@Nullable
			@Override
			public CharSequence getPageTitle(int position) {
				switch (position) {
					case 0:
						return "标题";
					case 1:
						return "内容";
					case 2:
						return "用户";
					case 3:
						return "标签";
					default:
						return "标题";
				}
			}
		});

		editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				switch (actionId) {
					case EditorInfo.IME_ACTION_SEARCH:
						new ViewModelProvider(SearchActivity.this).get(DataViewModel.class)
								.loadVideoData().postValue(new Pair<>(editText.getText().toString(),viewPager.getCurrentItem()));
						return true;
				}
				return false;
			}
		});


	}
}
