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
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.quanle.healthmanager.utils.Config;
import com.quanle.healthmanager.utils.Functions;
import com.quanle.healthmanager.utils.MyBaseAdapter;
import com.quanle.healthmanager.utils.User;
import com.quanle.healthmanager.widget.CustomDialog;
import com.quanle.healthmanager.widget.CustomProgressDialog;
import com.quanle.healthmanager.widget.NavigationBar;
import com.quanle.healthmanager.widget.NavigationBar.OnClickBackListener;

public class FourmPostActivity extends Activity {
	private Context context = FourmPostActivity.this;
	private Bundle bundle;
	private User user;

	private final int DISCONNECT_INTERNET = 400;
	private final int DISCONNECT_SERVER = 401;

	private static final int WHAT_DID_LOAD_DATA = 0;
	private final int WHAT_FINISH = 1;
	private final int WHAT_ERROR = 2;
	private final int WHAT_LOADING = 3;
	private final int WHAT_OPEN_LOADING = 4;

	private EditText replyEditText;
	private TextView replyTextView;
	private ListView fourmListView;
	private View fourmListHeaderView;
	private MyBaseAdapter fourmAdapter = null;
	private ArrayList<HashMap<String, Object>> fourmArrayList = new ArrayList<HashMap<String, Object>>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fourmpost);

		user = ((User) getApplicationContext());
		bundle = getIntent().getExtras();
		NavigationBar nvbar = (NavigationBar) findViewById(R.id.navbar);
		nvbar.setText("主题");
		nvbar.setOnClickBackListener(new OnClickBackListener() {
			@Override
			public void onClick() {
				finish();
				overridePendingTransition(R.anim.in_from_left,
						R.anim.out_to_right);
			}
		});

		replyEditText = (EditText) findViewById(R.id.replyEditText);

		LayoutInflater mLayoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		fourmListView = (ListView) findViewById(R.id.fourmListView);
		fourmListHeaderView = mLayoutInflater.inflate(
				R.layout.widget_fourmpost_header, new ListView(context), false);
		fourmListView.addHeaderView(fourmListHeaderView);
		fourmAdapter = new MyBaseAdapter(context, fourmArrayList,
				R.layout.widget_fourmpost_item, new String[] { "face", "time",
						"user", "content", "index" }, new int[] {
						R.id.faceImageView, R.id.timeTextView,
						R.id.userTextView, R.id.contentTextView,
						R.id.indexTextView });
		fourmListView.setAdapter(fourmAdapter);

		TextView titleTextView = (TextView) fourmListHeaderView
				.findViewById(R.id.titleTextView);
		titleTextView.setText(bundle.getString("title"));
		TextView readTextView = (TextView) fourmListHeaderView
				.findViewById(R.id.readTextView);
		readTextView.setText(""
				+ (Integer.parseInt(bundle.getString("hits")) + 1));
		replyTextView = (TextView) fourmListHeaderView
				.findViewById(R.id.replyTextView);
		replyTextView.setText(0 + "");

		Button replyButton = (Button) findViewById(R.id.replyButton);
		replyButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				CustomDialog.Builder builder;
				CustomDialog alertDialog;
				if (user.getUID() == 0) {
					builder = new CustomDialog.Builder(context);
					builder.setTitle(R.string.alert_message_warning)
							.setMessage(R.string.alert_message_noland)
							.setPositiveButton(R.string.alert_message_no,
									new OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											dialog.dismiss();
										}
									})
							.setNegativeButton(R.string.alert_message_yes,
									new OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											dialog.dismiss();
											Intent intent = new Intent();
											intent.setClass(context,
													LoginActivity.class);
											startActivity(intent);
											overridePendingTransition(
													R.anim.in_from_right,
													R.anim.out_to_left);
										}
									});
					alertDialog = builder.create();
					alertDialog.show();
					return;
				}

				final String replyString = replyEditText.getText().toString()
						.trim();
				if (replyString.equals("")) {
					Toast.makeText(context, R.string.fourm_toast_intro,
							Toast.LENGTH_SHORT).show();
					return;
				}

				new Thread(new Runnable() {
					@Override
					public void run() {
						List<NameValuePair> params = new ArrayList<NameValuePair>();
						params.add(new BasicNameValuePair("d", Functions
								.getDeviceId(getApplicationContext())));
						params.add(new BasicNameValuePair("c", Functions
								.getCPUSerial()));
						params.add(new BasicNameValuePair("u", user.getUID()
								+ ""));
						params.add(new BasicNameValuePair("rc", user
								.getRndCodeString()));
						params.add(new BasicNameValuePair("pid", bundle
								.getString("id") + ""));
						params.add(new BasicNameValuePair("i", replyString));
						String httpString = Functions.getHttpResponse(
								Config.APIURL + "edtFourmPost", params);
						if (httpString == null) {
							System.out.println(params.toString());
							Message msg = handler
									.obtainMessage(DISCONNECT_SERVER);
							msg.sendToTarget();
							return;
						}
						System.out.println(httpString);
						JSONObject person = null;

						try {
							JSONTokener jsonParser = new JSONTokener(httpString);
							person = (JSONObject) jsonParser.nextValue();
						} catch (JSONException e) {
							System.out.println(httpString);
							Message msg = handler
									.obtainMessage(DISCONNECT_SERVER);
							msg.sendToTarget();
							e.printStackTrace();
							return;
						}

						if (person == null) {
							System.out.println(httpString);
							Message msg = handler
									.obtainMessage(DISCONNECT_SERVER);
							msg.sendToTarget();
							return;
						}

						String reason = person.optString("reason");
						int result = person.optInt("result", 0);
						if (result == 0) {
							handler.obtainMessage(WHAT_ERROR, reason)
									.sendToTarget();
							return;
						}

						HashMap<String, Object> map = new HashMap<String, Object>();
						map.put("user", user.getNickName());
						map.put("face",
								Config.APP_LOCAL_PATH + "/face/"
										+ user.getUID() + ".jpg");
						map.put("uid", user.getUID());
						int index = fourmArrayList.size();
						String[] floorString = getResources().getStringArray(
								R.array.fourm_floor);
						String indexString = (index < 4) ? floorString[index]
								: String.format(
										getResources().getString(
												R.string.fourm_floor), index);

						map.put("index", indexString);
						map.put("content", Html.fromHtml(replyString,
								new Html.ImageGetter() {
									@Override
									public Drawable getDrawable(String source) {
										System.out.println(source);
										Drawable drawable = null;
										String fileString = Config.APP_LOCAL_PATH
												+ "/forum"
												+ source.substring(source
														.lastIndexOf("/"));
										File file = new File(fileString);
										if (!file.exists()) {
											Bitmap bitmap = Functions
													.getHttpBitmap(source);
											Functions.saveLoaclBitmap(bitmap,
													fileString);
										}
										drawable = Drawable
												.createFromPath(fileString);

										DisplayMetrics dm = new DisplayMetrics();
										dm = getResources().getDisplayMetrics();
										int screenWidth = dm.widthPixels;
										float scale = (drawable
												.getIntrinsicHeight() > drawable
												.getIntrinsicWidth()) ? screenWidth
												/ drawable.getIntrinsicHeight()
												: screenWidth
														/ drawable
																.getIntrinsicWidth();
										drawable.setBounds(0, 0, Math
												.round(drawable
														.getIntrinsicWidth()
														* scale), Math
												.round(drawable
														.getIntrinsicHeight()
														* scale));
										return drawable;
									}
								}, null));
						map.put("time",
								getResources().getString(
										R.string.fourm_posttime_never));
						fourmArrayList.add(map);

						handler.obtainMessage(WHAT_FINISH, reason)
								.sendToTarget();
					}
				}).start();
			}
		});

		loadFourmData();
	}

	private void loadFourmData() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				handler.obtainMessage(WHAT_OPEN_LOADING).sendToTarget();

				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("d", Functions
						.getDeviceId(getApplicationContext())));
				params.add(new BasicNameValuePair("c", Functions.getCPUSerial()));
				params.add(new BasicNameValuePair("u", user.getUID() + ""));
				params.add(new BasicNameValuePair("rc", user.getRndCodeString()));
				params.add(new BasicNameValuePair("id", bundle.getString("id")
						+ ""));
				String httpString = Functions.getHttpResponse(Config.APIURL
						+ "getFourmPost", params);
				if (httpString == null) {
					System.out.println(params.toString());
					Message msg = handler.obtainMessage(DISCONNECT_SERVER);
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
					Message msg = handler.obtainMessage(DISCONNECT_SERVER);
					msg.sendToTarget();
					e.printStackTrace();
					return;
				}

				if (person == null) {
					System.out.println(httpString);
					Message msg = handler.obtainMessage(DISCONNECT_SERVER);
					msg.sendToTarget();
					return;
				}

				JSONArray jsonArray = person.optJSONArray("list");
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject object = jsonArray.optJSONObject(i);
					HashMap<String, Object> map = new HashMap<String, Object>();
					map.put("user", object.optString("usrName"));

					File f = new File(Config.APP_LOCAL_PATH + "/face/"
							+ object.optString("uID") + ".jpg");
					if (!f.exists()) {
						Bitmap bitmap = Functions.getHttpBitmap(object
								.optString("icon"));
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
					int index = object.optInt("index");
					String[] floorString = getResources().getStringArray(
							R.array.fourm_floor);
					String indexString = (index < 4) ? floorString[index]
							: String.format(
									getResources().getString(
											R.string.fourm_floor), index);

					map.put("index", indexString);
					map.put("content", Html.fromHtml(
							object.optString("content"),
							new Html.ImageGetter() {
								@Override
								public Drawable getDrawable(String source) {
									System.out.println(source);
									Drawable drawable = null;
									String fileString = Config.APP_LOCAL_PATH
											+ "/forum"
											+ source.substring(source
													.lastIndexOf("/"));
									File file = new File(fileString);
									if (file.exists() && file.length() == 0)
										file.delete();
									if (!file.exists()) {
										Bitmap bitmap = Functions
												.getHttpBitmap(source);
										if (bitmap != null)
											Functions.saveLoaclBitmap(bitmap,
													fileString);
									}

									if (file.exists()) {
										drawable = Drawable
												.createFromPath(fileString);

										DisplayMetrics dm = new DisplayMetrics();
										dm = getResources().getDisplayMetrics();
										int screenWidth = dm.widthPixels;
										float scale = (drawable
												.getIntrinsicHeight() > drawable
												.getIntrinsicWidth()) ? screenWidth
												/ drawable.getIntrinsicHeight()
												: screenWidth
														/ drawable
																.getIntrinsicWidth();
										drawable.setBounds(0, 0, Math
												.round(drawable
														.getIntrinsicWidth()
														* scale), Math
												.round(drawable
														.getIntrinsicHeight()
														* scale));
										return drawable;
									} else
										return null;

								}
							}, null));
					map.put("time",
							Functions.getNeverTime(object.optString("addtime")));
					fourmArrayList.add(map);
				}

				Message msg = handler.obtainMessage(WHAT_DID_LOAD_DATA);
				msg.sendToTarget();
			}
		}).start();
	}

	private Handler handler = new Handler() {
		CustomProgressDialog progressDialog;
		CustomProgressDialog.Builder progressBuilder;

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case WHAT_DID_LOAD_DATA:
				if (progressDialog != null)
					progressDialog.dismiss();

				fourmAdapter.notifyDataSetChanged();
				replyTextView.setText((fourmArrayList.size() - 1) + "");
				break;

			case WHAT_LOADING:
				progressBuilder = new CustomProgressDialog.Builder(context);
				progressBuilder.setMessage(R.string.alert_message_fourm_submit);
				progressDialog = progressBuilder.create();
				progressDialog.show();
				break;

			case WHAT_OPEN_LOADING:
				progressBuilder = new CustomProgressDialog.Builder(context);
				progressBuilder.setMessage(R.string.alert_message_loadding);
				progressDialog = progressBuilder.create();
				progressDialog.show();
				break;

			case DISCONNECT_SERVER:
				if (progressDialog != null)
					progressDialog.dismiss();

				Toast.makeText(context,
						R.string.alert_message_disconnection_server,
						Toast.LENGTH_SHORT).show();
				break;

			case WHAT_ERROR:
				Toast.makeText(context, (String) msg.obj, Toast.LENGTH_SHORT)
						.show();
				if (progressDialog != null)
					progressDialog.dismiss();
				break;

			case WHAT_FINISH:
				if (progressDialog != null)
					progressDialog.dismiss();
				((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
						.hideSoftInputFromWindow(FourmPostActivity.this
								.getCurrentFocus().getWindowToken(),
								InputMethodManager.HIDE_NOT_ALWAYS);
				Toast.makeText(context, (String) msg.obj, Toast.LENGTH_SHORT)
						.show();
				replyEditText.setText(null);
				fourmAdapter.notifyDataSetChanged();
				fourmListView.scrollTo(0, fourmListView.getCount() - 1);
				replyTextView.setText((fourmArrayList.size() - 1) + "");
				break;
			}

		}

	};

}
