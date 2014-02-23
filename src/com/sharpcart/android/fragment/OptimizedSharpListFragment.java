package com.sharpcart.android.fragment;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.sharpcart.android.R;
import com.sharpcart.android.model.ImageResource;
import com.sharpcart.android.model.ShoppingItem;
import com.sharpcart.android.model.Store;
import com.sharpcart.android.provider.SharpCartContentProvider;
import com.sharpcart.android.utilities.SharpCartUtilities;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;

public class OptimizedSharpListFragment extends Fragment {

	private static final String TAG = OptimizedSharpListFragment.class.getSimpleName();
	private ArrayList<Store> optimizedStores;
	private TableRow optimizationTableHeaderRow;
	private TableLayout optimizationTableHeader;
	private TableLayout optimizationTableBody;
    private Drawable d;
    private Cursor cursor;
    private TextView longestItemDescription;
    
    private static final String[] PROJECTION_IMAGELOCATION = new String[] {
	    SharpCartContentProvider.COLUMN_IMAGE_LOCATION};
    
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
        final Bundle savedInstanceState) {
    	
    	final View view = inflater.inflate(R.layout.optimized_sharp_list, container, false);
    	
    	optimizationTableBody = (TableLayout) view.findViewById(R.id.optimizationTableBody);
    	optimizationTableBody.setStretchAllColumns(true);
    	
    	optimizationTableHeader = (TableLayout) view.findViewById(R.id.optimizationTableHeader);
    	optimizationTableHeader.setStretchAllColumns(true);
    	
    	longestItemDescription = new TextView(view.getContext());
    	
    	//Create table body 
    	createBody(optimizationTableBody, view.getContext());
    	
    	//Create table header row
    	createHeader(optimizationTableHeader, view.getContext(),longestItemDescription);
    	
    	return view;
    }
    
    public void setOptimizedStores(final ArrayList<Store> optimizedStores)
    {
    	this.optimizedStores = optimizedStores;
    	markBestPricePerUnit(this.optimizedStores);
    }
    
    private void createHeader(final TableLayout table,final Context context, final TextView longestItemDescription)
    {
    	//First clear any previous items in the table layout
    	table.removeAllViews();
    	
    	optimizationTableHeaderRow = new TableRow(context);
    	
    	final TableRow totalCostTableRow = new TableRow(context);
    	
        if (optimizedStores!=null)
        {
        	//add first column 
        	final TextView itemImageLength = new TextView(context);
        	final TextView empty3 = new TextView(context);
        	
        	final TextView label = new TextView(context);
        	
        	label.setLayoutParams(new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
        	label.setText("Total Cost");
        	label.setGravity(Gravity.CENTER);
        	label.setTextAppearance(context, android.R.style.TextAppearance_Medium);
        	label.setTextColor(Color.GREEN);
        	
        	itemImageLength.setMinimumWidth(130); //width of item image
        	longestItemDescription.setMaxHeight(0);
        	
        	optimizationTableHeaderRow.addView(itemImageLength);
        	optimizationTableHeaderRow.addView(longestItemDescription);
        	
        	totalCostTableRow.addView(empty3);
        	totalCostTableRow.addView(label);
        	
	        for (int i=0;i<optimizedStores.size();i++)
	        {
		        final ImageView storeImage = new ImageView(context);
		        
				try {
					for (final ImageResource imageResource : SharpCartUtilities.getInstance().getStoreImages())
					{
						if (imageResource.getName().equalsIgnoreCase(optimizedStores.get(i).getName()))
						{
							storeImage.setImageResource(imageResource.getDrawableResourceId());
						}
					}
				    
					
				} catch (final Exception ex) {
				    Log.d("ShoppingItemAdapter", ex.getLocalizedMessage());
				}
				    
		        //configure store image
				storeImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
				storeImage.setMinimumHeight(50);
				storeImage.setMinimumWidth(50);
     		   
				optimizationTableHeaderRow.addView(storeImage);
				
				final TextView storeTotalCost = new TextView(context);
				//storeTotalCost.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
				storeTotalCost.setText("$ "+Double.toString(Math.round(optimizedStores.get(i).getTotal_cost() * 100.0) / 100.0));
				storeTotalCost.setGravity(Gravity.CENTER);
				storeTotalCost.setTextAppearance(context, android.R.style.TextAppearance_Medium);
				storeTotalCost.setTextColor(Color.GREEN);
				
				totalCostTableRow.addView(storeTotalCost);
				
	        }
	
	        table.addView(optimizationTableHeaderRow, new TableLayout.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT)); 

        }
    }
    
    private void createBody(final TableLayout table,final Context context)
    {
        if ((optimizedStores.size()!=0)&&(optimizedStores!=null))
        {
        	
        	//add first total cost row
        	final TableRow totalCostTableRow = new TableRow(context);
        	final TextView empty = new TextView(context);
        	final TextView label = new TextView(context);
        	
        	label.setLayoutParams(new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
        	label.setText("Total Cost");
        	label.setGravity(Gravity.CENTER);
        	label.setTextAppearance(context, android.R.style.TextAppearance_Medium);
        	label.setTextColor(Color.GREEN);
        	
        	totalCostTableRow.addView(empty);
        	totalCostTableRow.addView(label);
        	
	        for (int i=0;i<optimizedStores.size();i++)
	        {
				final TextView storeTotalCost = new TextView(context);
				//storeTotalCost.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
				storeTotalCost.setText("$ "+Double.toString(Math.round(optimizedStores.get(i).getTotal_cost() * 100.0) / 100.0));
				storeTotalCost.setGravity(Gravity.CENTER);
				storeTotalCost.setTextAppearance(context, android.R.style.TextAppearance_Medium);
				storeTotalCost.setTextColor(Color.GREEN);
				
				totalCostTableRow.addView(storeTotalCost);
				
	        }
	
	        table.addView(totalCostTableRow, new TableLayout.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT));    
       
        	final List<ShoppingItem> shoppingItems = optimizedStores.get(0).getItems();
        	
        	//iterate over all the items for each store and present the item information
        	for (int i=0;i<optimizedStores.get(0).getItems().size();i++)
        	{
        		final TableRow itemRow = new TableRow(context);
        		
        		final TextView itemDescription = new TextView(context);
        		
        		final ImageView imageView = new ImageView(context);
        		
        		new TableLayout.LayoutParams
				  (android.view.ViewGroup.LayoutParams.MATCH_PARENT,android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        		
        		try {
        		    // get input stream
        			cursor = context.getContentResolver().query(
        					SharpCartContentProvider.CONTENT_URI_SHOPPING_ITEMS, 
        					PROJECTION_IMAGELOCATION, 
        					SharpCartContentProvider.COLUMN_ID + "='" + shoppingItems.get(i).getId()+"'", 
        					null, 
        					null);
        			
        			cursor.moveToFirst();
        			final int image_location_index = cursor.getColumnIndexOrThrow(SharpCartContentProvider.COLUMN_IMAGE_LOCATION);
        			final String shoppingItemImageLocation = cursor.getString(image_location_index).replaceFirst("/", "");
        			
        		    final InputStream ims = context.getAssets().open(shoppingItemImageLocation);
        		    
        		    // load image as Drawable
        		    d = Drawable.createFromStream(ims, null);
        		    
        		    // set image to ImageView
        		   imageView.setImageDrawable(d);
        		   //imageView.setScaleType(ImageView.ScaleType.CENTER);
        		   imageView.setMinimumHeight(100);
        		   imageView.setMinimumWidth(100);
        		   imageView.setPadding(5, 5, 5, 5);
        		   
        		   cursor.close();
        		   
        		} catch (final IOException ex)
        		{
        			
        		}
        		
        		itemDescription.setGravity(Gravity.LEFT);
        		itemDescription.setTextAppearance(context, android.R.style.TextAppearance_Medium);
        		itemDescription.setPadding(10,0,10,0);
        		//itemDescription.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        		itemDescription.setText(shoppingItems.get(i).getDescription());
        		//itemDescription.setBackgroundResource(R.drawable.shopping_item_border);
        		itemDescription.setTextColor(Color.WHITE);
        		
        		//save longest item description
        		if (longestItemDescription.getText().length()<itemDescription.getText().length())
        			longestItemDescription.setText(itemDescription.getText());
        		
        		//itemRow.setLayoutParams(tableRowParams);
        		itemRow.setBackgroundResource(R.drawable.shopping_item_border);
        		//itemRow.setPadding(20, 20, 20, 20);
        		itemRow.addView(imageView);
        		itemRow.addView(itemDescription);
        		
        		//Iterate over each store 
        		for (int x=0;x<optimizedStores.size();x++)
        		{
            		final LinearLayout itemInformation = new LinearLayout(context);
        			final TextView itemPrice = new TextView(context);
        			final TextView itemPricePerUnit = new TextView(context);
        			
        			itemInformation.setOrientation(LinearLayout.VERTICAL);
        			itemInformation.addView(itemPrice);
        			itemInformation.addView(itemPricePerUnit);
        			
        			itemPrice.setGravity(Gravity.LEFT);
        			itemPrice.setTextColor(Color.WHITE);
        			final ShoppingItem item =optimizedStores.get(x).getItems().get(i);
        			
        			if (item.getPrice()!=0)
        			{
    					itemPricePerUnit.setText("$"+Double.toString(Math.round(item.getPrice_per_unit() * 100.0) / 100.0)+"/"+item.getUnit());
    					
    					itemPrice.setText("$ "+Double.toString(Math.round(item.getTotal_price() * 100.0) / 100.0)+"\n"+
    								Double.toString(item.getQuantity())+ " "+item.getUnit());
    					
        				if (item.isBest_price_per_unit())
        				{
        					itemPricePerUnit.setTextColor(Color.GREEN);
        				}
        				else
        				{
        					itemPricePerUnit.setTextColor(Color.WHITE);
        				}
        			}
        			else
        				itemPrice.setText("Not Sold Here \n");
        			
        			//itemPrice.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        			itemInformation.setBackgroundResource(R.drawable.shopping_item_border);
        			
            		//set itemInformation height/width to match that of the item image
        			itemInformation.setMinimumHeight(100);
        			itemInformation.setMinimumWidth(100);
            		
        			itemRow.addView(itemInformation);
        		
        		}
        		
    	        table.addView(itemRow, new TableLayout.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT)); 
        	}
        }
    }
    
    public void refresh()
    {
    	//Create table header row
    	createHeader(optimizationTableBody, getView().getContext(),longestItemDescription);
    	
    	//Create table body 
    	createBody(optimizationTableBody, getView().getContext());
    }
    
    @Override
	public void onDetach()
    {
    	super.onDetach();
    	getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
    }
    
    /*
     * go over all the items in the optimized stores list and mark for each item if it has the best price per unit
     */
    private void markBestPricePerUnit(final ArrayList<Store> optimizedStores)
    {
    	int storeIndex = 0;
    	final int totalAmountOfItems = optimizedStores.get(0).getItems().size();
    	int bestPricePerUnitStoreIndex=0;
    	
		for (int i=0;i<totalAmountOfItems;i++)
		{
	    	while (storeIndex<(optimizedStores.size()-1))
	    	{
    			if (optimizedStores.get(bestPricePerUnitStoreIndex).getItems().get(i).getPrice_per_unit()<optimizedStores.get(storeIndex+1).getItems().get(i).getPrice_per_unit())
    			{
    				optimizedStores.get(bestPricePerUnitStoreIndex).getItems().get(i).setBest_price_per_unit(true);
    				optimizedStores.get(storeIndex+1).getItems().get(i).setBest_price_per_unit(false);
    			} else
    			{
    				optimizedStores.get(storeIndex+1).getItems().get(i).setBest_price_per_unit(true);
    				optimizedStores.get(bestPricePerUnitStoreIndex).getItems().get(i).setBest_price_per_unit(false);
       				bestPricePerUnitStoreIndex = storeIndex+1;
    			}
    			
    			storeIndex++;
    		}
    		bestPricePerUnitStoreIndex = 0;
    		storeIndex = 0;
    	}
    }
    
}
