package com.demo.btvideo.view.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.lifecycle.ViewModelStore;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.demo.btvideo.AppController;
import com.demo.btvideo.R;
import com.demo.btvideo.model.User;
import com.demo.btvideo.model.Msg;
import com.demo.btvideo.net.NetInterface;
import com.demo.btvideo.statement.StateGuest;
import com.demo.btvideo.statement.StateLogin;
import com.demo.btvideo.utils.NetWorkUtils;
import com.demo.btvideo.utils.ServerURL;
import com.demo.btvideo.view.fragment.FragmentIndex;
import com.demo.btvideo.view.fragment.FragmentUser;
import com.demo.btvideo.viewmodel.DataViewModel;
import com.demo.btvideo.viewmodel.LoadMoreViewModel;
import com.demo.btvideo.viewmodel.LoginViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Type;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

	@BindView(R.id.main_toolbar)
	Toolbar toolbar;
	@BindView(R.id.bottom_navi)
	BottomNavigationView bottomNavigationView;
	@BindView(R.id.main_viewpager)
	ViewPager viewPager;

	SharedPreferences preferences;
	String token="";

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ButterKnife.bind(this);
		setSupportActionBar(toolbar);
		preferences= PreferenceManager.getDefaultSharedPreferences(this);
		token=preferences.getString("token","");
		if (token.isEmpty()){
			AppController.getInstance().setLogin(new StateGuest());
		}else {
			AppController.getInstance().setLogin(new StateLogin());
			AppController.getInstance().updateAuth(token);
			checkIdentify();
		}
		LoginViewModel.getInstance().update().postValue(0);
//		裁剪图片
		toolbar.setNavigationIcon(R.drawable.ic_baseline_person_24);
		getSupportActionBar().setTitle("首页");
		viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
			@NonNull
			@Override
			public Fragment getItem(int position) {
				if (position == 0) {
					return FragmentIndex.getInstance();
				} else {
					return FragmentUser.getInstance();
				}
			}

			@Override
			public int getCount() {
				return 2;
			}
		});

		viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

			}

			@Override
			public void onPageSelected(int position) {
				bottomNavigationView.setSelectedItemId(bottomNavigationView.getMenu().getItem(position).getItemId());
			}

			@Override
			public void onPageScrollStateChanged(int state) {

			}
		});

		bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
			if (item.getItemId()==R.id.menu_index){
			 	viewPager.setCurrentItem(0);
						getSupportActionBar().setTitle("首页");
			}else if (item.getItemId()==R.id.menu_user_index){
				viewPager.setCurrentItem(1);
				getSupportActionBar().setTitle("个人中心");
			}
			return true;
		});
		FloatingActionButton floatingActionButton=new FloatingActionButton(this);
		floatingActionButton.setImageResource(R.drawable.ic_upload);
		BottomNavigationMenuView navi_menu_view=(BottomNavigationMenuView)bottomNavigationView.getChildAt(0);
		navi_menu_view.addView(floatingActionButton,1);
		floatingActionButton.setOnClickListener(v -> {

			Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
			          intent.setType("*/*");  // 选择文件类型
			             intent.addCategory(Intent.CATEGORY_OPENABLE);
			               startActivityForResult( Intent.createChooser(intent, "Select a File to Upload"), 200);

		});
	}

	public void checkIdentify() {
		NetWorkUtils.httpPost(ServerURL.USER_INFO, "", new okhttp3.Callback() {
			@Override
			public void onFailure(@NotNull okhttp3.Call call, @NotNull IOException e) {
				e.printStackTrace();
			}

			@Override
			public void onResponse(@NotNull okhttp3.Call call, @NotNull okhttp3.Response response) throws IOException {
				String resp=response.body().string();
				if (response.isSuccessful()&&NetWorkUtils.isJson(resp)){
					Msg userMsg=new Gson().fromJson(resp,Msg.class);
					if(userMsg.getCode()==401){
						AppController.getInstance().setLogin(new StateLogin());
						preferences.edit().putString("token","").apply();
						Looper.prepare();
						Toast.makeText(MainActivity.this, userMsg.getMessage(), Toast.LENGTH_SHORT).show();
						Looper.loop();
					}else if(userMsg.getCode()==200){
						Type type=new TypeToken<Msg<User>>(){}.getType();
						Msg<User> userMsg1=new Gson().fromJson(resp,type);
						ViewModelProviders.of(MainActivity.this).get(DataViewModel.class).userLiveData().postValue(userMsg1.getData());
					}
				}
			}
		});

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode==200){
			if (data!=null){
				System.out.println(data.getData().toString());
			}
		}
	}
}
