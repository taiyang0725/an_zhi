package com.quanle.healthmanager;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.javis.Adapter.Adapter_GridView;
import com.javis.Adapter.Adapter_GridView_hot;
import com.javis.ab.view.AbOnItemClickListener;
import com.javis.ab.view.AbSlidingPlayView;
import com.quanle.healthmanager.utils.DownImg;
import com.quanle.healthmanager.utils.NetListener;
import com.quanle.healthmanager.utils.NetListener.CallBack;
import com.quanle.healthmanager.widget.NavigationBar;

public class FragmentStore extends Fragment {

	// 分类的九宫格
	private GridView gridView_classify;
	// 热门市场的九宫格
	private GridView my_gridView_hot;
	private Adapter_GridView adapter_GridView_catetory;
	private Adapter_GridView_hot adapter_GridView_products;
	// 首页轮播
	private AbSlidingPlayView viewPager;

	// 分类九宫格的资源文件
	private JSONArray catetory = new JSONArray();
	// 热门市场的资源文件
	private JSONArray products = new JSONArray();
	/** 存储首页轮播的界面 */
	private ArrayList<View> allListView;

	JSONObject data = new JSONObject();
	View parentView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		 
		parentView = inflater.inflate(R.layout.fragment_store, container, false);
		NavigationBar navigationBar = (NavigationBar) parentView
				.findViewById(R.id.storeLayout);
		navigationBar.setText("健康商城");
		initView(parentView);
		load();
		return parentView;
	}

	public void load() {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		NetListener net = new NetListener(getActivity());
		net.ask(params, "getStore", new CallBack() {

			@Override
			public void onOver(JSONObject obj) {
				try {
					// TODO Auto-generated method stub
					if (obj != null) {
						data = obj;
						catetory = data.getJSONArray("catetory");
						products = data.getJSONArray("products");
						adapter_GridView_catetory.setData(catetory);
						adapter_GridView_products.setData(products);
						adapter_GridView_catetory.notifyDataSetChanged();
						adapter_GridView_products.notifyDataSetChanged();
						inidata();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		});

	}

	public void inidata() {
		initViewPager();
	}

	private void initView(View view) {
		gridView_classify = (GridView) view.findViewById(R.id.my_gridview);
		my_gridView_hot = (GridView) view.findViewById(R.id.my_gridview_hot);
		gridView_classify.setSelector(new ColorDrawable(Color.TRANSPARENT));
		my_gridView_hot.setSelector(new ColorDrawable(Color.TRANSPARENT));
		adapter_GridView_catetory = new Adapter_GridView(getActivity(),
				catetory);
		adapter_GridView_products = new Adapter_GridView_hot(getActivity(),
				products);
		gridView_classify.setAdapter(adapter_GridView_catetory);
		my_gridView_hot.setAdapter(adapter_GridView_products);

		viewPager = (AbSlidingPlayView) view.findViewById(R.id.viewPager_menu);
		// 设置播放方式为顺序播放
		viewPager.setPlayType(1);
		// 设置播放间隔时间
		viewPager.setSleepTime(3000);

		gridView_classify.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// 挑战到宝贝搜索界面

			}
		});
		my_gridView_hot.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// 跳转到宝贝详情界面

			}
		});

	}

	private void initViewPager() {

		if (allListView != null) {
			allListView.clear();
			allListView = null;
		}
		allListView = new ArrayList<View>();

		try {
			for (int i = 0; i < data.getJSONArray("slide").length(); i++) {
				// 导入ViewPager的布局
				View view = LayoutInflater.from(getActivity()).inflate(
						R.layout.pic_item, null);
				ImageView imageView = (ImageView) view
						.findViewById(R.id.pic_item);
				DownImg d = new DownImg(imageView);
				d.execute("http://120.27.39.61/PlatManager/ShopManager/images/cjryp.jpg");
				allListView.add(view);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		viewPager.addViews(allListView);
		// 开始轮播
		viewPager.startPlay();
		viewPager.setOnItemClickListener(new AbOnItemClickListener() {
			@Override
			public void onClick(int position) {
				// 跳转到详情界面

			}
		});
	}
}
