package com.sharpcart.android.service;

import com.sharpcart.android.authenticator.AuthenticatorActivity;
import com.sharpcart.android.provider.SharpCartContentProvider;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class SharpCartSyncServiceAlarmReciever extends BroadcastReceiver {

	@Override
	/*
	 * (non-Javadoc)
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
	 * Once we receive our alarm we will initiate a manual sync by our sync adapter
	 */
	public void onReceive(final Context context, final Intent intent) {
        // Pass the settings flags by inserting them in a bundle
        final Bundle settingsBundle = new Bundle();
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        /*
         * Request the sync for the default account, authority, and
         * manual sync settings
         */
        final AccountManager mAccountManager = AccountManager.get(context);
	    final Account[] accounts = mAccountManager.getAccountsByType(AuthenticatorActivity.PARAM_ACCOUNT_TYPE);
        ContentResolver.requestSync(accounts[0], SharpCartContentProvider.AUTHORITY, settingsBundle);   
	}

}
