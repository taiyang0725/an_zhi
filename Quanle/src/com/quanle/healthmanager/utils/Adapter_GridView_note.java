package com.quanle.healthmanager.utils;

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

public class Adapter_GridView_note extends BaseAdapter {
	private Context context;
	private JSONArray data;
	AsyncImageLoader imageLoader;

	public Adapter_GridView_note(Context context, JSONArray data) {

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
					R.layout.adapter_grid_note, null);
			holderView.text_name = (TextView) currentView
					.findViewById(R.id.text_name);

			currentView.setTag(holderView);
		} else {
			holderView = (HolderView) currentView.getTag();
		}

		try {
			JSONObject obj = data.getJSONObject(position);
			holderView.text_name.setText(obj.getString("title"));

		} catch (Exception e) {
			e.printStackTrace();
		}

		return currentView;
	}

	public class HolderView {

		private TextView text_name;

	}

}
