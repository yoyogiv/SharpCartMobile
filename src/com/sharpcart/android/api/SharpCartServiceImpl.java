package com.sharpcart.android.api;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import org.apache.http.auth.AuthenticationException;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.sharpcart.android.exception.SharpCartException;
import com.sharpcart.android.model.MainSharpList;
import com.sharpcart.android.model.Sale;
import com.sharpcart.android.model.SharpList;
import com.sharpcart.android.model.ShoppingItem;
import com.sharpcart.android.model.ShoppingListItem;
import com.sharpcart.android.model.StorePrices;
import com.sharpcart.android.model.UserProfile;
import com.sharpcart.android.net.SimpleHttpHelper;

public class SharpCartServiceImpl {
    private static final String TAG = SharpCartServiceImpl.class.getCanonicalName();

    private static Type getSharpListToken() {
		return new TypeToken<List<SharpList>>() {
		}.getType();
    }

    private static Type getStoresToken() {
		return new TypeToken<List<StorePrices>>() {
		}.getType();
    }

    private static Type getShoppingItemToken() {
		//return new TypeToken<List<ShoppingListItem>>() {}.getType();
    	return new TypeToken<List<ShoppingItem>>() {}.getType();
    }
    
    private static Type getSaleToken() {
		return new TypeToken<List<Sale>>() {
		}.getType();
    }
    
    private static Type getUserProfileToken() {
		return new TypeToken<UserProfile>() {
		}.getType();
    }
    
    /*
     * fetch sharp lists for a specific user
     */
    public static List<SharpList> fetchSharpLists(final String username)
	    throws AuthenticationException, JsonParseException, IOException,SharpCartException {
		
    	Log.d(TAG, "Fetching Sharp Lists...");
		final String url = SharpCartUrlFactory.getInstance().getSharpListsUrl();
	
		//String response = HttpHelper.getHttpResponseAsStringUsingPOST(url,"username=" + username + "&action=getSharpLists");

		String response = SimpleHttpHelper.doPost(url,"application/x-www-form-urlencoded","username=" + username + "&action=getSharpLists");

		//remove /n and /r from response
		response = response.replaceAll("(\\r|\\n)", "");
		
		final Gson gson = new Gson();
	
		final List<SharpList> lists = gson.fromJson(response, getSharpListToken());
	
		return lists;
    }
    
    /*
     * fetch stores for a specific user
     */
    public static List<StorePrices> fetchStores(final String username)
	    throws AuthenticationException, JsonParseException, IOException,SharpCartException {
		
    	Log.d(TAG, "Fetching Store...");
		final String url = SharpCartUrlFactory.getInstance().getStoresUrl();
	
		//String response = HttpHelper.getHttpResponseAsStringUsingPOST(url,"username=" + username + "&action=getStores");
	
		String response = SimpleHttpHelper.doPost(url,"application/x-www-form-urlencoded","username=" + username + "&action=getStores");

		//remove /n and /r from response
		response = response.replaceAll("(\\r|\\n)", "");
		
		final Gson gson = new Gson();
	
		final List<StorePrices> stores = gson.fromJson(response, getStoresToken());
	
		return stores;
    }
    
    /*
     * fetch prices for a specific user, store and sharp list 
     */
    public static List<ShoppingListItem> fetchPrices(final String username,final String storeName, final String sharpListId)
	    throws AuthenticationException, JsonParseException, IOException,SharpCartException {
		
    	Log.d(TAG, "Fetching Prices...");
		final String url = SharpCartUrlFactory.getInstance().getPricesUrl();
	
		//String response = HttpHelper.getHttpResponseAsStringUsingPOST(url,"username=" + username + "&storeName=" + storeName+ "&sharpListId=" + sharpListId + "&action=getPrices");
	
		String response = SimpleHttpHelper.doPost(url,"application/x-www-form-urlencoded","username=" + username + "&storeName=" + storeName+ "&sharpListId=" + sharpListId + "&action=getPrices");

		//remove /n and /r from response
		response = response.replaceAll("(\\r|\\n)", "");
		
		final Gson gson = new Gson();
	
		final List<ShoppingListItem> items = gson.fromJson(response,getShoppingItemToken());
	
		return items;
    }

    /*
     * fetch all shopping items on sale for a specific user
     */
    public static List<Sale> fetchShoppingItemsOnSale(final String username)
    	    throws AuthenticationException, JsonParseException, IOException,SharpCartException {
    		
        	Log.d(TAG, "Fetching Shopping Items on Sale...");
    		final String url = SharpCartUrlFactory.getInstance().getItemsOnSaleUrl();
    	
    		//String response = HttpHelper.getHttpResponseAsStringUsingPOST(url,"username=" + username + "&action=getShoppingItemsOnSale");
    	
    		String response = SimpleHttpHelper.doPost(url,"application/x-www-form-urlencoded","username=" + username + "&action=getShoppingItemsOnSale");

    		//remove /n and /r from response
    		response = response.replaceAll("(\\r|\\n)", "");
    		
    		final Gson gson = new Gson();
    	
    		final List<Sale> itemsOnSale = gson.fromJson(response,getSaleToken());
    	
    		return itemsOnSale;
        }

    /*
     * fetch unavailable items for a specific user
     */
    public static List<ShoppingItem> fetchUnavailableItems(final String username)
    	    throws AuthenticationException, JsonParseException, IOException,SharpCartException {
    		
        	Log.d(TAG, "Fetching Unavailable Items...");
    		final String url = SharpCartUrlFactory.getInstance().getUnavailableItemsUrl();
    	
    		//String response = HttpHelper.getHttpResponseAsStringUsingPOST(url,"username=" + username + "&action=getUnavailableItems");
 
    		String response = SimpleHttpHelper.doPost(url,"application/x-www-form-urlencoded","userName=" + username);

    		//remove /n and /r from response
    		response = response.replaceAll("(\\r|\\n)", "");
    		
    		//change all uppercase to lower case
    		response = response.toLowerCase();
    		
    		final Gson gson=  new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm").create();
    	
    		final List<ShoppingItem> unavailableItems = gson.fromJson(response,getShoppingItemToken());
    	
    		return unavailableItems;
        }
    
    /*
     * fetch active sharp list items for a specific user
     */
    public static List<ShoppingListItem> fetchActiveSharpListItems(final String username)
    	    throws AuthenticationException, JsonParseException, IOException,SharpCartException {
    		
        	Log.d(TAG, "Fetching Active Sharp List Items...");
        	
 		   	//Turn MainSharpList object into a json string
 		   	final Gson gson = new Gson();
 		   	
 		   	final String json = gson.toJson(MainSharpList.getInstance());
 		   
    		final String url = SharpCartUrlFactory.getInstance().getSyncActiveSharpListUrl();
    	
    		//String response = HttpHelper.getHttpResponseAsString(url, "POST","application/json", json);
    
    		String response = SimpleHttpHelper.doPost(url,"application/json",json);

    		//remove /n and /r from response
    		response = response.replaceAll("(\\r|\\n)", "");
    		
    		//change all uppercase to lower case
    		//response = response.toLowerCase();
    	
    		final List<ShoppingListItem> activeSharpListItems = gson.fromJson(response,getShoppingItemToken());
    	
    		return activeSharpListItems;
        }
    
    /*
     * fetch user profile from server
     */
    public static UserProfile fetchUserProfile(final String userName)
    	    throws AuthenticationException, JsonParseException, IOException,SharpCartException {
    		
        	Log.d(TAG, "Fetching User Profile...");
        	
 		   	//Turn UserProfile object into a json string  	
 		   	final Gson gson=  new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm").create();
 		   	
 		   	UserProfile.getInstance().setUserName(userName);
 		   	
 		   	final String json = gson.toJson(UserProfile.getInstance());
 		   
    		final String url = SharpCartUrlFactory.getInstance().getUserProfileUrl();
    	
    		//String response = HttpHelper.getHttpResponseAsString(url, "POST","application/json", json);

    		String response = SimpleHttpHelper.doPost(url,"application/json",json);

    		//remove /n and /r from response
    		response = response.replaceAll("(\\r|\\n)", "");
    		
    		//change all uppercase to lower case
    		//response = response.toLowerCase();
    		
    		final UserProfile userProfile = gson.fromJson(response,getUserProfileToken());
    	
    		return userProfile;
        }
}
