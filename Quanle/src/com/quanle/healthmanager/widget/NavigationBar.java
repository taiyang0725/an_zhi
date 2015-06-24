package com.quanle.healthmanager.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.quanle.healthmanager.R;

public class NavigationBar extends LinearLayout {
	private String titleString;
	private TextView titleTextView;
	private ImageView backImageView;
	private ClickListener clickListener = new ClickListener();
	private ClickListener clickButtonListener = new ClickListener();
	private OnClickBackListener mClickBackListener;
	private OnClickButtonListener mClickButtonListener;
	private Button btnButton;

	public NavigationBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.widget_navbar, this);

		titleTextView = (TextView) findViewById(R.id.tvTitle);
		backImageView = (ImageView) findViewById(R.id.ivBack);
		btnButton = (Button) findViewById(R.id.btn);
		backImageView.setFocusable(true);
		backImageView.setClickable(true);
	}

	public void setImageViewDisplay(Boolean b) {

		backImageView.setVisibility(b ? View.VISIBLE : View.GONE);

	}

	public void setText(int string) {
		titleString = getResources().getString(string);
		this.setText(titleString);
	}

	public void setText(String string) {
		titleString = string;
		titleTextView.setText(titleString);
	}

	public void setBackDisplayable(boolean b) {
		backImageView.setVisibility(b ? View.VISIBLE : View.GONE);
	}

	public void setOnClickBackListener(OnClickBackListener listener) {
		mClickBackListener = listener;
		backImageView.setOnClickListener(clickListener);
	}

	public void setOnClickButtonListener(OnClickButtonListener listener) {
		mClickButtonListener = listener;
		btnButton.setOnClickListener(clickButtonListener);
	}

	class ClickListener implements OnClickListener {
		public void onClick(View v) {
			if (v == backImageView)
				mClickBackListener.onClick();
			if (v == btnButton)
				mClickButtonListener.onClick();
		}
	}

	public Button getBtnButton() {
		return btnButton;
	}

	public void setBtnButton(Button btnButton) {
		this.btnButton = btnButton;
	}

	public void setButtonDisplay(boolean b) {
		btnButton.setVisibility(b ? View.VISIBLE : View.GONE);
	}

	public interface OnClickBackListener {
		public void onClick();
	}

	public interface OnClickButtonListener {
		public void onClick();
	}

	public void setButtonText(int text) {
		btnButton.setText(text);
	}
}
