package com.userplay.bazar22.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.userplay.bazar22.R;
import com.userplay.bazar22.utils.Constants;

public class WebChatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_chat);

        WebView webView = (WebView) findViewById(R.id.web);
        webView.loadUrl(Constants.LIVE_CHAT_URL);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setUserAgentString(System.getProperty("http.agent"));


        webView.setWebViewClient(new WebViewClient() {

            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (URLUtil.isNetworkUrl(url)) {
                    return false;
                }
                startActivity(new Intent("android.intent.action.VIEW", Uri.parse(url)));
                return true;
            }


        });
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);
        CookieManager.getInstance().flush();

        webView.setWebChromeClient(new WebChromeClient() {
            private ProgressDialog mProgress;

            public void onProgressChanged(WebView view, int progress) {
                if (this.mProgress == null) {
                    ProgressDialog progressDialog = new ProgressDialog(WebChatActivity.this);
                    this.mProgress = progressDialog;
                    progressDialog.show();
                }
                this.mProgress.setMessage("Loading " + String.valueOf(progress) + "%");
                if (progress == 100) {
                    this.mProgress.dismiss();
                    this.mProgress = null;
                }
            }
        });

        webView.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() != 0) {
                    return false;
                }
                WebView webView = (WebView) v;
                if (keyCode != 4 || !webView.canGoBack()) {
                    return false;
                }
                webView.goBack();
                return true;
            }
        });
    }
}