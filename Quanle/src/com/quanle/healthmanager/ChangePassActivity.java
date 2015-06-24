package com.quanle.healthmanager;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.quanle.healthmanager.R;
import com.quanle.healthmanager.utils.Config;
import com.quanle.healthmanager.utils.Functions;
import com.quanle.healthmanager.utils.User;
import com.quanle.healthmanager.utils.SharedPreferencesHelper;
import com.quanle.healthmanager.widget.NavigationBar;
import com.quanle.healthmanager.widget.NavigationBar.OnClickBackListener;

public class ChangePassActivity extends Activity {
	private String TAG = "ChgpassActivity";
	private Handler handler;
	private RegThread regThread;
	private Message msg;
	private Functions funcs = new Functions();
	private Context context = ChangePassActivity.this;
	private User user;

	private final int DISCONNECT_INTERNET = 400;
	private final int DISCONNECT_SERVER = 401;
	private final int USRPASS_NULL = 303;
	private final int USRPASS_DIFF = 304;
	private final int USRPASS_LENGTH = 302;
	private final int OLDUSRPASS_NULL = 305;
	private final int OTHER_ERROR = 999;
	private final int REG_FINISH = 100;

	private EditText etUsrPass;
	private EditText etUsrPass1;
	private EditText etUsrPass2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chgpass);

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
					finish();
					overridePendingTransition(R.anim.in_from_left,
							R.anim.out_to_right);
					break;
				}
			}
		};

		NavigationBar nvbar = (NavigationBar) findViewById(R.id.navbar);
		nvbar.setText(R.string.main_menu_changepassword);
		nvbar.setOnClickBackListener(new OnClickBackListener() {
			@Override
			public void onClick() {
				finish();
				overridePendingTransition(R.anim.in_from_left,
						R.anim.out_to_right);
			}
		});

		etUsrPass = (EditText) findViewById(R.id.userNameEditText);
		etUsrPass1 = (EditText) findViewById(R.id.etPass1);
		etUsrPass2 = (EditText) findViewById(R.id.etPass2);

		Button btnLogin = (Button) findViewById(R.id.btnLogin);
		btnLogin.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				regThread = new RegThread();
				regThread.start();
			}
		});
	}

	private class RegThread extends Thread {
		public void run() {
			if (!funcs.checkInternet(context)) {
				msg = new Message();
				msg.what = DISCONNECT_INTERNET;
				handler.sendMessage(msg);
				return;
			}

			String strUsrPass = etUsrPass.getText().toString().trim();
			String strUsrPass1 = etUsrPass1.getText().toString().trim();
			String strUsrPass2 = etUsrPass2.getText().toString().trim();

			if (strUsrPass.equals("")) {
				msg = new Message();
				msg.what = OLDUSRPASS_NULL;
				handler.sendMessage(msg);
				return;
			}

			if (strUsrPass1.equals("")) {
				msg = new Message();
				msg.what = USRPASS_NULL;
				handler.sendMessage(msg);
				return;
			} else if (strUsrPass1.length() < 6 || strUsrPass1.length() > 18) {
				msg = new Message();
				msg.what = USRPASS_LENGTH;
				handler.sendMessage(msg);
				return;
			}

			if (!strUsrPass1.equals(strUsrPass2)) {
				msg = new Message();
				msg.what = USRPASS_DIFF;
				handler.sendMessage(msg);
				return;
			}

			strUsrPass = funcs.stringToMD5(strUsrPass);
			strUsrPass1 = funcs.stringToMD5(strUsrPass1);
			List<NameValuePair> params = new ArrayList<NameValuePair>();

			params.add(new BasicNameValuePair("d", funcs.getDeviceId(context)));
			params.add(new BasicNameValuePair("c", funcs.getCPUSerial()));
			params.add(new BasicNameValuePair("u", user.getUID() + ""));
			params.add(new BasicNameValuePair("rc", user.getRndCodeString()));
			params.add(new BasicNameValuePair("r", strUsrPass));
			params.add(new BasicNameValuePair("p", strUsrPass1));

			String httpString = funcs.getHttpResponse(
					Config.APIURL + "modPass", params);
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
				System.out.println(httpString);
			}

			if (result == 0) {
				msg = new Message();
				msg.what = OTHER_ERROR;
				msg.obj = reason;
				handler.sendMessage(msg);
				return;
			}

			SharedPreferencesHelper sph = new SharedPreferencesHelper(context,
					"config");
			sph.putString("usrpass", strUsrPass1);

			msg = new Message();
			msg.what = REG_FINISH;
			msg.obj = reason;
			handler.sendMessage(msg);
		}
	}
}
