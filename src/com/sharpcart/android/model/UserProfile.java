package com.sharpcart.android.model;

public class UserProfile {
	private static final UserProfile instance = new UserProfile();
	
	private String stores;
	private String zip;
	private String familySize;
	private String userName;
	
	private UserProfile() {
		zip = null;
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
	
	public String conversFamilyStringToValue(String familySize)
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
}
