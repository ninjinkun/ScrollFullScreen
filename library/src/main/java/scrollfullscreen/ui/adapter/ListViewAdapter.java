package scrollfullscreen.ui.adapter;

import android.view.View;
import android.widget.AbsListView;

import scrollfullscreen.ScrollDetector;

/**
 * Adapter for ListView, GridView
 * <p>
 * Dispatch AbsListView.OnScrollListener to ScrollDetector.onScrollChanged.
 * Because ListView's OnScrollChangedListener don't respond true height.
 * </p>
 */

public class ListViewAdapter implements AbsListView.OnScrollListener {
    private ScrollDetector scrollDetector;
    private AbsListView.OnScrollListener proxyOnScrollListener;
    private int previousFirstVisibleItem;
    private static final int UNDEFINED_ROW_HEIGHT = Integer.MIN_VALUE;
    private int rowHeight = UNDEFINED_ROW_HEIGHT;

    private ListViewAdapter(ScrollDetector scrollDetector) {
        this.scrollDetector = scrollDetector;
    }

    private void setRowHeight(int rowHeight) {
        this.rowHeight = rowHeight;
    }

    private void setProxyOnScrollListener(AbsListView.OnScrollListener proxyOnScrollListener) {
        this.proxyOnScrollListener = proxyOnScrollListener;
    }

    /**
     * Dummy for proxy
     *
     * @param view
     * @param scrollState
     */
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (proxyOnScrollListener != null) {
            proxyOnScrollListener.onScrollStateChanged(view, scrollState);
        }
    }

    /**
     * Dispatch AbsListView.OnScrollListener to ScrollDetector.onScrollChanged
     *
     * @param view
     * @param firstVisibleItem
     * @param visibleItemCount
     * @param totalItemCount
     */
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (proxyOnScrollListener != null) {
            proxyOnScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }
        // Calculate row height
        // NOTE: this method optimized for that all rows are same height
        if (rowHeight == UNDEFINED_ROW_HEIGHT && view.getAdapter().getCount() > 0) {
            int viewCount = view.getAdapter().getCount();
            // ListView includes headers and footers in view count
            // Choose center of views for pick up row view
            int viewCountCenter = viewCount / 2;
            View rowView = view.getAdapter().getView(viewCountCenter, null, null);
            rowView.measure(0, 0);
            int measuredRowHeight = rowView.getMeasuredHeight();
            if (measuredRowHeight > 0) {
                rowHeight = measuredRowHeight;
            }
        }
        scrollDetector.onScrollChanged(0, firstVisibleItem * rowHeight, 0, previousFirstVisibleItem * rowHeight);
        previousFirstVisibleItem = firstVisibleItem;
    }

    /**
     *  Provide ListViewAdapter instance
     */

    public static class Builder {
        ListViewAdapter listViewAdapter;

        /**
         * Constructor
         * @param scrollDetector
         */
        public Builder(ScrollDetector scrollDetector) {
            listViewAdapter = new ListViewAdapter(scrollDetector);
        }

        /**
         * Specify listView row height
         * <p>It's optional. If this parameter is not specified, this module will measure row height automatically.</p>
         *
         * @param rowHeight
         * @return
         */
        public Builder rowHeight(int rowHeight) {
            listViewAdapter.setRowHeight(rowHeight);
            return this;
        }

        /**
         * Proxy scroll method to your OnScrollListener
         * <p>If you always used your AbsListView.OnScrollListener (e.g. for paging),
         * you would use this method for proxy scroll event</p>
         * @param onScrollListener
         * @return
         */
        public Builder proxyTo(AbsListView.OnScrollListener onScrollListener) {
            listViewAdapter.setProxyOnScrollListener(onScrollListener);
            return this;
        }

        /**
         * Creates a ListViewAdapter with the arguments supplied to this builder.
         * @return ListViewAdapter instance
         */
        public ListViewAdapter build() {
            return listViewAdapter;
        }
    }
}
