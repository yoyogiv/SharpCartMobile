
package com.sharpcart.android.api;

import android.util.Log;

import com.sharpcart.android.exception.SharpCartException;
import com.sharpcart.android.model.MainSharpList;
import com.sharpcart.android.net.HttpHelper;
import com.sharpcart.android.utilities.SharpCartConstants;

public class LoginServiceImpl {

	private static final String TAG = LoginServiceImpl.class.getCanonicalName();

	public static boolean login(final String username, final String password)
			throws SharpCartException {
		
		final String response = sendCredentials(username, password);
		return hasLoggedIn(response);
	}

	public static String sendCredentials(final String username, final String password)
			throws SharpCartException {

		final String url = SharpCartUrlFactory.getInstance().getLoginUrl();
		final String ret = HttpHelper.getHttpResponseAsStringUsingPOST(url,
				"userName=" + username + "&password=" + password);
		
		//set main sharp list object userName
		MainSharpList.getInstance().setUserName(username);
		
		return ret;
	}

	public static boolean hasLoggedIn(String response) {
		Log.d(TAG, "response: " + response);
		
		//Remove any trailing \n or \r
		response = response.replaceAll("(\\r|\\n)", "");
		
		return response.equalsIgnoreCase(SharpCartConstants.SUCCESS);
	}

	public boolean logout() {
		return false;
	}
}
