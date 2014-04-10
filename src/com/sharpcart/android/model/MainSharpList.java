package com.sharpcart.android.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class MainSharpList {

    private static final MainSharpList instance = new MainSharpList();
    
	private List<ShoppingListItem> mainSharpList;
    private String userName;
    private String email;
    private String action;
    private String listTitle;
    private boolean is_deleted;
    private Date lastUpdated;
    private String timeZone;
    
    private MainSharpList() {
    	mainSharpList = new ArrayList<ShoppingListItem>();
    	userName = "";
    	is_deleted = false;
    	lastUpdated = new Date(0);
    	timeZone = TimeZone.getDefault().getID();  	
    }

    public static MainSharpList getInstance() {
    	return instance;
    }
    
    public boolean addShoppingItemToList(final ShoppingListItem shoppingItem)
    {
    	//if we already have a shopping item object with the same id in our list there is no need to add another one
    	if (isItemInList(shoppingItem.getId()))
    	{
    		return true;
    	}
    	else 
    		return mainSharpList.add(shoppingItem);
    }
    
    public boolean addShoppingItemToList(final int shoppingItemId)
    {
    	for (final ShoppingListItem item : mainSharpList)
    	{
    		if (item.getId()==shoppingItemId)
    		{
    			item.setIs_deleted(false);
    			return true;
    		}
    	}
    	
    	return false;
    }
    
    public boolean removeShoppingItemFromList(final ShoppingListItem shoppingItem)
    {
    	return mainSharpList.remove(shoppingItem);
    }
    
    public boolean removeShoppingItemFromList(final int shoppingItemId)
    {
    	for (final ShoppingListItem item : mainSharpList)
    	{
    		if (item.getId()==shoppingItemId)
    		{
    			 return mainSharpList.remove(item); //This will remove the item from the list
    			
    			 /*
    			//Mark the item as deleted for server sync operations
    			item.setIs_deleted(true);
    			*/
    		}
    	}
    	
    	return false;
    }
    
    /**
	 * @return the mainSharpList
	 */
	public List<ShoppingListItem> getMainSharpList() {
		return mainSharpList;
	}

	/**
	 * @param mainSharpList the mainSharpList to set
	 */
	public void setMainSharpList(final List<ShoppingListItem> mainSharpList) {
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
	public void setUserName(final String userName) {
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
	public void setEmail(final String email) {
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
	public void setAction(final String action) {
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
	public void setListTitle(final String listTitle) {
		this.listTitle = listTitle;
	}

	public void setItemQuantity(final int shoppingItemId,final double itemQuantity)
	{
    	for (final ShoppingListItem item : mainSharpList)
    	{
    		if (item.getId()==shoppingItemId)
    			 item.setQuantity(itemQuantity);
    	}
    	
	}
	
	public void empty()
	{
		mainSharpList.clear();
	}
	
	public boolean isItemInList(final int itemId)
	{
		for (final ShoppingListItem item : mainSharpList)
		{
			if (item.getId() == itemId)
			{
				return true;
			}
		}
		
		return false;
	}

	/**
	 * @return the is_deleted
	 */
	public boolean isIs_deleted() {
		return is_deleted;
	}

	/**
	 * @param is_deleted the is_deleted to set
	 */
	public void setIs_deleted(final boolean is_deleted) {
		this.is_deleted = is_deleted;
	}

	/**
	 * @return the lastUpdated
	 */
	public Date getLastUpdated() {
		return lastUpdated;
	}

	/**
	 * @param lastUpdated the lastUpdated to set
	 */
	public void setLastUpdated(final Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	/**
	 * @param lastUpdated the lastUpdated to set
	 */
	public void setLastUpdated(final Long lastUpdated) {
		this.lastUpdated = new Date(lastUpdated);
	}
	
	/**
	 * @return the timeZone
	 */
	public String getTimeZone() {
		return timeZone;
	}

	/**
	 * @param timeZone the timeZone to set
	 */
	public void setTimeZone(final String timeZone) {
		this.timeZone = timeZone;
	}
	
	
}
