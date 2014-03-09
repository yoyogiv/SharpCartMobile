package com.sharpcart.android.fragment;

import java.util.Set;

import com.sharpcart.android.R;
import com.sharpcart.android.utilities.SharpCartUtilities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class ChooseStoreDialogFragment extends DialogFragment {
	
	private Spinner mChooseStoreSpinner;
	private Button mChooseStoreButton;
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
	public Dialog onCreateDialog(Bundle savedInstanceState) {
	    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    
        //get the mode for the dialog, either it is choosing a store for the weekly sale info or for in-store mode
        final Bundle bundle = getArguments();
        mMode = bundle.getInt("chooseStoreDialogMode");
        
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
        
	    builder.setTitle(R.string.choose_store)
	           .setItems(storesArray, new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog, int which) {
	            	   
	   	            // Return input text to activity
	   				final ChooseStoreDialogFragmentListener activity = (ChooseStoreDialogFragmentListener) getActivity();
	   	            
	   				activity.onFinishChooseStoreDialog(storesArray[which],mMode);
	           }
	    });
	    
	    return builder.create();
	}
}
