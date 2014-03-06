package com.sharpcart.android.adapter;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;

import com.sharpcart.android.R;
import com.sharpcart.android.custom.ShoppingItemQuantityEditText;
import com.sharpcart.android.dao.MainSharpListDAO;
import com.sharpcart.android.model.MainSharpList;
import com.sharpcart.android.provider.SharpCartContentProvider;
import com.sharpcart.android.utilities.SharpCartUtilities;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class MainSharpListItemAdapter extends CursorAdapter {
    private final LayoutInflater mInflater;
    private final Activity mActivity;
    private Drawable d;
    
    private static final String TAG = MainSharpListItemAdapter.class.getSimpleName();
    
    public MainSharpListItemAdapter(final Activity activity,final Cursor cursor) {
		super(activity, cursor, false);   	
    	
		mActivity = activity;
		mInflater = LayoutInflater.from(activity);
    }
    
    public void updateCursor()
    {
    	changeCursor(getManagedCursor(mActivity));
    }
    
    
    /*
     * This method will generate our cursor by running a query to retrieve all shopping items 
     * from the main sharp list table
     */
    private static Cursor getManagedCursor(final Activity activity) {
    	Cursor cursor;
    	
		cursor =  activity.getContentResolver().query(
		SharpCartContentProvider.CONTENT_URI_SHARP_LIST_ITEMS,
		null,
		null, 
		null,
		SharpCartContentProvider.DEFAULT_SORT_ORDER);
		
		return cursor;
 
    }
    
	@Override
	public void bindView(final View view, final Context context, final Cursor cursor) {
		
    	//Create a view holder and populate it with information from the database cursor
    	final ShoppingItemViewContainer holder = (ShoppingItemViewContainer) view.getTag();
    	
    	//check to make sure we didnt get a damage item from the db
    	
		//holder.itemNameTextView.setText(cursor.getString(mNameIndex)+"\n"+cursor.getString(mDescriptionIndex));
    	holder.itemNameTextView.setText(cursor.getString(cursor.getColumnIndexOrThrow(SharpCartContentProvider.COLUMN_DESCRIPTION)));
		holder.itemUnitTextView.setText(SharpCartUtilities.getInstance().getUnitName(cursor.getInt(cursor.getColumnIndexOrThrow(SharpCartContentProvider.COLUMN_SHOPPING_ITEM_UNIT_ID))));
		holder.itemQuantityEditText.setText(cursor.getString(cursor.getColumnIndexOrThrow(SharpCartContentProvider.COLUMN_QUANTITY)));
		
		holder.itemId = (cursor.getInt(cursor.getColumnIndexOrThrow(SharpCartContentProvider.COLUMN_ID)));
		holder.itemName = (cursor.getString(cursor.getColumnIndexOrThrow(SharpCartContentProvider.COLUMN_NAME)));
		holder.itemDescription = (cursor.getString(cursor.getColumnIndexOrThrow(SharpCartContentProvider.COLUMN_DESCRIPTION)));
		holder.itemImageLocation = (cursor.getString(cursor.getColumnIndexOrThrow(SharpCartContentProvider.COLUMN_IMAGE_LOCATION)));
		holder.itemQuantity = (cursor.getDouble(cursor.getColumnIndexOrThrow(SharpCartContentProvider.COLUMN_QUANTITY)));
		holder.itemConversionRatio = (cursor.getDouble(cursor.getColumnIndexOrThrow(SharpCartContentProvider.COLUMN_UNIT_TO_ITEM_CONVERSION_RATIO)));
		holder.itemCategoryId = (cursor.getInt(cursor.getColumnIndexOrThrow(SharpCartContentProvider.COLUMN_SHOPPING_ITEM_CATEGORY_ID)));
		holder.itemUnitId = (cursor.getInt(cursor.getColumnIndexOrThrow(SharpCartContentProvider.COLUMN_SHOPPING_ITEM_UNIT_ID)));
		/*
		 * Load images for shopping items from assets folder
		 */
		if (holder.itemImageLocation!=null)
		try {
		    // get input stream
			final String shoppingItemImageLocation = cursor.getString(cursor.getColumnIndexOrThrow(SharpCartContentProvider.COLUMN_IMAGE_LOCATION)).replaceFirst("/", "");
			
		    final InputStream ims = mActivity.getApplicationContext().getAssets().open(shoppingItemImageLocation);
		    
		    // load image as Drawable
		    d = Drawable.createFromStream(ims, null);
		    
		    // set image to ImageView
		    holder.imageView.setImageDrawable(d);
			
			/*Set onClick event for delete button */
			holder.deleteImageButton.setOnClickListener(new OnClickListener()
			{
		    	   @Override
		    	   public void onClick(final View v) 
		    	   {
		    		   
		    		   //use the DAO object to delete shopping item from main sharp list table
		    		   MainSharpListDAO.getInstance().deleteMainSharpListItem(mContext.getContentResolver(), holder.itemId);
		    		   
		    		   //delete shopping item from main sharp list object
		    		   MainSharpList.getInstance().removeShoppingItemFromList(holder.itemId);
		    		   
		    		   Toast.makeText(mContext,holder.itemDescription + " Deleted ",Toast.LENGTH_SHORT).show();
		    	   }
		    });
			
		} catch (final IOException ex) {
		    Log.d("MainSharpListItemAdapter", ex.getLocalizedMessage());
		}
		
		holder.itemQuantityEditText.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(final View v, final MotionEvent event) {
				
				//save current quantity
				if (((EditText)v).getText().length()!=0)
					holder.itemQuantity = Double.valueOf(((EditText)v).getText().toString());
				
				((EditText)v).setText("");
				
				//return false since we want the default behavior to continue
				return false;
			}
		});
		
		holder.itemQuantityEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(final View v, final boolean hasFocus) {
			            
				if (!hasFocus)
				{
					if (((EditText)v).getText().length()!=0)
					try {
							final double itemQuantity = Double.valueOf(((TextView)v).getText().toString());
							
							if (itemQuantity>0)
							{
								//Update MainSharpList object
								MainSharpList.getInstance().setItemQuantity(holder.itemId, itemQuantity);
								
								//Update db
								final ContentValues cv = new ContentValues();
								cv.put(SharpCartContentProvider.COLUMN_QUANTITY, itemQuantity);
								
								mActivity.getContentResolver().update(
										SharpCartContentProvider.CONTENT_URI_SHARP_LIST_ITEMS,
										cv,
										SharpCartContentProvider.COLUMN_ID+"="+holder.itemId, 
										null);
								
								
								//if we changed the original value we update the MainSharpList LastUpdated time stamp
								if (itemQuantity!=holder.itemQuantity)
								{
					    		   //update the MainSharpList last updated time stamp
					    		   MainSharpList.getInstance().setLastUpdated(new Timestamp(System.currentTimeMillis()).toString());
								}
							} else
							{
								((EditText)v).setError("Must be larger than 0");
							}
							
						} catch (final NumberFormatException ex)
						{
							Log.d(TAG,ex.getMessage());
						}
					else {
						//Return the original quantity value to the edit text
						holder.itemQuantityEditText.setText(String.valueOf(holder.itemQuantity));
					}
				}
			}
		});
		
		
		holder.itemQuantityEditText.setOnEditorActionListener(new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(final TextView v, final int actionId, final KeyEvent event) {
				
				boolean handled = false;
				
				//if user clicked on "done" or "next" options
	            if((actionId == EditorInfo.IME_ACTION_NEXT)||(actionId == EditorInfo.IME_ACTION_DONE))
	            {
	            	if (((EditText)v).getText().length()!=0)
					try {
						final double itemQuantity = Double.valueOf(v.getText().toString());
						
						if (itemQuantity>0)
						{
						//Update MainSharpList object
						MainSharpList.getInstance().setItemQuantity(holder.itemId, itemQuantity);
						
						//Update db
						final ContentValues cv = new ContentValues();
						cv.put(SharpCartContentProvider.COLUMN_QUANTITY, itemQuantity);
						
						mActivity.getContentResolver().update(
								SharpCartContentProvider.CONTENT_URI_SHARP_LIST_ITEMS,
								cv,
								SharpCartContentProvider.COLUMN_ID+"="+holder.itemId, 
								null);
								   
						handled = true;
						
						//if we changed the original value we update the MainSharpList LastUpdated time stamp
						if (itemQuantity!=holder.itemQuantity)
						{
			    		   //update the MainSharpList last updated time stamp
			    		   MainSharpList.getInstance().setLastUpdated(new Timestamp(System.currentTimeMillis()).toString());
						}
						} else
						{
							((EditText)v).setError("Must be larger than 0");
						}
						final InputMethodManager imm = (InputMethodManager)mActivity.getSystemService(
							      Context.INPUT_METHOD_SERVICE);
							imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
		    		   
					} catch (final NumberFormatException ex)
					{
						Log.d(TAG,ex.getMessage());
					}
					else {
						//Return the original quantity value to the edit text
						holder.itemQuantityEditText.setText(String.valueOf(holder.itemQuantity));
					}
	            }
	            
				return handled;
			}
		});
	}

	@Override
	public View newView(final Context context, final Cursor cursor, final ViewGroup parent) {
		
		final View view = mInflater.inflate(R.layout.main_sharp_list_item, parent,false);
		
		final ShoppingItemViewContainer holder = new ShoppingItemViewContainer();
		
		//set image name text view
		holder.itemNameTextView = (TextView) view.findViewById(R.id.mainSharpListShoppingItemName);
		holder.itemUnitTextView = (TextView) view.findViewById(R.id.itemUnitText);
		holder.deleteImageButton = (ImageButton) view.findViewById(R.id.mainSharpListShoppingItemDeleteButton);
		holder.itemQuantityEditText = (ShoppingItemQuantityEditText) view.findViewById(R.id.quantityTextInput);
		
		holder.imageView = (ImageView) view.findViewById(R.id.mainSharpListShoppingItemImageView);
		
		//Set item quantity from user input
		holder.itemQuantity = Double.valueOf(((ShoppingItemQuantityEditText) view.findViewById(R.id.quantityTextInput)).getText().toString());
		
		view.setTag(holder);
	
		return view;
	}
	
    static public class ShoppingItemViewContainer {
		public ImageView imageView;
		public TextView itemDescriptionTextView;
		public TextView itemNameTextView;
		public TextView itemUnitTextView;
		public ShoppingItemQuantityEditText itemQuantityEditText;
		//public EditText itemQuantityEditText;
		public ImageButton deleteImageButton;
	
		public int itemId;
		public String itemName;
		public String itemDescription;
		public int itemUnitId;
		public int itemCategoryId;
		public String itemImageLocation;
		public double itemQuantity;
		public double itemConversionRatio;
    }
}
