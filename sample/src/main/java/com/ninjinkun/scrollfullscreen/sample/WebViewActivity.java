package com.ninjinkun.scrollfullscreen.sample;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import scrollfullscreen.ScrollDetector;
import scrollfullscreen.ScrollDetector.OnFullScreenListener;
import scrollfullscreen.ui.adapter.ScrollViewAdapter;

public class WebViewActivity extends Activity implements OnFullScreenListener {
    private ScrollViewAdapter scrollViewAdapter;
    private WebView webView;
    private Animator viewTopAnimator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        setupWebView();
        setupScrollFullscreen();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        webView.getViewTreeObserver().removeOnScrollChangedListener(scrollViewAdapter);
    }

    private void setupWebView() {
        webView = (WebView)findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("http://en.wikipedia.org/wiki/Android_(operating_system)");
    }

    private void setupScrollFullscreen() {
        webView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                webView.setTranslationY(getActionBarHeight());
            }
        });

        ScrollDetector detector = new ScrollDetector(this, getActionBarHeight(), 0);
        scrollViewAdapter = new ScrollViewAdapter.Builder(webView, detector).build();
        webView.getViewTreeObserver().addOnScrollChangedListener(scrollViewAdapter);
    }

    private int getActionBarHeight() {
        int actionBarHeight = 0;
        // Detect ActionBar height
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
        {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
        }
        return actionBarHeight;
    }

    @Override
    public void onFullScreenStarted() {
        if (viewTopAnimator == null || !viewTopAnimator.isRunning()) {
            getActionBar().hide();

            // Adjust view's top
            int animTime = getResources().getInteger(android.R.integer.config_mediumAnimTime);
            viewTopAnimator = ObjectAnimator.ofFloat(webView, View.TRANSLATION_Y, 0).setDuration(animTime);
            viewTopAnimator.start();
        }
    }

    @Override
    public void onFullScreenFinished() {
        if (viewTopAnimator == null || !viewTopAnimator.isRunning()) {
            getActionBar().show();

            // Adjust view's top
            int animTimeResourceID = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ? android.R.integer.config_shortAnimTime : android.R.integer.config_mediumAnimTime;
            int animTime = getResources().getInteger(animTimeResourceID);
            viewTopAnimator = ObjectAnimator.ofFloat(webView, View.TRANSLATION_Y, getActionBarHeight()).setDuration(animTime);
            viewTopAnimator.start();
        }
    }

}
