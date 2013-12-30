package com.sharpcart.android.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;

import com.sharpcart.android.model.SharpList;
import com.sharpcart.android.model.ShoppingItem;
import com.sharpcart.android.provider.SharpCartContentProvider;

public class MainSharpListDAO {
    private static final MainSharpListDAO instance = new MainSharpListDAO();

    private MainSharpListDAO() {
    }

    public static MainSharpListDAO getInstance() {
    	return instance;
    }
    
    //Creates a ContentValues object based on the values within a provided shoppingItem
    private ContentValues getMainSharpListItemContentValues(ShoppingItem shoppingItem) {
		ContentValues cv = new ContentValues();
		cv.put(SharpCartContentProvider.COLUMN_ID, shoppingItem.getId());
		cv.put(SharpCartContentProvider.COLUMN_NAME, shoppingItem.getName());
		cv.put(SharpCartContentProvider.COLUMN_DESCRIPTION, shoppingItem.getDescription());
		cv.put(SharpCartContentProvider.COLUMN_SHOPPING_ITEM_CATEGORY_ID, shoppingItem.getCategoryId());
		cv.put(SharpCartContentProvider.COLUMN_SHOPPING_ITEM_UNIT_ID, shoppingItem.getUnitId());
		cv.put(SharpCartContentProvider.COLUMN_IMAGE_LOCATION, shoppingItem.getImageLocation());
		cv.put(SharpCartContentProvider.COLUMN_QUANTITY, shoppingItem.getQuantity());
		
		return cv;
    }
    
    //Add a new item to the sharp list table = a new row
    public void addNewItemToMainSharpList(ContentResolver contentResolver, ShoppingItem shoppingItem) {
    	/*
    	 * generate a content value object based on the provided shoppingItem object 
    	 * and use it to create a new row in our main sharp list table
    	 */
    	ContentValues contentValue = getMainSharpListItemContentValues(shoppingItem);
    	contentResolver.insert(SharpCartContentProvider.CONTENT_URI_SHARP_LIST_ITEMS, contentValue);
    }

    /*
     * This method will get a list of shopping items with a specific selection from
     * the device database and turn them into an ArrayList of shopping item objects
     */
    public List<ShoppingItem> getMainSharpListItemsWithSelection(ContentResolver contentResolver, String selection) {
    	
    	//Run a query on db to get shopping items from the main sharp list table
    	Cursor cursor = contentResolver.query(
		SharpCartContentProvider.CONTENT_URI_SHARP_LIST_ITEMS, 
		null,
		selection, 
		null, 
		null);

		List<ShoppingItem> list = new ArrayList<ShoppingItem>();
		
		//iterate over query results and create new shopping item objects
		while (cursor.moveToNext()) 
		{
		    ShoppingItem currentShoppingItem = new ShoppingItem();
		    
		    //Populate shopping item object with the information from the database query within the cursor object
		    currentShoppingItem.setId(cursor.getInt(cursor.getColumnIndexOrThrow(SharpCartContentProvider.COLUMN_ID)));
		    currentShoppingItem.setName(cursor.getString(cursor.getColumnIndexOrThrow(SharpCartContentProvider.COLUMN_NAME)));
		    currentShoppingItem.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(SharpCartContentProvider.COLUMN_DESCRIPTION)));
		    currentShoppingItem.setCategoryId(cursor.getInt(cursor.getColumnIndexOrThrow(SharpCartContentProvider.COLUMN_SHOPPING_ITEM_CATEGORY_ID)));
		    currentShoppingItem.setUnitId(cursor.getInt(cursor.getColumnIndexOrThrow(SharpCartContentProvider.COLUMN_SHOPPING_ITEM_UNIT_ID)));
		    currentShoppingItem.setImageLocation(cursor.getString(cursor.getColumnIndexOrThrow(SharpCartContentProvider.COLUMN_IMAGE_LOCATION)));
		    currentShoppingItem.setQuantity(cursor.getDouble(cursor.getColumnIndexOrThrow(SharpCartContentProvider.COLUMN_QUANTITY)));
		    
		    list.add(currentShoppingItem);
		}
	
		cursor.close();
		return list;
    }
    
    /*
     * delete an item from the main sharp list table using the item id
     */
    public int deleteMainSharpListItem(ContentResolver contentResolver, int id) {
    	int ret = 0;

	    ret = contentResolver.delete(
		    SharpCartContentProvider.CONTENT_URI_SHARP_LIST_ITEMS,
		    SharpCartContentProvider.COLUMN_ID + "=" + id, null);

	    return ret;
    }

}
