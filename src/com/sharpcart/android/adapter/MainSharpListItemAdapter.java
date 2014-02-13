package com.sharpcart.android.adapter;

import java.io.IOException;
import java.io.InputStream;
import com.sharpcart.android.R;
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
import android.view.View;
import android.view.View.OnFocusChangeListener;
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
    private final Cursor c;
    private Drawable d;
    
    private  final int mNameIndex;
    private  final int mDescriptionIndex;
    private  final int mIdIndex;
    private  final int mUnitIdIndex;
    private  final int mCategoryIdIndex;
    private  final int mImageLocationIndex;
    private  final int mQuantityIndex;

    private static final String[] PROJECTION_ID_NAME_DESCRIPTION_CATEGORYID_UNITID_IMAGELOCATION_QUANTITY = new String[] {
	    SharpCartContentProvider.COLUMN_ID,
	    SharpCartContentProvider.COLUMN_NAME,
	    SharpCartContentProvider.COLUMN_DESCRIPTION,
	    SharpCartContentProvider.COLUMN_SHOPPING_ITEM_CATEGORY_ID,
	    SharpCartContentProvider.COLUMN_SHOPPING_ITEM_UNIT_ID,
	    SharpCartContentProvider.COLUMN_IMAGE_LOCATION,
	    SharpCartContentProvider.COLUMN_QUANTITY};
    
    private static final String TAG = MainSharpListItemAdapter.class.getSimpleName();
    
    public MainSharpListItemAdapter(final Activity activity) {
		super(activity, getManagedCursor(activity), false);   	
    	//super(activity, null, false);
    	
		c = getCursor();

		mActivity = activity;
		mInflater = LayoutInflater.from(activity);
		
		mIdIndex = c.getColumnIndexOrThrow(SharpCartContentProvider.COLUMN_ID);	
		mNameIndex = c.getColumnIndexOrThrow(SharpCartContentProvider.COLUMN_NAME);
		mDescriptionIndex = c.getColumnIndexOrThrow(SharpCartContentProvider.COLUMN_DESCRIPTION);
		mUnitIdIndex = c.getColumnIndexOrThrow(SharpCartContentProvider.COLUMN_SHOPPING_ITEM_UNIT_ID);
		mCategoryIdIndex = c.getColumnIndexOrThrow(SharpCartContentProvider.COLUMN_SHOPPING_ITEM_CATEGORY_ID);
		mImageLocationIndex = c.getColumnIndexOrThrow(SharpCartContentProvider.COLUMN_IMAGE_LOCATION);
		mQuantityIndex = c.getColumnIndexOrThrow(SharpCartContentProvider.COLUMN_QUANTITY);
		
		c.close();
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
		PROJECTION_ID_NAME_DESCRIPTION_CATEGORYID_UNITID_IMAGELOCATION_QUANTITY,
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
    	holder.itemNameTextView.setText(cursor.getString(mDescriptionIndex));
		holder.itemUnitTextView.setText(SharpCartUtilities.getInstance().getUnitName(cursor.getInt(mUnitIdIndex)));
		holder.itemQuantityEditText.setText(cursor.getString(mQuantityIndex));
		
		holder.itemId = (cursor.getInt(mIdIndex));
		holder.itemName = (cursor.getString(mNameIndex));
		holder.itemDescription = (cursor.getString(mDescriptionIndex));
		holder.itemImageLocation = (cursor.getString(mImageLocationIndex));
		holder.itemQuantity = (cursor.getDouble(mQuantityIndex));
		
		holder.itemCategoryId = (cursor.getInt(mCategoryIdIndex));
		holder.itemUnitId = (cursor.getInt(mUnitIdIndex));
		/*
		 * Load images for shopping items from assets folder
		 */
		if (holder.itemImageLocation!=null)
		try {
		    // get input stream
			final String shoppingItemImageLocation = cursor.getString(mImageLocationIndex).replaceFirst("/", "");
			
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
		    		   
		    		   //Update main sharp list adapter cursor to reflect the added shopping item
		    		   updateCursor();
		    		   
		    		   Toast.makeText(mContext,holder.itemDescription + " Deleted ",Toast.LENGTH_SHORT).show();
		    	   }
		    });
			
		} catch (final IOException ex) {
		    Log.d("MainSharpListItemAdapter", ex.getLocalizedMessage());
		}
		
		holder.itemQuantityEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(final View v, final boolean hasFocus) {
			            
				if (!hasFocus)
				{
					
					try {
							final double itemQuantity = Double.valueOf(((TextView)v).getText().toString());
							
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
		    		   
						} catch (final NumberFormatException ex)
						{
							Log.d(TAG,ex.getMessage());
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
					try {
						final double itemQuantity = Double.valueOf(v.getText().toString());
						
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
						
						final InputMethodManager imm = (InputMethodManager)mActivity.getSystemService(
							      Context.INPUT_METHOD_SERVICE);
							imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
		    		   
					} catch (final NumberFormatException ex)
					{
						Log.d(TAG,ex.getMessage());
					}
	            }
	            
				return handled;
			}
		});
		
		
		/*
		holder.itemQuantityEditText.addTextChangedListener(new TextWatcher()
	    {
	        @Override
	        public void onTextChanged(CharSequence s, int start, int before, int count)
	        {
	            
	        }

	        @Override
	        public void beforeTextChanged(CharSequence s, int start, int count, int after)
	        {
	            
	        }

	        @Override
	        public void afterTextChanged(Editable s)
	        {
	        	double itemQuantity = 1;
	        	boolean validQuantity = true;
	        	
	        	try {
					 itemQuantity = Double.valueOf(s.toString());
	        	} catch (NumberFormatException ex)
	        	{
	        		validQuantity = false;
	        	}
	        	
	        	if (validQuantity)
	        	{
					//Update MainSharpList object
					MainSharpList.getInstance().setItemQuantity(holder.itemId, itemQuantity);
					
					//Update db
					ContentValues cv = new ContentValues();
					cv.put(SharpCartContentProvider.COLUMN_QUANTITY, itemQuantity);
					
					mActivity.getContentResolver().update(
							SharpCartContentProvider.CONTENT_URI_SHARP_LIST_ITEMS,
							cv,
							SharpCartContentProvider.COLUMN_ID+"="+holder.itemId, 
							null);
					
	        	}
	        }
	    });
	    */
	}

	@Override
	public View newView(final Context context, final Cursor cursor, final ViewGroup parent) {
		
		final View view = mInflater.inflate(R.layout.main_sharp_list_item, parent,false);
		
		final ShoppingItemViewContainer holder = new ShoppingItemViewContainer();
		
		//set image name text view
		holder.itemNameTextView = (TextView) view.findViewById(R.id.mainSharpListShoppingItemName);
		holder.itemUnitTextView = (TextView) view.findViewById(R.id.itemUnitText);
		holder.deleteImageButton = (ImageButton) view.findViewById(R.id.mainSharpListShoppingItemDeleteButton);
		//holder.itemQuantityEditText = (ShoppingItemQuantityEditText) view.findViewById(R.id.quantityTextInput);
		holder.itemQuantityEditText = (EditText) view.findViewById(R.id.quantityTextInput);
		
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
		public TextView itemUnitTextView;
		//public ShoppingItemQuantityEditText itemQuantityEditText;
		public EditText itemQuantityEditText;
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
