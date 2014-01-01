package com.sharpcart.android.fragment;

import com.sharpcart.android.R;
import com.sharpcart.android.adapter.MainSharpListItemAdapter;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ListView;

public class MainSharpList extends Fragment {

	public static MainSharpListItemAdapter mainSharpListAdapter;
	private ListView mainSharpListItemsListView;
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
    	
    	final View view = inflater.inflate(R.layout.main_sharp_list, container, false);
    	
	    //initialize main sharp list list view
	    mainSharpListAdapter = new MainSharpListItemAdapter(getActivity());
	    mainSharpListItemsListView = (ListView) view.findViewById(R.id.mainSharpListItemsListView);
	    mainSharpListItemsListView.setAdapter(mainSharpListAdapter);
	    
        // Inflate the layout for this fragment
        return view;
    }
    
   public void updateSharpList()
   {
	   mainSharpListAdapter.updateCursor();
   }
}
