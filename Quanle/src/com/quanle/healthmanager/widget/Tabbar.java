package com.quanle.healthmanager.widget;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.quanle.healthmanager.R;

public class Tabbar extends LinearLayout {
	public static final int SPLITLINE_TOP = 1;
	public static final int SPLITLINE_BOTTOM = 2;
	public static final int SPLITLINE_GONE = 0;
	public static final int TYPE_ALL = 0;
	public static final int TYPE_ICON_ONLY = 1;
	public static final int TYPE_TEXT_ONLY = 2;

	private int splitLineColor = Color.parseColor("#dddddd");
	private int normalTextColor = Color.parseColor("#999999");
	private int pressedTextColor = Color.parseColor("#1fbaf3");
	private int selectIndex = 0;
	private int splitLine = SPLITLINE_TOP;
	private int type = TYPE_ALL;

	private View viewLine;
	private View viewSelect;
	private LinearLayout ly;
	private ArrayList<HashMap<String, Object>> arrayList = new ArrayList<HashMap<String, Object>>();

	private OnTabChengedListener mTabChangedListener;

	/*
	 * public Tabbar(Context context) { super(context); LayoutInflater inflater
	 * = (LayoutInflater) context
	 * .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	 * inflater.inflate(R.layout.view_tabbar, this); }
	 */

	public Tabbar(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.widget_tabbar, this);

		viewLine = (View) findViewById(R.id.viewLine);
		viewSelect = (View) findViewById(R.id.viewSelect);
		ly = (LinearLayout) findViewById(R.id.lyToolbar);

		int child = ly.getChildCount();
		removeItemAll();
		for (int i = 0; i < child; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("title", "ѡ�" + (1 + i));
			map.put("normalIcon", R.drawable.home_normal);
			map.put("pressedIcon", R.drawable.home_pressed);
			arrayList.add(map);
		}

		init();
	}

	private void init() {

		for (int i = 0; i < arrayList.size(); i++) {
			final int index = i;
			HashMap<String, Object> map = arrayList.get(i);
			TabbarItem ti = new TabbarItem(getContext());

			switch (type) {
			case TYPE_TEXT_ONLY:
				ti.setIconVisibility(false);
				break;

			default:
				System.out.println();
				ti.setNormalIcon((Integer) map.get("normalIcon"));
				ti.setPressedIcon((Integer) map.get("pressedIcon"));
				ti.setIconVisibility(true);
				break;
			}

			ti.setNormalTextColor(normalTextColor);
			ti.setPressedTextColor(pressedTextColor);
			ti.setText((String) map.get("title"));
			ti.setFocusable(true);
			ti.setClickable(true);
			ly.addView(ti);

			LayoutParams params = (LayoutParams) ti.getLayoutParams();
			params.weight = 1;
			ti.setLayoutParams(params);

			ti.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (mTabChangedListener != null) {
						mTabChangedListener.onTabChanged(index);
					}
					setSelectIndex(index);
				}
			});
		}

		viewSelect.setVisibility(type != TYPE_TEXT_ONLY ? View.GONE
				: View.VISIBLE);
		viewSelect.setBackgroundColor(pressedTextColor);

		final TabbarItem ti = (TabbarItem) ly.getChildAt(selectIndex);
		ViewTreeObserver vto = ti.getViewTreeObserver();
		vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
			public boolean onPreDraw() {
				int width = ti.getMeasuredWidth();

				RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) viewSelect
						.getLayoutParams();
				params.width = width;
				viewSelect.setLayoutParams(params);
				return true;
			}
		});

	}

	public int getSelectIndex() {
		return selectIndex;
	}

	public void setSelectIndex(int selectIndex) {
		this.selectIndex = selectIndex;

		for (int j = 0; j < ly.getChildCount(); j++) {
			TabbarItem ti = (TabbarItem) ly.getChildAt(j);
			ti.setChecked(j == selectIndex);
		}

		final TabbarItem ti = (TabbarItem) ly.getChildAt(selectIndex);
		RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) viewSelect
				.getLayoutParams();
		TranslateAnimation translate = new TranslateAnimation(0, ti.getLeft()
				- params.leftMargin, 0, 0);
		translate.setDuration(100);
		translate.setFillAfter(true);
		translate.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationEnd(Animation animation) {
				RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) viewSelect
						.getLayoutParams();
				params.leftMargin = ti.getLeft();
				viewSelect.setLayoutParams(params);
				viewSelect.setAnimation(null);
			}

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}
		});
		viewSelect.setAnimation(translate);
		translate.startNow();

	}

	public void setNormalTextColor(int color) {
		normalTextColor = color;
		for (int i = 0; i < ly.getChildCount(); i++) {
			TabbarItem ti = (TabbarItem) ly.getChildAt(i);
			if (selectIndex != i)
				ti.setNormalTextColor(color);
		}
	}

	public void setNormalTextColor(String string) {
		setNormalTextColor(Color.parseColor(string));
	}

	public void setPressedTextColor(int color) {
		pressedTextColor = color;
		for (int i = 0; i < ly.getChildCount(); i++) {
			TabbarItem ti = (TabbarItem) ly.getChildAt(i);
			if (selectIndex == i)
				ti.setPressedTextColor(color);
		}

		viewSelect.setBackgroundColor(pressedTextColor);
	}

	public void setItem(ArrayList<HashMap<String, Object>> tabbarArrayList) {
		removeItemAll();
		arrayList = tabbarArrayList;
		selectIndex = 0;
		init();
	}

	//
	// public void setItem(int index, String text) {
	// setItem(index, text, 0, 0);
	// }
	//
	// public void setItem(int index, String text, int normalIcon, int
	// pressedIcon) {
	// TabbarItem ti = (TabbarItem) ly.getChildAt(index);
	// ti.setText(text);
	// if (normalIcon > 0)
	// ti.setNormalIcon(normalIcon);
	// if (pressedIcon > 0)
	// ti.setPressedIcon(pressedIcon);
	//
	// // Log.d("Tarbar", text + "  icon " + normalIcon + "," + pressedIcon);
	// ti.setNormalTextColor(normalTextColor);
	// ti.setPressedTextColor(pressedTextColor);
	// ti.setChecked(index == selectIndex);
	// }

	public void setOnTabChengedListener(OnTabChengedListener listener) {
		mTabChangedListener = listener;
	}

	public interface OnTabChengedListener {
		public void onTabChanged(int index);
	}

	public int getTabCount() {
		return ly.getChildCount();
	}

	public void setSplitLine(int line) {
		splitLine = line;

		RelativeLayout.LayoutParams params;
		switch (splitLine) {
		case SPLITLINE_GONE:
			viewLine.setVisibility(View.GONE);
			break;

		case SPLITLINE_BOTTOM:
			params = (RelativeLayout.LayoutParams) viewLine.getLayoutParams();
			params.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
			params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			viewLine.setLayoutParams(params);
			viewLine.setVisibility(View.VISIBLE);

			params = (RelativeLayout.LayoutParams) viewSelect.getLayoutParams();
			params.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
			params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			viewSelect.setLayoutParams(params);
			break;

		case SPLITLINE_TOP:
			params = (RelativeLayout.LayoutParams) viewLine.getLayoutParams();
			params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
			params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);
			viewLine.setLayoutParams(params);
			viewLine.setVisibility(View.VISIBLE);

			params = (RelativeLayout.LayoutParams) viewSelect.getLayoutParams();
			params.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
			params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			viewSelect.setLayoutParams(params);
			break;
		}
	}

	public void setSplitLineColor(int color) {
		splitLineColor = color;
		viewLine.setBackgroundColor(color);
	}

	public void setType(int t) {
		type = t;
		init();
	}

	public void removeItemAll() {
		ly.removeAllViews();
		arrayList.clear();
	}

	public void setBackground(int color) {
		ly.setBackgroundColor(color);
	}

	// public void addItem(String string) {
	// addItem(string, 0, 0);
	// }
	//
	// public void addItem(String string, int normalIcon, int pressedIcon) {
	// TabbarItem ti = new TabbarItem(getContext());
	// ti.setText(string);
	// if (normalIcon > 0)
	// ti.setNormalIcon(normalIcon);
	// if (pressedIcon > 0)
	// ti.setPressedIcon(pressedIcon);
	//
	// ti.setNormalTextColor(normalTextColor);
	// ti.setPressedTextColor(pressedTextColor);
	// ly.addView(ti);
	// LayoutParams params = (LayoutParams) ti.getLayoutParams();
	// params.weight = 1;
	// ti.setLayoutParams(params);
	//
	// selectIndex = 0;
	// init();
	// }
}
