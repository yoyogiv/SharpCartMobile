package com.sharpcart.android.fragment;

import com.sharpcart.android.R;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;


public class SettingsFragment extends PreferenceActivity implements OnSharedPreferenceChangeListener{
	   @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);

	        // Load the preferences from an XML resource
	        addPreferencesFromResource(R.xml.preferences);
	    }
	   
	   @Override
	   protected void onResume() {
	       super.onResume();
	       getPreferenceScreen().getSharedPreferences()
	               .registerOnSharedPreferenceChangeListener(this);
	   }

	   @Override
	   protected void onPause() {
	       super.onPause();
	       getPreferenceScreen().getSharedPreferences()
	               .unregisterOnSharedPreferenceChangeListener(this);
	   }

	   @Override
	   public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,String key) {
	   
		   Preference preference = findPreference(key);
	   }
}


