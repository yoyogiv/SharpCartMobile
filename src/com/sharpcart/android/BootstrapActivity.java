package com.sharpcart.android;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.android.wizardpager.SharpCartLoginActivity;
import com.sharpcart.android.authenticator.AuthenticatorActivity;
import com.sharpcart.android.model.UserProfile;

public class BootstrapActivity extends Activity {
    private static final String TAG = BootstrapActivity.class
	    .getCanonicalName();
    private static final int NEW_ACCOUNT = 0;
    private static final int EXISTING_ACCOUNT = 1;
    private AccountManager mAccountManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.bootstrap);
	
	mAccountManager = AccountManager.get(this);
	final Account[] accounts = mAccountManager
		.getAccountsByType(AuthenticatorActivity.PARAM_ACCOUNT_TYPE);

	if (accounts.length == 0) {
	    // There are no accounts! We need to create one.
	    Log.d(TAG, "No accounts found. Starting login...");
	    
	    /*
	    final Intent intent = new Intent(this, AuthenticatorActivity.class);
	    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
	    startActivityForResult(intent, NEW_ACCOUNT);
	    */
	    
	    final Intent intent = new Intent(this, SharpCartLoginActivity.class);
	    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
	    startActivityForResult(intent, NEW_ACCOUNT);
	    
	} else {
	    // For now we assume that there's only one account.
	    final String password = mAccountManager.getPassword(accounts[0]);
	    Log.d(TAG, "Using account with name " + accounts[0].name);
	    if (password == null) {
		Log.d(TAG, "The password is empty, launching login");
		final Intent intent = new Intent(this,
			AuthenticatorActivity.class);
		intent.putExtra(AuthenticatorActivity.PARAM_USER,
			accounts[0].name);
		startActivityForResult(intent, EXISTING_ACCOUNT);
	    } else {
		Log.d(TAG, "User and password found, no need for manual login");
		// The user is already logged in. Go ahead!
		
		startActivity(new Intent(this, MainActivity.class));
		finish();
	    }
	}
	
		//initialize UserProfile using shared preferences
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		UserProfile.getInstance().setZip(sharedPref.getString("pref_zip", "78681"));
		UserProfile.getInstance().setStores(sharedPref.getString("pref_stores_entries", "1-3-4"));
		UserProfile.getInstance().setFamilySize(sharedPref.getString("pref_family_size", "3"));
	
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	
    	super.onActivityResult(requestCode, resultCode, data);

	if (mAccountManager.getAccountsByType(AuthenticatorActivity.PARAM_ACCOUNT_TYPE).length > 0) 
	{		
	    final Intent i = new Intent(this, MainActivity.class);
	    startActivity(i);
	    finish();
	} else {
	    finish();
	}
    }
 
}
