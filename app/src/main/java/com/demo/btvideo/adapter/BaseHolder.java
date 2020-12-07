package com.demo.btvideo.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.demo.btvideo.R;

public class BaseHolder extends RecyclerView.ViewHolder {
	TextView title,label;
	ImageView cover;
	public BaseHolder(@NonNull View itemView) {
		super(itemView);
		title=itemView.findViewById(R.id.baseitem_title);
		label=itemView.findViewById(R.id.baseitem_content);
		cover=itemView.findViewById(R.id.baseitem_img);
	}
}
