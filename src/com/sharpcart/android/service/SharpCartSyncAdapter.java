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
import com.sharpcart.android.R;
import com.sharpcart.android.api.SharpCartServiceImpl;
import com.sharpcart.android.authenticator.AuthenticatorActivity;

import com.sharpcart.android.dao.MainSharpListDAO;
import com.sharpcart.android.exception.SharpCartException;
import com.sharpcart.android.fragment.MainSharpListFragment;
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

    public SharpCartSyncAdapter(Context context, boolean autoInitialize) {
		super(context, autoInitialize);
		mAccountManager = AccountManager.get(context);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,ContentProviderClient provider, SyncResult syncResult) {
    
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

    protected void syncShoppingItemsOnSale(List<Sale> itemsOnSale)
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
    
    protected void syncUnavailableItems(List<ShoppingItem> unavilableItems)
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
    
    protected void syncActiveSharpListItems(List<ShoppingItem> activeSharpListItems)
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
		
		//Add items to table
		for (ShoppingItem item : activeSharpListItems)
		{
			MainSharpListDAO.getInstance().addNewItemToMainSharpList(getContext().getContentResolver(), item);
		}
		
    }
    
    protected void syncUserProfile(UserProfile userProfile)
    {
    	//set user profile to the one we got from the db
    	UserProfile.getInstance().update(userProfile);
    	
    	//update settings
    	SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this.getContext());
    	sharedPref.edit().putString("pref_zip", userProfile.getZip()).commit();
    	sharedPref.edit().putString("pref_family_size", String.valueOf(userProfile.getFamilySize())).commit();
    	
    	Set<String> stores = new TreeSet<String>();
    	String stores_string_from_db = userProfile.getStores();
    	String[] stores_array = stores_string_from_db.split("-");
    	
    	for (String store : stores_array)
    	{
    		stores.add(store);
    	}
    	
    	//update stores settings
    	sharedPref.edit().putStringSet("pref_stores", stores).commit();
    }
    
    /*
    protected void syncDirtyToServer(List<SharpList> dirtyList)
	    throws AuthenticationException, IOException, SharpCartException {
	for (SharpList sharpList : dirtyList) {
	    Log.d(TAG, "Dirty list: " + sharpList);

	    switch (sharpList.getStatus()) {
	    case StatusFlag.ADD:
		pushNewSharpList(sharpList);
		break;
	    case StatusFlag.MOD:
		throw new SharpCartException(
			"Todo title modification is not supported");
	    case StatusFlag.DELETE:
		pushDeleteSharpList(sharpList);
		break;
	    default:
		throw new RuntimeException("Invalid status: "
			+ sharpList.getStatus());
	    }
	}
    }

    private void pushNewSharpList(SharpList todo)
	    throws AuthenticationException, IOException, SharpCartException {
	SharpList serverTodo = SharpCartServiceImpl.createSharpList(todo
		.getName());
	mSharpListDAO.clearAdd(mContentResolver, todo.getId(), serverTodo);
    }

    private void pushDeleteSharpList(SharpList todo)
	    throws AuthenticationException, SharpCartException {
	SharpCartServiceImpl.deleteSharpList(todo.getId());
	mSharpListDAO.deleteSharpListForced(mContentResolver, todo.getId());
    }

    protected void syncRemoteDeleted(List<SharpList> remotesharpLists) {
	Log.d(TAG, "Syncing remote deleted lists...");

	List<SharpList> localClean = mSharpListDAO
		.getCleanSharpLists(mContentResolver);
	for (SharpList cleanSharpList : localClean) {

	    if (!remotesharpLists.contains(cleanSharpList)) {
		Log.d(TAG, "Todo with id " + cleanSharpList.getId()
			+ " has been deleted remotely.");
		mSharpListDAO.forcedDeleteSharpList(mContentResolver,
			cleanSharpList.getId());
	    }
	}
    }

    protected void syncFromServerToLocalStorage(List<SharpList> sharpLists) {
	for (SharpList sharpListFromServer : sharpLists) {
	    SharpList sharpListInDb = mSharpListDAO.isSharpListInDb(
		    mContentResolver, sharpListFromServer.getId());

	    if (sharpListInDb == null) {
		Log.d(TAG, "Adding new sharp list from server: "
			+ sharpListFromServer);

		mSharpListDAO.addNewSharpList(mContentResolver,
			sharpListFromServer, StatusFlag.CLEAN);

	    } else if (sharpListInDb.getStatus() == StatusFlag.CLEAN) {
		Log.d(TAG, "Modifying list from server: " + sharpListInDb);
		mSharpListDAO.modifySharpListFromServer(mContentResolver,
			sharpListFromServer);
	    }

	}
    }

    protected void syncRemoteStores(List<Store> stores) {

	// Since we have no more than 4 rows in our store table
	// We are going to just delete it every time we sync

	mStoreDAO.emptyStoresTable(mContentResolver);

	for (Store storesFromServer : stores) {
	    Store storeInDb = mStoreDAO.isStoreInDb(mContentResolver,
		    storesFromServer.getId());

	    if (storeInDb == null) {
		Log.d(TAG, "Adding new store from server: " + storesFromServer);

		mStoreDAO.addNewStore(mContentResolver, storesFromServer,
			StatusFlag.CLEAN);

	    } else if (storeInDb.getStatus() == StatusFlag.CLEAN) {
		Log.d(TAG, "Modifying list from server: " + storeInDb);
		mStoreDAO.modifyStoreFromServer(mContentResolver,
			storesFromServer);
	    }

	}
    }
	
	*/
    
    protected List<SharpList> fetchSharpLists(String username)
	    throws AuthenticationException, SharpCartException,
	    JsonParseException, IOException {
		final List<SharpList> list = SharpCartServiceImpl.fetchSharpLists(username);
	
		return list;
    }

    protected List<Store> fetchStores(String username)
	    throws AuthenticationException, SharpCartException,
	    JsonParseException, IOException {
		final List<Store> stores = SharpCartServiceImpl.fetchStores(username);
	
		return stores;
    }

    protected List<Sale> fetchSales(String username)
    	    throws AuthenticationException, SharpCartException,JsonParseException, IOException {
    		
    		final List<Sale> sales = SharpCartServiceImpl.fetchShoppingItemsOnSale(username);
    	
    		return sales;
        }
    
    protected List<ShoppingItem> fetchUnavailableItems(String username)
    	    throws AuthenticationException, SharpCartException,JsonParseException, IOException {
    		
    		final List<ShoppingItem> unavilableItems = SharpCartServiceImpl.fetchUnavailableItems(username);
    	
    		return unavilableItems;
        }
    
    protected List<ShoppingItem> fetchActiveSharpListItems(String username)
    	    throws AuthenticationException, SharpCartException,JsonParseException, IOException {
    		
    		final List<ShoppingItem> activeSharpListItems = SharpCartServiceImpl.fetchActiveSharpListItems(username);
    	
    		return activeSharpListItems;
        }
    
    protected UserProfile fetchUserProfile(String username)
    	    throws AuthenticationException, SharpCartException,JsonParseException, IOException {
    		
    		final UserProfile userProfile = SharpCartServiceImpl.fetchUserProfile(username);
    	
    		return userProfile;
        }
    
    private void handleException(String authtoken, Exception e,
	    SyncResult syncResult) {
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
