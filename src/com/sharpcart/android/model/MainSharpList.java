package com.sharpcart.android.model;

import java.util.List;

public class MainSharpList {

	private List<ShoppingItem> shoppingItems;
	
	public void addNewShoppingItem(ShoppingItem shoppingItem)
	{
		shoppingItems.add(shoppingItem);
	}
	
	public void deleteShoppingItem(ShoppingItem shoppingItem)
	{
		shoppingItems.remove(shoppingItem);
	}
	
	public void clearList()
	{
		shoppingItems.clear();
	}
	
}
