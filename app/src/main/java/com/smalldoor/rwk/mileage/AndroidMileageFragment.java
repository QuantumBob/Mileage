package com.smalldoor.rwk.mileage;


import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.TextView;

/**
 * Adroid version of the JavaScript Mileage screen
 */
public class AndroidMileageFragment extends Fragment {

    public AndroidMileageFragment() {
        // Required empty public constructor
    }

    @SuppressLint("SetJavaScriptEnabled, addJavascriptInterface")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_android_mileage, container, false);
        TextView dateView = (TextView) view.findViewById(R.id.editMileageDate);
        dateView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event){
                if(MotionEvent.ACTION_UP == event.getAction()){
                    DialogFragment newFragment = new DatePickerMileageFragment();
                    newFragment.show(getFragmentManager(), "datePicker");
                }
                return false;
            }
        });
        WebView mWebView = (WebView) view.findViewById(R.id.chart_web_view);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(new WebAppInterface(mWebView.getContext()), "Android");
        mWebView.loadUrl("file:///android_asset/www/index.html");

        view.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent){
                InputMethodManager inputMethodManager = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                return false;
            }
        });

        view.requestFocus();
        return view;
    }
}
