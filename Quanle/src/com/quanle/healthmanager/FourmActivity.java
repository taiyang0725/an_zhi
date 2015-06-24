package com.quanle.healthmanager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.quanle.healthmanager.utils.Config;
import com.quanle.healthmanager.utils.DisplayUtil;
import com.quanle.healthmanager.utils.Functions;
import com.quanle.healthmanager.utils.MyBaseAdapter;
import com.quanle.healthmanager.utils.User;
import com.quanle.healthmanager.widget.CustomProgressDialog;
import com.quanle.healthmanager.widget.NavigationBar;
import com.quanle.healthmanager.widget.NavigationBar.OnClickBackListener;
import com.quanle.healthmanager.widget.NavigationBar.OnClickButtonListener;
import com.quanle.healthmanager.widget.PullDownView.OnPullDownListener;
import com.quanle.healthmanager.widget.PullDownView;

public class FourmActivity extends Activity implements OnPullDownListener,
		OnItemClickListener {
	private Context context = FourmActivity.this;
	private Bundle bundle;
	private User user;

	private final int DISCONNECT_INTERNET = 400;
	private final int DISCONNECT_SERVER = 401;
	private final int UPDATE_FINSH = 402;
	private final int UPDATE_NULL = 407;

	private static final int WHAT_DID_LOAD_DATA = 0;
	private static final int WHAT_DID_REFRESH = 1;
	private static final int WHAT_DID_MORE = 2;
	private static final int WHAT_DID_FIAL = 9;

	private static final int REQUEST_POST = 0;

	private PullDownView fourmView;
	private ListView fourmListView;
	private ImageView noneImageView;
	private MyBaseAdapter fourmAdapter = null;
	private ArrayList<HashMap<String, Object>> fourmArrayList = new ArrayList<HashMap<String, Object>>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fourm);

		user = ((User) getApplicationContext());
		bundle = getIntent().getExtras();
		NavigationBar nvbar = (NavigationBar) findViewById(R.id.navbar);
		nvbar.setText(bundle.getString("title"));
		nvbar.setOnClickBackListener(new OnClickBackListener() {
			@Override
			public void onClick() {
				finish();
				overridePendingTransition(R.anim.in_from_left,
						R.anim.out_to_right);
			}
		});

		if (user.getUID() > 0) {
			nvbar.setButtonDisplay(true);
			nvbar.setButtonText(R.string.fourm_btn_postcreate);
			nvbar.setOnClickButtonListener(new OnClickButtonListener() {
				@Override
				public void onClick() {
					Intent intent = new Intent();
					intent.setClass(context, FourmPostEditActivity.class);
					intent.putExtra("cid", bundle.getString("id"));
					startActivityForResult(intent, REQUEST_POST);
					overridePendingTransition(R.anim.in_from_right,
							R.anim.out_to_left);

				}
			});
		}

		noneImageView = (ImageView) findViewById(R.id.noneImageView);
		fourmView = (PullDownView) findViewById(R.id.fourmPullDownView);
		fourmView.setOnPullDownListener(this);
		fourmListView = fourmView.getListView();

		fourmListView.setOnItemClickListener(this);
		fourmAdapter = new MyBaseAdapter(context, fourmArrayList,
				R.layout.widget_fourm_item, new String[] { "face", "title",
						"hits", "reply", "time", "user" }, new int[] {
						R.id.faceImageView, R.id.titleTextView,
						R.id.readTextView, R.id.replyTextView,
						R.id.timeTextView, R.id.userTextView });
		fourmListView.setAdapter(fourmAdapter);
		fourmListView.setDivider(null);
		fourmListView.setDividerHeight(DisplayUtil.dip2px(context, 5));
		fourmListView.setPadding(DisplayUtil.dip2px(context, 5),
				DisplayUtil.dip2px(context, 5), DisplayUtil.dip2px(context, 5),
				DisplayUtil.dip2px(context, 5));
		fourmView.enableAutoFetchMore(true, 1);

		loadFourmData(false);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case REQUEST_POST:
				noneImageView.setVisibility(View.GONE);
				fourmView.setVisibility(View.VISIBLE);

				loadFourmData(true);
				break;
			default:
				break;
			}
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	private void loadFourmData(final boolean refresh) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("d", Functions
						.getDeviceId(getApplicationContext())));
				params.add(new BasicNameValuePair("c", Functions.getCPUSerial()));
				params.add(new BasicNameValuePair("u", user.getUID() + ""));
				params.add(new BasicNameValuePair("rc", user.getRndCodeString()));
				params.add(new BasicNameValuePair("id", bundle.getString("id")));
				String httpString = Functions.getHttpResponse(Config.APIURL
						+ "getFourmList", params);
				System.out.println(params);
				if (httpString == null) {
					System.out.println(params.toString());
					Message msg = handler.obtainMessage(WHAT_DID_FIAL);
					msg.sendToTarget();
					return;
				}

				JSONObject person = null;
				String reason = "";
				System.out.println(httpString);
				try {
					JSONTokener jsonParser = new JSONTokener(httpString);
					person = (JSONObject) jsonParser.nextValue();
				} catch (JSONException e) {
					Message msg = handler.obtainMessage(WHAT_DID_FIAL);
					msg.sendToTarget();
					e.printStackTrace();
					return;
				}

				if (person == null) {
					Message msg = handler.obtainMessage(WHAT_DID_FIAL);
					msg.sendToTarget();
					return;
				}

				if (refresh)
					fourmArrayList.clear();

				JSONArray jsonArray = person.optJSONArray("list");
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject object = jsonArray.optJSONObject(i);
					HashMap<String, Object> map = new HashMap<String, Object>();
					map.put("user", object.optString("usrName"));

					File f = new File(Config.APP_LOCAL_PATH + "/face/"
							+ object.optString("uID") + ".jpg");
					if (!f.exists()) {
						Functions.CreateFolderTree(Config.APP_LOCAL_PATH
								+ "/face/");
						Bitmap bitmap = Functions.getHttpBitmap(object
								.optString("face"));
						if (bitmap != null)
							Functions.saveLoaclBitmap(
									bitmap,
									Config.APP_LOCAL_PATH + "/face/"
											+ object.optInt("uID") + ".jpg");
					}
					map.put("face",
							Config.APP_LOCAL_PATH + "/face/"
									+ object.optString("uID") + ".jpg");
					map.put("uid", object.optString("uID"));
					map.put("title", object.optString("title"));
					map.put("id", object.optString("id"));
					map.put("user", object.optString("usrName"));
					map.put("hits", object.optString("hits"));
					map.put("reply", object.optString("reply"));
					map.put("time",
							Functions.getNeverTime(object.optString("addtime")));
					fourmArrayList.add(map);
				}

				Integer end = person.optInt("isEnd");

				if (refresh)
					handler.obtainMessage(end, 0, WHAT_DID_REFRESH)
							.sendToTarget();
				else
					handler.obtainMessage(end, 0, WHAT_DID_LOAD_DATA)
							.sendToTarget();
			}
		}).start();
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case WHAT_DID_LOAD_DATA: {
				fourmAdapter.notifyDataSetChanged();
				fourmView.notifyDidDataLoad(msg.arg1 == 0);
				break;
			}
			case WHAT_DID_REFRESH: {
				fourmAdapter.notifyDataSetChanged();
				fourmView.notifyDidRefresh(msg.arg1 == 0);
				break;
			}

			case WHAT_DID_MORE: {
				fourmAdapter.notifyDataSetChanged();
				fourmView.notifyDidLoadMore(msg.arg1 == 0);
				break;
			}
			}

			if (fourmArrayList.size() == 0) {
				noneImageView.setVisibility(View.VISIBLE);
				fourmView.setVisibility(View.GONE);
			} else {
				noneImageView.setVisibility(View.GONE);
				fourmView.setVisibility(View.VISIBLE);
			}
		}

	};

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		HashMap<String, Object> map = fourmArrayList.get(position);
		Intent intent = new Intent();
		intent.setClass(context, FourmPostActivity.class);
		System.out.println(map.get("hits"));
		intent.putExtra("id", map.get("id") + "");
		intent.putExtra("title", (String) map.get("title"));
		intent.putExtra("hits", map.get("hits") + "");
		startActivity(intent);
		overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
	}

	@Override
	public void onRefresh() {
		loadFourmData(true);
	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub

	}

}
