package com.demo.btvideo.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;

import com.demo.btvideo.R;

public class BasePreferenceItem extends FrameLayout {


	View itemView;
	String title;
	@DrawableRes
	int resId;
	TextView textTitle, textSummary;
	ImageView imgIcon;

	public BasePreferenceItem(Context context) {
		super(context);
		init(context);
	}

	public BasePreferenceItem(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ItemSetting);
		title=typedArray.getString(R.styleable.ItemSetting_setting_title);
		resId=typedArray.getResourceId(R.styleable.ItemSetting_setting_icon, R.drawable.ic_mine);
		init(context);
		typedArray.recycle();
	}
	private void init(Context context) {
		itemView = LayoutInflater.from(context).inflate(R.layout.item_preference, this,true);
		imgIcon = itemView.findViewById(R.id.preference_icon);
		textTitle = itemView.findViewById(R.id.preference_title);
		textTitle.setText(title);
		imgIcon.setImageResource(resId);
	}

	public void setTitle(String title) {
		this.title = title;
		textTitle.setText(title);
	}



	public void setIcon(int resId) {
		this.resId = resId;
		imgIcon.setImageResource(resId);
	}
	public String getTitle(){
		return title;
	}

}
