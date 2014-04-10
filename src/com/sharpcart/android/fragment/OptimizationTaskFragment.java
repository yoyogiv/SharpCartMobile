package com.sharpcart.android.fragment;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.sharpcart.android.api.SharpCartUrlFactory;
import com.sharpcart.android.model.MainSharpList;
import com.sharpcart.android.model.ShoppingListItem;
import com.sharpcart.android.model.StorePrices;
import com.sharpcart.android.net.SimpleHttpHelper;
import com.sharpcart.android.utilities.SharpCartConstants;
import com.sharpcart.android.utilities.SharpCartUtilities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

/**
 * This Fragment manages a single background task and retains itself across
 * configuration changes.
 */
public class OptimizationTaskFragment extends Fragment {
  private static final String TAG = OptimizationTaskFragment.class.getSimpleName();

  private static Type getStoreToken() {
	return new TypeToken<List<StorePrices>>() {}.getType();
  }
  
  private ArrayList<StorePrices> optimizedStores;
  
  private ProgressDialog pd;
  
  /**
   * Callback interface through which the fragment can report the task's
   * progress and results back to the Activity.
   */
 public interface TaskCallbacks {
    public void onOptimizationTaskPreExecute();
    public void onOptimizationTaskProgressUpdate(int percent);
    public void onOptimizationTaskCancelled();
    public void onOptimizationTaskPostExecute(ArrayList<StorePrices> optimizedStores);
  }

  private TaskCallbacks mCallbacks;
  private Context mContext;
  private SharpListOptimizationTask mTask;
  private boolean mRunning;

  /**
   * Android passes us a reference to the newly created Activity by calling this
   * method after each configuration change.
   */
  @Override
  public void onAttach(final Activity activity) {
    Log.d(TAG, "onAttach(Activity)");
    super.onAttach(activity);
    
    if (!(activity instanceof TaskCallbacks)) {
      throw new IllegalStateException("Activity must implement the TaskCallbacks interface.");
    }
    
    // Hold a reference to the parent Activity so we can report back the task's
    // current progress and results.
    mCallbacks = (TaskCallbacks) activity;
    
    //init progress dialog
    pd = new ProgressDialog(activity);
  }

  /**
   * This method is called only once when the Fragment is first created.
   */
  @Override
  public void onCreate(final Bundle savedInstanceState) {
    Log.d(TAG, "onCreate(Bundle)");
    super.onCreate(savedInstanceState);
    setRetainInstance(true);
    
    mContext = getActivity().getApplicationContext();
  }

  /**
   * This method is <em>not</em> called when the Fragment is being retained
   * across Activity instances.
   */
  @Override
  public void onDestroy() {
    Log.d(TAG, "onDestroy()");
    super.onDestroy();
    cancel();
  }

  /*****************************/
  /***** TASK FRAGMENT API *****/
  /*****************************/

  /**
   * Start the background task.
   */
  public void start() {
    if (!mRunning) {
      mTask = new SharpListOptimizationTask();
      mTask.execute();
      mRunning = true;
    }
  }

  /**
   * Cancel the background task.
   */
  public void cancel() {
    if (mRunning) {
      mTask.cancel(false);
      mTask = null;
      mRunning = false;
    }
  }

  /**
   * Returns the current state of the background task.
   */
  public boolean isRunning() {
    return mRunning;
  }

  /***************************/
  /***** BACKGROUND TASK *****/
  /***************************/

  private class SharpListOptimizationTask extends AsyncTask<Void, Integer, Void> {

    @Override
    protected void onPreExecute() {
      // Proxy the call to the Activity
      mCallbacks.onOptimizationTaskPreExecute();
      mRunning = true;
      
      //Show progress spinner
      pd.setMessage("Please wait...");
      pd.show();
    }

    @Override
    protected Void doInBackground(final Void... ignore) {

    	if (SharpCartUtilities.getInstance().hasActiveInternetConnection(mContext))
    	{
		   //Turn MainSharpList object into a json string
		   final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm").create();
		   
		   //before we create the json we want to change any item using oz quantity to reflect oz and not packages
		   for (final ShoppingListItem item : MainSharpList.getInstance().getMainSharpList())
		   {
			   if (item.getUnit()!=null)
				   if ((item.getUnit().equalsIgnoreCase("oz"))||(item.getUnit().equalsIgnoreCase("package")))
				   {
					   if ((item.getConversion_ratio()!=-1)&&(item.getConversion_ratio()!=0))
						   item.setQuantity(item.getQuantity()/item.getConversion_ratio());
				   }
		   }
		   
		   MainSharpList mainSharpList = MainSharpList.getInstance();
		   
		   final String json = gson.toJson(mainSharpList);
		
		   //return things to the way the were before
		   for (final ShoppingListItem item : MainSharpList.getInstance().getMainSharpList())
		   {
			   if (item.getUnit()!=null)
				   if ((item.getUnit().equalsIgnoreCase("oz"))||(item.getUnit().equalsIgnoreCase("package")))
				   {
					   if (item.getConversion_ratio()!=-1)
						   item.setQuantity(item.getQuantity()*item.getConversion_ratio());
				   }
		   }
		   
		   //Post json string to SharpCart server
		   try {
			   Log.d(TAG,"Connecting to server to optimize sharp list");
			   
			   final String url = SharpCartUrlFactory.getInstance().getOptimizeUrl();
			   
			   final String response = SimpleHttpHelper.doPost(url,"application/json",json,true);
			   
			   Log.d(TAG,"Server Response: "+response);
			   
			   if (response.length()!=0 && !response.equalsIgnoreCase(SharpCartConstants.SERVER_ERROR_CODE))
				   optimizedStores = gson.fromJson(response, getStoreToken());
		   
		   } catch (final IOException ex)
		   {
			   Log.d(TAG,ex.getMessage());
			   this.cancel(true);
		   }
    	} else
    	{
    		 this.cancel(true);
    	}
    	
      return null;
    }

    @Override
    protected void onProgressUpdate(final Integer... percent) {
      // Proxy the call to the Activity
      mCallbacks.onOptimizationTaskProgressUpdate(percent[0]);
    }

    @Override
    protected void onCancelled() {
      // Proxy the call to the Activity
      mCallbacks.onOptimizationTaskCancelled();
      mRunning = false;
      
      pd.dismiss();
    }

    @Override
    protected void onPostExecute(final Void ignore) {
    	// Proxy the call to the Activity
    	if (optimizedStores!=null)
    		mCallbacks.onOptimizationTaskPostExecute(optimizedStores);
    	mRunning = false;
    	
    	pd.dismiss();
    }
  }

  public ArrayList<StorePrices> getOptimizedStores()
  {
	return optimizedStores;  
  }
  
}