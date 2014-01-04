package com.sharpcart.android.fragment;

import com.sharpcart.android.R;
import com.sharpcart.android.adapter.MainSharpListItemAdapter;
import com.sharpcart.android.dao.MainSharpListDAO;
import com.sharpcart.android.provider.SharpCartContentProvider;

import android.support.v4.app.Fragment;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;

import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class MainSharpListFragment extends Fragment {

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
	    
	    //setup on click event for delete button
	    Button deleteButton = (Button) view.findViewById(R.id.emptyMainSharpListButton);
	    
	    deleteButton.setOnClickListener(new OnClickListener()
		{
	    	   @Override
	    	   public void onClick(View v) 
	    	   {
	    		   //make sure user is sure they want to empty list
	    		   DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
	    			    @Override
	    			    public void onClick(DialogInterface dialog, int which) {
	    			        switch (which){
	    			        case DialogInterface.BUTTON_POSITIVE:
	    			        	
	    			    		   //use content provider to empty main sharp list table
	    			    		   getActivity().getContentResolver().delete(
	    			    				   SharpCartContentProvider.CONTENT_URI_SHARP_LIST_ITEMS, 
	    			    				   null, 
	    			    				   null);
	    			    		   
	    			    		   //Update main sharp list adapter cursor to reflect the empty sharp list
	    			    		   mainSharpListAdapter.updateCursor();
	    			    		   
	    			            break;

	    			        case DialogInterface.BUTTON_NEGATIVE:
	    			            //No button clicked
	    			            break;
	    			        }
	    			    }
	    			};

	    			AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
	    			builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
	    			    .setNegativeButton("No", dialogClickListener).show();
	    		    
	    	   }
	    });
        // Inflate the layout for this fragment
        return view;
    }
    
   public void updateSharpList()
   {
	   mainSharpListAdapter.updateCursor();
   }
}
