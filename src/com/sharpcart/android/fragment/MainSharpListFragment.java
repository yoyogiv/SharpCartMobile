package com.sharpcart.android.fragment;

import com.sharpcart.android.R;
import com.sharpcart.android.adapter.MainSharpListItemAdapter;
import com.sharpcart.android.fragment.EmailSharpListDialogFragment.EmailSharpListDialogFragmentListener;
import com.sharpcart.android.model.MainSharpList;
import com.sharpcart.android.provider.SharpCartContentProvider;
import com.sharpcart.android.utilities.SharpCartUtilities;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;

import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MainSharpListFragment extends Fragment {

	private static final String TAG = MainSharpListFragment.class.getSimpleName();
	
	public static MainSharpListItemAdapter mainSharpListAdapter;
	private ListView mainSharpListItemsListView;
	private OptimizationTaskFragment mOptimizationTaskFragment;
	private EmailSharpListTaskFragment mEmailSharpListTaskFragment;
	private ProgressBar mProgressBar;
	  
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
    	
    	final View view = inflater.inflate(R.layout.main_sharp_list, container, false);
    	
	    //initialize main sharp list list view
	    mainSharpListAdapter = new MainSharpListItemAdapter(getActivity());
	    mainSharpListItemsListView = (ListView) view.findViewById(R.id.mainSharpListItemsListView);
	    mainSharpListItemsListView.setAdapter(mainSharpListAdapter);
	    
	    //setup on click event for delete button
	    ImageButton deleteButton = (ImageButton) view.findViewById(R.id.emptyMainSharpListButton);
	    
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
	    			    		   
	    			    		   //empty MainSharpList object
	    			    		   MainSharpList.getInstance().empty();
	    			    		   
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
	    
	    //setup on click event for optimize button
	    ImageButton optimizeButton = (ImageButton) view.findViewById(R.id.optimizeMainSharpListButton);
	    
	    FragmentManager fm = getFragmentManager();
	    mOptimizationTaskFragment = (OptimizationTaskFragment) fm.findFragmentByTag("optimizeSharpListTask");
	    mEmailSharpListTaskFragment = (EmailSharpListTaskFragment) fm.findFragmentByTag("emailSharpListTask");
	    
	    // If the Fragment is non-null, then it is currently being
	    // retained across a configuration change.
	    if (mOptimizationTaskFragment == null) {
	      mOptimizationTaskFragment = new OptimizationTaskFragment();
	      fm.beginTransaction().add(mOptimizationTaskFragment, "optimizeSharpListTask").commit();
	    }

	    if (mOptimizationTaskFragment.isRunning()) {
	    	optimizeButton.setEnabled(false);
	    } else {
	    	optimizeButton.setEnabled(true);
	    }
		    
	    optimizeButton.setOnClickListener(new OnClickListener()
		{
	    	   @Override
	    	   public void onClick(View v) 
	    	   {
	    		   //Only run the task is we dont have an empty list
	    		   if (mainSharpListAdapter.getCount()!=0)
	    			   mOptimizationTaskFragment.start();
	    	   }
	    });
	    
	    //setup on click event for email button
	    ImageButton emailButton = (ImageButton) view.findViewById(R.id.emailShapListButton);
	    
	    if (mEmailSharpListTaskFragment == null) {
	    	mEmailSharpListTaskFragment = new EmailSharpListTaskFragment();
		      fm.beginTransaction().add(mEmailSharpListTaskFragment, "emailSharpListTask").commit();
		    }

	    if (mEmailSharpListTaskFragment.isRunning()) {
	    	emailButton.setEnabled(false);
	    } else {
	    	emailButton.setEnabled(true);
	    }
	    
	    emailButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showEmailSharpListDialog();			
			}
		});
	    
        // Inflate the layout for this fragment
        return view;
    }
    
   public void updateSharpList()
   {
	   mainSharpListAdapter.updateCursor();
   }
	
   public void emailSharpList()
   {
	   mEmailSharpListTaskFragment.start();
   }
   
   private void showEmailSharpListDialog() {
        FragmentManager fm = this.getFragmentManager();
        EmailSharpListDialogFragment emailSharpListDialog = new EmailSharpListDialogFragment();
        emailSharpListDialog.show(fm, "emailSharpListDialogFragment");
    }
   
}
