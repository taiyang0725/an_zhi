package com.quanle.healthmanager;

import java.io.File;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

import com.quanle.healthmanager.utils.Config;
import com.quanle.healthmanager.utils.FormFile;
import com.quanle.healthmanager.utils.Functions;
import com.quanle.healthmanager.utils.User;
import com.quanle.healthmanager.widget.CustomProgressDialog;
import com.quanle.healthmanager.widget.NavigationBar;
import com.quanle.healthmanager.widget.NavigationBar.OnClickBackListener;
import com.quanle.healthmanager.widget.NavigationBar.OnClickButtonListener;

public class FourmEditActivity extends Activity {
	private Context context = FourmEditActivity.this;
	private User user;

	private final int DISCONNECT_INTERNET = 400;
	private final int DISCONNECT_SERVER = 401;
	private final int WHAT_WAIT = 404;
	private final int WHAT_FINISH = 402;
	private final int WHAT_ERROR = 403;
	private final int RESULT_LOAD_IMAGE = 1;
	private final int RESULT_LOAD_CAMRA = 2;
	private final int RESULT_LOAD_CLIP = 3;

	private ImageView iconImageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fourmedit);

		user = ((User) getApplicationContext());

		iconImageView = (ImageView) findViewById(R.id.iconImageView);

		NavigationBar nvbar = (NavigationBar) findViewById(R.id.navbar);
		nvbar.setText(R.string.fourm_btn_create);
		nvbar.setOnClickBackListener(new OnClickBackListener() {
			@Override
			public void onClick() {
				finish();
				overridePendingTransition(R.anim.in_from_left,
						R.anim.out_to_right);
			}
		});
		nvbar.setButtonDisplay(true);
		nvbar.setButtonText(R.string.alert_message_ok);
		nvbar.setOnClickButtonListener(new OnClickButtonListener() {
			@Override
			public void onClick() {
				EditText titleEditText = (EditText) findViewById(R.id.titleEditText);
				EditText introEditText = (EditText) findViewById(R.id.introEditText);
				final String titleString = titleEditText.getText().toString()
						.trim();
				final String introString = introEditText.getText().toString()
						.trim();
				if (titleString.equals("")) {
					Toast.makeText(context, R.string.fourmedit_toast_title,
							Toast.LENGTH_SHORT).show();
					return;
				}

				new Thread(new Runnable() {
					@Override
					public void run() {
						handler.obtainMessage(WHAT_WAIT).sendToTarget();

						String httpString = null;

						HashMap<String, String> params = new HashMap<String, String>();
						params.put("u", user.getUID() + "");
						params.put("rc", user.getRndCodeString());
						params.put("t", titleString);
						params.put("i", introString);

						FormFile formfile = null;
						File file = null;

						if (iconImageView.getTag() != null) {
							file = new File((String) iconImageView.getTag());
							if (file == null) {
								handler.obtainMessage(
										WHAT_ERROR,
										getResources().getString(
												R.string.toast_face_fail))
										.sendToTarget();
								return;
							}
							formfile = new FormFile(file.getName(), file,
									"image", "application/octet-stream");
						}

						try {
							httpString = Functions.uploadPost(Config.APIURL
									+ "edtFourm", params, formfile);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						if (httpString == null) {
							handler.obtainMessage(
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

							handler.obtainMessage(
									WHAT_ERROR,
									getResources().getString(
											R.string.toast_face_fail))
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

						if (file != null) {
							Functions.CreateFolderTree(Config.APP_LOCAL_PATH
									+ "/fourm/");
							file.renameTo(new File(Config.APP_LOCAL_PATH
									+ "/fourm/" + person.optInt("id") + ".jpg"));
						}
						handler.obtainMessage(WHAT_FINISH, person)
								.sendToTarget();

					}
				}).start();

			}
		});

		final FrameLayout iconLayout = (FrameLayout) findViewById(R.id.iconLayout);
		iconLayout.setClickable(true);
		iconLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
						.hideSoftInputFromWindow(FourmEditActivity.this
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
				popwin.showAtLocation(iconLayout, Gravity.BOTTOM, 0, 0);
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
			case DISCONNECT_INTERNET:
				if (progressDialog != null)
					progressDialog.dismiss();

				Toast.makeText(context,
						R.string.alert_message_disconnection_network,
						Toast.LENGTH_SHORT).show();
				break;

			case DISCONNECT_SERVER:
				if (progressDialog != null)
					progressDialog.dismiss();

				Toast.makeText(context,
						R.string.alert_message_disconnection_server,
						Toast.LENGTH_SHORT).show();
				break;

			case WHAT_WAIT:
				progressBuilder = new CustomProgressDialog.Builder(context);
				progressBuilder.setMessage(R.string.alert_message_fourm_submit);
				progressDialog = progressBuilder.create();
				progressDialog.show();
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
				JSONObject person = (JSONObject) msg.obj;
				Toast.makeText(context, person.optString("reason"),
						Toast.LENGTH_SHORT).show();

				finish();
				Intent intent = new Intent();
				intent.setClass(context, FourmActivity.class);
				intent.putExtra("id", person.optInt("id") + "");
				intent.putExtra("title", person.optString("title"));
				startActivity(intent);
				overridePendingTransition(R.anim.in_from_right,
						R.anim.out_to_left);
				break;
			}
		}
	};

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
				iconImageView.setImageBitmap(Functions.getLoacalBitmap(data
						.getExtras().getString("image")));
				iconImageView.setTag(data.getExtras().getString("image"));
				break;
			default:
				break;
			}
		}
	}
}
