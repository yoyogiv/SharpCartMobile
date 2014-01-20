package com.sharpcart.android.utilities;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import android.accounts.Account;
import android.content.ContentResolver;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.sharpcart.android.api.SharpCartUrlFactory;
import com.sharpcart.android.exception.SharpCartException;
import com.sharpcart.android.model.ImageResource;
import com.sharpcart.android.model.MainSharpList;
import com.sharpcart.android.net.HttpHelper;
import com.sharpcart.android.provider.SharpCartContentProvider;

public class SharpCartUtilities {

	private static final String TAG = SharpCartUtilities.class.getSimpleName();
	
    private static final SharpCartUtilities instance = new SharpCartUtilities();
    
    private ArrayList<ImageResource> storeImages;
    
    private SharpCartUtilities() {
    	storeImages = new ArrayList<ImageResource>();
    	
    	storeImages.add(new ImageResource(com.sharpcart.android.R.drawable.costco,"costco"));
    	storeImages.add(new ImageResource(com.sharpcart.android.R.drawable.heb,"heb"));
    	storeImages.add(new ImageResource(com.sharpcart.android.R.drawable.walmart,"walmart"));
    	storeImages.add(new ImageResource(com.sharpcart.android.R.drawable.sprouts,"sprouts"));
    	storeImages.add(new ImageResource(com.sharpcart.android.R.drawable.samsclub,"sams club"));
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
        final Bundle settingsBundle = new Bundle();
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        /*
         * Request the sync for the default account, authority, and
         * manual sync settings
         */
        ContentResolver.requestSync(account, SharpCartContentProvider.AUTHORITY, settingsBundle);
         
    }
    
    private boolean isNetworkAvailable(Context context) {
        final ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }
    
    public boolean hasActiveInternetConnection(Context context) {
        if (isNetworkAvailable(context)) {
            try {               	
                final HttpURLConnection urlc = (HttpURLConnection) (new URL("http://www.google.com").openConnection());
                urlc.setRequestProperty("User-Agent", "Test");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(1500); 
                urlc.connect();
                return (urlc.getResponseCode() == 200);
            } catch (final IOException e) {
                Log.e(TAG, "Error checking internet connection", e);
            }
        } else {
            Log.d(TAG, "No network available!");
        }
        return false;
    }

	public ArrayList<ImageResource> getStoreImages() {
		return storeImages;
	}

	public void setStoreImages(ArrayList<ImageResource> storeImages) {
		this.storeImages = storeImages;
	}
  
}
