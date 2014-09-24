package com.ninjinkun.scrollfullscreen.sample;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import scrollfullscreen.ScrollDetector;
import scrollfullscreen.ScrollDetector.OnFullScreenListener;
import scrollfullscreen.ui.adapter.ListViewAdapter;
import scrollfullscreen.ui.helper.ViewTopHelper;

public class ListActivity extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new ListFragment())
                    .commit();
        }
    }

    public static class ListFragment extends android.support.v4.app.ListFragment implements OnFullScreenListener {
        private ViewTopHelper viewTopHelper;
        private ArrayAdapter<String> adapter;
        private boolean isActionBarAnimating;

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

        public ListFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            setupListView();
            setupFullScreen();
        }

        private void setupListView() {
            List<String> rows = new ArrayList<String>() {{
                for (int i = 0; i < 50; i++) {
                    add(String.valueOf(i));
                }
            }};
            adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, rows);
            getListView().setAdapter(adapter);
        }

        private void setupFullScreen() {
            int actionBarHeight = 0;
            // Calculate ActionBar height
            TypedValue tv = new TypedValue();
            if (getActivity().getTheme().resolveAttribute(R.attr.actionBarSize, tv, true))
            {
                actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
            }
            getListView().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    viewTopHelper.setOffsetToActionBarShownPostision();
                }
            });

            viewTopHelper = new ViewTopHelper(getListView(), actionBarHeight, 0);

            ListView listView = getListView();
            ScrollDetector detector = new ScrollDetector(this, actionBarHeight, 0);
            listView.setOnScrollListener(new ListViewAdapter.Builder(detector).build());
        }

        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            super.onListItemClick(l, v, position, id);
            startActivity(new Intent(getActivity(), WebViewActivity.class));
        }

        @Override
        public void onFullScreenFinished() {
            if (!isActionBarAnimating) {
                ((ActionBarActivity) getActivity()).getSupportActionBar().show();

                int animTimeResourceID = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ? android.R.integer.config_shortAnimTime : android.R.integer.config_mediumAnimTime;
                int animTime = getResources().getInteger(animTimeResourceID);
                viewTopHelper.animateOffsetToActionBarShownPosition(actionBarAnimationListener, animTime);
            }
        }

        @Override
        public void onFullScreenStarted() {
            if (!isActionBarAnimating) {
                ((ActionBarActivity) getActivity()).getSupportActionBar().hide();

                int animTime = getResources().getInteger(android.R.integer.config_mediumAnimTime);
                viewTopHelper.animateOffsetToActionBarHiddenPosition(actionBarAnimationListener, animTime);
            }
        }
    }
}
