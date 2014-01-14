
package com.sharpcart.android.api;

import android.util.Log;

import com.sharpcart.android.exception.SharpCartException;
import com.sharpcart.android.model.MainSharpList;
import com.sharpcart.android.net.HttpHelper;

public class LoginServiceImpl {

	private static final String TAG = LoginServiceImpl.class.getCanonicalName();

	public static boolean login(String username, String password)
			throws SharpCartException {
		String response = sendCredentials(username, password);
		return hasLoggedIn(response);
	}

	public static String sendCredentials(String username, String password)
			throws SharpCartException {
		/*
		String fmt = SharpCartUrlFactory.getInstance().getLoginUrlFmt();
		String url = String.format(fmt, username, password);
		*/

		String url = SharpCartUrlFactory.getInstance().getLoginUrl();
		String ret = HttpHelper.getHttpResponseAsStringUsingPOST(url,
				"username=" + username + "&passwd=" + password);
		
		//set main sharp list object userName
		MainSharpList.getInstance().setUserName(username);
		
		return ret;
	}

	public static boolean hasLoggedIn(String response) {
		Log.d(TAG, "response: " + response);
		
		//Remove any trailing \n or \r
		response = response.replaceAll("(\\r|\\n)", "");
		
		return "ok".equals(response);
	}

	public boolean logout() {
		return false;
	}
}
