package com.quanle.healthmanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.quanle.healthmanager.utils.AsyncImageLoader;
import com.quanle.healthmanager.utils.AsyncImageLoader.ImageDownloadCallBack;
import com.quanle.healthmanager.utils.NetListener;
import com.quanle.healthmanager.utils.NetListener.CallBack;

public class FragmentHome extends Fragment {
	View parentView;
	// private NavigationBar navigationBar;
	ListView listview;
	ListViewAdapter listadapter;
	GridView gridview;
	JSONArray array = new JSONArray();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// if (parentView == null) {
		parentView = inflater.inflate(R.layout.fragment_home, container, false);
		
		// navigationBar = (NavigationBar) parentView.findViewById(R.id.nbMain);
		// navigationBar.setText("全乐健康");
		iniView();
		// load();
		// }
		// TODO Auto-generated method stub
		return parentView;
	}

	public void load() {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		NetListener net = new NetListener(getActivity());
		net.ask(params, "getMain", new CallBack() {

			@Override
			public void onOver(JSONObject obj) {
				try {
					// TODO Auto-generated method stub
					if (obj != null) {
						JSONObject o = new JSONObject();
						o.put("type", 1);
						o.put("title", "健康提醒");
						array.put(o);
						JSONArray tip = obj.getJSONArray("tip");
						for (int i = 0; i < tip.length(); i++) {
							o = tip.getJSONObject(i);
							o.put("type", "tip");
							array.put(o);
						}
						o = new JSONObject();
						o.put("type", 1);
						o.put("title", "健康咨询");
						array.put(o);
						JSONArray articles = obj.getJSONArray("articles");
						for (int i = 0; i < articles.length(); i++) {
							o = articles.getJSONObject(i);
							o.put("type", "articles");
							array.put(o);
						}
						o = new JSONObject();
						o.put("type", 1);
						o.put("title", "乐圈");
						array.put(o);
						JSONArray fourm = obj.getJSONArray("fourm");
						for (int i = 0; i < fourm.length(); i++) {
							o = fourm.getJSONObject(i);
							o.put("type", "fourm");
							array.put(o);
						}
						o = new JSONObject();
						o.put("type", 1);
						o.put("title", "热门商品");
						array.put(o);
						JSONArray products = obj.getJSONArray("products");
						for (int i = 0; i < products.length(); i++) {
							o = products.getJSONObject(i);
							o.put("type", "products");
							array.put(o);
						}
						listadapter.notifyDataSetChanged();

					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		});

	}

	private void iniView() {
		listview = (ListView) parentView.findViewById(R.id.listview);
		listadapter = new ListViewAdapter(getActivity());
		listview.setAdapter(listadapter);
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

			}

		});

		ArrayList<HashMap<String, Object>> lstImageItem = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("ItemImage", R.drawable.addedvalue_01);// 添加图像资源的ID
		map.put("ItemText", "家政服务");// 按序号做ItemText
		lstImageItem.add(map);
		map = new HashMap<String, Object>();
		map.put("ItemImage", R.drawable.addedvalue_02);// 添加图像资源的ID
		map.put("ItemText", "提醒服务");// 按序号做ItemText
		lstImageItem.add(map);
		map = new HashMap<String, Object>();
		map.put("ItemImage", R.drawable.addedvalue_03);// 添加图像资源的ID
		map.put("ItemText", "社区保健");// 按序号做ItemText
		lstImageItem.add(map);
		map = new HashMap<String, Object>();
		map.put("ItemImage", R.drawable.addedvalue_04);// 添加图像资源的ID
		map.put("ItemText", "社区保护");// 按序号做ItemText
		lstImageItem.add(map);
		map = new HashMap<String, Object>();
		map.put("ItemImage", R.drawable.addedvalue_05);// 添加图像资源的ID
		map.put("ItemText", "社区食疗");// 按序号做ItemText
		lstImageItem.add(map);
		map = new HashMap<String, Object>();
		map.put("ItemImage", R.drawable.addedvalue_06);// 添加图像资源的ID
		map.put("ItemText", "社区保险");// 按序号做ItemText
		lstImageItem.add(map);

		GridView gridview = (GridView) parentView.findViewById(R.id.gridview);
		SimpleAdapter saImageItems = new SimpleAdapter(getActivity(),
				lstImageItem, R.layout.item_home_gridview, new String[] {
						"ItemImage", "ItemText" }, new int[] { R.id.imageview,
						R.id.textview });
		// 添加并且显示
		gridview.setAdapter(saImageItems);
		// 添加消息处理
		iniJktx();
		iniJkzx();
	}

	public void iniJktx() {
		String[] strs = new String[] { "您已经3周没有做过健康记录了", "建议不要再夜晚进餐" };
		LayoutInflater listContainer = LayoutInflater.from(getActivity());
		LinearLayout lay_jktx = (LinearLayout) parentView
				.findViewById(R.id.lay_jktx);
		for (int i = 0; i < strs.length; i++) {

			View item_home_jktx = listContainer.inflate(
					R.layout.item_home_jktx, null);
			TextView textview = (TextView) item_home_jktx
					.findViewById(R.id.textview);
			textview.setText(strs[i]);
			lay_jktx.addView(item_home_jktx);
		}

	}

	public void iniJkzx() {

		LayoutInflater listContainer = LayoutInflater.from(getActivity());
		LinearLayout lay_jkzx = (LinearLayout) parentView
				.findViewById(R.id.lay_jkzx);

		for (int i = 0; i < 2; i++) {
			View item_home_jkzx = listContainer.inflate(
					R.layout.item_home_jkzx, null);
			TextView textview_title = (TextView) item_home_jkzx
					.findViewById(R.id.textview_title);
			TextView textview_context = (TextView) item_home_jkzx
					.findViewById(R.id.textview_context);
			ImageView imageview = (ImageView) item_home_jkzx
					.findViewById(R.id.imageview);
			textview_title.setText("冬季科学月子洗头需注意7事项");
			textview_context
					.setText("头需注意7事项冬季科学月子洗头需注意7事项冬季科学月子洗头需注意7事项冬季科学月子洗头需注意7事项冬季科学月子洗头需注意7事项冬季科学月子洗头需注意7事项");
			imageview.setImageResource(R.drawable.xtou_demo);
			lay_jkzx.addView(item_home_jkzx);

		}

	}

	class ListViewAdapter extends BaseAdapter {
		private Context context; // 运行上下文
		AsyncImageLoader imageLoader;
		private LayoutInflater listContainer; // 视图容器

		public final class ListItemView { // 自定义控件集合

			public ImageView imageview;
			public TextView text_title;
			public TextView text_name;
			public TextView text_context;
			public LinearLayout lay_title;
			public LinearLayout lay_title2;

		}

		public ListViewAdapter(Context context) {
			this.context = context;
			listContainer = LayoutInflater.from(context); // 创建视图容器并设置上下文
			imageLoader = new AsyncImageLoader(context);
		}

		@Override
		public void notifyDataSetChanged() {
			// TODO Auto-generated method stub

			super.notifyDataSetChanged();
		}

		public int getCount() {
			// TODO Auto-generated method stub
			return array.length();
		}

		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			try {
				return array.getJSONObject(arg0);
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
		 * ListView Item设置
		 */
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			// TODO Auto-generated method stub
			Log.e("method", "getView");
			ListItemView listItemView = null;
			if (convertView == null) {
				final int selectID = position;
				// 自定义视图
				// if (convertView == null) {
				listItemView = new ListItemView();
				// 获取list_item布局文件的视图
				convertView = listContainer.inflate(R.layout.item_home, null);
				// 获取控件对象

				listItemView.imageview = (ImageView) convertView
						.findViewById(R.id.imageview);
				listItemView.text_title = (TextView) convertView
						.findViewById(R.id.text_title);
				listItemView.text_name = (TextView) convertView
						.findViewById(R.id.text_name);
				listItemView.text_name = (TextView) convertView
						.findViewById(R.id.text_name);
				listItemView.text_context = (TextView) convertView
						.findViewById(R.id.text_context);
				listItemView.lay_title = (LinearLayout) convertView
						.findViewById(R.id.lay_title);
				listItemView.lay_title2 = (LinearLayout) convertView
						.findViewById(R.id.lay_title2);

				// 设置控件集到convertView
				convertView.setTag(listItemView);
			} else {
				listItemView = (ListItemView) convertView.getTag();
			}

			listItemView.lay_title.setVisibility(View.GONE);
			listItemView.lay_title2.setVisibility(View.GONE);
			try {
				JSONObject obj = array.getJSONObject(position);
				if (obj.getString("type").equals("1")) {

					listItemView.lay_title.setVisibility(View.VISIBLE);
					listItemView.text_title.setText(obj.getString("title"));
				} else if (obj.getString("type").equals("articles")) {
					listItemView.lay_title2.setVisibility(View.VISIBLE);
					String title = obj.getString("title");
					final String picture = obj.getString("picture");
					String content = obj.getString("content");
					if (picture.equals("")) {
						listItemView.imageview.setVisibility(View.GONE);

						listItemView.imageview.setTag(picture);
						listItemView.imageview
								.setImageResource(R.drawable.ic_launcher);

						if (picture != null && !picture.equals("")) {
							Bitmap bitmap = imageLoader.loadImage(
									listItemView.imageview, picture,
									new ImageDownloadCallBack() {

										@Override
										public void onImageDownloaded(
												ImageView imageView,
												Bitmap bitmap) {
											// 通过 tag 来防止图片错位
											if (imageView.getTag() != null
													&& imageView.getTag()
															.equals(picture)) {
												imageView
														.setImageBitmap(bitmap);
											}
										}
									});

							if (bitmap != null) {
								listItemView.imageview.setImageBitmap(bitmap);
							}
						}
					}
					listItemView.text_name.setText(title);
					listItemView.text_context.setText(content);

				} else if (obj.getString("type").equals("fourm")) {
					listItemView.lay_title2.setVisibility(View.VISIBLE);
					String title = obj.getString("title");
					final String picture = obj.getString("face");
					String content = obj.getString("content");
					if (picture == null || picture.equals("")) {
						listItemView.imageview.setVisibility(View.GONE);

						
						
					}else{
						listItemView.imageview.setVisibility(View.VISIBLE);
						listItemView.imageview.setTag(picture);
						listItemView.imageview
								.setImageResource(R.drawable.ic_launcher);

						if (picture != null && !picture.equals("")) {
							Bitmap bitmap = imageLoader.loadImage(
									listItemView.imageview, picture,
									new ImageDownloadCallBack() {

										@Override
										public void onImageDownloaded(
												ImageView imageView,
												Bitmap bitmap) {
											// 通过 tag 来防止图片错位
											if (imageView.getTag() != null
													&& imageView.getTag()
															.equals(picture)) {
												imageView
														.setImageBitmap(bitmap);
											}
										}
									});

							if (bitmap != null) {
								listItemView.imageview.setImageBitmap(bitmap);
							}
						}
					}
					listItemView.text_name.setText(title);
					listItemView.text_context.setText(content);
				} else if (obj.getString("type").equals("products")) {
					listItemView.lay_title2.setVisibility(View.VISIBLE);
					String title = obj.getString("title");
					final String picture = obj.getString("image");
					String content = obj.getString("price");
					if (picture == null || picture.equals("")) {
						listItemView.imageview.setVisibility(View.GONE);

					} else {
						listItemView.imageview.setVisibility(View.VISIBLE);
						listItemView.imageview.setTag(picture);
						listItemView.imageview
								.setImageResource(R.drawable.ic_launcher);

						if (picture != null && !picture.equals("")) {
							Bitmap bitmap = imageLoader.loadImage(
									listItemView.imageview, picture,
									new ImageDownloadCallBack() {

										@Override
										public void onImageDownloaded(
												ImageView imageView,
												Bitmap bitmap) {
											// 通过 tag 来防止图片错位
											if (imageView.getTag() != null
													&& imageView.getTag()
															.equals(picture)) {
												imageView
														.setImageBitmap(bitmap);
											}
										}
									});

							if (bitmap != null) {
								listItemView.imageview.setImageBitmap(bitmap);
							}
						}
					}
					listItemView.text_name.setText(title);
					listItemView.text_context.setText(content);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			return convertView;
		}
	}
}
