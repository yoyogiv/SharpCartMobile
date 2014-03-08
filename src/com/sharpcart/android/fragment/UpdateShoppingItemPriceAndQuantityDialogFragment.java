package com.sharpcart.android.fragment;

import com.sharpcart.android.R;
import com.sharpcart.android.fragment.EmailSharpListDialogFragment.EmailSharpListDialogFragmentListener;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class UpdateShoppingItemPriceAndQuantityDialogFragment extends DialogFragment {
	
	private EditText quantity;
	private EditText price;
	
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
        	shoppingItemId = bundle.getInt("shoppingItemId");
        else
        	shoppingItemId = 0;
        
	    // Inflate and set the layout for the dialog
	    // Pass null as the parent view because its going in the dialog layout
        final View view = inflater.inflate(R.layout.update_shopping_item_price_and_quantity_dialog, null);
        
        quantity = (EditText)view.findViewById(R.id.change_quantity);
        price = (EditText)view.findViewById(R.id.change_price);
        
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