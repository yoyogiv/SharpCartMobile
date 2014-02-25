package com.sharpcart.android;

import java.util.ArrayList;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SlidingPaneLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.sharpcart.android.authenticator.AuthenticatorActivity;
import com.sharpcart.android.dao.MainSharpListDAO;
import com.sharpcart.android.fragment.ChooseStoreDialogFragment;
import com.sharpcart.android.fragment.MainScreenFragment;
import com.sharpcart.android.fragment.MainSharpListFragment;
import com.sharpcart.android.fragment.OptimizedSharpListFragment;
import com.sharpcart.android.fragment.OptimizationTaskFragment;
import com.sharpcart.android.fragment.EmailSharpListTaskFragment;
import com.sharpcart.android.fragment.ChooseStoreDialogFragment.ChooseStoreDialogFragmentListener;
import com.sharpcart.android.fragment.EmailSharpListDialogFragment.EmailSharpListDialogFragmentListener;
import com.sharpcart.android.fragment.SettingsFragment;
import com.sharpcart.android.fragment.StoreSharpListFragment;
import com.sharpcart.android.model.MainSharpList;
import com.sharpcart.android.model.Store;
import com.sharpcart.android.utilities.SharpCartUtilities;

public class MainActivity extends FragmentActivity implements 
													OptimizationTaskFragment.TaskCallbacks,
													MainScreenFragment.OnShoppingItemSelectedListener, 
													EmailSharpListDialogFragmentListener, 
													EmailSharpListTaskFragment.TaskCallbacks,
													ChooseStoreDialogFragmentListener{

	private SlidingPaneLayout mPane;
	private AccountManager mAccountManager;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ListView mDrawerList;
    private String[] mApplicationNavigation;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
	private Context mContext;
	private MainScreenFragment mainScreenFragment;
	private MainSharpListFragment mainSharpListFragment;
	private OptimizedSharpListFragment optimizedSharpListFragment;
	private OptimizationTaskFragment mInStoreOptimizationTaskFragment;
	private StoreSharpListFragment storeSharpListFragment;
	private Account[] accounts;
	
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);
		
		mContext = getApplicationContext();
		
		mPane = (SlidingPaneLayout) findViewById(R.id.sliding_pane);
		//mPane.setPanelSlideListener(new PaneListener());
		
		mPane.openPane();
	    
		//drawer
		mTitle = mDrawerTitle = getTitle();
		
		mApplicationNavigation = getResources().getStringArray(R.array.application_navigation_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        
        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mApplicationNavigation));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
                ) {
            @Override
			public void onDrawerClosed(final View view) {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            @Override
			public void onDrawerOpened(final View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        
        mDrawerList.setItemChecked(0, true);
        
		//Start fragments
		mainScreenFragment = new MainScreenFragment();
		mainSharpListFragment = new MainSharpListFragment();
		optimizedSharpListFragment = new OptimizedSharpListFragment();
		storeSharpListFragment = new StoreSharpListFragment();

	    getSupportFragmentManager().beginTransaction().add(R.id.main_screen_fragment, mainScreenFragment, "main screen").commit();

	    getSupportFragmentManager().beginTransaction().add(R.id.main_sharp_list_fragment, mainSharpListFragment, "sharp list").commit();
	    
	    //save our account so we can use it when we need it
	    mAccountManager = AccountManager.get(getBaseContext());
		accounts = mAccountManager.getAccountsByType(AuthenticatorActivity.PARAM_ACCOUNT_TYPE);
		
	    //Load items to MainSharpList object
	    MainSharpList.getInstance().setUserName(mAccountManager.getAccounts()[0].name);
	    MainSharpList.getInstance().setMainSharpList(
	    		MainSharpListDAO.getInstance().getMainSharpListItemsWithSelection(getContentResolver(), null));
	}
    
	@Override
	public void onShoppingItemSelected() {
    	((MainSharpListFragment)getSupportFragmentManager().findFragmentById(R.id.main_sharp_list_fragment)).updateSharpList();
    }
	
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
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
    public boolean onOptionsItemSelected(final MenuItem item) {
    	
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
       if (mDrawerToggle.onOptionsItemSelected(item)) {
           return true;
       }
       
		final int itemId = item.getItemId();
		if (itemId == R.id.settings) {
			final Intent i = new Intent(this, SettingsFragment.class);
			startActivity(i);
			return true;
		} else if (itemId == R.id.refresh) {
			SharpCartUtilities.getInstance().syncFromServer(accounts[0]);
			return true;
		} else if (itemId == R.id.logout) {
			//verify with user
			   final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
				    @Override
				    public void onClick(final DialogInterface dialog, final int which) {
				        switch (which){
				        case DialogInterface.BUTTON_POSITIVE:
				        	//remove account
							mAccountManager.removeAccount(accounts[0], null, null);
							//close app
							finish();
				            break;

				        case DialogInterface.BUTTON_NEGATIVE:
				            //No button clicked
				            break;
				        }
				    }
				};
			final AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Logout?").setPositiveButton("Yes", dialogClickListener)
 			    .setNegativeButton("No", dialogClickListener).show();
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
    }

	@Override
	public void onPreExecute() {
		
	}

	@Override
	public void onProgressUpdate(final int percent) {
		
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
	public void onPostExecute(final ArrayList<Store> optimizedStores) {
		
		if (optimizedStores.size()!=0)
		{
			//Check if this is the choose stores optimization task fragment
			if (mInStoreOptimizationTaskFragment!=null)
			{
				storeSharpListFragment.setOptimizedStores(optimizedStores);
				mInStoreOptimizationTaskFragment = null;
				
				//now that we have both are store name and optimized stores we can start the Store Sharp List Fragment
		        // update the main content by replacing fragments
				final FragmentTransaction ft = getSupportFragmentManager().beginTransaction(); 
				ft.addToBackStack(null);
				ft.replace(R.id.main_screen_fragment, storeSharpListFragment, "storeSharpListFragment");
				ft.commit();
                
			} else // this is the optimization fragment
			{
			
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
					optimizedSharpListFragment.refresh();
				}
				
				//if running on a device in which the app uses the sliding pane, close the pane so our fragment is in full screen
				if ((mPane.isSlideable())&&(!findViewById(R.id.drawer_layout).getTag().equals("big_screen")))
					mPane.closePane();
				
		    	//always show the optimization table in landscape
		    	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		    	
		    	//disable MainSharpList optimize button
		    	((ImageButton)findViewById(R.id.optimizeMainSharpListButton)).setEnabled(false);
			}
			
		} else
		{
			Toast.makeText(mContext,"We had a problem, please try again...",Toast.LENGTH_SHORT).show();
		}
		
	}
	
	@Override
	public void onFinishEmailSharpListDialog(final String sharpListName,final String email) {
		
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
	public void onPostSharpListEmailSent(final String response) {
		if (response.equalsIgnoreCase("sent"))
			Toast.makeText(this, "List sent", Toast.LENGTH_SHORT).show();
		else
			Toast.makeText(this, response, Toast.LENGTH_SHORT).show();
		
	}
	
	@Override
	public void onBackPressed() {
		//we only want to show an exit confirmation dialog if we leave the application or "in-store" mode, this is to prevent accidental exit and lose of data
		final int fragmentStackCount = getSupportFragmentManager().getBackStackEntryCount();
		
		if(fragmentStackCount == 0)
		{
		    new AlertDialog.Builder(this)
		           .setMessage("Quit?")
		           .setCancelable(false)
		           .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		               @Override
					public void onClick(final DialogInterface dialog, final int id) {
		            	   //call a sync to the server
		            	   if (accounts!=null)
		            		   SharpCartUtilities.getInstance().syncFromServer(accounts[0]);
		                   
		            	   //close application
		            	   finish();
		               }
		           })
		           .setNegativeButton("No", null)
		           .show();
		}
		
		else if((fragmentStackCount == 1) && (getSupportFragmentManager().findFragmentByTag("storeSharpListFragment")!=null))
		{
		    new AlertDialog.Builder(this)
		           .setMessage("Leave in-store mode?")
		           .setCancelable(false)
		           .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		               @Override
					public void onClick(final DialogInterface dialog, final int id) {
		                    getSupportFragmentManager().popBackStack();
		               }
		           })
		           .setNegativeButton("No", null)
		           .show();
		} else {
			
	    	//enable MainSharpList optimize button
	    	((ImageButton)findViewById(R.id.optimizeMainSharpListButton)).setEnabled(true);
	    	
		    super.onBackPressed();
		}
	}
	
    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
            selectItem(position);
        }
    }

    private void selectItem(final int position) {
    	
        // update the main content by replacing fragments
		final FragmentTransaction ft = getSupportFragmentManager().beginTransaction(); 
		ft.addToBackStack(null);
		
		//Check if the fragment is already running
		if (getSupportFragmentManager().findFragmentByTag("storeSharpListFragment")==null)
		{
			//only if we have some items in our list
			if (MainSharpList.getInstance().getMainSharpList().size()!=0)
				showChooseStoreDialog();
		} else //refresh the fragment
		{
			
		}
		
		mPane.closePane();
		
        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
        //setTitle(mApplicationNavigation[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
        
    }

    @Override
    public void setTitle(final CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }
    
    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        mDrawerLayout.isDrawerOpen(mDrawerList);
        
        return super.onPrepareOptionsMenu(menu);
    }

	@Override
	public void onFinishChooseStoreDialog(final String store) {
		final android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
	    // If the Fragment is non-null, then it is currently being
	    // retained across a configuration change.
		if (getSupportFragmentManager().findFragmentByTag("optimizeSharpListTask")!=null)
			mInStoreOptimizationTaskFragment = (OptimizationTaskFragment)getSupportFragmentManager().findFragmentByTag("optimizeSharpListTask");
		
	    if (mInStoreOptimizationTaskFragment == null) {
	    	mInStoreOptimizationTaskFragment = new OptimizationTaskFragment();
	      fm.beginTransaction().add(mInStoreOptimizationTaskFragment, "chooseStoreOptimizeSharpListTask").commit();
	    }
	    
	    mInStoreOptimizationTaskFragment.start();
	    
	    storeSharpListFragment.setStoreName(store);
	}
	
   private void showChooseStoreDialog() {
        final android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        final ChooseStoreDialogFragment chooseStoreDialogFragment = new ChooseStoreDialogFragment();
        chooseStoreDialogFragment.show(fm, "chooseStoreDialogFragment");
    }
	
}
