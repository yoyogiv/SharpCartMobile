package com.sharpcart.android.fragment;

import java.util.ArrayList;

import com.sharpcart.android.R;
import com.sharpcart.android.adapter.AutocompleteShoppingItemAdapter;
import com.sharpcart.android.adapter.AutocompleteShoppingItemAdapter.ShoppingItemViewContainer;
import com.sharpcart.android.adapter.ShoppingItemAdapter;
import com.sharpcart.android.dao.MainSharpListDAO;
import com.sharpcart.android.model.CategoryImage;
import com.sharpcart.android.model.ShoppingItem;

import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainScreenFragment extends Fragment{

	private LinearLayout categoriesGallery;
	private int itemBackground;
	private Context mContext;
	private GridView shoppingItemsGridView;
	private ContentResolver mResolver;  // A content resolver for accessing the provider
	 
    OnShoppingItemSelectedListener mCallback;

    // Container Activity must implement this interface
    public interface OnShoppingItemSelectedListener {
        public void onShoppingItemSelected();
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
    	
    	final View view = inflater.inflate(R.layout.main_screen, container, false);
    	
		/* Category Images
		 * We are adding them to the array in the same order they show up on the web site
		 */
		ArrayList<CategoryImage> categoryImages = new ArrayList<CategoryImage>();
		
		categoryImages.add(new CategoryImage(R.drawable.produce, 3));
		categoryImages.add(new CategoryImage(R.drawable.meat, 5));
		categoryImages.add(new CategoryImage(R.drawable.dairy, 6));
		categoryImages.add(new CategoryImage(R.drawable.bakery, 7));
		categoryImages.add(new CategoryImage(R.drawable.organic, 22));
		categoryImages.add(new CategoryImage(R.drawable.frozen, 18));
		categoryImages.add(new CategoryImage(R.drawable.breakfast, 21));
		categoryImages.add(new CategoryImage(R.drawable.grains_and_pasta, 16));
		categoryImages.add(new CategoryImage(R.drawable.canned, 10));
		categoryImages.add(new CategoryImage(R.drawable.snacks, 4));
		categoryImages.add(new CategoryImage(R.drawable.condiments, 20));
		categoryImages.add(new CategoryImage(R.drawable.beverages, 11));
		categoryImages.add(new CategoryImage(R.drawable.baking, 12));
		categoryImages.add(new CategoryImage(R.drawable.baby, 8));
		categoryImages.add(new CategoryImage(R.drawable.pet, 9));
		categoryImages.add(new CategoryImage(R.drawable.personal_care, 14));
		categoryImages.add(new CategoryImage(R.drawable.paper, 15));
		categoryImages.add(new CategoryImage(R.drawable.cleaning_supplies, 19));

		categoriesGallery = (LinearLayout)view.findViewById(R.id.categories_gallery);
		
		mContext = getActivity().getApplicationContext();
		
		// Get the content resolver for the application
		mResolver = getActivity().getContentResolver();
		
		/*Set a grey background; wraps around the images */
		TypedArray a = getActivity().obtainStyledAttributes(R.styleable.CategoryGallery);
		itemBackground = a.getResourceId(R.styleable.CategoryGallery_android_galleryItemBackground, 1);
		a.recycle();
		
		/*Load category images into category horizontal view*/
		for (CategoryImage categoryImage : categoryImages)
		{
			ImageView imageView = new ImageView(mContext);
			imageView.setImageResource(categoryImage.getDrawableResourceId());
			imageView.setBackgroundResource(itemBackground);
			imageView.setId(categoryImage.getDatabaseId()); //We are using the category database id to set the image view id so we can latter use it when the user clicks on the image
			
			/*Set onClick event for each category image */
			imageView.setOnClickListener(new OnClickListener()
			{

		    	   @Override
		    	   public void onClick(View v) 
		    	   {
			    	    //update shopping items grid view with the items in the category the user clicked
			    	    showCategoryItems(v.getId());
		    	   }
		    });
			
			categoriesGallery.addView(imageView);
			
		}
		
		//initialize our autocomplete search 
	    final AutoCompleteTextView completeTextView = (AutoCompleteTextView) view.findViewById(R.id.autoCompleteTextView);
	    AutocompleteShoppingItemAdapter mAdapter = new AutocompleteShoppingItemAdapter(getActivity());  
	    completeTextView.setAdapter(mAdapter);
		
	    completeTextView.setOnItemClickListener(new OnItemClickListener() 
	    {
	        @Override
	        public void onItemClick(AdapterView<?> p, View v, int pos, long id) {
	        	ShoppingItemViewContainer holder = (ShoppingItemViewContainer) v.getTag();
	        	
    		   //Create a new shopping item object based on the item clicked
    		   ShoppingItem selectedShoppingItem = new ShoppingItem();
    		   
    		   selectedShoppingItem.setId(holder.itemId);
    		   selectedShoppingItem.setShopping_Item_Category_Id(holder.itemCategoryId);
    		   selectedShoppingItem.setShopping_Item_Unit_Id(holder.itemUnitId);
    		   selectedShoppingItem.setName(holder.itemName);
    		   selectedShoppingItem.setDescription(holder.itemDescription);
    		   selectedShoppingItem.setQuantity(1.0);
    		   selectedShoppingItem.setImage_Location(holder.itemImageLocation);
    		   
    		   //use the DAO object to insert the new shopping item object into the main sharp list table
    		   MainSharpListDAO.getInstance().addNewItemToMainSharpList(mContext.getContentResolver(), selectedShoppingItem);
    		   
    		   //update main sharp list fragment
    		   MainScreenFragment mainScreen = (MainScreenFragment) ((FragmentActivity) getActivity()).getSupportFragmentManager().findFragmentById(R.id.main_screen_fragment);
    		   mainScreen.updateSharpList();
    		    
    		   //clear text
    		   completeTextView.setText("");
    		   
    		  Toast.makeText(mContext,holder.itemDescription + " Added ",Toast.LENGTH_SHORT).show();	
	        }
		});
	    
		// create a populate a grid view with shopping items 
		shoppingItemsGridView = (GridView) view.findViewById(R.id.shoppingItemsGridView);
		shoppingItemsGridView.setAdapter(new ShoppingItemAdapter(getActivity())); 
		
        // Inflate the layout for this fragment
        return view;
    }
    
	public void showCategoryItems(int categoryId)
	{
		((ShoppingItemAdapter)shoppingItemsGridView.getAdapter()).setCategoryId(Integer.toString(categoryId));
		((ShoppingItemAdapter)shoppingItemsGridView.getAdapter()).updateCursor();
	}
	
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnShoppingItemSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnShoppingItemSelectedListener");
        }
    }
    
    public void updateSharpList()
    {
    	 mCallback.onShoppingItemSelected();
    }
}
