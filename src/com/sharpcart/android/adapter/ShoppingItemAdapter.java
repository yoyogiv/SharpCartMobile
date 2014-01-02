package com.sharpcart.android.adapter;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.commons.lang3.text.WordUtils;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.CursorAdapter;
import android.widget.FilterQueryProvider;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sharpcart.android.MainActivity;
import com.sharpcart.android.fragment.MainScreen;
import com.sharpcart.android.fragment.MainSharpList;
import com.sharpcart.android.R;
import com.sharpcart.android.dao.MainSharpListDAO;
import com.sharpcart.android.fragment.MainSharpList;
import com.sharpcart.android.model.ShoppingItem;
import com.sharpcart.android.provider.SharpCartContentProvider;

public class ShoppingItemAdapter extends CursorAdapter implements Filterable{

    private LayoutInflater mInflater;
    private final int mNameIndex;
    private final int mDescriptionIndex;
    private final int mIdIndex;
    private final int mImageLocationIndex;
    private final int mCategoryIdIndex;
    private final int mUnitIdIndex;
    
    private Activity mActivity;
    private FragmentActivity mFragmentActivity;
    private ArrayList<String> selectedShoppingItemId;
    private Context mContext;
    private Drawable d;
	private static String categoryId;
    private MainSharpListDAO mainSharpListDAO;
    
    private static final String[] PROJECTION_ID_NAME_DESCRIPTION_CATEGORYID_UNITID_IMAGELOCATION = new String[] {
	    SharpCartContentProvider.COLUMN_ID,
	    SharpCartContentProvider.COLUMN_NAME,
	    SharpCartContentProvider.COLUMN_DESCRIPTION,
	    SharpCartContentProvider.COLUMN_SHOPPING_ITEM_CATEGORY_ID,
	    SharpCartContentProvider.COLUMN_SHOPPING_ITEM_UNIT_ID,
	    SharpCartContentProvider.COLUMN_IMAGE_LOCATION };

    public ShoppingItemAdapter(Activity activity) {
		super(activity, getManagedCursor(activity), false);
	
		mActivity = activity;
		mFragmentActivity = (FragmentActivity)activity;
		mInflater = LayoutInflater.from(activity);
		final Cursor c = getCursor();
		mContext = activity.getApplicationContext();
		
		mIdIndex = c.getColumnIndexOrThrow(SharpCartContentProvider.COLUMN_ID);
		mNameIndex = c.getColumnIndexOrThrow(SharpCartContentProvider.COLUMN_NAME);
		mDescriptionIndex = c.getColumnIndexOrThrow(SharpCartContentProvider.COLUMN_DESCRIPTION);
		mCategoryIdIndex = c.getColumnIndexOrThrow(SharpCartContentProvider.COLUMN_SHOPPING_ITEM_CATEGORY_ID);
		mUnitIdIndex = c.getColumnIndexOrThrow(SharpCartContentProvider.COLUMN_SHOPPING_ITEM_UNIT_ID);
		mImageLocationIndex = c.getColumnIndexOrThrow(SharpCartContentProvider.COLUMN_IMAGE_LOCATION);
		
		selectedShoppingItemId = new ArrayList<String>();
		
		mainSharpListDAO = MainSharpListDAO.getInstance();
		
    }
    
    public void updateCursor()
    {
    	changeCursor(getManagedCursor(mActivity));
    }
    
    private static Cursor getManagedCursor(Activity activity) {
    	if (categoryId==null)
    		categoryId="3";
    	
		return activity.getContentResolver().query(
			SharpCartContentProvider.CONTENT_URI_SHOPPING_ITEMS,
			PROJECTION_ID_NAME_DESCRIPTION_CATEGORYID_UNITID_IMAGELOCATION,
			SharpCartContentProvider.COLUMN_SHOPPING_ITEM_CATEGORY_ID + "='" + categoryId+"'", 
			null,
			SharpCartContentProvider.DEFAULT_SORT_ORDER);
    }

    @Override
    public void bindView(View view, Context context, Cursor c) {
    	
    	//Create a view holder and populate it with information from the database cursor
    	final ShoppingItemViewContainer holder = (ShoppingItemViewContainer) view.getTag();

		//holder.itemNameTextView.setText(c.getString(mNameIndex));
		//holder.itemDescriptionTextView.setText(c.getString(mDescriptionIndex));
		
		holder.itemId = (c.getInt(mIdIndex));
		holder.itemCategoryId = (c.getInt(mCategoryIdIndex));
		holder.itemUnitId = (c.getInt(mUnitIdIndex));
		holder.itemName = (c.getString(mNameIndex));
		holder.itemDescription = (c.getString(mDescriptionIndex));
		holder.itemImageLocation = (c.getString(mImageLocationIndex));
		
		
		/*
		 * Load images for shopping items from assests folder
		 */
		try {
		    // get input stream
			String shoppingItemImageLocation = c.getString(mImageLocationIndex).replaceFirst("/", "");
			
		    InputStream ims = mActivity.getApplicationContext().getAssets().open(shoppingItemImageLocation);
		    
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
		    		   ShoppingItem selectedShoppingItem = new ShoppingItem();
		    		   
		    		   selectedShoppingItem.setId(holder.itemId);
		    		   selectedShoppingItem.setShopping_Item_Category_Id(holder.itemCategoryId);
		    		   selectedShoppingItem.setShopping_Item_Unit_Id(holder.itemUnitId);
		    		   selectedShoppingItem.setName(holder.itemName);
		    		   selectedShoppingItem.setDescription(holder.itemDescription);
		    		   selectedShoppingItem.setQuantity(1.0);
		    		   selectedShoppingItem.setImage_Location(holder.itemImageLocation);
		    		   
		    		   //use the DAO object to insert the new shopping item object into the main sharp list table
		    		   mainSharpListDAO.addNewItemToMainSharpList(mContext.getContentResolver(), selectedShoppingItem);
		    		   
		    		   //update main sharp list fragment
		    		   MainScreen mainScreen = (MainScreen) ((FragmentActivity) mActivity).getSupportFragmentManager().findFragmentById(R.id.main_screen_fragment);
		    		   mainScreen.updateSharpList();
		    		   
		    		  Toast.makeText(mContext,holder.itemDescription + " Added ",Toast.LENGTH_SHORT).show();
		    	   }
		    });
			
		} catch (IOException ex) {
		    Log.d("ShoppingItemAdapter", ex.getLocalizedMessage());
		}
		
		final int id = Integer.valueOf(holder.itemId);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
		final View view = mInflater.inflate(R.layout.shopping_item_box, parent,false);
		
		ShoppingItemViewContainer holder = new ShoppingItemViewContainer();
		
		//set image name text view
		//holder.itemNameTextView = (TextView) view.findViewById(R.id.shopping_item_row_name);
		//holder.itemDescriptionTextView = (TextView) view.findViewById(R.id.shopping_item_row_description);
		holder.imageView = (ImageView) view.findViewById(R.id.shoppingItemImageView);
		
		view.setTag(holder);
	
		return view;
    }

    public String getSelectedShoppingItemId() {
		return selectedShoppingItemId.toString();
    }

    static class ShoppingItemViewContainer {
		public ImageView imageView;
		public TextView itemNameTextView;
		public TextView itemDescriptionTextView;
		
		public String itemName;
		public String itemDescription;
		public int itemUnitId;
		public int itemCategoryId;
		public int itemId;
		public String itemImageLocation;
		public int itemQuantity;
    }
    
    public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
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
        FilterQueryProvider filter = getFilterQueryProvider();
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
