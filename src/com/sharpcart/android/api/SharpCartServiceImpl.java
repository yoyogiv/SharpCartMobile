package com.sharpcart.android.api;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import org.apache.http.auth.AuthenticationException;

import android.text.format.DateFormat;
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

    private static Type getMainSharpListToken() {
		return new TypeToken<MainSharpList>() {
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
    		
    		final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:SS").create();
    	
    		final List<Sale> itemsOnSale = gson.fromJson(response,getSaleToken());
    	
        	Log.d(TAG, "Fetched Shopping Items on Sale");
        	
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
    		
    		final Gson gson=  new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:SS").create();
    	
    		final List<ShoppingItem> unavailableItems = gson.fromJson(response,getShoppingItemToken());
    	
        	Log.d(TAG, "Fetched Unavailable Items");
        	
    		return unavailableItems;
        }
    
    /*
     * fetch active sharp list items for a specific user
     */
    public static List<ShoppingListItem> fetchActiveSharpListItems(final String username)
    	    throws AuthenticationException, JsonParseException, IOException,SharpCartException {
    		
        	Log.d(TAG, "Fetching Active Sharp List Items...");
        	
 		   	//Turn MainSharpList object into a json string
 		   	final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:SS").create();
 		   	
 		   	final String json = gson.toJson(MainSharpList.getInstance());
 		   
    		final String url = SharpCartUrlFactory.getInstance().getSyncActiveSharpListUrl();
    
    		String response = SimpleHttpHelper.doPost(url,"application/json",json);

    		//remove /n and /r from response
    		response = response.replaceAll("(\\r|\\n)", "");
    		
        	Log.d(TAG, "Fetched Active Sharp List Items");
    	
    		final MainSharpList serverSharpList = gson.fromJson(response,getMainSharpListToken());
    		
    		//compare last update date between server and device sharp list
    		if (serverSharpList.getLastUpdated().after(MainSharpList.getInstance().getLastUpdated()))
    		{
        		final List<ShoppingListItem> activeSharpListItems = serverSharpList.getMainSharpList();
        		return activeSharpListItems;
    		} else
    		{
    			return null;
    		}      
        }
    
    /*
     * fetch user profile from server
     */
    public static UserProfile fetchUserProfile(final String userName)
    	    throws AuthenticationException, JsonParseException, IOException,SharpCartException {
    		
        	Log.d(TAG, "Fetching User Profile...");
        	
 		   	//Turn UserProfile object into a json string  	
 		   	final Gson gson=  new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:SS").create();
 		   	
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
    	
        	Log.d(TAG, "Fetched User Profile");
        	
    		return userProfile;
        }
}
