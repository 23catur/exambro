package id.sch.smkn2cikbar.exambrowser;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import static id.sch.smkn2cikbar.exambrowser.DetectConnection.isNetworkStatusAvialable;

public class MainActivity extends AppCompatActivity {
//    private static final int SPLASH_TIME_OUT = 3000;
    private SwipeRefreshLayout swipeRefreshLayout;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar(findViewById(R.id.toolbar));

        final WebView webView = findViewById(R.id.activity_main_webview);
        setupWebView(webView);
        setupSwipeRefreshLayout(webView);

        // Menutup aplikasi setelah waktu yang ditentukan
//        new Handler().postDelayed(() -> finish(), SPLASH_TIME_OUT);
    }

    private void setupWebView(WebView webView) {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.setWebViewClient(new ExamWebView());
        webView.loadUrl("http://192.168.2.20");  // Menggunakan URL yang diberikan
    }

    private void setupSwipeRefreshLayout(final WebView webView) {
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE, Color.CYAN);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (isNetworkStatusAvialable(MainActivity.this)) {
                webView.reload();
                webView.getSettings().setDomStorageEnabled(true);
            } else {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private class ExamWebView extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (isNetworkStatusAvialable(MainActivity.this)) {
                view.loadUrl(url);
            }
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            swipeRefreshLayout.setRefreshing(false);
            super.onPageFinished(view, url);
        }

//        @Override
//        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
//            swipeRefreshLayout.setRefreshing(false);
//            Intent intent = new Intent(MainActivity.this, InputAddress.class);
//            intent.putExtra("valid", "offline");
//            startActivity(intent);
//            finish(); // Menutup MainActivity secara normal
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            new MyDialogFragment().show(getSupportFragmentManager(), "MyDialogFragmentTag");
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
