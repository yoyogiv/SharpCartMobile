package com.sharpcart.android.adapter;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.text.WordUtils;
import com.sharpcart.android.R;
import com.sharpcart.android.dao.MainSharpListDAO;
import com.sharpcart.android.fragment.StoreSharpListFragment;
import com.sharpcart.android.model.MainSharpList;
import com.sharpcart.android.model.ShoppingItem;
import com.sharpcart.android.provider.SharpCartContentProvider;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.OnEditorActionListener;

public class StoreSharpListItemAdapter extends ArrayAdapter<ShoppingItem> {
	
	private final Activity mActivity;
	private List<ShoppingItem> mShoppingItems;
	private Drawable d;
	private DecimalFormat df;
	
    private static final String[] PROJECTION_IMAGELOCATION = new String[] {
	    SharpCartContentProvider.COLUMN_ID,
	    SharpCartContentProvider.COLUMN_IMAGE_LOCATION,};
    
	public StoreSharpListItemAdapter(final Activity context,
			List<ShoppingItem> shoppingItems) {
		super(context, R.layout.store_sharp_list, shoppingItems);
		
		mActivity = context;
		mShoppingItems = shoppingItems;
		df = new DecimalFormat("#,###,##0.00");
	}

    @Override
    public View getView(final int position, final View view, final ViewGroup parent) {
	    final StoreItemViewContainer viewContainer;
		View rowView = view;

		// ---if the row is displayed for the first time---
		if (rowView == null) {
	
		    final LayoutInflater inflater = mActivity.getLayoutInflater();
		    rowView = inflater.inflate(R.layout.store_sharp_list_item, null, true);
	
		    // ---create a view container object---
		    viewContainer = new StoreItemViewContainer();
	
		    // ---get the references to all the views in the row---
		    viewContainer.imageView = (ImageView) rowView.findViewById(R.id.storeListItemImageView);
		    viewContainer.itemDescriptionTextView = (TextView) rowView.findViewById(R.id.description);
		    viewContainer.itemQuantityEditText = (EditText) rowView.findViewById(R.id.quantity);
		    //viewContainer.itemPackageSizeEditText = (EditText) rowView.findViewById(R.id.packageSize);
		    //viewContainer.itemUnitTextView = (TextView) rowView.findViewById(R.id.unit);
		    viewContainer.itemPriceEditText = (EditText) rowView.findViewById(R.id.price);
		    viewContainer.checkBox = (ImageButton) rowView.findViewById(R.id.checkBox);
		    
		    // ---assign the view container to the rowView---
		    rowView.setTag(viewContainer);
	
			} else {
		
			    // ---view was previously created; can recycle---
			    // ---retrieve the previously assigned tag to get
			    // a reference to all the views; bypass the findViewByID() process,
			    // which is computationally expensive---
			    viewContainer = (StoreItemViewContainer) rowView.getTag();
			}
		
			// ---customize the content of each row based on position---
			viewContainer.itemDescriptionTextView.setText(WordUtils.capitalize(getItem(position).getDescription())+"\n"+
					"("+getItem(position).getPackage_quantity()+" "+getItem(position).getUnit()+")");
			viewContainer.itemQuantityEditText.setText(df.format(getItem(position).getQuantity()/getItem(position).getPackage_quantity()));
			//viewContainer.itemUnitTextView.setText("("+getItem(position).getPackage_quantity()+" "+getItem(position).getUnit()+")");
			//viewContainer.itemPackageSizeEditText.setText(String.valueOf(getItem(position).getPackage_quantity()));
			viewContainer.itemPriceEditText.setText(df.format(
					(getItem(position).getPackage_quantity()*getItem(position).getPrice_per_unit())/(getItem(position).getQuantity()/getItem(position).getPackage_quantity())));
			
			viewContainer.checkBox.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(final View v) {
					double itemTotalCost = viewContainer.itemPrice*viewContainer.itemQuantity;

					//update total cost text view
					((StoreSharpListFragment)((FragmentActivity)mActivity).
							getSupportFragmentManager().
							findFragmentByTag("storeSharpListFragment")).updateTotalCost(itemTotalCost);
					
					//move item to in-cart grid
					((StoreSharpListFragment)((FragmentActivity)mActivity).
							getSupportFragmentManager().
							findFragmentByTag("storeSharpListFragment")).moveItemToCart(getItem(position));
					
					//remove the item from our adapter
					removeItem(getItem(position));			
				}
			});
			
			/*
			 * Load images for shopping items from assets folder
			 */
			final String shoppingItemImageLocation;
			
			if (getItem(position).getImage_location()==null)
			{
				shoppingItemImageLocation = getShoppingItemImageLocationFromDatabase(getItem(position).getId()).replaceFirst("/", "");
			} else //one of the extra items
			{
				shoppingItemImageLocation = getItem(position).getImage_location().replaceFirst("/", "");	
			}
			
			try {
			    // get input stream				
			    final InputStream ims = mActivity.getApplicationContext().getAssets().open(shoppingItemImageLocation);
			    
			    // load image as Drawable
			    d = Drawable.createFromStream(ims, null);
			    
			    // set image to ImageView
			    viewContainer.imageView.setImageDrawable(d);
				
				
			} catch (final IOException ex) {
			    Log.d("StoreSharpListItemAdapter", ex.getLocalizedMessage());
			}
			
			viewContainer.itemQuantityEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
				
				@Override
				public void onFocusChange(final View v, final boolean hasFocus) {
				            
					if (!hasFocus)
					{
						
						try {
								final double itemQuantity = Double.valueOf(((TextView)v).getText().toString());
								
								//Update item quantity
								viewContainer.itemQuantity = itemQuantity;
								getItem(position).setQuantity(itemQuantity);

							} catch (final NumberFormatException ex)
							{
								Log.d("StoreSharpListItemAdapter",ex.getMessage());
							}
					}
				}
			});
			
			
			viewContainer.itemQuantityEditText.setOnEditorActionListener(new OnEditorActionListener() {
				
				@Override
				public boolean onEditorAction(final TextView v, final int actionId, final KeyEvent event) {
					
					boolean handled = false;
					
					//if user clicked on "done" or "next" options
		            if((actionId == EditorInfo.IME_ACTION_NEXT)||(actionId == EditorInfo.IME_ACTION_DONE))
		            {
						try {
							final double itemQuantity = Double.valueOf(v.getText().toString());
							
							//Update item quantity
							viewContainer.itemQuantity = itemQuantity;
							getItem(position).setQuantity(itemQuantity);
							
							final InputMethodManager imm = (InputMethodManager)mActivity.getSystemService(
								      Context.INPUT_METHOD_SERVICE);
								imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
			    		   
						} catch (final NumberFormatException ex)
						{
							Log.d("StoreSharpListItemAdapter",ex.getMessage());
						}
		            }
		            
					return handled;
				}
			});
			
			viewContainer.itemPriceEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
				
				@Override
				public void onFocusChange(final View v, final boolean hasFocus) {
				            
					if (!hasFocus)
					{
						
						try {
								final double itemPrice = Double.valueOf(((TextView)v).getText().toString());
								
								//Update item quantity
								viewContainer.itemPrice = itemPrice;
								getItem(position).setPrice(itemPrice);
								
							} catch (final NumberFormatException ex)
							{
								Log.d("StoreSharpListItemAdapter",ex.getMessage());
							}
					}
				}
			});
			
			
			viewContainer.itemPriceEditText.setOnEditorActionListener(new OnEditorActionListener() {
				
				@Override
				public boolean onEditorAction(final TextView v, final int actionId, final KeyEvent event) {
					
					boolean handled = false;
					
					//if user clicked on "done" or "next" options
		            if((actionId == EditorInfo.IME_ACTION_NEXT)||(actionId == EditorInfo.IME_ACTION_DONE))
		            {
						try {
							final double itemPrice = Double.valueOf(v.getText().toString());
							
							//Update item quantity
							viewContainer.itemPrice = itemPrice;
							getItem(position).setPrice(itemPrice);
							
							final InputMethodManager imm = (InputMethodManager)mActivity.getSystemService(
								      Context.INPUT_METHOD_SERVICE);
								imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
			    		   
						} catch (final NumberFormatException ex)
						{
							Log.d("StoreSharpListItemAdapter",ex.getMessage());
						}
		            }
		            
					return handled;
				}
			});
			
			// Save our item information so we can use it later when we update items	
			viewContainer.itemName = getItem(position).getName();
			viewContainer.itemDescription = getItem(position).getDescription();
			viewContainer.itemPrice = (getItem(position).getPackage_quantity()*getItem(position).getPrice_per_unit())/(getItem(position).getQuantity()/getItem(position).getPackage_quantity());
			viewContainer.itemQuantity = getItem(position).getQuantity()/getItem(position).getPackage_quantity();
			viewContainer.itemPackageSize = getItem(position).getPackage_quantity();
			viewContainer.itemImageLocation = getItem(position).getImage_location();
			viewContainer.itemUnit = getItem(position).getUnit();
			viewContainer.itemId = getItem(position).getId();
			viewContainer.itemImageLocation = getItem(position).getImage_location();
		
			//sort
			sort();
			
			return rowView;
    }
	
    private void removeItem(ShoppingItem item)
    {
    	remove(item);
    	notifyDataSetChanged();
    }
    
    private String getShoppingItemImageLocationFromDatabase(int itemId)
    {
    	Cursor cursor;
    	
		cursor =  mActivity.getContentResolver().query(
				SharpCartContentProvider.CONTENT_URI_SHOPPING_ITEMS,
				PROJECTION_IMAGELOCATION,
				SharpCartContentProvider.COLUMN_ID + "='"+itemId+"'", 
				null,
				SharpCartContentProvider.DEFAULT_SORT_ORDER);
		
		cursor.moveToFirst();
		
		String imageLocation = cursor.getString(cursor.getColumnIndexOrThrow(SharpCartContentProvider.COLUMN_IMAGE_LOCATION));
		
		//close cursor
		cursor.close();
		
		return imageLocation;
    }
    
    public void sort() {
        Collections.sort(mShoppingItems, new Comparator<ShoppingItem>() {                
            @Override
            public int compare(ShoppingItem item1, ShoppingItem item2) {
                return item1.getDescription().compareTo(item2.getDescription());
            }
        });
    }
    
	//a class view container for our store sharp list items
    static public class StoreItemViewContainer {
		public ImageView imageView;
		public TextView itemDescriptionTextView;
		public TextView itemUnitTextView;
		public EditText itemQuantityEditText;
		public EditText itemPackageSizeEditText;
		public EditText itemPriceEditText;
		public ImageButton checkBox;
		
		public int itemId;
		public String itemName;
		public String itemDescription;
		public int itemUnitId;
		public String itemUnit;
		public int itemCategoryId;
		public String itemImageLocation;
		public double itemQuantity;
		public double itemPackageSize;
		public double itemPrice;
    }
}
