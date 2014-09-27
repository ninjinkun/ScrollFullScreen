# ScrollFullScreen

![ScreenCast](https://cloud.githubusercontent.com/assets/113420/4385617/b0c7063c-43c6-11e4-9e53-0a8623d3ed93.gif)

ScrollFullScreen is scroll to full screen library like Google I/O 2014 App. It supports ListView, ScrollView, WebView.

# Usage

## 1. Implement OnFullScreenListener and instance ScrollDetector

ScrollFullScreen does **not provide full screen behevior**. You should implement your own beheivior to listener.

```java
ScrollDetector scrollDetector = new ScrollDetector(new OnFullScreenListener() {
    @Override
    public void onFullScreenStarted() {
        // Implement full screen beheivior
        getSupportActionBar().hide();
    }

    @Override
    public void onFullScreenFinished() {
        getSupportActionBar().show();
    }
});
```

## 2. Attach to view

### ListView

Instance ListViewAdapter and set OnScreenListener to ListView.

```java
ListView listView = getListView();
ListViewAdapter listViewAdapter = new ListViewAdapter.Builder(scrollDetector).build();
listView.setOnScrollListener(listViewAdapter); // ListViewAdapter implements AbsListView.OnScrollListener
```

### WebView, ScrollView

Use ScrollViewAdapter and add OnScrollChangedListener to ScrollView's ViewTreeObserver.

```java
WebView webView = (WebView)findViewById(R.id.webview);
ScrollViewAdapter scrollViewAdapter = new ScrollViewAdapter.Builder(webView, scrollDetector).build();
webView.getViewTreeObserver().addOnScrollChangedListener(scrollViewAdapter); // ScrollViewAdapter implements ViewTreeObserver.OnScrollChangedListener
```

# Requrements
- ListView
    - Android 2.3 or later
- ScrollView, WebView
    - Android 4.0 or later

# Download

We are planing to upload maven later.

# License

[Apache]: http://www.apache.org/licenses/LICENSE-2.0
[MIT]: http://www.opensource.org/licenses/mit-license.php
[GPL]: http://www.gnu.org/licenses/gpl.html
[BSD]: http://opensource.org/licenses/bsd-license.php
[MIT license][MIT].
