package com.sharpcart.android.model;

/**
 * A shopping item
 */
public class ShoppingItem implements Comparable<ShoppingItem>{
	private Long id;

	private String name;
	
	private String description;
	
	private Category category;
	
	private Unit unit;
	
	private String imageLocation;
	
	private float unitToItemConversionRatio;
	
	
	public ShoppingItem(final String name, final String description,
			final Category category, final Unit unit, final String imageLocation,
			final float unitToItemConversionRatio) {
		this.name = name;
		this.description = description;
		this.category = category;
		this.unit = unit;
		this.imageLocation = imageLocation;
		this.unitToItemConversionRatio = unitToItemConversionRatio;
	}

	//empty constructor
	public ShoppingItem(){
		
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
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(final String description) {
		this.description = description;
	}

	/**
	 * @return the category
	 */
	public Category getCategory() {
		return category;
	}

	/**
	 * @param category the category to set
	 */
	public void setCategory(final Category category) {
		this.category = category;
	}

	/**
	 * @return the unit
	 */
	public Unit getUnit() {
		return unit;
	}

	/**
	 * @param unit the unit to set
	 */
	public void setUnit(final Unit unit) {
		this.unit = unit;
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
	 * @return the unitToItemConversionRatio
	 */
	public float getUnitToItemConversionRatio() {
		return unitToItemConversionRatio;
	}

	/**
	 * @param unitToItemConversionRatio the unitToItemConversionRatio to set
	 */
	public void setUnitToItemConversionRatio(final float unitToItemConversionRatio) {
		this.unitToItemConversionRatio = unitToItemConversionRatio;
	}

	@Override
	public int compareTo(final ShoppingItem o) {
		return id.compareTo(o.getId());
	}
	
	
}
