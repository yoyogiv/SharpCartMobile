package com.sharpcart.android.adapter;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
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
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class StoreSharpListExpandableAdapter extends BaseExpandableListAdapter {
	
	private final Activity mActivity;
	private final List<String> mListDataHeader;
	private final HashMap<String, List<ShoppingItem>> mListChildData;
	private Drawable d;
	private final DecimalFormat df;
	
    private static final String[] PROJECTION_IMAGELOCATION = new String[] {
	    SharpCartContentProvider.COLUMN_ID,
	    SharpCartContentProvider.COLUMN_IMAGE_LOCATION,};
    
	public StoreSharpListExpandableAdapter(final Activity activity, final List<String> listDataHeader,
            final HashMap<String, List<ShoppingItem>> listChildData) {
        mActivity = activity;
        mListDataHeader = listDataHeader;
        mListChildData = listChildData;
        
        df = new DecimalFormat("#,###,##0.00");
    }

	@Override
	public Object getChild(final int groupPosition, final int childPosition) {
        return mListChildData.get(mListDataHeader.get(groupPosition)).get(childPosition);
	}

	@Override
	public long getChildId(final int groupPosition, final int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView(final int groupPosition, final int childPosition,
			final boolean isLastChild, final View convertView, final ViewGroup parent) {
		
        final ShoppingItem shoppingItem = (ShoppingItem) getChild(groupPosition, childPosition);
        
        View rowView = convertView;
        
        if (!shoppingItem.isIn_cart())
        {
        	final StoreItemViewContainer viewContainer;
        	
       		// ---if the row is displayed for the first time---
        	String className = "";
        	if (rowView!=null)
        		className = rowView.getTag().getClass().getName();
        	
    		if ((rowView == null) || (className.equalsIgnoreCase("com.sharpcart.android.adapter.StoreSharpListExpandableAdapter$InCartStoreItemViewContainer")))
	        {
	            final LayoutInflater infalInflater =  mActivity.getLayoutInflater();
	            
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
		
			} else 
			{
			
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
						
						//move item to in-cart category
						shoppingItem.setIn_cart(true);
						mListChildData.get("In Cart").add(shoppingItem);
						
						//close soft keyboard
		                final InputMethodManager inputManager = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
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
				
				viewContainer.itemQuantityEditText.setOnTouchListener(new OnTouchListener() {
					
					@Override
					public boolean onTouch(final View v, final MotionEvent event) {
						
						//save current quantity
						if (((EditText)v).getText().length()!=0)
							viewContainer.itemQuantity = Double.valueOf(((EditText)v).getText().toString());
						
						((EditText)v).setText("");
						
						//return false since we want the default behavior to continue
						return false;
					}
				});
				
				viewContainer.itemQuantityEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
					
					@Override
					public void onFocusChange(final View v, final boolean hasFocus) {
					            
						if (!hasFocus)
						{
							if (((EditText)v).getText().length()!=0)
							try {
									final double itemQuantity = Double.valueOf(((EditText)v).getText().toString());
									
									//Update item quantity
									viewContainer.itemQuantity = itemQuantity;
									shoppingItem.setQuantity(itemQuantity);
	
								} catch (final NumberFormatException ex)
								{
									Log.d("StoreSharpListItemAdapter",ex.getMessage());
								}
							else {
								//Return the original quantity value to the edit text
								viewContainer.itemQuantityEditText.setText(String.valueOf(viewContainer.itemQuantity));
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
			            	if (((EditText)v).getText().length()!=0)
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
			            	else {
								//Return the original quantity value to the edit text
								viewContainer.itemQuantityEditText.setText(String.valueOf(viewContainer.itemQuantity));
							}
			            }
			            
						return handled;
					}
				});
				
				viewContainer.itemPriceEditText.setOnTouchListener(new OnTouchListener() {
					
					@Override
					public boolean onTouch(final View v, final MotionEvent event) {
						
						//save current price
						if (((EditText)v).getText().length()!=0)
							viewContainer.itemPrice = Double.valueOf(((EditText)v).getText().toString());
						
						((EditText)v).setText("");
						
						//return false since we want the default behavior to continue
						return false;
					}
				});
				
				
				viewContainer.itemPriceEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
					
					@Override
					public void onFocusChange(final View v, final boolean hasFocus) {
					            
						if (!hasFocus)
						{
							if (((EditText)v).getText().length()!=0)
							try {
									final double itemPrice = Double.valueOf(((TextView)v).getText().toString());
									
									//Update item quantity
									viewContainer.itemPrice = itemPrice;
									shoppingItem.setPrice(itemPrice);
									
								} catch (final NumberFormatException ex)
								{
									Log.d("StoreSharpListItemAdapter",ex.getMessage());
								}
			            	else {
								//Return the original price value to the edit text
								viewContainer.itemPriceEditText.setText(String.valueOf(viewContainer.itemPrice));
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
			            	if (((EditText)v).getText().length()!=0)
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
			            	else {
								//Return the original price value to the edit text
								viewContainer.itemPriceEditText.setText(String.valueOf(viewContainer.itemPrice));
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
	        
        } else //this item is in the cart
        {
        	final InCartStoreItemViewContainer viewContainer;
        	
    		// ---if the row is displayed for the first time---
        	String className = "";
        	if (rowView!=null)
        		className = rowView.getTag().getClass().getName();
        	
    		if ((rowView == null) || (className.equalsIgnoreCase("com.sharpcart.android.adapter.StoreSharpListExpandableAdapter$StoreItemViewContainer")))
    		{
    	
    		    final LayoutInflater inflater = mActivity.getLayoutInflater();
    		    rowView = inflater.inflate(R.layout.store_in_cart_sharp_list_item, null, true);
    	
    		    // ---create a view container object---
    		    viewContainer = new InCartStoreItemViewContainer();
    	
    		    // ---get the references to all the views in the row---
    		    viewContainer.imageView = (ImageView) rowView.findViewById(R.id.storeListItemImageView);
    		    viewContainer.itemDescriptionTextView = (TextView) rowView.findViewById(R.id.description);
    		    viewContainer.itemQuantityEditText = (TextView) rowView.findViewById(R.id.quantity);
    		    viewContainer.itemPriceEditText = (TextView) rowView.findViewById(R.id.price);
    		    viewContainer.checkBox = (ImageButton) rowView.findViewById(R.id.checkBox);
    		    
    		    // ---assign the view container to the rowView---
    		    rowView.setTag(viewContainer);
    	
    		} else 
    		{
    		
			    // ---view was previously created; can recycle---
			    // ---retrieve the previously assigned tag to get
			    // a reference to all the views; bypass the findViewByID() process,
			    // which is computationally expensive---
			    viewContainer = (InCartStoreItemViewContainer) rowView.getTag();
    		}
    		
    			// ---customize the content of each row based on position---
    			viewContainer.itemDescriptionTextView.setText(WordUtils.capitalize(shoppingItem.getDescription())+"\n"+
    				"("+shoppingItem.getPackage_quantity()+" "+shoppingItem.getUnit()+")");			
    			viewContainer.itemQuantityEditText.setText(df.format(shoppingItem.getQuantity()));
    			viewContainer.itemPriceEditText.setText(df.format(shoppingItem.getPrice()));
    						
    			viewContainer.checkBox.setOnClickListener(new OnClickListener() {
    				
    				@Override
    				public void onClick(final View v) {
    					final double itemTotalCost = viewContainer.itemPrice*viewContainer.itemQuantity;
    					
    					//update total cost text view
    					((StoreSharpListFragment)((FragmentActivity)mActivity).
    							getSupportFragmentManager().
    							findFragmentByTag("storeSharpListFragment")).updateTotalCost(itemTotalCost*(-1));
    					
    					//Return item to list
    					shoppingItem.setIn_cart(false);
    					
    					//if the category still has items in it we can just add the item back
    					//but if it is empty we have to create a new List<ShoppingItem>
    					final String itemCategoryName = WordUtils.capitalizeFully(shoppingItem.getCategory());
    					
    					if (mListChildData.get(itemCategoryName)!=null)
    					{
    						mListChildData.get(itemCategoryName).add(shoppingItem);;
    					}
    					else
    					{
    						final List<ShoppingItem> itemCategory = new ArrayList<ShoppingItem>();
    						itemCategory.add(shoppingItem);
    						mListChildData.put(itemCategoryName, itemCategory);
    					}
    						
						//remove the item from in-cart category
		                mListChildData.get("In Cart").remove(shoppingItem);
		                
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
	}

	@Override
	public int getChildrenCount(final int groupPosition) {
        return mListChildData.get(mListDataHeader.get(groupPosition)).size();
	}

	@Override
	public Object getGroup(final int groupPosition) {
		return mListDataHeader.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return mListDataHeader.size();
	}

	@Override
	public long getGroupId(final int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(final int groupPosition, final boolean isExpanded,
			View convertView, final ViewGroup parent) {
        final String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            final LayoutInflater infalInflater = mActivity.getLayoutInflater();
            convertView = infalInflater.inflate(R.layout.in_store_list_groups, null);
        }
 
        final TextView categoryTitle = (TextView) convertView.findViewById(R.id.inStoreCategoryHeader);
        categoryTitle.setTypeface(null, Typeface.BOLD);
        categoryTitle.setText(headerTitle);
 
        return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(final int groupPosition, final int childPosition) {
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
    
    public void addItemToList(final ShoppingItem item)
    {
    	//if we already have the item category
    	if (mListChildData.get(item.getCategory())!=null)
    		mListChildData.get(item.getCategory()).add(item);
    	else //we need to create a new category and add the item
    	{
    		//Add the new category before the "In cart" category which should always be the last
    		mListDataHeader.add(mListDataHeader.size()-1, item.getCategory());
    		
    		final List<ShoppingItem> newCategory = new ArrayList<ShoppingItem>();
    		newCategory.add(item);
    		mListChildData.put(item.getCategory(), newCategory);
    	}
    	
    	notifyDataSetChanged();
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
    
	//a class view container for our store sharp list items
    static public class InCartStoreItemViewContainer {
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
