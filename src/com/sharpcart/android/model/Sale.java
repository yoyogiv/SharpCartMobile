package com.sharpcart.android.model;

public class Sale {

	private int Id;
	private double Sale_Price;
	private String Start_Date;
	private String End_Date;
	private int Store_Id;
	private int Shopping_Item_Id;
	private int Category_Id;
	private double Quantity;
	private double Price_Per_Unit;
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
	 * @return the sale_Price
	 */
	public double getSale_Price() {
		return Sale_Price;
	}
	/**
	 * @param sale_Price the sale_Price to set
	 */
	public void setSale_Price(double sale_Price) {
		Sale_Price = sale_Price;
	}
	/**
	 * @return the start_Date
	 */
	public String getStart_Date() {
		return Start_Date;
	}
	/**
	 * @param start_Date the start_Date to set
	 */
	public void setStart_Date(String start_Date) {
		Start_Date = start_Date;
	}
	/**
	 * @return the end_Date
	 */
	public String getEnd_Date() {
		return End_Date;
	}
	/**
	 * @param end_Date the end_Date to set
	 */
	public void setEnd_Date(String end_Date) {
		End_Date = end_Date;
	}
	/**
	 * @return the store_Id
	 */
	public int getStore_Id() {
		return Store_Id;
	}
	/**
	 * @param store_Id the store_Id to set
	 */
	public void setStore_Id(int store_Id) {
		Store_Id = store_Id;
	}
	/**
	 * @return the shopping_Item_Id
	 */
	public int getShopping_Item_Id() {
		return Shopping_Item_Id;
	}
	/**
	 * @param shopping_Item_Id the shopping_Item_Id to set
	 */
	public void setShopping_Item_Id(int shopping_Item_Id) {
		Shopping_Item_Id = shopping_Item_Id;
	}
	/**
	 * @return the category_Id
	 */
	public int getCategory_Id() {
		return Category_Id;
	}
	/**
	 * @param category_Id the category_Id to set
	 */
	public void setCategory_Id(int category_Id) {
		Category_Id = category_Id;
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
	
	
}
