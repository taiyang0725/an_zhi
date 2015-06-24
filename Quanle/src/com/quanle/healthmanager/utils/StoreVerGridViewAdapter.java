package com.quanle.healthmanager.utils;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.quanle.healthmanager.R;
import com.quanle.healthmanager.model.Good;

public class StoreVerGridViewAdapter extends BaseAdapter {
	Context context;
	List<Good> list;

	public StoreVerGridViewAdapter(Context _context, List<Good> _list) {
		this.list = _list;
		this.context = _context;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater layoutInflater = LayoutInflater.from(context);
		convertView = layoutInflater.inflate(R.layout.store_ver_grid_item, null);
		TextView title = (TextView) convertView.findViewById(R.id.title);
		TextView price = (TextView) convertView.findViewById(R.id.price);
		Good good = list.get(position);
		title.setText(good.getTitle());
		price.setText(good.getPrice());

		return convertView;
	}
}
