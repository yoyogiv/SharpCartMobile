package com.sharpcart.android.utilities;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import android.accounts.Account;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.sharpcart.android.BootstrapActivity;
import com.sharpcart.android.R;
import com.sharpcart.android.model.ImageResource;
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
		//SHOPPING_ITEM_UNIT[5]= "OZ";
		SHOPPING_ITEM_UNIT[5]= "Package"; //it seems to make more sense to use "package" and not "oz" for items that use "oz" in the db when presenting it to the user
		SHOPPING_ITEM_UNIT[14]= "-";
		
		//init shopping item category list
		SHOPPING_ITEM_CATEGORY = new String[25];
		
		SHOPPING_ITEM_CATEGORY[3] = "Produce";
		SHOPPING_ITEM_CATEGORY[4] = "Snacks";
		SHOPPING_ITEM_CATEGORY[5] = "Meat And Fish";
		SHOPPING_ITEM_CATEGORY[6] = "Dairy";
		SHOPPING_ITEM_CATEGORY[7] = "Bakery";
		SHOPPING_ITEM_CATEGORY[8] = "Baby Supplies";
		SHOPPING_ITEM_CATEGORY[9] = "Pet Supplies";
		SHOPPING_ITEM_CATEGORY[10] = "Canned Food";
		SHOPPING_ITEM_CATEGORY[11] = "Beverages";
		SHOPPING_ITEM_CATEGORY[12] = "Baking";
		SHOPPING_ITEM_CATEGORY[14] = "Personal Care";
		SHOPPING_ITEM_CATEGORY[15] = "Paper Goods";
		SHOPPING_ITEM_CATEGORY[16] = "Grains And Pasta";
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
    public void syncFromServer(final Account account)
    {   	
        // Pass the settings flags by inserting them in a bundle
        final Bundle settingsBundle = new Bundle();
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        /*
         * Request the sync for the default account, authority, and
         * manual sync settings
         */
        ContentResolver.cancelSync(account, SharpCartContentProvider.AUTHORITY);
        ContentResolver.requestSync(account, SharpCartContentProvider.AUTHORITY, settingsBundle);
         
    }
    
    private boolean isNetworkAvailable(final Context context) {
        final ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }
    
    public boolean hasActiveInternetConnection(final Context context) {
    	HttpURLConnection urlc = null;
        if (isNetworkAvailable(context)) {
            try {               	
                urlc = (HttpURLConnection) (new URL("http://www.google.com").openConnection());
                urlc.setRequestProperty("User-Agent", "Test");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(1500); 
                urlc.connect();
                return (urlc.getResponseCode() == 200);
            } catch (final IOException e) {
                Log.e(TAG, "Error checking internet connection", e);
            } finally
            {
            	urlc.disconnect();
            }
        } else {
            Log.d(TAG, "No network available!");
        }
        return false;
    }

	public ArrayList<ImageResource> getStoreImages() {
		return storeImages;
	}

	public void setStoreImages(final ArrayList<ImageResource> storeImages) {
		this.storeImages = storeImages;
	}
  
	public String getUnitName(final int unitId)
	{
		return SHOPPING_ITEM_UNIT[unitId];
	}
	
	public String getCategoryName(final int categoryId)
	{
		return SHOPPING_ITEM_CATEGORY[categoryId];
	}
	
	public String getStoreName(final int storeId)
	{
		return STORES[storeId];
	}
	
	public int sendUserReminderNotificationToCreateGroceryList(final Context context)
	{
		final int mId = 1;
		
		final NotificationCompat.Builder mBuilder =
		        new NotificationCompat.Builder(context)
		        .setSmallIcon(R.drawable.ic_launcher)
		        .setContentTitle("Grocery List")
		        .setContentText("Dont forget to create a grocery list");
		
		// Creates an explicit intent for an Activity in your app
		final Intent resultIntent = new Intent(context, BootstrapActivity.class);

		// The stack builder object will contain an artificial back stack for the
		// started Activity.
		// This ensures that navigating backward from the Activity leads out of
		// your application to the Home screen.
		final TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(BootstrapActivity.class);
		
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		final PendingIntent resultPendingIntent =
		        stackBuilder.getPendingIntent(
		            0,
		            PendingIntent.FLAG_UPDATE_CURRENT
		        );
		mBuilder.setContentIntent(resultPendingIntent);
		final NotificationManager mNotificationManager =
		    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		
		// mId allows you to update the notification later on.
		mNotificationManager.notify(mId, mBuilder.build());
		
		return mId;
	}
}
