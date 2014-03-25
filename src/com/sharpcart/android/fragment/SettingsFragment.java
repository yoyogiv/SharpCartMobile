package com.sharpcart.android.fragment;

import java.util.Date;
import java.util.Set;

import com.sharpcart.android.R;
import com.sharpcart.android.model.UserProfile;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;


public class SettingsFragment extends PreferenceActivity implements OnSharedPreferenceChangeListener{
	   @Override
	    public void onCreate(final Bundle savedInstanceState) {
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
	   public void onSharedPreferenceChanged(final SharedPreferences sharedPreferences,final String key) {
		   
           final Preference pref = findPreference(key);
           
           //save a timestamp for when the preference was changed
           UserProfile.getInstance().setLastUpdated(new Date());

           sharedPreferences.edit()
           		.putLong("user_profile_last_updated", new Date().getTime())
           		.commit();
           
		   if (key.equals("pref_zip"))
		   {
			   UserProfile.getInstance().setZip(sharedPreferences.getString(key, ""));
			   
	           //Set summary to be the user-description for the selected value
			   pref.setSummary(sharedPreferences.getString(key, ""));
		   }
		   
		   if (key.equals("pref_family_size"))
		   {
			   UserProfile.getInstance().setFamilySize(sharedPreferences.getString(key, ""));
			   
	            // Set summary to be the user-description for the selected value
			   pref.setSummary(UserProfile.getInstance().convertFamilySizeNumberToString(sharedPreferences.getString(key, "")));
		   }
		   
		   if (key.equals("pref_stores"))
		   {
			   //UserProfile.getInstance().setFamilySize(Integer.valueOf(sharedPreferences.getString(key, "")));
			   final Set<String> stores = sharedPreferences.getStringSet(key, null);
			   String stores_db_string = "";
			   
			   for (final String store : stores)
			   {
				   stores_db_string+=store+"-";
			   }
			   
			   //remove last "-"
			   stores_db_string = stores_db_string.substring(0, stores_db_string.length() - 1);
			   
			   UserProfile.getInstance().setStores(stores_db_string);
			   
	            // Set summary to be the user-description for the selected value
			   pref.setSummary(UserProfile.getInstance().convertStoresSetToString((sharedPreferences.getStringSet(key, null))));
		   }
	}
}


