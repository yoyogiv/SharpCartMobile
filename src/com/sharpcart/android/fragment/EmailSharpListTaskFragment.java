package com.sharpcart.android.fragment;

import java.util.ArrayList;

import com.google.gson.Gson;
import com.sharpcart.android.api.SharpCartUrlFactory;
import com.sharpcart.android.exception.SharpCartException;
import com.sharpcart.android.model.MainSharpList;
import com.sharpcart.android.model.Store;
import com.sharpcart.android.net.HttpHelper;
import com.sharpcart.android.utilities.SharpCartUtilities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

public class EmailSharpListTaskFragment extends Fragment {

	private static final String TAG = EmailSharpListTaskFragment.class.getSimpleName();
	private ProgressDialog pd;
	private String response;
	
	public EmailSharpListTaskFragment() {
		
	}
	
	  /**
	   * Callback interface through which the fragment can report the task's
	   * progress and results back to the Activity.
	   */
	 public interface TaskCallbacks {
	    public void onPreSharpListEmailSent();
	    public void onPostSharpListEmailSent(String response);
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
	  public void onAttach(Activity activity) {
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
	  public void onCreate(Bundle savedInstanceState) {
	    Log.i(TAG, "onCreate(Bundle)");
	    super.onCreate(savedInstanceState);
	    setRetainInstance(true);
	    
	    mContext = this.getActivity().getApplicationContext();
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
	      mCallbacks.onPreSharpListEmailSent();
	      mRunning = true;
	      
	      //Show progress spinner
	      pd.setMessage("Please wait...");
	      pd.show();
	    }

	    @Override
	    protected Void doInBackground(Void... ignore) {

	    	if (SharpCartUtilities.getInstance().hasActiveInternetConnection(mContext))
	    	{
			   //Turn MainSharpList object into a json string
			   Gson gson = new Gson();
			   String json = gson.toJson(MainSharpList.getInstance());
				   
			   //Post json string to SharpCart server
			   try {
				   String url = SharpCartUrlFactory.getInstance().getEmailSharpListUrl();
			  
				   response = HttpHelper.getHttpResponseAsString(url, "POST","application/json", json);
			   
			   } catch (SharpCartException ex)
			   {
				   Log.d(TAG,ex.getMessage());
				   response = ex.getMessage();
			   }
	    	} else
	    	{
	    		 this.cancel(true);
	    		 response = "no internet";
	    	}
	    	
	      return null;
	    }

	    @Override
	    protected void onCancelled() {
	      mRunning = false;
	      
	      pd.dismiss();
	    }

	    @Override
	    protected void onPostExecute(Void ignore) {
	    	// Proxy the call to the Activity
	    	if (response!=null)
	    		mCallbacks.onPostSharpListEmailSent(response);
	    	mRunning = false;
	  	
	    	pd.dismiss();
	    }
	  }
  
}
