package com.quanle.healthmanager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.quanle.healthmanager.R;
import com.quanle.healthmanager.utils.Config;
import com.quanle.healthmanager.widget.NavigationBar;
import com.quanle.healthmanager.widget.NavigationBar.OnClickBackListener;

public class WebActivity extends Activity {
	private final String TAG = "WebActivity";
	private Context context = this;
	private WebView web;

	private Intent intent;
	private int type = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_web);

		NavigationBar nvbar = (NavigationBar) findViewById(R.id.navbar);
		String[] titleStrings = getResources().getStringArray(
				R.array.main_nav_title);

		nvbar.setOnClickBackListener(new OnClickBackListener() {
			@Override
			public void onClick() {
				finish();
				overridePendingTransition(R.anim.in_from_left,
						R.anim.out_to_right);
			}
		});

		intent = this.getIntent();

		type = intent.getIntExtra("type", 0);
		String urlString = "";

		switch (type) {
		case 0:
			urlString = Config.APIURL + "getPronunciation";
			nvbar.setText(titleStrings[5]);
			break;
		default:
			urlString = Config.APIURL + "getGrammer";
			nvbar.setText(titleStrings[6]);
			break;
		}

		web = (WebView) findViewById(R.id.web);
		web.setWebViewClient(new WebViewClient());
		web.loadUrl(urlString);

	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && web.canGoBack()) {
			web.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
