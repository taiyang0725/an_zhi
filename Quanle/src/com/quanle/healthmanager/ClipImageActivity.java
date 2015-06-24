package com.quanle.healthmanager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.quanle.healthmanager.utils.Config;
import com.quanle.healthmanager.utils.DisplayUtil;
import com.quanle.healthmanager.utils.Functions;
import com.quanle.healthmanager.widget.ClipImageLayout;

public class ClipImageActivity extends Activity {
	private ClipImageLayout mClipImageLayout;
	private Context context = ClipImageActivity.this;
	private float width;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_clipimage);

		Intent intent = getIntent();
		String imageFile = intent.getExtras().getString("imageFile");

		width = 300;// DisplayUtil.dip2px(context, 100);

		FileInputStream fis = null;
		try {
			fis = new FileInputStream(imageFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Bitmap bitmap = BitmapFactory.decodeStream(fis);
		mClipImageLayout = (ClipImageLayout) findViewById(R.id.clipImageLayout);
		mClipImageLayout.setImage(bitmap);

		((ImageView) findViewById(R.id.ivBack))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						finish();
						overridePendingTransition(R.anim.in_from_left,
								R.anim.out_to_right);
					}
				});

		((TextView) findViewById(R.id.btnOK))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Bitmap bitmap = mClipImageLayout.clip();
						float scale = (bitmap.getHeight() > bitmap.getWidth()) ? width
								/ bitmap.getHeight()
								: width / bitmap.getWidth();
						bitmap = Functions.bitmapZoom(bitmap, bitmap.getWidth()
								* scale, bitmap.getHeight() * scale);

						Functions.saveLoaclBitmap(bitmap, Config.APP_LOCAL_PATH
								+ "/tmp/image.jpg");

						Intent intent = new Intent();
						intent.putExtra("image", Config.APP_LOCAL_PATH
								+ "/tmp/image.jpg");
						setResult(RESULT_OK, intent);
						finish();
						overridePendingTransition(R.anim.in_from_left,
								R.anim.out_to_right);
					}
				});
	}
}
