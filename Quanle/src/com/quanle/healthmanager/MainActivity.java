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
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.quanle.healthmanager.utils.Config;
import com.quanle.healthmanager.utils.DisplayUtil;
import com.quanle.healthmanager.utils.FormFile;
import com.quanle.healthmanager.utils.Functions;
import com.quanle.healthmanager.utils.MyBaseAdapter;
import com.quanle.healthmanager.utils.SharedPreferencesHelper;
import com.quanle.healthmanager.utils.User;
import com.quanle.healthmanager.widget.CustomDialog;
import com.quanle.healthmanager.widget.CustomProgressDialog;
import com.quanle.healthmanager.widget.Menu;
import com.quanle.healthmanager.widget.Menu.OnMenuClickListener;
import com.quanle.healthmanager.widget.NavigationBar;
import com.quanle.healthmanager.widget.NavigationBar.OnClickButtonListener;
import com.quanle.healthmanager.widget.PullDownView;
import com.quanle.healthmanager.widget.PullDownView.OnPullDownListener;
import com.quanle.healthmanager.widget.Slider;
import com.quanle.healthmanager.widget.Tabbar;

public class MainActivity extends Activity implements OnPullDownListener,
		OnItemClickListener {
	private Context context = MainActivity.this;
	private MyBaseAdapter adapter = null;
	private ArrayList<HashMap<String, Object>> arrayList = new ArrayList<HashMap<String, Object>>();
	private ArrayList<HashMap<String, Object>> fourmArrayList = new ArrayList<HashMap<String, Object>>();

	private MyBaseAdapter fourmAdapter = null;
	private PullDownView fourmView;
	private ListView fourmListView;
	private ViewGroup rootView;
	private Tabbar mainTabbar;
	private NavigationBar workNarbar;
	private NavigationBar personNarbar;
	private NavigationBar mainNarbar;

	private Menu menu;
	private PopupWindow popwin;

	private NavigationBar personNavigationBar;
	private NavigationBar fourmNavigationBar;
	private ImageView camarImageView;
	private ImageView faceImageView;

	private Slider slider;

	private FrameLayout faceLayout;

	private User user;
	private long mExitTime;

	private final int RESULT_LOAD_IMAGE = 1;
	private final int RESULT_LOAD_CAMRA = 2;
	private final int RESULT_LOAD_CLIP = 3;

	private static final int WHAT_DID_LOAD_DATA = 0;
	private static final int WHAT_DID_REFRESH = 1;
	private static final int WHAT_DID_MORE = 2;
	private static final int WHAT_DID_FIAL = 9;

	private static final int WHAT_ERROR = 0;
	private static final int WHAT_FINISH = 1;
	private static final int WHAT_LOADING = 2;

	private static final int WHAT_SLIDE_FINISH = 10;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		user = ((User) getApplicationContext());
		rootView = (ViewGroup) getRootView((Activity) context);

		faceLayout = (FrameLayout) findViewById(R.id.faceLayout);
		camarImageView = (ImageView) findViewById(R.id.camarImageView);
		faceImageView = (ImageView) findViewById(R.id.faceImageView);

		slider = (Slider) findViewById(R.id.slider);

		String[] navTitleString = getResources().getStringArray(
				R.array.main_nav_title);
		int[] tabbarPressedIconInt = new int[] { R.drawable.home_pressed,
				R.drawable.log_pressed, R.drawable.fourm_pressed,
				R.drawable.appointment_pressed, R.drawable.store_pressed,
				R.drawable.person_pressed };
		int[] tabbarIconInt = new int[] { R.drawable.home_normal,
				R.drawable.log_normal, R.drawable.fourm_normal,
				R.drawable.appointment_normal, R.drawable.store_normal,
				R.drawable.person_normal };
		String[] tabbarItemTitleString = getResources().getStringArray(
				R.array.main_toolbar_item_title);

		ArrayList<HashMap<String, Object>> tabbarArrayList = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < tabbarItemTitleString.length; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("title", tabbarItemTitleString[i]);
			map.put("normalIcon", tabbarIconInt[i]);
			map.put("pressedIcon", tabbarPressedIconInt[i]);
			tabbarArrayList.add(map);
		}

		personNavigationBar = (NavigationBar) findViewById(R.id.nbPerson);
		fourmNavigationBar = (NavigationBar) findViewById(R.id.nbForum);

		mainTabbar = (Tabbar) findViewById(R.id.tbMain);
		mainTabbar.setNormalTextColor(getResources().getColor(R.color.gary));
		mainTabbar.setPressedTextColor(getResources().getColor(R.color.blue));
		mainTabbar.setItem(tabbarArrayList);
		mainTabbar.setSelectIndex(0);

		for (int i = 0; i < mainTabbar.getTabCount(); i++) {
			RelativeLayout rlLayout = (RelativeLayout) rootView.getChildAt(i);
			rlLayout.setVisibility((i == 0) ? View.VISIBLE : View.GONE);
			NavigationBar navBar = (NavigationBar) rlLayout.getChildAt(0);
			navBar.setText(navTitleString[i]);
			navBar.setBackDisplayable(false);
		}

		mainTabbar.setOnTabChengedListener(new Tabbar.OnTabChengedListener() {
			@Override
			public void onTabChanged(int index) {
				if (index == 2 && fourmArrayList.isEmpty()) // ������������Ȧ��ť������δ��ȡ���ݣ��Ϳ�ʼ��ȡ
					loadFourmData(false);

				for (int i = 0; i < mainTabbar.getTabCount(); i++) {
					RelativeLayout rlLayout = (RelativeLayout) rootView
							.getChildAt(i);
					rlLayout.setVisibility((i == index) ? View.VISIBLE
							: View.GONE);
				}
			}
		});

		ArrayList<ArrayList<HashMap<String, Object>>> menuArrayList = new ArrayList<ArrayList<HashMap<String, Object>>>();
		ArrayList<HashMap<String, Object>> subMenuArrayList = new ArrayList<HashMap<String, Object>>();

		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("tag", "mail");
		map.put("icon", R.drawable.menu_mail);
		map.put("title", getResources().getString(R.string.main_menu_mail));
		map.put("displayRightArray", true);
		map.put("visible", false);
		subMenuArrayList.add(map);

		map = new HashMap<String, Object>();
		map.put("tag", "changepass");
		map.put("icon", R.drawable.menu_changepass);
		map.put("title",
				getResources().getString(R.string.main_menu_changepassword));
		map.put("displayRightArray", true);
		map.put("visible", false);
		subMenuArrayList.add(map);

		map = new HashMap<String, Object>();
		map.put("tag", "changeinfo");
		map.put("icon", R.drawable.menu_personinfo);
		map.put("title", getResources()
				.getString(R.string.main_menu_changeinfo));
		map.put("displayRightArray", true);
		map.put("visible", false);
		subMenuArrayList.add(map);

		map = new HashMap<String, Object>();
		map.put("tag", "tag");
		map.put("icon", R.drawable.menu_tag);
		map.put("title", getResources().getString(R.string.main_menu_tag));
		map.put("displayRightArray", true);
		map.put("visible", false);
		subMenuArrayList.add(map);

		map = new HashMap<String, Object>();
		map.put("tag", "family");
		map.put("icon", R.drawable.menu_family);
		map.put("title", getResources().getString(R.string.main_menu_family));
		map.put("displayRightArray", true);
		map.put("visible", false);
		subMenuArrayList.add(map);

		menuArrayList.add(subMenuArrayList);

		subMenuArrayList = new ArrayList<HashMap<String, Object>>();

		map = new HashMap<String, Object>();
		map.put("tag", "city");
		map.put("icon", R.drawable.menu_city);
		map.put("title", getResources()
				.getString(R.string.main_menu_changecity));
		map.put("displayRightArray", true);
		subMenuArrayList.add(map);

		map = new HashMap<String, Object>();
		map.put("tag", "age");
		map.put("icon", R.drawable.menu_age);
		map.put("title", getResources().getString(R.string.main_menu_age));
		map.put("displayRightArray", true);
		subMenuArrayList.add(map);

		menuArrayList.add(subMenuArrayList);

		subMenuArrayList = new ArrayList<HashMap<String, Object>>();

		map = new HashMap<String, Object>();
		map.put("tag", "system");
		map.put("icon", R.drawable.menu_system);
		map.put("title", getResources().getString(R.string.main_menu_system));
		map.put("displayRightArray", true);
		subMenuArrayList.add(map);

		menuArrayList.add(subMenuArrayList);

		menu = (Menu) findViewById(R.id.menu);
		menu.setMenu(menuArrayList);
		menu.setOnMenuClickListener(new OnMenuClickListener() {
			public void onClick(String tagString) {
				if (tagString == "system") {
					Intent intent = new Intent();
					intent.setClass(context, SystemActivity.class);
					startActivity(intent);
					overridePendingTransition(R.anim.in_from_right,
							R.anim.out_to_left);
				} else if (tagString == "changepass") {
					Intent intent = new Intent();
					intent.setClass(context, ChangePassActivity.class);
					startActivity(intent);
					overridePendingTransition(R.anim.in_from_right,
							R.anim.out_to_left);

				} else if (tagString == "changeinfo") {
					Intent intent = new Intent();
					intent.setClass(context, ChangeInfoActivity.class);
					startActivity(intent);
					overridePendingTransition(R.anim.in_from_right,
							R.anim.out_to_left);
				}
			}
		});

		// RelativeLayout chgpassLayout = (RelativeLayout)
		// findViewById(R.id.chgpassLayout);
		// chgpassLayout.setClickable(true);
		// chgpassLayout.setOnClickListener(new Button.OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// Intent intent = new Intent();
		// intent.setClass(context, ChgpassActivity.class);
		// startActivity(intent);
		// overridePendingTransition(R.anim.in_from_right,
		// R.anim.out_to_left);
		// }
		// });

		Button loginButton = (Button) findViewById(R.id.loginButton);
		loginButton.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(context, LoginActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.in_from_right,
						R.anim.out_to_left);
			}
		});

		Button regButton = (Button) findViewById(R.id.regButton);
		regButton.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(context, RegActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.in_from_right,
						R.anim.out_to_left);
			}
		});

		/*
		 * homeworkListView = (ListView) findViewById(R.id.homeworkListView);
		 * homeworkAdapter = new MyBaseAdapter(this, homeworkArrayList,
		 * R.layout.listview_homework, new String[] { "title", "icon", "status",
		 * "endtime" }, new int[] { R.id.tvTitle, R.id.ivIcon, R.id.tvStatus,
		 * R.id.tvEndtime }); homeworkListView.setAdapter(homeworkAdapter);
		 * homeworkListView.setOnItemClickListener(new OnItemClickListener() {
		 * 
		 * @Override public void onItemClick(AdapterView<?> parent, View view,
		 * int position, long id) { HashMap<String, Object> map =
		 * homeworkArrayList.get(position); Intent intent = new Intent();
		 * intent.putExtra("id", (Integer) map.get("id"));
		 * intent.putExtra("title", map.get("title").toString()); //
		 * intent.setClass(context, HomeworkActivity.class);
		 * startActivity(intent);
		 * overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
		 * } });
		 */

		faceLayout.setClickable(true);
		faceLayout.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (user.getLandStatus() == 0) {
					Intent intent = new Intent();
					intent.setClass(context, LoginActivity.class);
					startActivity(intent);
					overridePendingTransition(R.anim.in_from_right,
							R.anim.out_to_left);
					return;
				}

				LayoutInflater mLayoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
				View menuView = mLayoutInflater.inflate(
						R.layout.popupwindow_face, null, false);
				popwin = new PopupWindow(menuView, LayoutParams.MATCH_PARENT,
						LayoutParams.WRAP_CONTENT, true);
				popwin.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.popupwindow_bg));
				popwin.setAnimationStyle(R.style.popupAnimation);
				popwin.showAtLocation(menu, Gravity.BOTTOM, 0, 0);
				popwin.update();
				((Button) menuView.findViewById(R.id.btnGallery))
						.setOnClickListener(new Button.OnClickListener() {
							@Override
							public void onClick(View v) {
								popwin.dismiss();

								if (!Functions.hasSDCard()) {
									Toast.makeText(
											context,
											getResources()
													.getString(
															R.string.app_toast_nosdcard),
											Toast.LENGTH_SHORT).show();
									return;
								}

								Intent intent = new Intent(
										Intent.ACTION_PICK,
										android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
								startActivityForResult(intent,
										RESULT_LOAD_IMAGE);
								overridePendingTransition(R.anim.in_from_right,
										R.anim.out_to_left);
							}
						});
				((Button) menuView.findViewById(R.id.btnCamar))
						.setOnClickListener(new Button.OnClickListener() {
							@Override
							public void onClick(View v) {
								popwin.dismiss();

								if (!Functions.hasSDCard()) {
									Toast.makeText(
											context,
											getResources()
													.getString(
															R.string.app_toast_nosdcard),
											Toast.LENGTH_SHORT).show();
									return;
								}

								Intent intent = new Intent(
										MediaStore.ACTION_IMAGE_CAPTURE);
								Functions
										.CreateFolderTree(Config.APP_LOCAL_PATH
												+ "/tmp");
								Uri imageUri = Uri.fromFile(new File(
										Config.APP_LOCAL_PATH + "/tmp",
										"image.jpg"));
								intent.putExtra(MediaStore.EXTRA_OUTPUT,
										imageUri);
								startActivityForResult(intent,
										RESULT_LOAD_CAMRA);
							}
						});
			}
		});

		fourmView = (PullDownView) findViewById(R.id.fourmPullDownView);
		fourmView.setOnPullDownListener(this);
		fourmListView = fourmView.getListView();

		fourmListView.setOnItemClickListener(this);
		fourmAdapter = new MyBaseAdapter(context, fourmArrayList,
				R.layout.widget_fourmcategory_item, new String[] { "face",
						"title", "count", "time", "intro" }, new int[] {
						R.id.faceImageView, R.id.titleTextView,
						R.id.countTextView, R.id.timeTextView,
						R.id.introTextView });
		fourmListView.setAdapter(fourmAdapter);
		fourmListView.setDivider(null);
		fourmListView.setDividerHeight(DisplayUtil.dip2px(context, 5));
		fourmListView.setPadding(DisplayUtil.dip2px(context, 5),
				DisplayUtil.dip2px(context, 5), DisplayUtil.dip2px(context, 5),
				DisplayUtil.dip2px(context, 5));
		fourmView.enableAutoFetchMore(true, 1);

		// TODO �̳ǲ���
		new Thread(new Runnable() {
			@Override
			public void run() {
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("d", Functions
						.getDeviceId(getApplicationContext())));
				params.add(new BasicNameValuePair("c", Functions.getCPUSerial()));
				String httpString = Functions.getHttpResponse(Config.APIURL
						+ "getStoreSlide", params);
				if (httpString == null) {
					return;
				}

				System.out.println(httpString);
				JSONObject person = null;
				String reason = "";

				try {
					JSONTokener jsonParser = new JSONTokener(httpString);
					person = (JSONObject) jsonParser.nextValue();
				} catch (JSONException e) {
					e.printStackTrace();
					return;
				}

				if (person == null) {
					return;
				}

				int result = person.optInt("result");
				reason = person.optString("reason");

				if (result < 0) {
					return;
				}

				JSONArray jsonArray = person.optJSONArray("list");
				Bitmap[] bmp = new Bitmap[jsonArray.length()];
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject object = jsonArray.optJSONObject(i);
					String url = object.optString("picture");
					String fileString = url.substring(url.lastIndexOf("/") + 1,
							url.length());
					File f = new File(Config.APP_LOCAL_PATH + "/store/"
							+ fileString);
					Bitmap bitmap;

					if (!f.exists()) {
						Functions.CreateFolderTree(Config.APP_LOCAL_PATH
								+ "/store/");
						bitmap = Functions.getHttpBitmap(url);
						if (bitmap != null)
							Functions.saveLoaclBitmap(bitmap,
									Config.APP_LOCAL_PATH + "/store/"
											+ fileString);
					} else {
						bitmap = Functions
								.getLoacalBitmap(Config.APP_LOCAL_PATH
										+ "/store/" + fileString);
					}

					bmp[i] = bitmap;
				}

				mUIHandler.obtainMessage(WHAT_SLIDE_FINISH, bmp).sendToTarget();
			}
		}).start();
	}

	

	private static View getRootView(Activity context) {
		return ((ViewGroup) context.findViewById(android.R.id.content))
				.getChildAt(0);
	}

	@Override
	protected void onResume() {
		LinearLayout guestLayout = (LinearLayout) findViewById(R.id.guestLayout);
		LinearLayout userLayout = (LinearLayout) findViewById(R.id.userLayout);

		if (user.getFace() != null)
			faceImageView.setImageBitmap(user.getFace());
		else
			faceImageView.setImageResource(R.drawable.face);

		if (user.getLandStatus() > 0) {
			TextView nickTextView = (TextView) findViewById(R.id.nickTextView);

			guestLayout.setVisibility(View.GONE);
			userLayout.setVisibility(View.VISIBLE);
			nickTextView.setText(user.getNickName());
			menu.setValue("city", user.getCityString());
			menu.setDisplay("family", true);
			menu.setDisplay("changeinfo", true);
			menu.setDisplay("tag", true);
			if (user.getLandStatus() != User.LANDSTATUS_MAIL) {
				menu.setDisplay("mail", true);
				menu.setDisplay("changepass", false);
			} else {
				menu.setDisplay("mail", false);
				menu.setDisplay("changepass", true);
			}
			camarImageView.setImageDrawable(getResources().getDrawable(
					R.drawable.camar));
			personNavigationBar.setButtonDisplay(true);
			personNavigationBar.setButtonText(R.string.main_btn_logout);
			personNavigationBar
					.setOnClickButtonListener(new OnClickButtonListener() {
						@Override
						public void onClick() {
							CustomDialog.Builder builder;
							CustomDialog alertDialog;
							builder = new CustomDialog.Builder(context);
							builder.setTitle(R.string.main_alert_message_logout)
									.setPositiveButton(
											R.string.alert_message_cancel,
											new OnClickListener() {
												@Override
												public void onClick(
														DialogInterface dialog,
														int which) {
													dialog.dismiss();

												}
											})
									.setNegativeButton(
											R.string.alert_message_ok,
											new OnClickListener() {
												@Override
												public void onClick(
														DialogInterface dialog,
														int which) {
													dialog.dismiss();

													user.setLandStatus(0);
													user.setCityString(null);
													user.setFace(null);
													user.setNickName(null);
													user.setGender(-1);
													user.setProvinceString(null);
													user.setTokenString(null);
													user.setIDCard(null);
													user.setRndCodeString(null);
													user.setUID(0);
													user.setBirthDate(null);

													SharedPreferencesHelper sph = new SharedPreferencesHelper(
															context, "config");
													sph.putInt("loginType", 0);

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
						}
					});

			fourmNavigationBar.setButtonDisplay(true);
			fourmNavigationBar.setButtonText(R.string.fourm_btn_create);
			fourmNavigationBar
					.setOnClickButtonListener(new OnClickButtonListener() {
						@Override
						public void onClick() {
							Intent intent = new Intent();
							intent.setClass(context, FourmEditActivity.class);
							startActivity(intent);
							overridePendingTransition(R.anim.in_from_right,
									R.anim.out_to_left);
						}
					});
		} else {
			guestLayout.setVisibility(View.VISIBLE);
			userLayout.setVisibility(View.GONE);
			menu.setDisplay("family", false);
			menu.setDisplay("changeinfo", false);
			menu.setDisplay("mail", false);
			menu.setDisplay("tag", false);
			menu.setDisplay("changepass", false);
			personNavigationBar.setButtonDisplay(false);
			camarImageView.setImageDrawable(null);
		}

		if (user.getBirthDate() == null) {
			SharedPreferencesHelper sph = new SharedPreferencesHelper(context,
					"config");
			String[] ageStrings = getResources().getStringArray(
					R.array.select_age_tip);

			menu.setValue("age",
					ageStrings[Integer.parseInt(sph.getString("age"))]);
		}

		super.onResume();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		System.err
				.println(resultCode + "  " + requestCode + "  " + data == null);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case RESULT_LOAD_CAMRA:
				Intent intent = new Intent();
				intent.setClass(context, ClipImageActivity.class);
				intent.putExtra("imageFile", Config.APP_LOCAL_PATH
						+ "/tmp/image.jpg");
				startActivityForResult(intent, RESULT_LOAD_CLIP);
				overridePendingTransition(R.anim.in_from_right,
						R.anim.out_to_left);
				break;

			case RESULT_LOAD_IMAGE:
				if (null == data)
					break;
				Uri selectedImage = data.getData();
				String[] filePathColumn = { MediaStore.Images.Media.DATA };

				Cursor cursor = getContentResolver().query(selectedImage,
						filePathColumn, null, null, null);
				cursor.moveToFirst();

				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				String picturePath = cursor.getString(columnIndex);
				cursor.close();

				intent = new Intent();
				intent.setClass(context, ClipImageActivity.class);
				intent.putExtra("imageFile", picturePath);
				startActivityForResult(intent, RESULT_LOAD_CLIP);
				overridePendingTransition(R.anim.in_from_right,
						R.anim.out_to_left);
				break;

			case RESULT_LOAD_CLIP:
				new Thread(new Runnable() {
					@Override
					public void run() {
						faceHandler.obtainMessage(WHAT_LOADING).sendToTarget();

						File file = new File(data.getExtras()
								.getString("image"));
						if (file == null) {
							faceHandler.obtainMessage(
									WHAT_ERROR,
									getResources().getString(
											R.string.toast_face_fail))
									.sendToTarget();
							return;
						}

						FormFile formfile = new FormFile(file.getName(), file,
								"image", "application/octet-stream");
						HashMap<String, String> params = new HashMap<String, String>();
						params.put("u", user.getUID() + "");
						params.put("rc", user.getRndCodeString());

						String httpString = null;
						try {
							httpString = Functions.uploadPost(Config.APIURL
									+ "modFace", params, formfile);
						} catch (Exception e) {
							e.printStackTrace();
						}

						if (httpString == null) {
							System.out.println("httpString null");
							faceHandler.obtainMessage(
									WHAT_ERROR,
									getResources().getString(
											R.string.toast_face_fail))
									.sendToTarget();
							return;
						}

						System.out.println(httpString);
						JSONTokener jsonParser = new JSONTokener(httpString);
						JSONObject person;
						try {
							person = (JSONObject) jsonParser.nextValue();
						} catch (JSONException e) {
							e.printStackTrace();

							faceHandler.obtainMessage(
									WHAT_ERROR,
									getResources().getString(
											R.string.toast_face_fail))
									.sendToTarget();
							return;
						}

						int result = person.optInt("result");
						String reason = person.optString("reason");

						if (result == 0) {
							faceHandler.obtainMessage(WHAT_ERROR, reason)
									.sendToTarget();
							return;
						}

						file.renameTo(new File(Config.APP_LOCAL_PATH + "/face/"
								+ user.getUID() + ".jpg"));
						Bitmap bitmap = Functions
								.getLoacalBitmap(Config.APP_LOCAL_PATH
										+ "/face/" + user.getUID() + ".jpg");
						user.setFace(bitmap);
						faceHandler.obtainMessage(WHAT_FINISH, reason)
								.sendToTarget();
					};
				}).start();

				break;

			default:
				break;
			}
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if ((System.currentTimeMillis() - mExitTime) > 2000) {
				Toast.makeText(this, R.string.app_toast_exit,
						Toast.LENGTH_SHORT).show();
				mExitTime = System.currentTimeMillis();

			} else {
				finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
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
				System.out.println("shua ����");
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

	// TODO �̳�Handler
	private Handler slideHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case WHAT_SLIDE_FINISH:
				slider.SetImages((Bitmap[]) msg.obj);
				break;

			default:
				break;
			}
		}
	};

	private Handler faceHandler = new Handler() {
		CustomProgressDialog progressDialog;
		CustomProgressDialog.Builder builder;

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case WHAT_LOADING:
				builder = new CustomProgressDialog.Builder(context);
				builder.setMessage(R.string.alert_message_image_uploading);
				progressDialog = builder.create();
				progressDialog.show();
				break;

			case WHAT_ERROR:
				Toast.makeText(context, (String) msg.obj, Toast.LENGTH_SHORT)
						.show();
				progressDialog.dismiss();
				break;

			case WHAT_FINISH:
				faceImageView.setImageBitmap(user.getFace());
				progressDialog.dismiss();
				Toast.makeText(context, (String) msg.obj, Toast.LENGTH_SHORT)
						.show();
				break;

			default:
				break;

			}

		}

	};

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		HashMap<String, Object> map = fourmArrayList.get(position);
		Intent intent = new Intent();
		intent.setClass(context, FourmActivity.class);
		intent.putExtra("id", map.get("id") + "");
		intent.putExtra("title", (String) map.get("title"));
		startActivity(intent);
		overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
	}

	@Override
	public void onRefresh() {
		loadFourmData(true);
	}

	@Override
	public void onLoadMore() {

	}
	
	private void loadFourmData(final boolean refresh) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("d", Functions
						.getDeviceId(MainActivity.this)));
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
