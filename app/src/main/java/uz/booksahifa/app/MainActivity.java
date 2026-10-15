package uz.booksahifa.app;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceError;
import android.content.Intent;
import android.net.Uri;

public class MainActivity extends Activity {
    private WebView webView;
    private FrameLayout container;
    private ProgressBar progressBar;
    private TextView errorText;

    private static final String SITE_URL =
        "https://kaleidoscopic-fox-2832ce.netlify.app";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        container = new FrameLayout(this);
        container.setBackgroundColor(Color.WHITE);
        setContentView(container);

        progressBar = new ProgressBar(this);
        FrameLayout.LayoutParams progressParams =
            new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER
            );
        container.addView(progressBar, progressParams);

        errorText = new TextView(this);
        errorText.setTextColor(Color.DKGRAY);
        errorText.setTextSize(16);
        errorText.setGravity(Gravity.CENTER);
        errorText.setPadding(32, 32, 32, 32);
        errorText.setVisibility(android.view.View.GONE);
        container.addView(errorText, new FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        ));

        webView = new WebView(this);
        container.addView(webView, new FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        ));

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setLoadsImagesAutomatically(true);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);

        webView.setBackgroundColor(Color.WHITE);
        webView.setWebChromeClient(new WebChromeClient());

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url,
                                      android.graphics.Bitmap favicon) {
                progressBar.setVisibility(android.view.View.VISIBLE);
                errorText.setVisibility(android.view.View.GONE);
                webView.setVisibility(android.view.View.INVISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(android.view.View.GONE);
                webView.setVisibility(android.view.View.VISIBLE);
            }

            @Override
            public void onReceivedError(WebView view,
                                        WebResourceRequest request,
                                        WebResourceError error) {
                if (request.isForMainFrame()) {
                    progressBar.setVisibility(android.view.View.GONE);
                    webView.setVisibility(android.view.View.GONE);
                    errorText.setText(
                        "Sahifani ochib bo‘lmadi.\nInternetni tekshiring va qayta urinib ko‘ring."
                    );
                    errorText.setVisibility(android.view.View.VISIBLE);
                    errorText.setOnClickListener(v -> loadSite());
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(
                    WebView view, WebResourceRequest request) {
                Uri uri = request.getUrl();
                String scheme = uri.getScheme();

                if ("https".equals(scheme) || "http".equals(scheme)) {
                    return false;
                }

                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, uri));
                } catch (Exception ignored) {
                }
                return true;
            }
        });

        loadSite();
    }

    private void loadSite() {
        if (webView != null) {
            errorText.setVisibility(android.view.View.GONE);
            progressBar.setVisibility(android.view.View.VISIBLE);
            webView.setVisibility(android.view.View.INVISIBLE);
            webView.loadUrl(SITE_URL);
        }
    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.destroy();
            webView = null;
        }
        super.onDestroy();
    }
}
