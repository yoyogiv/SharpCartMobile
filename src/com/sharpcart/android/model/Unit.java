package com.sharpcart.android.model;

/*
 * Shopping item unit
 */
public class Unit {
	private Long id;
	
	private String name;
	
	
	/**
	 * @param name
	 */
	public Unit(final String name) {
		this.name = name;
	}
	
	//Empty constructor
	public Unit()
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
	
}
