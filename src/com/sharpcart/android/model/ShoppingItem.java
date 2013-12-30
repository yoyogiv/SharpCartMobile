package com.sharpcart.android.model;

public class ShoppingItem implements Comparable<ShoppingItem> {
    private int id;
    private String name;
    private double price_per_unit;
    private double quantity;
    private String unit;
    private String category;
    private String description;
    private int categoryId;
    private int unitId;
    private double conversionRatio;
    private String imageLocation;
    
    /*
     * I should create a specific constructor for this object and make the default constructor private so you must 
     * use it since a shopping item MUST have at least :id,name,description,category and unit for everything to work correctly
     */
    
    public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}

	public int getUnitId() {
		return unitId;
	}

	public void setUnitId(int unitId) {
		this.unitId = unitId;
	}

	public double getConversionRatio() {
		return conversionRatio;
	}

	public void setConversionRatio(double conversionRatio) {
		this.conversionRatio = conversionRatio;
	}

	public String getImageLocation() {
		return imageLocation;
	}

	public void setImageLocation(String imageLocation) {
		this.imageLocation = imageLocation;
	}

	public int getId() {
	return id;
    }

    public void setId(int id) {
	this.id = id;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public double getPrice_per_unit() {
	return price_per_unit;
    }

    public void setPrice_per_unit(double price_per_unit) {
	this.price_per_unit = price_per_unit;
    }

    public double getQuantity() {
	return quantity;
    }

    public void setQuantity(double quantity) {
	this.quantity = quantity;
    }

    public String getUnit() {
	return unit;
    }

    public void setUnit(String unit) {
	this.unit = unit;
    }

    public String getCategory() {
	return category;
    }

    public void setCategory(String category) {
	this.category = category;
    }

    @Override
    public int compareTo(ShoppingItem arg0) {

	return this.name.compareTo(arg0.getName());
    }

}
