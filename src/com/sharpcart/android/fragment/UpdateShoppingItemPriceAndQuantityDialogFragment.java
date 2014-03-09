package com.sharpcart.android.fragment;

import java.io.IOException;
import java.io.InputStream;

import com.sharpcart.android.R;
import com.sharpcart.android.fragment.EmailSharpListDialogFragment.EmailSharpListDialogFragmentListener;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class UpdateShoppingItemPriceAndQuantityDialogFragment extends DialogFragment {
	
	private EditText quantity;
	private EditText price;
	private TextView description;
	private String shoppingItemDescription;
	private String shoppingItemImageLocation;
	private ImageView shoppingItemImageView;
	private Drawable d;
	
    public interface UpdateShoppingItemPriceAndQuantityDialogFragmentListener {
        void onUpdateShoppingItemPriceAndQuantityDialogFragment(int shoppingItemId,double quantity,double price);
    }
    
	public UpdateShoppingItemPriceAndQuantityDialogFragment() {
		
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.DialogFragment#onCreateDialog(android.os.Bundle)
	 */
	@Override
	public Dialog onCreateDialog(final Bundle savedInstanceState) {

	    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    // Get the layout inflater
	    final LayoutInflater inflater = getActivity().getLayoutInflater();
	    
        final Bundle bundle = getArguments();
        final int shoppingItemId;
        
        if (bundle!=null)
        {
        	shoppingItemId = bundle.getInt("shoppingItemId",0);
        	shoppingItemDescription = bundle.getString("shoppingItemDescription");
        	shoppingItemImageLocation = bundle.getString("shoppingItemImageLocation");
        }
        else
        	shoppingItemId = 0;
        
	    // Inflate and set the layout for the dialog
	    // Pass null as the parent view because its going in the dialog layout
        final View view = inflater.inflate(R.layout.update_shopping_item_price_and_quantity_dialog, null);
        
        quantity = (EditText)view.findViewById(R.id.change_quantity);
        price = (EditText)view.findViewById(R.id.change_price);
        
        description = (TextView)view.findViewById(R.id.update_item_description);
        description.setText(shoppingItemDescription);
        
        shoppingItemImageView = (ImageView)view.findViewById(R.id.update_item_image_view);
        
		try {
		    // get input stream				
		    final InputStream ims = getActivity().getAssets().open(shoppingItemImageLocation);
		    
		    // load image as Drawable
		    d = Drawable.createFromStream(ims, null);
		    
		    // set image to ImageView
		    shoppingItemImageView.setImageDrawable(d);
			
			
		} catch (final IOException ex) {
		    Log.d("UpdateShoppingItemPriceAndQuantityDialogFragment", ex.getLocalizedMessage());
		}
		
	    builder.setView(view)
	    // Add action buttons
	           .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
	               @Override
	               public void onClick(final DialogInterface dialog, final int id) {
	                   //Update item price and quantity
	            	   if (shoppingItemId!=0)
	            	   {
	   		            // Return input text to activity
	   					final UpdateShoppingItemPriceAndQuantityDialogFragmentListener activity = (UpdateShoppingItemPriceAndQuantityDialogFragmentListener) getActivity();
	   		            
	   					try
	   					{
	   						activity.onUpdateShoppingItemPriceAndQuantityDialogFragment(shoppingItemId, Double.valueOf(quantity.getText().toString()), Double.valueOf(price.getText().toString()));
	   					} catch (NumberFormatException ex)
	   					{
	   						
	   					}
	            	   }
	            		   
	               }
	           })
	           .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
	               @Override
				public void onClick(final DialogInterface dialog, final int id) {
	            	   UpdateShoppingItemPriceAndQuantityDialogFragment.this.getDialog().cancel();
	               }
	           });

	    return builder.create();
	}


}
