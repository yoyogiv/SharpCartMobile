package com.sharpcart.android.provider;

import java.util.HashMap;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class SharpCartContentProvider extends ContentProvider {
    public static final String SHOPPING_ITEM_TABLE_NAME = "Shopping_Item";
    public static final String SHOPPING_ITEM_CATEGORY_TABLE_NAME = "Shopping_Item_Category";
    public static final String SHOPPING_ITEM_UNIT_TABLE_NAME = "Shopping_Item_Unit";
    public static final String SHARP_LIST_ITEMS_TABLE_NAME = "Main_Sharp_List_Item";
    public static final String STORE_TABLE_NAME = "Grocery_Store";

    public static final String AUTHORITY = SharpCartContentProvider.class.getCanonicalName();
    
    /* shopping item table column names */
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "Name";
    public static final String COLUMN_DESCRIPTION = "Description";
    public static final String COLUMN_SHOPPING_ITEM_CATEGORY_ID = "Shopping_Item_Category_Id";
    public static final String COLUMN_SHOPPING_ITEM_UNIT_ID = "Shopping_Item_Unit_Id";
    public static final String COLUMN_IMAGE_LOCATION = "Image_Location";
    public static final String COLUMN_UNIT_TO_ITEM_CONVERSION_RATIO = "Unit_To_Item_Conversion_Ratio";
    public static final String COLUMN_QUANTITY = "Quantity";
    public static final String COLUMN_ON_SALE = "On_Sale";
    public static final String COLUMN_ACTIVE = "Active";
    
    /* store table column names */
    public static final String COLUMN_STREET = "Street";
    public static final String COLUMN_CITY = "City";
    public static final String COLUMN_STATE = "State";
    public static final String COLUMN_ZIP = "ZIP";
    public static final String COLUMN_ON_SALE_FLYER_URL = "On_Sale_Flyer_URL";
    
    private static final int SHOPPING_ITEM = 1;
    private static final int SHOPPING_ITEM_DIR = 2;
    private static final int CATEGORIES = 3;
    private static final int UNITS = 4;
    private static final int SHARP_LIST_ITEMS = 5;
    private static final int SEARCH_SUGGEST = 6;
    private static final int STORE = 7;

    public static final String DEFAULT_SORT_ORDER = "Name ASC";
    
    /* projection maps, which are the same as SQL query */
    private static HashMap<String, String> projectionMapShoppingItem;
    private static HashMap<String, String> projectionMapSharpList;
    private static HashMap<String, String> projectionMapStore;
    
    private static final UriMatcher sUriMatcher;
    	
    /*
     * following are different constants representing our different database tables
     */
    public static final String CONTENT_TYPE_SHOPPING_ITEM = "vnd.android.cursor.item/vnd.sharpcart.shoppingitem";
    public static final String CONTENT_TYPE_SHOPPING_ITEM_DIR = "vnd.android.cursor.dir/vnd.sharpcart.shoppingitem";
    public static final String CONTENT_TYPE_CATEGORIES = "vnd.android.cursor.dir/vnd.sharpcart.categories";
    public static final String CONTENT_TYPE_UNITS = "vnd.android.cursor.dir/vnd.sharpcart.units";
    public static final String CONTET_TYPE_SHARP_LIST_ITEM_DIR = "vnd.android.cursor.DIR/vnd.sharpcart.mainsharplistitems";
    public static final String CONTET_TYPE_STORE_DIR = "vnd.android.cursor.dir/vnd.sharpcart.stores";
    
    public static final Uri CONTENT_URI_SHOPPING_ITEMS = Uri.parse("content://"
	    + AUTHORITY + "/" + SHOPPING_ITEM_TABLE_NAME);

    public static final Uri CONTENT_URI_CATEGORIES = Uri.parse("content://"
	    + AUTHORITY + "/" + SHOPPING_ITEM_CATEGORY_TABLE_NAME);

    public static final Uri CONTENT_URI_UNITS = Uri.parse("content://"
    	    + AUTHORITY + "/" + SHOPPING_ITEM_UNIT_TABLE_NAME);
    
    public static final Uri CONTENT_URI_SHARP_LIST_ITEMS = Uri.parse("content://"
    	    + AUTHORITY + "/" + SHARP_LIST_ITEMS_TABLE_NAME);
    
    public static final Uri CONTENT_URI_STORE = Uri.parse("content://"
    	    + AUTHORITY + "/" + STORE_TABLE_NAME);
    
    private DatabaseHelper dbHelper;

    static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	
		// uri matchers for our shopping item table
		sUriMatcher.addURI(AUTHORITY, SHOPPING_ITEM_TABLE_NAME, SHOPPING_ITEM);
		sUriMatcher.addURI(AUTHORITY, SHOPPING_ITEM_TABLE_NAME + "/#",SHOPPING_ITEM_DIR);
	
        // to get suggestions...
		sUriMatcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY, SEARCH_SUGGEST);
		sUriMatcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY + "/*", SEARCH_SUGGEST);
        
		// uri matcher for our categories table
		sUriMatcher.addURI(AUTHORITY, SHOPPING_ITEM_CATEGORY_TABLE_NAME, CATEGORIES);
	
		// uri matcher for our unit table
		sUriMatcher.addURI(AUTHORITY, SHOPPING_ITEM_UNIT_TABLE_NAME, UNITS);
		
		// uri matcher for our sharp list
		sUriMatcher.addURI(AUTHORITY, SHARP_LIST_ITEMS_TABLE_NAME, SHARP_LIST_ITEMS);
		
		// uri matcher for our store table
		sUriMatcher.addURI(AUTHORITY, STORE_TABLE_NAME, STORE);
		
		// shopping items table projection map
		projectionMapShoppingItem = new HashMap<String, String>();
		projectionMapShoppingItem.put(COLUMN_ID, COLUMN_ID);
		projectionMapShoppingItem.put(COLUMN_NAME, COLUMN_NAME);
		projectionMapShoppingItem.put(COLUMN_DESCRIPTION, COLUMN_DESCRIPTION);
		projectionMapShoppingItem.put(COLUMN_SHOPPING_ITEM_CATEGORY_ID, COLUMN_SHOPPING_ITEM_CATEGORY_ID);
		projectionMapShoppingItem.put(COLUMN_SHOPPING_ITEM_UNIT_ID, COLUMN_SHOPPING_ITEM_UNIT_ID);
		projectionMapShoppingItem.put(COLUMN_IMAGE_LOCATION, COLUMN_IMAGE_LOCATION);
		projectionMapShoppingItem.put(COLUMN_UNIT_TO_ITEM_CONVERSION_RATIO, COLUMN_UNIT_TO_ITEM_CONVERSION_RATIO);
		projectionMapShoppingItem.put(COLUMN_ACTIVE, COLUMN_ACTIVE);
		projectionMapShoppingItem.put(COLUMN_ON_SALE, COLUMN_ON_SALE);
		
		// sharp list table projection map
		projectionMapSharpList = new HashMap<String, String>();
		projectionMapSharpList.put(COLUMN_ID, COLUMN_ID);
		projectionMapSharpList.put(COLUMN_NAME, COLUMN_NAME);
		projectionMapSharpList.put(COLUMN_DESCRIPTION, COLUMN_DESCRIPTION);
		projectionMapSharpList.put(COLUMN_SHOPPING_ITEM_CATEGORY_ID, COLUMN_SHOPPING_ITEM_CATEGORY_ID);
		projectionMapSharpList.put(COLUMN_SHOPPING_ITEM_UNIT_ID, COLUMN_SHOPPING_ITEM_UNIT_ID);
		projectionMapSharpList.put(COLUMN_IMAGE_LOCATION, COLUMN_IMAGE_LOCATION);
		projectionMapSharpList.put(COLUMN_QUANTITY, COLUMN_QUANTITY);
		projectionMapSharpList.put(COLUMN_UNIT_TO_ITEM_CONVERSION_RATIO, COLUMN_UNIT_TO_ITEM_CONVERSION_RATIO);
		
		// store table projection map
		projectionMapStore = new HashMap<String, String>();
		projectionMapStore.put(COLUMN_ID, COLUMN_ID);
		projectionMapStore.put(COLUMN_NAME, COLUMN_NAME);
		projectionMapStore.put(COLUMN_CITY, COLUMN_CITY);
		projectionMapStore.put(COLUMN_STREET, COLUMN_STREET);
		projectionMapStore.put(COLUMN_STATE, COLUMN_STATE);
		projectionMapStore.put(COLUMN_IMAGE_LOCATION, COLUMN_IMAGE_LOCATION);
		projectionMapStore.put(COLUMN_ZIP, COLUMN_ZIP);
		projectionMapStore.put(COLUMN_ON_SALE_FLYER_URL, COLUMN_ON_SALE_FLYER_URL);
    }

    @Override
    public boolean onCreate() {
    	//We bind to our DatabaseHelper class which will provide us with access to the local db file
		dbHelper = new DatabaseHelper(getContext());
		
		return true;
    }

    @Override
    /*
     * (non-Javadoc)
     * @see android.content.ContentProvider#query(android.net.Uri, java.lang.String[], java.lang.String, java.lang.String[], java.lang.String)
     * This method will run a general sql query on our database
     */
    public Cursor query(final Uri uri, final String[] projection, final String selection,final String[] selectionArgs, final String sortOrder) {

		final SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		
		switch (sUriMatcher.match(uri)) {
		case SHOPPING_ITEM:
		    qb.setTables(SHOPPING_ITEM_TABLE_NAME);
		    qb.setProjectionMap(projectionMapShoppingItem);
		    break;
		case SHOPPING_ITEM_DIR:
		    qb.setTables(SHOPPING_ITEM_TABLE_NAME);
		    qb.setProjectionMap(projectionMapShoppingItem);
		    qb.appendWhere(COLUMN_ID + "=" + uri.getPathSegments().get(1));
		    break;
		case SHARP_LIST_ITEMS:
		    qb.setTables(SHARP_LIST_ITEMS_TABLE_NAME);
		    qb.setProjectionMap(projectionMapSharpList);
		    break;
		case STORE:
		    qb.setTables(STORE_TABLE_NAME);
		    qb.setProjectionMap(projectionMapStore);
		    break;
		/*
        case SEARCH_SUGGEST:
            if (selectionArgs == null) {
              throw new IllegalArgumentException(
                  "selectionArgs must be provided for the Uri: " + uri);
            }
            final MatrixCursor cursor = getSuggestions(selectionArgs[0]);
            return cursor;
            */
		default:
		    throw new RuntimeException("Unknown URI");
		}
	
		final SQLiteDatabase db = dbHelper.getReadableDatabase();
		final Cursor c = qb.query(db, projection, selection, selectionArgs, null,null, sortOrder);
	
		// By setting the cursor with a notification, any time we change
		// information on the database
		// it will automatically reflect the change to the content provider.
		c.setNotificationUri(getContext().getContentResolver(), uri);
		
		return c;
    }

    @Override
    /*
     * (non-Javadoc)
     * @see android.content.ContentProvider#getType(android.net.Uri)
     * 
     */
    public String getType(final Uri uri) {
		switch (sUriMatcher.match(uri)) {
		case SHOPPING_ITEM:
		    return CONTENT_TYPE_SHOPPING_ITEM;
		case SHOPPING_ITEM_DIR:
		    return CONTENT_TYPE_SHOPPING_ITEM_DIR;
		case CATEGORIES:
		    return CONTENT_TYPE_CATEGORIES;
		case UNITS:
		    return CONTENT_TYPE_UNITS;
		case SHARP_LIST_ITEMS:
		    return CONTET_TYPE_SHARP_LIST_ITEM_DIR;
		case STORE:
		    return CONTET_TYPE_STORE_DIR;
		default:
		    throw new IllegalArgumentException("Unknown URI " + uri);
		}
    }

    @Override
    /*
     * (non-Javadoc)
     * @see android.content.ContentProvider#insert(android.net.Uri, android.content.ContentValues)
     * this method will insert values into a table
     */
    public Uri insert(final Uri uri, final ContentValues initialValues) {
		ContentValues values;
		
		if (initialValues != null) {
		    values = new ContentValues(initialValues);
		} else {
		    values = new ContentValues();
		}
		
		String table = null;
		String nullableCol = null;
	
		switch (sUriMatcher.match(uri)) {
			case SHOPPING_ITEM:
			    table = SHOPPING_ITEM_TABLE_NAME;
			    nullableCol = SHOPPING_ITEM_TABLE_NAME;
			    break;
			case SHARP_LIST_ITEMS:
			    table = SHARP_LIST_ITEMS_TABLE_NAME;
			    nullableCol = SHARP_LIST_ITEMS_TABLE_NAME;
			    break;
			case STORE:
			    table = STORE_TABLE_NAME;
			    nullableCol = STORE_TABLE_NAME;
			    break;
			default:
			    new RuntimeException("Invalid URI for inserting: " + uri);
		}
	
		//Use database helper to get our db in writable mode and insert a new row into it
		final SQLiteDatabase db = dbHelper.getWritableDatabase();
		final long rowId = db.insert(table, nullableCol, values);
	
		//Notify the application context that the database has changed
		if (rowId > 0) {
		    final Uri noteUri = ContentUris.withAppendedId(uri, rowId);
		    getContext().getContentResolver().notifyChange(noteUri, null);
		    return noteUri;
		}

		throw new SQLException("Failed to insert row into " + uri);
    }

    @Override
    /*
     * (non-Javadoc)
     * @see android.content.ContentProvider#delete(android.net.Uri, java.lang.String, java.lang.String[])
     * this method will delete a row from a table using specific selection/argument
     */
    public int delete(final Uri uri, final String selection, final String[] selectionArgs) {
	final SQLiteDatabase db = dbHelper.getWritableDatabase();
	int count;

	switch (sUriMatcher.match(uri)) {
	case SHOPPING_ITEM:
	    count = db.delete(SHOPPING_ITEM_TABLE_NAME, selection, selectionArgs);
	    break;
	case SHOPPING_ITEM_DIR:
	    final String id = uri.getPathSegments().get(1);
	    count = db.delete(SHOPPING_ITEM_TABLE_NAME, COLUMN_ID
		    + "="
		    + id
		    + (!TextUtils.isEmpty(selection) ? " AND (" + selection
			    + ")" : ""), selectionArgs);
	    break;
	case SHARP_LIST_ITEMS:
	    count = db.delete(SHARP_LIST_ITEMS_TABLE_NAME, selection, selectionArgs);
	    break;
	case STORE:
	    count = db.delete(STORE_TABLE_NAME, selection, selectionArgs);
	    break;
	default:
	    throw new RuntimeException("Unkown URI: " + uri);
	}

	getContext().getContentResolver().notifyChange(uri, null);
	return count;
    }

    @Override
    /*
     * (non-Javadoc)
     * @see android.content.ContentProvider#update(android.net.Uri, android.content.ContentValues, java.lang.String, java.lang.String[])
     * this method will update a row in a table using specific calues and selection parameters
     */
    public int update(final Uri uri, final ContentValues values, final String selection,final String[] selectionArgs) {
		final SQLiteDatabase db = dbHelper.getWritableDatabase();
		int count = 0;
		switch (sUriMatcher.match(uri)) {
			case SHOPPING_ITEM:
			    count = db.update(SHOPPING_ITEM_TABLE_NAME, values, selection,selectionArgs);
			    break;
			case SHOPPING_ITEM_DIR:
			    count = db.update(SHOPPING_ITEM_TABLE_NAME, values,
				    COLUMN_ID
					    + "="
					    + uri.getPathSegments().get(1)
					    + (!TextUtils.isEmpty(selection) ? " AND ("
						    + selection + ")" : ""), selectionArgs);
			    break;
			case SHARP_LIST_ITEMS:
			    count = db.update(SHARP_LIST_ITEMS_TABLE_NAME, values, selection,selectionArgs);
			    break;
			case STORE:
			    count = db.update(STORE_TABLE_NAME, values, selection,selectionArgs);
			    break;
			default:
			    throw new RuntimeException("Unknown URI " + uri);
			}
	
		getContext().getContentResolver().notifyChange(uri, null);
		
		return count;
    }
}
