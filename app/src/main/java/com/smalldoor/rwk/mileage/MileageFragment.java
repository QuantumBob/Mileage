package com.smalldoor.rwk.mileage;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewFragment;

import com.smalldoor.rwk.mileage.R;
import com.smalldoor.rwk.mileage.WebAppInterface;

/**
 * The WebView fragment for the Mileage html page
 */
public class MileageFragment extends WebViewFragment {

    WebView mWebView;

    public MileageFragment() {
        // Required empty public constructor
    }

    @SuppressLint("SetJavaScriptEnabled, addJavascriptInterface")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);
        /** Inflate the layout for this fragment */
//        View view = inflater.inflate(R.layout.fragment_mileage, container, false);
//        mWebView = (WebView) view.findViewById(R.id.recycler_view);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        mWebView = (WebView)inflater.inflate(R.layout.fragment_mileage, container, false);

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(new WebAppInterface(mWebView.getContext()), "Android");
        mWebView.loadUrl("file:///android_asset/www/_index.html");

        return mWebView;
    }


}
