package com.demo.btvideo.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.demo.btvideo.R;
import com.demo.btvideo.view.activity.LoginActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FragmentUser extends Fragment {

	private static FragmentUser fragmentUser;
	View mainView;


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
		return mainView;
	}

	@OnClick(R.id.btnLogin)
	public void toLogin(){
		startActivity(new Intent(getContext(), LoginActivity.class));
	}
}
