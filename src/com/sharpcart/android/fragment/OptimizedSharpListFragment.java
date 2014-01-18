package com.sharpcart.android.fragment;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.sharpcart.android.R;
import com.sharpcart.android.model.ImageResource;
import com.sharpcart.android.model.ShoppingItem;
import com.sharpcart.android.model.Store;
import com.sharpcart.android.utilities.SharpCartUtilities;

import android.app.Notification.Style;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;

public class OptimizedSharpListFragment extends Fragment {

	private static final String TAG = OptimizedSharpListFragment.class.getSimpleName();
	private ArrayList<Store> optimizedStores;
	private TableLayout optimizationTableHeader;
	private TableLayout optimizationTableBody;
	
    private Drawable d;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
    	
    	final View view = inflater.inflate(R.layout.optimized_sharp_list, container, false);
    	optimizationTableHeader = (TableLayout) view.findViewById(R.id.optimizationTableHeader);
    	optimizationTableHeader.setStretchAllColumns(true);
    	
    	optimizationTableBody = (TableLayout) view.findViewById(R.id.optimizationTableBody);
    	optimizationTableBody.setStretchAllColumns(true);
    	
    	//Create table header row
    	createHeader(optimizationTableBody, view.getContext());
    	
    	//Create table body 
    	createBody(optimizationTableBody, view.getContext());
    	
    	return view;
    }
    
    public void setOptimizedStores(ArrayList<Store> optimizedStores)
    {
    	this.optimizedStores = optimizedStores;
    }
    
    private void createHeader(TableLayout table,Context context)
    {
    	//First clear any previous items in the table layout
    	table.removeAllViews();
    	
    	TableRow storeTableRow = new TableRow(context);
    	TableRow totalCostTableRow = new TableRow(context);
    	
        if (optimizedStores!=null)
        {
        	//add first column 
        	TextView empty = new TextView(context);
        	TextView label = new TextView(context);
        	
        	label.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        	label.setText("Total Cost");
        	label.setGravity(Gravity.CENTER);
        	label.setTextAppearance(context, android.R.style.TextAppearance_Medium);
        	label.setTextColor(Color.GREEN);
        	
        	storeTableRow.addView(empty);
        	totalCostTableRow.addView(label);
        	
	        for (int i=0;i<optimizedStores.size();i++)
	        {
		        ImageView storeImage = new ImageView(context);
		        
		        
		        storeImage.setAdjustViewBounds(true);
		        //storeImage.setMinimumWidth(40);
		        //storeImage.setMinimumHeight(40);
		        //storeImage.setMaxHeight(100);
		       //storeImage.setMaxWidth(100);
		        
				try {
					/*
				    // get input stream
					String shoppingItemImageLocation = ((Store)optimizedStores.get(i)).getStore_image_location().replaceFirst("/", "");
					
				    InputStream ims = context.getAssets().open(shoppingItemImageLocation);
				    
				    // load image as Drawable
				    d = Drawable.createFromStream(ims, null);
				    
				    // set image to ImageView
				    storeImage.setImageDrawable(d);
				    */	
					for (ImageResource imageResource : SharpCartUtilities.getInstance().getStoreImages())
					{
						if (imageResource.getName().equalsIgnoreCase(((Store)optimizedStores.get(i)).getName()))
						{
							storeImage.setImageResource(imageResource.getDrawableResourceId());
						}
					}
				    
					
				} catch (Exception ex) {
				    Log.d("ShoppingItemAdapter", ex.getLocalizedMessage());
				}
				    
		        
				storeTableRow.addView(storeImage);
				
				TextView storeTotalCost = new TextView(context);
				storeTotalCost.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
				storeTotalCost.setText("$ "+Double.toString(Math.round(((Store)optimizedStores.get(i)).getTotal_cost() * 100.0) / 100.0));
				storeTotalCost.setGravity(Gravity.CENTER);
				storeTotalCost.setTextAppearance(context, android.R.style.TextAppearance_Medium);
				storeTotalCost.setTextColor(Color.GREEN);
				
				totalCostTableRow.addView(storeTotalCost);
				
	        }
	
	        table.addView(storeTableRow, new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)); 
	        table.addView(totalCostTableRow, new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));    

        }
    }
    
    private void createBody(TableLayout table,Context context)
    {
        if ((optimizedStores.size()!=0)&&(optimizedStores!=null))
        {
        	
        	List<ShoppingItem> shoppingItems = ((Store)optimizedStores.get(0)).getItems();
        	
        	//iterate over all the items for each store and present the item information
        	for (int i=0;i<((Store)optimizedStores.get(0)).getItems().size();i++)
        	{
        		TableRow itemRow = new TableRow(context);
        		
        		TextView itemDescription = new TextView(context);
        		
        		TableLayout.LayoutParams tableRowParams=
        				  new TableLayout.LayoutParams
        				  (TableLayout.LayoutParams.MATCH_PARENT,TableLayout.LayoutParams.WRAP_CONTENT);
        		
        		
        		itemDescription.setGravity(Gravity.LEFT);
        		itemDescription.setTextAppearance(context, android.R.style.TextAppearance_Medium);
        		itemDescription.setPadding(10,0,10,0);
        		//itemDescription.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        		itemDescription.setText(shoppingItems.get(i).getDescription());
        		//itemDescription.setBackgroundResource(R.drawable.main_sharp_list_shopping_item_style);
        		itemDescription.setTextColor(Color.WHITE);
        		
        		//itemRow.setLayoutParams(tableRowParams);
        		//itemRow.setBackgroundResource(R.drawable.shopping_item_border);
        		//itemRow.setPadding(20, 20, 20, 20);
        		itemRow.addView(itemDescription);
        		
        		//Iterate over each store 
        		for (int x=0;x<optimizedStores.size();x++)
        		{
        			TextView itemPrice = new TextView(context);
        			
        			itemPrice.setGravity(Gravity.LEFT);
        			ShoppingItem item =((Store)optimizedStores.get(x)).getItems().get(i);
        			
        			if (item.getPrice()!=0)
        				itemPrice.setText("$ "+Double.toString(Math.round(item.getPrice() * 100.0) / 100.0)+"\n"+
        								Double.toString(item.getQuantity())+ " "+item.getUnit());
        			else
        				itemPrice.setText("Not Sold Here \n");
        			
        			//itemPrice.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        			itemPrice.setTextColor(Color.WHITE);
        			itemPrice.setBackgroundResource(R.drawable.shopping_item_border);
        			
        			itemRow.addView(itemPrice);
        		}
        		
    	        table.addView(itemRow, new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)); 
        	}
        }
    }
    
    private void createFooter(TableLayout table)
    {
    	
    }
    
    public void refresh()
    {
    	//Create table header row
    	createHeader(optimizationTableBody, this.getView().getContext());
    	
    	//Create table body 
    	createBody(optimizationTableBody, this.getView().getContext());
    }
}
