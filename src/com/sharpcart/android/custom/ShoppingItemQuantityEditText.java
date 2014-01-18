package com.sharpcart.android.custom;

import com.sharpcart.android.adapter.MainSharpListItemAdapter;
import com.sharpcart.android.adapter.MainSharpListItemAdapter.ShoppingItemViewContainer;
import com.sharpcart.android.model.MainSharpList;
import com.sharpcart.android.provider.SharpCartContentProvider;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewParent;
import android.widget.EditText;
import android.widget.ListView;

public class ShoppingItemQuantityEditText extends EditText {

    private static final String TAG = ShoppingItemQuantityEditText.class.getSimpleName();

    public ShoppingItemQuantityEditText(Context context) {
    	super(context);
	}
	
	public ShoppingItemQuantityEditText(Context context, AttributeSet attrs) {
	    super(context, attrs);
	    // this Contructure required when you are using this view in xml
	}
	
	public ShoppingItemQuantityEditText(Context context, AttributeSet attrs, int defStyle) {
	    super(context, attrs, defStyle);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
	    super.onDraw(canvas);
	
	}

	@Override
	public boolean onKeyPreIme(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
	        
	    	//update shopping item quantity    
	    	updateShoppingItemQuantity();
	        
	    	//update MainSharpListAdapter cursor
	    	final ViewParent viewParent = getParent();
	    	final ViewParent viewGrandParent = viewParent.getParent();
	    	((MainSharpListItemAdapter)((ListView) viewGrandParent).getAdapter()).updateCursor();
	    	
	        return false;
	    }
	    
	    return super.dispatchKeyEvent(event);
	}
	
	private int updateShoppingItemQuantity()
	{
		final View view = (View) getParent();
		final ShoppingItemViewContainer holder = (ShoppingItemViewContainer) view.getTag();
		
		try {
			final double itemQuantity = Double.valueOf(getText().toString());
			
			//Update MainSharpList object
			MainSharpList.getInstance().setItemQuantity(holder.itemId, itemQuantity);
			
			//Update db
			final ContentValues cv = new ContentValues();
			cv.put(SharpCartContentProvider.COLUMN_QUANTITY, itemQuantity);
			
			final int count = view.getContext().getApplicationContext().getContentResolver().update(
					SharpCartContentProvider.CONTENT_URI_SHARP_LIST_ITEMS,
					cv,
					SharpCartContentProvider.COLUMN_ID+"="+holder.itemId, 
					null);
			
			return count;
		   
		} catch (final NumberFormatException ex)
		{
			Log.d(TAG,ex.getMessage());
		}
		
		return 0;
	}
}
