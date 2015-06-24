package com.quanle.healthmanager.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.quanle.healthmanager.R;

public class TabbarItem extends LinearLayout {
	private String text;
	private TextView tv;
	private ImageView iv;
	private int normalIcon;
	private int pressedIcon;
	private int normalTextColor;
	private int pressedTextColor;
	private boolean checked = false;
	private boolean iconVisibility = true;

	public TabbarItem(Context context) {
		super(context);

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.widget_tabbar_item, this);

		iv = (ImageView) findViewById(R.id.ivToolbarIcon);
		tv = (TextView) findViewById(R.id.tvToolbarText);
	}

	public TabbarItem(Context context, AttributeSet attrs) {
		super(context, attrs);

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.widget_tabbar_item, this);

		iv = (ImageView) findViewById(R.id.ivToolbarIcon);
		tv = (TextView) findViewById(R.id.tvToolbarText);
	}

	public String getText() {
		return text;
	}

	public void setText(String string) {
		text = string;
		tv.setText(string);
	}

	public void setPressedIcon(int homePressed) {
		pressedIcon = homePressed;
		if (checked)
			iv.setImageResource(pressedIcon);
	}

	public void setNormalIcon(int homeNormal) {
		normalIcon = homeNormal;
		if (!checked)
			iv.setImageResource(normalIcon);
	}

	public void setChecked(boolean b) {
		checked = b;
		if (b) {
			tv.setTextColor(pressedTextColor);
			iv.setImageResource(pressedIcon);
		} else {
			tv.setTextColor(normalTextColor);
			iv.setImageResource(normalIcon);
		}
	}

	public void setNormalTextColor(int color) {
		normalTextColor = color;
	}

	public void setPressedTextColor(int color) {
		pressedTextColor = color;
	}

	public void setIconVisibility(boolean b) {
		iconVisibility = b;
		iv.setVisibility(iconVisibility ? View.VISIBLE : View.GONE);

		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) tv
				.getLayoutParams();
		params.addRule(RelativeLayout.ALIGN_PARENT_TOP, b ? 0 : 1);
		tv.setLayoutParams(params);
	}

	public void setTextSize(int size) {
		tv.setTextSize(size);
	}
}
