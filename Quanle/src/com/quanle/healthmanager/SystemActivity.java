package com.quanle.healthmanager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.quanle.healthmanager.utils.Config;
import com.quanle.healthmanager.utils.Functions;
import com.quanle.healthmanager.widget.Menu;
import com.quanle.healthmanager.widget.Menu.OnMenuClickListener;
import com.quanle.healthmanager.widget.NavigationBar;
import com.quanle.healthmanager.widget.NavigationBar.OnClickBackListener;

public class SystemActivity extends Activity {
	private String TAG = "RegActivity";
	private Handler handler;
	private CacheThread cacheThread;
	private UpdateThread updateThread;
	private Message msg;
	private Functions funcs = new Functions();
	private Context context = SystemActivity.this;

	private final int DISCONNECT_INTERNET = 400;
	private final int DISCONNECT_SERVER = 401;
	private final int UPDATE_FINSH = 402;
	private final int UPDATE_NULL = 407;
	private final int DOWNLOAD_NEWVER = 403;
	private final int DOWNLOAD_PROGRESS = 404;
	private final int DOWNLOAD_FAIL = 405;
	private final int DOWNLOAD_FINSH = 406;
	private final int CACHE_FINSH = 100;
	private final int CACHE_FAIL = 101;

	private Menu menu;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_system);

		handler = new Handler() {
			AlertDialog.Builder builder;
			AlertDialog alertDialog;

			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case DISCONNECT_INTERNET:
					menu.setValue(0, 0,
							getResources().getString(R.string.main_update_fail));
					menu.setDisplayLoading(0, 0, false);
					break;

				case DISCONNECT_SERVER:
					menu.setValue(0, 0,
							getResources().getString(R.string.main_update_fail));
					menu.setDisplayLoading(0, 0, false);
					break;

				case UPDATE_FINSH:
					menu.setValue(0, 0, String.format(
							getResources()
									.getString(R.string.main_update_value),
							msg.obj.toString()));
					menu.setDisplayLoading(0, 0, false);
					break;

				case UPDATE_NULL:
					menu.setValue(0, 0,
							getResources().getString(R.string.main_update_null));
					menu.setDisplayLoading(0, 0, false);

					break;

				case CACHE_FINSH:
					menu.setDisplayLoading("cache", false);
					if (msg.arg1 == 0)
						menu.setValue(
								0,
								1,
								getResources().getString(
										R.string.main_cache_null));
					else
						menu.setValue("cache", String.format(getResources()
								.getString(R.string.main_cache_value),
								Functions.getFormatSize(msg.arg1)));
					break;
				}
			}
		};

		NavigationBar nvbar = (NavigationBar) findViewById(R.id.navbar);
		nvbar.setText(R.string.main_menu_system);
		nvbar.setOnClickBackListener(new OnClickBackListener() {
			@Override
			public void onClick() {
				finish();
				overridePendingTransition(R.anim.in_from_left,
						R.anim.out_to_right);
			}
		});

		ArrayList<ArrayList<HashMap<String, Object>>> menuArrayList = new ArrayList<ArrayList<HashMap<String, Object>>>();

		ArrayList<HashMap<String, Object>> subMenuArrayList = new ArrayList<HashMap<String, Object>>();

		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("tag", "update");
		map.put("icon", R.drawable.menu_update);
		map.put("title", getResources().getString(R.string.main_menu_update));
		map.put("displayLoading", true);
		map.put("displayRightArray", false);
		subMenuArrayList.add(map);

		map = new HashMap<String, Object>();
		map.put("tag", "cache");
		map.put("icon", R.drawable.menu_cache);
		map.put("title", getResources().getString(R.string.main_menu_cache));
		map.put("displayLoading", true);
		map.put("displayRightArray", false);
		subMenuArrayList.add(map);

		menuArrayList.add(subMenuArrayList);

		subMenuArrayList = new ArrayList<HashMap<String, Object>>();

		map = new HashMap<String, Object>();
		map.put("tag", "suggest");
		map.put("icon", R.drawable.menu_suggest);
		map.put("title", getResources().getString(R.string.main_menu_suggest));
		map.put("displayLoading", false);
		map.put("displayRightArray", true);
		subMenuArrayList.add(map);

		map = new HashMap<String, Object>();
		map.put("tag", "about");
		map.put("icon", R.drawable.menu_about);
		map.put("title", getResources().getString(R.string.main_menu_about));
		map.put("displayLoading", false);
		map.put("displayRightArray", true);
		subMenuArrayList.add(map);

		menuArrayList.add(subMenuArrayList);

		menu = (Menu) findViewById(R.id.menu);
		menu.setMenu(menuArrayList);
		menu.setOnMenuClickListener(new OnMenuClickListener() {
			public void onClick(String tagString) {
				if (tagString == "update") {
					Toast.makeText(context, "开发中...", Toast.LENGTH_SHORT)
							.show();
				} else if (tagString == "cache") {
					Toast.makeText(context, "开发中...", Toast.LENGTH_SHORT)
							.show();
				} else if (tagString == "suggest") {
					Intent intent = new Intent();
					intent.setClass(context, SuggestActivity.class);
					startActivity(intent);
					overridePendingTransition(R.anim.in_from_right,
							R.anim.out_to_left);
				} else if (tagString == "about") {
					Intent intent = new Intent();
					intent.setClass(context, AboutActivity.class);
					startActivity(intent);
					overridePendingTransition(R.anim.in_from_right,
							R.anim.out_to_left);
				}
			}
		});

		cacheThread = new CacheThread();
		cacheThread.start();

		updateThread = new UpdateThread();
		updateThread.start();
	}

	private class CacheThread extends Thread {
		public void run() {
			File file = new File(Config.APP_LOCAL_PATH);
			if (!file.exists()) {
				Message msg = new Message();
				msg.what = CACHE_FINSH;
				msg.arg1 = 0;
				handler.sendMessage(msg);
				return;
			}

			Message msg = new Message();
			msg.what = CACHE_FINSH;
			msg.arg1 = (int) Functions.getFolderSize(file);
			handler.sendMessage(msg);
			return;

		}
	}

	private class UpdateThread extends Thread {
		public void run() {
			String httpString = funcs.getHttpResponse(Config.APIURL
					+ "checkUpdate");
			Log.d(TAG, "http=============" + httpString);

			if (httpString == null) {
				msg = new Message();
				msg.what = DISCONNECT_SERVER;
				handler.sendMessage(msg);
				return;
			}

			int verCode = 0;
			String reason = null;
			String url = null;
			String ver = null;
			try {
				JSONTokener jsonParser = new JSONTokener(httpString);
				JSONObject person = (JSONObject) jsonParser.nextValue();
				verCode = person.getInt("verCode");
				reason = person.getString("reason");
				url = person.getString("url");
				ver = person.getString("ver");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			int curVersion = funcs.getAppVersionName(getApplicationContext());

			if (curVersion < verCode) {
				msg = new Message();
				msg.what = UPDATE_FINSH;
				msg.obj = ver;
				handler.sendMessage(msg);
				return;
			}

			msg = new Message();
			msg.what = UPDATE_NULL;
			handler.sendMessage(msg);
			return;
		}
	}

}
