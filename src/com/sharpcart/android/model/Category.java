package com.sharpcart.android.model;

/*
 * Shopping item category
 */
public class Category {
	private Long id;
	
	private String name;
	
	private String imageLocation;
	
	private int priority;
	
	
	/**
	 * @param name
	 * @param imageLocation
	 * @param priority
	 */
	public Category(final String name, final String imageLocation, final int priority) {
		this.name = name;
		this.imageLocation = imageLocation;
		this.priority = priority;
	}
	
	//Empty constructor
	public Category()
	{
		
	}
	
	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(final Long id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * @return the imageLocation
	 */
	public String getImageLocation() {
		return imageLocation;
	}

	/**
	 * @param imageLocation the imageLocation to set
	 */
	public void setImageLocation(final String imageLocation) {
		this.imageLocation = imageLocation;
	}

	/**
	 * @return the priority
	 */
	public int getPriority() {
		return priority;
	}

	/**
	 * @param priority the priority to set
	 */
	public void setPriority(final int priority) {
		this.priority = priority;
	}
	
}
