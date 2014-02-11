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
    private final String[] SHOPPING_ITEM_UNIT;
    private final String[] SHOPPING_ITEM_CATEGORY;
    private final String[] STORES;
    
    private SharpCartUtilities() {
    	storeImages = new ArrayList<ImageResource>();
    	
    	storeImages.add(new ImageResource(com.sharpcart.android.R.drawable.costco,"costco"));
    	storeImages.add(new ImageResource(com.sharpcart.android.R.drawable.heb,"heb"));
    	storeImages.add(new ImageResource(com.sharpcart.android.R.drawable.walmart,"walmart"));
    	storeImages.add(new ImageResource(com.sharpcart.android.R.drawable.sprouts,"sprouts"));
    	storeImages.add(new ImageResource(com.sharpcart.android.R.drawable.samsclub,"sams club"));
    	
    	//init stores list
    	STORES = new String[6];
    	
    	STORES[1] = "HEB";
    	STORES[2] = "Walmart";
    	STORES[3] = "Costco";
    	STORES[4] = "Sprouts";
    	STORES[5] = "Sams Club";
    	
		//init shopping item unit list
		SHOPPING_ITEM_UNIT = new String[15];
		
		SHOPPING_ITEM_UNIT[9]= "Bag";
		SHOPPING_ITEM_UNIT[7]= "Can";
		SHOPPING_ITEM_UNIT[13]= "Feet";
		SHOPPING_ITEM_UNIT[12]= "Gallon";
		SHOPPING_ITEM_UNIT[6]= "Items";
		SHOPPING_ITEM_UNIT[4]= "LBS";
		SHOPPING_ITEM_UNIT[8]= "Liter";
		SHOPPING_ITEM_UNIT[5]= "OZ";
		SHOPPING_ITEM_UNIT[14]= "-";
		
		//init shopping item category list
		SHOPPING_ITEM_CATEGORY = new String[25];
		
		SHOPPING_ITEM_CATEGORY[3] = "Produce";
		SHOPPING_ITEM_CATEGORY[4] = "Snacks";
		SHOPPING_ITEM_CATEGORY[5] = "Mean and Fish";
		SHOPPING_ITEM_CATEGORY[6] = "Dairy";
		SHOPPING_ITEM_CATEGORY[7] = "Bakery";
		SHOPPING_ITEM_CATEGORY[8] = "Baby Supplies";
		SHOPPING_ITEM_CATEGORY[9] = "Pet Supplies";
		SHOPPING_ITEM_CATEGORY[10] = "Canned Food";
		SHOPPING_ITEM_CATEGORY[11] = "Beverages";
		SHOPPING_ITEM_CATEGORY[12] = "Baking";
		SHOPPING_ITEM_CATEGORY[14] = "Personal Care";
		SHOPPING_ITEM_CATEGORY[15] = "Paper Goods";
		SHOPPING_ITEM_CATEGORY[16] = "Grains and Pasta";
		SHOPPING_ITEM_CATEGORY[18] = "Frozen";
		SHOPPING_ITEM_CATEGORY[19] = "Cleaning Supplies";
		SHOPPING_ITEM_CATEGORY[20] = "Condiments";
		SHOPPING_ITEM_CATEGORY[21] = "Breakfast";
		SHOPPING_ITEM_CATEGORY[22] = "Organic";
		SHOPPING_ITEM_CATEGORY[23] = "Extra";
		
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
  
	public String getUnitName(int unitId)
	{
		return SHOPPING_ITEM_UNIT[unitId];
	}
	
	public String getCategoryName(int categoryId)
	{
		return SHOPPING_ITEM_CATEGORY[categoryId];
	}
	
	public String getStoreName(int storeId)
	{
		return STORES[storeId];
	}
}
