package com.quanle.healthmanager.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.quanle.healthmanager.R;

public class MyBaseAdapter extends BaseAdapter {
	private Context context;
	private ArrayList<HashMap<String, Object>> list;
	private String[] items = new String[] {};
	private int[] ids = new int[] {};
	private int resource;
	private LayoutInflater mInflater;
	private ArrayList<HashMap<String, String>> arr = new ArrayList<HashMap<String, String>>();

	public MyBaseAdapter(Context context,
			ArrayList<HashMap<String, Object>> list, int resource,
			String[] items, int[] ids) {
		this.context = context;
		this.list = list;
		this.items = items;
		this.ids = ids;
		this.resource = resource;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	}

	public void changeDate() {
		notifyDataSetChanged();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view = null;
		convertView = mInflater.inflate(resource, null);

		if (position < 0 || list.size() <= 0)
			return null;

		for (int i = 0; i < ids.length; i++) {
			view = (View) convertView.findViewById(ids[i]);
			if (view instanceof TextView) {
				TextView txt = (TextView) convertView.findViewById(ids[i]);
				Object txtString = list.get(position).get(items[i]);
				if (txtString instanceof Spanned)
					txt.setText((Spanned) txtString);
				else
					txt.setText((String) txtString);
			}

			if (view instanceof ImageView) {
				ImageView img = (ImageView) convertView.findViewById(ids[i]);
				Object object = list.get(position).get(items[i]);

				if (object instanceof integer) {
					img.setImageResource((Integer) object);
				} else if (object instanceof String) {
					File f = new File((String) object);
					if (f.exists()) {
						Bitmap bitmap = BitmapFactory
								.decodeFile((String) object);
						img.setImageBitmap(bitmap);
					}
				} else if (object instanceof Bitmap) {
					img.setImageBitmap((Bitmap) object);
				}
			}

			// if (view.getTag() != null && view.getTag().equals("")) {
			// view.setOnClickListener(new ItemClickListener(position));
			// }
		}

		return convertView;
	}

	// class ItemClickListener implements OnClickListener {
	// private int position;
	//
	// }

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	public ArrayList<HashMap<String, String>> getCheckedItem() {
		return arr;
	}

	public void arrClear() {
		arr.clear();
	}
}
