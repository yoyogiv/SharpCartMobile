package com.sharpcart.android.fragment;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.text.WordUtils;

import com.sharpcart.android.R;
import com.sharpcart.android.adapter.AutocompleteShoppingItemAdapter;
import com.sharpcart.android.adapter.AutocompleteShoppingItemAdapter.ShoppingItemViewContainer;
import com.sharpcart.android.adapter.ShoppingItemAdapter;
import com.sharpcart.android.dao.MainSharpListDAO;
import com.sharpcart.android.model.ImageResource;
import com.sharpcart.android.model.MainSharpList;
import com.sharpcart.android.model.ShoppingItem;
import com.sharpcart.android.provider.SharpCartContentProvider;
import com.sharpcart.android.utilities.SharpCartUtilities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class MainScreenFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

	private LinearLayout categoriesGallery;
	private int itemBackground;
	private Context mContext;
	private GridView shoppingItemsGridView;
	private ShoppingItemAdapter shoppingItemsAdapter;
	private String categoryId;
	private ImageView voiceSearchButton;
	private AutoCompleteTextView shoppingItemAutoCompleteSearchBar;
	private static final int VOICE_RECOGNITION_REQUEST_CODE = 1001;
	OnShoppingItemSelectedListener mCallback;

    // Container Activity must implement this interface
    public interface OnShoppingItemSelectedListener {
        public void onShoppingItemSelected();
    }
    
    @Override 
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        
        // Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.
		shoppingItemsAdapter = new ShoppingItemAdapter(getActivity(),null);
		shoppingItemsGridView.setAdapter(shoppingItemsAdapter); 
	    
        getLoaderManager().initLoader(0, null, this);
    }
    
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,final Bundle savedInstanceState) {
    	
    	final View view = inflater.inflate(R.layout.main_screen, container, false);
    	
		/* Category Images
		 * We are adding them to the array in the same order they show up on the web site
		 */
		final ArrayList<ImageResource> categoryImages = new ArrayList<ImageResource>();
		
		categoryImages.add(new ImageResource(R.drawable.produce, 3));
		categoryImages.add(new ImageResource(R.drawable.meat, 5));
		categoryImages.add(new ImageResource(R.drawable.dairy, 6));
		categoryImages.add(new ImageResource(R.drawable.bakery, 7));
		categoryImages.add(new ImageResource(R.drawable.organic, 22));
		categoryImages.add(new ImageResource(R.drawable.frozen, 18));
		categoryImages.add(new ImageResource(R.drawable.breakfast, 21));
		categoryImages.add(new ImageResource(R.drawable.grains_and_pasta, 16));
		categoryImages.add(new ImageResource(R.drawable.canned, 10));
		categoryImages.add(new ImageResource(R.drawable.snacks, 4));
		categoryImages.add(new ImageResource(R.drawable.condiments, 20));
		categoryImages.add(new ImageResource(R.drawable.beverages, 11));
		categoryImages.add(new ImageResource(R.drawable.baking, 12));
		categoryImages.add(new ImageResource(R.drawable.baby, 8));
		categoryImages.add(new ImageResource(R.drawable.pet, 9));
		categoryImages.add(new ImageResource(R.drawable.personal_care, 14));
		categoryImages.add(new ImageResource(R.drawable.paper, 15));
		categoryImages.add(new ImageResource(R.drawable.cleaning_supplies, 19));

		categoriesGallery = (LinearLayout)view.findViewById(R.id.categories_gallery);
		
		mContext = getActivity().getApplicationContext();
		
		/*Set a grey background; wraps around the images */
		final TypedArray a = getActivity().obtainStyledAttributes(R.styleable.CategoryGallery);
		itemBackground = a.getResourceId(R.styleable.CategoryGallery_android_galleryItemBackground, 1);
		a.recycle();
		
		/*Load category images into category horizontal view*/
		for (final ImageResource categoryImage : categoryImages)
		{
			final ImageView imageView = new ImageView(mContext);
			imageView.setImageResource(categoryImage.getDrawableResourceId());
			imageView.setBackgroundResource(itemBackground);
			imageView.setId(categoryImage.getDatabaseId()); //We are using the category database id to set the image view id so we can latter use it when the user clicks on the image
			
			/*Set onClick event for each category image */
			imageView.setOnClickListener(new OnClickListener()
			{

		    	   @Override
		    	   public void onClick(final View v) 
		    	   {
			    	    //update shopping items grid view with the items in the category the user clicked
			    	    showCategoryItems(v.getId());
		    	   }
		    });
			
			categoriesGallery.addView(imageView);
			
		}
		
		//initialize our autocomplete search 
	    shoppingItemAutoCompleteSearchBar = (AutoCompleteTextView) view.findViewById(R.id.autoCompleteTextView);
	    final AutocompleteShoppingItemAdapter mAdapter = new AutocompleteShoppingItemAdapter(getActivity());  
	    shoppingItemAutoCompleteSearchBar.setAdapter(mAdapter);
		
	    shoppingItemAutoCompleteSearchBar.setOnItemClickListener(new OnItemClickListener() 
	    {
	        @Override
	        public void onItemClick(final AdapterView<?> p, final View v, final int pos, final long id) {
	        	final ShoppingItemViewContainer holder = (ShoppingItemViewContainer) v.getTag();
	        	
    		   //Create a new shopping item object based on the item clicked
    		   final ShoppingItem selectedShoppingItem = new ShoppingItem();
    		   
    		   selectedShoppingItem.setId(holder.itemId);
    		   selectedShoppingItem.setShopping_item_category_id(holder.itemCategoryId);
    		   selectedShoppingItem.setShopping_item_unit_id(holder.itemUnitId);
    		   selectedShoppingItem.setCategory(SharpCartUtilities.getInstance().getCategoryName(holder.itemCategoryId));
    		   selectedShoppingItem.setUnit(SharpCartUtilities.getInstance().getUnitName(holder.itemUnitId));
    		   selectedShoppingItem.setName(holder.itemName);
    		   selectedShoppingItem.setDescription(holder.itemDescription);
    		   selectedShoppingItem.setQuantity(1.0);
    		   selectedShoppingItem.setImage_location(holder.itemImageLocation);
    		   
    		   //use the DAO object to insert the new shopping item object into the main sharp list table
    		   MainSharpListDAO.getInstance().addNewItemToMainSharpList(mContext.getContentResolver(), selectedShoppingItem);
    		      
    		   //update MainSharpList object
    		   MainSharpList.getInstance().addShoppingItemToList(selectedShoppingItem);
    		   MainSharpList.getInstance().setLastUpdated(new Timestamp(System.currentTimeMillis()).toString());
    		   
    		   //clear text
    		   shoppingItemAutoCompleteSearchBar.setText("");
    		   
    		   Toast.makeText(mContext,holder.itemDescription + " Added ",Toast.LENGTH_SHORT).show();	
	        }
		});
	    
	    //Set an onEditor action for autocomplete text in case the user clicks "done"
	    shoppingItemAutoCompleteSearchBar.setOnEditorActionListener(new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(final TextView v, final int actionId, final KeyEvent event) {
				
				if (EditorInfo.IME_ACTION_DONE == actionId) {
					if (shoppingItemAutoCompleteSearchBar.getText().length()!=0) //only add an item if there is text
					{
		    		   //Create a new extra item shopping item object
		    		   final ShoppingItem selectedShoppingItem = new ShoppingItem();
		    		   
		    		   final Random ran = new Random();
		    		   final int x = ran.nextInt(100) + 500;
		    		   
		    		   selectedShoppingItem.setId(x);
		    		   selectedShoppingItem.setShopping_item_category_id(23);
		    		   selectedShoppingItem.setShopping_item_unit_id(0);
		    		   selectedShoppingItem.setCategory("Extra");
		    		   selectedShoppingItem.setName(WordUtils.capitalizeFully(shoppingItemAutoCompleteSearchBar.getText().toString()));
		    		   selectedShoppingItem.setDescription(WordUtils.capitalizeFully(shoppingItemAutoCompleteSearchBar.getText().toString()));
		    		   selectedShoppingItem.setQuantity(1.0);
		    		   selectedShoppingItem.setImage_location("/images/shoppingItems/default.png");
		    		   
		    		   //use the DAO object to insert the new shopping item object into the main sharp list table
		    		   MainSharpListDAO.getInstance().addNewItemToMainSharpList(mContext.getContentResolver(), selectedShoppingItem);
		    		   	   
		    		   //update MainSharpList object
		    		   MainSharpList.getInstance().addShoppingItemToList(selectedShoppingItem);
		    		   
		    		   //inform the user
		    		   Toast.makeText(mContext,WordUtils.capitalizeFully(shoppingItemAutoCompleteSearchBar.getText().toString())+ " Added",Toast.LENGTH_SHORT).show();
		    		   
		    		   //clear text
		    		   shoppingItemAutoCompleteSearchBar.setText("");
					}
		    		   	
				}
				
				return false;
			}
		});
	    
		categoryId = "3";
	
		shoppingItemsGridView = (GridView) view.findViewById(R.id.shoppingItemsGridView);
		
		//Voice search action
		voiceSearchButton = (ImageView) view.findViewById(R.id.voiceSearchButton);
		voiceSearchButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(final View v) {
				voiceSearch();
			}
		});
		checkVoiceRecognition();
		
        // Inflate the layout for this fragment
        return view;
    }
    
	public void showCategoryItems(final int categoryId)
	{	
		this.categoryId = String.valueOf(categoryId);
		getLoaderManager().restartLoader(0, null, this);
	}
	
    @Override
    public void onAttach(final Activity activity) {
        super.onAttach(activity);
        
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnShoppingItemSelectedListener) activity;
        } catch (final ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnShoppingItemSelectedListener");
        }
    }
    
    public void updateSharpList()
    {
    	 mCallback.onShoppingItemSelected();
    }
    
    private void checkVoiceRecognition() {
    	
	  // Check if voice recognition is present
	  final PackageManager pm = getActivity().getPackageManager();
	
	  final List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
	
	  if (activities.size() == 0) {
		  voiceSearchButton.setEnabled(false);
	  }
    }
    
	private void voiceSearch() {
		final Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

		// Specify the calling package to identify your application
		intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass().getPackage().getName());

		// Given an hint to the recognizer about what the user is going to say
		// There are two form of language model available
		// 1.LANGUAGE_MODEL_WEB_SEARCH : For short phrases
		// 2.LANGUAGE_MODEL_FREE_FORM : If not sure about the words or phrases
		// and its domain.

		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,

		RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

		// Start the Voice recognizer activity for the result.
		startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
    }
	
	@Override
	public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		if (requestCode == VOICE_RECOGNITION_REQUEST_CODE) {
			;
			getActivity();
			// If Voice recognition is successful then it returns RESULT_OK
			if (resultCode == Activity.RESULT_OK) 
			{
				final ArrayList<String> textMatchList = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
				
				shoppingItemAutoCompleteSearchBar.setText(textMatchList.get(0));
				shoppingItemAutoCompleteSearchBar.showDropDown();

			// Result code for various error.
			} else if (resultCode == RecognizerIntent.RESULT_AUDIO_ERROR) {

				showToastMessage("Audio Error");

			} else if (resultCode == RecognizerIntent.RESULT_CLIENT_ERROR) {

				showToastMessage("Client Error");

			} else if (resultCode == RecognizerIntent.RESULT_NETWORK_ERROR) {

				showToastMessage("Network Error");

			} else if (resultCode == RecognizerIntent.RESULT_NO_MATCH) {

				showToastMessage("No Match");

			} else if (resultCode == RecognizerIntent.RESULT_SERVER_ERROR) {

				showToastMessage("Server Error");

			}
		}

		super.onActivityResult(requestCode, resultCode, data);

	}
	 
	 /**
	  * Helper method to show the toast message
	  **/
	 
	private void showToastMessage(final String message){
	   Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
	  }
	  
	@Override
	public void onLoadFinished(final android.support.v4.content.Loader<Cursor> loader,
			final Cursor data) {
	       // Swap the new cursor in.  (The framework will take care of closing the
	       // old cursor once we return.)
		shoppingItemsAdapter.swapCursor(data);
	}

	@Override
	public void onLoaderReset(final android.support.v4.content.Loader<Cursor> loader) {
        // This is called when the last Cursor provided to onLoadFinished()
        // above is about to be closed.  We need to make sure we are no
        // longer using it.
		shoppingItemsAdapter.swapCursor(null);
		
	}

	@Override
	public android.support.v4.content.Loader<Cursor> onCreateLoader(final int arg0,final Bundle arg1) {
        final CursorLoader cl = new CursorLoader(getActivity(), 
				SharpCartContentProvider.CONTENT_URI_SHOPPING_ITEMS,
				null,//get all columns since we are going to use all of them
				SharpCartContentProvider.COLUMN_SHOPPING_ITEM_CATEGORY_ID + "='" + categoryId+"' AND " +
				SharpCartContentProvider.COLUMN_ACTIVE + "= '1'", 
				null,
				SharpCartContentProvider.DEFAULT_SORT_ORDER);
        
        cl.setUpdateThrottle(2000); // update at most every 2 seconds.
        return cl;
	}
		
}
