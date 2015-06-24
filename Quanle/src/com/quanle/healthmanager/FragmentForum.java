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

import com.quanle.healthmanager.utils.Config;
import com.quanle.healthmanager.utils.DisplayUtil;
import com.quanle.healthmanager.utils.Functions;
import com.quanle.healthmanager.utils.MyBaseAdapter;
import com.quanle.healthmanager.utils.User;
import com.quanle.healthmanager.widget.NavigationBar;
import com.quanle.healthmanager.widget.PullDownView;
import com.quanle.healthmanager.widget.PullDownView.OnPullDownListener;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class FragmentForum extends Fragment implements OnPullDownListener,
		OnItemClickListener {
	View parentView;
	private NavigationBar navigationBar;
	private MyBaseAdapter fourmAdapter = null;
	private PullDownView fourmView;
	private ListView fourmListView;
	private User user;
	private ArrayList<HashMap<String, Object>> fourmArrayList = new ArrayList<HashMap<String, Object>>();
	private static final int WHAT_DID_LOAD_DATA = 0;
	private static final int WHAT_DID_REFRESH = 1;
	private static final int WHAT_DID_MORE = 2;
	private static final int WHAT_DID_FIAL = 9;

	private Button btnCreat;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		user = ((User) getActivity().getApplicationContext());
		// if (parentView == null) {
		parentView = inflater
				.inflate(R.layout.fragment_forum, container, false);
		// }
		navigationBar = (NavigationBar) parentView.findViewById(R.id.nbForum);
		navigationBar.setText("乐圈");

		navigationBar.setImageViewDisplay(false);

		navigationBar.setButtonText(R.string.fourm_btn_create);
		navigationBar.setButtonDisplay(true);
		navigationBar
				.setOnClickButtonListener(new NavigationBar.OnClickButtonListener() {

					@Override
					public void onClick() {
						startActivity(new Intent(getActivity(),
								FourmEditActivity.class));

					}
				});

		// TODO Auto-generated method stub

		fourmView = (PullDownView) parentView
				.findViewById(R.id.fourmPullDownView);
		fourmView.setOnPullDownListener(this);
		fourmListView = fourmView.getListView();

		fourmListView.setOnItemClickListener(this);
		fourmAdapter = new MyBaseAdapter(getActivity(), fourmArrayList,
				R.layout.widget_fourmcategory_item, new String[] { "face",
						"title", "count", "time", "intro" }, new int[] {
						R.id.faceImageView, R.id.titleTextView,
						R.id.countTextView, R.id.timeTextView,
						R.id.introTextView });
		fourmListView.setAdapter(fourmAdapter);
		fourmListView.setDivider(null);
		fourmListView.setDividerHeight(DisplayUtil.dip2px(getActivity(), 5));
		fourmListView.setPadding(DisplayUtil.dip2px(getActivity(), 5),
				DisplayUtil.dip2px(getActivity(), 5),
				DisplayUtil.dip2px(getActivity(), 5),
				DisplayUtil.dip2px(getActivity(), 5));
		fourmView.enableAutoFetchMore(true, 1);

		// if ( fourmArrayList.isEmpty()) // 如果点击的是乐圈按钮并且尚未读取数据，就开始读取
		loadFourmData(true);

		return parentView;
	}

	private Handler mUIHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case WHAT_DID_LOAD_DATA: {
				fourmAdapter.notifyDataSetChanged();
				fourmView.notifyDidDataLoad((msg.arg1 == 0));
				break;
			}
			case WHAT_DID_REFRESH: {
				System.out.println("shua 完了");
				fourmAdapter.notifyDataSetChanged();
				fourmView.notifyDidRefresh((msg.arg1 == 0));
				break;
			}

			case WHAT_DID_MORE: {
				fourmAdapter.notifyDataSetChanged();
				fourmView.notifyDidLoadMore((msg.arg1 == 0));
				break;
			}
			}

		}

	};

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		// TODO Auto-generated method stub
		HashMap<String, Object> map = fourmArrayList.get(position);
		Intent intent = new Intent();
		intent.setClass(getActivity(), FourmActivity.class);
		intent.putExtra("id", map.get("id") + "");
		intent.putExtra("title", (String) map.get("title"));
		startActivity(intent);
		getActivity().overridePendingTransition(R.anim.in_from_right,
				R.anim.out_to_left);
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		loadFourmData(true);
	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub

	}

	private void loadFourmData(final boolean refresh) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("d", Functions
						.getDeviceId(getActivity())));
				params.add(new BasicNameValuePair("c", Functions.getCPUSerial()));
				params.add(new BasicNameValuePair("u", user.getUID() + ""));
				params.add(new BasicNameValuePair("rc", user.getRndCodeString()));
				String httpString = Functions.getHttpResponse(Config.APIURL
						+ "getFourmCategoryList", params);
				System.out.println(params);
				if (httpString == null) {
					System.out.println("httpString null");
					Message msg = mUIHandler.obtainMessage(WHAT_DID_FIAL);
					msg.sendToTarget();
					return;
				}

				System.out.println(httpString);
				JSONObject person = null;
				String reason = "";

				try {
					JSONTokener jsonParser = new JSONTokener(httpString);
					person = (JSONObject) jsonParser.nextValue();
				} catch (JSONException e) {
					System.out.println(httpString);
					Message msg = mUIHandler.obtainMessage(WHAT_DID_FIAL);
					msg.sendToTarget();
					e.printStackTrace();
					return;
				}

				if (person == null) {
					Message msg = mUIHandler.obtainMessage(WHAT_DID_FIAL);
					msg.sendToTarget();
					return;
				}

				int result = person.optInt("result");
				reason = person.optString("reason");

				if (result < 0) {
					System.out.println(reason);
					return;
				}

				if (refresh)
					fourmArrayList.clear();

				JSONArray jsonArray = person.optJSONArray("list");
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject object = jsonArray.optJSONObject(i);
					HashMap<String, Object> map = new HashMap<String, Object>();
					map.put("user", object.optString("usrName"));

					File f = new File(Config.APP_LOCAL_PATH + "/forum/"
							+ object.optInt("id") + ".jpg");
					if (!f.exists()) {
						Functions.CreateFolderTree(Config.APP_LOCAL_PATH
								+ "/forum/");
						Bitmap bitmap = Functions.getHttpBitmap(object
								.optString("icon"));
						if (bitmap != null)
							Functions.saveLoaclBitmap(
									bitmap,
									Config.APP_LOCAL_PATH + "/forum/"
											+ object.optInt("id") + ".jpg");
					}

					map.put("face",
							Config.APP_LOCAL_PATH + "/forum/"
									+ object.optInt("id") + ".jpg");
					map.put("uid", object.optString("uID"));
					map.put("id", object.optInt("id"));
					map.put("title", object.optString("title"));
					map.put("intro", object.optString("intro"));
					map.put("count", object.optString("hits"));
					map.put("time",
							Functions.getNeverTime(object.optString("addtime")));
					fourmArrayList.add(map);
				}

				Integer end = person.optInt("isEnd");

				if (refresh)
					mUIHandler.obtainMessage(WHAT_DID_REFRESH, end, 0)
							.sendToTarget();
				else
					mUIHandler.obtainMessage(WHAT_DID_LOAD_DATA, end, 0)
							.sendToTarget();
			}
		}).start();
	}
}
