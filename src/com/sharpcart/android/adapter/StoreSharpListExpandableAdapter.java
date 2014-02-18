package com.sharpcart.android.adapter;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.text.WordUtils;

import com.sharpcart.android.R;
import com.sharpcart.android.fragment.StoreSharpListFragment;
import com.sharpcart.android.model.ShoppingItem;
import com.sharpcart.android.provider.SharpCartContentProvider;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class StoreSharpListExpandableAdapter extends BaseExpandableListAdapter {
	
	private Activity mActivity;
	private List<String> mListDataHeader;
	private HashMap<String, List<ShoppingItem>> mListChildData;
	private Drawable d;
	private final DecimalFormat df;
	
    private static final String[] PROJECTION_IMAGELOCATION = new String[] {
	    SharpCartContentProvider.COLUMN_ID,
	    SharpCartContentProvider.COLUMN_IMAGE_LOCATION,};
    
	public StoreSharpListExpandableAdapter(Activity activity, List<String> listDataHeader,
            HashMap<String, List<ShoppingItem>> listChildData) {
        mActivity = activity;
        mListDataHeader = listDataHeader;
        mListChildData = listChildData;
        
        df = new DecimalFormat("#,###,##0.00");
    }

	@Override
	public Object getChild(int groupPosition, int childPosition) {
        return mListChildData.get(mListDataHeader.get(groupPosition)).get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView(final int groupPosition, final int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		
        final ShoppingItem shoppingItem = (ShoppingItem) getChild(groupPosition, childPosition);
        final StoreItemViewContainer viewContainer;
        View rowView = convertView;
        
        if (rowView == null) {
            LayoutInflater infalInflater =  mActivity.getLayoutInflater();
            rowView = infalInflater.inflate(R.layout.store_sharp_list_item, null);
            
		    // ---create a view container object---
		    viewContainer = new StoreItemViewContainer();
	
		    // ---get the references to all the views in the row---
		    viewContainer.imageView = (ImageView) rowView.findViewById(R.id.storeListItemImageView);
		    viewContainer.itemDescriptionTextView = (TextView) rowView.findViewById(R.id.description);
		    viewContainer.itemQuantityEditText = (EditText) rowView.findViewById(R.id.quantity);
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
			viewContainer.itemDescriptionTextView.setText(WordUtils.capitalize(shoppingItem.getDescription())+"\n"+
					"("+shoppingItem.getPackage_quantity()+" "+shoppingItem.getUnit()+")");
			
			viewContainer.itemQuantityEditText.setText(df.format(shoppingItem.getQuantity()));

			viewContainer.itemPriceEditText.setText(df.format(shoppingItem.getPrice()));
				
			viewContainer.checkBox.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(final View v) {
					
					try {
						final double itemQuantity = Double.valueOf(viewContainer.itemQuantityEditText.getText().toString());
						
						//Update item quantity
						viewContainer.itemQuantity = itemQuantity;
						shoppingItem.setQuantity(itemQuantity);
						
						final double itemPrice = Double.valueOf(viewContainer.itemPriceEditText.getText().toString());
						
						//Update item quantity
						viewContainer.itemPrice = itemPrice;
						shoppingItem.setPrice(itemPrice);
						
					} catch (final NumberFormatException ex)
					{
						Log.d("StoreSharpListItemAdapter",ex.getMessage());
					}
					
					final double itemTotalCost = viewContainer.itemPrice*viewContainer.itemQuantity;
					
					//update total cost text view
					((StoreSharpListFragment)((FragmentActivity)mActivity).
							getSupportFragmentManager().
							findFragmentByTag("storeSharpListFragment")).updateTotalCost(itemTotalCost);
					
					//move item to in-cart grid
					mListChildData.get("In Cart").add(shoppingItem);
					
					//close soft keyboard
	                InputMethodManager inputManager = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
	                if (mActivity.getWindow().getCurrentFocus() != null) {
	                    inputManager.hideSoftInputFromWindow(mActivity.getWindow().getCurrentFocus().getWindowToken(), 0);
	                }
	                
					//remove the item from its category
	                mListChildData.get(WordUtils.capitalizeFully(shoppingItem.getCategory())).remove(shoppingItem);
	                
	                notifyDataSetChanged();
				}
			});
			
			/*
			 * Load images for shopping items from assets folder
			 */
			final String shoppingItemImageLocation;
			
			if (shoppingItem.getImage_location()==null)
			{
				shoppingItemImageLocation = getShoppingItemImageLocationFromDatabase(shoppingItem.getId()).replaceFirst("/", "");
			} else //one of the extra items
			{
				shoppingItemImageLocation = shoppingItem.getImage_location().replaceFirst("/", "");	
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
								shoppingItem.setQuantity(itemQuantity);

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
					
					final boolean handled = false;
					
					//if user clicked on "done" or "next" options
		            if((actionId == EditorInfo.IME_ACTION_NEXT)||(actionId == EditorInfo.IME_ACTION_DONE))
		            {
						try {
							final double itemQuantity = Double.valueOf(v.getText().toString());
							
							//Update item quantity
							viewContainer.itemQuantity = itemQuantity;
							shoppingItem.setQuantity(itemQuantity);
							
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
								shoppingItem.setPrice(itemPrice);
								
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
					
					final boolean handled = false;
					
					//if user clicked on "done" or "next" options
		            if((actionId == EditorInfo.IME_ACTION_NEXT)||(actionId == EditorInfo.IME_ACTION_DONE))
		            {
						try {
							final double itemPrice = Double.valueOf(v.getText().toString());
							
							//Update item quantity
							viewContainer.itemPrice = itemPrice;
							shoppingItem.setPrice(itemPrice);
							
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
			viewContainer.itemName = shoppingItem.getName();
			viewContainer.itemDescription = shoppingItem.getDescription();

			viewContainer.itemPrice = shoppingItem.getPrice();
			viewContainer.itemQuantity = shoppingItem.getQuantity();
			
			viewContainer.itemPackageSize = shoppingItem.getPackage_quantity();
			viewContainer.itemImageLocation = shoppingItem.getImage_location();
			viewContainer.itemUnit = shoppingItem.getUnit();
			viewContainer.itemId = shoppingItem.getId();
			viewContainer.itemImageLocation = shoppingItem.getImage_location();
			  
        return rowView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
        return mListChildData.get(mListDataHeader.get(groupPosition)).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return mListDataHeader.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return mListDataHeader.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = mActivity.getLayoutInflater();
            convertView = infalInflater.inflate(R.layout.in_store_list_groups, null);
        }
 
        TextView categoryTitle = (TextView) convertView.findViewById(R.id.inStoreCategoryHeader);
        categoryTitle.setTypeface(null, Typeface.BOLD);
        categoryTitle.setText(headerTitle);
 
        return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
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
