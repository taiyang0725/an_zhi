package com.quanle.healthmanager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.quanle.healthmanager.utils.SharedPreferencesHelper;
import com.quanle.healthmanager.widget.Slider;
import com.quanle.healthmanager.widget.Slider.OnViewChangeListener;

public class SlideActivity extends Activity {
	private Context context = this;

	private Slider slider;
	private LinearLayout layoutDot;
	private TextView skipTextView;
	private int[] sliderInt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_slide);

		sliderInt = new int[] { R.drawable.slide02, R.drawable.slide03,
				R.drawable.slide04, R.drawable.slide01 };
		slider = (Slider) findViewById(R.id.slider);
		slider.SetImages(sliderInt);
		layoutDot = (LinearLayout) findViewById(R.id.layoutDot);
		skipTextView = (TextView) findViewById(R.id.skipTextView);

		slider.SetOnViewChangeListener(new OnViewChangeListener() {
			@Override
			public void OnViewChange(int p) {
				page(p);
			}
		});

		skipTextView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(context, SelectAgeActivity.class);
				startActivity(intent);
				finish();
				overridePendingTransition(R.anim.in_from_right, 0);
			}
		});

		page(0);
	}

	private void page(int p) {
		for (int i = 0; i < layoutDot.getChildCount(); i++) {
			ImageView imageView = (ImageView) layoutDot.getChildAt(i);
			imageView.setImageResource(R.drawable.guide_dot_black);
		}

		ImageView imageView = (ImageView) layoutDot.getChildAt(p);
		imageView.setImageResource(R.drawable.guide_dot_white);

		if (p == layoutDot.getChildCount() - 1)
			skipTextView.setText(R.string.slide_enter);
	}
}
