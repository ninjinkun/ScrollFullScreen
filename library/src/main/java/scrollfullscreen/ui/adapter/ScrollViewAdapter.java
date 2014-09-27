package scrollfullscreen.ui.adapter;

import android.view.View;
import android.view.ViewTreeObserver;
import android.webkit.WebView;
import android.widget.ScrollView;

import java.lang.ref.WeakReference;

import scrollfullscreen.ScrollDetector;

/**
 * Adapter for ScrollView and WebView
 * <p>
 * Dispatch view's ViewObserver.OnScrollChangedListener to ScrollDetector
 * </p>
 */

public class ScrollViewAdapter implements ViewTreeObserver.OnScrollChangedListener {
    private ScrollDetector scrollDetector;
    private WeakReference<View> scrollView;
    private static final int UNDEFINED_PREVIOUS_Y_VALUE = Integer.MIN_VALUE;
    private int previousY = UNDEFINED_PREVIOUS_Y_VALUE;

    private ScrollViewAdapter(View scrollView, ScrollDetector scrollDetector) {
        if (!(scrollView instanceof ScrollView || scrollView instanceof WebView)) {
            throw new IllegalStateException("scrollView must be ScrollView or WebView");
        }

        this.scrollDetector = scrollDetector;
        this.scrollView = new WeakReference<View>(scrollView);
    }

    /**
     * Dispatch ViewTreeObserver.OnScrollChangedListener to ScrollDetector
     */
    @Override
    public void onScrollChanged() {
        View view = scrollView.get();
        if (view != null) {
            int y = view.getScrollY();
            if (previousY != UNDEFINED_PREVIOUS_Y_VALUE) {
                scrollDetector.onScrollChanged(0, y, 0, previousY);
            }
            previousY = y;
        }
    }

    /**
     * Reset current states
     */
    public void reset() {
        previousY = UNDEFINED_PREVIOUS_Y_VALUE;
        scrollDetector.reset();
    }

    /**
     * Provide ScrollViewAdapter instance
     * <p>NOTE: This class is currently for only build ScrollViewAdapter.
     * It may take some options in the future.</p>
     */
    public static class Builder {
        private ScrollViewAdapter scrollViewAdapter;

        /**
         * Constructor
         * @param scrollView
         * @param scrollDetector
         */
        public Builder(View scrollView, ScrollDetector scrollDetector) {
            scrollViewAdapter = new ScrollViewAdapter(scrollView, scrollDetector);
        }

        /**
         * Creates a ScrollViewAdapter with the arguments supplied to this builder.
         * @return
         */
        public ScrollViewAdapter build() {
            return scrollViewAdapter;
        }
    }
}
