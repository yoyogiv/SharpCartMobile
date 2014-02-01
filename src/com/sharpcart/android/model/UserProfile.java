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
}
