package com.sharpcart.android.fragment;

import java.util.List;
import java.util.Set;

import com.sharpcart.android.R;
import com.sharpcart.android.dao.StoreDAO;
import com.sharpcart.android.model.Store;
import com.sharpcart.android.utilities.SharpCartUtilities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;

public class ChooseStoreDialogFragment extends DialogFragment {
	
	private int mMode;
	
    public interface ChooseStoreDialogFragmentListener {
        void onFinishChooseStoreDialog(String store,int mMode);
    }
    
	public ChooseStoreDialogFragment() {
		
	}
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.DialogFragment#onCreateDialog(android.os.Bundle)
	 */
	@Override
	public Dialog onCreateDialog(final Bundle savedInstanceState) {
	    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    
        //get the mode for the dialog, either it is choosing a store for the weekly sale info or for in-store mode
        final Bundle bundle = getArguments();
        mMode = bundle.getInt("chooseStoreDialogMode");
        
        /*
        //populate the spinner based on user preferences
        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext());
        final Set<String> stores = sharedPref.getStringSet("pref_stores", null);
        final String[] storesArray = new String[stores.size()];
        int index = 0;
        for (final String store : stores)
        {
        	storesArray[index] = SharpCartUtilities.getInstance().getStoreName(Integer.valueOf(store));
        	index++;
        }
         */
        
        List<Store> stores = StoreDAO.getInstance().getStore(getActivity().getContentResolver(), "");
        
        final String[] storesArray = new String[stores.size()];
        int index = 0;
        
        for (Store store : stores)
        {
        	storesArray[index] = store.getName();
        	index++;
        }
        
	    builder.setTitle(R.string.choose_store)
	           .setItems(storesArray, new DialogInterface.OnClickListener() {
	               @Override
				public void onClick(final DialogInterface dialog, final int which) {
	            	   
	   	            // Return input text to activity
	   				final ChooseStoreDialogFragmentListener activity = (ChooseStoreDialogFragmentListener) getActivity();
	   	            
	   				activity.onFinishChooseStoreDialog(storesArray[which],mMode);
	           }
	    });
	    
	    return builder.create();
	}
}
