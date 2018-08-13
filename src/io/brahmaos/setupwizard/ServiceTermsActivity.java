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

public class ServiceTermsActivity extends Activity {
    private ProgressBar pbarLoading;
    private WebView wvService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_service_terms);
        initView();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initView() {

        pbarLoading = findViewById(R.id.loading_pbar);
        pbarLoading.setVisibility(View.VISIBLE);

        wvService = findViewById(R.id.service_terms_wv);
        wvService.setVisibility(View.GONE);

        wvService.setWebViewClient(new ServiceWebViewClient());
        WebSettings webSettings = wvService.getSettings();
        webSettings.setJavaScriptEnabled(true);
        wvService.loadUrl(BrahmaConfig.getServiceTermsUrl());
    }

    public class ServiceWebViewClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            pbarLoading.setVisibility(View.GONE);
            wvService.setVisibility(View.VISIBLE);
            super.onPageFinished(view, url);
        }
    }
}
