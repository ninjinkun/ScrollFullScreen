package com.ninjinkun.scrollfullscreen.sample;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.TypedValue;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import scrollfullscreen.ScrollDetector.OnFullScreenListener;
import scrollfullscreen.ScrollDetector;
import scrollfullscreen.ui.adapter.ScrollViewAdapter;
import scrollfullscreen.ui.helper.ViewTopHelper;

public class WebViewActivity extends ActionBarActivity implements OnFullScreenListener {
    private ViewTopHelper viewTopHelper;
    private ScrollViewAdapter scrollViewAdapter;
    private boolean isActionBarAnimating; // Lock flag for suppress animation twice
    private WebView webView;

    private Animation.AnimationListener actionBarAnimationListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
            isActionBarAnimating = true;
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            isActionBarAnimating = false;
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };

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
//        webView.getViewTreeObserver().removeOnScrollChangedListener(scrollViewAdapter);
    }

    private void setupWebView() {
        webView = (WebView)findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("http://en.wikipedia.org/wiki/Android_(operating_system)");
    }

    private void setupScrollFullscreen() {
        int actionBarHeight = 0;
        // Detect ActionBar height
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(R.attr.actionBarSize, tv, true))
        {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
        }

        viewTopHelper = new ViewTopHelper(webView, actionBarHeight, 0);
        webView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                viewTopHelper.setOffsetToActionBarShownPostision();
            }
        });

        ScrollDetector detector = new ScrollDetector(this, actionBarHeight, 0);
        scrollViewAdapter = new ScrollViewAdapter.Builder(webView, detector).build();
        webView.getViewTreeObserver().addOnScrollChangedListener(scrollViewAdapter);
    }

    @Override
    public void onFullScreenStarted() {
        if (!isActionBarAnimating) {
            getSupportActionBar().hide();
            int animTime = getResources().getInteger(android.R.integer.config_mediumAnimTime);
            viewTopHelper.animateOffsetToActionBarHiddenPosition(actionBarAnimationListener, animTime);
        }
    }

    @Override
    public void onFullScreenFinished() {
        if (!isActionBarAnimating) {
            getSupportActionBar().show();
            int animTimeResourceID = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ? android.R.integer.config_shortAnimTime : android.R.integer.config_mediumAnimTime;
            int animTime = getResources().getInteger(animTimeResourceID);
            viewTopHelper.animateOffsetToActionBarShownPosition(actionBarAnimationListener, animTime);
        }
    }

}
