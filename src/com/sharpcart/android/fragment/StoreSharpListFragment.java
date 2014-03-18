package com.sharpcart.android.fragment;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

import org.apache.commons.lang3.text.WordUtils;

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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.sharpcart.android.R;
import com.sharpcart.android.adapter.AutocompleteShoppingItemAdapter;
import com.sharpcart.android.adapter.AutocompleteShoppingItemAdapter.ShoppingItemViewContainer;
import com.sharpcart.android.adapter.StoreSharpListExpandableAdapter;
import com.sharpcart.android.dao.MainSharpListDAO;
import com.sharpcart.android.model.ImageResource;
import com.sharpcart.android.model.MainSharpList;
import com.sharpcart.android.model.ShoppingListItem;
import com.sharpcart.android.model.StorePrices;
import com.sharpcart.android.utilities.SharpCartUtilities;

public class StoreSharpListFragment extends Fragment {
    private static final String TAG = StoreSharpListFragment.class
	    .getSimpleName();

    private ExpandableListView storeSharpListItems;
    private ImageView storeImage;
    private StoreSharpListExpandableAdapter storeSharpListItemAdapter;
    private String storeName;
    private ArrayList<StorePrices> optimizedStores;
    private DecimalFormat df;
    private AutoCompleteTextView shoppingItemAutoCompleteSearchBar;
    private ImageView voiceSearchButton;
    private List<String> categoryNameList;
    private HashMap<String, List<ShoppingListItem>> shoppingItemList;

    private static final int VOICE_RECOGNITION_REQUEST_CODE = 1001;
    public TextView totalCost;

    @Override
    public View onCreateView(final LayoutInflater inflater,
	    final ViewGroup container, final Bundle savedInstanceState) {

	final View view = inflater.inflate(
		R.layout.in_store_list_main_list_view, container, false);

	// initialize store sharp list list view
	categoryNameList = new ArrayList<String>();
	shoppingItemList = new HashMap<String, List<ShoppingListItem>>();
	storeSharpListItems = (ExpandableListView) view
		.findViewById(R.id.inStoreExpandableListView);
	storeImage = (ImageView) view.findViewById(R.id.storeImageView);

	if (storeName != null && optimizedStores != null) {
	    // iterate over stores and find the store with the same name as the
	    // one the user choose
	    for (final StorePrices store : optimizedStores) {
		if (store.getName().equalsIgnoreCase(storeName)) {
		    initForExpandableAdapter(categoryNameList,
			    shoppingItemList, store.getItems());
		    
		    storeSharpListItemAdapter = new StoreSharpListExpandableAdapter(
			    getActivity(), categoryNameList, shoppingItemList);

		    try {
			for (final ImageResource imageResource : SharpCartUtilities
				.getInstance().getStoreImages()) {
			    if (imageResource.getName().equalsIgnoreCase(
				    storeName)) {
				storeImage.setImageResource(imageResource
					.getDrawableResourceId());
			    }
			}

		    } catch (final Exception ex) {
			Log.d("ShoppingItemAdapter", ex.getLocalizedMessage());
		    }
		}
	    }
	}

	storeSharpListItems.setAdapter(storeSharpListItemAdapter);

	// Open all groups by default
	for (int i = 0; i < storeSharpListItemAdapter.getGroupCount(); i++)
	    storeSharpListItems.expandGroup(i);

	totalCost = (TextView) view.findViewById(R.id.toatlCostTextView);

	df = new DecimalFormat("#,###,##0.00");

	// initialize our auto complete search
	shoppingItemAutoCompleteSearchBar = (AutoCompleteTextView) view
		.findViewById(R.id.autoCompleteTextView);
	final AutocompleteShoppingItemAdapter mAdapter = new AutocompleteShoppingItemAdapter(
		getActivity());
	shoppingItemAutoCompleteSearchBar.setAdapter(mAdapter);

	shoppingItemAutoCompleteSearchBar
		.setOnItemClickListener(new OnItemClickListener() {
		    @Override
		    public void onItemClick(final AdapterView<?> p,
			    final View v, final int pos, final long id) {
			final ShoppingItemViewContainer holder = (ShoppingItemViewContainer) v
				.getTag();

			// Create a new shopping item object based on the item
			// clicked
			final ShoppingListItem selectedShoppingItem = new ShoppingListItem();

			selectedShoppingItem.setId(holder.itemId);
			selectedShoppingItem
				.setShopping_item_category_id(holder.itemCategoryId);
			selectedShoppingItem
				.setShopping_item_unit_id(holder.itemUnitId);
			selectedShoppingItem.setCategory(SharpCartUtilities
				.getInstance().getCategoryName(
					holder.itemCategoryId));
			selectedShoppingItem.setUnit(SharpCartUtilities
				.getInstance().getUnitName(holder.itemUnitId));
			selectedShoppingItem.setName(holder.itemName);
			selectedShoppingItem
				.setDescription(holder.itemDescription);
			selectedShoppingItem.setQuantity(1.0);
			selectedShoppingItem
				.setImage_location(holder.itemImageLocation);

			// use the DAO object to insert the new shopping item
			// object into the main sharp list table
			MainSharpListDAO.getInstance()
				.addNewItemToMainSharpList(
					getActivity().getContentResolver(),
					selectedShoppingItem);

			// update MainSharpList object
			MainSharpList.getInstance().addShoppingItemToList(
				selectedShoppingItem);
			MainSharpList.getInstance().setLastUpdated(
				new Timestamp(System.currentTimeMillis())
					.toString());

			// update in-store list
			storeSharpListItemAdapter
				.addItemToList(selectedShoppingItem);

			// clear text
			shoppingItemAutoCompleteSearchBar.setText("");

			Toast.makeText(getActivity(),
				holder.itemDescription + " Added ",
				Toast.LENGTH_SHORT).show();
		    }
		});

	// Set an onEditor action for autocomplete text in case the user clicks
	// "done"
	shoppingItemAutoCompleteSearchBar
		.setOnEditorActionListener(new OnEditorActionListener() {

		    @Override
		    public boolean onEditorAction(final TextView v,
			    final int actionId, final KeyEvent event) {

			if (EditorInfo.IME_ACTION_DONE == actionId) {
			    if (shoppingItemAutoCompleteSearchBar.getText()
				    .length() != 0) // only add an item if there
						    // is text
			    {
				// Create a new extra item shopping item object
				final ShoppingListItem selectedShoppingItem = new ShoppingListItem();

				final Random ran = new Random();
				final int x = ran.nextInt(100) + 500;

				selectedShoppingItem.setId(x);
				selectedShoppingItem
					.setShopping_item_category_id(23);
				selectedShoppingItem
					.setShopping_item_unit_id(0);
				selectedShoppingItem.setCategory("Extra");
				selectedShoppingItem.setName(WordUtils
					.capitalizeFully(shoppingItemAutoCompleteSearchBar
						.getText().toString()));
				selectedShoppingItem.setDescription(WordUtils
					.capitalizeFully(shoppingItemAutoCompleteSearchBar
						.getText().toString()));
				selectedShoppingItem.setQuantity(1.0);
				selectedShoppingItem.setPackage_quantity(1.0);
				selectedShoppingItem.setUnit("-");
				selectedShoppingItem
					.setImage_location("/images/shoppingItems/default.png");

				// use the DAO object to insert the new shopping
				// item object into the main sharp list table
				MainSharpListDAO.getInstance()
					.addNewItemToMainSharpList(
						getActivity()
							.getContentResolver(),
						selectedShoppingItem);

				// update MainSharpList object
				MainSharpList.getInstance()
					.addShoppingItemToList(
						selectedShoppingItem);

				// update in-store list
				storeSharpListItemAdapter
					.addItemToList(selectedShoppingItem);

				// inform the user
				Toast.makeText(
					getActivity(),
					WordUtils
						.capitalizeFully(shoppingItemAutoCompleteSearchBar
							.getText().toString())
						+ " Added", Toast.LENGTH_SHORT)
					.show();

				// clear text
				shoppingItemAutoCompleteSearchBar.setText("");
			    }
			}

			return false;
		    }
		});

	// Voice search action
	voiceSearchButton = (ImageView) view
		.findViewById(R.id.voiceSearchButton);
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
     * @param storeName
     *            the storeName to set
     */
    public void setStoreName(final String storeName) {
	this.storeName = storeName;
    }

    /**
     * @return the optimizedStores
     */
    public ArrayList<StorePrices> getOptimizedStores() {
	return optimizedStores;
    }

    /**
     * @param optimizedStores
     *            the optimizedStores to set
     */
    public void setOptimizedStores(final ArrayList<StorePrices> optimizedStores) {
	this.optimizedStores = optimizedStores;
    }

    public void updateTotalCost(final double itemPrice) {
	double totalCostNumber = Double.valueOf(totalCost.getText().toString());
	totalCostNumber += itemPrice;
	totalCost.setText(df.format(totalCostNumber));

	storeSharpListItemAdapter.notifyDataSetChanged();
    }

    private void initForExpandableAdapter(final List<String> categoryNameList,
	    final HashMap<String, List<ShoppingListItem>> shoppingItemList,
	    List<ShoppingListItem> shoppingItems) {
	// First we need to remove unavailable items and add extra items
	shoppingItems = removeUnavailableItemsAndAddExtraItems(shoppingItems);
	final HashSet<String> tempCategoryNameSet = new HashSet<String>();

	// now we need to convert our list into two lists: category headers and
	// shopping items
	for (final ShoppingListItem item : shoppingItems) {
	    tempCategoryNameSet.add(WordUtils.capitalizeFully(item
		    .getCategory()));
	}

	for (final String name : tempCategoryNameSet) {
	    categoryNameList.add(name);
	}

    //Sort category names
    Collections.sort(categoryNameList);
    
	// Add "In Cart" section to the end of the list
	categoryNameList.add("In Cart");

	for (final String categoryName : categoryNameList) {
	    List<ShoppingListItem> items = new ArrayList<ShoppingListItem>();

	    for (final ShoppingListItem item : shoppingItems) {
		if (item.getCategory().equalsIgnoreCase(categoryName)) {
		    items.add(item);
		}
	    }

	    shoppingItemList.put(categoryName, items);
	    items = null;
	}
    }

    private List<ShoppingListItem> removeUnavailableItemsAndAddExtraItems(
	    final List<ShoppingListItem> shoppingItems) {
	
    	// remove any item that has a price = 0
	final ListIterator<ShoppingListItem> li = shoppingItems.listIterator();
	
	while (li.hasNext()) {
	    final ShoppingListItem item = li.next();
	    
	    if (item.getPrice() == 0)
	    	li.remove();
	    else // set item price and quantity
	    {
			// items using oz
			if ((item.getUnit().equalsIgnoreCase("oz"))) 
			{
			    if ((item.getPackage_quantity() > 0)) // make sure we are not dividing by 0
			    {
					item.setQuantity(item.getQuantity()/item.getPackage_quantity()); //change the quantity from oz to amount of pacakges
					//item.setPrice(item.getPrice_per_unit()* item.getPackage_quantity()*item.getQuantity());//change price per unit from per-oz to per-package
					//item.setPackage_quantity(1.0); //should I modify the package quantity?
			    }
			}

		// items using lbs
		else if (item.getUnit().equalsIgnoreCase("lbs")) 
		{
		    if (item.getPackage_quantity() > 1) // if the items is sold in packages larger than 1 lbs
		    {
		    	//item.setPrice(item.getPrice_per_unit()*item.getQuantity());
		    } else //item is sold in packages of 1 lbs
		    {
		    	//item.setPrice(item.getPrice_per_unit()*item.getQuantity());
		    }
		} else // Everything else
			{
			    //item.setPrice((item.getPackage_quantity() * item.getPrice_per_unit()) / (item.getQuantity() / item.getPackage_quantity()));
			    item.setQuantity(item.getQuantity()/item.getPackage_quantity());
			}
	    }
	}

	// add extra items from MainSharpList
	for (final ShoppingListItem item : MainSharpList.getInstance()
		.getMainSharpList()) {
	    if (item.getShopping_item_category_id() == 23) //23 is the extra items category id number
		shoppingItems.add(item);
	}

	return shoppingItems;
    }

    private void checkVoiceRecognition() {

		// Check if voice recognition is present
		final PackageManager pm = getActivity().getPackageManager();
	
		final List<ResolveInfo> activities = pm.queryIntentActivities(
			new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
	
		if (activities.size() == 0) {
		    voiceSearchButton.setEnabled(false);
		}
    }

    private void voiceSearch() {
		final Intent intent = new Intent(
			RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
	
		// Specify the calling package to identify your application
		intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass()
			.getPackage().getName());
	
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
    public void onActivityResult(final int requestCode, final int resultCode,
	    final Intent data) {
		if (requestCode == VOICE_RECOGNITION_REQUEST_CODE) {
			
		    // If Voice recognition is successful then it returns RESULT_OK
		    if (resultCode == Activity.RESULT_OK) {
		    	
			final ArrayList<String> textMatchList = data
				.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
	
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
    
    /*
     * Update a shopping item price and quantity based on its id and add it to the cart
     */
    public void updateShoppingItemAndAddItToCart(final int shoppingItemId, final double quantity, final double price)
    {
    	storeSharpListItemAdapter.updateShoppingItemAndAddItToCart(shoppingItemId,quantity,price);
    }

    /**
     * Helper method to show the toast message
     **/

    private void showToastMessage(final String message) {
	Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
}
