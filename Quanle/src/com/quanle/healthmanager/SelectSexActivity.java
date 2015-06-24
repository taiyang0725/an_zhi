package com.quanle.healthmanager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.quanle.healthmanager.widget.NavigationBar;

public class SelectSexActivity extends Activity {
	private Context context = this;

	private NavigationBar navBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_sex);

		navBar = (NavigationBar) findViewById(R.id.navbar);
		navBar.setText(R.string.select_sex_title);
		navBar.setBackDisplayable(false);

		LinearLayout layoutAge = (LinearLayout) findViewById(R.id.layoutAge);
		for (int i = 0; i < layoutAge.getChildCount(); i++) {
			final int ind = i;
			LinearLayout layout = (LinearLayout) layoutAge.getChildAt(i);
			layout.setClickable(true);
			layout.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
					intent.setClass(context, SelectAgeActivity.class);
					intent.putExtra("sex", ind);
					startActivity(intent);
					finish();
					overridePendingTransition(R.anim.in_from_right, 0);
				}
			});
		}

	}
}
