package com.sharpcart.android.model;

import java.util.ArrayList;
import java.util.List;

public class MainSharpList {

    private static final MainSharpList instance = new MainSharpList();
    
	private List<ShoppingItem> mainSharpList;
    private String userName;
    private String email;
    private String action;
    private String listTitle;
    
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
    
	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the action
	 */
	public String getAction() {
		return action;
	}

	/**
	 * @param action the action to set
	 */
	public void setAction(String action) {
		this.action = action;
	}

	/**
	 * @return the listTitle
	 */
	public String getListTitle() {
		return listTitle;
	}

	/**
	 * @param listTitle the listTitle to set
	 */
	public void setListTitle(String listTitle) {
		this.listTitle = listTitle;
	}

	public void setItemQuantity(int shoppingItemId,double itemQuantity)
	{
    	for (ShoppingItem item : mainSharpList)
    	{
    		if (item.getId()==shoppingItemId)
    			 item.setQuantity(itemQuantity);
    	}
    	
	}
	
	public void empty()
	{
		mainSharpList.clear();
	}
}
