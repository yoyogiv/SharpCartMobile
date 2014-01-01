package com.sharpcart.android.service;

import java.io.IOException;
import java.util.List;

import org.apache.http.ParseException;
import org.apache.http.auth.AuthenticationException;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.JsonParseException;
import com.sharpcart.android.api.SharpCartServiceImpl;
import com.sharpcart.android.authenticator.AuthenticatorActivity;

import com.sharpcart.android.exception.SharpCartException;


public class SharpCartSyncAdapter extends AbstractThreadedSyncAdapter {

    private static final String TAG = SharpCartSyncAdapter.class
	    .getCanonicalName();
    private final ContentResolver mContentResolver;
    private AccountManager mAccountManager;
    private final static SharpListDAO mSharpListDAO = SharpListDAO
	    .getInstance();

    public SharpCartSyncAdapter(Context context, boolean autoInitialize) {
	super(context, autoInitialize);
	mContentResolver = context.getContentResolver();
	mAccountManager = AccountManager.get(context);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
	    ContentProviderClient provider, SyncResult syncResult) {

	String authtoken = null;
	try {
	    authtoken = mAccountManager.blockingGetAuthToken(account,
		    AuthenticatorActivity.PARAM_AUTHTOKEN_TYPE, true);

	    Account[] accounts = mAccountManager
		    .getAccountsByType(AuthenticatorActivity.PARAM_ACCOUNT_TYPE);

	    List<SharpList> sharpLists = fetchSharpLists(accounts[0].name);

	    List<Store> stores = fetchStores(accounts[0].name);

	    // syncRemoteDeleted(sharpLists);

	    syncRemoteStores(stores);

	    syncFromServerToLocalStorage(sharpLists);

	    // syncDirtyToServer(mSharpListDAO.getDirtyList(mContentResolver));

	} catch (Exception e) {
	    handleException(authtoken, e, syncResult);
	}

    }

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

    protected List<SharpList> fetchSharpLists(String username)
	    throws AuthenticationException, SharpCartException,
	    JsonParseException, IOException {
	List<SharpList> list = SharpCartServiceImpl.fetchSharpLists(username);

	return list;
    }

    protected List<Store> fetchStores(String username)
	    throws AuthenticationException, SharpCartException,
	    JsonParseException, IOException {
	List<Store> stores = SharpCartServiceImpl.fetchStores(username);

	return stores;
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
