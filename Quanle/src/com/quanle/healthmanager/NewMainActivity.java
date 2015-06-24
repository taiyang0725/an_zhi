package com.quanle.healthmanager;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewGroup;

import com.quanle.healthmanager.widget.Tabbar;

public class NewMainActivity extends FragmentActivity {
//	FragmentHome fragHome;
//	FragmentNote fragNote;
//	FragmentForum fragForum;
//	FragmentRegist fragRegist;
//	FragmentStore fragStore;
//	FragmentPerson fragPerson;
	// private ViewGroup rootView;
	private Tabbar mainTabbar;
	private FragmentManager fragmentManager;

	// NavigationBar nbMain;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_new);
		fragmentManager = getSupportFragmentManager();
		// fragHome = new FragmentHome();
		// fragNote = new FragmentNote();
		// fragForum = new FragmentForum();
		// fragRegist = new FragmentRegist();
		// fragStore = new FragmentStore();
		// fragPerson = new FragmentPerson();
		// changeFragment(fragHome);
		showFrag(0);
		// String[] navTitleString = getResources().getStringArray(
		// R.array.main_nav_title);
		// nbMain.setText(navTitleString[0]);

		// rootView = (ViewGroup) getRootView(this);

		int[] tabbarPressedIconInt = new int[] { R.drawable.home_pressed,
				R.drawable.log_pressed, R.drawable.fourm_pressed,
				R.drawable.appointment_pressed, R.drawable.store_pressed,
				R.drawable.person_pressed };
		int[] tabbarIconInt = new int[] { R.drawable.home_normal,
				R.drawable.log_normal, R.drawable.fourm_normal,
				R.drawable.appointment_normal, R.drawable.store_normal,
				R.drawable.person_normal };
		String[] tabbarItemTitleString = getResources().getStringArray(
				R.array.main_toolbar_item_title);
		ArrayList<HashMap<String, Object>> tabbarArrayList = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < tabbarItemTitleString.length; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("title", tabbarItemTitleString[i]);
			map.put("normalIcon", tabbarIconInt[i]);
			map.put("pressedIcon", tabbarPressedIconInt[i]);
			tabbarArrayList.add(map);
		}

		mainTabbar = (Tabbar) findViewById(R.id.tbMain);
		mainTabbar.setNormalTextColor(getResources().getColor(R.color.gary));
		mainTabbar.setPressedTextColor(getResources().getColor(R.color.blue));
		mainTabbar.setItem(tabbarArrayList);
		mainTabbar.setSelectIndex(0);

		// for (int i = 0; i < mainTabbar.getTabCount(); i++) {
		// RelativeLayout rlLayout = (RelativeLayout) rootView.getChildAt(i);
		// rlLayout.setVisibility((i == 0) ? View.VISIBLE : View.GONE);
		// NavigationBar navBar = (NavigationBar) rlLayout.getChildAt(0);
		// navBar.setText(navTitleString[i]);
		// navBar.setBackDisplayable(false);
		// }

		mainTabbar.setOnTabChengedListener(new Tabbar.OnTabChengedListener() {
			@Override
			public void onTabChanged(int index) {
				// String[] navTitleString = getResources().getStringArray(
				// R.array.main_nav_title);
				// nbMain.setText(navTitleString[index]);
				showFrag(index);
			}
		});

	}

	public void showFrag(int index) {
//		FragmentTransaction transaction = fragmentManager.beginTransaction();
//		hideFragments(transaction);
		switch (index) {
		case 0:
			changeFragment(new FragmentHome());
//			if (fragHome == null) {
//				// ���MessageFragmentΪ�գ��򴴽�һ������ӵ�������
//				fragHome = new FragmentHome();
//				transaction.add(R.id.main_fragment, fragHome);
//			} else {
//				// ���MessageFragment��Ϊ�գ���ֱ�ӽ�����ʾ����
//				 
//				transaction.show(fragHome);
//			}
			break;
		case 1:
			changeFragment(new FragmentNote());
//			if (fragNote == null) {
//				// ���MessageFragmentΪ�գ��򴴽�һ������ӵ�������
//				fragNote = new FragmentNote();
//				transaction.add(R.id.main_fragment, fragNote);
//			} else {
//				// ���MessageFragment��Ϊ�գ���ֱ�ӽ�����ʾ����
//				transaction.show(fragNote);
//			}

			break;
		case 2:
			changeFragment(new FragmentForum());
//			if (fragForum == null) {
//				// ���MessageFragmentΪ�գ��򴴽�һ������ӵ�������
//				fragForum = new FragmentForum();
//				transaction.add(R.id.main_fragment, fragForum);
//			} else {
//				// ���MessageFragment��Ϊ�գ���ֱ�ӽ�����ʾ����
//				transaction.show(fragForum);
//			}
			break;
		case 3:
			changeFragment(new FragmentRegist());
//			if (fragRegist == null) {
//				// ���MessageFragmentΪ�գ��򴴽�һ������ӵ�������
//				fragRegist = new FragmentRegist();
//				transaction.add(R.id.main_fragment, fragRegist);
//			} else {
//				// ���MessageFragment��Ϊ�գ���ֱ�ӽ�����ʾ����
//				transaction.show(fragRegist);
//			}

			break;
		case 4:
			changeFragment(new FragmentStore());
//			if (fragStore == null) {
//				// ���MessageFragmentΪ�գ��򴴽�һ������ӵ�������
//				fragStore = new FragmentStore();
//				transaction.add(R.id.main_fragment, fragStore);
//			} else {
//				// ���MessageFragment��Ϊ�գ���ֱ�ӽ�����ʾ����
//				transaction.show(fragStore);
//			}

			break;
		case 5:
			changeFragment(new FragmentPerson());
//			if (fragPerson == null) {
//				// ���MessageFragmentΪ�գ��򴴽�һ������ӵ�������
//				fragPerson = new FragmentPerson();
//				transaction.add(R.id.main_fragment, fragPerson);
//			} else {
//				// ���MessageFragment��Ϊ�գ���ֱ�ӽ�����ʾ����
//				transaction.show(fragPerson);
//			}

			break;

		}
//		transaction.addToBackStack(null);
//		transaction.commit();
	}

//	/**
//	 * �����е�Fragment����Ϊ����״̬��
//	 * 
//	 * @param transaction
//	 *            ���ڶ�Fragmentִ�в���������
//	 */
//	private void hideFragments(FragmentTransaction transaction) {
//		if (fragHome != null) {
//			transaction.hide(fragHome);
//		}
//		if (fragNote != null) {
//			transaction.hide(fragNote);
//		}
//		if (fragForum != null) {
//			transaction.hide(fragForum);
//		}
//		if (fragRegist != null) {
//			transaction.hide(fragRegist);
//		}
//		if (fragStore != null) {
//			transaction.hide(fragStore);
//		}
//		if (fragPerson != null) {
//			transaction.hide(fragPerson);
//		}
//	}

	// private static View getRootView(Activity context) {
	// return ((ViewGroup) context.findViewById(android.R.id.content))
	// .getChildAt(0);
	// }

	private void changeFragment(Fragment targetFragment) {

		// getSupportFragmentManager().beginTransaction()
		// .replace(R.id.main_fragment, targetFragment, "fragment")
		// .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
		// .commit();
		// getSupportFragmentManager().beginTransaction().addToBackStack(null);

		FragmentTransaction transaction = fragmentManager.beginTransaction();
		// Replace whatever is in the fragment_container view with this
		// fragment,
		// and add the transaction to the back stack
		transaction.replace(R.id.main_fragment, targetFragment);
//		transaction.addToBackStack(null);
		// Commit the transaction
		transaction.commit();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

}
