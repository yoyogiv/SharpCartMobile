package com.sharpcart.android.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class UserProfile {
	private static final UserProfile instance = new UserProfile();
	
	private List<Store> stores;
	private String zip;
	private String familySize;
	private String userName;
	private String password;
	private Date lastUpdated;
	
	private UserProfile() {
		zip = "";
		familySize = "";
		userName = "";
		stores = new ArrayList<Store>();
	}

    public static UserProfile getInstance() {
    	return instance;
    }
	/**
	 * @return the stores
	 */
	public List<Store> getStores() {
		return stores;
	}

	/**
	 * @param stores the stores to set
	 */
	public void setStores(final List<Store> stores) {
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
	public void setZip(final String zip) {
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
	public void setFamilySize(final String familySize) {
		
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
	public void setUserName(final String userName) {
		this.userName = userName;
	}
	
	public void update(final UserProfile userProfile)
	{
		familySize = userProfile.getFamilySize();
		stores = userProfile.getStores();
		zip = userProfile.getZip();
		userName  = userProfile.getUserName();
	}
	
	public String storesStringFromStoreName(String storeNames)
	{
		final String[] stores = storeNames.split(",");
		
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
	
	public String convertStoresSetToString(final Set<String> stores)
	{
		String storesString = "";
		
		for (final String store : stores)
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

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(final String password) {
		this.password = password;
	}

	/**
	 * @return the lastUpdated
	 */
	public Date getLastUpdated() {
		return lastUpdated;
	}

	/**
	 * @param lastUpdated the lastUpdated to set
	 */
	public void setLastUpdated(final Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}
	
	public void setLastUpdated(final Long lastUpdate) {
		lastUpdated = new Date(lastUpdate);
	}
	
}
