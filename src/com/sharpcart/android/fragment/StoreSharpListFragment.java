package com.sharpcart.android.fragment;

import java.util.ArrayList;

import com.sharpcart.android.R;
import com.sharpcart.android.adapter.StoreSharpListItemAdapter;
import com.sharpcart.android.model.MainSharpList;
import com.sharpcart.android.model.Store;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

public class StoreSharpListFragment extends Fragment {
	private static final String TAG = StoreSharpListFragment.class.getSimpleName();
	
	private GridView storeSharpListItems;
	private StoreSharpListItemAdapter storeSharpListItemAdapter;
	private String storeName;
	private ArrayList<Store> optimizedStores; 
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
    	
    	final View view = inflater.inflate(R.layout.store_sharp_list, container, false);
    	
	    //initialize store sharp list list view
    	storeSharpListItems = (GridView) view.findViewById(R.id.storeSharpListItems);
    	if (storeName!=null && optimizedStores!=null)
    	{
    		//iterate over stores and find the store with the same name as the one the user choose
    		for (Store store : optimizedStores)
    		{
    			if (store.getName().equalsIgnoreCase(storeName))
    				storeSharpListItemAdapter = new StoreSharpListItemAdapter(getActivity(), store.getItems());
    		}
    	} else
    	{
        	storeSharpListItemAdapter = new StoreSharpListItemAdapter(getActivity(), MainSharpList.getInstance().getMainSharpList());
    	}
    	
    	storeSharpListItems.setAdapter(storeSharpListItemAdapter);
    	
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
	public void setStoreName(String storeName) {
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
	public void setOptimizedStores(ArrayList<Store> optimizedStores) {
		this.optimizedStores = optimizedStores;
	}
	
}
