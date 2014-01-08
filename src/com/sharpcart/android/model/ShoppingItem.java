package com.sharpcart.android.model;

public class ShoppingItem implements Comparable<ShoppingItem> {
    private int id;
    private String name;
    private double price_per_unit;
    private double quantity;
    private String unit;
    private String category;
    private String description;
    private int Shopping_Item_Category_Id;
    private int Shopping_Item_Unit_Id;
    private double conversion_ratio;
    private double price;
    private double total_price;
    private double package_quantity;
    private String default_unit_in_db;
    private String is_using_default_unit;
    private String in_db;
    private String Image_Location;
    
    
    /*
     * I should create a specific constructor for this object and make the default constructor private so you must 
     * use it since a shopping item MUST have at least :id,name,description,category and unit for everything to work correctly
     */

	@Override
    public int compareTo(ShoppingItem arg0) {

		return this.name.compareTo(arg0.getName());
    }


	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}


	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
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
	public void setName(String name) {
		this.name = name;
	}


	/**
	 * @return the price_per_unit
	 */
	public double getPrice_per_unit() {
		return price_per_unit;
	}


	/**
	 * @param price_per_unit the price_per_unit to set
	 */
	public void setPrice_per_unit(double price_per_unit) {
		this.price_per_unit = price_per_unit;
	}


	/**
	 * @return the quantity
	 */
	public double getQuantity() {
		return quantity;
	}


	/**
	 * @param quantity the quantity to set
	 */
	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}


	/**
	 * @return the unit
	 */
	public String getUnit() {
		return unit;
	}


	/**
	 * @param unit the unit to set
	 */
	public void setUnit(String unit) {
		this.unit = unit;
	}


	/**
	 * @return the category
	 */
	public String getCategory() {
		return category;
	}


	/**
	 * @param category the category to set
	 */
	public void setCategory(String category) {
		this.category = category;
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
	public void setDescription(String description) {
		this.description = description;
	}


	/**
	 * @return the shopping_Item_Category_Id
	 */
	public int getShopping_Item_Category_Id() {
		return Shopping_Item_Category_Id;
	}


	/**
	 * @param shopping_Item_Category_Id the shopping_Item_Category_Id to set
	 */
	public void setShopping_Item_Category_Id(int shopping_Item_Category_Id) {
		Shopping_Item_Category_Id = shopping_Item_Category_Id;
	}


	/**
	 * @return the shopping_Item_Unit_Id
	 */
	public int getShopping_Item_Unit_Id() {
		return Shopping_Item_Unit_Id;
	}


	/**
	 * @param shopping_Item_Unit_Id the shopping_Item_Unit_Id to set
	 */
	public void setShopping_Item_Unit_Id(int shopping_Item_Unit_Id) {
		Shopping_Item_Unit_Id = shopping_Item_Unit_Id;
	}


	/**
	 * @return the conversion_ratio
	 */
	public double getConversion_ratio() {
		return conversion_ratio;
	}


	/**
	 * @param conversion_ratio the conversion_ratio to set
	 */
	public void setConversion_ratio(double conversion_ratio) {
		this.conversion_ratio = conversion_ratio;
	}


	/**
	 * @return the price
	 */
	public double getPrice() {
		return price;
	}


	/**
	 * @param price the price to set
	 */
	public void setPrice(double price) {
		this.price = price;
	}


	/**
	 * @return the total_price
	 */
	public double getTotal_price() {
		return total_price;
	}


	/**
	 * @param total_price the total_price to set
	 */
	public void setTotal_price(double total_price) {
		this.total_price = total_price;
	}


	/**
	 * @return the package_quantity
	 */
	public double getPackage_quantity() {
		return package_quantity;
	}


	/**
	 * @param package_quantity the package_quantity to set
	 */
	public void setPackage_quantity(double package_quantity) {
		this.package_quantity = package_quantity;
	}


	/**
	 * @return the default_unit_in_db
	 */
	public String getDefault_unit_in_db() {
		return default_unit_in_db;
	}


	/**
	 * @param default_unit_in_db the default_unit_in_db to set
	 */
	public void setDefault_unit_in_db(String default_unit_in_db) {
		this.default_unit_in_db = default_unit_in_db;
	}


	/**
	 * @return the is_using_default_unit
	 */
	public String getIs_using_default_unit() {
		return is_using_default_unit;
	}


	/**
	 * @param is_using_default_unit the is_using_default_unit to set
	 */
	public void setIs_using_default_unit(String is_using_default_unit) {
		this.is_using_default_unit = is_using_default_unit;
	}


	/**
	 * @return the in_db
	 */
	public String getIn_db() {
		return in_db;
	}


	/**
	 * @param in_db the in_db to set
	 */
	public void setIn_db(String in_db) {
		this.in_db = in_db;
	}


	/**
	 * @return the image_Location
	 */
	public String getImage_Location() {
		return Image_Location;
	}


	/**
	 * @param image_Location the image_Location to set
	 */
	public void setImage_Location(String image_Location) {
		Image_Location = image_Location;
	}

}
