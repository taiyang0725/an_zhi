package com.quanle.healthmanager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.quanle.healthmanager.utils.HttpUtil;
import com.quanle.healthmanager.widget.NavigationBar;

public class FragmentRegist extends Fragment implements OnClickListener {
	View parentView;
	JSONArray doctors = new JSONArray();
	JSONArray offices = new JSONArray();
	private NavigationBar navigationBar;
	ListView listview;
	ListViewAdapter listadapter;
	ListView listview_office;
	ListView_office_Adapter listadapter_office;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_rec:
			listview.setVisibility(View.VISIBLE);
			listview_office.setVisibility(View.GONE);
			listadapter.notifyDataSetChanged();
			break;
		case R.id.btn_all:
			listview_office.setVisibility(View.VISIBLE);
			listview.setVisibility(View.GONE);
			listadapter_office.notifyDataSetChanged();

		default:
			break;
		}
	}

	public void iniView() {
		((TextView) parentView.findViewById(R.id.btn_rec))
				.setOnClickListener(this);
		;
		((TextView) parentView.findViewById(R.id.btn_all))
				.setOnClickListener(this);
		;

		listview = (ListView) parentView.findViewById(R.id.listview);
		listadapter = new ListViewAdapter(getActivity());
		listview.setAdapter(listadapter);
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				try {
					String doctorid = doctors.getJSONObject(arg2).getString(
							"id");
					Intent intent = new Intent(getActivity(),
							DoctorActivity.class);
					intent.putExtra("doctorid", doctorid);
					startActivity(intent);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		});

		listview_office = (ListView) parentView
				.findViewById(R.id.listview_office);
		listadapter_office = new ListView_office_Adapter(getActivity());
		listview_office.setAdapter(listadapter_office);
		listview_office.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				try {
					String officeid = offices.getJSONObject(arg2).getString(
							"id");
					Intent intent = new Intent(getActivity(),
							DoctorsActivity.class);
					intent.putExtra("officeid", officeid);
					startActivity(intent);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		});

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// if (parentView == null) {
		parentView = inflater.inflate(R.layout.fragment_register, container,
				false);
		navigationBar = (NavigationBar) parentView
				.findViewById(R.id.nbRegister);
		navigationBar.setText("预约挂号");
		iniView();
		DownFromCloud d = new DownFromCloud(getActivity());
		d.execute("http://120.27.39.61:88/?a=getRegist");

		return parentView;
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
//				DownImg d = new DownImg(listItemView.imageview_headimg);
//				d.execute(doctors.getJSONObject(position).getString("face"));
			} catch (Exception e) {
				e.printStackTrace();
			}
			return convertView;
		} 
	}

	class ListView_office_Adapter extends BaseAdapter {
		private Context context;

		private LayoutInflater listContainer;

		public final class ListItemView {

			public TextView text_name;

		}

		public ListView_office_Adapter(Context context) {
			this.context = context;
			listContainer = LayoutInflater.from(context);

		}

		@Override
		public void notifyDataSetChanged() {
			// TODO Auto-generated method stub

			super.notifyDataSetChanged();
		}

		public int getCount() {
			// TODO Auto-generated method stub
			return offices.length();
		}

		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			try {
				return offices.getJSONObject(arg0);
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

		public View getView(final int position, View convertView,
				ViewGroup parent) {
			// TODO Auto-generated method stub
			Log.e("method", "getView");
			ListItemView listItemView = null;
			if (convertView == null) {
				final int selectID = position;

				listItemView = new ListItemView();

				convertView = listContainer.inflate(
						R.layout.item_regist_office, null);

				listItemView.text_name = (TextView) convertView
						.findViewById(R.id.text_name);

				convertView.setTag(listItemView);
			} else {
				listItemView = (ListItemView) convertView.getTag();
			}
			try {
				String name = offices.getJSONObject(position)
						.getString("title");

				listItemView.text_name.setText(name);

			} catch (Exception e) {
				e.printStackTrace();
			}
			return convertView;
		}
	}

	class DownFromCloud extends AsyncTask<String, Integer, String> {
		Context context;

		public DownFromCloud(Context context) {
			super();
			this.context = context;
		}

		@Override
		protected String doInBackground(String... arg0) {
			String url = arg0[0];
			try {
				String result = HttpUtil.getRequest(url);
				System.out.println(">>>>" + result);
				JSONObject root = new JSONObject(result);
				int r = root.getInt("result");
				if (r == 1) {
					doctors = root.getJSONArray("doctor");
					offices = root.getJSONArray("office");
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
			listadapter.notifyDataSetChanged();
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

}
