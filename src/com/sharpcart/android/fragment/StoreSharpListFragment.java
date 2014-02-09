package com.sharpcart.android.fragment;

import com.sharpcart.android.R;
import com.sharpcart.android.adapter.StoreSharpListItemAdapter;
import com.sharpcart.android.model.MainSharpList;

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
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
    	
    	final View view = inflater.inflate(R.layout.store_sharp_list, container, false);
    	
	    //initialize store sharp list list view
    	storeSharpListItems = (GridView) view.findViewById(R.id.storeSharpListItems);
    	storeSharpListItemAdapter = new StoreSharpListItemAdapter(getActivity(), MainSharpList.getInstance().getMainSharpList());
    	storeSharpListItems.setAdapter(storeSharpListItemAdapter);
    	
        // Inflate the layout for this fragment
        return view;
	}
}
