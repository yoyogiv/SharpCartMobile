package com.sharpcart.android.adapter;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

import com.sharpcart.android.R;
import com.sharpcart.android.dao.MainSharpListDAO;
import com.sharpcart.android.model.MainSharpList;
import com.sharpcart.android.model.Store;
import com.sharpcart.android.provider.SharpCartContentProvider;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ChooseGroceryStoreAdapter extends ArrayAdapter<Store> {
	private Drawable d;
	
	public ChooseGroceryStoreAdapter(Context context, int resource,List<Store> stores) {
		super(context, resource, stores);
		
	}

	/* (non-Javadoc)
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	    LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View storeView = inflater.inflate(R.layout.store, parent, false);
	    
	    TextView storeAddress = (TextView) storeView.findViewById(R.id.storeAddressTextView);
	    storeAddress.setText((getItem(position)).getStreet());
	    
	    ImageView storeImage = (ImageView) storeView.findViewById(R.id.storeImageView);
	    
		try {
		    // get input stream
			final String shoppingItemImageLocation = getItem(position).getImageLocation().replaceFirst("/", "");
			
		    final InputStream ims = getContext().getAssets().open(shoppingItemImageLocation);
		    
		    // load image as Drawable
		    d = Drawable.createFromStream(ims, null);
		    
		    // set image to ImageView
		    storeImage.setImageDrawable(d);
				
		} catch (final IOException ex) {
		    Log.d("ChooseGroceryStoreAdapter", ex.getLocalizedMessage());
		}
		

	    return storeView;
	}
	
	
}
