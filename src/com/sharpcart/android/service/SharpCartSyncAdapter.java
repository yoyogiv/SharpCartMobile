package com.sharpcart.android.service;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.http.ParseException;
import org.apache.http.auth.AuthenticationException;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SyncResult;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.JsonParseException;
import com.sharpcart.android.api.SharpCartServiceImpl;
import com.sharpcart.android.authenticator.AuthenticatorActivity;

import com.sharpcart.android.dao.MainSharpListDAO;
import com.sharpcart.android.exception.SharpCartException;
import com.sharpcart.android.model.MainSharpList;
import com.sharpcart.android.model.Sale;
import com.sharpcart.android.model.SharpList;
import com.sharpcart.android.model.ShoppingItem;
import com.sharpcart.android.model.Store;
import com.sharpcart.android.model.UserProfile;
import com.sharpcart.android.provider.SharpCartContentProvider;
import com.sharpcart.android.utilities.SharpCartUtilities;


public class SharpCartSyncAdapter extends AbstractThreadedSyncAdapter {

    private static final String TAG = SharpCartSyncAdapter.class.getCanonicalName();
    private final AccountManager mAccountManager;

    public SharpCartSyncAdapter(final Context context, final boolean autoInitialize) {
		super(context, autoInitialize);
		mAccountManager = AccountManager.get(context);
    }

    @Override
    public void onPerformSync(final Account account, final Bundle extras, final String authority,final ContentProviderClient provider, final SyncResult syncResult) {
    
	String authtoken = null;
	
	//only try sync if we have an internet conection
	if (SharpCartUtilities.getInstance().hasActiveInternetConnection(getContext()))
		{
			try {
			    authtoken = mAccountManager.blockingGetAuthToken(account,
				    AuthenticatorActivity.PARAM_AUTHTOKEN_TYPE, true);
		
			    final Account[] accounts = mAccountManager
				    .getAccountsByType(AuthenticatorActivity.PARAM_ACCOUNT_TYPE);
		
			    final List<Sale> sales = fetchSales(accounts[0].name);
			   
			    final List<ShoppingItem> unavailableItems = fetchUnavailableItems(accounts[0].name);
			    
			    final List<ShoppingItem> activeSharpListItems = fetchActiveSharpListItems(accounts[0].name);
			    
			    final UserProfile userProfile = fetchUserProfile(accounts[0].name);
			    	
			    syncShoppingItemsOnSale(sales);
			    
			    syncUnavailableItems(unavailableItems);
			    
			    syncActiveSharpListItems(activeSharpListItems);
			    
			    syncUserProfile(userProfile);
			    
				} catch (final Exception e) {
				    handleException(authtoken, e, syncResult);
				}
		}
    }

    protected void syncShoppingItemsOnSale(final List<Sale> itemsOnSale)
    {
		final ContentValues cv = new ContentValues();
		
		//Reset all items on sale to 0 
		cv.put(SharpCartContentProvider.COLUMN_ON_SALE, "0");
   		getContext().getContentResolver().update(
				SharpCartContentProvider.CONTENT_URI_SHOPPING_ITEMS, 
				cv, 
				null, 
				null);
   		
    	//iterate over all shopping items and update their "On_Sale" field
    	for (final Sale item : itemsOnSale)
    	{
    		cv.put(SharpCartContentProvider.COLUMN_ON_SALE, "1");
    		
    		getContext().getContentResolver().update(
    				SharpCartContentProvider.CONTENT_URI_SHOPPING_ITEMS, 
    				cv, 
    				SharpCartContentProvider.COLUMN_ID+"="+item.getShopping_Item_Id(), 
    				null);
    	}
    	
    }
    
    protected void syncUnavailableItems(final List<ShoppingItem> unavilableItems)
    {
		final ContentValues cv = new ContentValues();
		
		//Reset all items active to 1
		cv.put(SharpCartContentProvider.COLUMN_ACTIVE, "1");
   		
		getContext().getContentResolver().update(
				SharpCartContentProvider.CONTENT_URI_SHOPPING_ITEMS, 
				cv, 
				null, 
				null);
   		
    	//iterate over all shopping items and update their "On_Sale" field
    	for (final ShoppingItem item : unavilableItems)
    	{
    		cv.put(SharpCartContentProvider.COLUMN_ACTIVE, "0");
    		
    		getContext().getContentResolver().update(
    				SharpCartContentProvider.CONTENT_URI_SHOPPING_ITEMS, 
    				cv, 
    				SharpCartContentProvider.COLUMN_ID + "=" + item.getId(), 
    				null);
    	}    	
    }
    
    /*
     * This method will make sure that our sharp lists are synced accross devices
     */
    protected void syncActiveSharpListItems(final List<ShoppingItem> activeSharpListItems)
    {
    	//set MainSharpList items to the list we got from the server
    	MainSharpList.getInstance().setMainSharpList(activeSharpListItems);
    	MainSharpList.getInstance().setIs_deleted(false);
    	
    	//update database list
    	//clear the table
		getContext().getContentResolver().delete(
				SharpCartContentProvider.CONTENT_URI_SHARP_LIST_ITEMS, 
				null, 
				null);
		
	   //make sure that oz items are back to package quantities and not oz
	   for (final ShoppingItem item : activeSharpListItems)
	   {
		   if (item.getUnit()!=null)
			   if ((item.getUnit().equalsIgnoreCase("oz")))
			   {
				   if (item.getConversion_ratio()!=-1)
					   item.setQuantity(item.getQuantity()*item.getConversion_ratio());
			   }
	   }
		   
		//Add items to table
		for (final ShoppingItem item : activeSharpListItems)
		{
			MainSharpListDAO.getInstance().addNewItemToMainSharpList(getContext().getContentResolver(), item);
		}
		
    }
    
    protected void syncUserProfile(final UserProfile userProfile)
    {
    	//set user profile to the one we got from the db
    	UserProfile.getInstance().update(userProfile);
    	
    	//update settings
    	final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
    	sharedPref.edit().putString("pref_zip", userProfile.getZip()).commit();
    	sharedPref.edit().putString("pref_family_size", userProfile.getFamilySize()).commit();
    	
    	final Set<String> stores = new TreeSet<String>();
    	String stores_string_from_db = userProfile.getStores();
    	
    	//remove any white space
    	stores_string_from_db = stores_string_from_db.replaceAll("\\s+","");
    	
    	final String[] stores_array = stores_string_from_db.split("-");
    	
    	for (final String store : stores_array)
    	{
    		stores.add(store);
    	}
    	
    	//update stores settings
    	sharedPref.edit().putStringSet("pref_stores", stores).commit();
    }
    
    protected List<SharpList> fetchSharpLists(final String username)
	    throws AuthenticationException, SharpCartException,
	    JsonParseException, IOException {
		final List<SharpList> list = SharpCartServiceImpl.fetchSharpLists(username);
	
		return list;
    }

    protected List<Store> fetchStores(final String username)
	    throws AuthenticationException, SharpCartException,
	    JsonParseException, IOException {
		final List<Store> stores = SharpCartServiceImpl.fetchStores(username);
	
		return stores;
    }

    protected List<Sale> fetchSales(final String username)
    	    throws AuthenticationException, SharpCartException,JsonParseException, IOException {
    		
    		final List<Sale> sales = SharpCartServiceImpl.fetchShoppingItemsOnSale(username);
    	
    		return sales;
        }
    
    protected List<ShoppingItem> fetchUnavailableItems(final String username)
    	    throws AuthenticationException, SharpCartException,JsonParseException, IOException {
    		
    		final List<ShoppingItem> unavilableItems = SharpCartServiceImpl.fetchUnavailableItems(username);
    	
    		return unavilableItems;
        }
    
    protected List<ShoppingItem> fetchActiveSharpListItems(final String username)
    	    throws AuthenticationException, SharpCartException,JsonParseException, IOException {
    		
    		final List<ShoppingItem> activeSharpListItems = SharpCartServiceImpl.fetchActiveSharpListItems(username);
    	
    		return activeSharpListItems;
        }
    
    protected UserProfile fetchUserProfile(final String username)
    	    throws AuthenticationException, SharpCartException,JsonParseException, IOException {
    		
    		final UserProfile userProfile = SharpCartServiceImpl.fetchUserProfile(username);
    	
    		return userProfile;
        }
    
    private void handleException(final String authtoken, final Exception e,
	    final SyncResult syncResult) {
	if (e instanceof AuthenticatorException) {
	    syncResult.stats.numParseExceptions++;
	    Log.e(TAG, "AuthenticatorException", e);
	} else if (e instanceof OperationCanceledException) {
	    Log.e(TAG, "OperationCanceledExcepion", e);
	} else if (e instanceof IOException) {
	    Log.e(TAG, "IOException", e);
	    syncResult.stats.numIoExceptions++;
	} else if (e instanceof AuthenticationException) {
	    mAccountManager.invalidateAuthToken(
		    AuthenticatorActivity.PARAM_ACCOUNT_TYPE, authtoken);
	    // The numAuthExceptions require user intervention and are
	    // considered hard errors.
	    // We automatically get a new hash, so let's make SyncManager retry
	    // automatically.
	    syncResult.stats.numIoExceptions++;
	    Log.e(TAG, "AuthenticationException", e);
	} else if (e instanceof ParseException) {
	    syncResult.stats.numParseExceptions++;
	    Log.e(TAG, "ParseException", e);
	} else if (e instanceof JsonParseException) {
	    syncResult.stats.numParseExceptions++;
	    Log.e(TAG, "JSONException", e);
	} else if (e instanceof SharpCartException) {
	    Log.e(TAG, "SharpCartException", e);
	}
    }

}
