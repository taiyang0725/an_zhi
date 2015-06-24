package com.quanle.healthmanager.utils;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

public class DownImg extends AsyncTask<String, Integer, String> {
	ImageView context;
	Bitmap bitmap;

	public DownImg(ImageView context) {
		super();
		this.context = context;
	}

	@Override
	protected String doInBackground(String... arg0) {
		String imageurl = arg0[0];
		try {

			if (imageurl != null && !imageurl.equals("")
					&& !imageurl.equals("null")) {
				bitmap = BitmapUtils.getbitmap(imageurl);

			}
			return "ok";
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
	}

	@Override
	protected void onPostExecute(String result) {
		if (bitmap != null) {
			context.setImageBitmap(bitmap);
		}
		super.onPostExecute(result);
	}

	@Override
	protected void onPreExecute() {

		super.onPreExecute();
	}

	@Override
	protected void onProgressUpdate(Integer... values) {

		super.onProgressUpdate(values);
	}

}