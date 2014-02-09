package com.sharpcart.android.adapter;

import java.util.List;

import org.apache.commons.lang3.text.WordUtils;
import com.sharpcart.android.R;
import com.sharpcart.android.model.ShoppingItem;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class StoreSharpListItemAdapter extends ArrayAdapter<ShoppingItem> {
	
	private Activity mActivity;
	private List<ShoppingItem> mShoppingItems;
	
	public StoreSharpListItemAdapter(Activity context,
			List<ShoppingItem> shoppingItems) {
		super(context, R.layout.store_sharp_list, shoppingItems);
		
		mActivity = context;
		mShoppingItems = shoppingItems;
	}

    @Override
    public View getView(int position, View view, ViewGroup parent) {
	    StoreItemViewContainer viewContainer;
		View rowView = view;

		// ---if the row is displayed for the first time---
		if (rowView == null) {
	
		    LayoutInflater inflater = mActivity.getLayoutInflater();
		    rowView = inflater.inflate(R.layout.store_sharp_list_item, null, true);
	
		    // ---create a view container object---
		    viewContainer = new StoreItemViewContainer();
	
		    // ---get the references to all the views in the row---
		    viewContainer.itemDescriptionTextView = (TextView) rowView.findViewById(R.id.description);
		    viewContainer.itemQuantityEditText = (EditText) rowView.findViewById(R.id.quantity);
		    viewContainer.itemPackageSizeEditText = (EditText) rowView.findViewById(R.id.packageSize);
		    viewContainer.itemUnitTextView = (TextView) rowView.findViewById(R.id.unit);
		    viewContainer.itemPriceEditText = (EditText) rowView.findViewById(R.id.price);
		    viewContainer.checkBox = (CheckBox) rowView.findViewById(R.id.checkBox);
		    
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
			viewContainer.itemPackageSizeEditText.setText(String.valueOf(getItem(position).getPackage_quantity()));
			viewContainer.itemPriceEditText.setText(String.valueOf(getItem(position).getPrice()));
			
			// Save our item information so we can use it later when we update items
		
			viewContainer.itemName = getItem(position).getName();
			viewContainer.itemDescription = getItem(position).getDescription();
			viewContainer.itemPrice = getItem(position).getPrice();
			viewContainer.itemQuantity = getItem(position).getQuantity();
			viewContainer.itemPackageSize = getItem(position).getPackage_quantity();
			viewContainer.itemImageLocation = getItem(position).getImage_location();
			viewContainer.itemUnit = getItem(position).getUnit();
			viewContainer.itemId = this.getItem(position).getId();
		
			return rowView;
    }
	
	//a class view container for our store sharp list items
    static public class StoreItemViewContainer {
		public ImageView imageView;
		public TextView itemDescriptionTextView;
		public TextView itemUnitTextView;
		public EditText itemQuantityEditText;
		public EditText itemPackageSizeEditText;
		public EditText itemPriceEditText;
		public CheckBox checkBox;
		
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
