package com.sharpcart.android.fragment;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sharpcart.android.api.SharpCartUrlFactory;
import com.sharpcart.android.exception.SharpCartException;
import com.sharpcart.android.model.MainSharpList;
import com.sharpcart.android.model.Store;
import com.sharpcart.android.net.HttpHelper;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

/**
 * This Fragment manages a single background task and retains itself across
 * configuration changes.
 */
public class TaskFragment extends Fragment {
  private static final String TAG = TaskFragment.class.getSimpleName();

  private static Type getStoreToken() {
	return new TypeToken<List<Store>>() {}.getType();
  }
  
  /**
   * Callback interface through which the fragment can report the task's
   * progress and results back to the Activity.
   */
 public interface TaskCallbacks {
    public void onPreExecute();
    public void onProgressUpdate(int percent);
    public void onCancelled();
    public void onPostExecute();
  }

  private TaskCallbacks mCallbacks;
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
  }

  /**
   * This method is called only once when the Fragment is first created.
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    Log.i(TAG, "onCreate(Bundle)");
    super.onCreate(savedInstanceState);
    setRetainInstance(true);
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
    }

    @Override
    protected Void doInBackground(Void... ignore) {
    	/*
      for (int i = 0; !isCancelled() && i < 100; i++) {
        Log.i(TAG, "publishProgress(" + i + "%)");
        SystemClock.sleep(100);
        publishProgress(i);
      }
      */

	   //Turn MainSharpList object into a json string
	   Gson gson = new Gson();
	   String json = gson.toJson(MainSharpList.getInstance());
		   
	   //Post json string to SharpCart server
	   try {
		   String url = SharpCartUrlFactory.getInstance().getOptimizeUrl();
	  
		   String response = HttpHelper.getHttpResponseAsString(url, "POST","application/json", json);
		   
		   ArrayList<Store> optimizedStore = gson.fromJson(response, getStoreToken());
	   
	   } catch (SharpCartException ex)
	   {
		   Log.d(TAG,ex.getMessage());
	   }
		   
      return null;
    }

    @Override
    protected void onProgressUpdate(Integer... percent) {
      // Proxy the call to the Activity
      mCallbacks.onProgressUpdate(percent[0]);
    }

    @Override
    protected void onCancelled() {
      // Proxy the call to the Activity
      mCallbacks.onCancelled();
      mRunning = false;
    }

    @Override
    protected void onPostExecute(Void ignore) {
      // Proxy the call to the Activity
      mCallbacks.onPostExecute();
      mRunning = false;
    }
  }

  /************************/
  /***** LOGS & STUFF *****/
  /************************/

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    Log.i(TAG, "onActivityCreated(Bundle)");
    super.onActivityCreated(savedInstanceState);
  }

  @Override
  public void onStart() {
    Log.i(TAG, "onStart()");
    super.onStart();
  }

  @Override
  public void onResume() {
    Log.i(TAG, "onResume()");
    super.onResume();
  }

  @Override
  public void onPause() {
    Log.i(TAG, "onPause()");
    super.onPause();
  }

  @Override
  public void onStop() {
    Log.i(TAG, "onStop()");
    super.onStop();
  }
 
}