package com.sharpcart.android;

import com.sharpcart.android.adapter.AutocompleteShoppingItemAdapter;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

public class SearchActivity extends ListActivity {

   @Override
   public void onCreate(final Bundle savedInstanceState) { 
	      super.onCreate(savedInstanceState);
	      
	      setContentView(R.layout.searchable_shopping_items);
	      
	      final AutocompleteShoppingItemAdapter mAdapter = new AutocompleteShoppingItemAdapter(this);
	      setListAdapter(mAdapter);
	      
	      handleIntent(getIntent()); 
	   } 

	   @Override
	public void onNewIntent(final Intent intent) { 
	      setIntent(intent); 
	      handleIntent(intent); 
	   } 

	   @Override
	public void onListItemClick(final ListView l, 
	      final View v, final int position, final long id) { 
	      // call detail activity for clicked entry 
	   } 

	   private void handleIntent(final Intent intent) { 
	      if (Intent.ACTION_SEARCH.equals(intent.getAction())) { 
	         final String query = 
	               intent.getStringExtra(SearchManager.QUERY); 
	         doSearch(query); 
	      } 
	   }    

	   private void doSearch(final String queryStr) { 
	   // get a Cursor, prepare the ListAdapter
	   // and set it
	   } 
}
