package com.sharpcart.android.utilities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.os.Bundle;

import com.sharpcart.android.authenticator.AuthenticatorActivity;
import com.sharpcart.android.provider.SharpCartContentProvider;

public class SharpCartUtilities {

    private static final SharpCartUtilities instance = new SharpCartUtilities();

    private SharpCartUtilities() {
    	
    }

    public static SharpCartUtilities getInstance() {
    	return instance;
    }
	
    /*
     * force syncadapter to sync information from server
     */
    public void syncFromServer(Account account)
    {
        // Pass the settings flags by inserting them in a bundle
        Bundle settingsBundle = new Bundle();
        settingsBundle.putBoolean(
                ContentResolver.SYNC_EXTRAS_MANUAL, true);
        settingsBundle.putBoolean(
                ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        /*
         * Request the sync for the default account, authority, and
         * manual sync settings
         */
        ContentResolver.requestSync(account, SharpCartContentProvider.AUTHORITY, settingsBundle);   	
    }
}
