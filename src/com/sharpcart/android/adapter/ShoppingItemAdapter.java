package com.sharpcart.android.adapter;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.FilterQueryProvider;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sharpcart.android.fragment.MainScreenFragment;
import com.sharpcart.android.R;
import com.sharpcart.android.dao.MainSharpListDAO;
import com.sharpcart.android.model.MainSharpList;
import com.sharpcart.android.model.ShoppingItem;
import com.sharpcart.android.provider.SharpCartContentProvider;
import com.sharpcart.android.utilities.SharpCartUtilities;

public class ShoppingItemAdapter extends CursorAdapter implements Filterable{

    private final LayoutInflater mInflater;
    private final int mNameIndex;
    private final int mDescriptionIndex;
    private final int mIdIndex;
    private final int mImageLocationIndex;
    private final int mCategoryIdIndex;
    private final int mUnitIdIndex;
    private final int mOnSaleIndex;
    private final int mActiveIndex;
    
    private final Activity mActivity;
    private final ArrayList<String> selectedShoppingItemId;
    private final Context mContext;
    private Drawable d;
	private static String categoryId;
    private final MainSharpListDAO mainSharpListDAO;
    private Drawable onSaleDrawable;
    
	private static SharedPreferences prefs = null;
	
    private static final String[] PROJECTION_ID_NAME_DESCRIPTION_CATEGORYID_UNITID_IMAGELOCATION = new String[] {
	    SharpCartContentProvider.COLUMN_ID,
	    SharpCartContentProvider.COLUMN_NAME,
	    SharpCartContentProvider.COLUMN_DESCRIPTION,
	    SharpCartContentProvider.COLUMN_SHOPPING_ITEM_CATEGORY_ID,
	    SharpCartContentProvider.COLUMN_SHOPPING_ITEM_UNIT_ID,
	    SharpCartContentProvider.COLUMN_IMAGE_LOCATION,
	    SharpCartContentProvider.COLUMN_ON_SALE,
	    SharpCartContentProvider.COLUMN_ACTIVE};

    public ShoppingItemAdapter(Activity activity) {
		super(activity, getManagedCursor(activity), false);
		
		mActivity = activity;
		mInflater = LayoutInflater.from(activity);
		final Cursor c = getCursor();
		mContext = activity.getApplicationContext();
		
		mIdIndex = c.getColumnIndexOrThrow(SharpCartContentProvider.COLUMN_ID);
		mNameIndex = c.getColumnIndexOrThrow(SharpCartContentProvider.COLUMN_NAME);
		mDescriptionIndex = c.getColumnIndexOrThrow(SharpCartContentProvider.COLUMN_DESCRIPTION);
		mCategoryIdIndex = c.getColumnIndexOrThrow(SharpCartContentProvider.COLUMN_SHOPPING_ITEM_CATEGORY_ID);
		mUnitIdIndex = c.getColumnIndexOrThrow(SharpCartContentProvider.COLUMN_SHOPPING_ITEM_UNIT_ID);
		mImageLocationIndex = c.getColumnIndexOrThrow(SharpCartContentProvider.COLUMN_IMAGE_LOCATION);
		mOnSaleIndex = c.getColumnIndex(SharpCartContentProvider.COLUMN_ON_SALE);
		mActiveIndex = c.getColumnIndex(SharpCartContentProvider.COLUMN_ACTIVE);
		
		selectedShoppingItemId = new ArrayList<String>();
		
		mainSharpListDAO = MainSharpListDAO.getInstance();
		
		// Load our updated image into a drawable once
		try {
			    // get input stream
			    final InputStream ims = mContext.getAssets().open("images/on_sale.png");
			    // load image as Drawable
			    onSaleDrawable = Drawable.createFromStream(ims, null);
			    
			    ims.close();

			} catch (final IOException ex) 
			{
			    Log.d("storeItemArrayAdapter", ex.getLocalizedMessage());
			}
		
		c.close();
    }
    
    public void updateCursor()
    {
    	changeCursor(getManagedCursor(mActivity));
    }
    
    private static Cursor getManagedCursor(Activity activity) {
    	
		prefs = activity.getApplication().getSharedPreferences("com.sharpcart.android", android.content.Context.MODE_PRIVATE);
		
		if (categoryId==null)
			categoryId = "3";
		
    	//the special case of the first login
    	if (prefs.getBoolean("firstrun", true))
    	{
    	//if (categoryId==null)
    	//{
    		prefs.edit().putBoolean("firstrun", false).commit();
    		
    		return activity.getContentResolver().query(
    				SharpCartContentProvider.CONTENT_URI_SHOPPING_ITEMS,
    				PROJECTION_ID_NAME_DESCRIPTION_CATEGORYID_UNITID_IMAGELOCATION,
    				SharpCartContentProvider.COLUMN_SHOPPING_ITEM_CATEGORY_ID + "='3'", 
    				null,
    				SharpCartContentProvider.DEFAULT_SORT_ORDER);
    	} else
    	{   	
			return activity.getContentResolver().query(
				SharpCartContentProvider.CONTENT_URI_SHOPPING_ITEMS,
				PROJECTION_ID_NAME_DESCRIPTION_CATEGORYID_UNITID_IMAGELOCATION,
				SharpCartContentProvider.COLUMN_SHOPPING_ITEM_CATEGORY_ID + "='" + categoryId+"' AND " +
				SharpCartContentProvider.COLUMN_ACTIVE + "= '1'", 
				null,
				SharpCartContentProvider.DEFAULT_SORT_ORDER);
    	}
    }

    @Override
    public void bindView(View view, Context context, Cursor c) {
    	
    	//Create a view holder and populate it with information from the database cursor
    	final ShoppingItemViewContainer holder = (ShoppingItemViewContainer) view.getTag();

		holder.itemNameTextView.setText(c.getString(mNameIndex));
		//holder.itemDescriptionTextView.setText(c.getString(mDescriptionIndex));
		
		holder.itemId = (c.getInt(mIdIndex));
		holder.itemCategoryId = (c.getInt(mCategoryIdIndex));
		holder.itemUnitId = (c.getInt(mUnitIdIndex));
		holder.itemName = (c.getString(mNameIndex));
		holder.itemDescription = (c.getString(mDescriptionIndex));
		holder.itemImageLocation = (c.getString(mImageLocationIndex));
		
		if (c.getInt(mOnSaleIndex)==1)
			holder.itemOnSale = 1;
		else
			holder.itemOnSale = 0;
		
		if (c.getInt(mActiveIndex)==1)
			holder.itemActive = 1;
		else
			holder.itemActive = 0;
		
		/*
		 * Load images for shopping items from assests folder
		 */
		try {
		    // get input stream
			final String shoppingItemImageLocation = c.getString(mImageLocationIndex).replaceFirst("/", "");
			
		    final InputStream ims = mActivity.getApplicationContext().getAssets().open(shoppingItemImageLocation);
		    
		    // load image as Drawable
		    d = Drawable.createFromStream(ims, null);
		    
		    // set image to ImageView
		    holder.imageView.setImageDrawable(d);
		    
			/*Set onClick event for each item image */
			holder.imageView.setOnClickListener(new OnClickListener()
			{
		    	   @Override
		    	   public void onClick(View v) 
		    	   {
			    		   //Create a new shopping item object based on the item clicked
			    		   final ShoppingItem selectedShoppingItem = new ShoppingItem();
			    		   
			    		   selectedShoppingItem.setId(holder.itemId);
			    		   selectedShoppingItem.setShopping_item_category_id(holder.itemCategoryId);
			    		   selectedShoppingItem.setShopping_item_unit_id(holder.itemUnitId);
			    		   selectedShoppingItem.setName(holder.itemName);
			    		   selectedShoppingItem.setDescription(holder.itemDescription);
			    		   selectedShoppingItem.setQuantity(1.0);
			    		   selectedShoppingItem.setImage_location(holder.itemImageLocation);
			    		   selectedShoppingItem.setCategory(SharpCartUtilities.getInstance().getCategoryName(holder.itemCategoryId));
			    		   selectedShoppingItem.setUnit(SharpCartUtilities.getInstance().getUnitName(holder.itemUnitId));
			    		   selectedShoppingItem.setDefault_unit_in_db(SharpCartUtilities.getInstance().getUnitName(holder.itemUnitId));
			    		   
			    		   //use the DAO object to insert the new shopping item object into the main sharp list table
			    		   mainSharpListDAO.addNewItemToMainSharpList(mContext.getContentResolver(), selectedShoppingItem);
			    		   
			    		   //update main sharp list fragment
			    		   /*
			    		   final MainScreenFragment mainScreen = (MainScreenFragment) ((FragmentActivity) mActivity).getSupportFragmentManager().findFragmentById(R.id.main_screen_fragment);
			    		   mainScreen.updateSharpList();
			    		   */
			    		   
		    		   //before we add a new item to the list, we check if we already have one in the list
		    		   if (!MainSharpList.getInstance().isItemInList(holder.itemId))
		    		   {   
			    		   //add new item to main sharp list object
			    		   MainSharpList.getInstance().addShoppingItemToList(selectedShoppingItem);
			    		   
		    		   } else // we already have an item of this type in our list so no need to insert a new object
		    		   {
		    			   MainSharpList.getInstance().addShoppingItemToList(holder.itemId);
		    		   }
		    		   
		    		   MainSharpList.getInstance().setLastUpdated(new Timestamp(System.currentTimeMillis()).toString());

		    		  Toast.makeText(mContext,holder.itemDescription + " Added ",Toast.LENGTH_SHORT).show();
		    	   }
		    });
			
			//Set the on sale image for items that are on sale
			if (holder.itemOnSale==1)
			{
				holder.onSaleImageView.setImageDrawable(onSaleDrawable);
			} else
			{
				holder.onSaleImageView.setImageResource(android.R.color.transparent);
			}
			
			
		} catch (final IOException ex) {
		    Log.d("ShoppingItemAdapter", ex.getLocalizedMessage());
		}
		
		Integer.valueOf(holder.itemId);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
		final View view = mInflater.inflate(R.layout.shopping_item_box, parent,false);
		
		final ShoppingItemViewContainer holder = new ShoppingItemViewContainer();
		
		//set image name text view
		holder.itemNameTextView = (TextView) view.findViewById(R.id.shopping_item_row_name);
		//holder.itemDescriptionTextView = (TextView) view.findViewById(R.id.shopping_item_row_description);
		holder.imageView = (ImageButton) view.findViewById(R.id.shoppingItemImageView);
		holder.onSaleImageView = (ImageView) view.findViewById(R.id.shoppingItemOnSaleImageView);
		
		if (cursor.getInt(mOnSaleIndex)==1)
			holder.itemOnSale = 1;
		else
			holder.itemOnSale = 0;
			
		view.setTag(holder);
	
		return view;
    }

    public String getSelectedShoppingItemId() {
		return selectedShoppingItemId.toString();
    }

    static class ShoppingItemViewContainer {
		public ImageButton imageView;
		public ImageView onSaleImageView;
		public TextView itemNameTextView;
		public TextView itemDescriptionTextView;
		
		public String itemName;
		public String itemDescription;
		public int itemUnitId;
		public int itemCategoryId;
		public int itemId;
		public String itemImageLocation;
		public double itemQuantity;
		public int itemOnSale;
		public int itemActive;
    }
    
    public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		ShoppingItemAdapter.categoryId = categoryId;
	}
	
    @Override
    /*
     * (non-Javadoc)
     * @see android.widget.CursorAdapter#convertToString(android.database.Cursor)
     * This method will convert the returned shopping item description into a string that will be shown in our search auto complete text
     */
    public String convertToString(Cursor cursor) {
        return cursor.getString(cursor.getColumnIndexOrThrow(SharpCartContentProvider.COLUMN_DESCRIPTION));
    }
    
    @Override
    /*
     * (non-Javadoc)
     * @see android.widget.CursorAdapter#runQueryOnBackgroundThread(java.lang.CharSequence)
     * In order for our shopping item adapter to work with an auto complete text box, we have to implement Filterable.
     * This method will do just that. It will run a query on our database that will look for any shopping item where with a name like 
     * the string the user is typing in the auto complete search box.
     * 
     */
    public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
        final FilterQueryProvider filter = getFilterQueryProvider();
        if (filter != null) {
            return filter.runQuery(constraint);
        }
        
        String shoppingItemDescription = "";
        
        if (constraint!=null)
        {
        	shoppingItemDescription = constraint.toString();
        }
        
        return mActivity.getContentResolver().query(
    			SharpCartContentProvider.CONTENT_URI_SHOPPING_ITEMS,
    			PROJECTION_ID_NAME_DESCRIPTION_CATEGORYID_UNITID_IMAGELOCATION,
    			SharpCartContentProvider.COLUMN_NAME + " LIKE '%"+shoppingItemDescription+"%'", 
    			null,
    			SharpCartContentProvider.DEFAULT_SORT_ORDER);
    }
}
