package com.quanle.healthmanager.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesHelper {

	SharedPreferences sp;
	SharedPreferences.Editor editor;

	Context context;

	public SharedPreferencesHelper(Context c, String name) {
		context = c;
		sp = context.getSharedPreferences(name, 0);
		editor = sp.edit();
	}

	public void putString(String key, String value) {
		editor = sp.edit();
		editor.putString(key, value);
		editor.commit();
	}

	public String getString(String key) {
		return getString(key, null);
	}

	public String getString(String key, String defValue) {
		return sp.getString(key, defValue);
	}

	public int getInt(String key, int defValue) {
		return sp.getInt(key, defValue);
	}

	public void putInt(String key, int value) {
		editor = sp.edit();
		editor.putInt(key, value);
		editor.commit();
	}

	public int getInt(String key) {
		return getInt(key, 0);
	}

}
