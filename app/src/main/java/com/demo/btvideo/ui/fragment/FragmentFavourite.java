package com.demo.btvideo.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.demo.btvideo.R;

import butterknife.ButterKnife;

public class FragmentFavourite extends Fragment {
	View mainView;

	private static class Holder {
		private static FragmentFavourite instance = new FragmentFavourite();
	}

	public static FragmentFavourite getInstance() {
		return FragmentFavourite.Holder.instance;
	}



	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		mainView=inflater.inflate(R.layout.layout_list_usercenter,container,false);
		ButterKnife.bind(this,mainView);
		return mainView;
	}
}
