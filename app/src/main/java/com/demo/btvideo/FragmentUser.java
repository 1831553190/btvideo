package com.demo.btvideo;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import com.demo.btvideo.view.activity.LoginActivity;
import com.demo.btvideo.viewmodel.LoginViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FragmentUser extends Fragment {

	private static FragmentUser fragmentUser;
	View mainView;

	@BindView(R.id.guestView)
	ConstraintLayout guestView;


	public static FragmentUser getInstance(){
		if (fragmentUser ==null){
			synchronized (FragmentUser.class){
				if (fragmentUser ==null){
					fragmentUser =new FragmentUser();
				}
			}
		}
		return fragmentUser;
	}


	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		mainView=inflater.inflate(R.layout.fragment_user,container,false);
		ButterKnife.bind(this,mainView);
		LoginViewModel.getInstance().update().observe(this, new Observer<Integer>() {
			@Override
			public void onChanged(Integer integer) {
				boolean l=AppController.getInstance().isLogin();
				guestView.setVisibility(l?View.GONE:View.VISIBLE);
			}
		});
		return mainView;
	}

	@OnClick(R.id.img_login)
	public void toLogin(){
		startActivity(new Intent(getContext(), LoginActivity.class));
	}

}
