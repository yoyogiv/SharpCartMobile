package com.sharpcart.android.fragment;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sharpcart.android.api.SharpCartUrlFactory;
import com.sharpcart.android.model.MainSharpList;
import com.sharpcart.android.model.ShoppingItem;
import com.sharpcart.android.model.Store;
import com.sharpcart.android.net.SimpleHttpHelper;
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
	return new TypeToken<List<Store>>() {}.getType();
  }
  
  private ArrayList<Store> optimizedStores;
  
  private ProgressDialog pd;
  
  /**
   * Callback interface through which the fragment can report the task's
   * progress and results back to the Activity.
   */
 public interface TaskCallbacks {
    public void onPreExecute();
    public void onProgressUpdate(int percent);
    public void onCancelled();
    public void onPostExecute(ArrayList<Store> optimizedStores);
  }

  private TaskCallbacks mCallbacks;
  private Context mContext;
  private DummyTask mTask;
  private boolean mRunning;

  /**
   * Android passes us a reference to the newly created Activity by calling this
   * method after each configuration change.
   */
  @Override
  public void onAttach(final Activity activity) {
    Log.i(TAG, "onAttach(Activity)");
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
    Log.i(TAG, "onCreate(Bundle)");
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
    Log.i(TAG, "onDestroy()");
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
      mTask = new DummyTask();
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

  /**
   * A dummy task that performs some (dumb) background work and proxies progress
   * updates and results back to the Activity.
   */
  private class DummyTask extends AsyncTask<Void, Integer, Void> {

    @Override
    protected void onPreExecute() {
      // Proxy the call to the Activity
      mCallbacks.onPreExecute();
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
		   final Gson gson = new Gson();
		   
		   //before we create the json we want to change any item using oz quantity to reflect oz and not packages
		   for (final ShoppingItem item : MainSharpList.getInstance().getMainSharpList())
		   {
			   if (item.getUnit()!=null)
				   if ((item.getUnit().equalsIgnoreCase("oz")))
				   {
					   if (item.getConversion_ratio()!=-1)
						   item.setQuantity(item.getQuantity()/item.getConversion_ratio());
				   }
		   }
		   
		   final String json = gson.toJson(MainSharpList.getInstance());
			   
		   //Post json string to SharpCart server
		   try {
			   final String url = SharpCartUrlFactory.getInstance().getOptimizeUrl();
		  
			   //final String response = HttpHelper.getHttpResponseAsString(url, "POST","application/json", json);
			   
			   final String response = SimpleHttpHelper.doPost(url,json);

			   optimizedStores = gson.fromJson(response, getStoreToken());
		   
		   } catch (final IOException ex)
		   {
			   Log.d(TAG,ex.getMessage());
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
      mCallbacks.onProgressUpdate(percent[0]);
    }

    @Override
    protected void onCancelled() {
      // Proxy the call to the Activity
      mCallbacks.onCancelled();
      mRunning = false;
      
      pd.dismiss();
    }

    @Override
    protected void onPostExecute(final Void ignore) {
    	// Proxy the call to the Activity
    	if (optimizedStores!=null)
    		mCallbacks.onPostExecute(optimizedStores);
    	mRunning = false;
    	
    	pd.dismiss();
    }
  }

  public ArrayList<Store> getOptimizedStores()
  {
	return optimizedStores;  
  }
  
}