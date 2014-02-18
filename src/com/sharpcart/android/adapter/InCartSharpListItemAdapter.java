package com.sharpcart.android.adapter;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.text.WordUtils;

import com.sharpcart.android.R;
import com.sharpcart.android.fragment.StoreSharpListFragment;
import com.sharpcart.android.model.ShoppingItem;
import com.sharpcart.android.provider.SharpCartContentProvider;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class InCartSharpListItemAdapter extends ArrayAdapter<ShoppingItem> {
	
	private final Activity mActivity;
	private final List<ShoppingItem> mShoppingItems;
	private Drawable d;
	private final DecimalFormat df;
	
    private static final String[] PROJECTION_IMAGELOCATION = new String[] {
	    SharpCartContentProvider.COLUMN_ID,
	    SharpCartContentProvider.COLUMN_IMAGE_LOCATION,};
    
	public InCartSharpListItemAdapter(final Activity context,
			final List<ShoppingItem> shoppingItems) {
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
		    rowView = inflater.inflate(R.layout.store_in_cart_sharp_list_item, null, true);
	
		    // ---create a view container object---
		    viewContainer = new StoreItemViewContainer();
	
		    // ---get the references to all the views in the row---
		    viewContainer.imageView = (ImageView) rowView.findViewById(R.id.storeListItemImageView);
		    viewContainer.itemDescriptionTextView = (TextView) rowView.findViewById(R.id.description);
		    viewContainer.itemQuantityEditText = (TextView) rowView.findViewById(R.id.quantity);
		    viewContainer.itemPriceEditText = (TextView) rowView.findViewById(R.id.price);
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
			viewContainer.itemQuantityEditText.setText(df.format(getItem(position).getQuantity()));
			viewContainer.itemPriceEditText.setText(df.format(getItem(position).getPrice()));
						
			viewContainer.checkBox.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(final View v) {
					final double itemTotalCost = viewContainer.itemPrice*viewContainer.itemQuantity;
					
					//update total cost text view
					((StoreSharpListFragment)((FragmentActivity)mActivity).
							getSupportFragmentManager().
							findFragmentByTag("storeSharpListFragment")).updateTotalCost(itemTotalCost*(-1));
					
					//move item to in cart grid
					/*
					((StoreSharpListFragment)((FragmentActivity)mActivity).
							getSupportFragmentManager().
							findFragmentByTag("storeSharpListFragment")).moveItemOutOfCart(getItem(position));
					*/
					
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
			
			// Save our item information so we can use it later when we update items	
			viewContainer.itemName = getItem(position).getName();
			viewContainer.itemDescription = getItem(position).getDescription();
			viewContainer.itemPrice = getItem(position).getPrice();
			viewContainer.itemQuantity = getItem(position).getQuantity();
			viewContainer.itemPackageSize = getItem(position).getPackage_quantity();
			viewContainer.itemImageLocation = getItem(position).getImage_location();
			viewContainer.itemUnit = getItem(position).getUnit();
			viewContainer.itemId = getItem(position).getId();
			viewContainer.itemImageLocation = getItem(position).getImage_location();
		
			//sort
			sort();
			
			return rowView;
    }
	
    private void removeItem(final ShoppingItem item)
    {
    	remove(item);
    	notifyDataSetChanged();
    }
    
    private String getShoppingItemImageLocationFromDatabase(final int itemId)
    {
    	Cursor cursor;
    	
		cursor =  mActivity.getContentResolver().query(
				SharpCartContentProvider.CONTENT_URI_SHOPPING_ITEMS,
				PROJECTION_IMAGELOCATION,
				SharpCartContentProvider.COLUMN_ID + "='"+itemId+"'", 
				null,
				SharpCartContentProvider.DEFAULT_SORT_ORDER);
		
		cursor.moveToFirst();
		
		final String imageLocation = cursor.getString(cursor.getColumnIndexOrThrow(SharpCartContentProvider.COLUMN_IMAGE_LOCATION));
		
		//close cursor
		cursor.close();
		
		return imageLocation;
    }
    
    public void sort() {
        Collections.sort(mShoppingItems, new Comparator<ShoppingItem>() {                
            @Override
            public int compare(final ShoppingItem item1, final ShoppingItem item2) {
                return item1.getDescription().compareTo(item2.getDescription());
            }
        });
    }
    
	//a class view container for our store sharp list items
    static public class StoreItemViewContainer {
		public ImageView imageView;
		public TextView itemDescriptionTextView;
		public TextView itemUnitTextView;
		public TextView itemQuantityEditText;
		public TextView itemPackageSizeEditText;
		public TextView itemPriceEditText;
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
