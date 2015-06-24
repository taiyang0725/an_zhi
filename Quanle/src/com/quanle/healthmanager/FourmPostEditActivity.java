package com.quanle.healthmanager;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.R.string;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.quanle.healthmanager.utils.Config;
import com.quanle.healthmanager.utils.DisplayUtil;
import com.quanle.healthmanager.utils.FormFile;
import com.quanle.healthmanager.utils.Functions;
import com.quanle.healthmanager.utils.User;
import com.quanle.healthmanager.widget.CustomProgressDialog;
import com.quanle.healthmanager.widget.NavigationBar;
import com.quanle.healthmanager.widget.NavigationBar.OnClickBackListener;
import com.quanle.healthmanager.widget.NavigationBar.OnClickButtonListener;

public class FourmPostEditActivity extends Activity {
	private Context context = FourmPostEditActivity.this;
	private Bundle bundle;
	private User user;
	private UploadThread uploadThread;

	private final int DISCONNECT_SERVER = 401;
	private final int WHAT_FINISH = 0;
	private final int WHAT_ERROR = 1;
	private final int WHAT_LOADING = 2;
	private final int WHAT_UPLOAD = 3;
	private final int WHAT_UPLOAD_FINISH = 4;

	private final int RESULT_LOAD_IMAGE = 1;
	private final int RESULT_LOAD_CAMRA = 2;
	private final int RESULT_LOAD_CLIP = 3;

	private EditText introEditText;

	// private ArrayList<HashMap<String, String>> fileList = new
	// ArrayList<HashMap<String, String>>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fourmpostedit);

		user = ((User) getApplicationContext());
		bundle = getIntent().getExtras();

		introEditText = (EditText) findViewById(R.id.introEditText);

		NavigationBar nvbar = (NavigationBar) findViewById(R.id.navbar);
		nvbar.setText(R.string.fourm_title_postcreate);
		nvbar.setOnClickBackListener(new OnClickBackListener() {
			@Override
			public void onClick() {
				finish();
				overridePendingTransition(R.anim.in_from_left,
						R.anim.out_to_right);
			}
		});
		nvbar.setButtonDisplay(true);
		nvbar.setButtonText(R.string.btn_post);
		nvbar.setOnClickButtonListener(new OnClickButtonListener() {
			@Override
			public void onClick() {
				EditText titleEditText = (EditText) findViewById(R.id.titleEditText);
				final String titleString = titleEditText.getText().toString()
						.trim();
				final String introString = introEditText.getText().toString()
						.trim();

				if (titleString.equals("")) {
					Toast.makeText(context, R.string.fourm_toast_title,
							Toast.LENGTH_SHORT).show();
					return;
				}

				if (introString.equals("")) {
					Toast.makeText(context, R.string.fourm_toast_title,
							Toast.LENGTH_SHORT).show();
					return;
				}

				if (!Functions.checkInternet(context)) {
					Toast.makeText(context,
							R.string.alert_message_disconnection_network,
							Toast.LENGTH_SHORT).show();
					return;
				}

				new Thread(new Runnable() {
					@Override
					public void run() {
						Message msg = handler.obtainMessage(WHAT_LOADING);

						List<NameValuePair> params = new ArrayList<NameValuePair>();
						params.add(new BasicNameValuePair("d", Functions
								.getDeviceId(getApplicationContext())));
						params.add(new BasicNameValuePair("c", Functions
								.getCPUSerial()));
						params.add(new BasicNameValuePair("u", user.getUID()
								+ ""));
						params.add(new BasicNameValuePair("rc", user
								.getRndCodeString()));
						params.add(new BasicNameValuePair("cid", bundle
								.getString("cid")));
						params.add(new BasicNameValuePair("t", titleString));
						params.add(new BasicNameValuePair("i", introString
								.replace("\n", "<br />")));

						String httpString = Functions.getHttpResponse(
								Config.APIURL + "edtFourmPost", params);

						System.out.println(params);

						if (httpString == null) {
							System.out.println(params.toString());
							handler.obtainMessage(DISCONNECT_SERVER)
									.sendToTarget();
							return;
						}

						System.out.println(httpString);

						JSONObject person = null;
						try {
							JSONTokener jsonParser = new JSONTokener(httpString);
							person = (JSONObject) jsonParser.nextValue();
						} catch (JSONException e) {
							handler.obtainMessage(DISCONNECT_SERVER)
									.sendToTarget();
							e.printStackTrace();
							return;
						}

						if (person == null) {
							handler.obtainMessage(DISCONNECT_SERVER)
									.sendToTarget();
							return;
						}

						int result = person.optInt("result");
						String reason = person.optString("reason");

						if (result == 0) {
							handler.obtainMessage(WHAT_ERROR, reason)
									.sendToTarget();
							return;
						}

						handler.obtainMessage(WHAT_FINISH, reason)
								.sendToTarget();
					}
				}).start();
			}
		});

		final ImageView picImageView = (ImageView) findViewById(R.id.picImageView);
		picImageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
						.hideSoftInputFromWindow(FourmPostEditActivity.this
								.getCurrentFocus().getWindowToken(),
								InputMethodManager.HIDE_NOT_ALWAYS);

				LayoutInflater mLayoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
				View menuView = mLayoutInflater.inflate(
						R.layout.popupwindow_face, null, false);
				final PopupWindow popwin = new PopupWindow(menuView,
						LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT,
						true);
				popwin.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.popupwindow_bg));
				popwin.setAnimationStyle(R.style.popupAnimation);
				popwin.showAtLocation(picImageView, Gravity.BOTTOM, 0, 0);
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
										"camar.jpg"));
								intent.putExtra(MediaStore.EXTRA_OUTPUT,
										imageUri);
								startActivityForResult(intent,
										RESULT_LOAD_CAMRA);
							}
						});

			}
		});
	}

	private Handler handler = new Handler() {
		CustomProgressDialog progressDialog;
		CustomProgressDialog.Builder progressBuilder;

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case WHAT_LOADING:
				progressBuilder = new CustomProgressDialog.Builder(context);
				progressBuilder.setMessage(R.string.alert_message_fourm_submit);
				progressDialog = progressBuilder.create();
				progressDialog.show();
				break;

			case WHAT_UPLOAD:
				progressBuilder = new CustomProgressDialog.Builder(context);
				progressBuilder
						.setMessage(R.string.alert_message_image_uploading);
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
				Toast.makeText(context, (String) msg.obj, Toast.LENGTH_SHORT)
						.show();
				setResult(RESULT_OK);
				finish();
				overridePendingTransition(R.anim.in_from_left,
						R.anim.out_to_right);
				break;

			case WHAT_UPLOAD_FINISH:
				if (progressDialog != null)
					progressDialog.dismiss();

				Editable edit = introEditText.getEditableText();// ��ȡEditText������
				int index = introEditText.getSelectionStart();

				String path = "<img src=\"/images/forum/" + msg.obj + "\" />";
				Bitmap bitmap = Functions.getLoacalBitmap(Config.APP_LOCAL_PATH
						+ "/forum/" + msg.obj);
				bitmap = Functions.bitmapZoom(bitmap,
						DisplayUtil.dip2px(context, 30),
						DisplayUtil.dip2px(context, 30));
				SpannableString ss = new SpannableString(path);
				ImageSpan span = new ImageSpan(bitmap);
				ss.setSpan(span, 0, path.length(),
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

				if (index < 0 || index >= edit.length()) {
					edit.append(ss);
				} else {
					edit.insert(index, ss);// �������λ�ò�������
				}

				break;
			}

		}

	};

	private class UploadThread extends Thread {
		private String fileString;

		public void setFile(String fileString) {
			this.fileString = fileString;
		}

		public void run() {
			handler.obtainMessage(WHAT_UPLOAD).sendToTarget();

			String httpString = null;

			HashMap<String, String> params = new HashMap<String, String>();
			params.put("u", user.getUID() + "");
			params.put("rc", user.getRndCodeString());

			FormFile formfile = null;
			File file = null;

			file = new File(Config.APP_LOCAL_PATH + "/tmp/image.jpg");
			if (file == null) {
				handler.obtainMessage(WHAT_ERROR,
						getResources().getString(R.string.toast_face_fail))
						.sendToTarget();
				return;
			}
			formfile = new FormFile(file.getName(), file, "image",
					"application/octet-stream");

			try {
				httpString = Functions.uploadPost(Config.APIURL
						+ "uploadFourmPicture", params, formfile);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (httpString == null) {
				handler.obtainMessage(WHAT_ERROR,
						getResources().getString(R.string.toast_face_fail))
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

				handler.obtainMessage(WHAT_ERROR,
						getResources().getString(R.string.toast_face_fail))
						.sendToTarget();
				return;
			}

			int result = person.optInt("result");
			String reason = person.optString("reason");
			String remoteFileString = person.optString("fileName");
			if (result == 0) {
				handler.obtainMessage(WHAT_ERROR, reason).sendToTarget();
				return;
			}

			if (file != null) {
				Functions.CreateFolderTree(Config.APP_LOCAL_PATH + "/forum/");
				file.renameTo(new File(Config.APP_LOCAL_PATH + "/forum/"
						+ remoteFileString));
			}

			// HashMap<String, String> map = new HashMap<String, String>();
			// map.put("local", fileString);
			// map.put("remote", remoteFileString);
			System.out.println("fileString" + fileString + ","
					+ remoteFileString);
			// fileList.add(map);

			handler.obtainMessage(WHAT_UPLOAD_FINISH, remoteFileString)
					.sendToTarget();

		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case RESULT_LOAD_CAMRA:
				Intent intent = new Intent();
				intent.setClass(context, ClipImageActivity.class);
				intent.putExtra("imageFile", Config.APP_LOCAL_PATH
						+ "/tmp/camar.jpg");
				startActivityForResult(intent, RESULT_LOAD_CLIP);
				overridePendingTransition(R.anim.in_from_right,
						R.anim.out_to_left);
				break;

			case RESULT_LOAD_IMAGE:
				if (null == data)
					break;
				Uri selectedImage = data.getData();
				ContentResolver resolver = getContentResolver();
				Bitmap bitmap = null;
				try {
					bitmap = BitmapFactory.decodeStream(resolver
							.openInputStream(selectedImage));
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				float width = 600f;
				float scale = (bitmap.getHeight() > bitmap.getWidth()) ? width
						/ bitmap.getHeight() : width / bitmap.getWidth();
				bitmap = Functions.bitmapZoom(bitmap,
						bitmap.getWidth() * scale, bitmap.getHeight() * scale);
				Functions
				.CreateFolderTree(Config.APP_LOCAL_PATH
						+ "/tmp");				
				Functions.saveLoaclBitmap(bitmap, Config.APP_LOCAL_PATH
						+ "/tmp/image.jpg");

				uploadThread = new UploadThread();
				uploadThread.setFile(selectedImage.getPath());
				uploadThread.start();
				break;

			default:
				break;
			}
		}
	}
}
