package com.sharpcart.android;

import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.content.res.Configuration;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import com.sharpcart.android.adapter.MainSharpListItemAdapter;
import com.sharpcart.android.fragment.MainScreen;
import com.sharpcart.android.fragment.MainSharpList;

public class MainActivity extends FragmentActivity implements MainScreen.OnShoppingItemSelectedListener{

	private SlidingPaneLayout mPane;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);
		
		mPane = (SlidingPaneLayout) findViewById(R.id.sliding_pane);
		mPane.setPanelSlideListener(new PaneListener());
		
	    mPane.openPane();
	 
	    getSupportFragmentManager().beginTransaction()
	        .add(R.id.main_screen_fragment, new MainScreen(), "main screen").commit();
	    getSupportFragmentManager().beginTransaction()
	        .add(R.id.main_sharp_list_fragment, new MainSharpList(), "sharp list").commit();
	}
	
	private class PaneListener implements SlidingPaneLayout.PanelSlideListener {

	    @Override
	    public void onPanelClosed(View view) {
	        System.out.println("Panel closed");
	     }

	    @Override
	    public void onPanelOpened(View view) {
	       System.out.println("Panel opened");    
	    }

	    @Override
	    public void onPanelSlide(View view, float arg1) {
	        System.out.println("Panel sliding");
	    }
	}
	
	//If the user clicked on a shopping item, update the main sharp list fragment
    public void onShoppingItemSelected() {
    	((MainSharpList)getSupportFragmentManager().findFragmentById(R.id.main_sharp_list_fragment)).updateSharpList();
    }
}
