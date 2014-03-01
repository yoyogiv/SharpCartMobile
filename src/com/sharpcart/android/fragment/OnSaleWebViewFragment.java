package com.sharpcart.android.fragment;

import com.sharpcart.android.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

public class OnSaleWebViewFragment extends Fragment{
	
	private WebView mWebView;
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		final View view = inflater.inflate(R.layout.on_sale_web_view, container, false);
		
		mWebView = (WebView) view.findViewById(R.id.onSaleWebView);
		mWebView.loadUrl("http://heb.inserts2online.com/customer_Frame.jsp?drpStoreID=373");	
		
		return view;
	}
	
	

}
