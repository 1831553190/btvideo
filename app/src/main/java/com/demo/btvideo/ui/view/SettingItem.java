package com.demo.btvideo.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.demo.btvideo.R;

public class SettingItem extends FrameLayout {

	View itemView;
	String title, summary;
	int resId;
	TextView textTitle, textSummary;
	ImageView imgIcon;

	public SettingItem(Context context) {
		super(context);
		init(context);
	}

	public SettingItem(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ItemSetting);
		title=typedArray.getString(R.styleable.ItemSetting_setting_title);
		summary=typedArray.getString(R.styleable.ItemSetting_setting_summary);
		resId=typedArray.getResourceId(R.styleable.ItemSetting_setting_icon, R.drawable.ic_mine);
		init(context);
		typedArray.recycle();
	}

	private void init(Context context) {
		itemView = LayoutInflater.from(context).inflate(R.layout.item_setting, this,true);
		imgIcon = itemView.findViewById(R.id.setting_icon);
		textTitle = itemView.findViewById(R.id.setting_title);
		textSummary = itemView.findViewById(R.id.setting_summary);
		textTitle.setText(title);
		if (summary==null||summary.isEmpty()){
			textSummary.setVisibility(GONE);
		}else {
			textSummary.setText(summary);
		}
		imgIcon.setImageResource(resId);
	}

	public void setTitle(String title) {
		this.title = title;
		textTitle.setText(title);
	}

	public void setSummary(String summary) {
		this.summary = summary;
		textSummary.setText(title);
	}

	public void setIcon(int resId) {
		this.resId = resId;
		imgIcon.setImageResource(resId);
	}
	public String getTitle(){
		return title;
	}

	public String getSummary(){
		return summary;
	}

}
