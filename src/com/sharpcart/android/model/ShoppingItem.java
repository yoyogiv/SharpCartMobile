package com.sharpcart.android.model;

public class ShoppingItem implements Comparable<ShoppingItem> {
    private int Id;
    private String Name;
    private double Price_Per_Unit;
    private double Quantity;
    private String Unit;
    private String Category;
    private String Description;
    private int Shopping_Item_Category_Id;
    private int Shopping_Item_Unit_Id;
    private double Unit_To_Item_Conversion_Ratio;
    private String Image_Location;
    
    
    /*
     * I should create a specific constructor for this object and make the default constructor private so you must 
     * use it since a shopping item MUST have at least :id,name,description,category and unit for everything to work correctly
     */
  
    /**
	 * @return the id
	 */
	public int getId() {
		return Id;
	}


	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		Id = id;
	}


	/**
	 * @return the name
	 */
	public String getName() {
		return Name;
	}


	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		Name = name;
	}


	/**
	 * @return the price_Per_Unit
	 */
	public double getPrice_Per_Unit() {
		return Price_Per_Unit;
	}


	/**
	 * @param price_Per_Unit the price_Per_Unit to set
	 */
	public void setPrice_Per_Unit(double price_Per_Unit) {
		Price_Per_Unit = price_Per_Unit;
	}


	/**
	 * @return the quantity
	 */
	public double getQuantity() {
		return Quantity;
	}


	/**
	 * @param quantity the quantity to set
	 */
	public void setQuantity(double quantity) {
		Quantity = quantity;
	}


	/**
	 * @return the unit
	 */
	public String getUnit() {
		return Unit;
	}


	/**
	 * @param unit the unit to set
	 */
	public void setUnit(String unit) {
		Unit = unit;
	}


	/**
	 * @return the category
	 */
	public String getCategory() {
		return Category;
	}


	/**
	 * @param category the category to set
	 */
	public void setCategory(String category) {
		Category = category;
	}


	/**
	 * @return the description
	 */
	public String getDescription() {
		return Description;
	}


	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		Description = description;
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
	 * @return the unit_To_Item_Conversion_Ratio
	 */
	public double getUnit_To_Item_Conversion_Ratio() {
		return Unit_To_Item_Conversion_Ratio;
	}


	/**
	 * @param unit_To_Item_Conversion_Ratio the unit_To_Item_Conversion_Ratio to set
	 */
	public void setUnit_To_Item_Conversion_Ratio(
			double unit_To_Item_Conversion_Ratio) {
		Unit_To_Item_Conversion_Ratio = unit_To_Item_Conversion_Ratio;
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


	@Override
    public int compareTo(ShoppingItem arg0) {

	return this.Name.compareTo(arg0.getName());
    }

}
