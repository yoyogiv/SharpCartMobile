package com.sharpcart.android.fragment;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import com.sharpcart.android.R;
import com.sharpcart.android.model.Store;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;

public class OptimizedSharpListFragment extends Fragment {

	private static final String TAG = OptimizedSharpListFragment.class.getSimpleName();
	private ArrayList<Store> optimizedStores;
	private TableLayout optimizationTable;
    private Drawable d;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
    	
    	final View view = inflater.inflate(R.layout.optimized_sharp_list, container, false);
    	optimizationTable = (TableLayout) view.findViewById(R.id.optimizationTable);
    	
    	//Create table header row
    	createHeader(optimizationTable, view.getContext());
    	
    	return view;
    }
    
    public void setOptimizedStores(ArrayList<Store> optimizedStores)
    {
    	this.optimizedStores = optimizedStores;
    }
    
    private void createHeader(TableLayout table,Context context)
    {
    	TableRow tr = new TableRow(context);
        tr.setPadding(0,20,0,20);
        tr.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

        ImageView storeImage = new ImageView(context);
        
        storeImage.setMinimumWidth(100);
        storeImage.setMinimumHeight(100);
        
		/*Set a grey background; wraps around the images */
		TypedArray a = getActivity().obtainStyledAttributes(R.styleable.CategoryGallery);
		int itemBackground = a.getResourceId(R.styleable.CategoryGallery_android_galleryItemBackground, 1);
		a.recycle();
		
		storeImage.setBackgroundResource(itemBackground);
		
		try {
		    // get input stream
			String shoppingItemImageLocation = "images/stores/heb.png";
			
		    InputStream ims = context.getAssets().open(shoppingItemImageLocation);
		    
		    // load image as Drawable
		    d = Drawable.createFromStream(ims, null);
		    
		    // set image to ImageView
		    storeImage.setImageDrawable(d);
		    
		} catch (IOException ex) {
		    Log.d("ShoppingItemAdapter", ex.getLocalizedMessage());
		}
		    
        
        tr.addView(storeImage);

        table.addView(tr, new TableLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));    	
    }
    
    private void createBody(TableLayout table)
    {
    	
    }
    
    private void createFooter(TableLayout table)
    {
    	
    }
}
