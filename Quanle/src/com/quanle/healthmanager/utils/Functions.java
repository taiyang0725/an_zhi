package com.quanle.healthmanager.utils;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.provider.Settings.Secure;
import android.text.TextUtils;
import android.util.Log;

public class Functions {
	private static Boolean DEBUG = true;

	public static String getNeverTime(String time) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
		Date now = new Date();
		try {
			java.util.Date date = df.parse(time);
			long l = now.getTime() - date.getTime();
			long day = l / (24 * 60 * 60 * 1000);
			long hour = (l / (60 * 60 * 1000) - day * 24);
			long min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);
			long s = (l / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);

			StringBuffer sb = new StringBuffer();
			if (s < 60 && min < 1)
				sb.append(s + "秒");
			else if (min < 60 && hour < 1)
				sb.append(min + "分");
			else if (hour < 60 && day < 1)
				sb.append(hour + "小时");
			else
				sb.append(day + "天");

			sb.append("前");
			return sb.toString();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public static File saveLoaclBitmap(Bitmap bitmap, String path) {
		File myCaptureFile = new File(path);

		try {
			BufferedOutputStream bos = new BufferedOutputStream(
					new FileOutputStream(myCaptureFile));
			if (path.substring(path.length() - 3, path.length()).toLowerCase()
					.equals("jpg")) {
				bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);
			} else if (path.substring(path.length() - 3, path.length())
					.toLowerCase().equals("png")) {
				bitmap.compress(Bitmap.CompressFormat.PNG, 80, bos);
			}
			bos.flush();
			bos.close();
			return myCaptureFile;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * 直接通过HTTP协议提交数据到服务器,实现如下面表单提交功能: <FORM METHOD=POST
	 * ACTION="http://192.168.0.200:8080/ssi/fileload/test.do"
	 * enctype="multipart/form-data"> <INPUT TYPE="text" NAME="name"> <INPUT
	 * TYPE="text" NAME="id"> <input type="file" name="imagefile"/> <input
	 * type="file" name="zip"/> </FORM>
	 * 
	 * @param path
	 *            上传路径(注：避免使用localhost或127.0.0.1这样的路径测试，因为它会指向手机模拟器，你可以使用http://
	 *            www.xxx.cn或http://192.168.1.10:8080这样的路径测试)
	 * @param params
	 *            请求参数 key为参数名,value为参数值
	 * @param file
	 *            上传文件
	 */
	public static String uploadPost(String path, Map<String, String> params,
			FormFile[] files) throws Exception {
		final String BOUNDARY = "---------------------------7da2137580612"; // 数据分隔线
		final String endline = "--" + BOUNDARY + "--\r\n";// 数据结束标志

		int fileDataLength = 0;
		if (files != null || files.length == 0) {
			for (FormFile uploadFile : files) {// 得到文件类型数据的总长度
				if (uploadFile != null) {
					StringBuilder fileExplain = new StringBuilder();
					fileExplain.append("--");
					fileExplain.append(BOUNDARY);
					fileExplain.append("\r\n");
					fileExplain.append("Content-Disposition: form-data;name=\""
							+ uploadFile.getParameterName() + "\";filename=\""
							+ uploadFile.getFilname() + "\"\r\n");
					fileExplain.append("Content-Type: "
							+ uploadFile.getContentType() + "\r\n\r\n");
					fileExplain.append("\r\n");
					fileDataLength += fileExplain.length();
					if (uploadFile.getInStream() != null) {
						fileDataLength += uploadFile.getFile().length();
					} else {
						fileDataLength += uploadFile.getData().length;
					}
				}
			}
		}

		StringBuilder textEntity = new StringBuilder();
		for (Map.Entry<String, String> entry : params.entrySet()) {// 构造文本类型参数的实体数据
			textEntity.append("--");
			textEntity.append(BOUNDARY);
			textEntity.append("\r\n");
			textEntity.append("Content-Disposition: form-data; name=\""
					+ entry.getKey() + "\"\r\n\r\n");
			textEntity.append(entry.getValue());
			textEntity.append("\r\n");
		}
		// 计算传输给服务器的实体数据总长度
		int dataLength = textEntity.toString().getBytes().length
				+ fileDataLength + endline.getBytes().length;

		System.out.println(textEntity.toString());
		URL url = new URL(path);
		int port = url.getPort() == -1 ? 80 : url.getPort();
		Socket socket = new Socket(InetAddress.getByName(url.getHost()), port);
		String pathString = url.getPath();
		if (!url.getQuery().equals(""))
			pathString += "?" + url.getQuery();
		System.out.println(url.getHost());
		System.out.println(url.getQuery());
		System.out.println(url.getPath());
		System.out.println(port);

		OutputStream outStream = socket.getOutputStream();
		// 下面完成HTTP请求头的发送
		String requestmethod = "POST " + pathString + " HTTP/1.1\r\n";
		System.out.println(requestmethod);
		outStream.write(requestmethod.getBytes());
		String accept = "Accept: image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*\r\n";
		System.out.println(accept);
		outStream.write(accept.getBytes());
		String language = "Accept-Language: zh-CN\r\n";
		System.out.println(language);
		outStream.write(language.getBytes());
		String contenttype = "Content-Type: multipart/form-data; boundary="
				+ BOUNDARY + "\r\n";
		System.out.println(contenttype);
		outStream.write(contenttype.getBytes());
		String contentlength = "Content-Length: " + dataLength + "\r\n";
		System.out.println(contentlength);
		outStream.write(contentlength.getBytes());
		String alive = "Connection: Keep-Alive\r\n";
		System.out.println(alive);
		outStream.write(alive.getBytes());
		String host = "Host: " + url.getHost() + ":" + port + "\r\n";
		System.out.println(host);
		outStream.write(host.getBytes());
		// 写完HTTP请求头后根据HTTP协议再写一个回车换行
		outStream.write("\r\n".getBytes());
		// 把所有文本类型的实体数据发送出来
		outStream.write(textEntity.toString().getBytes());
		// 把所有文件类型的实体数据发送出来

		if (files != null || files.length == 0) {
			for (FormFile uploadFile : files) {
				if (uploadFile != null) {
					StringBuilder fileEntity = new StringBuilder();
					fileEntity.append("--");
					fileEntity.append(BOUNDARY);
					fileEntity.append("\r\n");
					fileEntity.append("Content-Disposition: form-data;name=\""
							+ uploadFile.getParameterName() + "\";filename=\""
							+ uploadFile.getFilname() + "\"\r\n");
					fileEntity.append("Content-Type: "
							+ uploadFile.getContentType() + "\r\n\r\n");
					outStream.write(fileEntity.toString().getBytes());
					if (uploadFile.getInStream() != null) {
						byte[] buffer = new byte[1024];
						int len = 0;
						while ((len = uploadFile.getInStream().read(buffer, 0,
								1024)) != -1) {
							outStream.write(buffer, 0, len);
						}
						uploadFile.getInStream().close();
					} else {
						outStream.write(uploadFile.getData(), 0,
								uploadFile.getData().length);
					}
					outStream.write("\r\n".getBytes());
				}
			}
		}

		// 下面发送数据结束标志，表示数据已经结束
		outStream.write(endline.getBytes());
		outStream.flush();
		socket.shutdownOutput();
		InputStreamReader im = new InputStreamReader(socket.getInputStream(),
				"UTF-8");
		BufferedReader reader = new BufferedReader(im);
		ArrayList<String> result = new ArrayList<String>();
		String line;
		while ((line = reader.readLine()) != null)
			result.add(line);

		for (int i = 0; i < result.size(); i++) {
			System.out.println(result.get(i));
		}

		if (result.get(0).indexOf("200") == -1)
			return null;

		outStream.close();
		reader.close();
		socket.close();
		return result.get(result.size() - 1);
	}

	/**
	 * 提交数据到服务器
	 * 
	 * @param path
	 *            上传路径(注：避免使用localhost或127.0.0.1这样的路径测试，因为它会指向手机模拟器，你可以使用http://
	 *            www.xxx.cn或http://192.168.1.10:8080这样的路径测试)
	 * @param params
	 *            请求参数 key为参数名,value为参数值
	 * @param file
	 *            上传文件
	 */
	public static String uploadPost(String path, Map<String, String> params,
			FormFile file) throws Exception {
		return uploadPost(path, params, new FormFile[] { file });
	}

	public static boolean hasSDCard() {
		String state = Environment.getExternalStorageState();
		if (state.equals(Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isDateStringValid(String date) {
		if (date == null)
			return false;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			sdf.parse(date);
			return true;
		} catch (java.text.ParseException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static boolean isIDCardValid(String idCardString) {
		if (idCardString == null)
			return false;

		idCardString = idCardString.trim().toLowerCase();

		if (idCardString.length() != 18)
			return false;

		int[] intArr = { 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2 };
		int sum = 0;
		for (int i = 0; i < intArr.length; i++)
			sum += Character.digit(idCardString.charAt(i), 10) * intArr[i];
		int mod = sum % 11;

		int[] intArr2 = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
		int[] intArr3 = { 1, 0, 'x', 9, 8, 7, 6, 5, 4, 3, 2 };
		String matchDigit = "";
		for (int i = 0; i < intArr2.length; i++) {
			int j = intArr2[i];
			if (j == mod) {
				matchDigit = String.valueOf(intArr3[i]);
				if (intArr3[i] > 57) {
					matchDigit = String.valueOf((char) intArr3[i]);
				}
			}
		}
		if (matchDigit
				.equals(idCardString.substring(idCardString.length() - 1))) {
			return true;
		} else {
			return false;
		}
	}

	public static String getAppVersion(Context context) {
		PackageInfo info = null;
		try {
			info = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return info.versionName;
	}

	/**
	 * 获取文件夹大小
	 * 
	 * @param file
	 *            File实例
	 * @return long
	 */
	public static long getFolderSize(java.io.File file) {
		long size = 0;
		try {
			java.io.File[] fileList = file.listFiles();
			for (int i = 0; i < fileList.length; i++) {
				if (fileList[i].isDirectory())
					size = size + getFolderSize(fileList[i]);
				else
					size = size + fileList[i].length();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return size;
	}

	/**
	 * 删除指定目录下文件及目录
	 * 
	 * @param deleteThisPath
	 * @param filepath
	 * @return
	 */
	public void deleteFolderFile(String filePath, boolean deleteThisPath) {
		if (!TextUtils.isEmpty(filePath)) {
			try {
				File file = new File(filePath);
				if (file.isDirectory()) {// 处理目录
					File files[] = file.listFiles();
					for (int i = 0; i < files.length; i++) {
						deleteFolderFile(files[i].getAbsolutePath(), true);
					}
				}
				if (deleteThisPath) {
					if (!file.isDirectory()) {// 如果是文件，删除
						file.delete();
					} else {// 目录
						if (file.listFiles().length == 0) {// 目录下没有文件或者目录，删除
							file.delete();
						}
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * 格式化单位
	 * 
	 * @param size
	 * @return
	 */
	public static String getFormatSize(double size) {
		double kiloByte = size / 1024;
		if (kiloByte < 1) {
			return size + "Byte(s)";
		}

		double megaByte = kiloByte / 1024;
		if (megaByte < 1) {
			BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
			return result1.setScale(2, BigDecimal.ROUND_HALF_UP)
					.toPlainString() + "KB";
		}

		double gigaByte = megaByte / 1024;
		if (gigaByte < 1) {
			BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
			return result2.setScale(2, BigDecimal.ROUND_HALF_UP)
					.toPlainString() + "MB";
		}

		double teraBytes = gigaByte / 1024;
		if (teraBytes < 1) {
			BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
			return result3.setScale(2, BigDecimal.ROUND_HALF_UP)
					.toPlainString() + "GB";
		}
		BigDecimal result4 = new BigDecimal(teraBytes);
		return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()
				+ "TB";
	}

	public static boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		return pattern.matcher(str).matches();
	}

	public boolean checkApkExist(Context context, String packageName) {
		if (packageName == null || "".equals(packageName)) {
			return false;
		}
		try {
			context.getPackageManager().getApplicationInfo(packageName,
					PackageManager.GET_UNINSTALLED_PACKAGES);
			return true;
		} catch (NameNotFoundException e) {
			return false;
		}
	}

	public static String getDeviceId(Context context) {
		String android_id = Secure.getString(context.getContentResolver(),
				Secure.ANDROID_ID);
		return android_id;
	}

	public static String getCPUSerial() {
		String str = "", strCPU = "", cpuAddress = "0000000000000000";
		try {
			// 读取CPU信息
			Process pp = Runtime.getRuntime().exec("cat /proc/cpuinfo");
			InputStreamReader ir = new InputStreamReader(pp.getInputStream());
			LineNumberReader input = new LineNumberReader(ir);
			// 查找CPU序列号
			for (int i = 1; i < 16; i++) {
				str = input.readLine();
				if (str != null) {
					// 查找到序列号所在行
					if (str.indexOf("Serial") > -1) {
						// 提取序列号
						strCPU = str.substring(str.indexOf(":") + 1,
								str.length());
						// 去空格
						cpuAddress = strCPU.trim();
						break;
					}
				} else {
					// 文件结尾
					break;
				}
			}
		} catch (IOException ex) {
			// 赋予默认值
			ex.printStackTrace();
		}
		return cpuAddress;
	}

	public static String getHttpResponse(String urlApi) {
		return getHttpResponse(urlApi, null);
	}

	public static String getHttpResponse(String urlApi,
			List<NameValuePair> params) {
		HttpPost httpRequest = new HttpPost(urlApi);
		httpRequest.getParams().setParameter(
				CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
		try {
			if (params != null)
				httpRequest.setEntity(new UrlEncodedFormEntity(params,
						HTTP.UTF_8));
			HttpResponse httpResponse = new DefaultHttpClient()
					.execute(httpRequest);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				String strResult = EntityUtils.toString(
						httpResponse.getEntity(), HTTP.UTF_8);
				return strResult;
			} else {
				System.err.println(EntityUtils.toString(
						httpResponse.getEntity(), HTTP.UTF_8));
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static int getAppVersionName(Context context) {
		int versionName = -1;
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
			versionName = pi.versionCode;
		} catch (Exception e) {
			Log.e("GET VER", "Exception", e);
		}
		return versionName;
	}

	public static ArrayList<String> splitString(String string, String split) {
		ArrayList<String> list = new ArrayList<String>();

		while (string.indexOf(split) >= 0) {
			list.add(string.substring(0, string.indexOf(split)));
			string = string.substring(string.indexOf(split) + 1);
		}
		list.add(string);

		return list;
	}

	public static boolean CreateFolder(String strFolder) {
		boolean bReturn = false;
		Log.d("FILE", "Create " + strFolder);
		File f = new File(strFolder);
		if (!f.exists()) {
			bReturn = f.mkdirs();
			Log.d("FILE", "" + strFolder + " Created.");
		} else {
			bReturn = true;
		}
		return bReturn;
	}

	public static boolean CreateFolderTree(String strFolder) {
		boolean bReturn = false;
		Log.d("FILE", "Create " + strFolder);
		String strDirString = "";
		ArrayList<String> dirList = splitString(strFolder, "/");
		Log.d("FILE", "DIRList " + dirList.size());

		for (int i = 0; i < dirList.size(); i++) {
			strDirString += dirList.get(i) + "/";
			CreateFolder(strDirString);

			Log.d("FILE", "DIRList====" + strDirString);
		}
		return bReturn;
	}

	// android获取sd卡路径方法：
	public String getSDPath() {
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
		}
		return sdDir.toString();
	}

	public static Bitmap getLoacalBitmap(String path) {
		try {
			FileInputStream fis = new FileInputStream(path);
			return BitmapFactory.decodeStream(fis);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static boolean checkInternet(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();

		if (info != null && info.isConnected()) {
			return true;
		} else {
			// 不能连接到
			// Toast.makeText(CAWordsListActivity.this,
			// "you get not connect to the Internet,please log!",
			// Toast.LENGTH_SHORT).show();
			// try {
			// this.wait(3000);
			// } catch (InterruptedException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			// this.finish();
			return false;
		}
	}

	/**
	 * 复制文件(以超快的速度复制文件)
	 * 
	 * @author:suhj 2006-8-31
	 * @param srcFile
	 *            源文件File
	 * @param destDir
	 *            目标目录File
	 * @param newFileName
	 *            新文件名
	 * @return 实际复制的字节数，如果文件、目录不存在、文件为null或者发生IO异常，返回-1
	 */
	public static long CopyFile(File srcFile, String newFileName) {
		File newFile = new File(newFileName);
		File destDir = new File(newFile.getParent());
		long copySizes = 0;
		if (!srcFile.exists()) {
			System.out.println("源文件不存在");
			copySizes = -1;
		} else if (!destDir.exists()) {
			System.out.println("目标目录不存在");
			copySizes = -1;
		} else if (newFileName == null) {
			System.out.println("文件名为null");
			copySizes = -1;
		} else {
			try {
				FileChannel fcin = new FileInputStream(srcFile).getChannel();
				FileChannel fcout = new FileOutputStream(new File(destDir,
						newFileName)).getChannel();
				ByteBuffer buff = ByteBuffer.allocate(1024);
				int b = 0, i = 0;
				// long t1 = System.currentTimeMillis();
				/*
				 * while(fcin.read(buff) != -1){ buff.flip(); fcout.write(buff);
				 * buff.clear(); i++; }
				 */
				long size = fcin.size();
				fcin.transferTo(0, fcin.size(), fcout);
				// fcout.transferFrom(fcin,0,fcin.size());
				// 一定要分清哪个文件有数据，那个文件没有数据，数据只能从有数据的流向
				// 没有数据的文件
				// long t2 = System.currentTimeMillis();
				fcin.close();
				fcout.close();
				copySizes = size;
				// long t = t2-t1;
				// System.out.println("复制了" + i + "个字节\n" + "时间" + t);
				// System.out.println("复制了" + size + "个字节\n" + "时间" + t);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return copySizes;
	}

	/**
	 * 将字符串转成MD5值
	 * 
	 * @param string
	 * @return
	 */
	public static String stringToMD5(String string) {
		byte[] hash;

		try {
			hash = MessageDigest.getInstance("MD5").digest(
					string.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}

		StringBuilder hex = new StringBuilder(hash.length * 2);
		for (byte b : hash) {
			if ((b & 0xFF) < 0x10)
				hex.append("0");
			hex.append(Integer.toHexString(b & 0xFF));
		}

		return hex.toString();
	}

	public static Bitmap getHttpBitmap(String url) {
		URL myFileURL;
		Bitmap bitmap = null;
		try {
			myFileURL = new URL(url);
			// 获得连接
			HttpURLConnection conn = (HttpURLConnection) myFileURL
					.openConnection();
			// 设置超时时间为6000毫秒，conn.setConnectionTiem(0);表示没有时间限制
			conn.setConnectTimeout(6000);
			// 连接设置获得数据流
			conn.setDoInput(true);
			// 不使用缓存
			conn.setUseCaches(false);
			// 这句可有可无，没有影响
			// conn.connect();
			// 得到数据流
			InputStream is = conn.getInputStream();
			// 解析得到图片
			bitmap = BitmapFactory.decodeStream(is);
			// 关闭数据流
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return bitmap;

	}

	public static Bitmap bitmapZoom(Bitmap bitmap, float width, float height) {
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		Matrix matrix = new Matrix();
		float scaleWidth = ((float) width / w);
		float scaleHeight = ((float) height / h);
		matrix.postScale(scaleWidth, scaleHeight);// 利用矩阵进行缩放不会造成内存溢出
		Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
		return newbmp;
	}
}
