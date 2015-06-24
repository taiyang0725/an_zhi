package com.quanle.healthmanager;

import com.quanle.healthmanager.utils.SharedPreferencesHelper;
import com.quanle.healthmanager.widget.NavigationBar;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SelectAgeActivity extends Activity {
	private Context context = this;
	private Intent sexIntent;

	private LinearLayout layoutAge[];
	private NavigationBar navBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_age);

		sexIntent = this.getIntent();

		navBar = (NavigationBar) findViewById(R.id.navbar);
		navBar.setText(R.string.select_age_title);
		navBar.setBackDisplayable(false);

		layoutAge = new LinearLayout[] {
				(LinearLayout) findViewById(R.id.ageLayout1),
				(LinearLayout) findViewById(R.id.ageLayout2),
				(LinearLayout) findViewById(R.id.ageLayout3),
				(LinearLayout) findViewById(R.id.ageLayout4),
				(LinearLayout) findViewById(R.id.ageLayout5),
				(LinearLayout) findViewById(R.id.ageLayout6) };

		String[] titleString = getResources().getStringArray(
				R.array.select_age_title);
		String[] tipString = getResources().getStringArray(
				R.array.select_age_tip);

		for (int i = 0; i < layoutAge.length; i++) {
			final int ind = i;
			LinearLayout layout = (LinearLayout) layoutAge[i].getChildAt(1);
			TextView textView = (TextView) layout.getChildAt(0);
			textView.setText(titleString[i]);
			textView = (TextView) layout.getChildAt(1);
			textView.setText(tipString[i]);

			layoutAge[i].setClickable(true);
			layoutAge[i].setOnClickListener(new Button.OnClickListener() {
				@Override
				public void onClick(View v) {
					SharedPreferencesHelper sph = new SharedPreferencesHelper(
							context, "config");
					sph.putString("firstRun", "0");
					sph.putString("age", ind + "");

					Intent intent = new Intent();
					intent.setClass(context, NewMainActivity.class);
					intent.putExtra("sex", sexIntent.getIntExtra("", 0));
					intent.putExtra("age", ind);
					startActivity(intent);
					finish();
					overridePendingTransition(R.anim.in_from_right,
							R.anim.out_to_left);
				}
			});
		}

	}
}
