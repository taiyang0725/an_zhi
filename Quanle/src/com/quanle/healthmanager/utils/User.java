package com.quanle.healthmanager.utils;

import java.util.Date;

import android.app.Application;
import android.graphics.Bitmap;

public class User extends Application {
	private Bitmap face;
	private String nickName;
	private int uID;
	private int gender = -1;
	private int landStatus = 0; // 0Î´µÇÂ¼ 1ÓÊÏäµÇÂ½ 2QQµÇÂ½ 3Î¢ÐÅµÇÂ½ 4ÐÂÀËµÇÂ½
	private String mailString;
	private String tokenString;
	private String cityString;
	private String provinceString;
	private Date birthDate;
	private String rndCodeString;
	private String idCardString;

	public static final int LANDSTATUS_MAIL = 1;
	public static final int LANDSTATUS_QQ = 2;
	public static final int LANDSTATUS_MICROMSG = 3;
	public static final int LANDSTATUS_SINA = 4;

	public String getIDCard() {
		return idCardString;
	}

	public void setIDCard(String idCardString) {
		this.idCardString = idCardString;
	}

	public int getUID() {
		return uID;
	}

	public void setUID(int uID) {
		this.uID = uID;
	}

	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	public String getProvinceString() {
		return provinceString;
	}

	public void setProvinceString(String provinceString) {
		this.provinceString = provinceString;
	}

	public String getRndCodeString() {
		return rndCodeString;
	}

	public void setRndCodeString(String rndCodeString) {
		this.rndCodeString = rndCodeString;
	}

	public String getCityString() {
		return cityString;
	}

	public void setCityString(String cityString) {
		this.cityString = cityString;
	}

	public Bitmap getFace() {
		return face;
	}

	public void setFace(Bitmap faceBitmap) {
		this.face = faceBitmap;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public int getGender() {
		return gender;
	}

	public void setGender(int gender) {
		this.gender = gender;
	}

	public int getLandStatus() {
		return landStatus;
	}

	public void setLandStatus(int landStatus) {
		this.landStatus = landStatus;
	}

	public String getMailString() {
		return mailString;
	}

	public void setMailString(String mailString) {
		this.mailString = mailString;
	}

	public String getTokenString() {
		return tokenString;
	}

	public void setTokenString(String tokenString) {
		this.tokenString = tokenString;
	}
}
