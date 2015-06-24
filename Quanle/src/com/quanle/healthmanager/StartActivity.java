package com.quanle.healthmanager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.quanle.healthmanager.utils.Config;
import com.quanle.healthmanager.utils.Functions;
import com.quanle.healthmanager.utils.SharedPreferencesHelper;
import com.quanle.healthmanager.utils.User;
import com.quanle.healthmanager.widget.CustomDialog;
import com.quanle.healthmanager.widget.CustomProgressDialog;

public class StartActivity extends Activity {
	private final String TAG = "StartActivity";
	Context context = StartActivity.this;
	private TimerTask task;
	private final Timer timer = new Timer();
	private StartThread startThread;
	private DownloadThread downloadThread;
	private Handler handler;
	private Message msg;
	private boolean autoLogin = false;

	private final int DISCONNECT_INTERNET = 400;
	private final int DISCONNECT_SERVER = 401;
	private final int DOWNLOAD_ERROR = 300;
	private final int DOWNLOAD_PROGRESS = 301;
	private final int GOTO_ACTIVITY = 101;
	private final int NEWVER = 200;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);

		handler = new Handler() {
			CustomDialog.Builder builder;
			CustomDialog alertDialog;
			CustomProgressDialog progressDialog;
			CustomProgressDialog.Builder progressBuilder;

			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case NEWVER:
					final JSONObject jsonObject = (JSONObject) msg.obj;
					String ver = jsonObject.optString("ver");
					builder = new CustomDialog.Builder(context);
					builder.setTitle(
							String.format(
									getResources().getString(
											R.string.alert_message_update), ver))
							.setMessage(jsonObject.optString("reason"))
							.setPositiveButton(R.string.alert_message_cancel,
									new OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {

											dialog.dismiss();

											startThread = new StartThread();
											startThread.start();
										}
									})
							.setNegativeButton(R.string.alert_message_ok,
									new OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											dialog.dismiss();

											progressBuilder = new CustomProgressDialog.Builder(
													context);
											progressBuilder
													.setMessage(R.string.alert_message_update_downloading);
											progressDialog = progressBuilder
													.create();
											progressDialog.show();

											DownloadThread thread = new DownloadThread();
											thread.setUrl(jsonObject
													.optString("url"));
											thread.start();
										}
									});
					alertDialog = builder.create();
					alertDialog.show();
					break;
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

				case DOWNLOAD_ERROR:
					progressDialog.dismiss();

					builder = new CustomDialog.Builder(context);
					builder.setMessage((Integer) msg.obj).setPositiveButton(
							R.string.alert_message_ok, new OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();

									startThread = new StartThread();
									startThread.start();
								}
							});
					alertDialog = builder.create();
					alertDialog.show();
					break;

				case DOWNLOAD_PROGRESS:
					DecimalFormat format = new DecimalFormat("#.##");
					double d = 100 * (Float.parseFloat(msg.arg1 + "") / Float
							.parseFloat(msg.arg2 + ""));
					String p = format.format(d);
					progressBuilder.setMessage(String.format(
							getResources().getString(
									R.string.alert_message_update_downloading),
							Functions.getFormatSize(msg.arg1),
							Functions.getFormatSize(msg.arg2), p));
					break;

				case GOTO_ACTIVITY:
					timer.cancel();

					if (autoLogin)
						Toast.makeText(context,
								R.string.start_autologin_finish,
								Toast.LENGTH_SHORT).show();

					Intent intent = new Intent();
					SharedPreferencesHelper sph = new SharedPreferencesHelper(
							context, "config");
					int isFirstRun = Integer.parseInt(sph.getString("firstRun",
							"1"));
					if (isFirstRun == 1)
						intent.setClass(context, SlideActivity.class);
					else
						intent.setClass(context, NewMainActivity.class);
					startActivity(intent);
					finish();
					overridePendingTransition(R.anim.in_from_right,
							R.anim.out_to_left);
					break;
				}
			}
		};

		// 在新的线程中进行初始化工作
		UpdateThread updateThread = new UpdateThread();
		updateThread.start();

		task = new TimerTask() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				msg = new Message();
				msg.what = GOTO_ACTIVITY;
				handler.sendMessage(msg);
			}
		};

	}

	// 下载线程
	private class DownloadThread extends Thread {
		private String url;

		public void run() {
			// 开始下载文件
			try {
				URL Url = new URL(url);
				URLConnection conn = Url.openConnection();
				conn.connect();
				InputStream is = conn.getInputStream();
				int fileSize = conn.getContentLength();// 根据响应获取文件大小

				if (fileSize <= 0) { // 获取内容长度为0
					msg = new Message();
					msg.what = DOWNLOAD_ERROR;
					msg.obj = R.string.alert_message_update_err_filenull;
					handler.sendMessage(msg);
					return;
				}

				if (is == null) { // 没有下载流
					msg = new Message();
					msg.what = DOWNLOAD_ERROR;
					msg.obj = R.string.alert_message_update_err_filenull;
					handler.sendMessage(msg);
					return;
				}

				Functions.CreateFolderTree("/sdcard/quanle/health/update");

				FileOutputStream FOS = new FileOutputStream(
						"/sdcard/quanle/health/update/quanle.apk.tmp"); // 创建写入文件内存流，

				// 通过此流向目标写文件
				byte buf[] = new byte[1024];
				int downLoadFilePosition = 0;
				int numread;
				while ((numread = is.read(buf)) != -1) {
					FOS.write(buf, 0, numread);
					downLoadFilePosition += numread;
					msg = new Message();
					msg.what = DOWNLOAD_PROGRESS;
					msg.arg1 = downLoadFilePosition;
					msg.arg2 = fileSize;
					handler.sendMessage(msg);
				}

				File f = new File("/sdcard/quanle/health/update/quanle.apk.tmp");
				f.renameTo(new File("/sdcard/quanle/health/update/quanle.apk"));

				Intent intent = new Intent();
				// 设置目标应用安装包路径
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setAction(android.content.Intent.ACTION_VIEW);
				intent.setDataAndType(Uri.fromFile(new File(
						"/sdcard/quanle/health/update/quanle.apk")),
						"application/vnd.android.package-archive");
				startActivity(intent);
				System.exit(0);
			} catch (MalformedURLException e) {
				e.printStackTrace();
				msg = new Message();
				msg.what = DOWNLOAD_ERROR;
				msg.obj = R.string.app_toast_unknowerr;
				handler.sendMessage(msg);

			} catch (IOException e) {
				e.printStackTrace();
				msg = new Message();
				msg.what = DOWNLOAD_ERROR;
				msg.obj = R.string.app_toast_unknowerr;
				handler.sendMessage(msg);

			}

		}

		public void setUrl(String string) {
			this.url = string;
		}
	}

	// 更新线程
	private class UpdateThread extends Thread {
		public void run() {
			String httpString = Functions.getHttpResponse(Config.APIURL
					+ "checkUpdate");

			if (httpString != null) {
				int verCode = 0;
				JSONObject josn = null;
				try {
					JSONTokener jsonParser = new JSONTokener(httpString);
					josn = (JSONObject) jsonParser.nextValue();
					verCode = josn.getInt("verCode");
				} catch (JSONException e) {
					e.printStackTrace();
				}

				int curVersion = Functions
						.getAppVersionName(getApplicationContext());

				if (curVersion < verCode) {
					msg = new Message();
					msg.what = NEWVER;
					msg.obj = josn;
					handler.sendMessage(msg);
					return;
				}
			}

			startThread = new StartThread();
			startThread.start();
		}

	}

	// 启动线程
	private class StartThread extends Thread {
		public void run() {

			List<NameValuePair> params = new ArrayList<NameValuePair>();

			params.add(new BasicNameValuePair("d", Functions
					.getDeviceId(getApplicationContext())));
			params.add(new BasicNameValuePair("c", Functions.getCPUSerial()));

			SharedPreferencesHelper sph = new SharedPreferencesHelper(context,
					"config");
			int loginType = sph.getInt("loginType");
			String defUsrid = sph.getString("uid", "");
			String defPass = sph.getString("password", "");
			String defToken = sph.getString("token", "");

			params.add(new BasicNameValuePair("t", loginType + ""));
			switch (loginType) {
			case 0:
				timer.schedule(task, 3000, 3000);
				return;

			case 1:
				params.add(new BasicNameValuePair("u", defUsrid));
				params.add(new BasicNameValuePair("p", defPass));
				break;

			case 2:
				params.add(new BasicNameValuePair("tk", defToken));
				break;
			}

			String httpString = Functions.getHttpResponse(Config.APIURL
					+ "login", params);

			if (httpString == null) {
				System.out.println(params.toString());
				msg = new Message();
				msg.what = DISCONNECT_SERVER;
				handler.sendMessage(msg);
			} else {

				JSONObject person = null;
				String reason = "";

				try {
					JSONTokener jsonParser = new JSONTokener(httpString);
					person = (JSONObject) jsonParser.nextValue();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (person == null) {
					System.out.println(httpString);
					msg = new Message();
					msg.what = DISCONNECT_SERVER;
					handler.sendMessage(msg);
				} else {

					int result = person.optInt("result", 0);
					if (result == 0) {
						sph.putInt("loginType", 0);

					} else {

						User user = ((User) getApplicationContext());
						user.setCityString(person.optString("city"));
						user.setLandStatus(loginType);
						user.setTokenString(defToken);
						user.setUID(person.optInt("uID"));
						user.setNickName(person.optString("nickName"));
						user.setGender(person.optInt("gender"));
						user.setProvinceString(person.optString("province"));
						user.setRndCodeString(person.optString("rndCode"));

						Date birth = null;
						try {
							SimpleDateFormat sdf = new SimpleDateFormat(
									String.format(
											getResources().getString(
													R.string.date_format),
											"yyyy", "MM", "dd"));
							birth = sdf.parse(person.optString("birthDay"));
						} catch (ParseException e) {
							e.printStackTrace();
						}
						user.setBirthDate(birth);

						user.setIDCard(person.optString("idCard"));

						String faceString = person.optString("face");
						if (Functions.hasSDCard() && faceString != null
								&& !faceString.equals("")) {
							Bitmap faceBitmap = null;
							File f = new File(Config.APP_LOCAL_PATH + "/face/",
									person.optInt("uID") + ".jpg");
							if (!f.exists()) {
								Functions
										.CreateFolderTree(Config.APP_LOCAL_PATH
												+ "/face/");
								faceBitmap = Functions.getHttpBitmap(person
										.optString("face"));
								if (faceBitmap != null) {
									Functions.saveLoaclBitmap(faceBitmap,
											Config.APP_LOCAL_PATH + "/face/"
													+ person.optInt("uID")
													+ ".jpg");
								}
							} else {
								System.out.println("face file exists");
								faceBitmap = Functions
										.getLoacalBitmap(Config.APP_LOCAL_PATH
												+ "/face/"
												+ person.optInt("uID") + ".jpg");

							}
							if (faceBitmap != null)
								user.setFace(faceBitmap);
						}

						autoLogin = true;
					}
				}
			}

			timer.schedule(task, 3000, 3000);
		}
	}

}
