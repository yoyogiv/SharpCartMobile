package com.sharpcart.android.fragment;

import com.sharpcart.android.R;

import android.app.ProgressDialog;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class OnSaleWebViewFragment extends Fragment{
	
	private WebView mWebView;
	private String storeSalesUrl;
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {
		final ProgressDialog pd = new ProgressDialog(getActivity(),ProgressDialog.STYLE_SPINNER);
		pd.setMessage("Loading store sale flyer...");
		pd.setTitle("Loading...");
		pd.show();
		
		final View view = inflater.inflate(R.layout.on_sale_web_view, container, false);
		
		mWebView = (WebView) view.findViewById(R.id.onSaleWebView);
		final WebSettings webSettings = mWebView.getSettings();
		mWebView.setInitialScale(1); //set zoom scale
		mWebView.setWebViewClient(new WebViewClient(){
	           @Override
	            public void onPageFinished(WebView view, String url) {
	                if(pd.isShowing()&&pd!=null)
	                {
	                    pd.dismiss();
	                }
	            }

			/* (non-Javadoc)
			 * @see android.webkit.WebViewClient#onReceivedError(android.webkit.WebView, int, java.lang.String, java.lang.String)
			 */
			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				Toast.makeText(view.getContext(), "Your Internet Connection May not be active Or " + description , Toast.LENGTH_LONG).show();
			}

			/* (non-Javadoc)
			 * @see android.webkit.WebViewClient#onReceivedSslError(android.webkit.WebView, android.webkit.SslErrorHandler, android.net.http.SslError)
			 */
			@Override
			public void onReceivedSslError(WebView view,
					SslErrorHandler handler, SslError error) {
				handler.proceed();
			}
			
	             
		}); //set that all links open in the webview and not outside of the app
		
		webSettings.setJavaScriptEnabled(true); //enabled javascript
		webSettings.setBuiltInZoomControls(true); //allow zooming with gestures
		webSettings.setDisplayZoomControls(true); //display zooming controls
		webSettings.setLoadWithOverviewMode(false); 
		webSettings.setUseWideViewPort(true);
		
		final Bundle bundle = getArguments();
		storeSalesUrl = bundle.getString("storeOnSaleUrl");
		
		if (storeSalesUrl!="")
			mWebView.loadUrl(storeSalesUrl);
		
		return view;
	}

	/**
	 * @return the storeSalesUrl
	 */
	public String getStoreSalesUrl() {
		return storeSalesUrl;
	}

	/**
	 * @param storeSalesUrl the storeSalesUrl to set
	 */
	public void setStoreSalesUrl(final String storeSalesUrl) {
		this.storeSalesUrl = storeSalesUrl;
	}
	
	/*
	 * Load a url into the webview
	 */
	public void loadUrl(final String storeSalesUrl)
	{
		if (storeSalesUrl!="")
			mWebView.loadUrl(storeSalesUrl);
	}
}
