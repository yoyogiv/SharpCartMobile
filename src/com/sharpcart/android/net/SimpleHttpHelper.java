package com.sharpcart.android.net;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpStatus;

import com.sharpcart.android.model.UserProfile;
import com.sharpcart.android.utilities.SharpCartConstants;

import android.util.Base64;
import android.util.Log;

public class SimpleHttpHelper {
    private static final String TAG = SimpleHttpHelper.class.getCanonicalName();
    public SimpleHttpHelper() {
		
	}
	
    /**
     * Given a string representation of a URL, sets up a connection and gets
     * an input stream.
     */
    public static String doPost(final String urlString,final String contentType, final String requestBodyString,final boolean secure) throws IOException {
        final URL url = new URL(urlString);

        final HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
          urlConnection.setReadTimeout(10000 /* milliseconds */);
          urlConnection.setConnectTimeout(15000 /* milliseconds */);
          urlConnection.setRequestProperty("Content-Type", contentType); //this is required in order for Spring Jackson to work
          urlConnection.setRequestProperty("Accept", "application/json"); //this is required in order for Spring Jackson to work
          urlConnection.setDoOutput(true);
          urlConnection.setRequestMethod("POST");
          urlConnection.setChunkedStreamingMode(0);
          
          //add authorization header
          if (secure)
          {
	          final String auth = UserProfile.getInstance().getUserName()+":"+UserProfile.getInstance().getPassword();
	          final byte[] encodedAuthorisation = Base64.encode(auth.getBytes(), Base64.NO_WRAP);
	          urlConnection.setRequestProperty("Authorization", "Basic " + new String(encodedAuthorisation));
          }
          
          urlConnection.connect();
          
          final PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
          out.print(requestBodyString);
          out.close();
     
          int status = urlConnection.getResponseCode();
          
          final InputStream in;
          
          if(status >= HttpStatus.SC_BAD_REQUEST)
        	  in = new BufferedInputStream(urlConnection.getErrorStream());
          else
        	  in = new BufferedInputStream(urlConnection.getInputStream());
          final String response =  readIt(in);
          
          in.close(); //important to close the stream
          
          return response;
        		  
        }
         catch (final Exception ex)
         {
        	 return SharpCartConstants.SERVER_ERROR_CODE;
         }
         finally {
          urlConnection.disconnect();
        }
    }
    
    /**
     * Given a string representation of a URL, sets up a connection and gets
     * an input stream.
     */
    public static String doGet(final String urlString, final String parameters,final boolean secure) throws IOException {
        final URL url = new URL(urlString+"?"+parameters);

        final HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
          urlConnection.setReadTimeout(10000 /* milliseconds */);
          urlConnection.setConnectTimeout(15000 /* milliseconds */);
          //urlConnection.setRequestProperty("Content-Type", contentType); //this is required in order for Spring Jackson to work
          urlConnection.setRequestProperty("Accept", "application/json"); //this is required in order for Spring Jackson to work
          urlConnection.setDoOutput(false);
          urlConnection.setRequestMethod("GET");
          urlConnection.setChunkedStreamingMode(0);
          
          //add authorization header
          if(secure)
          {
	          final String auth = UserProfile.getInstance().getUserName()+":"+UserProfile.getInstance().getPassword();
	          final byte[] encodedAuthorisation = Base64.encode(auth.getBytes(), Base64.NO_WRAP);
	          urlConnection.setRequestProperty("Authorization", "Basic " + new String(encodedAuthorisation));
          }
          
          urlConnection.connect();
          
          Log.d(TAG, "Fetching url using GET: "+url);
  
          int status = urlConnection.getResponseCode();
          
          final InputStream in;
          
          if(status >= HttpStatus.SC_BAD_REQUEST)
        	  in = new BufferedInputStream(urlConnection.getErrorStream());
          else
        	  in = new BufferedInputStream(urlConnection.getInputStream());
          
          final String response =  readIt(in);
          
          in.close(); //important to close the stream
          
          return response;
        		  
        }
         catch (final Exception ex)
         {
        	 ex.printStackTrace();
        	 return SharpCartConstants.SERVER_ERROR_CODE;
         }
         finally {
          urlConnection.disconnect();
        }
    }
    
    /** Reads an InputStream and converts it to a String.
     */
    private static String readIt(final InputStream stream) throws IOException, UnsupportedEncodingException {
        final java.util.Scanner s = new java.util.Scanner(stream).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
    
}
