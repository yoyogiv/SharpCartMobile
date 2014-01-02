package com.sharpcart.android.api;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import org.apache.http.auth.AuthenticationException;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.sharpcart.android.exception.SharpCartException;
import com.sharpcart.android.model.Sale;
import com.sharpcart.android.model.SharpList;
import com.sharpcart.android.model.ShoppingItem;
import com.sharpcart.android.model.Store;
import com.sharpcart.android.net.HttpHelper;

public class SharpCartServiceImpl {
    private static final String TAG = SharpCartServiceImpl.class.getCanonicalName();

    private static Type getSharpListToken() {
		return new TypeToken<List<SharpList>>() {
		}.getType();
    }

    private static Type getStoresToken() {
		return new TypeToken<List<Store>>() {
		}.getType();
    }

    private static Type getShoppingItemToken() {
		return new TypeToken<List<ShoppingItem>>() {
		}.getType();
    }
    
    private static Type getSaleToken() {
		return new TypeToken<List<Sale>>() {
		}.getType();
    }
    
    /*
     * fetch sharp lists for a specific user
     */
    public static List<SharpList> fetchSharpLists(String username)
	    throws AuthenticationException, JsonParseException, IOException,SharpCartException {
		
    	Log.d(TAG, "Fetching Sharp Lists...");
		String url = SharpCartUrlFactory.getInstance().getSharpListsUrl();
	
		String response = HttpHelper.getHttpResponseAsStringUsingPOST(url,
			"username=" + username + "&action=getSharpLists");
	
		//remove /n and /r from response
		response = response.replaceAll("(\\r|\\n)", "");
		
		Gson gson = new Gson();
	
		List<SharpList> lists = gson.fromJson(response, getSharpListToken());
	
		return lists;
    }
    
    /*
     * fetch stores for a specific user
     */
    public static List<Store> fetchStores(String username)
	    throws AuthenticationException, JsonParseException, IOException,SharpCartException {
		
    	Log.d(TAG, "Fetching Store...");
		String url = SharpCartUrlFactory.getInstance().getStoresUrl();
	
		String response = HttpHelper.getHttpResponseAsStringUsingPOST(url,
			"username=" + username + "&action=getStores");
		
		//remove /n and /r from response
		response = response.replaceAll("(\\r|\\n)", "");
		
		Gson gson = new Gson();
	
		List<Store> stores = gson.fromJson(response, getStoresToken());
	
		return stores;
    }
    
    /*
     * fetch prices for a specific user, store and sharp list 
     */
    public static List<ShoppingItem> fetchPrices(String username,String storeName, String sharpListId)
	    throws AuthenticationException, JsonParseException, IOException,SharpCartException {
		
    	Log.d(TAG, "Fetching Prices...");
		String url = SharpCartUrlFactory.getInstance().getPricesUrl();
	
		String response = HttpHelper.getHttpResponseAsStringUsingPOST(url,
			"username=" + username + "&storeName=" + storeName
				+ "&sharpListId=" + sharpListId + "&action=getPrices");
	
		//remove /n and /r from response
		response = response.replaceAll("(\\r|\\n)", "");
		
		Gson gson = new Gson();
	
		List<ShoppingItem> items = gson.fromJson(response,getShoppingItemToken());
	
		return items;
    }

    /*
     * fetch all shopping items on sale for a specific user
     */
    public static List<Sale> fetchShoppingItemsOnSale(String username)
    	    throws AuthenticationException, JsonParseException, IOException,SharpCartException {
    		
        	Log.d(TAG, "Fetching Shopping Items on Sale...");
    		String url = SharpCartUrlFactory.getInstance().getItemsOnSaleUrl();
    	
    		String response = HttpHelper.getHttpResponseAsStringUsingPOST(url,"username=" + username + "&action=getShoppingItemsOnSale");
    	
    		//remove /n and /r from response
    		response = response.replaceAll("(\\r|\\n)", "");
    		
    		Gson gson = new Gson();
    	
    		List<Sale> itemsOnSale = gson.fromJson(response,getSaleToken());
    	
    		return itemsOnSale;
        }

    /*
     * fetch unavailable items for a specific user
     */
    public static List<ShoppingItem> fetchUnavailableItems(String username)
    	    throws AuthenticationException, JsonParseException, IOException,SharpCartException {
    		
        	Log.d(TAG, "Fetching Unavailable Items...");
    		String url = SharpCartUrlFactory.getInstance().getUnavailableItemsUrl();
    	
    		String response = HttpHelper.getHttpResponseAsStringUsingPOST(url,"username=" + username + "&action=getUnavailableItems");
    	
    		//remove /n and /r from response
    		response = response.replaceAll("(\\r|\\n)", "");
    		
    		Gson gson = new Gson();
    	
    		List<ShoppingItem> unavailableItems = gson.fromJson(response,getShoppingItemToken());
    	
    		return unavailableItems;
        }
    
    public static SharpList createSharpList(String title)
	    throws AuthenticationException, JsonParseException, IOException,SharpCartException {
		
    	Log.d(TAG, "Creating Sharp list " + title);
		String urlFmt = SharpCartUrlFactory.getInstance()
			.getSharpListAddUrlFmt();
		String url = String.format(urlFmt, title);
		String response = HttpHelper.getHttpResponseAsString(url, null);
	
		//remove /n and /r from response
		response = response.replaceAll("(\\r|\\n)", "");
		
		Gson gson = new Gson();
		List<SharpList> lists = gson.fromJson(response, getSharpListToken());
	
		if (lists.size() != 1) {
		    throw new SharpCartException("Error creating Sharp List " + title);
		}
	
		return lists.get(0);
    }

    public static void deleteSharpList(int id) throws AuthenticationException,SharpCartException {
		
    	Log.d(TAG, "Deleting Sharp list with id " + id);
		String urlFmt = SharpCartUrlFactory.getInstance()
			.getSharpListDeleteUrlFmt();
		String url = String.format(urlFmt, id);
		HttpHelper.getHttpResponseAsString(url, null);
    }

    public static Store createStore(String title)
	    throws AuthenticationException, JsonParseException, IOException,SharpCartException {
		
    	Log.d(TAG, "Creating Store " + title);
		String urlFmt = SharpCartUrlFactory.getInstance().getStoreAddUrlFmt();
		String url = String.format(urlFmt, title);
		String response = HttpHelper.getHttpResponseAsString(url, null);
	
		Gson gson = new Gson();
		List<Store> stores = gson.fromJson(response, getStoresToken());
	
		if (stores.size() != 1) {
		    throw new SharpCartException("Error creating Store " + title);
		}
	
		return stores.get(0);
    }

    public static void deleteStore(int id) throws AuthenticationException,SharpCartException {
		
    	Log.d(TAG, "Deleting Store with id " + id);
		String urlFmt = SharpCartUrlFactory.getInstance()
			.getStoreDeleteUrlFmt();
		String url = String.format(urlFmt, id);
		HttpHelper.getHttpResponseAsString(url, null);
    }
}
