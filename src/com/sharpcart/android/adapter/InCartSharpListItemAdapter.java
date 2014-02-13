package com.sharpcart.android.adapter;

import java.util.List;

import org.apache.commons.lang3.text.WordUtils;

import com.sharpcart.android.R;
import com.sharpcart.android.fragment.StoreSharpListFragment;
import com.sharpcart.android.model.ShoppingItem;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class InCartSharpListItemAdapter extends ArrayAdapter<ShoppingItem> {
	
	private final Activity mActivity;
	
	public InCartSharpListItemAdapter(final Activity context,
			List<ShoppingItem> shoppingItems) {
		super(context, R.layout.store_sharp_list, shoppingItems);
		
		mActivity = context;
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
		    viewContainer.itemDescriptionTextView = (TextView) rowView.findViewById(R.id.description);
		    viewContainer.itemQuantityEditText = (EditText) rowView.findViewById(R.id.quantity);
		    //viewContainer.itemPackageSizeEditText = (EditText) rowView.findViewById(R.id.packageSize);
		    viewContainer.itemUnitTextView = (TextView) rowView.findViewById(R.id.unit);
		    viewContainer.itemPriceEditText = (EditText) rowView.findViewById(R.id.price);
		    viewContainer.checkBox = (Button) rowView.findViewById(R.id.checkBox);
		    viewContainer.checkBox.setText("Return");
		    
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
			viewContainer.itemDescriptionTextView.setText(WordUtils.capitalize(getItem(position).getDescription()));
			viewContainer.itemQuantityEditText.setText(String.valueOf(getItem(position).getQuantity()));
			viewContainer.itemUnitTextView.setText(getItem(position).getUnit()+" ");
			//viewContainer.itemPackageSizeEditText.setText(String.valueOf(getItem(position).getPackage_quantity()));
			viewContainer.itemPriceEditText.setText(String.valueOf(getItem(position).getPrice()));
			
			viewContainer.checkBox.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(final View v) {
					double itemTotalCost = viewContainer.itemPrice*viewContainer.itemQuantity;
		
					//update total cost text view
					((StoreSharpListFragment)((FragmentActivity)mActivity).
							getSupportFragmentManager().
							findFragmentByTag("storeSharpListFragment")).updateTotalCost(itemTotalCost*(-1));
					
					//move item to in cart grid
					((StoreSharpListFragment)((FragmentActivity)mActivity).
							getSupportFragmentManager().
							findFragmentByTag("storeSharpListFragment")).moveItemOutOfCart(getItem(position));
					
					//remove the item from our adapter
					removeItem(getItem(position));
						
				}
			});
			
			// Save our item information so we can use it later when we update items	
			viewContainer.itemName = getItem(position).getName();
			viewContainer.itemDescription = getItem(position).getDescription();
			viewContainer.itemPrice = getItem(position).getPrice();
			viewContainer.itemQuantity = getItem(position).getQuantity();
			viewContainer.itemPackageSize = getItem(position).getPackage_quantity();
			viewContainer.itemImageLocation = getItem(position).getImage_location();
			viewContainer.itemUnit = getItem(position).getUnit();
			viewContainer.itemId = getItem(position).getId();
		
			return rowView;
    }
	
    private void removeItem(ShoppingItem item)
    {
    	remove(item);
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
		public Button checkBox;
		
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
