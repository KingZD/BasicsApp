package com.project.jaijite.activity;

import android.net.http.SslError;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.project.jaijite.R;
import com.project.jaijite.base.BaseTitleActivity;
import com.project.jaijite.util.LogUtils;

import butterknife.BindView;

public class WebViewActivity extends BaseTitleActivity {
    public static String PARAM_URL = "param_url";
    public static String PARAM_NAME = "param_name";
    @BindView(R.id.loadingBar)
    ProgressBar loadingBar;
    @BindView(R.id.errorUrl)
    TextView errorUrlV;
    @BindView(R.id.webView)
    WebView webView;
    private int time = 0;
    private String title;
    private String url;

    @Override
    public int getLayoutId() {
        return R.layout.activity_web_view;
    }

    @Override
    public void initView() {
        this.url = getIntent().getStringExtra(PARAM_URL);
        if ((this.url != null) && (!this.url.startsWith("http://")) && (!this.url.startsWith("https://"))) {
            this.url = ("http://" + this.url);
        }
        LogUtils.v(this.url);
        this.title = getIntent().getStringExtra(PARAM_NAME);
        setTvTitle(title);
        this.webView.getSettings().setJavaScriptEnabled(true);
        this.webView.getSettings().setSupportZoom(true);
        this.webView.getSettings().setBuiltInZoomControls(true);
        this.webView.getSettings().setUseWideViewPort(true);
        this.webView.getSettings().setLoadWithOverviewMode(true);
        this.webView.getSettings().setAppCacheEnabled(true);
        this.webView.getSettings().setDomStorageEnabled(true);
        this.webView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView paramAnonymousWebView, String paramAnonymousString) {
                paramAnonymousWebView.getSettings().setBlockNetworkImage(false);
                super.onPageFinished(paramAnonymousWebView, paramAnonymousString);
            }

            public void onReceivedError(WebView paramAnonymousWebView, int paramAnonymousInt, String paramAnonymousString1, String paramAnonymousString2) {
                showLoadingBar(false, false, 100);
                super.onReceivedError(paramAnonymousWebView, paramAnonymousInt, paramAnonymousString1, paramAnonymousString2);
            }

            public void onReceivedSslError(WebView paramAnonymousWebView, SslErrorHandler paramAnonymousSslErrorHandler, SslError paramAnonymousSslError) {
                super.onReceivedSslError(paramAnonymousWebView, paramAnonymousSslErrorHandler, paramAnonymousSslError);
                paramAnonymousSslErrorHandler.proceed();
            }

            public boolean shouldOverrideUrlLoading(WebView paramAnonymousWebView, String paramAnonymousString) {
                paramAnonymousWebView.getSettings().setBlockNetworkImage(true);
                if (!TextUtils.isEmpty(paramAnonymousString)) {
                    showLoadingBar(true, true, 0);
                    toLoad(paramAnonymousWebView, paramAnonymousString);
                }
                return true;
            }
        });
        this.webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView paramAnonymousWebView, int paramAnonymousInt) {
                if (paramAnonymousInt == 100) {
                    showLoadingBar(false, false, paramAnonymousInt);
                    return;
                }
                showLoadingBar(false, true, paramAnonymousInt);
            }

            public void onReceivedTitle(WebView paramAnonymousWebView, String paramAnonymousString) {
                super.onReceivedTitle(paramAnonymousWebView, paramAnonymousString);
                setTvTitle(paramAnonymousString);
            }
        });
        this.webView.loadUrl(url);
    }

    private void showLoadingBar(boolean paramBoolean1, boolean paramBoolean2, int paramInt) {
        if (paramBoolean1) {
            this.loadingBar.setVisibility(View.VISIBLE);
            this.loadingBar.setProgress(0);
            this.loadingBar.setMax(100);
            return;
        }
        this.loadingBar.setProgress(paramInt);
        if (paramInt == 100) {
            this.loadingBar.setVisibility(View.GONE);
        }
    }

    private void toLoad(WebView paramWebView, String paramString) {
        if (paramWebView != null) {
            paramWebView.loadUrl(paramString);
        }
    }

    public void close(View paramView) {
        finish();
    }

    protected void onDestroy() {
        Log.d("MyWebView", "onDestroy");
        if (this.webView != null) {
            this.webView.setVisibility(View.GONE);
            this.webView.destroy();
            this.webView = null;
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if ((this.webView != null) && (this.webView.canGoBack())) {
            this.webView.goBack();
            return;
        }
        super.onBackPressed();
    }
}
