package com.sharpcart.android.adapter;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import com.sharpcart.android.R;
import com.sharpcart.android.custom.ShoppingItemQuantityEditText;
import com.sharpcart.android.dao.MainSharpListDAO;
import com.sharpcart.android.model.MainSharpList;
import com.sharpcart.android.provider.SharpCartContentProvider;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
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

    private static final String TAG = MainSharpListItemAdapter.class.getSimpleName();
    
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
    	changeCursor(getManagedCursor(mActivity));
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

		holder.itemNameTextView.setText(cursor.getString(mNameIndex)+"\n"+cursor.getString(mDescriptionIndex));
		//holder.itemDescriptionTextView.setText(cursor.getString(mDescriptionIndex));
		holder.itemQuantityEditText.setText(cursor.getString(mQuantityIndex));
		
		holder.itemId = (cursor.getInt(mIdIndex));
		holder.itemName = (cursor.getString(mNameIndex));
		holder.itemDescription = (cursor.getString(mDescriptionIndex));
		holder.itemImageLocation = (cursor.getString(mImageLocationIndex));
		holder.itemQuantity = (cursor.getDouble(mQuantityIndex));
		
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
		    
			/*Set onClick event for delete button */
			holder.deleteImageButton.setOnClickListener(new OnClickListener()
			{
		    	   @Override
		    	   public void onClick(View v) 
		    	   {
		    		   
		    		   //use the DAO object to delete shopping item from main sharp list table
		    		   MainSharpListDAO.getInstance().deleteMainSharpListItem(mContext.getContentResolver(), holder.itemId);
		    		   
		    		   //delete shopping item from main sharp list object
		    		   MainSharpList.getInstance().removeShoppingItemFromList(holder.itemId);
		    		   
		    		   //Update main sharp list adapter cursor to reflect the added shopping item
		    		   updateCursor();
		    		   
		    		   Toast.makeText(mContext,holder.itemDescription + " Deleted ",Toast.LENGTH_SHORT).show();
		    	   }
		    });
			
		} catch (IOException ex) {
		    Log.d("MainSharpListItemAdapter", ex.getLocalizedMessage());
		}
		
		//setup a lost focus action so that when the user changed the item amount we will update it in both the db and MainSharpList object
		holder.itemQuantityEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				
				if (!hasFocus)
				{
					try {
						double itemQuantity = Double.valueOf(holder.itemQuantityEditText.getText().toString());
						
						//Update MainSharpList object
						MainSharpList.getInstance().setItemQuantity(holder.itemId, itemQuantity);
						
						//Update db
						ContentValues cv = new ContentValues();
						cv.put(SharpCartContentProvider.COLUMN_QUANTITY, itemQuantity);
						
						int count = mActivity.getContentResolver().update(
								SharpCartContentProvider.CONTENT_URI_SHARP_LIST_ITEMS,
								cv,
								SharpCartContentProvider.COLUMN_ID+"="+holder.itemId, 
								null);
						
		    		   //Update main sharp list adapter cursor to reflect the added shopping item
		    		   updateCursor();
		    		   
					} catch (NumberFormatException ex)
					{
						Log.d(TAG,ex.getMessage());
					}
				}
			}
		});
		
		
		holder.itemQuantityEditText.setOnEditorActionListener(new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

	            if((actionId == EditorInfo.IME_ACTION_DONE)||(event.getKeyCode() == KeyEvent.KEYCODE_BACK))
	            {
					try {
						double itemQuantity = Double.valueOf(v.getText().toString());
						
						//Update MainSharpList object
						MainSharpList.getInstance().setItemQuantity(holder.itemId, itemQuantity);
						
						//Update db
						ContentValues cv = new ContentValues();
						cv.put(SharpCartContentProvider.COLUMN_QUANTITY, itemQuantity);
						
						int count = mActivity.getContentResolver().update(
								SharpCartContentProvider.CONTENT_URI_SHARP_LIST_ITEMS,
								cv,
								SharpCartContentProvider.COLUMN_ID+"="+holder.itemId, 
								null);
						
		    		   //Update main sharp list adapter cursor to reflect the added shopping item
		    		   updateCursor();
		    		   
		    		   return true;
		    		   
					} catch (NumberFormatException ex)
					{
						Log.d(TAG,ex.getMessage());
					}
	            }
	            
				return false;
			}
		});
		
		/*
		holder.itemQuantityEditText.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				try {
					
					double itemQuantity = Double.valueOf(s.toString());
					
					//Update MainSharpList object
					MainSharpList.getInstance().setItemQuantity(holder.itemId, itemQuantity);
					
					//Update db
					ContentValues cv = new ContentValues();
					cv.put(SharpCartContentProvider.COLUMN_QUANTITY, itemQuantity);
					
					int count = mActivity.getContentResolver().update(
							SharpCartContentProvider.CONTENT_URI_SHARP_LIST_ITEMS,
							cv,
							SharpCartContentProvider.COLUMN_ID+"="+holder.itemId, 
							null);
					
	    		   //Update main sharp list adapter cursor to reflect the added shopping item
	    		   //updateCursor();
	    		   
				} catch (NumberFormatException ex)
				{
					Log.d(TAG,ex.getMessage());
				}
				
			}
		});
		*/
		
		final int id = Integer.valueOf(holder.itemId);

	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		final View view = mInflater.inflate(R.layout.main_sharp_list_item, parent,false);
		
		ShoppingItemViewContainer holder = new ShoppingItemViewContainer();
		
		//set image name text view
		holder.itemNameTextView = (TextView) view.findViewById(R.id.mainSharpListShoppingItemName);
		//holder.itemDescriptionTextView = (TextView) view.findViewById(R.id.mainSharpListShoppingItemDescription);
		holder.deleteImageButton = (ImageButton) view.findViewById(R.id.mainSharpListShoppingItemDeleteButton);
		holder.itemQuantityEditText = (ShoppingItemQuantityEditText) view.findViewById(R.id.quantityTextInput);
		
		holder.imageView = (ImageView) view.findViewById(R.id.mainSharpListShoppingItemImageView);
	
		
		//Set item quantity from user input
		holder.itemQuantity = Double.valueOf(((EditText) view.findViewById(R.id.quantityTextInput)).getText().toString());
		
		view.setTag(holder);
	
		return view;
	}
	
    static public class ShoppingItemViewContainer {
		public ImageView imageView;
		public TextView itemDescriptionTextView;
		public TextView itemNameTextView;
		public ShoppingItemQuantityEditText itemQuantityEditText;
		public ImageButton deleteImageButton;
	
		public int itemId;
		public String itemName;
		public String itemDescription;
		public int itemUnitId;
		public int itemCategoryId;
		public String itemImageLocation;
		public double itemQuantity;
    }
}
