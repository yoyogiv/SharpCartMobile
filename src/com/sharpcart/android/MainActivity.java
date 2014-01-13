package com.sharpcart.android;

import java.util.ArrayList;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.sharpcart.android.authenticator.AuthenticatorActivity;
import com.sharpcart.android.dao.MainSharpListDAO;
import com.sharpcart.android.fragment.MainScreenFragment;
import com.sharpcart.android.fragment.MainSharpListFragment;
import com.sharpcart.android.fragment.OptimizedSharpListFragment;
import com.sharpcart.android.fragment.OptimizationTaskFragment;
import com.sharpcart.android.fragment.EmailSharpListTaskFragment;
import com.sharpcart.android.fragment.EmailSharpListDialogFragment.EmailSharpListDialogFragmentListener;
import com.sharpcart.android.model.MainSharpList;
import com.sharpcart.android.model.Store;
import com.sharpcart.android.utilities.SharpCartUtilities;

public class MainActivity extends FragmentActivity implements OptimizationTaskFragment.TaskCallbacks,
MainScreenFragment.OnShoppingItemSelectedListener, EmailSharpListDialogFragmentListener, EmailSharpListTaskFragment.TaskCallbacks {

	private SlidingPaneLayout mPane;
	private AccountManager mAccountManager;
	private ProgressDialog pd;
	private Context mContext;
	private MainScreenFragment mainScreenFragment;
	private MainSharpListFragment mainSharpListFragment;
	private OptimizedSharpListFragment optimizedSharpListFragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);
		
		mContext = this.getApplicationContext();
		
		mPane = (SlidingPaneLayout) findViewById(R.id.sliding_pane);
		//mPane.setPanelSlideListener(new PaneListener());
		
		mPane.openPane();
	    
		//Start fragments
		mainScreenFragment = new MainScreenFragment();
		mainSharpListFragment = new MainSharpListFragment();
		optimizedSharpListFragment = new OptimizedSharpListFragment();
		
	    getSupportFragmentManager().beginTransaction().add(R.id.main_screen_fragment, mainScreenFragment, "main screen").commit();

	    getSupportFragmentManager().beginTransaction().add(R.id.main_sharp_list_fragment, mainSharpListFragment, "sharp list").commit();
	    
	    mAccountManager = AccountManager.get(getBaseContext());
	    
	    //Load items to MainSharpList object
	    MainSharpList.getInstance().setUserName(mAccountManager.getAccounts()[0].name);
	    MainSharpList.getInstance().setMainSharpList(
	    		MainSharpListDAO.getInstance().getMainSharpListItemsWithSelection(getContentResolver(), null));
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
    	((MainSharpListFragment)getSupportFragmentManager().findFragmentById(R.id.main_sharp_list_fragment)).updateSharpList();
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

	@Override
	public void onPreExecute() {
		
	}

	@Override
	public void onProgressUpdate(int percent) {
		
	}

	@Override
	public void onCancelled() {
		Toast.makeText(mContext,"No Internet Connection",Toast.LENGTH_SHORT).show();
	}

	@Override
	/*
	 * (non-Javadoc)
	 * @see com.sharpcart.android.fragment.TaskFragment.TaskCallbacks#onPostExecute()
	 * After the optimize sync task has finished we have a list of stores with items/prices and 
	 * we can start our optimizedList fragment
	 */
	public void onPostExecute(ArrayList<Store> optimizedStores) {
		
		//set the optimized sharp list fragment stores
		optimizedSharpListFragment.setOptimizedStores(optimizedStores);
		
		final FragmentTransaction ft = getSupportFragmentManager().beginTransaction(); 
		ft.addToBackStack(null);
		
		//Check if the fragment is already running
		if (getSupportFragmentManager().findFragmentByTag("optimizedSharpListFragment")==null)
		{
			ft.replace(R.id.main_screen_fragment, optimizedSharpListFragment, "optimizedSharpListFragment");
			ft.commit();
		} else //refresh the fragment
		{
			/*
			ft.remove(optimizedSharpListFragment);
			ft.commit();
			*/
			optimizedSharpListFragment.refresh();
		}
		
	}
	
	@Override
	public void onFinishEditDialog(String sharpListName,String email) {
		
		if ((sharpListName.length()!=0)&&(email.length()!=0))
		{
			MainSharpList.getInstance().setListTitle(sharpListName);
			MainSharpList.getInstance().setEmail(email);
			
			mainSharpListFragment.emailSharpList();
	
		} else
			Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onPreSharpListEmailSent() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPostSharpListEmailSent(String response) {
		if (response.equalsIgnoreCase("sent"))
			Toast.makeText(this, "List sent", Toast.LENGTH_SHORT).show();
		else
			Toast.makeText(this, response, Toast.LENGTH_SHORT).show();
		
	}
    
}
