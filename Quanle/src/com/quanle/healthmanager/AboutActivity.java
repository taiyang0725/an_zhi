package com.quanle.healthmanager;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import com.quanle.healthmanager.utils.Functions;
import com.quanle.healthmanager.widget.NavigationBar;
import com.quanle.healthmanager.widget.NavigationBar.OnClickBackListener;

public class AboutActivity extends Activity {
	private final String TAG = "StartActivity";
	Context context = AboutActivity.this;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);

		TextView verTextView = (TextView) findViewById(R.id.verTextview);
		verTextView.setText(String.format(
				getResources().getString(R.string.app_ver),
				Functions.getAppVersion(context)));

		NavigationBar nvbar = (NavigationBar) findViewById(R.id.navbar);
		nvbar.setText(R.string.main_menu_about);
		nvbar.setOnClickBackListener(new OnClickBackListener() {
			@Override
			public void onClick() {
				finish();
				overridePendingTransition(R.anim.in_from_left,
						R.anim.out_to_right);
			}
		});

	}
}
