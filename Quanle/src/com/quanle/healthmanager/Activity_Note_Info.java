package com.quanle.healthmanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.quanle.healthmanager.utils.AsyncImageLoader;
import com.quanle.healthmanager.utils.NetListener;
import com.quanle.healthmanager.utils.NetListener.CallBack;
import com.quanle.healthmanager.utils.User;
import com.quanle.healthmanager.widget.NavigationBar;
import com.quanle.healthmanager.widget.NavigationBar.OnClickButtonListener;

public class Activity_Note_Info extends Activity {

	private NavigationBar navigationBar;
	ListView noteListView;
	JSONArray array = new JSONArray();
	ListViewAdapter lsitviewadapter;
	String no;
	 
	 

	// ListView noteEditListView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_note_plus);
		no=getIntent().getStringExtra("no");
		navigationBar = (NavigationBar) findViewById(R.id.nbNote);
		navigationBar.setText("记录详情");
		navigationBar.setBackDisplayable(true);
		navigationBar.setButtonDisplay(false);
		navigationBar.setButtonText(R.string.note_plus);
		navigationBar.setOnClickButtonListener(new OnClickButtonListener() {

			@Override
			public void onClick() {
				// TODO Auto-generated method stub
			}
		});
		noteListView = (ListView) findViewById(R.id.noteListView);

		lsitviewadapter = new ListViewAdapter(this);
		noteListView.setAdapter(lsitviewadapter);
		noteListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				 
			}
		});

		// noteEditListView = (ListView) findViewById(R.id.noteEditListView);
		// listviewEidtAdapter = new ListViewEidtAdapter(this);
		// noteEditListView.setAdapter(listviewEidtAdapter);
		//
		// noteEditListView.setOnItemSelectedListener(new
		// OnItemSelectedListener() {
		//
		// @Override
		// public void onItemSelected(AdapterView<?> arg0, View view,
		// int arg2, long arg3) {
		// // TODO Auto-generated method stub
		// noteEditListView.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
		// EditText edit=(EditText)view.findViewById(R.id.text_edit);
		// edit.requestFocus();
		// }
		//
		// @Override
		// public void onNothingSelected(AdapterView<?> arg0) {
		// // TODO Auto-generated method stub
		// noteEditListView.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
		// }
		// });
		load();
	}

	@Override
	public void onBackPressed() {
		 
			super.onBackPressed();
		 
	}

	public void load() {
		User user = (User) getApplicationContext();
		int usrid = user.getUID();
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		NetListener net = new NetListener(this);
		net.ask(params, "getUsrPhysical&id="+usrid+"&p="+no, new CallBack() {

			@Override
			public void onOver(JSONObject obj) {
				if (obj != null) {
					try {
						array = obj.getJSONArray("list");
						lsitviewadapter.notifyDataSetChanged();

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				// TODO Auto-generated method stub
			}
		});
	}

	 

	 

	class ListViewAdapter extends BaseAdapter {
		private Context context; // 运行上下文
		AsyncImageLoader imageLoader;
		private LayoutInflater listContainer; // 视图容器

		public final class ListItemView { // 自定义控件集合

			public TextView text_title;

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
				convertView = listContainer.inflate(R.layout.item_note_info,
						null);
				// 获取控件对象
				listItemView.text_title = (TextView) convertView
						.findViewById(R.id.text_title);
				convertView.setTag(listItemView);
			} else {
				listItemView = (ListItemView) convertView.getTag();
			}
			try {
				JSONObject obj = array.getJSONObject(position);
				String item=obj.getString("item");
				String value=obj.getString("value");
				String range=obj.getString("range");
				String unit=obj.getString("unit");
				
				String title = obj.getString("item");
				listItemView.text_title.setText(title);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return convertView;
		}
	}

	
}
