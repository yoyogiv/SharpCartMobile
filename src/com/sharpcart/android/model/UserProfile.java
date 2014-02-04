package com.sharpcart.android.model;

import java.util.Set;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class UserProfile {
	private static final UserProfile instance = new UserProfile();
	
	private String stores;
	private String zip;
	private String familySize;
	private String userName;
	
	private UserProfile() {
		zip = "";
		familySize = "";
		userName = "";
		stores = "";
	}

    public static UserProfile getInstance() {
    	return instance;
    }
	/**
	 * @return the stores
	 */
	public String getStores() {
		return stores;
	}

	/**
	 * @param stores the stores to set
	 */
	public void setStores(String stores) {
		this.stores = stores;
	}

	/**
	 * @return the zip
	 */
	public String getZip() {
		return zip;
	}

	/**
	 * @param zip the zip to set
	 */
	public void setZip(String zip) {
		this.zip = zip;
	}

	/**
	 * @return the familySize
	 */
	public String getFamilySize() {
		return familySize;
	}


	/**
	 * @param familySize the familySize to set
	 */
	public void setFamilySize(String familySize) {
		
		this.familySize = familySize;
		
		if (familySize.equalsIgnoreCase("single"))
			this.familySize = "1";
		
		if (familySize.equalsIgnoreCase("couple"))
			this.familySize = "2";
		
		if (familySize.equalsIgnoreCase("four or less"))
			this.familySize = "3";
		
		if (familySize.equalsIgnoreCase("five or more"))
			this.familySize = "4";
		
	}
	
	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public void update(UserProfile userProfile)
	{
		this.familySize = userProfile.getFamilySize();
		this.stores = userProfile.getStores();
		this.zip = userProfile.getZip();
		this.userName  = userProfile.getUserName();
	}
	
	public String storesStringFromStoreName(String storeNames)
	{
		String[] stores = storeNames.split(",");
		
		//remove white space
		storeNames = storeNames.replaceAll("\\s+","");
		
		for (String store: stores)
		{
			//remove white spaces
			store = store.replaceAll("\\s+","");
			
			if (store.equalsIgnoreCase("Costco"))
				storeNames = storeNames.replace("Costco", "3");
			
			if (store.equalsIgnoreCase("HEB"))
				storeNames = storeNames.replace("HEB", "1");
			
			if (store.equalsIgnoreCase("Walmart"))
				storeNames = storeNames.replace("Walmart", "2");
			
			if (store.equalsIgnoreCase("Sprouts"))
				storeNames = storeNames.replace("Sprouts", "4");
			
			if (store.equalsIgnoreCase("Sams Club"))
				storeNames = storeNames.replace("Sams Club", "5");
		}
		
		//replace , with -
		storeNames = storeNames.replace(",", "-");
		
		return storeNames;
	}
	
	public String convertFamilyStringToValue(String familySize)
	{
		//remove whitespaces
		familySize = familySize.replaceAll("\\s+","");
		
		if (familySize.equalsIgnoreCase("single"))
			familySize = "1";
		
		if (familySize.equalsIgnoreCase("couple"))
			familySize = "2";
		
		if (familySize.equalsIgnoreCase("four or less"))
			familySize = "3";
		
		if (familySize.equalsIgnoreCase("five or more"))
			familySize = "4";
		
		return familySize;
	}
	
	public String convertFamilySizeNumberToString(String familySizeNumber)
	{
		//remove whitespaces
		familySizeNumber = familySizeNumber.replaceAll("\\s+","");
		
		if (familySizeNumber.equalsIgnoreCase("1"))
			familySizeNumber = "Single";
		
		if (familySizeNumber.equalsIgnoreCase("2"))
			familySizeNumber = "Couple";
		
		if (familySizeNumber.equalsIgnoreCase("3"))
			familySizeNumber = "Four or less";
		
		if (familySizeNumber.equalsIgnoreCase("4"))
			familySizeNumber = "Five or less";
		
		return familySizeNumber;
	}
	
	public String convertStoresSetToString(Set<String> stores)
	{
		String storesString = "";
		
		for (String store : stores)
		{
			if (store.equalsIgnoreCase("1"))
				storesString+="HEB,";
			
			if (store.equalsIgnoreCase("2"))
				storesString+="Walmart,";
			
			if (store.equalsIgnoreCase("3"))
				storesString+="Costco,";
			
			if (store.equalsIgnoreCase("4"))
				storesString+="Sprouts,";
			
			if (store.equalsIgnoreCase("5"))
				storesString+="Sams Club,";
		}
		
		//remove last ,
		storesString = storesString.substring(0, storesString.length()-1);
		
		return storesString;
	}
}
