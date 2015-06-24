package com.quanle.healthmanager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
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
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.quanle.healthmanager.utils.Config;
import com.quanle.healthmanager.utils.DisplayUtil;
import com.quanle.healthmanager.utils.Functions;
import com.quanle.healthmanager.utils.NumericWheelAdapter;
import com.quanle.healthmanager.utils.User;
import com.quanle.healthmanager.widget.Menu;
import com.quanle.healthmanager.widget.Menu.OnMenuClickListener;
import com.quanle.healthmanager.widget.NavigationBar;
import com.quanle.healthmanager.widget.NavigationBar.OnClickBackListener;
import com.quanle.healthmanager.widget.WheelView;
import com.quanle.healthmanager.widget.WheelView.OnWheelChangedListener;

public class ChangeInfoActivity extends Activity {
	private Handler handler;
	private RegThread regThread;
	private Message msg;
	private Context context = ChangeInfoActivity.this;
	private User user;

	private final int DISCONNECT_INTERNET = 400;
	private final int DISCONNECT_SERVER = 401;
	private final int USRPASS_NULL = 303;
	private final int USRPASS_DIFF = 304;
	private final int USRPASS_LENGTH = 302;
	private final int OLDUSRPASS_NULL = 305;
	private final int OTHER_ERROR = 999;
	private final int REG_FINISH = 100;

	private Menu menu;
	private PopupWindow popWin;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chginfo);

		user = (User) getApplicationContext();

		handler = new Handler() {
			AlertDialog.Builder builder;
			AlertDialog alertDialog;

			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case DISCONNECT_INTERNET:
					Toast.makeText(context,
							R.string.alert_message_disconnection_network,
							Toast.LENGTH_SHORT).show();
					break;

				case DISCONNECT_SERVER:
					Toast.makeText(context,
							R.string.alert_message_disconnection_server,
							Toast.LENGTH_SHORT).show();
					break;

				case OLDUSRPASS_NULL:
					Toast.makeText(context,
							R.string.changepass_toast_usrpass_null,
							Toast.LENGTH_SHORT).show();
					break;

				case USRPASS_NULL:
					Toast.makeText(context,
							R.string.changepass_toast_usrpass1_null,
							Toast.LENGTH_SHORT).show();
					break;

				case USRPASS_LENGTH:
					Toast.makeText(context,
							R.string.changepass_toast_usrpass1_length,
							Toast.LENGTH_SHORT).show();
					break;

				case USRPASS_DIFF:
					Toast.makeText(context,
							R.string.changepass_toast_usrpass_different,
							Toast.LENGTH_SHORT).show();
					break;

				case OTHER_ERROR:
					Toast.makeText(context, (String) msg.obj,
							Toast.LENGTH_SHORT).show();
					break;

				case REG_FINISH:
					Toast.makeText(context, (String) msg.obj,
							Toast.LENGTH_SHORT).show();
					break;
				}
			}
		};

		NavigationBar nvbar = (NavigationBar) findViewById(R.id.navbar);
		nvbar.setText(R.string.main_menu_changeinfo);
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
		map.put("tag", "username");
		map.put("title", getResources().getString(R.string.changeinfo_usrname));
		map.put("value", user.getNickName());
		map.put("valueType", Menu.EDITTEXT);
		map.put("editTextMaxLength", 5);
		subMenuArrayList.add(map);

		String gender = null;
		map = new HashMap<String, Object>();
		map.put("tag", "gender");
		map.put("title", getResources().getString(R.string.changeinfo_gender));
		switch (user.getGender()) {
		case 0:
			gender = getResources().getString(R.string.select_sex_boy);
			break;
		case 1:
			gender = getResources().getString(R.string.select_sex_girl);
			break;
		default:
			gender = getResources().getString(R.string.select_sex_title);
			break;
		}
		map.put("value", gender);
		map.put("valueAlign", Menu.VALUEALIGN_LEFT);
		subMenuArrayList.add(map);

		map = new HashMap<String, Object>();
		map.put("tag", "idcard");
		map.put("title", getResources().getString(R.string.changeinfo_idcard));
		map.put("value", user.getIDCard());
		map.put("valueType", Menu.EDITTEXT);
		map.put("editTextDigits", "0123456789x");
		map.put("editTextMaxLength", 18);
		map.put("editTextInputType", InputType.TYPE_NUMBER_FLAG_SIGNED);
		subMenuArrayList.add(map);

		map = new HashMap<String, Object>();
		map.put("tag", "birth");
		map.put("title", getResources().getString(R.string.changeinfo_birth));
		Date birth = user.getBirthDate();
		if (birth == null) {
			map.put("value",
					getResources().getString(
							R.string.changeinfo_toast_birth_null));
		} else {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(birth);
			map.put("value", String.format(
					getResources().getString(R.string.date_format),
					calendar.get(Calendar.YEAR),
					(calendar.get(Calendar.MONTH) + 1),
					(calendar.get(Calendar.DATE))));
		}
		map.put("valueAlign", Menu.VALUEALIGN_LEFT);
		subMenuArrayList.add(map);

		menuArrayList.add(subMenuArrayList);

		menu = (Menu) findViewById(R.id.menu);
		menu.setMenu(menuArrayList);
		menu.setTitleWidth(80);
		menu.setOnMenuClickListener(new OnMenuClickListener() {
			public void onClick(String tagString) {
				if (tagString == "gender") {
					((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
							.hideSoftInputFromWindow(ChangeInfoActivity.this
									.getCurrentFocus().getWindowToken(),
									InputMethodManager.HIDE_NOT_ALWAYS);

					LayoutInflater mLayoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
					View menuView = mLayoutInflater.inflate(
							R.layout.popupwindow_gender, null, false);
					popWin = new PopupWindow(menuView,
							LayoutParams.MATCH_PARENT,
							LayoutParams.WRAP_CONTENT, true);
					popWin.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.popupwindow_bg));
					popWin.setAnimationStyle(R.style.popupAnimation);
					popWin.showAtLocation(menu, Gravity.BOTTOM, 0, 0);
					popWin.update();
					LinearLayout layoutAge = (LinearLayout) menuView
							.findViewById(R.id.layoutAge);
					((LinearLayout) layoutAge.getChildAt(0))
							.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									menu.setValue("gender", getResources()
											.getString(R.string.select_sex_boy));
									popWin.dismiss();
								}
							});
					((LinearLayout) layoutAge.getChildAt(1))
							.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									menu.setValue(
											"gender",
											getResources().getString(
													R.string.select_sex_girl));
									popWin.dismiss();
								}
							});

				} else if (tagString == "birth") {
					((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
							.hideSoftInputFromWindow(ChangeInfoActivity.this
									.getCurrentFocus().getWindowToken(),
									InputMethodManager.HIDE_NOT_ALWAYS);

					LayoutInflater mLayoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
					View menuView = mLayoutInflater.inflate(
							R.layout.popupwindow_date, null, false);
					popWin = new PopupWindow(menuView,
							LayoutParams.MATCH_PARENT,
							LayoutParams.WRAP_CONTENT, true);
					popWin.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.popupwindow_bg));
					popWin.setAnimationStyle(R.style.popupAnimation);
					popWin.showAtLocation(menu, Gravity.BOTTOM, 0, 0);
					popWin.update();

					Calendar defCalendar = Calendar.getInstance();

					try {
						SimpleDateFormat sdf = new SimpleDateFormat(String
								.format(getResources().getString(
										R.string.date_format), "yyyy", "MM",
										"dd"));
						Date date = sdf.parse(menu.getValue("birth"));
						defCalendar.setTime(date);
					} catch (ParseException e) {
						e.printStackTrace();
					}

					int year = defCalendar.get(Calendar.YEAR);
					int month = defCalendar.get(Calendar.MONTH);
					int day = defCalendar.get(Calendar.DATE);
					// 添加大小月月份并将其转换为list,方便之后的判断
					String[] months_big = { "1", "3", "5", "7", "8", "10", "12" };
					String[] months_little = { "4", "6", "9", "11" };

					final List<String> list_big = Arrays.asList(months_big);
					final List<String> list_little = Arrays
							.asList(months_little);

					Calendar calendar = Calendar.getInstance();
					final int START_YEAR = 1900;
					int END_YEAR = calendar.get(Calendar.YEAR);
					// 年
					final WheelView yearWheelView = (WheelView) menuView
							.findViewById(R.id.year);
					yearWheelView.setAdapter(new NumericWheelAdapter(
							START_YEAR, END_YEAR));// 设置"年"的显示数据
					yearWheelView.setCyclic(false);// 可循环滚动
					yearWheelView.setLabel(R.string.date_year);// 添加文字
					yearWheelView.setCurrentItem(year - START_YEAR);// 初始化时显示的数据

					// 月
					final WheelView monthWheelView = (WheelView) menuView
							.findViewById(R.id.month);
					monthWheelView.setAdapter(new NumericWheelAdapter(1, 12));
					monthWheelView.setCyclic(false);
					monthWheelView.setLabel(R.string.date_mouth);
					monthWheelView.setCurrentItem(month);

					// 日
					final WheelView dayWheelView = (WheelView) menuView
							.findViewById(R.id.day);
					dayWheelView.setCyclic(false);
					// 判断大小月及是否闰年,用来确定"日"的数据
					if (list_big.contains(String.valueOf(month + 1))) {
						dayWheelView.setAdapter(new NumericWheelAdapter(1, 31));
					} else if (list_little.contains(String.valueOf(month + 1))) {
						dayWheelView.setAdapter(new NumericWheelAdapter(1, 30));
					} else {
						// 闰年
						if ((year % 4 == 0 && year % 100 != 0)
								|| year % 400 == 0)
							dayWheelView.setAdapter(new NumericWheelAdapter(1,
									29));
						else
							dayWheelView.setAdapter(new NumericWheelAdapter(1,
									28));
					}
					dayWheelView.setLabel(R.string.date_day);
					dayWheelView.setCurrentItem(day - 1);

					// 添加"年"监听
					OnWheelChangedListener wheelListener_year = new OnWheelChangedListener() {
						public void onChanged(WheelView wheel, int oldValue,
								int newValue) {
							int year_num = newValue + START_YEAR;
							// 判断大小月及是否闰年,用来确定"日"的数据
							if (list_big.contains(String.valueOf(monthWheelView
									.getCurrentItem() + 1))) {
								dayWheelView
										.setAdapter(new NumericWheelAdapter(1,
												31));
							} else if (list_little.contains(String
									.valueOf(monthWheelView.getCurrentItem() + 1))) {
								dayWheelView
										.setAdapter(new NumericWheelAdapter(1,
												30));
							} else {
								if ((year_num % 4 == 0 && year_num % 100 != 0)
										|| year_num % 400 == 0)
									dayWheelView
											.setAdapter(new NumericWheelAdapter(
													1, 29));
								else
									dayWheelView
											.setAdapter(new NumericWheelAdapter(
													1, 28));
							}
						}
					};
					// 添加"月"监听
					OnWheelChangedListener wheelListener_month = new OnWheelChangedListener() {
						public void onChanged(WheelView wheel, int oldValue,
								int newValue) {
							int month_num = newValue + 1;
							// 判断大小月及是否闰年,用来确定"日"的数据
							if (list_big.contains(String.valueOf(month_num))) {
								dayWheelView
										.setAdapter(new NumericWheelAdapter(1,
												31));
							} else if (list_little.contains(String
									.valueOf(month_num))) {
								dayWheelView
										.setAdapter(new NumericWheelAdapter(1,
												30));
							} else {
								if (((yearWheelView.getCurrentItem() + START_YEAR) % 4 == 0 && (yearWheelView
										.getCurrentItem() + START_YEAR) % 100 != 0)
										|| (yearWheelView.getCurrentItem() + START_YEAR) % 400 == 0)
									dayWheelView
											.setAdapter(new NumericWheelAdapter(
													1, 29));
								else
									dayWheelView
											.setAdapter(new NumericWheelAdapter(
													1, 28));
							}
						}
					};
					yearWheelView.addChangingListener(wheelListener_year);
					monthWheelView.addChangingListener(wheelListener_month);
					dayWheelView.TEXT_SIZE = DisplayUtil.dip2px(context, 16);
					monthWheelView.TEXT_SIZE = DisplayUtil.dip2px(context, 16);
					yearWheelView.TEXT_SIZE = DisplayUtil.dip2px(context, 16);

					((Button) menuView.findViewById(R.id.btnOk))
							.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									menu.setValue(
											"birth",
											String.format(
													getResources()
															.getString(
																	R.string.date_format),
													(yearWheelView
															.getCurrentItem() + START_YEAR),
													(monthWheelView
															.getCurrentItem() + 1),
													(dayWheelView
															.getCurrentItem() + 1)));
									popWin.dismiss();
								}
							});

					((Button) menuView.findViewById(R.id.btnCancel))
							.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									popWin.dismiss();
								}
							});

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

		Button submitButton = (Button) findViewById(R.id.submitButton);
		submitButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String usrNameString = menu.getValue("username").trim();
				String genderString = menu.getValue("gender").trim();
				String idCardString = menu.getValue("idcard").trim();
				String birthString = menu.getValue("birth").trim();

				if (usrNameString.equals("")) {
					Toast.makeText(context,
							R.string.changeinfo_toast_usrname_null,
							Toast.LENGTH_SHORT).show();
					return;
				}

				if (!genderString.equals(getResources().getString(
						R.string.select_sex_boy))
						&& !genderString.equals(getResources().getString(
								R.string.select_sex_girl))) {
					Toast.makeText(context,
							R.string.changeinfo_toast_gender_null,
							Toast.LENGTH_SHORT).show();
					return;
				}

				if (idCardString.equals("")) {
					Toast.makeText(context,
							R.string.changeinfo_toast_idcard_null,
							Toast.LENGTH_SHORT).show();
					return;
				} else if (!Functions.isIDCardValid(idCardString)) {
					Toast.makeText(context,
							R.string.changeinfo_toast_idcard_format,
							Toast.LENGTH_SHORT).show();
					return;
				}

				try {
					SimpleDateFormat sdf = new SimpleDateFormat(String.format(
							getResources().getString(R.string.date_format),
							"yyyy", "MM", "dd"));
					sdf.parse(birthString);
				} catch (ParseException e) {
					e.printStackTrace();
					Toast.makeText(context,
							R.string.changeinfo_toast_birth_null,
							Toast.LENGTH_SHORT).show();
					return;
				}

				regThread = new RegThread();
				regThread.start();
			}
		});
	}

	private class RegThread extends Thread {
		public void run() {
			if (!Functions.checkInternet(context)) {
				msg = new Message();
				msg.what = DISCONNECT_INTERNET;
				handler.sendMessage(msg);
				return;
			}

			String usrNameString = menu.getValue("username").trim();
			String genderString = menu.getValue("gender").trim();
			String idCardString = menu.getValue("idcard").trim();
			String birthString = menu.getValue("birth").trim();

			Date date = null;
			try {
				Calendar defCalendar = Calendar.getInstance();
				SimpleDateFormat sdf = new SimpleDateFormat(String.format(
						getResources().getString(R.string.date_format), "yyyy",
						"MM", "dd"));
				date = sdf.parse(birthString);
				defCalendar.setTime(date);
				birthString = defCalendar.get(Calendar.YEAR) + "-"
						+ (defCalendar.get(Calendar.MONTH) + 1) + "-"
						+ defCalendar.get(Calendar.DATE);
			} catch (ParseException e) {
				e.printStackTrace();
				msg = new Message();
				msg.what = OTHER_ERROR;
				msg.obj = getResources()
						.getString(R.string.app_toast_unknowerr);
				handler.sendMessage(msg);
				return;
			}

			List<NameValuePair> params = new ArrayList<NameValuePair>();

			params.add(new BasicNameValuePair("d", Functions
					.getDeviceId(context)));
			params.add(new BasicNameValuePair("c", Functions.getCPUSerial()));
			params.add(new BasicNameValuePair("u", user.getUID() + ""));
			params.add(new BasicNameValuePair("rc", user.getRndCodeString()));
			params.add(new BasicNameValuePair("n", usrNameString));
			params.add(new BasicNameValuePair("b", birthString));
			params.add(new BasicNameValuePair("g", genderString));
			params.add(new BasicNameValuePair("i", idCardString));

			String httpString = Functions.getHttpResponse(Config.APIURL
					+ "modInfo", params);
			if (httpString == null) {
				System.out.println(params.toString());
				msg = new Message();
				msg.what = DISCONNECT_SERVER;
				handler.sendMessage(msg);
				return;
			}

			int result = 0;
			String reason = "";

			try {
				JSONTokener jsonParser = new JSONTokener(httpString);
				JSONObject person = (JSONObject) jsonParser.nextValue();
				result = person.getInt("result");
				reason = person.getString("reason");
			} catch (JSONException e) {
				e.printStackTrace();
			}

			if (result == 0) {
				msg = new Message();
				msg.what = OTHER_ERROR;
				msg.obj = reason;
				handler.sendMessage(msg);
				return;
			}

			user.setBirthDate(date);
			System.out.println("set date = " + date.toString());
			user.setGender(genderString.equals(getResources().getString(
					R.string.select_sex_boy)) ? 0 : 1);
			user.setIDCard(idCardString);
			user.setNickName(usrNameString);

			msg = new Message();
			msg.what = REG_FINISH;
			msg.obj = reason;
			handler.sendMessage(msg);
		}
	}
}
