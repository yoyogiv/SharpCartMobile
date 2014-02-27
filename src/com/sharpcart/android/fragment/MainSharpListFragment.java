package com.sharpcart.android.fragment;

import java.sql.Timestamp;

import com.espian.showcaseview.ShowcaseView;
import com.espian.showcaseview.ShowcaseViews;
import com.sharpcart.android.R;
import com.sharpcart.android.adapter.MainSharpListItemAdapter;
import com.sharpcart.android.model.MainSharpList;
import com.sharpcart.android.provider.SharpCartContentProvider;

import android.support.v4.content.CursorLoader;
import android.app.AlertDialog;
import android.support.v4.app.LoaderManager;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.GridView;

public class MainSharpListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>  {

	private static final String TAG = MainSharpListFragment.class.getSimpleName();
	
	public static MainSharpListItemAdapter mainSharpListAdapter;
	private GridView mainSharpListItemsListView;
	private OptimizationTaskFragment mOptimizationTaskFragment;
	private EmailSharpListTaskFragment mEmailSharpListTaskFragment;
	private ShowcaseViews mViews;
	private static SharedPreferences prefs = null;
	
    private static final String[] PROJECTION_ID_NAME_DESCRIPTION_CATEGORYID_UNITID_IMAGELOCATION_QUANTITY = new String[] {
	    SharpCartContentProvider.COLUMN_ID,
	    SharpCartContentProvider.COLUMN_NAME,
	    SharpCartContentProvider.COLUMN_DESCRIPTION,
	    SharpCartContentProvider.COLUMN_SHOPPING_ITEM_CATEGORY_ID,
	    SharpCartContentProvider.COLUMN_SHOPPING_ITEM_UNIT_ID,
	    SharpCartContentProvider.COLUMN_IMAGE_LOCATION,
	    SharpCartContentProvider.COLUMN_QUANTITY};
    
    private static final float SHOWCASE_LIKE_SCALE = 0.5f;
    
    @Override 
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        
        // Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.
	    mainSharpListAdapter = new MainSharpListItemAdapter(getActivity(),null);
	    mainSharpListItemsListView.setAdapter(mainSharpListAdapter);
	    
	    //ShowcaseView
        //setContentView() needs to be called in the Activity first.
        //That's why it has to be in onActivityCreated().
	    prefs = getActivity().getApplication().getSharedPreferences("com.sharpcart.android", android.content.Context.MODE_PRIVATE);
	    
	    if (prefs.getBoolean("MainSharpListFragmentFirstRun", true))
	    {
	        final ShowcaseView.ConfigOptions co = new ShowcaseView.ConfigOptions();
	        co.hideOnClickOutside = false;
	        co.shotType = ShowcaseView.TYPE_ONE_SHOT;
		        
	        mViews = new ShowcaseViews(getActivity(),
	                new ShowcaseViews.OnShowcaseAcknowledged() {
	            @Override
	            public void onShowCaseAcknowledged(final ShowcaseView showcaseView) {
	            }
	        });
	        
	        mViews.addView( new ShowcaseViews.ItemViewProperties(R.id.optimizeMainSharpListButton,
	                R.string.showcase_optimization_title,
	                R.string.showcase_optimization_message,
	                SHOWCASE_LIKE_SCALE));
	        mViews.addView( new ShowcaseViews.ItemViewProperties(R.id.emailShapListButton,
	                R.string.showcase_email_title,
	                R.string.showcase_email_message,
	                SHOWCASE_LIKE_SCALE));
	        mViews.show();
	        
	        prefs.edit().putBoolean("MainSharpListFragmentFirstRun", false).commit();
	    }
        
        getLoaderManager().initLoader(0, null, this);
    }
    
	@Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
        final Bundle savedInstanceState) {
    	
    	final View view = inflater.inflate(R.layout.main_sharp_list, container, false);
    	
	    //initialize main sharp list list view
	    mainSharpListItemsListView = (GridView) view.findViewById(R.id.mainSharpListItemsListView);

	    //setup on click event for delete button
	    final ImageButton deleteButton = (ImageButton) view.findViewById(R.id.emptyMainSharpListButton);
	    
	    if (MainSharpList.getInstance().getMainSharpList().size()==0)
	    	deleteButton.setEnabled(false);
	    
	    deleteButton.setOnClickListener(new OnClickListener()
		{
	    	   @Override
	    	   public void onClick(final View v) 
	    	   {
	    		   if (MainSharpList.getInstance().getMainSharpList().size()!=0)
	    		   {
		    		   //make sure user is sure they want to empty list
		    		   final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
		    			    @Override
		    			    public void onClick(final DialogInterface dialog, final int which) {
		    			        switch (which){
		    			        case DialogInterface.BUTTON_POSITIVE:
		    			        	
		    			    		   //use content provider to empty main sharp list table
		    			    		   getActivity().getContentResolver().delete(
		    			    				   SharpCartContentProvider.CONTENT_URI_SHARP_LIST_ITEMS, 
		    			    				   null, 
		    			    				   null);
		    			    		   
		    			    		   //empty MainSharpList object
		    			    		   MainSharpList.getInstance().empty();
		    			    		   MainSharpList.getInstance().setIs_deleted(true);
		    			    		   MainSharpList.getInstance().setLastUpdated(new Timestamp(System.currentTimeMillis()).toString());
		    			    		   
		    			    		   //disable button
		    			    		   deleteButton.setEnabled(false);
		    			            break;
	
		    			        case DialogInterface.BUTTON_NEGATIVE:
		    			            //No button clicked
		    			            break;
		    			        }
		    			    }
		    			};
	
		    			final AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
		    			builder.setMessage("Empty List?").setPositiveButton("Yes", dialogClickListener)
		    			    .setNegativeButton("No", dialogClickListener).show();
	    		   }	    
	    	   }
	    });
	    
	    //setup on click event for optimize button
	    final ImageButton optimizeButton = (ImageButton) view.findViewById(R.id.optimizeMainSharpListButton);
	    
	    final FragmentManager fm = getFragmentManager();
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
	    	   public void onClick(final View v) 
	    	   {
	    		   //Only run the task is we dont have an empty list
	    		   if (mainSharpListAdapter.getCount()!=0)
	    			   mOptimizationTaskFragment.start();
	    	   }
	    });
	    
	    //setup on click event for email button
	    final ImageButton emailButton = (ImageButton) view.findViewById(R.id.emailShapListButton);
	    
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
			public void onClick(final View v) {
				//We dont want to email an empty list
				if (MainSharpList.getInstance().getMainSharpList().size()!=0)
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
        final FragmentManager fm = getFragmentManager();
        final EmailSharpListDialogFragment emailSharpListDialog = new EmailSharpListDialogFragment();
        emailSharpListDialog.show(fm, "emailSharpListDialogFragment");
    }

	@Override
	public void onLoadFinished(final android.support.v4.content.Loader<Cursor> loader,
			final Cursor data) {
	       // Swap the new cursor in.  (The framework will take care of closing the
	       // old cursor once we return.)
			mainSharpListAdapter.swapCursor(data);
	}

	@Override
	public void onLoaderReset(final android.support.v4.content.Loader<Cursor> loader) {
        // This is called when the last Cursor provided to onLoadFinished()
        // above is about to be closed.  We need to make sure we are no
        // longer using it.
		mainSharpListAdapter.swapCursor(null);
		
	}

	@Override
	public android.support.v4.content.Loader<Cursor> onCreateLoader(final int arg0,final Bundle arg1) {
        final CursorLoader cl = new CursorLoader(getActivity(), 
				SharpCartContentProvider.CONTENT_URI_SHARP_LIST_ITEMS,
				null,//using null since we are going to grab all columns
				null, 
				null,
				SharpCartContentProvider.DEFAULT_SORT_ORDER);
        
        cl.setUpdateThrottle(2000); // update at most every 2 seconds.
        return cl;
	}
	
}
