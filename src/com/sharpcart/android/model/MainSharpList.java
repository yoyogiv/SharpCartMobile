package com.sharpcart.android.model;

import java.util.ArrayList;
import java.util.List;

public class MainSharpList {

    private static final MainSharpList instance = new MainSharpList();
    
	private List<ShoppingItem> mainSharpList;
    private String userName;
    
    private MainSharpList() {
    	mainSharpList = new ArrayList<ShoppingItem>();
    	userName = "";
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
    
    /**
	 * @return the mainSharpList
	 */
	public List<ShoppingItem> getMainSharpList() {
		return mainSharpList;
	}

	/**
	 * @param mainSharpList the mainSharpList to set
	 */
	public void setMainSharpList(List<ShoppingItem> mainSharpList) {
		this.mainSharpList = mainSharpList;
	}

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}
    
}
