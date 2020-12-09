package com.demo.btvideo.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;

import com.demo.btvideo.R;

public class BaseEditItem extends FrameLayout {


	View itemView;
	String title, content;
	@DrawableRes
	int resId;
	TextView textTitle, textSummary;
	EditText editText;
	boolean editable;

	public BaseEditItem(Context context) {
		super(context);
		init(context);
	}

	public BaseEditItem(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ItemEdit);
		title=typedArray.getString(R.styleable.ItemEdit_edit_title);
		content =typedArray.getString(R.styleable.ItemEdit_edit_content);
		editable =typedArray.getBoolean(R.styleable.ItemEdit_edit_editable,true);
		init(context);
		typedArray.recycle();
	}
	private void init(Context context) {
		itemView = LayoutInflater.from(context).inflate(R.layout.layout_item_edit, this,true);
		editText = itemView.findViewById(R.id.layout_editText);
		textTitle = itemView.findViewById(R.id.layout_edit_title);
		textTitle.setText(title);
		editText.setText(content);
		editText.setEnabled(editable);
	}

	public void setTitle(String title) {
		this.title = title;
		textTitle.setText(title);
	}
	public String getTitle(){
		return title;
	}

	public void setContent(String content) {
		this.content = content;
		editText.setText(content);
	}

	public String getContent() {
		return editText.getText().toString();
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
		editText.setEnabled(false);
	}

	public void setInputType(int type){
		editText.setInputType(type);
	}

	public void setDefaultVal(String title, String content){
		this.title=title;
		this.content =content;
	}
}
