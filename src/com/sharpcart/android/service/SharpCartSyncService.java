package com.sharpcart.android.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class SharpCartSyncService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private static SharpCartSyncAdapter sSyncAdapter = null;

    @Override
    public void onCreate() {
	synchronized (sSyncAdapterLock) {
	    if (sSyncAdapter == null) {
		sSyncAdapter = new SharpCartSyncAdapter(
			getApplicationContext(), true);
	    }
	}
    }

    @Override
    public IBinder onBind(Intent intent) {
	return sSyncAdapter.getSyncAdapterBinder();
    }

}
