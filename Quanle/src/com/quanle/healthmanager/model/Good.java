package com.quanle.healthmanager.model;

public class Good {
	private String id;
	private String name;
	private String title;
	private String price;

	public Good(String id, String name, String title, String price) {
		super();
		this.id = id;
		this.name = name;
		this.title = title;
		this.price = price;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

}