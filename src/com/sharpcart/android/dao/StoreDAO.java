package com.sharpcart.android.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;

import com.sharpcart.android.model.Store;
import com.sharpcart.android.provider.SharpCartContentProvider;

public class StoreDAO {
    private static final StoreDAO instance = new StoreDAO();

    private StoreDAO() {
    }

    public static StoreDAO getInstance() {
    	return instance;
    }
    
    //Creates a ContentValues object based on the values within a provided shoppingItem
    private ContentValues getStoreContentValues(final Store store) {
		final ContentValues cv = new ContentValues();
		cv.put(SharpCartContentProvider.COLUMN_ID, store.getId());
		cv.put(SharpCartContentProvider.COLUMN_NAME, store.getName());
		cv.put(SharpCartContentProvider.COLUMN_STREET, store.getStreet());
		cv.put(SharpCartContentProvider.COLUMN_CITY, store.getCity());
		cv.put(SharpCartContentProvider.COLUMN_STATE, store.getState());
		cv.put(SharpCartContentProvider.COLUMN_IMAGE_LOCATION, store.getImageLocation());
		cv.put(SharpCartContentProvider.COLUMN_ZIP, store.getZip());
		cv.put(SharpCartContentProvider.COLUMN_ON_SALE_FLYER_URL, store.getOnSaleFlyerURL());
		
		return cv;
    }
    
    public void addStore(final ContentResolver contentResolver,final Store store)
    {
    	final ContentValues contentValue = getStoreContentValues(store);
    	contentResolver.insert(SharpCartContentProvider.CONTENT_URI_STORE, contentValue);
    }
    
    public List<Store> getStore(final ContentResolver contentResolver, final String selection)
    {
    	//Run a query on db to get stores
    	final Cursor cursor = contentResolver.query(
		SharpCartContentProvider.CONTENT_URI_STORE, 
		null,
		selection, 
		null, 
		null);

		final List<Store> stores = new ArrayList<Store>();
		
		//iterate over query results and create new shopping item objects
		while (cursor.moveToNext()) 
		{
		    final Store currentStore = new Store();
		    
		    //Populate store object with the information from the database query within the cursor object
		    currentStore.setId(cursor.getLong(cursor.getColumnIndexOrThrow(SharpCartContentProvider.COLUMN_ID)));
		    currentStore.setName(cursor.getString(cursor.getColumnIndexOrThrow(SharpCartContentProvider.COLUMN_NAME)));
		    currentStore.setStreet(cursor.getString(cursor.getColumnIndexOrThrow(SharpCartContentProvider.COLUMN_STREET)));
		    currentStore.setCity(cursor.getString(cursor.getColumnIndexOrThrow(SharpCartContentProvider.COLUMN_CITY)));
		    currentStore.setState(cursor.getString(cursor.getColumnIndexOrThrow(SharpCartContentProvider.COLUMN_STATE)));
		    currentStore.setZip(cursor.getString(cursor.getColumnIndexOrThrow(SharpCartContentProvider.COLUMN_ZIP)));
		    currentStore.setImageLocation(cursor.getString(cursor.getColumnIndexOrThrow(SharpCartContentProvider.COLUMN_IMAGE_LOCATION)));
		    currentStore.setOnSaleFlyerURL(cursor.getString(cursor.getColumnIndexOrThrow(SharpCartContentProvider.COLUMN_ON_SALE_FLYER_URL)));
		    
		    stores.add(currentStore);
		}
	
		cursor.close();
		
		return stores;
    }
    
    public int clear(final ContentResolver contentResolver)
    {
    	int ret = 0;

	    ret = contentResolver.delete(
		    SharpCartContentProvider.CONTENT_URI_STORE,
		    null, 
		    null);

	    return ret;
    }
}
