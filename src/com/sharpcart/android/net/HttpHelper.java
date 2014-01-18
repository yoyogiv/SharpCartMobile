package com.sharpcart.android.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

import android.util.Log;

import com.sharpcart.android.exception.SharpCartException;

public class HttpHelper {

    private static final String ACCEPT = "Accept";
    private static final String TAG = HttpHelper.class.getCanonicalName();
    private static final int CONN_TIMEOUT = 20000;
    private static final String POST_METHOD = "POST";
    private static final String GET_METHOD = "GET";
    private static final String DEFAULT_CONTENT_TYPE = "application/x-www-form-urlencoded";
    private static DefaultHttpClient mHttpClient;

    private static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";
    private static final String ENCODING_GZIP = "gzip";

    public static void maybeCreateHttpClient() {
		if (mHttpClient == null) {
		    mHttpClient = setupHttpClient();
		}
    }

    public static String getHttpResponseAsStringUsingPOST(String url,String requestBodyString) throws SharpCartException {
    	return getHttpResponseAsString(url, requestBodyString, true);
    }

    private static String getHttpResponseAsString(String url,String requestBodyString, boolean usePost)
	    throws SharpCartException {

		maybeCreateHttpClient();
	
		final String method = usePost ? POST_METHOD : GET_METHOD;
		
		return getHttpResponseAsString(url, method, DEFAULT_CONTENT_TYPE,requestBodyString);
    }

    public static String getHttpResponseAsString(String url,String requestbodyString) throws SharpCartException {
    	return getHttpResponseAsString(url, GET_METHOD, DEFAULT_CONTENT_TYPE,requestbodyString);
    }

    public static String getHttpResponseAsString(String url, String method,String contentType, String requestBodyString)
	    throws SharpCartException {
    	
		maybeCreateHttpClient();
	
		String responseString = null;
		try {
		    responseString = handleRequest(url, method, contentType,
			    requestBodyString, new BasicResponseHandler());
		} catch (final Exception e) {
		    handleException(e);
		}
	
		return responseString;
    }

    private static void handleException(Exception exception)
	    throws SharpCartException {
    	
		if (exception instanceof HttpResponseException) {
		    throw new SharpCartException("Response from server: "
			    + ((HttpResponseException) exception).getStatusCode() + ""
			    + exception.getMessage());
		} else {
		    throw new SharpCartException(exception.getMessage());
		}
    }

    private static String handleRequest(String url, String method,String contentType, String requestBodyString,ResponseHandler<String> responseHandler)
	    throws UnsupportedEncodingException, IOException,ClientProtocolException {
		
    	String responseString;
	
		if (POST_METHOD.equals(method)) {
		    responseString = doPost(url, contentType, requestBodyString,responseHandler);
		} else {
		    responseString = doGet(url, contentType, requestBodyString,responseHandler);
		}
	
		return responseString;
    }

    private static String doGet(String url, String contentType,String requestBodyString, ResponseHandler<String> responseHandler)
	    throws IOException, ClientProtocolException {
		
    	if (requestBodyString != null) {
		    url += "?" + requestBodyString;
		}

		Log.d(TAG, "URL: " + url);
		final HttpGet getRequest = new HttpGet(url);
		getRequest.setHeader(HTTP.CONTENT_TYPE, contentType);
		getRequest.setHeader(ACCEPT, contentType);
		return mHttpClient.execute(getRequest, responseHandler);
    }

    private static String doPost(String url, String contentType,String requestBodyString, ResponseHandler<String> responseHandler)
	    throws UnsupportedEncodingException, IOException,ClientProtocolException {
    	
		final HttpPost postRequest = new HttpPost(url);
		postRequest.setHeader(HTTP.CONTENT_TYPE, contentType);
		postRequest.setHeader(ACCEPT, contentType);
	
		if (contentType.equalsIgnoreCase("application/json"))
		{
			// Prepare JSON to send by setting the entity
			postRequest.setEntity(new StringEntity(requestBodyString, "UTF-8"));
		} else
		{
			// parse requestBodyString
			final String[] postParameters = requestBodyString.split("&");
			final List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
		
			// Iterate over all the postParametres string
			for (final String postParameter : postParameters) 
			{
			    final String[] keyValue = postParameter.split("=");
		
			    // Setup post values
			    nameValuePairs.add(new BasicNameValuePair(keyValue[0], keyValue[1]));
			}
		
			postRequest.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		}
		
		Log.d(TAG, "URL: " + url + " with params " + requestBodyString);
	
		return mHttpClient.execute(postRequest, responseHandler);
    }

    private static DefaultHttpClient setupHttpClient() {
		
    	final HttpParams httpParams = new BasicHttpParams();
		setConnectionParams(httpParams);
		final SchemeRegistry schemeRegistry = registerFactories();
		final ClientConnectionManager clientConnectionManager = new ThreadSafeClientConnManager(httpParams, schemeRegistry);
	
		final DefaultHttpClient client = new DefaultHttpClient(clientConnectionManager, httpParams);
	
		client.addRequestInterceptor(new HttpRequestInterceptor() 
		{
		    @Override
			public void process(HttpRequest request, HttpContext context) 
		    {
			
			    // Add header to accept gzip content
				if (!request.containsHeader(HEADER_ACCEPT_ENCODING)) 
					{
					    request.addHeader(HEADER_ACCEPT_ENCODING, ENCODING_GZIP);
					}
		    }
		});
	
		client.addResponseInterceptor(new HttpResponseInterceptor() 
		{
		    @Override
			public void process(HttpResponse response, HttpContext context) 
		    {
				// Inflate any responses compressed with gzip final
				final HttpEntity entity = response.getEntity();
				final Header encoding = entity.getContentEncoding();
				if (encoding != null) 
				{
				    for (final HeaderElement element : encoding.getElements()) 
				    {
						if (element.getName().equalsIgnoreCase(ENCODING_GZIP)) 
						{
						    response.setEntity(new InflatingEntity(response.getEntity()));
						    break;
						}
				    }
				}
		    }
		});
		
		client.setRedirectHandler(new FollowPostRedirectHandler());
	
		return client;
    }

    private static SchemeRegistry registerFactories() {
    	
		final SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		schemeRegistry.register(new Scheme("https",new SimpleSSLSocketFactory(), 443));
		
		return schemeRegistry;
    }

    private static void setConnectionParams(HttpParams httpParams) {
		HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(httpParams, HTTP.UTF_8);
		HttpConnectionParams.setConnectionTimeout(httpParams, CONN_TIMEOUT);
		HttpConnectionParams.setSoTimeout(httpParams, CONN_TIMEOUT);
    }

    private static class InflatingEntity extends HttpEntityWrapper {
		public InflatingEntity(HttpEntity wrapped) 
		{
		    super(wrapped);
		}

		@Override
		public InputStream getContent() throws IOException {
		    return new GZIPInputStream(wrappedEntity.getContent());
		}

		@Override
		public long getContentLength() {
		    return -1;
		}
    }
}
