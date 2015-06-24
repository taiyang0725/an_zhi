package com.quanle.healthmanager.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

/**
 * 图片异步加载类
 * 
 * @author Leslie.Fang
 * @company EnwaySoft
 * 
 */
public class NetListener {
	// 最大线程数
	public CallBack callback;
	Context context;

	public NetListener(Context context) {
		this.context = context;
	}

	public void ask(final List<NameValuePair> nvps, final String action,
			CallBack bookcallback) {
		System.out.println(Config.APIURL+action+"&"+nvps);
		this.callback = bookcallback;
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == 111 && callback != null) {
					JSONObject obj = (JSONObject) msg.obj;
					callback.onOver(obj);
				} else if (msg.what == 222 && callback != null) {
					callback.onOver(null);
				}
			}
		};

		Thread thread = new Thread() {
			@Override
			public void run() {
				try {

					String result = HttpUtil.getJsonStr(Config.APIURL, action,
							nvps);
					System.out.println(">>>>" + result);
					JSONObject root = new JSONObject(result);
					// 图片下载成功重新缓存并执行回调刷新界面
					if (root != null) {

						Message msg = new Message();
						msg.what = 111;
						msg.obj = root;
						handler.sendMessage(msg);
					}
				} catch (Exception e) {
					e.printStackTrace();

					Message msg = new Message();
					msg.what = 222;
					msg.obj = null;
					handler.sendMessage(msg);

				}
			}
		};
		thread.start();
	}

	/**
	 * 图片下载完成回调接口
	 * 
	 */
	public interface CallBack {
		void onOver(JSONObject obj);
	}

}