package com.sharpcart.android.model;

public class ImageResource {

	private Integer drawableResourceId;
	private Integer databaseId;

	private String name;
	
	public ImageResource(final Integer drawableResourceId, final Integer databaseId) {
		super();
		this.drawableResourceId = drawableResourceId;
		this.databaseId = databaseId;
	}

	public ImageResource(final Integer drawableResourceId, final String name) {
		super();
		this.drawableResourceId = drawableResourceId;
		this.name = name;
	}
	
	public Integer getDrawableResourceId() {
		return drawableResourceId;
	}
	
	public void setDrawableResourceId(final Integer drawableResourceId) {
		this.drawableResourceId = drawableResourceId;
	}

	public Integer getDatabaseId() {
		return databaseId;
	}
	
	public void setDatabaseId(final Integer databaseId) {
		this.databaseId = databaseId;
	}
	
	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}
}
