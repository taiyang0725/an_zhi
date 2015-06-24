package com.quanle.healthmanager.widget;

import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.text.InputFilter;
import android.text.method.DigitsKeyListener;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.quanle.healthmanager.R;
import com.quanle.healthmanager.utils.DisplayUtil;

public class Menu extends LinearLayout {
	public static final int VALUEALIGN_LEFT = 1;
	public static final int VALUEALIGN_RIGHT = 0;
	public static int EDITTEXT = 1;
	public static int TEXTVIEW = 0;
	private int titleWidth = 0;

	private ArrayList<ArrayList<HashMap<String, Object>>> menuArrayList;

	public OnMenuClickListener mOnMenuClickListener;

	public Menu(Context context, AttributeSet attrs) {
		super(context, attrs);
		setOrientation(VERTICAL);
		init();
	}

	private void init() {
		if (menuArrayList == null || menuArrayList.size() == 0)
			return;

		for (int i = 0; i < menuArrayList.size(); i++) {
			boolean visible = false;
			ArrayList<HashMap<String, Object>> subMenuArrayList = menuArrayList
					.get(i);
			BorderLinearLayout menuLayout = new BorderLinearLayout(getContext());
			addView(menuLayout);
			menuLayout.setBackgroundColor(Color.WHITE);
			menuLayout.setOrientation(VERTICAL);
			menuLayout
					.setPadding(DisplayUtil.dip2px(getContext(), 10), 0, 0, 0);
			menuLayout.setBorderColor(getResources().getColor(
					R.color.borderColor));
			menuLayout.setBorderSize(0, DisplayUtil.dip2px(getContext(), 0.5f),
					0, DisplayUtil.dip2px(getContext(), 0.5f));
			LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT);
			params.topMargin = DisplayUtil.dip2px(getContext(), 20);
			menuLayout.setLayoutParams(params);

			for (int j = 0; j < subMenuArrayList.size(); j++) {
				HashMap<String, Object> map = subMenuArrayList.get(j);

				BorderLinearLayout subMenuLayout = new BorderLinearLayout(
						getContext());
				menuLayout.addView(subMenuLayout);
				subMenuLayout.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.item));
				subMenuLayout.setClickable(true);
				subMenuLayout.setTag(map.get("tag"));
				subMenuLayout.setGravity(Gravity.CENTER_VERTICAL);
				subMenuLayout.setPadding(0,
						DisplayUtil.dip2px(getContext(), 10),
						DisplayUtil.dip2px(getContext(), 10),
						DisplayUtil.dip2px(getContext(), 10));
				subMenuLayout.setBorderColor(getResources().getColor(
						R.color.background_activity));
				subMenuLayout.setBorderSize(0,
						DisplayUtil.dip2px(getContext(), 0.5f), 0, 0);
				params = new LayoutParams(LayoutParams.MATCH_PARENT,
						LayoutParams.WRAP_CONTENT);
				subMenuLayout.setLayoutParams(params);

				final String tagString = (String) map.get("tag");

				int iconInt = 0;
				if (map.get("icon") != null)
					iconInt = (Integer) map.get("icon");
				String valueString = "";
				if (map.get("value") != null)
					valueString = (String) map.get("value");

				ImageView icon = new ImageView(getContext());
				if (iconInt > 0) {
					icon.setImageResource(iconInt);
					icon.setVisibility(View.VISIBLE);
				} else
					icon.setVisibility(View.GONE);
				subMenuLayout.addView(icon);
				params = new LayoutParams(LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT);
				params.rightMargin = DisplayUtil.dip2px(getContext(), 5);
				icon.setLayoutParams(params);

				TextView title = new TextView(getContext());
				title.setText((String) map.get("title"));
				subMenuLayout.addView(title);
				if (titleWidth > 0) {
					params = new LayoutParams(DisplayUtil.dip2px(getContext(),
							titleWidth), LayoutParams.WRAP_CONTENT);
					title.setLayoutParams(params);
				}

				int valueTypeInt = 0;
				if (map.get("valueType") != null)
					valueTypeInt = (Integer) map.get("valueType");

				int valueAlignInt = 0;
				if (map.get("valueAlign") != null)
					valueAlignInt = (Integer) map.get("valueAlign");

				if (valueTypeInt == EDITTEXT) {
					EditText valueEditText = new EditText(getContext());
					valueEditText.setText(valueString);
					valueEditText.setTextColor(getResources().getColor(
							R.color.gary));
					valueEditText.setBackgroundDrawable(null);
					valueEditText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
					valueEditText.setPadding(0, 0, 0, 0);

					if (map.get("editTextMaxLength") != null)
						valueEditText
								.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
										(Integer) map.get("editTextMaxLength")) });
					if (map.get("editTextMaxEms") != null)
						valueEditText.setMaxEms((Integer) map
								.get("editTextMaxEms"));
					if (map.get("editTextHint") != null)
						valueEditText
								.setHint((Integer) map.get("editTextHint"));
					if (map.get("editTextInputType") != null)
						valueEditText.setInputType((Integer) map
								.get("editTextInputType"));
					if (map.get("editTextDigits") != null)
						valueEditText
								.setKeyListener(DigitsKeyListener
										.getInstance((String) map
												.get("editTextDigits")));

					subMenuLayout.addView(valueEditText);
					params = new LayoutParams(LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT);
					params.weight = 1;
					valueEditText.setLayoutParams(params);

					subMenuLayout.setClickable(false);
					subMenuLayout.setBackgroundDrawable(null);
				} else {
					TextView value = new TextView(getContext());
					value.setText(valueString);
					value.setTextColor(getResources().getColor(R.color.gary));
					if (valueAlignInt == VALUEALIGN_RIGHT)
						value.setGravity(Gravity.RIGHT);
					value.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
					subMenuLayout.addView(value);
					params = new LayoutParams(LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT);
					params.weight = 1;
					value.setLayoutParams(params);
				}

				ImageView loading = new ImageView(getContext());
				if (map.get("displayLoading") != null
						&& (Boolean) map.get("displayLoading")) {
					loading.setImageResource(R.drawable.loading);
					loading.setVisibility(VISIBLE);
				} else
					loading.setVisibility(GONE);
				subMenuLayout.addView(loading);

				if (map.get("displayRightArray") != null
						&& (Boolean) map.get("displayRightArray")) {
					ImageView rightArray = new ImageView(getContext());
					rightArray.setImageResource(R.drawable.arrow_right_normal);
					subMenuLayout.addView(rightArray);
				}

				if (map.get("visible") != null && !(Boolean) map.get("visible")) {
					subMenuLayout.setVisibility(GONE);
				} else {
					visible = true;
				}

				subMenuLayout.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (mOnMenuClickListener != null) {
							mOnMenuClickListener.onClick(tagString);
						}

					}
				});
			}

			if (!visible)
				menuLayout.setVisibility(GONE);
		}
	}

	public void setMenu(ArrayList<ArrayList<HashMap<String, Object>>> arrayList) {
		menuArrayList = arrayList;
		init();
	}

	public void setDisplayLoading(int parent, int sub, boolean b) {
		BorderLinearLayout menuLayout = (BorderLinearLayout) getChildAt(parent);
		BorderLinearLayout subMenuLayout = (BorderLinearLayout) menuLayout
				.getChildAt(sub);
		ImageView loading = (ImageView) subMenuLayout.getChildAt(3);
		loading.setVisibility((b) ? VISIBLE : GONE);
	}

	public void setOnMenuClickListener(OnMenuClickListener listener) {
		mOnMenuClickListener = listener;

	}

	public interface OnMenuClickListener {
		public void onClick(String tag);
	}

	public void setTitleWidth(int width) {
		titleWidth = width;

		for (int i = 0; i < getChildCount(); i++) {
			BorderLinearLayout menuLayout = (BorderLinearLayout) getChildAt(i);
			for (int j = 0; j < menuLayout.getChildCount(); j++) {
				BorderLinearLayout subMenuLayout = (BorderLinearLayout) menuLayout
						.getChildAt(j);
				TextView title = (TextView) subMenuLayout.getChildAt(1);
				title.setWidth(DisplayUtil.dip2px(getContext(), width));
			}
		}
	}

	public void setValue(int parent, int sub, String string) {
		BorderLinearLayout menuLayout = (BorderLinearLayout) getChildAt(parent);
		BorderLinearLayout subMenuLayout = (BorderLinearLayout) menuLayout
				.getChildAt(sub);
		if (subMenuLayout.getChildAt(2) instanceof TextView) {
			TextView value = (TextView) subMenuLayout.getChildAt(2);
			value.setText(string);
		} else if (subMenuLayout.getChildAt(2) instanceof EditText) {
			EditText value = (EditText) subMenuLayout.getChildAt(2);
			value.setText(string);
		}

	}

	public void setValue(String tag, String string) {
		int p[] = findPosition(tag);
		if (p[0] > -1 && p[1] > -1)
			setValue(p[0], p[1], string);

	}

	public void setDisplay(int parent, int sub, boolean b) {
		BorderLinearLayout menuLayout = (BorderLinearLayout) getChildAt(parent);
		BorderLinearLayout subMenuLayout = (BorderLinearLayout) menuLayout
				.getChildAt(sub);
		subMenuLayout.setVisibility((b) ? VISIBLE : GONE);
		if (b)
			menuLayout.setVisibility(VISIBLE);

	}

	public void setDisplayLoading(String tag, boolean b) {
		int p[] = findPosition(tag);
		if (p[0] > -1 && p[1] > -1)
			setDisplayLoading(p[0], p[1], b);
	}

	public void setDisplay(String tag, boolean b) {
		int p[] = findPosition(tag);
		if (p[0] > -1 && p[1] > -1)
			setDisplay(p[0], p[1], b);
	}

	public String getValue(int parent, int sub) {
		BorderLinearLayout menuLayout = (BorderLinearLayout) getChildAt(parent);
		BorderLinearLayout subMenuLayout = (BorderLinearLayout) menuLayout
				.getChildAt(sub);

		String valueString = null;
		if (subMenuLayout.getChildAt(2) instanceof TextView) {
			TextView value = (TextView) subMenuLayout.getChildAt(2);
			valueString = value.getText().toString();
		} else if (subMenuLayout.getChildAt(2) instanceof EditText) {
			EditText value = (EditText) subMenuLayout.getChildAt(2);
			valueString = value.getText().toString();
		}

		return valueString;
	}

	public String getValue(String tag) {
		int p[] = findPosition(tag);
		return getValue(p[0], p[1]);
	}

	private int[] findPosition(String tag) {
		int p[] = new int[] { 0, 0 };
		for (int i = 0; i < getChildCount(); i++) {
			BorderLinearLayout menuLayout = (BorderLinearLayout) getChildAt(i);
			for (int j = 0; j < menuLayout.getChildCount(); j++) {
				BorderLinearLayout subMenuLayout = (BorderLinearLayout) menuLayout
						.getChildAt(j);
				if (subMenuLayout.getTag().equals(tag)) {
					p[0] = i;
					p[1] = j;
					break;
				}
			}
		}

		return p;
	}
}
