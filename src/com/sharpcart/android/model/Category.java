package com.sharpcart.android.model;

import java.util.ArrayList;
import java.util.Collections;

public class Category implements Comparable<Category> {
	private String name;
	private ArrayList<ShoppingListItem> shoppingItems;

	public Category(final String name, final ArrayList<ShoppingListItem> shoppingItems) {
		super();
		this.name = name;
		this.shoppingItems = shoppingItems;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public ArrayList<ShoppingListItem> getShoppingItems() {
		return shoppingItems;
	}

	public void setShoppingItems(final ArrayList<ShoppingListItem> shoppingItems) {
		this.shoppingItems = shoppingItems;
	}

	public void addShoppingItem(final ShoppingListItem shoppingItem) {
		shoppingItems.add(shoppingItem);
	}

	public void deleteShoppingItemByName(final String shoppingItemName) {
		// find shopping item with matching name and remove it from our array
		for (final ShoppingListItem shoppingItem : shoppingItems) {
			if (shoppingItem.getName().equalsIgnoreCase(shoppingItemName))
				shoppingItems.remove(shoppingItem);
		}
	}

	public void deleteShoppingItemById(final int shoppingItemId) {
		// find shopping item with matching id and remove it from our array
		for (final ShoppingListItem shoppingItem : shoppingItems) {
			if (shoppingItem.getId() == shoppingItemId)
				shoppingItems.remove(shoppingItem);
		}
	}

	public ShoppingListItem getShoppingItemByName(final String shoppingItemName) {
		// find shopping item with matching name and return it
		for (final ShoppingListItem shoppingItem : shoppingItems) {
			if (shoppingItem.getName().equalsIgnoreCase(shoppingItemName))
				return shoppingItem;
		}

		// if we went over all the objects and we found no match we return null
		return null;
	}

	public ShoppingListItem getShoppingItemById(final int shoppingItemId) {
		// find shopping item with matching name and return it
		for (final ShoppingListItem shoppingItem : shoppingItems) {
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
	public int compareTo(final Category another) {

		return name.compareTo(another.getName());
	}
}
