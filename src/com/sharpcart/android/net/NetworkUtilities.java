package com.sharpcart.android.net;

import android.content.Context;
import android.os.Handler;

import com.sharpcart.android.exception.SharpCartException;
import com.sharpcart.android.utilities.SharpCartUtilities;
import com.sharpcart.android.api.LoginServiceImpl;
import com.sharpcart.android.authenticator.AuthenticatorActivity;

public class NetworkUtilities {

  public static Thread performOnBackgroundThread(final Runnable runnable) {
    final Thread t = new Thread() {
      @Override
      public void run() {
        try {
          runnable.run();
        } finally {

        }
      }
    };
    t.start();
    return t;
  }

  public static Thread attemptAuth(final String username,
      final String password, final Handler handler,
      final Context context) {
    final Runnable runnable = new Runnable() {
    	@Override
		public void run() {
	        authenticate(username, password, handler, context);
	      }
    };
    
    return NetworkUtilities.performOnBackgroundThread(runnable);
  }

  private static void authenticate(final String username, final String password,
		final Handler handler, final Context context) {
    
	  	boolean hasLoggedIn = false;

	    try {
	    	//before we perform a login we check that there is an Internet connection
	    	if (SharpCartUtilities.getInstance().hasActiveInternetConnection(context))
	    	{
		      final String response = LoginServiceImpl.sendCredentials(username,password);
		      hasLoggedIn = LoginServiceImpl.hasLoggedIn(response);
	    	}
	    	
	      if (hasLoggedIn) {
	        sendResult(true, handler, context);
	      } else {
	        sendResult(false, handler, context);
	      }
	    } catch (final SharpCartException e) {
	      sendResult(false, handler, context);
	    }
  }

  private static void sendResult(final Boolean result,
      final Handler handler, final Context context) {
    if (handler == null || context == null) {
      return;
    }
    
    handler.post(new Runnable() {
    	@Override
		public void run() {
	        ((AuthenticatorActivity) context)
	            .onAuthenticationResult(result);
	      }
	    });
  }
}
