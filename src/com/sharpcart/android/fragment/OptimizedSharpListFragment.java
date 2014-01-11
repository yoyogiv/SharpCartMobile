package com.sharpcart.android.fragment;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.sharpcart.android.R;
import com.sharpcart.android.model.ShoppingItem;
import com.sharpcart.android.model.Store;

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
    	TableRow storeTableRow = new TableRow(context);
    	TableRow totalCostTableRow = new TableRow(context);
    	
        if (optimizedStores!=null)
        {
        	//add first column 
        	TextView empty = new TextView(context);
        	TextView label = new TextView(context);
        	
        	label.setText("Total Cost");
        	label.setGravity(Gravity.CENTER);
        	label.setTextAppearance(context, android.R.style.TextAppearance_Large);
        	
        	storeTableRow.addView(empty);
        	totalCostTableRow.addView(label);
        	
	        for (int i=0;i<optimizedStores.size();i++)
	        {
		        ImageView storeImage = new ImageView(context);
		        
		        storeImage.setMinimumWidth(50);
		        storeImage.setMinimumHeight(50);
		        
				/*Set a grey background; wraps around the images */
		        /*
				TypedArray a = getActivity().obtainStyledAttributes(R.styleable.CategoryGallery);
				int itemBackground = a.getResourceId(R.styleable.CategoryGallery_android_galleryItemBackground, 0);
				a.recycle();
				
				storeImage.setBackgroundResource(itemBackground);
				*/
		        
				try {
				    // get input stream
					String shoppingItemImageLocation = ((Store)optimizedStores.get(i)).getStore_image_location().replaceFirst("/", "");
					
				    InputStream ims = context.getAssets().open(shoppingItemImageLocation);
				    
				    // load image as Drawable
				    d = Drawable.createFromStream(ims, null);
				    
				    // set image to ImageView
				    storeImage.setImageDrawable(d);
				    
				} catch (IOException ex) {
				    Log.d("ShoppingItemAdapter", ex.getLocalizedMessage());
				}
				    
		        
				storeTableRow.addView(storeImage);
				
				TextView storeTotalCost = new TextView(context);
				storeTotalCost.setText("$ "+Double.toString(((Store)optimizedStores.get(i)).getTotal_cost()));
				storeTotalCost.setGravity(Gravity.CENTER);
				storeTotalCost.setTextAppearance(context, android.R.style.TextAppearance_Large);
				
				totalCostTableRow.addView(storeTotalCost);
				
	        }
	
	        table.addView(storeTableRow, new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)); 
	        table.addView(totalCostTableRow, new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));    

        }
    }
    
    private void createBody(TableLayout table,Context context)
    {
        if (optimizedStores!=null)
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
        		
        		//tableRowParams.setMargins(10, 10, 10, 10);
        		
        		itemDescription.setGravity(Gravity.LEFT);
        		itemDescription.setTextAppearance(context, android.R.style.TextAppearance_Medium);
        		itemDescription.setPadding(20,0,0,0);
        		itemDescription.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        		itemDescription.setText(shoppingItems.get(i).getDescription());
        		
        		itemRow.setLayoutParams(tableRowParams);
        		itemRow.setBackgroundResource(R.drawable.row_border);
        		itemRow.addView(itemDescription);
        		
        		//Iterate over each store 
        		for (int x=0;x<optimizedStores.size();x++)
        		{
        			TextView itemPrice = new TextView(context);
        			
        			itemPrice.setGravity(Gravity.LEFT);
        			ShoppingItem item =((Store)optimizedStores.get(x)).getItems().get(i);
        			
        			itemPrice.setText("$ "+Double.toString(item.getPrice())+"\n"+
        								Double.toString(item.getQuantity())+ " "+item.getUnit());
        			
        			itemPrice.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 100));
        			
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
}
