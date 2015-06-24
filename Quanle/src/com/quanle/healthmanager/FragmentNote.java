package com.quanle.healthmanager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.quanle.healthmanager.utils.AsyncImageLoader;
import com.quanle.healthmanager.utils.NetListener;
import com.quanle.healthmanager.utils.NetListener.CallBack;
import com.quanle.healthmanager.utils.User;
import com.quanle.healthmanager.widget.NavigationBar;
import com.quanle.healthmanager.widget.NavigationBar.OnClickButtonListener;

public class FragmentNote extends Fragment {
	View parentView;
	private NavigationBar navigationBar;

	ListView noteListView;
	JSONArray array = new JSONArray();
	ListViewAdapter lsitviewadapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		// if (parentView == null) {
		parentView = inflater.inflate(R.layout.fragment_note, container, false);
		navigationBar = (NavigationBar) parentView.findViewById(R.id.nbNote);
		navigationBar.setText("健康记录");
		navigationBar.setBackDisplayable(false);
		navigationBar.setButtonDisplay(true);
		navigationBar.setButtonText(R.string.note_plus);
		navigationBar.setOnClickButtonListener(new OnClickButtonListener() {

			@Override
			public void onClick() {
				// TODO Auto-generated method stub
				User user = (User) getActivity().getApplicationContext();
				int usrid = user.getUID();
				if (usrid == 0) {
					Toast.makeText(getActivity(), "请登录先!", Toast.LENGTH_LONG)
							.show();
				}
				Intent intent = new Intent(getActivity(),
						Activity_Note_plus.class);
				intent.putExtra("type", "add");
				intent.putExtra("no", "");
				intent.putExtra("subjectID", "");
				startActivity(intent);
			}
		});

		noteListView = (ListView) parentView.findViewById(R.id.noteListView);
		lsitviewadapter = new ListViewAdapter(getActivity());
		noteListView.setAdapter(lsitviewadapter);

		noteListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				delete(arg2);
				return true;
			}
		});
		noteListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				try {
					JSONObject obj = array.getJSONObject(arg2);
					String subjectID = obj.getString("subjectID");
					String no = obj.getString("no");

					Intent intent = new Intent(getActivity(),
							Activity_Note_plus.class);
					intent.putExtra("type", "browser");
					intent.putExtra("no", no);
					intent.putExtra("subjectID", subjectID);
					startActivity(intent);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});
		load();
		return parentView;
	}

	public void delete(final int position) {
		new AlertDialog.Builder(getActivity())
				.setTitle("是否删除该记录？")
				.setPositiveButton("确认", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						try {
							JSONObject obj = array.getJSONObject(position);
							String no = obj.getString("no");
							List<NameValuePair> params = new ArrayList<NameValuePair>();
							NetListener net = new NetListener(getActivity());
							net.ask(params, "deleteUserPhysical&id=" + no,
									new CallBack() {

										@Override
										public void onOver(JSONObject obj) {
											// TODO Auto-generated method stub
											System.out.println(obj);
											if (obj != null) {
												load();

											}

										}

									});
						} catch (Exception e) {
							e.printStackTrace();
						}
						dialog.dismiss();
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
				}).show();
	}

	public void delete() {

	}

	public void load() {
		User user = (User) getActivity().getApplicationContext();
		int usrid = user.getUID();
		List<NameValuePair> params = new ArrayList<NameValuePair>();

		NetListener net = new NetListener(getActivity());
		net.ask(params, "getUserPhysicalList&id=" + usrid, new CallBack() {

			@Override
			public void onOver(JSONObject obj) {
				// TODO Auto-generated method stub
				System.out.println(obj);
				if (obj != null) {

					try {
						array = obj.getJSONArray("list");
						lsitviewadapter.notifyDataSetChanged();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}

		});
	}

	class ListViewAdapter extends BaseAdapter {
		private Context context; // 运行上下文
		AsyncImageLoader imageLoader;
		private LayoutInflater listContainer; // 视图容器

		public final class ListItemView { // 自定义控件集合

			public TextView text_title;
			public TextView text_date;

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
				listItemView = new ListItemView();
				// 获取list_item布局文件的视图
				convertView = listContainer.inflate(R.layout.item_note, null);
				// 获取控件对象
				listItemView.text_title = (TextView) convertView
						.findViewById(R.id.text_title);
				listItemView.text_date = (TextView) convertView
						.findViewById(R.id.text_date);

				convertView.setTag(listItemView);
			} else {
				listItemView = (ListItemView) convertView.getTag();
			}

			try {
				JSONObject obj = array.getJSONObject(position);
				String time = obj.getString("time");
				String title = obj.getString("title");
				listItemView.text_date.setText(dateFormat(time));
				listItemView.text_title.setText(title);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return convertView;
		}
	}

	public String dateFormat(String dateDate) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分");
		String dateString = "";
		try {
			dateString = formatter.format(new SimpleDateFormat(
					"yyyy/MM/dd HH:mm:ss").parse(dateDate).getTime());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dateString;
	}

}
