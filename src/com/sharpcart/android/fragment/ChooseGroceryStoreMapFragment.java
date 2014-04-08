package com.sharpcart.android.fragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.auth.AuthenticationException;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.sharpcart.android.MainActivity;
import com.sharpcart.android.R;
import com.sharpcart.android.adapter.ChooseGroceryStoreAdapter;
import com.sharpcart.android.api.SharpCartServiceImpl;
import com.sharpcart.android.api.SharpCartUrlFactory;
import com.sharpcart.android.dao.StoreDAO;
import com.sharpcart.android.exception.SharpCartException;
import com.sharpcart.android.model.Store;
import com.sharpcart.android.model.UserProfile;
import com.sharpcart.android.net.SimpleHttpHelper;
import com.sharpcart.android.utilities.SharpCartUtilities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class ChooseGroceryStoreMapFragment extends FragmentActivity {
	/*
    * Note that this may be null if the Google Play services APK is not available.
    */
   private GoogleMap mMap;
   private ProgressDialog pd;
   private ChooseGroceryStoreAdapter chooseGroceryStoreAdapter;
   private ListView storesServingZipCodeListView;
   private List<Store> stores = new ArrayList<Store>();
   
   private static final String TAG = ChooseGroceryStoreMapFragment.class.getSimpleName();
   
   @Override
   protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.choose_grocery_store_map_fragment);
 	   pd = new ProgressDialog(this);
 	   storesServingZipCodeListView = (ListView) findViewById(R.id.storesServingZipCodeList);
 	   
 	   Button selectStoresButton = (Button) findViewById(R.id.chooseStoreButton);
 	   
 	   selectStoresButton.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			//delete all stores in the database
			StoreDAO.getInstance().clear(v.getContext().getContentResolver());
			
			for (Store store : chooseGroceryStoreAdapter.getSelectedStores())
			{
				//add new stores to database
				StoreDAO.getInstance().addStore(v.getContext().getContentResolver(), store);
				
				Toast.makeText(v.getContext(),"Stores added",Toast.LENGTH_SHORT).show();
				
				finish();
			}
		}
	});
 	   
       setUpMapIfNeeded();
   }

   @Override
   protected void onResume() {
       super.onResume();
       setUpMapIfNeeded();
   }

   /**
    * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
    * installed) and the map has not already been instantiated.. This will ensure that we only ever
    * call {@link #setUpMap()} once when {@link #mMap} is not null.
    * <p>
    * If it isn't installed {@link SupportMapFragment} (and
    * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
    * install/update the Google Play services APK on their device.
    * <p>
    * A user can return to this FragmentActivity after following the prompt and correctly
    * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
    * have been completely destroyed during this process (it is likely that it would only be
    * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
    * method in {@link #onResume()} to guarantee that it will be called.
    */
   private void setUpMapIfNeeded() {
       // Do a null check to confirm that we have not already instantiated the map.
       if (mMap == null) {
           // Try to obtain the map from the SupportMapFragment.
           mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                   .getMap();
           // Check if we were successful in obtaining the map.
           if (mMap != null) {
               setUpMap();
           }
       }
   }

   /**
    * This is where we can add markers or lines, add listeners or move the camera. In this case, we
    * just add a marker near Africa.
    * <p>
    * This should only be called once and when we are sure that {@link #mMap} is not null.
    */
   private void setUpMap() {
	 
	   //generates grocery store markers based on user zip code and store information in the database
	   GetStoresTask getStoresTask = new GetStoresTask();
	   getStoresTask.execute(this);
   }
   
   
   private class GetStoresTask extends AsyncTask<Activity, Integer, Activity> {
   	
     @Override
     protected void onPreExecute() {
       
       //Show progress spinner
       pd.setMessage("Please wait...");
       pd.setCancelable(false);
       pd.show();
     }

     @Override
     protected Activity doInBackground(final Activity...params) {

     	if (SharpCartUtilities.getInstance().hasActiveInternetConnection(params[0]))
     	{
	 		try {
				stores = SharpCartServiceImpl.fetchStoresForZipCode(UserProfile.getInstance().getZip());
			} catch (AuthenticationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JsonParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SharpCartException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
 			      
     	} else
     	{
     		Toast.makeText(params[0],"No Internet Connection",Toast.LENGTH_SHORT).show();
     		cancel(true);
     	}
     	
 		return params[0];
     }

     @Override
     protected void onProgressUpdate(final Integer... percent) {

     }

     @Override
     protected void onCancelled() {
       pd.dismiss();
     }

	@Override
     protected void onPostExecute(final Activity params) {
      	pd.dismiss();
      	
      	final List<LatLng> storeMarkers = new ArrayList<LatLng>();
      	
      	if (stores.size()>0)
      	{
      		for (Store store : stores)
	  	   {
	    	   LatLng storePosition = new LatLng(store.getLat(), store.getLng());
	    	   storeMarkers.add(storePosition);
	  		   mMap.addMarker(new MarkerOptions().position(storePosition).title(store.getName()));
	  	   }
	  	  
	    	 
	     // Pan to see all markers in view.
	     // Cannot zoom to bounds until the map has a size.
	     final View mapView = getSupportFragmentManager().findFragmentById(R.id.map).getView();
	     if (mapView.getViewTreeObserver().isAlive()) {
	         mapView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
	             @SuppressWarnings("deprecation") // We use the new method when supported
	             @SuppressLint("NewApi") // We check which build version we are using.
	             @Override
	             public void onGlobalLayout() {
	                 LatLngBounds bounds = new LatLngBounds.Builder().include(storeMarkers.get(0)).build();
	                 
	                 for (LatLng storePosition : storeMarkers)
	                 {
	                	 bounds = bounds.including(storePosition);
	                 }
	                 
	                 if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
	                   mapView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
	                 } else {
	                   mapView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
	                 }
	                 mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
	             }
	         });
	     }
	    
	    	chooseGroceryStoreAdapter = new ChooseGroceryStoreAdapter(params, R.layout.store, stores);
	    	storesServingZipCodeListView.setAdapter(chooseGroceryStoreAdapter);
	     } else //there are no stores in the user zip code
	     {
	    	 Toast.makeText(params,"Currently we do not support any stores in "+UserProfile.getInstance().getZip(),Toast.LENGTH_SHORT).show();
	    	 params.finish();
	     }
     }
   }
}
