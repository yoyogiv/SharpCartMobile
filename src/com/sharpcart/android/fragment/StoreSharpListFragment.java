package com.sharpcart.android.fragment;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

import org.apache.commons.lang3.text.WordUtils;

import com.sharpcart.android.R;
import com.sharpcart.android.adapter.AutocompleteShoppingItemAdapter;
import com.sharpcart.android.adapter.StoreSharpListExpandableAdapter;
import com.sharpcart.android.adapter.AutocompleteShoppingItemAdapter.ShoppingItemViewContainer;
import com.sharpcart.android.dao.MainSharpListDAO;
import com.sharpcart.android.model.ImageResource;
import com.sharpcart.android.model.MainSharpList;
import com.sharpcart.android.model.ShoppingItem;
import com.sharpcart.android.model.Store;
import com.sharpcart.android.utilities.SharpCartUtilities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView.OnEditorActionListener;

public class StoreSharpListFragment extends Fragment {
	private static final String TAG = StoreSharpListFragment.class.getSimpleName();
	
	private ExpandableListView storeSharpListItems;
	private ImageView storeImage;
	private StoreSharpListExpandableAdapter storeSharpListItemAdapter;
	private String storeName;
	private ArrayList<Store> optimizedStores;
	private DecimalFormat df;
	private AutoCompleteTextView completeTextView;
	private ImageView voiceSearchButton;
	private List<String> listDataHeader;
	private HashMap<String, List<ShoppingItem>> listChildData;
	
	private static final int VOICE_RECOGNITION_REQUEST_CODE = 1001;
	public TextView totalCost;
	
	@Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
        final Bundle savedInstanceState) {
    	
    	final View view = inflater.inflate(R.layout.in_store_list_main_list_view, container, false);
    	   	
	    //initialize store sharp list list view
    	listDataHeader = new ArrayList<String>();
    	listChildData = new HashMap<String, List<ShoppingItem>>();
    	storeSharpListItems = (ExpandableListView) view.findViewById(R.id.inStoreExpandableListView);
    	storeImage = (ImageView) view.findViewById(R.id.storeImageView);
    	
    	if (storeName!=null && optimizedStores!=null)
    	{
    		//iterate over stores and find the store with the same name as the one the user choose
    		for (final Store store : optimizedStores)
    		{
    			if (store.getName().equalsIgnoreCase(storeName))
    			{
    				initForExpandableAdapter(listDataHeader,listChildData, store.getItems());
    				
    				storeSharpListItemAdapter = new StoreSharpListExpandableAdapter(getActivity(), 
    						listDataHeader,listChildData);
    				
    				try {
    					for (final ImageResource imageResource : SharpCartUtilities.getInstance().getStoreImages())
    					{
    						if (imageResource.getName().equalsIgnoreCase(storeName))
    						{
    							storeImage.setImageResource(imageResource.getDrawableResourceId());
    						}
    					}	    
    					
    				} catch (final Exception ex) {
    				    Log.d("ShoppingItemAdapter", ex.getLocalizedMessage());
    				}
    			}
    		}
    	}
    	
    	storeSharpListItems.setAdapter(storeSharpListItemAdapter);
    	
    	//Open all groups by default
    	for(int i=0; i < storeSharpListItemAdapter.getGroupCount(); i++)
    		storeSharpListItems.expandGroup(i);
    	
    	totalCost = (TextView) view.findViewById(R.id.toatlCostTextView);
    	
    	df = new DecimalFormat("#,###,##0.00");
    	
		//initialize our auto complete search 
	    completeTextView = (AutoCompleteTextView) view.findViewById(R.id.autoCompleteTextView);
	    final AutocompleteShoppingItemAdapter mAdapter = new AutocompleteShoppingItemAdapter(getActivity());  
	    completeTextView.setAdapter(mAdapter);
		
	    completeTextView.setOnItemClickListener(new OnItemClickListener() 
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
    		   MainSharpListDAO.getInstance().addNewItemToMainSharpList(getActivity().getContentResolver(), selectedShoppingItem);
    		   
    		   //update MainSharpList object
    		   MainSharpList.getInstance().addShoppingItemToList(selectedShoppingItem);
    		   MainSharpList.getInstance().setLastUpdated(new Timestamp(System.currentTimeMillis()).toString());
    		   
    		   //update in-store list
    		   storeSharpListItemAdapter.addItemToList(selectedShoppingItem);
    		   
    		   //clear text
    		   completeTextView.setText("");
    		   
    		   Toast.makeText(getActivity(),holder.itemDescription + " Added ",Toast.LENGTH_SHORT).show();	
	        }
		});
	    
	    //Set an onEditor action for autocomplete text in case the user clicks "done"
	    completeTextView.setOnEditorActionListener(new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(final TextView v, final int actionId, final KeyEvent event) {
				
				if (EditorInfo.IME_ACTION_DONE == actionId) {
					
		    		   //Create a new extra item shopping item object
		    		   final ShoppingItem selectedShoppingItem = new ShoppingItem();
		    		   
		    		   final Random ran = new Random();
		    		   final int x = ran.nextInt(100) + 500;
		    		   
		    		   selectedShoppingItem.setId(x);
		    		   selectedShoppingItem.setShopping_item_category_id(23);
		    		   selectedShoppingItem.setShopping_item_unit_id(0);
		    		   selectedShoppingItem.setCategory("Extra");
		    		   selectedShoppingItem.setName(WordUtils.capitalizeFully(completeTextView.getText().toString()));
		    		   selectedShoppingItem.setDescription(WordUtils.capitalizeFully(completeTextView.getText().toString()));
		    		   selectedShoppingItem.setQuantity(1.0);
		    		   selectedShoppingItem.setPackage_quantity(1.0);
		    		   selectedShoppingItem.setUnit("-");
		    		   selectedShoppingItem.setImage_location("/images/shoppingItems/default.png");
		    		   
		    		   //use the DAO object to insert the new shopping item object into the main sharp list table
		    		   MainSharpListDAO.getInstance().addNewItemToMainSharpList(getActivity().getContentResolver(), selectedShoppingItem);
		    		   
		    		   //update MainSharpList object
		    		   MainSharpList.getInstance().addShoppingItemToList(selectedShoppingItem);
		    		   
		    		   //update in-store list
		    		   storeSharpListItemAdapter.addItemToList(selectedShoppingItem);
		    		   
		    		   //inform the user
		    		   Toast.makeText(getActivity(),WordUtils.capitalizeFully(completeTextView.getText().toString())+ " Added",Toast.LENGTH_SHORT).show();   	
				
		    		   //clear text
		    		   completeTextView.setText("");
				}
				
				return false;
			}
		});
	    
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

	/**
	 * @return the storeName
	 */
	public String getStoreName() {
		return storeName;
	}

	/**
	 * @param storeName the storeName to set
	 */
	public void setStoreName(final String storeName) {
		this.storeName = storeName;
	}

	/**
	 * @return the optimizedStores
	 */
	public ArrayList<Store> getOptimizedStores() {
		return optimizedStores;
	}

	/**
	 * @param optimizedStores the optimizedStores to set
	 */
	public void setOptimizedStores(final ArrayList<Store> optimizedStores) {
		this.optimizedStores = optimizedStores;
	}
	
	public void updateTotalCost(final double itemPrice)
	{
		double totalCostNumber = Double.valueOf(totalCost.getText().toString());
		totalCostNumber+=itemPrice;
		totalCost.setText(df.format(totalCostNumber));
		
		storeSharpListItemAdapter.notifyDataSetChanged();
	}
	private void initForExpandableAdapter(final List<String> listDataHeader,
            final HashMap<String, List<ShoppingItem>> listChildData, List<ShoppingItem> shoppingItems)
	{
		//First we need to remove unavailable items and add extra items
		shoppingItems = removeUnavailableItemsAndAddExtraItems(shoppingItems);
		final HashSet<String> tempCategoryNameSet = new HashSet<String>();
		
		//now we need to convert our list into two lists: category headers and shopping items
		for (final ShoppingItem item : shoppingItems)
		{
			tempCategoryNameSet.add(WordUtils.capitalizeFully(item.getCategory()));
		}
		
		for (final String name : tempCategoryNameSet)
		{
			listDataHeader.add(name);
		}
		
		//Add "In Cart" section
		listDataHeader.add("In Cart");
		
		for (final String categoryName : listDataHeader)
		{
			List<ShoppingItem> items = new ArrayList<ShoppingItem>();
			
			for (final ShoppingItem item : shoppingItems)
			{
				if (item.getCategory().equalsIgnoreCase(categoryName))
				{
					items.add(item);
				}
			}
			
			listChildData.put(categoryName, items);
			items = null;
		}
	}
	
	private List<ShoppingItem> removeUnavailableItemsAndAddExtraItems(final List<ShoppingItem> shoppingItems)
	{
		//remove any item that has a price = 0 and 
		final ListIterator<ShoppingItem> li = shoppingItems.listIterator();
		while (li.hasNext())
		{
			final ShoppingItem item = li.next();
			if(item.getPrice()==0)
				li.remove();
			else
			{
				item.setPrice((item.getPackage_quantity()*item.getPrice_per_unit())/(item.getQuantity()/item.getPackage_quantity()));
				item.setQuantity(item.getQuantity()/item.getPackage_quantity());
			}
		}
		
		
		//add extra items from MainSharpList
		for (final ShoppingItem item : MainSharpList.getInstance().getMainSharpList())
		{
			if (item.getShopping_item_category_id()==23)
				shoppingItems.add(item);
		}

		
		return shoppingItems;
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
				
				completeTextView.setText(textMatchList.get(0));
				completeTextView.showDropDown();

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
	   Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
	  }
}
