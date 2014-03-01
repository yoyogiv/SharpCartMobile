package com.sharpcart.android.fragment;

import java.util.Set;

import com.sharpcart.android.R;
import com.sharpcart.android.utilities.SharpCartUtilities;

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

	@Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.choose_store_dialog, container);
        
        mChooseStoreSpinner = (Spinner) view.findViewById(R.id.chooseStoreSpinner);
        mChooseStoreButton = (Button) view.findViewById(R.id.chooseStoreButton);
        
        getDialog().setTitle("Choose Store");
        
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
        
        ArrayAdapter<String> spinnerArrayAdapter;
		spinnerArrayAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, storesArray);
		
		// Specify the layout to use when the list of choices appears
		spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		mChooseStoreSpinner.invalidate();
		
		// Apply the adapter to the spinner
		mChooseStoreSpinner.setAdapter(spinnerArrayAdapter);
		
        mChooseStoreButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(final View v) {
				
	            // Return input text to activity
				final ChooseStoreDialogFragmentListener activity = (ChooseStoreDialogFragmentListener) getActivity();
	            
				activity.onFinishChooseStoreDialog(mChooseStoreSpinner.getSelectedItem().toString(),mMode);
				
				dismiss();
			}
		});
        
        //get the mode for the dialog, either it is choosing a store for the weekly sale info or for in-store mode
        Bundle bundle = getArguments();
        mMode = bundle.getInt("chooseStoreDialogMode");
        
        return view;
    }
}
