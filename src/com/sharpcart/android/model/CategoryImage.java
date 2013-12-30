package com.sharpcart.android.model;

public class CategoryImage {

	private Integer drawableResourceId;
	private Integer databaseId;
	
	public CategoryImage(Integer drawableResourceId, Integer databaseId) {
		super();
		this.drawableResourceId = drawableResourceId;
		this.databaseId = databaseId;
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
	
}
