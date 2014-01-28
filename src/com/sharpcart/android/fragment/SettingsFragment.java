package com.sharpcart.android.fragment;

import com.sharpcart.android.R;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;


public class SettingsFragment extends PreferenceActivity {
	   @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);

	        // Load the preferences from an XML resource
	        addPreferencesFromResource(R.xml.preferences);
	    }
}
