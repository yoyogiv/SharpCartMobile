package com.sharpcart.android.fragment;

import com.sharpcart.android.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class OnSaleWebViewFragment extends Fragment{
	
	private WebView mWebView;
	private String storeSalesUrl;
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {
		
		final View view = inflater.inflate(R.layout.on_sale_web_view, container, false);
		
		mWebView = (WebView) view.findViewById(R.id.onSaleWebView);
		final WebSettings webSettings = mWebView.getSettings();
		mWebView.setInitialScale(1); //set zoom scale
		mWebView.setWebViewClient(new WebViewClient()); //set that all links open in the webview and not outside of th app
		
		webSettings.setJavaScriptEnabled(true); //enabled javascript
		webSettings.setBuiltInZoomControls(true); //allow zooming with gestures
		webSettings.setDisplayZoomControls(true); //display zooming controls
		webSettings.setLoadWithOverviewMode(false); 
		webSettings.setUseWideViewPort(true);
		
		//mWebView.loadUrl("http://heb.inserts2online.com/customer_Frame.jsp?drpStoreID=373"); //HEB
		//mWebView.loadUrl("http://www.sprouts.com/specials/-/flyer/36348/store/110"); //Sprouts
		//mWebView.loadUrl("http://www.costco.com/warehouse-coupon-offers.html"); //Costco
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
	

}
