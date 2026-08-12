package com.madisoft.nuvolina;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.ImageView;
import android.view.View;
import android.view.ViewGroup;
import android.graphics.Color;

public class MainActivity extends Activity {
    private WebView webView;
    private ImageView splash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        RelativeLayout layout = new RelativeLayout(this);
        
        webView = new WebView(this);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.setAcceptThirdPartyCookies(webView, true);

        layout.addView(webView, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        splash = new ImageView(this);
        splash.setBackgroundColor(Color.parseColor("#C8A2C8"));
        splash.setImageResource(R.mipmap.ic_launcher);
        splash.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        layout.addView(splash, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        setContentView(layout);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString().toLowerCase();
                if (url.contains("/voti") || url.contains("/assenze") || url.contains("/note") || 
                    url.contains("/pagamenti") || url.contains("/colloqui") || url.contains("/documenti") || url.contains("/bacheche")) {
                    return true; 
                }
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (url != null) {
                    String curr = url.toLowerCase();
                    // Hide immediately on safe pages
                    if (curr.contains("login") || curr.contains("auth") || curr.contains("compiti") || curr.contains("argomenti")) {
                        splash.setVisibility(View.GONE);
                    }
                }

                String js = "javascript:(function() { " +
                    "function hideUnwanted() {" +
                    "  var compitiLink = null;" +
                    "  var menuItems = document.querySelectorAll('li, a, div[role=\"button\"], button, span, div');" +
                    "  menuItems.forEach(function(item) {" +
                    "    var text = item.textContent.trim();" +
                    "    if(text.length > 0 && text.length < 50) {" +
                    "      var isAllowed = text.indexOf('Compiti') !== -1 || text.indexOf('Argomenti') !== -1 || text.indexOf('Petra') !== -1 || text.indexOf('1B') !== -1;" +
                    "      var isMenu = text === 'Voti' || text.indexOf('Assenze') !== -1 || text.indexOf('Note') !== -1 || text.indexOf('Calendario') !== -1 || text.indexOf('Colloqui') !== -1 || text.indexOf('Bacheche') !== -1 || text.indexOf('Documenti') !== -1 || text.indexOf('Pagamenti') !== -1 || text.indexOf('Modulistica') !== -1 || text.indexOf('Materiale') !== -1 || text.indexOf('Scrutinio') !== -1 || text === 'Home';" +
                    "      if(isMenu && !isAllowed) { item.style.display = 'none'; }" +
                    
                    "      if(text === 'LUCA FEI' || text === 'LF') {" +
                    "         var parent = item.closest('a, button, .dropdown, li');" +
                    "         if(parent) { parent.style.display = 'none'; } else { item.style.display = 'none'; }" +
                    "      }" +

                    "      if(text.indexOf('Compiti') !== -1 && item.tagName.toLowerCase() === 'a' && item.href && item.href.indexOf('javascript') === -1) {" +
                    "         compitiLink = item.href;" +
                    "      }" +
                    "    }" +
                    "  });" +
                    
                    "  var currUrl = window.location.href.toLowerCase();" +
                    "  if (compitiLink && currUrl.indexOf('compiti') === -1 && currUrl.indexOf('argomenti') === -1 && currUrl.indexOf('login') === -1 && currUrl.indexOf('auth') === -1) {" +
                    "    if (!window.hasRedirectedToCompiti) {" +
                    "       window.hasRedirectedToCompiti = true;" +
                    "       window.location.replace(compitiLink);" +
                    "    }" +
                    "  }" +
                    "}" +
                    
                    "hideUnwanted();" +
                    "if (!window.nuvolaObserverStarted) {" +
                    "  window.nuvolaObserverStarted = true;" +
                    "  var observer = new MutationObserver(function() { hideUnwanted(); });" +
                    "  observer.observe(document.documentElement, { childList: true, subtree: true });" +
                    "  setInterval(function() {" +
                    "    var curr = window.location.href.toLowerCase();" +
                    "    if(curr.indexOf('/voti')!==-1 || curr.indexOf('/assenze')!==-1 || curr.indexOf('/note')!==-1 || curr.indexOf('/pagamenti')!==-1 || curr.indexOf('/colloqui')!==-1 || curr.indexOf('/documenti')!==-1) {" +
                    "      window.location.replace('https://nuvola.madisoft.it/');" +
                    "    }" +
                    "  }, 1000);" +
                    "}" +
                "})();";
                view.evaluateJavascript(js, null);

                // Ultimate failsafe: unblock UI after 2.5 seconds regardless of what happens
                view.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (splash != null) splash.setVisibility(View.GONE);
                    }
                }, 2500);
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
