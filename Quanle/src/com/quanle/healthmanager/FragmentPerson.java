package com.quanle.healthmanager;

import java.util.ArrayList;
import java.util.HashMap;

import com.quanle.healthmanager.utils.SharedPreferencesHelper;
import com.quanle.healthmanager.utils.User;
import com.quanle.healthmanager.widget.CustomDialog;
import com.quanle.healthmanager.widget.Menu;
import com.quanle.healthmanager.widget.NavigationBar;
import com.quanle.healthmanager.widget.Menu.OnMenuClickListener;
import com.quanle.healthmanager.widget.NavigationBar.OnClickButtonListener;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FragmentPerson extends Fragment {
	View parentView;
	private ImageView faceImageView;
	private User user;
	private Menu menu;
	private ImageView camarImageView;
	private NavigationBar personNavigationBar;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// if (parentView == null) {
		parentView = inflater.inflate(R.layout.fragment_person, container,
				false);
		personNavigationBar = (NavigationBar) parentView
				.findViewById(R.id.nbPerson);
		personNavigationBar.setText("个人中心");
		user = ((User) getActivity().getApplicationContext());
		faceImageView = (ImageView) parentView.findViewById(R.id.faceImageView);
		camarImageView = (ImageView) parentView
				.findViewById(R.id.camarImageView);
		// }
		ArrayList<ArrayList<HashMap<String, Object>>> menuArrayList = new ArrayList<ArrayList<HashMap<String, Object>>>();
		ArrayList<HashMap<String, Object>> subMenuArrayList = new ArrayList<HashMap<String, Object>>();

		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("tag", "mail");
		map.put("icon", R.drawable.menu_mail);
		map.put("title", getResources().getString(R.string.main_menu_mail));
		map.put("displayRightArray", true);
		map.put("visible", false);
		subMenuArrayList.add(map);

		map = new HashMap<String, Object>();
		map.put("tag", "changepass");
		map.put("icon", R.drawable.menu_changepass);
		map.put("title",
				getResources().getString(R.string.main_menu_changepassword));
		map.put("displayRightArray", true);
		map.put("visible", false);
		subMenuArrayList.add(map);

		map = new HashMap<String, Object>();
		map.put("tag", "changeinfo");
		map.put("icon", R.drawable.menu_personinfo);
		map.put("title", getResources()
				.getString(R.string.main_menu_changeinfo));
		map.put("displayRightArray", true);
		map.put("visible", false);
		subMenuArrayList.add(map);

		map = new HashMap<String, Object>();
		map.put("tag", "tag");
		map.put("icon", R.drawable.menu_tag);
		map.put("title", getResources().getString(R.string.main_menu_tag));
		map.put("displayRightArray", true);
		map.put("visible", false);
		subMenuArrayList.add(map);

		map = new HashMap<String, Object>();
		map.put("tag", "family");
		map.put("icon", R.drawable.menu_family);
		map.put("title", getResources().getString(R.string.main_menu_family));
		map.put("displayRightArray", true);
		map.put("visible", false);
		subMenuArrayList.add(map);

		menuArrayList.add(subMenuArrayList);

		subMenuArrayList = new ArrayList<HashMap<String, Object>>();

		map = new HashMap<String, Object>();
		map.put("tag", "city");
		map.put("icon", R.drawable.menu_city);
		map.put("title", getResources()
				.getString(R.string.main_menu_changecity));
		map.put("value", "");
		map.put("displayRightArray", true);
		subMenuArrayList.add(map);

		map = new HashMap<String, Object>();
		map.put("tag", "age");
		map.put("icon", R.drawable.menu_age);
		map.put("title", getResources().getString(R.string.main_menu_age));
		map.put("displayRightArray", true);
		subMenuArrayList.add(map);

		menuArrayList.add(subMenuArrayList);

		subMenuArrayList = new ArrayList<HashMap<String, Object>>();

		map = new HashMap<String, Object>();
		map.put("tag", "system");
		map.put("icon", R.drawable.menu_system);
		map.put("title", getResources().getString(R.string.main_menu_system));
		map.put("displayRightArray", true);
		subMenuArrayList.add(map);

		menuArrayList.add(subMenuArrayList);
		menu = (Menu) parentView.findViewById(R.id.menu);
		menu.setMenu(menuArrayList);
		menu.setOnMenuClickListener(new OnMenuClickListener() {
			public void onClick(String tagString) {
				if (tagString == "system") {
					Intent intent = new Intent();
					intent.setClass(getActivity(), SystemActivity.class);
					startActivity(intent);
					getActivity().overridePendingTransition(
							R.anim.in_from_right, R.anim.out_to_left);
				} else if (tagString == "changepass") {
					Intent intent = new Intent();
					intent.setClass(getActivity(), ChangePassActivity.class);
					startActivity(intent);
					getActivity().overridePendingTransition(
							R.anim.in_from_right, R.anim.out_to_left);

				} else if (tagString == "changeinfo") {
					Intent intent = new Intent();
					intent.setClass(getActivity(), ChangeInfoActivity.class);
					startActivity(intent);
					getActivity().overridePendingTransition(
							R.anim.in_from_right, R.anim.out_to_left);
				} else if (tagString.equals("city")) {
					Intent intent = new Intent();
					intent.setClass(getActivity(), CitySelectActivity.class);
					startActivity(intent);
					getActivity().overridePendingTransition(
							R.anim.in_from_right, R.anim.out_to_left);
				}
			}
		});
		Button loginButton = (Button) parentView.findViewById(R.id.loginButton);
		loginButton.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(getActivity(), LoginActivity.class);
				startActivity(intent);
				getActivity().overridePendingTransition(R.anim.in_from_right,
						R.anim.out_to_left);
			}
		});

		Button regButton = (Button) parentView.findViewById(R.id.regButton);
		regButton.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(getActivity(), RegActivity.class);
				startActivity(intent);
				getActivity().overridePendingTransition(R.anim.in_from_right,
						R.anim.out_to_left);
			}
		});

		// TODO Auto-generated method stub
		return parentView;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		LinearLayout guestLayout = (LinearLayout) parentView
				.findViewById(R.id.guestLayout);
		LinearLayout userLayout = (LinearLayout) parentView
				.findViewById(R.id.userLayout);

		if (user.getFace() != null) {
			System.out.println("found user face");
			faceImageView.setImageBitmap(user.getFace());
		} else {
			System.out.println("not found user face");
			faceImageView.setImageResource(R.drawable.face);
		}

		if (user.getLandStatus() > 0) {
			TextView nickTextView = (TextView) parentView
					.findViewById(R.id.nickTextView);

			guestLayout.setVisibility(View.GONE);
			userLayout.setVisibility(View.VISIBLE);
			nickTextView.setText(user.getNickName());
			menu.setValue("city", user.getCityString());
			menu.setDisplay("family", true);
			menu.setDisplay("changeinfo", true);
			menu.setDisplay("tag", true);
			if (user.getLandStatus() != User.LANDSTATUS_MAIL) {
				menu.setDisplay("mail", true);
				menu.setDisplay("changepass", false);
			} else {
				menu.setDisplay("mail", false);
				menu.setDisplay("changepass", true);
			}
			camarImageView.setImageDrawable(getResources().getDrawable(
					R.drawable.camar));
			personNavigationBar.setButtonDisplay(true);
			personNavigationBar.setButtonText(R.string.main_btn_logout);
			personNavigationBar
					.setOnClickButtonListener(new OnClickButtonListener() {
						@Override
						public void onClick() {
							CustomDialog.Builder builder;
							CustomDialog alertDialog;
							builder = new CustomDialog.Builder(getActivity());
							builder.setTitle(R.string.main_alert_message_logout)
									.setPositiveButton(
											R.string.alert_message_cancel,
											new OnClickListener() {
												@Override
												public void onClick(
														DialogInterface dialog,
														int which) {
													dialog.dismiss();

												}
											})
									.setNegativeButton(
											R.string.alert_message_ok,
											new OnClickListener() {
												@Override
												public void onClick(
														DialogInterface dialog,
														int which) {
													dialog.dismiss();

													user.setLandStatus(0);
													user.setCityString(null);
													user.setFace(null);
													user.setNickName(null);
													user.setGender(-1);
													user.setProvinceString(null);
													user.setTokenString(null);
													user.setIDCard(null);
													user.setRndCodeString(null);
													user.setUID(0);
													user.setBirthDate(null);

													SharedPreferencesHelper sph = new SharedPreferencesHelper(
															getActivity(),
															"config");
													sph.putInt("loginType", 0);

													Intent intent = new Intent();
													intent.setClass(
															getActivity(),
															LoginActivity.class);
													startActivity(intent);
													getActivity()
															.overridePendingTransition(
																	R.anim.in_from_right,
																	R.anim.out_to_left);
												}
											});
							alertDialog = builder.create();
							alertDialog.show();
						}
					});

			// fourmNavigationBar.setButtonDisplay(true);
			// fourmNavigationBar.setButtonText(R.string.fourm_btn_create);
			// fourmNavigationBar
			// .setOnClickButtonListener(new OnClickButtonListener() {
			// @Override
			// public void onClick() {
			// Intent intent = new Intent();
			// intent.setClass(getActivity(), FourmEditActivity.class);
			// startActivity(intent);
			// getActivity().overridePendingTransition(R.anim.in_from_right,
			// R.anim.out_to_left);
			// }
			// });
		} else {
			guestLayout.setVisibility(View.VISIBLE);
			userLayout.setVisibility(View.GONE);
			menu.setDisplay("family", false);
			menu.setDisplay("changeinfo", false);
			menu.setDisplay("mail", false);
			menu.setDisplay("tag", false);
			menu.setDisplay("changepass", false);
			personNavigationBar.setButtonDisplay(false);
			camarImageView.setImageDrawable(null);
		}

		if (user.getBirthDate() == null) {
			SharedPreferencesHelper sph = new SharedPreferencesHelper(
					getActivity(), "config");
			String[] ageStrings = getResources().getStringArray(
					R.array.select_age_tip);

			menu.setValue("age",
					ageStrings[Integer.parseInt(sph.getString("age"))]);
		}
		super.onResume();
	}

}
