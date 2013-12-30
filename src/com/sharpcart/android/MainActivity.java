package com.sharpcart.android;

import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.app.Activity;
import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.FilterQueryProvider;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import com.sharpcart.android.adapter.MainSharpListItemAdapter;
import com.sharpcart.android.adapter.ShoppingItemAdapter;
import com.sharpcart.android.model.CategoryImage;
import com.sharpcart.android.provider.SharpCartContentProvider;

public class MainActivity extends Activity {

	private LinearLayout categoriesGallery;
	private int itemBackground;
	private Context context;
	private GridView shoppingItemsGridView;
	private ListView mainSharpListItemsListView;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
	
	public static MainSharpListItemAdapter mainSharpListAdapter;
	
    private ContentResolver mResolver;  // A content resolver for accessing the provider
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		/* Category Images
		 * We are adding them to the array in the same order they show up on the web site
		 */
		ArrayList<CategoryImage> categoryImages = new ArrayList<CategoryImage>();
		
		categoryImages.add(new CategoryImage(R.drawable.produce, 3));
		categoryImages.add(new CategoryImage(R.drawable.meat, 5));
		categoryImages.add(new CategoryImage(R.drawable.dairy, 6));
		categoryImages.add(new CategoryImage(R.drawable.bakery, 7));
		categoryImages.add(new CategoryImage(R.drawable.organic, 22));
		categoryImages.add(new CategoryImage(R.drawable.frozen, 18));
		categoryImages.add(new CategoryImage(R.drawable.breakfast, 21));
		categoryImages.add(new CategoryImage(R.drawable.grains_and_pasta, 16));
		categoryImages.add(new CategoryImage(R.drawable.canned, 10));
		categoryImages.add(new CategoryImage(R.drawable.snacks, 4));
		categoryImages.add(new CategoryImage(R.drawable.condiments, 20));
		categoryImages.add(new CategoryImage(R.drawable.beverages, 11));
		categoryImages.add(new CategoryImage(R.drawable.baking, 12));
		categoryImages.add(new CategoryImage(R.drawable.baby, 8));
		categoryImages.add(new CategoryImage(R.drawable.pet, 9));
		categoryImages.add(new CategoryImage(R.drawable.personal_care, 14));
		categoryImages.add(new CategoryImage(R.drawable.paper, 15));
		categoryImages.add(new CategoryImage(R.drawable.cleaning_supplies, 19));

		categoriesGallery = (LinearLayout)findViewById(R.id.categories_gallery);
		
		context = this.getApplicationContext();
		
		// Get the content resolver for the application
		mResolver = getContentResolver();
		
		/*Set a grey background; wraps around the images */
		TypedArray a = obtainStyledAttributes(R.styleable.CategoryGallery);
		itemBackground = a.getResourceId(R.styleable.CategoryGallery_android_galleryItemBackground, 1);
		a.recycle();
		
		/*Load category images into category horizontal view*/
		for (CategoryImage categoryImage : categoryImages)
		{
			ImageView imageView = new ImageView(context);
			imageView.setImageResource(categoryImage.getDrawableResourceId());
			imageView.setBackgroundResource(itemBackground);
			imageView.setId(categoryImage.getDatabaseId()); //We are using the category database id to set the image view id so we can latter use it when the user clicks on the image
			
			/*Set onClick event for each category image */
			imageView.setOnClickListener(new OnClickListener()
			{

		    	   @Override
		    	   public void onClick(View v) 
		    	   {
			    	    //update shopping items grid view with the items in the category the user clicked
			    	    showCategoryItems(v.getId());
		    	   }
		    });
			
			categoriesGallery.addView(imageView);
			
		}
		
		/*Copy offline database if it doesn't already exist */
		String destDir = "/data/data/" + getPackageName() + "/databases/";
		String destPath = destDir + "SharpCart";
		File f = new File(destPath);
		if (!f.exists()) 
		{
			//---make sure directory exists---
			File directory = new File(destDir);
			directory.mkdirs();
			//---copy the db from the assets folder into
			// the databases folder---
			try 
			{
				CopyDB(getBaseContext().getAssets().open("SharpCart"),
				new FileOutputStream(destPath));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		
		/* create a populate a grid view with shopping items */
		shoppingItemsGridView = (GridView) findViewById(R.id.shoppingItemsGridView);
		shoppingItemsGridView.setAdapter(new ShoppingItemAdapter(this));
		
		/*initialize our autocomplete search */
	    AutoCompleteTextView completeTextView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
	    
	    ShoppingItemAdapter mAdapter = new ShoppingItemAdapter(this);
	    
	    completeTextView.setAdapter(mAdapter);
	    
	    /*initialize main sharp list list view*/
	    mainSharpListAdapter = new MainSharpListItemAdapter(this);
	    mainSharpListItemsListView = (ListView) findViewById(R.id.mainSharpListItemsListView);
	    mainSharpListItemsListView.setAdapter(mainSharpListAdapter);
	  
	    /*setup drawer layout*/
        mTitle = mDrawerTitle = getTitle();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
                ) {
			            public void onDrawerClosed(View view) {
			                getActionBar().setTitle(mTitle);
			                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
			            }
			
			            public void onDrawerOpened(View drawerView) {
			                getActionBar().setTitle(mDrawerTitle);
			                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
			            }
        			};
        mDrawerLayout.setDrawerListener(mDrawerToggle);

	}

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         // The action bar home/up action should open or close the drawer.
         // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void CopyDB(InputStream inputStream, OutputStream outputStream)throws IOException {
		//---copy 1K bytes at a time---
		byte[] buffer = new byte[1024];
		int length;
		while ((length = inputStream.read(buffer)) > 0) {
		outputStream.write(buffer, 0, length);
		}
		inputStream.close();
		outputStream.close();
	}
	
	public void showCategoryItems(int categoryId)
	{
		((ShoppingItemAdapter)shoppingItemsGridView.getAdapter()).setCategoryId(Integer.toString(categoryId));
		((ShoppingItemAdapter)shoppingItemsGridView.getAdapter()).updateCursor();
	}
	
    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
}
