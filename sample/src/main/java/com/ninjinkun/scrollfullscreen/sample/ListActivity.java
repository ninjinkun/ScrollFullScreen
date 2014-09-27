package com.ninjinkun.scrollfullscreen.sample;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import scrollfullscreen.ScrollDetector;
import scrollfullscreen.ScrollDetector.OnFullScreenListener;
import scrollfullscreen.ui.adapter.ListViewAdapter;

public class ListActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new ListFragment())
                    .commit();
        }
    }

    public static class ListFragment extends android.app.ListFragment implements OnFullScreenListener {
        private ArrayAdapter<String> adapter;
        private Animator viewTopAnimator;

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
            getListView().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    getListView().setTranslationY(getActionBarHeight());
                }
            });

            ListView listView = getListView();
            ScrollDetector detector = new ScrollDetector(this, getActionBarHeight(), 0);
            listView.setOnScrollListener(new ListViewAdapter.Builder(detector).build());
        }

        private int getActionBarHeight() {
            int actionBarHeight = 0;
            // Calculate ActionBar height
            TypedValue tv = new TypedValue();
            if (getActivity().getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
            {
                actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
            }
            return actionBarHeight;
        }

        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            super.onListItemClick(l, v, position, id);
            startActivity(new Intent(getActivity(), WebViewActivity.class));
        }

        @Override
        public void onFullScreenStarted() {
            if (viewTopAnimator == null || !viewTopAnimator.isRunning()) {
                getActivity().getActionBar().hide();

                // Adjust view's top
                int animTime = getResources().getInteger(android.R.integer.config_mediumAnimTime);
                viewTopAnimator = ObjectAnimator.ofFloat(getListView(), View.TRANSLATION_Y, 0).setDuration(animTime);
                viewTopAnimator.start();
            }
        }

        @Override
        public void onFullScreenFinished() {
            if (viewTopAnimator == null || !viewTopAnimator.isRunning()) {
                getActivity().getActionBar().show();

                // Adjust view's top
                int animTimeResourceID = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ? android.R.integer.config_shortAnimTime : android.R.integer.config_mediumAnimTime;
                int animTime = getResources().getInteger(animTimeResourceID);
                viewTopAnimator = ObjectAnimator.ofFloat(getListView(), View.TRANSLATION_Y, getActionBarHeight()).setDuration(animTime);
                viewTopAnimator.start();
            }
        }
    }
}
