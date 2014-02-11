package com.sharpcart.android.fragment;

import java.util.Set;

import com.sharpcart.android.R;
import com.sharpcart.android.fragment.EmailSharpListDialogFragment.EmailSharpListDialogFragmentListener;
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
import android.widget.EditText;
import android.widget.Spinner;

public class ChooseStoreDialogFragment extends DialogFragment {
	
	private Spinner mChooseStoreSpinner;
	private Button mChooseStoreButton;
	
    public interface ChooseStoreDialogFragmentListener {
        void onFinishChooseStoreDialog(String store);
    }
    
	public ChooseStoreDialogFragment() {
		
	}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.choose_store_dialog, container);
        
        mChooseStoreSpinner = (Spinner) view.findViewById(R.id.chooseStoreSpinner);
        mChooseStoreButton = (Button) view.findViewById(R.id.chooseStoreButton);
        
        getDialog().setTitle("Choose Store");
        
        //populate the spinner based on user preferences
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext());
        Set<String> stores = sharedPref.getStringSet("pref_stores", null);
        String[] storesArray = new String[stores.size()];
        int index = 0;
        for (String store : stores)
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
			public void onClick(View v) {
				
	            // Return input text to activity
				final ChooseStoreDialogFragmentListener activity = (ChooseStoreDialogFragmentListener) getActivity();
	            
				activity.onFinishChooseStoreDialog(mChooseStoreSpinner.getSelectedItem().toString());
				
				dismiss();
			}
		});
        
        return view;
    }
}
