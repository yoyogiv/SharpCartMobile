package com.sharpcart.android;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.os.Bundle;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.sharpcart.android.authenticator.AuthenticatorActivity;
import com.sharpcart.android.fragment.MainScreen;
import com.sharpcart.android.fragment.MainSharpList;
import com.sharpcart.android.provider.SharpCartContentProvider;
import com.sharpcart.android.utilities.SharpCartUtilities;

public class MainActivity extends FragmentActivity implements MainScreen.OnShoppingItemSelectedListener{

	private SlidingPaneLayout mPane;
	private AccountManager mAccountManager;
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);
		
		mPane = (SlidingPaneLayout) findViewById(R.id.sliding_pane);
		//mPane.setPanelSlideListener(new PaneListener());
		
		mPane.openPane();
	    
	    getSupportFragmentManager().beginTransaction().add(R.id.main_screen_fragment, new MainScreen(), "main screen").commit();

	    getSupportFragmentManager().beginTransaction().add(R.id.main_sharp_list_fragment, new MainSharpList(), "sharp list").commit();
	    
	    mAccountManager = AccountManager.get(this.getBaseContext());
	}
	
	/*
	private class PaneListener implements SlidingPaneLayout.PanelSlideListener {

	    @Override
	    public void onPanelClosed(View view) {
	        //System.out.println("Panel closed");
	     }

	    @Override
	    public void onPanelOpened(View view) {
	       //System.out.println("Panel opened");    
	    }

	    @Override
	    public void onPanelSlide(View view, float arg1) {
	       // System.out.println("Panel sliding");
	    }
	}
	*/
	
	
	//If the user clicked on a shopping item, update the main sharp list fragment
    
	@Override
	public void onShoppingItemSelected() {
    	((MainSharpList)getSupportFragmentManager().findFragmentById(R.id.main_sharp_list_fragment)).updateSharpList();
    }
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
     * Sync information from the server once the user clicks the sync from
     * server menu
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.refresh:
			Account[] accounts = mAccountManager.getAccountsByType(AuthenticatorActivity.PARAM_ACCOUNT_TYPE);
		    SharpCartUtilities.getInstance().syncFromServer(accounts[0]);
		    return true;
		default:
		    return super.onOptionsItemSelected(item);
		}
    }
    

    
}
