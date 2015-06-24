package com.javis.Adapter;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.quanle.healthmanager.R;
import com.quanle.healthmanager.utils.AsyncImageLoader;
import com.quanle.healthmanager.utils.AsyncImageLoader.ImageDownloadCallBack;

public class Adapter_GridView_hot extends BaseAdapter {
	private Context context;
	private JSONArray data;
	AsyncImageLoader imageLoader;

	public Adapter_GridView_hot(Context context, JSONArray data) {

		this.context = context;
		this.data = data;
		imageLoader = new AsyncImageLoader(context);
	}

	@Override
	public int getCount() {
		return data.length();
	}

	public JSONArray getData() {
		return data;
	}

	public void setData(JSONArray data) {
		this.data = data;
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int position, View currentView, ViewGroup arg2) {
		HolderView holderView = null;
		if (currentView == null) {
			holderView = new HolderView();
			currentView = LayoutInflater.from(context).inflate(
					R.layout.adapter_grid_hot_home, null);
			holderView.iv_pic = (ImageView) currentView
					.findViewById(R.id.iv_adapter_grid_pic);

			holderView.text_name = (TextView) currentView.findViewById(R.id.text_name);
			holderView.text_price = (TextView) currentView.findViewById(R.id.text_price);
			currentView.setTag(holderView);
		} else {
			holderView = (HolderView) currentView.getTag();
		}

		try {
			JSONObject obj=data.getJSONObject(position);
			holderView.text_name.setText(obj.getString("title"));
			holderView.text_price.setText("¥"+obj.getString("price"));
			final String image = data.getJSONObject(position)
					.getString("image");
			holderView.iv_pic.setTag(image);
			holderView.iv_pic.setImageResource(R.drawable.ic_launcher);
			if (image != null && !image.equals("")) {
				Bitmap bitmap = imageLoader.loadImage(holderView.iv_pic, image,
						new ImageDownloadCallBack() {

							@Override
							public void onImageDownloaded(ImageView imageView,
									Bitmap bitmap) {
								// 通过 tag 来防止图片错位
								if (imageView.getTag() != null
										&& imageView.getTag().equals(image)) {
									imageView.setImageBitmap(bitmap);
								}
							}
						});
				if (bitmap != null) {
					holderView.iv_pic.setImageBitmap(bitmap);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return currentView;
	}

	public class HolderView {

		private ImageView iv_pic;
		private TextView text_name;
		private TextView text_price;

	}

}
