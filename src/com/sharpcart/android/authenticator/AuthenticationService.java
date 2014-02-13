package com.sharpcart.android.authenticator;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class AuthenticationService extends Service {
	private Authenticator mAuthenticator;

	@Override
	public void onCreate() {
		super.onCreate();
		mAuthenticator = new Authenticator(this);
	}

	@Override
	public IBinder onBind(final Intent intent) {
		return mAuthenticator.getIBinder();
	}
}
