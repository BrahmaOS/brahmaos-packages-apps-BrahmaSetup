package io.brahmaos.setupwizard;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import io.brahmaos.setupwizard.util.BrahmaConfig;

public class PrivacyPolicyActivity extends Activity{
    private ProgressBar pbarLoading;
    private WebView wvPrivacy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_privacy_policy);
        initView();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initView() {

        pbarLoading = findViewById(R.id.loading_pbar);
        pbarLoading.setVisibility(View.VISIBLE);

        wvPrivacy = findViewById(R.id.privacy_policy_wv);
        wvPrivacy.setVisibility(View.GONE);

        wvPrivacy.setWebViewClient(new PrivacyWebViewClient());
        WebSettings webSettings = wvPrivacy.getSettings();
        webSettings.setJavaScriptEnabled(true);
        wvPrivacy.loadUrl(BrahmaConfig.getPrivacyUrl());
    }

    public class PrivacyWebViewClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            pbarLoading.setVisibility(View.GONE);
            wvPrivacy.setVisibility(View.VISIBLE);
            super.onPageFinished(view, url);
        }
    }
}
