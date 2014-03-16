package com.sharpcart.android.service;

import com.sharpcart.android.utilities.SharpCartUtilities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SharpCartAlarmService extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		
		//Send a reminder notification to the user to create a grocery list
		SharpCartUtilities.getInstance().sendUserReminderNotificationToCreateGroceryList(context);		
	}
}
