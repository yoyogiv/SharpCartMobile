package com.sharpcart.android.model;

public class ImageResource {

	private Integer drawableResourceId;
	private Integer databaseId;

	private String name;
	
	public ImageResource(Integer drawableResourceId, Integer databaseId) {
		super();
		this.drawableResourceId = drawableResourceId;
		this.databaseId = databaseId;
	}

	public ImageResource(Integer drawableResourceId, String name) {
		super();
		this.drawableResourceId = drawableResourceId;
		this.name = name;
	}
	
	public Integer getDrawableResourceId() {
		return drawableResourceId;
	}
	
	public void setDrawableResourceId(Integer drawableResourceId) {
		this.drawableResourceId = drawableResourceId;
	}

	public Integer getDatabaseId() {
		return databaseId;
	}
	
	public void setDatabaseId(Integer databaseId) {
		this.databaseId = databaseId;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
