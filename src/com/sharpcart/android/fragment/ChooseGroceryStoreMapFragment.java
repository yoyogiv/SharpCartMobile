package com.sharpcart.android.fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sharpcart.android.R;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;

public class ChooseGroceryStoreMapFragment extends FragmentActivity {
	/*
    * Note that this may be null if the Google Play services APK is not available.
    */
   private GoogleMap mMap;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.choose_grocery_store_map_fragment);
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
	   final LatLng HOME = new LatLng(30.511627,-97.727468);
	   final LatLng HEB = new LatLng(30.500538, -97.722161);
	   
       mMap.addMarker(new MarkerOptions().position(HOME).title("Home"));
       mMap.addMarker(new MarkerOptions().position(HEB).title("HEB"));
       
       // Pan to see all markers in view.
       // Cannot zoom to bounds until the map has a size.
       final View mapView = getSupportFragmentManager().findFragmentById(R.id.map).getView();
       if (mapView.getViewTreeObserver().isAlive()) {
           mapView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
               @SuppressWarnings("deprecation") // We use the new method when supported
               @SuppressLint("NewApi") // We check which build version we are using.
               @Override
               public void onGlobalLayout() {
                   LatLngBounds bounds = new LatLngBounds.Builder()
                           .include(HOME)
                           .include(HEB)
                           .build();
                   if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                     mapView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                   } else {
                     mapView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                   }
                   mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
               }
           });
       }
   }
}
