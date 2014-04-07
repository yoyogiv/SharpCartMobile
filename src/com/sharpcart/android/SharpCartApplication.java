package com.sharpcart.android;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.StrictMode;

import com.sharpcart.android.authenticator.AuthenticatorActivity;

public class SharpCartApplication extends Application {
	
	final static boolean DEVELOPER_MODE = false;
	private static Context context;
	
	@Override
	 public void onCreate() {
	     if (DEVELOPER_MODE) {
	         StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
	                 .detectDiskReads()
	                 .detectDiskWrites()
	                 .detectNetwork()   // or .detectAll() for all detectable problems
	                 .penaltyLog()
	                 .build());
	         StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
	                 .detectLeakedSqlLiteObjects()
	                 .detectLeakedClosableObjects()
	                 .penaltyLog()
	                 .penaltyDeath()
	                 .build());
	     }
	     
	     context = getApplicationContext();
	     
	     super.onCreate();
	 }
	 
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
    
    public static Context getAppContext()
    {
    	return context;
    }
}
