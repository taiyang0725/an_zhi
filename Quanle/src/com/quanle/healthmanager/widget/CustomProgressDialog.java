package com.quanle.healthmanager.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.quanle.healthmanager.R;

public class CustomProgressDialog extends Dialog {

	public CustomProgressDialog(Context context, int theme) {
		super(context, theme);
	}

	public CustomProgressDialog(Context context) {
		super(context);
	}

	public static class Builder {
		private Context context;
		private String messageString;
		private TextView tipTextView;

		public Builder(Context context) {
			this.context = context;
		}

		public CustomProgressDialog create() {

			final CustomProgressDialog dialog = new CustomProgressDialog(
					context, R.style.progressDialog);
			LayoutInflater inflater = LayoutInflater.from(context);
			View v = inflater.inflate(R.layout.widget_progressdialog, null);// 得到加载view
			LinearLayout layout = (LinearLayout) v
					.findViewById(R.id.dialog_view);// 加载布局

			tipTextView = (TextView) v.findViewById(R.id.tipTextView);// 提示文字
			if (messageString != null)
				tipTextView.setText(messageString);
			// progressDialog.setCancelable(false);// 不可以用“返回键”取消
			// progressDialog.setContentView(layout,
			// new LinearLayout.LayoutParams(
			// LinearLayout.LayoutParams.MATCH_PARENT,
			// LinearLayout.LayoutParams.MATCH_PARENT));// 设置布局
			// progressDialog.show();

			dialog.setContentView(layout);
			return dialog;
		}

		public Builder setMessage(int res) {
			this.setMessage((String) context.getText(res));
			return this;
		}

		public Builder setMessage(String string) {
			this.messageString = string;
			if (tipTextView != null)
				tipTextView.setText(string);
			return this;
		}
	}

	public void setMessage(String string) {
		// TODO Auto-generated method stub

	}

}
