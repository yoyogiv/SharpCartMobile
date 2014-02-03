package com.sharpcart.android.fragment;

import java.util.Set;

import com.sharpcart.android.R;
import com.sharpcart.android.model.UserProfile;

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
		   
		   if (key.equals("pref_zip"))
		   {
			   UserProfile.getInstance().setZip(sharedPreferences.getString(key, ""));
		   }
		   
		   if (key.equals("pref_family_size"))
		   {
			   UserProfile.getInstance().setFamilySize(sharedPreferences.getString(key, ""));
		   }
		   
		   if (key.equals("pref_stores"))
		   {
			   //UserProfile.getInstance().setFamilySize(Integer.valueOf(sharedPreferences.getString(key, "")));
			   Set<String> stores = sharedPreferences.getStringSet(key, null);
			   String stores_db_string = "";
			   
			   for (String store : stores)
			   {
				   stores_db_string+=store+"-";
			   }
			   
			   //remove last "-"
			   stores_db_string = stores_db_string.substring(0, stores_db_string.length() - 1);
			   
			   UserProfile.getInstance().setStores(stores_db_string);
		   }
	}
}


