package com.sharpcart.android.adapter;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

public class ChooseGroceryStoreAdapter extends ArrayAdapter<Store> {
	private Drawable d;
	
	private List<Store> selectedStores;
	
	private int selectedStoresSize = 0;
	
	private final boolean[] mCheckedState;
	
	public ChooseGroceryStoreAdapter(Context context, int resource,List<Store> stores) {
		super(context, resource, stores);
		selectedStores = new ArrayList<Store>();
		mCheckedState = new boolean[stores.size()];
	}

	/* (non-Javadoc)
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(final int position, View storeView, ViewGroup parent) {
		
		ViewHolder viewHolder;
		
		if (storeView==null)
		{
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			storeView = inflater.inflate(R.layout.store, parent, false);
			
			viewHolder = new ViewHolder();
			viewHolder.storeAddress = (TextView) storeView.findViewById(R.id.storeAddressTextView);
			viewHolder.storeImage = (ImageView) storeView.findViewById(R.id.storeImageView);
			viewHolder.storeSelectionCheckBox = (CheckBox) storeView.findViewById(R.id.selectStoreCheckBox);
			
			storeView.setTag(viewHolder);
		} else
		{
			viewHolder = (ViewHolder) storeView.getTag();
		}
	    	
	    viewHolder.storeAddress.setText((getItem(position)).getStreet());
	    
		try {
		    // get input stream
			final String shoppingItemImageLocation = getItem(position).getImageLocation().replaceFirst("/", "");
			
		    final InputStream ims = getContext().getAssets().open(shoppingItemImageLocation);
		    
		    // load image as Drawable
		    d = Drawable.createFromStream(ims, null);
		    
		    // set image to ImageView
		    viewHolder.storeImage.setImageDrawable(d);
				
		} catch (final IOException ex) {
		    Log.d("ChooseGroceryStoreAdapter", ex.getLocalizedMessage());
		}
		
		if (viewHolder.storeSelectionCheckBox == null)
		 {
			viewHolder.storeSelectionCheckBox = new CheckBox(getContext());
		 }
		 
		viewHolder.storeSelectionCheckBox.setChecked(mCheckedState[position]);
		
		viewHolder.storeSelectionCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				
				if (isChecked)
				{
					selectedStoresSize++;
					selectedStores.add(getItem(position));
					mCheckedState[position] = true;
				} else
				{
					selectedStoresSize--;
					selectedStores.remove(getItem(position));
					mCheckedState[position] = false;
				}
				
				if (selectedStoresSize>4)
				{
					Toast.makeText(buttonView.getContext(),"No more than 4 stores",Toast.LENGTH_SHORT).show();
					selectedStoresSize--;
					((CheckBox)buttonView).setChecked(false);
					selectedStores.remove(getItem(position));
					mCheckedState[position] = false;
				}
			}
		});

	    return storeView;
	}
	
	public List<Store> getSelectedStores()
	{
		return selectedStores;
	}
	
	static class ViewHolder {
		ImageView storeImage;
		TextView storeAddress;
		CheckBox storeSelectionCheckBox;
	}
}
