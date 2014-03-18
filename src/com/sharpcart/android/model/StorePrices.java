package com.sharpcart.android.model;

import java.util.List;

public class StorePrices {

    private String name;
    private List<ShoppingListItem> items;
    private double total_cost;
    private String store_image_location;
    private int id;
    private int status;

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
	 * @return the items
	 */
	public List<ShoppingListItem> getItems() {
		return items;
	}

	/**
	 * @param items the items to set
	 */
	public void setItems(final List<ShoppingListItem> items) {
		this.items = items;
	}

	/**
	 * @return the total_cost
	 */
	public double getTotal_cost() {
		return total_cost;
	}

	/**
	 * @param total_cost the total_cost to set
	 */
	public void setTotal_cost(final double total_cost) {
		this.total_cost = total_cost;
	}

	/**
	 * @return the store_image_location
	 */
	public String getStore_image_location() {
		return store_image_location;
	}

	/**
	 * @param store_image_location the store_image_location to set
	 */
	public void setStore_image_location(final String store_image_location) {
		this.store_image_location = store_image_location;
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
	public void setId(final int id) {
		this.id = id;
	}

	/**
	 * @return the status
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(final int status) {
		this.status = status;
	}

	@Override
    public String toString() {
    	return "Store [Name=" + name + "]";
    }

}
