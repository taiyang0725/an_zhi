package com.quanle.healthmanager;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import com.quanle.healthmanager.widget.NavigationBar;
import com.quanle.healthmanager.widget.NavigationBar.OnClickBackListener;
import com.quanle.healthmanager.widget.NavigationBar.OnClickButtonListener;

public class SuggestActivity extends Activity {
	Context context = SuggestActivity.this;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_suggest);

		NavigationBar nvbar = (NavigationBar) findViewById(R.id.navbar);
		nvbar.setText(R.string.main_menu_suggest);
		nvbar.setOnClickBackListener(new OnClickBackListener() {
			@Override
			public void onClick() {
				finish();
				overridePendingTransition(R.anim.in_from_left,
						R.anim.out_to_right);
			}
		});
		nvbar.setButtonDisplay(true);
		nvbar.setOnClickButtonListener(new OnClickButtonListener() {
			@Override
			public void onClick() {
				Toast.makeText(context, "¿ª·¢ÖÐ...", Toast.LENGTH_SHORT).show();
			}

		});

	}
}
