package com.sharpcart.android;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import com.sharpcart.android.authenticator.AuthenticatorActivity;
import com.sharpcart.android.dao.StoreDAO;
import com.sharpcart.android.model.MainSharpList;
import com.sharpcart.android.model.UserProfile;
import com.sharpcart.android.utilities.SharpCartUtilities;

public class BootstrapActivity extends Activity {
    private static final String TAG = BootstrapActivity.class.getCanonicalName();
    
    private static final int NEW_ACCOUNT = 0;
    private static final int EXISTING_ACCOUNT = 1;
    private AccountManager mAccountManager;
    private Context mContext;
    
    private final int SPLASH_DISPLAY_LENGHT = 3000;
    
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bootstrap);
		
		mContext = getBaseContext();
		
		mAccountManager = AccountManager.get(this);
		
		final Account[] accounts = mAccountManager
			.getAccountsByType(AuthenticatorActivity.PARAM_ACCOUNT_TYPE);
	
		//check the timestamp for our SharpCart db file, if it is older than the one in our asset, than we need to replace it
			final String destDir = "/data/data/" + getPackageName() + "/databases/";
 			
			final String destPath = destDir + "SharpCart";
			final File f = new File(destPath);
			
			final boolean exists = f.exists();
			f.length();
			
			if (exists && (f.lastModified()<1396993887490L)) 
			{
				//---make sure directory exists---
				final File directory = new File(destDir);
				directory.mkdirs();
				
				//---copy the db from the assets folder into
				// the databases folder---
				try 
				{
					CopyDB(getBaseContext().getAssets().open("SharpCart"),
					new FileOutputStream(destPath));
				} catch (final FileNotFoundException e) {
					e.printStackTrace();
				} catch (final IOException e) {
					e.printStackTrace();
				}
			}
			
		if (accounts.length == 0) {
		    // There are no accounts! We need to create one.
		    Log.d(TAG, "No accounts found. Starting login...");
		    
		    //final Intent intent = new Intent(this, SharpCartLoginActivity.class);
		    final Intent intent = new Intent(this, AuthenticatorActivity.class);
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
				UserProfile.getInstance().setUserName(accounts[0].name);
				UserProfile.getInstance().setPassword(password);
				
		        /* New Handler to start the Menu-Activity 
		         * and close this Splash-Screen after some seconds.*/
		        new Handler().postDelayed(new Runnable(){
		            @Override
		            public void run() {
		                /* Create an Intent that will start the MainActivity. */
		                final Intent mainIntent = new Intent(mContext,MainActivity.class);
		                startActivity(mainIntent);
		                finish();
		            }
		        }, SPLASH_DISPLAY_LENGHT);
		        
		    }
		}
	
		//initialize UserProfile using shared preferences
		final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		UserProfile.getInstance().setZip(sharedPref.getString("pref_zip", "78681"));
		//UserProfile.getInstance().setStores(sharedPref.getString("pref_stores_entries", "1-3-4"));
		UserProfile.getInstance().setFamilySize(sharedPref.getString("pref_family_size", "3"));
		UserProfile.getInstance().setLastUpdated(sharedPref.getLong("user_profile_last_updated", 0));
		UserProfile.getInstance().setStores(StoreDAO.getInstance().getStore(getContentResolver(), ""));
		
		//init MainSharpList last updated
		MainSharpList.getInstance().setLastUpdated(sharedPref.getLong("sharp_list_last_updated", 0));
		
		//initiate a sync
		if (accounts.length!=0)
			SharpCartUtilities.getInstance().syncFromServer(accounts[0]);
		
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
	
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
    
	private void CopyDB(final InputStream inputStream, final OutputStream outputStream)throws IOException {
		//---copy 1K bytes at a time---
		final byte[] buffer = new byte[1024];
		int length;
		while ((length = inputStream.read(buffer)) > 0) 
		{
			outputStream.write(buffer, 0, length);
		}
		
		inputStream.close();
		outputStream.close();
	}
 
}
