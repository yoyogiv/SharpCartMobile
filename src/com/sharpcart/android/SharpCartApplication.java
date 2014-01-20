package com.sharpcart.android;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;

import com.sharpcart.android.authenticator.AuthenticatorActivity;

public class SharpCartApplication extends Application {
		
    public Account getCurrentAccount() {
	final AccountManager accountManager = AccountManager.get(this);
	final Account[] accounts = accountManager
		.getAccountsByType(AuthenticatorActivity.PARAM_ACCOUNT_TYPE);

	if (accounts.length > 0) {
	    return accounts[0];
	} else {
	    final Intent intent = new Intent(this, MainActivity.class);
	    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    startActivity(intent);
	    return null;
	}
    }
    
}
