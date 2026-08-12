package com.madisoft.nuvolina;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends Activity {
    private WebView webView;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        webView = new WebView(this);
        setContentView(webView);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString().toLowerCase();
                if (url.contains("/voti") || url.contains("/assenze") || url.contains("/note") || 
                    url.contains("/pagamenti") || url.contains("/colloqui") || url.contains("/documenti")) {
                    return true; // Block navigation
                }
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                String js = "javascript:(function() { " +
                    "var menuItems = document.querySelectorAll('li, a.menu-item, .nav-item, .sidebar-menu-item, div[role=\"button\"]');" +
                    "menuItems.forEach(function(item) {" +
                    "  var text = item.textContent.trim();" +
                    "  if(text.length > 0 && text.length < 50) {" +
                    "    var isAllowed = text.indexOf('Compiti') !== -1 || text.indexOf('Argomenti') !== -1 || text.indexOf('Petra') !== -1 || text.indexOf('1B') !== -1;" +
                    "    var isMenu = text.indexOf('Voti') !== -1 || text.indexOf('Assenze') !== -1 || text.indexOf('Note') !== -1 || text.indexOf('Calendario') !== -1 || text.indexOf('Colloqui') !== -1 || text.indexOf('Bacheche') !== -1 || text.indexOf('Documenti') !== -1 || text.indexOf('Pagamenti') !== -1 || text.indexOf('Modulistica') !== -1 || text.indexOf('Materiale') !== -1;" +
                    "    if(isMenu && !isAllowed) { item.style.display = 'none'; }" +
                    "  }" +
                    "});" +
                "})();";
                view.evaluateJavascript(js, null);
            }
        });

        webView.loadUrl("https://nuvola.madisoft.it/login");
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
