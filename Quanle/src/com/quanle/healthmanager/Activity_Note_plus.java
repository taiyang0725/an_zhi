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

public class Activity_Note_plus extends Activity {

	private NavigationBar navigationBar;
	ListView noteListView;
	JSONArray array = new JSONArray();
	ListViewAdapter lsitviewadapter;
	// ListViewEidtAdapter listviewEidtAdapter;
	RelativeLayout lay_select;
	LinearLayout lay_edit;
	ScrollView lay_edit_scroll;
	HashMap<String, String> mapvalue = new HashMap<String, String>();
	String categoryId = "";;
	String type="";
	String no="";

	// ListView noteEditListView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_note_plus);
		categoryId = getIntent().getStringExtra("subjectID");
		type = getIntent().getStringExtra("type");
		no = getIntent().getStringExtra("no");
		lay_select = (RelativeLayout) findViewById(R.id.lay_select);
		lay_edit = (LinearLayout) findViewById(R.id.lay_edit);
		lay_edit_scroll = (ScrollView) findViewById(R.id.lay_edit_scroll);
		navigationBar = (NavigationBar) findViewById(R.id.nbNote);
		navigationBar.setText("添加纪录");
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
				try {
					// TODO Auto-generated method stub
					JSONObject obj = array.getJSONObject(arg2);
					categoryId = obj.getString("id");
					loadEdit(categoryId);
				} catch (Exception e) {
					e.printStackTrace();
				}

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
		if (type.equals("add")) {
			loadGroup();
		} else if (type.equals("browser")) {
			loadEdit(categoryId);
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		// if (lay_select.getVisibility() == View.GONE) {
		// lay_select.setVisibility(View.VISIBLE);
		// lay_edit_scroll.setVisibility(View.GONE);
		// navigationBar.setButtonDisplay(false);
		// } else {
		super.onBackPressed();
		// }
	}

	public void loadGroup() {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		NetListener net = new NetListener(this);
		net.ask(params, "getPhysicalCategoryList", new CallBack() {

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

	public void save() {
		User user = (User) getApplicationContext();
		int usrid = user.getUID();
		String paramIds = "";
		String paramValues = "";

		for (Iterator itr = mapvalue.keySet().iterator(); itr.hasNext();) {
			String key = (String) itr.next();
			String value = (String) mapvalue.get(key);
			if (!value.equals("")) {
				paramIds += key + "|";
				paramValues += value + "|";
			}
		}

		if (paramIds.equals("")) {
			Toast.makeText(this, "请至少填写一项", Toast.LENGTH_LONG).show();
			return;
		}
		paramIds = paramIds.substring(0, paramIds.length() - 1);
		paramValues = paramValues.substring(0, paramValues.length() - 1);

		List<NameValuePair> params = new ArrayList<NameValuePair>();

		params.add(new BasicNameValuePair("id", usrid + ""));
		params.add(new BasicNameValuePair("no", no));
		params.add(new BasicNameValuePair("g", categoryId));
		params.add(new BasicNameValuePair("i", paramIds));
		params.add(new BasicNameValuePair("v", paramValues));
		NetListener net = new NetListener(this);
		net.ask(params, "setPhysical", new CallBack() {

			@Override
			public void onOver(JSONObject obj) {
				// TODO Auto-generated method stub
				System.out.println(obj);
				Toast.makeText(Activity_Note_plus.this, "提交成功",
						Toast.LENGTH_LONG).show();
				finish();

			}

		});
	}

	public void loadEdit(String id) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();

		NetListener net = new NetListener(this);
		net.ask(params, "getPhysicalList&id=" + id, new CallBack() {

			@Override
			public void onOver(JSONObject obj) {
				if (obj != null) {
					try {
						array = obj.getJSONArray("list");
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					if (type.equals("browser")) {
						iniBrowserModel();
					} else {
						iniEditorModel();
					}

				}
				// TODO Auto-generated method stub

			}
		});
	}

	public void iniEditorModel() {
		try {
			lay_edit.removeAllViews();
			lay_edit_scroll.setVisibility(View.VISIBLE);
			lay_select.setVisibility(View.GONE);
			navigationBar.setButtonDisplay(true);
			navigationBar.getBtnButton().setTag("edit");
			navigationBar.setButtonText(R.string.note_plus_save);
			navigationBar.setOnClickButtonListener(new OnClickButtonListener() {

				@Override
				public void onClick() {
					// TODO Auto-generated method stub
					save();
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
		iniEditList(true);
	}

	public void iniBrowserModel() {
		try {
			lay_edit.removeAllViews();
			lay_edit_scroll.setVisibility(View.VISIBLE);
			lay_select.setVisibility(View.GONE);
			navigationBar.setButtonDisplay(true);
			navigationBar.getBtnButton().setTag("browser");
			navigationBar.setButtonText(R.string.note_plus_edit);
			navigationBar.setOnClickButtonListener(new OnClickButtonListener() {

				@Override
				public void onClick() {
					// TODO Auto-generated method stub
					if(navigationBar.getBtnButton().getTag().equals("browser")){
						navigationBar.setButtonText(R.string.note_plus_save);
						navigationBar.getBtnButton().setTag("edit");
						iniEditList(true);
					}else{
						save();
					}
					
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
		User user = (User) getApplicationContext();
		int usrid = user.getUID();
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		NetListener net = new NetListener(Activity_Note_plus.this);
		net.ask(params, "getUserPhysical&id=" + usrid + "&p=" + no,
				new CallBack() {

					@Override
					public void onOver(JSONObject obj) {
						if (obj != null) {
							try {
								JSONArray list = obj.getJSONArray("list");
								for (int i = 0; i < list.length(); i++) {
									String id = list.getJSONObject(i)
											.getString("id");
									String value = list.getJSONObject(i)
											.getString("value");
									mapvalue.put(id, value);
								}
								iniEditList(false);

							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						// TODO Auto-generated method stub
					}
				});
	}

	public void iniEditList(boolean isEditor) {
		lay_edit.removeAllViews();
		for (int i = 0; i < array.length(); i++) {

			View convertView = LayoutInflater.from(Activity_Note_plus.this)
					.inflate(R.layout.item_note_plus_edit, null);

			// 获取控件对象
			TextView text_title = (TextView) convertView
					.findViewById(R.id.text_title);
			TextView text_range = (TextView) convertView
					.findViewById(R.id.text_range);
			TextView text_unit = (TextView) convertView
					.findViewById(R.id.text_unit);
			EditText text_edit = (EditText) convertView
					.findViewById(R.id.text_edit);
			text_edit.setTag(i + "");

			try {
				JSONObject objvalue = array.getJSONObject(i);
				final String id = objvalue.getString("id");
				if (!mapvalue.containsKey(id)) {
					mapvalue.put(id, "");
				}

				String title = objvalue.getString("title");
				String range = objvalue.getString("range");
				String unit = objvalue.getString("unit");
				text_title.setText(title);
				text_range.setText("范围:" + range);
				text_unit.setText(unit);
				View line = LayoutInflater.from(Activity_Note_plus.this)
						.inflate(R.layout.item_note_plus_edit_line, null);
				text_edit.setEnabled(isEditor);
				if (isEditor || !mapvalue.get(id).equals("")) {
					text_edit.setText(mapvalue.get(id));
					text_edit.addTextChangedListener(new TextWatcher() {

						@Override
						public void onTextChanged(CharSequence s, int start,
								int before, int count) {
							// TODO Auto-generated method
							// stub

						}

						@Override
						public void beforeTextChanged(CharSequence s,
								int start, int count, int after) {
							// TODO Auto-generated method
							// stub

						}

						@Override
						public void afterTextChanged(Editable s) {
							// TODO Auto-generated method
							// stub
							mapvalue.put(id, s.toString());
						}
					});
					lay_edit.addView(convertView);
					lay_edit.addView(line);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
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
				convertView = listContainer.inflate(R.layout.item_note_plus,
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
				String title = obj.getString("title");
				listItemView.text_title.setText(title);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return convertView;
		}
	}

	// class ListViewEidtAdapter extends BaseAdapter {
	// private Context context; // 运行上下文
	// AsyncImageLoader imageLoader;
	// private LayoutInflater listContainer; // 视图容器
	//
	// public final class ListItemView { // 自定义控件集合
	//
	// public TextView text_title;
	// public TextView text_unit;
	// public TextView text_range;
	// public EditText text_edit;
	//
	// }
	//
	// public ListViewEidtAdapter(Context context) {
	// this.context = context;
	// listContainer = LayoutInflater.from(context); // 创建视图容器并设置上下文
	// imageLoader = new AsyncImageLoader(context);
	// }
	//
	// @Override
	// public void notifyDataSetChanged() {
	// // TODO Auto-generated method stub
	//
	// super.notifyDataSetChanged();
	// }
	//
	// public int getCount() {
	// // TODO Auto-generated method stub
	// return array.length();
	// }
	//
	// public Object getItem(int arg0) {
	// // TODO Auto-generated method stub
	// try {
	// return array.getJSONObject(arg0);
	// } catch (JSONException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// return null;
	// }
	//
	// public long getItemId(int arg0) {
	// // TODO Auto-generated method stub
	// return 0;
	// }
	//
	// /**
	// * ListView Item设置
	// */
	// public View getView(final int position, View convertView,
	// ViewGroup parent) {
	// // TODO Auto-generated method stub
	// Log.e("method", "getView");
	// ListItemView listItemView = null;
	// if (convertView == null) {
	// listItemView = new ListItemView();
	// // 获取list_item布局文件的视图
	// convertView = listContainer.inflate(
	// R.layout.item_note_plus_edit, null);
	// // 获取控件对象
	// listItemView.text_title = (TextView) convertView
	// .findViewById(R.id.text_title);
	// listItemView.text_range = (TextView) convertView
	// .findViewById(R.id.text_range);
	// listItemView.text_unit = (TextView) convertView
	// .findViewById(R.id.text_unit);
	// listItemView.text_edit = (EditText) convertView
	// .findViewById(R.id.text_edit);
	//
	//
	// convertView.setTag(listItemView);
	// } else {
	// listItemView = (ListItemView) convertView.getTag();
	// }
	// try {
	// JSONObject obj = array.getJSONObject(position);
	// String title = obj.getString("title");
	// String range=obj.getString("range");
	// String unit=obj.getString("unit");
	// listItemView.text_title.setText(title);
	// listItemView.text_range.setText("范围:"+range);
	// listItemView.text_unit.setText(unit);
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// return convertView;
	// }
	// }
}
