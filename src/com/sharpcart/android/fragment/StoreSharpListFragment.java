package com.sharpcart.android.fragment;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.sharpcart.android.R;
import com.sharpcart.android.adapter.InCartSharpListItemAdapter;
import com.sharpcart.android.adapter.StoreSharpListItemAdapter;
import com.sharpcart.android.custom.ExpandableHeightGridView;
import com.sharpcart.android.model.ImageResource;
import com.sharpcart.android.model.MainSharpList;
import com.sharpcart.android.model.ShoppingItem;
import com.sharpcart.android.model.Store;
import com.sharpcart.android.utilities.SharpCartUtilities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class StoreSharpListFragment extends Fragment {
	private static final String TAG = StoreSharpListFragment.class.getSimpleName();
	
	private ExpandableHeightGridView storeSharpListItems;
	private ExpandableHeightGridView inCartSharpListItems;
	private ImageView storeImage;
	private StoreSharpListItemAdapter storeSharpListItemAdapter;
	private InCartSharpListItemAdapter inCartSharpListItemAdapter;
	private String storeName;
	private ArrayList<Store> optimizedStores;
	private DecimalFormat df;
	
	public TextView totalCost;
	
	@Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
        final Bundle savedInstanceState) {
    	
    	final View view = inflater.inflate(R.layout.store_sharp_list, container, false);
    	
	    //initialize store sharp list list view
    	storeSharpListItems = (ExpandableHeightGridView) view.findViewById(R.id.storeSharpListItems);
    	storeSharpListItems.setExpanded(true);
    	storeImage = (ImageView) view.findViewById(R.id.storeImageView);
    	
    	if (storeName!=null && optimizedStores!=null)
    	{
    		//iterate over stores and find the store with the same name as the one the user choose
    		for (final Store store : optimizedStores)
    		{
    			if (store.getName().equalsIgnoreCase(storeName))
    			{
    				storeSharpListItemAdapter = new StoreSharpListItemAdapter(getActivity(), store.getItems());
    				
    				try {
    					for (final ImageResource imageResource : SharpCartUtilities.getInstance().getStoreImages())
    					{
    						if (imageResource.getName().equalsIgnoreCase(storeName))
    						{
    							storeImage.setImageResource(imageResource.getDrawableResourceId());
    						}
    					}	    
    					
    				} catch (final Exception ex) {
    				    Log.d("ShoppingItemAdapter", ex.getLocalizedMessage());
    				}
    			}
    		}
    	} else
    	{
        	storeSharpListItemAdapter = new StoreSharpListItemAdapter(getActivity(), MainSharpList.getInstance().getMainSharpList());
    	}
    	
    	storeSharpListItems.setAdapter(storeSharpListItemAdapter);
    	
    	totalCost = (TextView) view.findViewById(R.id.toatlCostTextView);
    	
    	//init in cart grid
    	List<ShoppingItem> inCartList = new ArrayList<ShoppingItem>();
    	
    	inCartSharpListItems = (ExpandableHeightGridView) view.findViewById(R.id.inCartListItems);
    	inCartSharpListItemAdapter = new InCartSharpListItemAdapter(getActivity(), inCartList);
    	inCartSharpListItems.setAdapter(inCartSharpListItemAdapter);
    	inCartSharpListItems.setExpanded(true);
    	
    	df = new DecimalFormat("#,###,##0.00");
    	
        // Inflate the layout for this fragment
        return view;
	}

	/**
	 * @return the storeName
	 */
	public String getStoreName() {
		return storeName;
	}

	/**
	 * @param storeName the storeName to set
	 */
	public void setStoreName(final String storeName) {
		this.storeName = storeName;
	}

	/**
	 * @return the optimizedStores
	 */
	public ArrayList<Store> getOptimizedStores() {
		return optimizedStores;
	}

	/**
	 * @param optimizedStores the optimizedStores to set
	 */
	public void setOptimizedStores(final ArrayList<Store> optimizedStores) {
		this.optimizedStores = optimizedStores;
	}
	
	public void updateTotalCost(double itemPrice)
	{
		double totalCostNumber = Double.valueOf(totalCost.getText().toString());
		totalCostNumber+=itemPrice;
		totalCost.setText(df.format(totalCostNumber));
		
		storeSharpListItemAdapter.notifyDataSetChanged();
		
	}
	
	public void moveItemToCart(ShoppingItem item)
	{
		inCartSharpListItemAdapter.add(item);
		inCartSharpListItemAdapter.notifyDataSetChanged();
	}
	
	public void moveItemOutOfCart(ShoppingItem item)
	{
		storeSharpListItemAdapter.add(item);
		storeSharpListItemAdapter.notifyDataSetChanged();
	}
}
