package com.sharpcart.android.model;

public class UserProfile {
	private static final UserProfile instance = new UserProfile();
	
	private String stores;
	private String zip;
	private int familySize;
	private String userName;
	
	private UserProfile() {
		zip = null;
		familySize = 0;
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
	public int getFamilySize() {
		return familySize;
	}

	/**
	 * @param familySize the familySize to set
	 */
	public void setFamilySize(int familySize) {
		this.familySize = familySize;
	}

	/**
	 * @param familySize the familySize to set
	 */
	public void setFamilySize(String familySize) {
		if (familySize.equalsIgnoreCase("single"))
			this.familySize = 1;
		
		if (familySize.equalsIgnoreCase("couple"))
			this.familySize = 2;
		
		if (familySize.equalsIgnoreCase("four or less"))
			this.familySize = 3;
		
		if (familySize.equalsIgnoreCase("five or more"))
			this.familySize = 4;
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
		
		for (String store: stores)
		{
			if (store.equalsIgnoreCase("costco"))
				storeNames.replace("Costco", "3");
			if (store.equalsIgnoreCase("HEB"))
				storeNames.replace("HEB", "1");
			if (store.equalsIgnoreCase("Walmart"))
				storeNames.replace("Walmart", "2");
			if (store.equalsIgnoreCase("Sprouts"))
				storeNames.replace("Sprouts", "4");
			if (store.equalsIgnoreCase("Sams Club"))
				storeNames.replace("Sams Club", "5");
		}
		
		//replace , with -
		storeNames.replace(",", "-");
		
		return storeNames;
	}
}
