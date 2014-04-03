package com.sharpcart.android.service;

import java.io.IOException;
import java.util.Date;
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
import com.sharpcart.android.model.ShoppingListItem;
import com.sharpcart.android.model.StorePrices;
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
	Account[] accounts = null;
	if (SharpCartUtilities.getInstance().hasActiveInternetConnection(getContext()))
		{
			try {
			    authtoken = mAccountManager.blockingGetAuthToken(account,
				    AuthenticatorActivity.PARAM_AUTHTOKEN_TYPE, true);
		
			    accounts = mAccountManager
				    .getAccountsByType(AuthenticatorActivity.PARAM_ACCOUNT_TYPE);
			    
				} catch (final Exception e) {
				    handleException(authtoken, e, syncResult);
				}
			
			if (accounts!=null)
			{
			    //final List<Sale> sales = fetchSales(accounts[0].name);
			   
			    List<ShoppingItem> unavailableItems = null;
				try {
					unavailableItems = fetchUnavailableItems(accounts[0].name);
				} catch (AuthenticationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JsonParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SharpCartException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			    
			    MainSharpList serverSharpList = null;
				try {
					serverSharpList = fetchActiveSharpListItems(accounts[0].name);
				} catch (AuthenticationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JsonParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SharpCartException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			    
			    UserProfile userProfile = null;
				try {
					userProfile = fetchUserProfile(accounts[0].name);
				} catch (AuthenticationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JsonParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SharpCartException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	
			    /*
			    if (sales!=null)
			    	syncShoppingItemsOnSale(sales);
			    */
			    
			    if (unavailableItems!=null)
			    	syncUnavailableItems(unavailableItems);
			    
			    if (serverSharpList!=null)
			    	syncActiveSharpListItems(serverSharpList);
			    
			    if (userProfile!=null)
			    	syncUserProfile(userProfile);
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
   		
    	//iterate over all shopping items and update their "Active" field
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
    protected void syncActiveSharpListItems(final MainSharpList serverSharpList)
    {
    	final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
    	
    	if (serverSharpList.getLastUpdated().after(MainSharpList.getInstance().getLastUpdated()))
    	{
    		sharedPref.edit().putBoolean("canSyncSharpList", true).commit();
    	} else
    	{
    		sharedPref.edit().putBoolean("canSyncSharpList", false).commit();
    	}
    	
    	//only sync device sharp list if the user decided to do so
    	if ((sharedPref.getBoolean("shouldSyncSharpList", false))&&(sharedPref.getBoolean("canSyncSharpList", false)))
    	{
    		List<ShoppingListItem> activeSharpListItems = serverSharpList.getMainSharpList();
    		
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
		   for (final ShoppingListItem item : activeSharpListItems)
		   {
			   if (item.getUnit()!=null)
				   if ((item.getUnit().equalsIgnoreCase("oz")))
				   {
					   if (item.getConversion_ratio()!=-1)
					   {
						   final double tempQuantity = item.getQuantity()*item.getConversion_ratio();
						   //only update quantities that are at least 1
						   if (tempQuantity>=1)
							   item.setQuantity(tempQuantity);
					   }
				   }
		   }
			
			//Add items to table
			for (final ShoppingListItem item : activeSharpListItems)
			{
				MainSharpListDAO.getInstance().addNewItemToMainSharpList(getContext().getContentResolver(), item);
			}
			
			//set sync flag back to false
			sharedPref.edit().putBoolean("shouldSyncSharpList", false).commit();
			
			//set last updated time stamp
 		   MainSharpList.getInstance().setLastUpdated(serverSharpList.getLastUpdated());
 		   sharedPref.edit().putLong("sharp_list_last_updated", serverSharpList.getLastUpdated().getTime()).commit(); 
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
    
    protected MainSharpList fetchActiveSharpListItems(final String username)
    	    throws AuthenticationException, SharpCartException,JsonParseException, IOException {
    		
    		final MainSharpList serverSharpList = SharpCartServiceImpl.fetchActiveSharpListItems(username);
    	
    		return serverSharpList;
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
