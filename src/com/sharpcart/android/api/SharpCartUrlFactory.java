package com.sharpcart.android.api;

public class SharpCartUrlFactory {
    private static String URL_BASE = "http://www.sharpcart.com/mobile/";
    public static final String LOGIN = "login.php";
    public static final String LOGOUT = "logout.php";
    public static final String MOBILE_MANAGEMENT = "mobileManagementController.php";
    public static final String OPTIMIZE = "optimize.php";
    public static final String EMAIL = "emailSharpList.php";
    public static final String SYNC_ACTIVE_SHARP_LIST = "syncActiveSharpList.php";
    public static final String USER_PROFILE = "userProfile.php";
    public static final String TODO_ADD = "add/%s";
    public static final String TODO_DELETE = "del/%d";

    private static SharpCartUrlFactory instance = null;

    private SharpCartUrlFactory() {
    }

    public static SharpCartUrlFactory getInstance() {
	if (instance == null) {
	    instance = new SharpCartUrlFactory();
	}

	return instance;
    }

    public String getLoginUrl() {
	return URL_BASE + LOGIN;
    }

    public String getLoginUrlFmt() {
	return getLoginUrl() + "username=%s&passwd=%s";
    }

    public String getLogoutUrl() {
	return URL_BASE + LOGOUT;
    }

    public String getSharpListsUrl() {
	return URL_BASE + MOBILE_MANAGEMENT;
    }

    public String getSharpListAddUrlFmt() {
	return URL_BASE + MOBILE_MANAGEMENT;
    }

    public String getSharpListDeleteUrlFmt() {
	return URL_BASE + MOBILE_MANAGEMENT;
    }

    public String getStoresUrl() {
	return URL_BASE + MOBILE_MANAGEMENT;
    }

    public String getPricesUrl() {
	return URL_BASE + MOBILE_MANAGEMENT;
    }

    public String getItemsOnSaleUrl() {
	return URL_BASE + MOBILE_MANAGEMENT;
    }
    
    public String getUnavailableItemsUrl() {
	return URL_BASE + MOBILE_MANAGEMENT;
    }
    
    public String getOptimizeUrl() {
	return URL_BASE + OPTIMIZE;
    }
    
    public String getEmailSharpListUrl() {
	return URL_BASE + EMAIL;
    }
    
    public String getSyncActiveSharpListUrl() {
	return URL_BASE + SYNC_ACTIVE_SHARP_LIST;
    }
    
    public String getUserProfileUrl() {
	return URL_BASE + USER_PROFILE;
    }
    
    public String getStoreAddUrlFmt() {
	return URL_BASE + MOBILE_MANAGEMENT;
    }

    public String getStoreDeleteUrlFmt() {
	return URL_BASE + MOBILE_MANAGEMENT;
    }
}
