package com.quanle.healthmanager;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.quanle.healthmanager.utils.AsyncImageLoader;
import com.quanle.healthmanager.utils.AsyncImageLoader.ImageDownloadCallBack;
import com.quanle.healthmanager.utils.NetListener;
import com.quanle.healthmanager.utils.NetListener.CallBack;
import com.quanle.healthmanager.utils.User;
import com.quanle.healthmanager.widget.NavigationBar;

public class DoctorsActivity extends Activity implements OnClickListener {

	JSONArray doctors = new JSONArray();
	private User user;
	private NavigationBar navigationBar;
	ListView listview;
	ListViewAdapter listadapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_doctor_list);
		user = ((User) getApplicationContext());
		navigationBar = (NavigationBar) findViewById(R.id.nbRegister);
		navigationBar.setText("医生列表");
		iniView();
		String officeid = this.getIntent().getStringExtra("officeid");
		loadDoctors(officeid);
	}

	public void loadDoctors(String officeid) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("u", user.getUID() + ""));
		params.add(new BasicNameValuePair("rc", user.getRndCodeString()));
		params.add(new BasicNameValuePair("t", officeid));
		NetListener net = new NetListener(this);
		net.ask(params, "getDoctorList", new CallBack() {

			@Override
			public void onOver(JSONObject obj) {
				try {
					// TODO Auto-generated method stub
					if (obj != null) {

						int r = obj.getInt("result");
						if (r == 1) {
							doctors = obj.getJSONArray("doctor");
							listadapter.notifyDataSetChanged();
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		});

	}

	public void onClick(View v) {
		switch (v.getId()) {

		default:
			break;
		}
	}

	public void iniView() {

		listview = (ListView) findViewById(R.id.listview);
		listadapter = new ListViewAdapter(this);
		listview.setAdapter(listadapter);
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				try {
					String doctorid = doctors.getJSONObject(arg2).getString(
							"id");
					Intent intent = new Intent(DoctorsActivity.this,
							DoctorActivity.class);
					intent.putExtra("doctorid", doctorid);
					startActivity(intent);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		});

	}

	class ListViewAdapter extends BaseAdapter {
		private Context context; // 杩愯涓婁笅鏂�
		AsyncImageLoader imageLoader;
		private LayoutInflater listContainer; // 瑙嗗浘瀹瑰櫒
	 

		public final class ListItemView { // 鑷畾涔夋帶浠堕泦鍚�

			public ImageView imageview_headimg;
			public TextView text_name;
			public TextView text_type;
			public TextView text_context;

		}

		public ListViewAdapter(Context context) {
			this.context = context;
			listContainer = LayoutInflater.from(context); // 鍒涘缓瑙嗗浘瀹瑰櫒骞惰缃笂涓嬫枃
			imageLoader = new AsyncImageLoader(context);
		}

		@Override
		public void notifyDataSetChanged() {
			// TODO Auto-generated method stub

			super.notifyDataSetChanged();
		}

		public int getCount() {
			// TODO Auto-generated method stub
			return doctors.length();
		}

		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			try {
				return doctors.getJSONObject(arg0);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		/**
		 * ListView Item璁剧疆
		 */
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			// TODO Auto-generated method stub
			Log.e("method", "getView");
			ListItemView listItemView = null;
			if (convertView == null) {
				final int selectID = position;

				listItemView = new ListItemView();

				convertView = listContainer.inflate(R.layout.item_regist, null);

				listItemView.imageview_headimg = (ImageView) convertView
						.findViewById(R.id.imageview_headimg);
				listItemView.text_name = (TextView) convertView
						.findViewById(R.id.text_name);

				listItemView.text_type = (TextView) convertView
						.findViewById(R.id.text_type);
				listItemView.text_context = (TextView) convertView
						.findViewById(R.id.text_context);

				convertView.setTag(listItemView);
			} else {
				listItemView = (ListItemView) convertView.getTag();
			}
			try {
				String name = doctors.getJSONObject(position).getString("name");
				String gender = doctors.getJSONObject(position).getString(
						"gender");
				String description = doctors.getJSONObject(position).getString(
						"description");

				listItemView.text_name.setText(name);
				listItemView.text_type.setText(gender);
				listItemView.text_context.setText(description);
 
				final String headimg =doctors.getJSONObject(position).getString("face");
				listItemView.imageview_headimg.setTag(headimg);
				listItemView.imageview_headimg
						.setImageResource(R.drawable.ic_launcher);

				if (headimg != null && !headimg.equals("")) {
					Bitmap bitmap = imageLoader.loadImage(
							listItemView.imageview_headimg, headimg,
							new ImageDownloadCallBack() {

								@Override
								public void onImageDownloaded(
										ImageView imageView, Bitmap bitmap) {
									// 通过 tag 来防止图片错位
									if (imageView.getTag() != null
											&& imageView.getTag().equals(
													headimg)) {
										imageView.setImageBitmap(bitmap);
									}
								}
							});

					if (bitmap != null) {
						listItemView.imageview_headimg.setImageBitmap(bitmap);
					}
				}
				// DownImg d=new DownImg(listItemView.imageview_headimg);
				// d.execute(
				// doctors.getJSONObject(position).getString("face"));
			} catch (Exception e) {
				e.printStackTrace();
			}
			return convertView;
		}
	}
 
	}
 
