package com.demo.btvideo.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.demo.btvideo.R;

public class FragmentIndex extends Fragment {

	private static FragmentIndex fragmentIndex;

	public static FragmentIndex getInstance(){
		if (fragmentIndex==null){
			synchronized (FragmentIndex.class){
				if (fragmentIndex==null){
					fragmentIndex=new FragmentIndex();
				}
			}
		}
		return fragmentIndex;
	}

	View mainView;


	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		mainView=LayoutInflater.from(getContext()).inflate(R.layout.fragment_main,container,false);
		return mainView;
	}
}
