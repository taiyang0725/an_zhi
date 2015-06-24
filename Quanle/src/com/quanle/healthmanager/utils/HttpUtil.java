package com.quanle.healthmanager.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

/**
 * 与服务端交互，返回json格式数据
 * 
 * @author Administrator
 * 
 */
public class HttpUtil {
	/**
	 * 
	 * @param action
	 *            请求action
	 * @param params
	 *            参数
	 * @return
	 * @throws Exception
	 */

	public static String getJsonStr(String url, String action,
			List<NameValuePair> params) throws ClientProtocolException,
			IOException, JSONException {
		System.out.println(url + action);
		HttpPost request = new HttpPost(url + action);
		request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
		// 发�?请求
		HttpResponse httpResponse = new DefaultHttpClient().execute(request);
		// 得到应答的字符串，这也是�?�� JSON 格式保存的数�?
		String retSrc = EntityUtils.toString(httpResponse.getEntity());
		return retSrc;
	}

	public static JSONObject getJson(String url, String action,
			List<NameValuePair> params) throws ClientProtocolException,
			IOException, JSONException {
		HttpPost request = new HttpPost(url + action);
		request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
		// 发�?请求
		HttpResponse httpResponse = new DefaultHttpClient().execute(request);
		// 得到应答的字符串，这也是�?�� JSON 格式保存的数�?
		String retSrc = EntityUtils.toString(httpResponse.getEntity());
		// 生成 JSON 对象
		System.out.println("收到>>" + retSrc);
		JSONObject array = new JSONObject(retSrc);

		return array;
	}

	public static String getRequest(String url) {
		try {

			// String bookid = arg0[0];
			// 获取文件�?
			URL myURL = new URL(url);
			System.out.println(myURL.toString());
			URLConnection conn = myURL.openConnection();

			conn.connect();
			InputStream is = conn.getInputStream();
			int fileSize = conn.getContentLength();// 根据响应获取文件大小
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			// FileOutputStream fos = new FileOutputStream(path + filename);
			// 把数据存入路�?文件�?
			byte buf[] = new byte[1024];

			do {
				// 循环读取
				int numread = is.read(buf);
				if (numread == -1) {
					break;
				}
				bos.write(buf, 0, numread);

			} while (true);
			try {
				is.close();
			} catch (Exception ex) {
				Log.e("tag", "error: " + ex.getMessage(), ex);
			}
			String bookContext = bos.toString("UTF-8");

			return bookContext;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static byte[] getRequestByte(String url) {
		try {

			// String bookid = arg0[0];
			// 获取文件�?
			URL myURL = new URL(url);
			System.out.println(myURL.toString());
			URLConnection conn = myURL.openConnection();

			conn.connect();
			InputStream is = conn.getInputStream();
			int fileSize = conn.getContentLength();// 根据响应获取文件大小
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			// FileOutputStream fos = new FileOutputStream(path + filename);
			// 把数据存入路�?文件�?
			byte buf[] = new byte[1024];

			do {
				// 循环读取
				int numread = is.read(buf);
				if (numread == -1) {
					break;
				}
				bos.write(buf, 0, numread);

			} while (true);
			try {
				is.close();
			} catch (Exception ex) {
				Log.e("tag", "error: " + ex.getMessage(), ex);
			}
			String bookContext = bos.toString("UTF-8");

			byte[] b = new byte[bos.size()];
			bos.write(b);
			return b;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
