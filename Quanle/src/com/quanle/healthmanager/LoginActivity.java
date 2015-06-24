package com.quanle.healthmanager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.quanle.healthmanager.utils.Config;
import com.quanle.healthmanager.utils.Functions;
import com.quanle.healthmanager.utils.SharedPreferencesHelper;
import com.quanle.healthmanager.utils.User;
import com.quanle.healthmanager.widget.NavigationBar;
import com.quanle.healthmanager.widget.NavigationBar.OnClickBackListener;
import com.quanle.healthmanager.widget.QuickLoginLayout;
import com.quanle.healthmanager.widget.QuickLoginLayout.OnLoginFinishListener;

public class LoginActivity extends Activity {
	private Handler handler;
	private QuickLoginThread quickLoginThread;
	private LoginThread loginThread;
	private Message msg;
	private Functions funcs = new Functions();
	private Context context = LoginActivity.this;

	private final int DISCONNECT_INTERNET = 400;
	private final int DISCONNECT_SERVER = 401;
	private final int USRID_NULL = 300;
	private final int USRID_FORMAT = 301;
	private final int USRPASS_NULL = 303;
	private final int USRPASS_LENGTH = 302;
	private final int USRPASS_DIFF = 304;
	private final int USRNAME_NULL = 305;
	private final int OTHER_ERROR = 999;
	private final int REG_FINISH = 100;
	private final int LOADING = 1;

	private EditText etUsrID;
	private EditText etUsrPass;
	private EditText etUsrPass2;

	private String tokenString;
	private String nickString;
	private String faceString;
	private String cityString;
	private String provinceString;
	private int genderInt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		handler = new Handler() {
			Dialog loadingDialog;

			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case LOADING:
					LayoutInflater inflater = LayoutInflater.from(context);
					View v = inflater.inflate(R.layout.widget_progressdialog,
							null);// �õ�����view
					LinearLayout layout = (LinearLayout) v
							.findViewById(R.id.dialog_view);// ���ز���

					loadingDialog = new Dialog(context, R.style.progressDialog);// �����Զ�����ʽdialog
					TextView tipTextView = (TextView) v
							.findViewById(R.id.tipTextView);// ��ʾ����
					tipTextView.setText(R.string.login_loading);
					loadingDialog.setCancelable(false);// �������á����ؼ���ȡ��
					loadingDialog.setContentView(layout,
							new LinearLayout.LayoutParams(
									LinearLayout.LayoutParams.MATCH_PARENT,
									LinearLayout.LayoutParams.MATCH_PARENT));// ���ò���
					loadingDialog.show();
					break;

				case DISCONNECT_INTERNET:
					if (loadingDialog != null)
						loadingDialog.dismiss();

					Toast.makeText(context,
							R.string.alert_message_disconnection_network,
							Toast.LENGTH_SHORT).show();
					break;

				case DISCONNECT_SERVER:
					if (loadingDialog != null)
						loadingDialog.dismiss();

					Toast.makeText(context,
							R.string.alert_message_disconnection_server,
							Toast.LENGTH_SHORT).show();
					break;

				case USRID_NULL:
					Toast.makeText(context, R.string.login_toast_usrid_null,
							Toast.LENGTH_SHORT).show();
					break;

				case USRPASS_NULL:
					Toast.makeText(context, R.string.login_toast_usrpass_null,
							Toast.LENGTH_SHORT).show();
					break;

				case USRID_FORMAT:
					Toast.makeText(context, R.string.reg_toast_usrid_format,
							Toast.LENGTH_SHORT).show();
					break;

				case OTHER_ERROR:
					if (loadingDialog != null)
						loadingDialog.dismiss();

					Toast.makeText(context, (String) msg.obj,
							Toast.LENGTH_SHORT).show();
					break;

				case REG_FINISH:
					if (loadingDialog != null)
						loadingDialog.dismiss();

					Toast.makeText(context, (String) msg.obj,
							Toast.LENGTH_SHORT).show();

					finish();
					overridePendingTransition(R.anim.in_from_left,
							R.anim.out_to_right);
					break;
				}
			}
		};

		NavigationBar nvbar = (NavigationBar) findViewById(R.id.navbar);
		nvbar.setText(R.string.login_title);
		nvbar.setOnClickBackListener(new OnClickBackListener() {
			@Override
			public void onClick() {
				finish();
				overridePendingTransition(R.anim.in_from_left,
						R.anim.out_to_right);
			}
		});

		TextView regTextView = (TextView) findViewById(R.id.regTextView);
		regTextView.setClickable(true);
		regTextView.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(context, RegActivity.class);
				startActivity(intent);
				finish();
				overridePendingTransition(R.anim.in_from_left,
						R.anim.out_to_right);
			}
		});

		etUsrID = (EditText) findViewById(R.id.etUsrID);
		etUsrPass = (EditText) findViewById(R.id.userNameEditText);
		etUsrPass2 = (EditText) findViewById(R.id.etPass2);

		Button btnLogin = (Button) findViewById(R.id.btnLogin);
		btnLogin.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				loginThread = new LoginThread();
				loginThread.start();
			}
		});

		QuickLoginLayout quickLoginLayout = (QuickLoginLayout) findViewById(R.id.quickLoginLayout);
		quickLoginLayout.setOnLoginFinishListener(new OnLoginFinishListener() {
			@Override
			public void onError() {

			}

			@Override
			public void onComplete(JSONObject json, String token) {

				nickString = json.optString("nickname");
				tokenString = token;
				faceString = json.optString("figureurl_qq_2");
				cityString = json.optString("city");
				provinceString = json.optString("province");
				String genderString = json.optString("gender");
				if (genderString.equals("男"))
					genderInt = 0;
				else if (genderString.equals("女"))
					genderInt = 1;
				else
					genderInt = -1;

				SharedPreferencesHelper sph = new SharedPreferencesHelper(
						context, "config");
				sph.putInt("loginType", 2);
				sph.putString("token", tokenString);

				quickLoginThread = new QuickLoginThread();
				quickLoginThread.start();
			}
		});
	}

	private class LoginThread extends Thread {
		public void run() {
			String strUsrid = etUsrID.getText().toString().trim();
			String strUsrPass = etUsrPass.getText().toString().trim();
			// int sexInt = 0;
			// if (rdSexFamale.isChecked())
			// sexInt = 1;

			if (strUsrid.equals("")) {
				msg = new Message();
				msg.what = USRID_NULL;
				handler.sendMessage(msg);
				return;
			} else if (!strUsrid.matches("\\w+@\\w+\\.\\w+")) {
				msg = new Message();
				msg.what = USRID_FORMAT;
				handler.sendMessage(msg);
				return;
			}

			if (strUsrPass.equals("")) {
				msg = new Message();
				msg.what = USRPASS_NULL;
				handler.sendMessage(msg);
				return;
			} else if (strUsrPass.length() < 6 || strUsrPass.length() > 18) {
				msg = new Message();
				msg.what = USRPASS_LENGTH;
				handler.sendMessage(msg);
				return;
			}

			msg = new Message();
			msg.what = LOADING;
			handler.sendMessage(msg);

			if (!funcs.checkInternet(context)) {
				msg = new Message();
				msg.what = DISCONNECT_INTERNET;
				handler.sendMessage(msg);
				return;
			}

			List<NameValuePair> params = new ArrayList<NameValuePair>();

			params.add(new BasicNameValuePair("d", funcs.getDeviceId(context)));
			params.add(new BasicNameValuePair("c", funcs.getCPUSerial()));
			params.add(new BasicNameValuePair("u", strUsrid));
			params.add(new BasicNameValuePair("p", Functions
					.stringToMD5(strUsrPass)));
			params.add(new BasicNameValuePair("t", "0"));

			String httpString = funcs.getHttpResponse(Config.APIURL + "login",
					params);

			if (httpString == null) {
				System.out.println(params.toString());
				msg = new Message();
				msg.what = DISCONNECT_SERVER;
				handler.sendMessage(msg);
				return;
			}

			JSONTokener jsonParser = new JSONTokener(httpString);
			JSONObject person;
			try {
				person = (JSONObject) jsonParser.nextValue();
			} catch (JSONException e) {
				e.printStackTrace();
				System.out.println(httpString);
				msg = new Message();
				msg.what = DISCONNECT_SERVER;
				handler.sendMessage(msg);
				return;
			}

			int result = person.optInt("result");
			String reason = person.optString("reason");

			if (result == 0) {
				msg = new Message();
				msg.what = OTHER_ERROR;
				msg.obj = reason;
				handler.sendMessage(msg);
				return;
			}

			User user = ((User) getApplicationContext());
			user.setNickName(person.optString("nickName"));
			user.setLandStatus(User.LANDSTATUS_MAIL);
			user.setMailString(strUsrid);
			user.setUID(person.optInt("uID"));
			user.setRndCodeString(person.optString("rndCode"));
			user.setCityString(person.optString("city"));
			user.setGender(person.optInt("gender"));
			user.setProvinceString(person.optString("province"));
			user.setIDCard(person.optString("idCard"));
			//user.setFace(faceBitmap);

			Date birth = null;
			try {
				SimpleDateFormat sdf = new SimpleDateFormat(String.format(
						getResources().getString(R.string.date_format), "yyyy",
						"MM", "dd"));
				birth = sdf.parse(person.optString("birthDay"));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			user.setBirthDate(birth);
			
			SharedPreferencesHelper sph = new SharedPreferencesHelper(context,
					"config");
			sph.putInt("loginType", 1);
			sph.putString("uid", person.optInt("uID") + "");
			sph.putString("password", Functions.stringToMD5(strUsrPass));

			msg = new Message();
			msg.what = REG_FINISH;
			msg.obj = reason;
			handler.sendMessage(msg);
		}
	}

	private class QuickLoginThread extends Thread {
		public void run() {
			msg = new Message();
			msg.what = LOADING;
			handler.sendMessage(msg);

			if (!funcs.checkInternet(context)) {
				msg = new Message();
				msg.what = DISCONNECT_INTERNET;
				handler.sendMessage(msg);
				return;
			}

			List<NameValuePair> params = new ArrayList<NameValuePair>();

			params.add(new BasicNameValuePair("d", funcs.getDeviceId(context)));
			params.add(new BasicNameValuePair("c", funcs.getCPUSerial()));
			params.add(new BasicNameValuePair("t", tokenString));
			params.add(new BasicNameValuePair("n", nickString));
			params.add(new BasicNameValuePair("f", faceString));
			params.add(new BasicNameValuePair("g", genderInt + ""));
			params.add(new BasicNameValuePair("ci", cityString));
			params.add(new BasicNameValuePair("p", provinceString));
			
			String httpString = funcs.getHttpResponse(Config.APIURL
					+ "regUsrQuick", params);

			if (httpString == null) {
				msg = new Message();
				msg.what = DISCONNECT_SERVER;
				handler.sendMessage(msg);
				return;
			}

			JSONTokener jsonParser = new JSONTokener(httpString);
			JSONObject person;
			try {
				person = (JSONObject) jsonParser.nextValue();
			} catch (JSONException e) {
				e.printStackTrace();

				msg = new Message();
				msg.what = DISCONNECT_SERVER;
				handler.sendMessage(msg);
				return;
			}

			int result = person.optInt("result");
			String reason = person.optString("reason");

			if (result == 0) {
				msg = new Message();
				msg.what = OTHER_ERROR;
				msg.obj = reason;
				handler.sendMessage(msg);
				return;
			}

			Bitmap faceBitmap = Functions.getHttpBitmap(faceString);
			Functions.CreateFolderTree("/sdcard/quanle/health/face/");
			File f = new File("/sdcard/quanle/health/face/", person.optInt("uID")
					+ ".jpg");
			if (!f.exists()) {
				try {
					FileOutputStream out = new FileOutputStream(f);
					faceBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
					out.flush();
					out.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			User user = ((User) getApplicationContext());
			user.setCityString(cityString);
			user.setFace(faceBitmap);
			user.setNickName(nickString);
			user.setGender(genderInt);
			user.setProvinceString(provinceString);
			user.setLandStatus(User.LANDSTATUS_QQ);
			user.setTokenString(tokenString);
			user.setIDCard(person.optString("idCard"));
			user.setRndCodeString(person.optString("rndCode"));
			user.setUID(person.optInt("uID"));
			Date birth = null;
			try {
				SimpleDateFormat sdf = new SimpleDateFormat(String.format(
						getResources().getString(R.string.date_format), "yyyy",
						"MM", "dd"));
				birth = sdf.parse(person.optString("birthDay"));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			user.setBirthDate(birth);

			msg = new Message();
			msg.what = REG_FINISH;
			msg.obj = reason;
			handler.sendMessage(msg);
		}
	}

	// @Override
	// protected void onActivityResult(int requestCode, int resultCode, Intent
	// data) {
	// super.onActivityResult(requestCode, resultCode, data);
	// if (mQQAuth != null)
	// mQQAuth.onActivityResult(requestCode, resultCode, data);
	// }
}
