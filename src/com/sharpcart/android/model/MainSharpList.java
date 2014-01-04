package com.sharpcart.android.model;

import java.util.ArrayList;
import java.util.List;

public class MainSharpList {

    private static final MainSharpList instance = new MainSharpList();
    
    private List<ShoppingItem> mainSharpList;
    
    private MainSharpList() {
    	mainSharpList = new ArrayList<ShoppingItem>();
    }

    public static MainSharpList getInstance() {
    	return instance;
    }
    
    public boolean addShoppingItemToList(ShoppingItem shoppingItem)
    {
    	 return mainSharpList.add(shoppingItem);
    }
    
    public boolean removeShoppingItemFromList(ShoppingItem shoppingItem)
    {
    	return mainSharpList.remove(shoppingItem);
    }
    
    public boolean removeShoppingItemFromList(int shoppingItemId)
    {
    	for (ShoppingItem item : mainSharpList)
    	{
    		if (item.getId()==shoppingItemId)
    			 return mainSharpList.remove(item);
    	}
    	
    	return false;
    }
    
}
