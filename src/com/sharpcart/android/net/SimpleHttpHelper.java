package com.sharpcart.android.net;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import com.sharpcart.android.model.UserProfile;

import android.util.Base64;

public class SimpleHttpHelper {
    private static final String TAG = HttpHelper.class.getCanonicalName();
    public SimpleHttpHelper() {
		
	}
	
    /**
     * Given a string representation of a URL, sets up a connection and gets
     * an input stream.
     */
    public static String doPost(final String urlString,final String requestBodyString) throws IOException {
        final URL url = new URL(urlString);

        final HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
          urlConnection.setReadTimeout(10000 /* milliseconds */);
          urlConnection.setConnectTimeout(15000 /* milliseconds */);
          urlConnection.setRequestProperty("Content-Type", "application/json"); //this is required in order for Spring Jackson to work
          urlConnection.setRequestProperty("Accept", "application/json"); //this is required in order for Spring Jackson to work
          urlConnection.setDoOutput(true);
          urlConnection.setRequestMethod("POST");
          urlConnection.setChunkedStreamingMode(0);
          
          //add authorization header
          String auth = UserProfile.getInstance().getUserName()+":"+UserProfile.getInstance().getPassword();
          byte[] encodedAuthorisation = Base64.encode(auth.getBytes(), 0);
          urlConnection.setRequestProperty("Authorization", "Basic " + new String(encodedAuthorisation));
          
          urlConnection.connect();
          
          final PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
          out.print(requestBodyString);
          out.close();
     
          final InputStream in = new BufferedInputStream(urlConnection.getInputStream());
          final String response =  readIt(in);
          
          in.close(); //important to close the stream
          
          return response;
          
        } finally {
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
