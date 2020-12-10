package com.demo.btvideo.ui.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.appbar.AppBarLayout;

public class FloatingScrollBehavior extends AppBarLayout.ScrollingViewBehavior {


	private boolean initialized;


//	@Override
//	protected boolean shouldHeaderOverlapScrollingChild() {
//		return true;
//	}

	public FloatingScrollBehavior(Context context, AttributeSet attrs) {
		super(context, attrs);
	}


	@Override
	public boolean onDependentViewChanged(@NonNull CoordinatorLayout parent, @NonNull View child, @NonNull View dependency) {
		boolean onDependentViewChange=FloatingScrollBehavior.super.onDependentViewChanged(parent,child,dependency);
		if (!this.initialized&&(dependency instanceof AppBarLayout)){
			this.initialized=true;
			setAppbarLayoutTranspatent((AppBarLayout)dependency);
		}
		return onDependentViewChange;
	}

	private void setAppbarLayoutTranspatent(AppBarLayout dependency) {
		dependency.setBackgroundColor(Color.TRANSPARENT);
		dependency.setTargetElevation(0.0f);
	}
}
