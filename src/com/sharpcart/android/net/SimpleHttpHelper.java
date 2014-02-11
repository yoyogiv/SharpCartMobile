package com.sharpcart.android.net;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.impl.client.DefaultHttpClient;

public class SimpleHttpHelper {
    private static final String ACCEPT = "Accept";
    private static final String TAG = HttpHelper.class.getCanonicalName();
    private static final int CONN_TIMEOUT = 20000;
    private static final String POST_METHOD = "POST";
    private static final String GET_METHOD = "GET";
    private static final String DEFAULT_CONTENT_TYPE = "application/x-www-form-urlencoded";
    private static DefaultHttpClient mHttpClient;

    private static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";
    private static final String ENCODING_GZIP = "gzip";
    
	public SimpleHttpHelper() {
		
	}
	
    /**
     * Given a string representation of a URL, sets up a connection and gets
     * an input stream.
     */
    public static String doPost(String urlString,String requestBodyString) throws IOException {
        URL url = new URL(urlString);

        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
          urlConnection.setReadTimeout(10000 /* milliseconds */);
          urlConnection.setConnectTimeout(15000 /* milliseconds */);
          urlConnection.setDoOutput(true);
          urlConnection.setRequestMethod("POST");
          urlConnection.setChunkedStreamingMode(0);
          
          urlConnection.connect();
          
          PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
          out.print(requestBodyString);
          out.close();
     
          InputStream in = new BufferedInputStream(urlConnection.getInputStream());
          return readIt(in);
          
        } finally {
          urlConnection.disconnect();
        }
    }
    
    /** Reads an InputStream and converts it to a String.
     */
    private static String readIt(InputStream stream) throws IOException, UnsupportedEncodingException {
        java.util.Scanner s = new java.util.Scanner(stream).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
    
}
