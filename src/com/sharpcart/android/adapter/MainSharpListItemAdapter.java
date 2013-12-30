package com.sharpcart.android.adapter;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import com.sharpcart.android.R;
import com.sharpcart.android.adapter.ShoppingItemAdapter.ShoppingItemViewContainer;
import com.sharpcart.android.provider.SharpCartContentProvider;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainSharpListItemAdapter extends CursorAdapter {
    private LayoutInflater mInflater;
    private Activity mActivity;
    private ArrayList<String> selectedShoppingItemId;
    private Context mContext;
    private Drawable d;
    
    private final int mNameIndex;
    private final int mDescriptionIndex;
    private final int mIdIndex;
    private final int mCategoryIdIndex;
    private final int mUnitIdIndex;
    private final int mImageLocationIndex;
    private final int mQuantityIndex;

    private static final String[] PROJECTION_ID_NAME_DESCRIPTION_CATEGORYID_UNITID_IMAGELOCATION_QUANTITY = new String[] {
	    SharpCartContentProvider.COLUMN_ID,
	    SharpCartContentProvider.COLUMN_NAME,
	    SharpCartContentProvider.COLUMN_DESCRIPTION,
	    SharpCartContentProvider.COLUMN_SHOPPING_ITEM_CATEGORY_ID,
	    SharpCartContentProvider.COLUMN_SHOPPING_ITEM_UNIT_ID,
	    SharpCartContentProvider.COLUMN_IMAGE_LOCATION,
	    SharpCartContentProvider.COLUMN_QUANTITY};

    public MainSharpListItemAdapter(Activity activity) {
		super(activity, getManagedCursor(activity), false);
	
		final Cursor c = getCursor();
		
		mActivity = activity;
		mInflater = LayoutInflater.from(activity);
		mContext = activity.getApplicationContext();
		
		mIdIndex = c.getColumnIndexOrThrow(SharpCartContentProvider.COLUMN_ID);	
		mNameIndex = c.getColumnIndexOrThrow(SharpCartContentProvider.COLUMN_NAME);
		mDescriptionIndex = c.getColumnIndexOrThrow(SharpCartContentProvider.COLUMN_DESCRIPTION);
		mCategoryIdIndex = c.getColumnIndexOrThrow(SharpCartContentProvider.COLUMN_SHOPPING_ITEM_CATEGORY_ID);
		mUnitIdIndex = c.getColumnIndexOrThrow(SharpCartContentProvider.COLUMN_SHOPPING_ITEM_UNIT_ID);
		mImageLocationIndex = c.getColumnIndexOrThrow(SharpCartContentProvider.COLUMN_IMAGE_LOCATION);
		mQuantityIndex = c.getColumnIndexOrThrow(SharpCartContentProvider.COLUMN_QUANTITY);
		
		selectedShoppingItemId = new ArrayList<String>();		
    }
    
    public void updateCursor()
    {
    	this.changeCursor(getManagedCursor(mActivity));
    }
    
    /*
     * This method will generate our cursor by running a query to retrieve all shopping items 
     * from the main sharp list table
     */
    private static Cursor getManagedCursor(Activity activity) {
		return activity.getContentResolver().query(
			SharpCartContentProvider.CONTENT_URI_SHARP_LIST_ITEMS,
			PROJECTION_ID_NAME_DESCRIPTION_CATEGORYID_UNITID_IMAGELOCATION_QUANTITY,
			null, 
			null,
			SharpCartContentProvider.DEFAULT_SORT_ORDER);
    }
    
	@Override
	public void bindView(View view, Context context, Cursor cursor) {
    	//Create a view holder and populate it with information from the database cursor
    	final ShoppingItemViewContainer holder = (ShoppingItemViewContainer) view.getTag();

		holder.itemDescriptionTextView.setText(cursor.getString(mDescriptionIndex));
		
		holder.itemId = (cursor.getInt(mIdIndex));
		holder.itemName = (cursor.getString(mNameIndex));
		holder.itemDescription = (cursor.getString(mDescriptionIndex));
		holder.itemImageLocation = (cursor.getString(mImageLocationIndex));
		holder.itemQuantity = (cursor.getInt(mQuantityIndex));
		/*
		 * Load images for shopping items from assets folder
		 */
		try {
		    // get input stream
			String shoppingItemImageLocation = cursor.getString(mImageLocationIndex).replaceFirst("/", "");
			
		    InputStream ims = mActivity.getApplicationContext().getAssets().open(shoppingItemImageLocation);
		    
		    // load image as Drawable
		    d = Drawable.createFromStream(ims, null);
		    
		    // set image to ImageView
		    holder.imageView.setImageDrawable(d);  
			
		} catch (IOException ex) {
		    Log.d("MainSharpListItemAdapter", ex.getLocalizedMessage());
		}
		
		final int id = Integer.valueOf(holder.itemId);

	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		final View view = mInflater.inflate(R.layout.shopping_item_box, parent,false);
		
		ShoppingItemViewContainer holder = new ShoppingItemViewContainer();
		
		//set image name text view
		holder.itemDescriptionTextView = (TextView) view.findViewById(R.id.shopping_item_row_name);
		holder.imageView = (ImageView) view.findViewById(R.id.shoppingItemImageView);
		
		view.setTag(holder);
	
		return view;
	}
	
    static class ShoppingItemViewContainer {
		public ImageView imageView;
		public TextView itemDescriptionTextView;
	
		public int itemId;
		public String itemName;
		public String itemDescription;
		public int itemUnitId;
		public int itemCategoryId;
		public String itemImageLocation;
		public int itemQuantity;
    }
}
