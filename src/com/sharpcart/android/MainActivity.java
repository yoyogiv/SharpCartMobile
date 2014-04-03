package com.sharpcart.android;

import java.util.ArrayList;
import java.util.Calendar;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SlidingPaneLayout;
import android.util.Log;
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
import com.sharpcart.android.fragment.OnSaleWebViewFragment;
import com.sharpcart.android.fragment.OptimizedSharpListFragment;
import com.sharpcart.android.fragment.OptimizationTaskFragment;
import com.sharpcart.android.fragment.EmailSharpListTaskFragment;
import com.sharpcart.android.fragment.ChooseStoreDialogFragment.ChooseStoreDialogFragmentListener;
import com.sharpcart.android.fragment.EmailSharpListDialogFragment.EmailSharpListDialogFragmentListener;
import com.sharpcart.android.fragment.SettingsFragment;
import com.sharpcart.android.fragment.StoreSharpListFragment;
import com.sharpcart.android.fragment.UpdateShoppingItemPriceAndQuantityDialogFragment.UpdateShoppingItemPriceAndQuantityDialogFragmentListener;
import com.sharpcart.android.model.MainSharpList;
import com.sharpcart.android.model.StorePrices;
import com.sharpcart.android.service.SharpCartAlarmService;
import com.sharpcart.android.utilities.SharpCartUtilities;

public class MainActivity extends FragmentActivity implements 
													OptimizationTaskFragment.TaskCallbacks,
													MainScreenFragment.OnShoppingItemSelectedListener, 
													EmailSharpListDialogFragmentListener, 
													EmailSharpListTaskFragment.TaskCallbacks,
													ChooseStoreDialogFragmentListener,
													UpdateShoppingItemPriceAndQuantityDialogFragmentListener{

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
	private AlarmManager alarmMgr;
	private PendingIntent alarmIntent;
	private SharedPreferences.Editor editor;
	
	private static final String TAG = MainActivity.class.getSimpleName();
	
	public static final int IN_STORE_MODE = 0;
	public static final int ON_SALE_MODE = 1;
	private static final String KEY_FIRST_RUN = "first_run";
	
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);
		
	    //final SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
	    final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
	    
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
	    MainSharpList.getInstance().setLastUpdated(sharedPreferences.getLong("sharp_list_last_updated", 0));
	    
	    //If this is the first run we setup a repeating alarm to notify the user once a week to create a grocery list
	    if (!sharedPreferences.contains("KEY_FIRST_RUN")) {
		    editor = sharedPreferences.edit();
		    editor.putString("KEY_FIRST_RUN", KEY_FIRST_RUN);
		    editor.commit();
		  
		    //Set a repeating alarm 
		    //Set the alarm to start at approximately 2:00 p.m.
		    final Calendar calendar = Calendar.getInstance();
		    calendar.setTimeInMillis(System.currentTimeMillis());
		    calendar.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);

		    // With setInexactRepeating(), you have to use one of the AlarmManager interval
		    // constants--in this case, AlarmManager.INTERVAL_DAY.
		    alarmMgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
		    final Intent intent = new Intent(this, SharpCartAlarmService.class);
		    alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
		    
		    
		    alarmMgr.setInexactRepeating(AlarmManager.RTC, //Alarm type
		    		calendar.getTimeInMillis(), //First trigger
		            AlarmManager.INTERVAL_DAY*7, //Repeating interval
		            alarmIntent); //Intet to start	
	    }
	    
	    //check if we can sync our sharp list, if so present the user with an option to sync the list
	    if (sharedPreferences.getBoolean("canSyncSharpList", false))
	    {
	    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    	// Add the buttons
	    	builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
	    	           public void onClick(DialogInterface dialog, int id) {
	    	               sharedPreferences.edit().putBoolean("shouldSyncSharpList", true).commit();
	    	               
	    	               //call a sync operation
	    	               SharpCartUtilities.getInstance().syncFromServer(accounts[0]);
	    	           }
	    	       });
	    	
	    	builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
	    	           public void onClick(DialogInterface dialog, int id) {
	    	               // User cancelled the dialog
	    	        	   sharedPreferences.edit().putBoolean("shouldSyncSharpList", false).commit();
	    	           }
	    	       });
	    	
	    	// Set other dialog properties
	    	builder.setMessage(R.string.sync_sharp_list_dialog_message)
	        .setTitle(R.string.sync_sharp_list_dialog_title);

	    	// Create the AlertDialog
	    	AlertDialog dialog = builder.create();
	    	
	    	//Show dialog
	    	dialog.show();
	    }
	}
    
	@Override
	public void onShoppingItemSelected() {
    	((MainSharpListFragment)getSupportFragmentManager().findFragmentById(R.id.main_sharp_list_fragment)).updateSharpList();
    }
	
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		
		/*
		 * This code will add a search bad to the top action bar.
		 * I am not sure this is the way to go since I am not able to get the same
		 * richness I get with my custom autocomplete text edit
		 * 
	    // Get the SearchView and set the searchable configuration
	    SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
	    SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
	    
	    // Set searchable activity
	    searchView.setSearchableInfo(searchManager.getSearchableInfo(new ComponentName(this, SearchActivity.class)));
	    searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
	    */
		
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
	public void onOptimizationTaskPreExecute() {
		
	}

	@Override
	public void onOptimizationTaskProgressUpdate(final int percent) {
		
	}

	@Override
	public void onOptimizationTaskCancelled() {
		Toast.makeText(mContext,"No Internet Connection",Toast.LENGTH_SHORT).show();
	}

	@Override
	/*
	 * (non-Javadoc)
	 * @see com.sharpcart.android.fragment.TaskFragment.TaskCallbacks#onPostExecute()
	 * After the optimize sync task has finished we have a list of stores with items/prices and 
	 * we can start our optimizedList fragment
	 */
	public void onOptimizationTaskPostExecute(final ArrayList<StorePrices> optimizedStores) {
		
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
    	
		//Store Mode
		if (position==IN_STORE_MODE)
		{
			//only if we have some items in our list
			if (MainSharpList.getInstance().getMainSharpList().size()!=0)
				showChooseStoreDialog(IN_STORE_MODE);

		}
		
		//On Sale
		if (position==ON_SALE_MODE)
		{
			showChooseStoreDialog(ON_SALE_MODE);
		}
		
		//if running on a device in which the app uses the sliding pane, close the pane so our fragment is in full screen
		if ((mPane.isSlideable())&&(!findViewById(R.id.drawer_layout).getTag().equals("big_screen")))
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
	public void onFinishChooseStoreDialog(final String store,final int mode) {
		final android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
	    
		//In-Store mode
		if (mode==IN_STORE_MODE)
		{
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
		
		//On Sale mode
		if (mode==ON_SALE_MODE)
		{
			final Bundle bundle = new Bundle();
			
			if (store.equalsIgnoreCase("costco"))
			{
				bundle.putString("storeOnSaleUrl", "http://www.costco.com/warehouse-coupon-offers.html");
			}
			  else if (store.equalsIgnoreCase("heb")) 
			{
				bundle.putString("storeOnSaleUrl", "http://heb.inserts2online.com/customer_Frame.jsp?drpStoreID=373");
			} else if (store.equalsIgnoreCase("sprouts")) {
				bundle.putString("storeOnSaleUrl", "http://www.sprouts.com/specials/-/flyer/36348/store/110");
			} else if (store.equalsIgnoreCase("walmart")) {
				bundle.putString("storeOnSaleUrl", "http://www.costco.com/warehouse-coupon-offers.html");
			} else if (store.equalsIgnoreCase("sams club")) {
				bundle.putString("storeOnSaleUrl", "http://www.costco.com/warehouse-coupon-offers.html");
			}
			
			final FragmentTransaction ft = getSupportFragmentManager().beginTransaction(); 
			ft.addToBackStack(null);
			
			//Check if we already have a running on sale web view fragment
			final OnSaleWebViewFragment onSaleWebViewFragment;
			
			if (getSupportFragmentManager().findFragmentByTag("onSaleWebViewFragment")==null)
			{
				onSaleWebViewFragment = new OnSaleWebViewFragment();
				//set the arguments for the web view fragment uri
				onSaleWebViewFragment.setArguments(bundle);
				
				ft.replace(R.id.main_screen_fragment, onSaleWebViewFragment, "onSaleWebViewFragment");
				ft.commit();

			} else //we already have a running web-view so we just need to load a different URL
			{
				onSaleWebViewFragment = (OnSaleWebViewFragment)getSupportFragmentManager().findFragmentByTag("onSaleWebViewFragment");
				onSaleWebViewFragment.loadUrl(bundle.getString("storeOnSaleUrl"));
			}
			

		}
	}
	
   private void showChooseStoreDialog(final int mode) {
	   
        final android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        final ChooseStoreDialogFragment chooseStoreDialogFragment = new ChooseStoreDialogFragment();
        
        //set fragment bundle with mode information
        final Bundle bundle = new Bundle();
        bundle.putInt("chooseStoreDialogMode", mode);
        
        chooseStoreDialogFragment.setArguments(bundle);
        
        chooseStoreDialogFragment.show(fm, "chooseStoreDialogFragment");
    }
	
   public SlidingPaneLayout getPane()
   {
	   return mPane;
   }

   @Override
	public void onUpdateShoppingItemPriceAndQuantityDialogFragment(final int shoppingItemId,final double quantity,
			final double price) {

	   //update store item price based on the information the user provided
	   Log.d(TAG, "Updating Shopping Item with: Id="+shoppingItemId+" Quantity="+quantity+" Price="+price);
	   
	   if (storeSharpListFragment!=null)
		   storeSharpListFragment.updateShoppingItemAndAddItToCart(shoppingItemId, quantity, price);
	}

}
