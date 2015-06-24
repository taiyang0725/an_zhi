package com.quanle.healthmanager.widget;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.quanle.healthmanager.R;
import com.quanle.healthmanager.utils.Config;
import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQAuth;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

public class QuickLoginLayout extends LinearLayout {
	private UserInfo mInfo;
	private QQAuth mQQAuth;
	private Tencent mTencent;

	private Context context;

	private OnLoginFinishListener mLoginFinishListener;

	private LinearLayout layout;

	public QuickLoginLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.widget_quicklogin, this);

		this.context = context;
		layout = (LinearLayout) getChildAt(0);

		init();
	}

	private void init() {
		ImageView imageView = (ImageView) layout.getChildAt(0);
		imageView.setClickable(true);
		imageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mTencent = Tencent.createInstance(Config.LOGIN_QQ_APPID,
						context);

				if (!mTencent.isSupportSSOLogin((Activity) context)) {
					Toast.makeText(context, R.string.app_toast_nosupportsso,
							Toast.LENGTH_SHORT).show();
					return;
				}
				mQQAuth = QQAuth.createInstance(Config.LOGIN_QQ_APPID, context);

				if (!mQQAuth.isSessionValid()) {
					mQQAuth.login((Activity) context, "get_simple_userinfo",
							loginListener);
				}
			}
		});

	}

	IUiListener loginListener = new BaseUiListener() {
		@Override
		protected void doComplete(JSONObject values) {
			if (mQQAuth != null && mQQAuth.isSessionValid()) {
				IUiListener listener = new IUiListener() {
					@Override
					public void onError(UiError e) {
					}

					@Override
					public void onComplete(Object response) {
						if (mLoginFinishListener != null)
							mLoginFinishListener.onComplete(
									(JSONObject) response, mQQAuth.getQQToken()
											.getAccessToken());
						;
					}

					@Override
					public void onCancel() {

					}
				};

				mInfo = new UserInfo(context, mQQAuth.getQQToken());
				mInfo.getUserInfo(listener);

			} else {
				System.out.println("Ã»µÇÂ½");
			}
		}
	};

	private class BaseUiListener implements IUiListener {
		@Override
		public void onComplete(Object response) {
			if (null == response) {
				System.out.println("·µ»ØÎª¿Õ µÇÂ¼Ê§°Ü");
				return;
			}
			JSONObject jsonResponse = (JSONObject) response;
			if (null != jsonResponse && jsonResponse.length() == 0) {
				System.out.println("·µ»ØÎª¿Õ µÇÂ¼Ê§°Ü");
				return;
			}
			System.out.println(" µÇÂ¼³É¹¦");
			System.out.println(response.toString());
			doComplete((JSONObject) response);
		}

		protected void doComplete(JSONObject values) {
		}

		@Override
		public void onError(UiError e) {
			System.out.println("µÇÂ¼Ê§°Ü" + e.errorDetail);
		}

		@Override
		public void onCancel() {
			System.out.println("µÇÂ¼Ê§°Ü onCancel");
		}
	}

	public void setOnLoginFinishListener(OnLoginFinishListener listener) {
		mLoginFinishListener = listener;
	}

	public interface OnLoginFinishListener {
		public void onComplete(JSONObject json, String token);

		public void onError();
	}
}
