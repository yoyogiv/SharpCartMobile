package com.sharpcart.android.custom;

import com.sharpcart.android.adapter.MainSharpListItemAdapter;
import com.sharpcart.android.adapter.MainSharpListItemAdapter.ShoppingItemViewContainer;
import com.sharpcart.android.model.MainSharpList;
import com.sharpcart.android.provider.SharpCartContentProvider;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewParent;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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
	
	protected void onDraw(Canvas canvas) {
	    super.onDraw(canvas);
	
	}

	public boolean onKeyPreIme(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
	        
	    	//update shopping item quantity    
	    	updateShoppingItemQuantity();
	        
	    	//update MainSharpListAdapter cursor
	    	ViewParent viewParent = this.getParent();
	    	ViewParent viewGrandParent = viewParent.getParent();
	    	((MainSharpListItemAdapter)((ListView) viewGrandParent).getAdapter()).updateCursor();
	    	
	        return false;
	    }
	    
	    return super.dispatchKeyEvent(event);
	}
	
	private int updateShoppingItemQuantity()
	{
		View view = (View) getParent();
		ShoppingItemViewContainer holder = (ShoppingItemViewContainer) view.getTag();
		
		try {
			double itemQuantity = Double.valueOf(getText().toString());
			
			//Update MainSharpList object
			MainSharpList.getInstance().setItemQuantity(holder.itemId, itemQuantity);
			
			//Update db
			ContentValues cv = new ContentValues();
			cv.put(SharpCartContentProvider.COLUMN_QUANTITY, itemQuantity);
			
			int count = view.getContext().getApplicationContext().getContentResolver().update(
					SharpCartContentProvider.CONTENT_URI_SHARP_LIST_ITEMS,
					cv,
					SharpCartContentProvider.COLUMN_ID+"="+holder.itemId, 
					null);
			
			return count;
		   
		} catch (NumberFormatException ex)
		{
			Log.d(TAG,ex.getMessage());
		}
		
		return 0;
	}
}
