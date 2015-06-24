package com.quanle.healthmanager;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.quanle.healthmanager.utils.Config;
import com.quanle.healthmanager.utils.DownImg;
import com.quanle.healthmanager.utils.Functions;
import com.quanle.healthmanager.utils.HttpUtil;
import com.quanle.healthmanager.utils.NetListener;
import com.quanle.healthmanager.utils.User;
import com.quanle.healthmanager.utils.NetListener.CallBack;
import com.quanle.healthmanager.widget.NavigationBar;

public class DoctorActivity extends Activity implements OnClickListener {

	private User user;
	private NavigationBar navigationBar;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_doctor);
		user = ((User) getApplicationContext());
		navigationBar = (NavigationBar) findViewById(R.id.nbRegister);
		navigationBar.setText("个人信息");
		iniView();

		String doctorid = this.getIntent().getStringExtra("doctorid");
		loadDoctor(doctorid);

	}

	public void loadDoctor(String doctorid) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("u", user.getUID() + ""));
		params.add(new BasicNameValuePair("rc", user.getRndCodeString()));
		params.add(new BasicNameValuePair("id", doctorid));

		NetListener net = new NetListener(this);
		net.ask(params, "getDoctor", new CallBack() {

			@Override
			public void onOver(JSONObject obj) {
				try {
					// TODO Auto-generated method stub
					if (obj != null) {

						iniInfo(obj);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		});

	}

	public void onClick(View v) {
		switch (v.getId()) {
		// case R.id.btn_yuyue:
		// Toast.makeText(this, "正在开发中", Toast.LENGTH_SHORT).show();

		default:
			break;
		}
	}

	public void iniView() {

	}

	public void iniInfo(JSONObject obj) {
		if (obj == null)
			return;
		TextView text_name = (TextView) findViewById(R.id.text_name);
		TextView text_gender = (TextView) findViewById(R.id.text_gender);
		TextView text_office = (TextView) findViewById(R.id.text_office);
		TextView text_unit = (TextView) findViewById(R.id.text_unit);
		TextView text_desctiption = (TextView) findViewById(R.id.text_desctiption);
		TextView text_advantage = (TextView) findViewById(R.id.text_advantage);
		ImageView imageview_headimg = (ImageView) findViewById(R.id.imageview_headimg);

		try {
			text_name.setText(obj.getString("name"));
			text_gender.setText(obj.getString("gender"));
			text_office.setText(obj.getString("office"));
			text_unit.setText(obj.getString("unit"));
			text_desctiption.setText(obj.getString("description"));
			text_advantage.setText(obj.getString("advantage"));
			DownImg d = new DownImg(imageview_headimg);
			d.execute(obj.getString("face"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
