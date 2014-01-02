package com.sharpcart.android.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;

import com.sharpcart.android.model.ShoppingItem;
import com.sharpcart.android.provider.SharpCartContentProvider;

public class ShoppingItemDAO {
    private static final ShoppingItemDAO instance = new ShoppingItemDAO();

    private ShoppingItemDAO() {
    }

    public static ShoppingItemDAO getInstance() {
    	return instance;
    }
    
    private ContentValues getShoppingItmeContentValues(ShoppingItem shoppingItem) {
		ContentValues cv = new ContentValues();
		cv.put(SharpCartContentProvider.COLUMN_ID, shoppingItem.getId());
		cv.put(SharpCartContentProvider.COLUMN_NAME, shoppingItem.getName());
		cv.put(SharpCartContentProvider.COLUMN_DESCRIPTION, shoppingItem.getDescription());
		cv.put(SharpCartContentProvider.COLUMN_SHOPPING_ITEM_CATEGORY_ID, shoppingItem.getShopping_Item_Category_Id());
		cv.put(SharpCartContentProvider.COLUMN_SHOPPING_ITEM_UNIT_ID, shoppingItem.getShopping_Item_Unit_Id());
		cv.put(SharpCartContentProvider.COLUMN_IMAGE_LOCATION, shoppingItem.getImage_Location());
		cv.put(SharpCartContentProvider.COLUMN_UNIT_TO_ITEM_CONVERSION_RATIO, shoppingItem.getUnit_To_Item_Conversion_Ratio());
		
		return cv;
    }

    /*
     * This method will get a list of shopping items with a specific selection from
     * the device database and turn them into an ArrayList of ShoppingItem objects
     */
    private List<ShoppingItem> getShoppingItemsWithSelection(ContentResolver contentResolver, String selection) {
		
    	Cursor cursor = contentResolver.query(
			SharpCartContentProvider.CONTENT_URI_SHOPPING_ITEMS, null,
			selection, null, null);
	
		List<ShoppingItem> list = new ArrayList<ShoppingItem>();
	
		while (cursor.moveToNext()) {
		    int localId = cursor.getInt(cursor
			    .getColumnIndexOrThrow(SharpCartContentProvider.COLUMN_ID));
	
		    String name = cursor
			    .getString(cursor
				    .getColumnIndexOrThrow(SharpCartContentProvider.COLUMN_NAME));
	
		    String description = cursor
			    .getString(cursor
				    .getColumnIndexOrThrow(SharpCartContentProvider.COLUMN_DESCRIPTION));
	
		    int categoryId = cursor
			    .getInt(cursor
				    .getColumnIndexOrThrow(SharpCartContentProvider.COLUMN_SHOPPING_ITEM_CATEGORY_ID));
	
		    int unitId = cursor
				    .getInt(cursor
					    .getColumnIndexOrThrow(SharpCartContentProvider.COLUMN_SHOPPING_ITEM_UNIT_ID));
		    
		    String imageLocation = cursor
				    .getString(cursor
					    .getColumnIndexOrThrow(SharpCartContentProvider.COLUMN_IMAGE_LOCATION));
		    
		    double conversionRatio = cursor
				    .getDouble(cursor
					    .getColumnIndexOrThrow(SharpCartContentProvider.COLUMN_UNIT_TO_ITEM_CONVERSION_RATIO));
		    
		    ShoppingItem currentShoppingItem = new ShoppingItem();
		    
		    currentShoppingItem.setId(localId);
		    currentShoppingItem.setName(name);
		    currentShoppingItem.setDescription(description);
		    currentShoppingItem.setShopping_Item_Category_Id(categoryId);
		    currentShoppingItem.setShopping_Item_Unit_Id(unitId);
		    currentShoppingItem.setImage_Location(imageLocation);
		    currentShoppingItem.setUnit_To_Item_Conversion_Ratio(conversionRatio);
		    
		    list.add(currentShoppingItem);
		}
	
		cursor.close();
		return list;
    }
}
