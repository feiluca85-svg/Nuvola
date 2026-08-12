package com.madisoft.nuvolina;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.view.ViewGroup;

public class MainActivity extends Activity {
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        webView = new WebView(this);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.setAcceptThirdPartyCookies(webView, true);

        setContentView(webView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

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
                // Ensure cookies are saved to disk immediately
                CookieManager.getInstance().flush();

                String js = "javascript:(function() { " +
                    "function hideUnwanted() {" +
                    "  if (location.href.indexOf('login') !== -1) {" +
                    "    var checkboxes = document.querySelectorAll('input[type=\"checkbox\"]');" +
                    "    checkboxes.forEach(function(cb) {" +
                    "      if (!cb.checked) {" +
                    "        cb.checked = true;" +
                    "        cb.dispatchEvent(new Event('change', { bubbles: true }));" +
                    "      }" +
                    "    });" +
                    "  }" +

                    "  var menuItems = document.querySelectorAll('li, a, div[role=\"button\"], button, span, div');" +
                    "  menuItems.forEach(function(item) {" +
                    "    var text = item.textContent.trim();" +
                    "    if (text.length > 0 && text.length < 60) {" +
                    "      var isAllowed = text.indexOf('Compiti') !== -1 || text.indexOf('Argomenti') !== -1 || text.indexOf('Petra') !== -1 || text.indexOf('1B') !== -1;" +
                    "      var isForbidden = text === 'Voti' || text.indexOf('Assenze') !== -1 || text.indexOf('Note') !== -1 || " +
                    "                        text.indexOf('Calendario') !== -1 || text.indexOf('Colloqui') !== -1 || text.indexOf('Bacheche') !== -1 || " +
                    "                        text.indexOf('Documenti') !== -1 || text.indexOf('Pagamenti') !== -1 || text.indexOf('Modulistica') !== -1 || " +
                    "                        text.indexOf('Materiale') !== -1 || text.indexOf('Scrutinio') !== -1 || text === 'Home' || text.indexOf('Home') === 0 || " +
                    "                        text === 'LUCA FEI' || text === 'LF';" +
                    "      if (isForbidden && !isAllowed) {" +
                    "         var parent = item.closest('a, button, .dropdown, li');" +
                    "         if (parent) { parent.style.display = 'none'; }" +
                    "         item.style.display = 'none';" +
                    "      }" +
                    "    }" +
                    "  });" +
                    
                    "  var currUrl = window.location.href.toLowerCase();" +
                    "  if (currUrl.indexOf('login') === -1 && currUrl.indexOf('auth') === -1 && currUrl.indexOf('compiti') === -1 && currUrl.indexOf('argomenti') === -1) {" +
                    "    if (!window.hasRedirectedToCompiti) {" +
                    "       window.hasRedirectedToCompiti = true;" +
                    "       window.location.replace('https://nuvola.madisoft.it/area-tutore/compiti');" +
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
                    "      window.location.replace('https://nuvola.madisoft.it/area-tutore/compiti');" +
                    "    }" +
                    "  }, 1000);" +
                    "}" +
                "})();";
                view.evaluateJavascript(js, null);
            }
        });

        webView.loadUrl("https://nuvola.madisoft.it/area-tutore/compiti");
    }

    @Override
    protected void onPause() {
        super.onPause();
        CookieManager.getInstance().flush();
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
