package com.sharpcart.android;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Application;
import android.content.Intent;

import com.sharpcart.android.authenticator.AuthenticatorActivity;

public class SharpCartApplication extends Application {

    public Account getCurrentAccount() {
	AccountManager accountManager = AccountManager.get(this);
	Account[] accounts = accountManager
		.getAccountsByType(AuthenticatorActivity.PARAM_ACCOUNT_TYPE);

	if (accounts.length > 0) {
	    return accounts[0];
	} else {
	    Intent intent = new Intent(this, MainActivity.class);
	    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    startActivity(intent);
	    return null;
	}
    }
}
