package com.sharpcart.android.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = DatabaseHelper.class.getCanonicalName();
    public static final String DATABASE_NAME = "SharpCart";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(final Context context) {
	super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
	public void onCreate(final SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
	/*
    Log.i(TAG, "Upgrading database from version " + oldVersion + " to "
		+ newVersion + ", which will destroy all old data");
	db.execSQL("DROP TABLE IF EXISTS "
		+ SharpCartContentProvider.SHARP_LISTS_TABLE_NAME);
	onCreate(db);
	*/
    }

}
