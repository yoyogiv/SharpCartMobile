package com.sharpcart.android.model;

import java.util.ArrayList;
import java.util.Collections;

public class Category implements Comparable<Category> {
	private String name;
	private ArrayList<ShoppingItem> shoppingItems;

	public Category(String name, ArrayList<ShoppingItem> shoppingItems) {
		super();
		this.name = name;
		this.shoppingItems = shoppingItems;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<ShoppingItem> getShoppingItems() {
		return shoppingItems;
	}

	public void setShoppingItems(ArrayList<ShoppingItem> shoppingItems) {
		this.shoppingItems = shoppingItems;
	}

	public void addShoppingItem(ShoppingItem shoppingItem) {
		shoppingItems.add(shoppingItem);
	}

	public void deleteShoppingItemByName(String shoppingItemName) {
		// find shopping item with matching name and remove it from our array
		for (final ShoppingItem shoppingItem : shoppingItems) {
			if (shoppingItem.getName().equalsIgnoreCase(shoppingItemName))
				shoppingItems.remove(shoppingItem);
		}
	}

	public void deleteShoppingItemById(int shoppingItemId) {
		// find shopping item with matching id and remove it from our array
		for (final ShoppingItem shoppingItem : shoppingItems) {
			if (shoppingItem.getId() == shoppingItemId)
				shoppingItems.remove(shoppingItem);
		}
	}

	public ShoppingItem getShoppingItemByName(String shoppingItemName) {
		// find shopping item with matching name and return it
		for (final ShoppingItem shoppingItem : shoppingItems) {
			if (shoppingItem.getName().equalsIgnoreCase(shoppingItemName))
				return shoppingItem;
		}

		// if we went over all the objects and we found no match we return null
		return null;
	}

	public ShoppingItem getShoppingItemById(int shoppingItemId) {
		// find shopping item with matching name and return it
		for (final ShoppingItem shoppingItem : shoppingItems) {
			if (shoppingItem.getId() == shoppingItemId)
				return shoppingItem;
		}

		// if we went over all the objects and we found no match we return null
		return null;
	}

	public void sort() {
		Collections.sort(shoppingItems);
	}

	@Override
	public int compareTo(Category another) {

		return name.compareTo(another.getName());
	}
}
