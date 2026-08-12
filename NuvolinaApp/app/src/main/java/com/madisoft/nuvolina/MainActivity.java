package com.madisoft.nuvolina;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Bitmap;
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
        
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.setAcceptThirdPartyCookies(webView, true);

        setContentView(webView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (url != null) {
                    String lowerUrl = url.toLowerCase();
                    // Intercettore di Rete: se Nuvola prova ad aprire la Dashboard/Home, la blocchiamo e andiamo sui Compiti
                    if (lowerUrl.equals("https://nuvola.madisoft.it/") || 
                        lowerUrl.equals("https://nuvola.madisoft.it") || 
                        lowerUrl.equals("https://nuvola.madisoft.it/area-tutore/") || 
                        lowerUrl.equals("https://nuvola.madisoft.it/area-tutore") ||
                        lowerUrl.contains("/dashboard")) {
                        view.stopLoading();
                        view.loadUrl("https://nuvola.madisoft.it/area-tutore/compiti");
                    }
                }
            }

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
                String js = "javascript:(function() { " +
                    // 1. CSS Injection: Nasconde fisicamente i link ancor prima che l'utente possa cliccarli
                    "if (!window.nuvolaCssInjected) {" +
                    "  window.nuvolaCssInjected = true;" +
                    "  var style = document.createElement('style');" +
                    "  style.innerHTML = " +
                    "    'a[href*=\"/voti\"], a[href*=\"/assenze\"], a[href*=\"/note\"], a[href*=\"/colloqui\"], a[href*=\"/documenti\"], a[href*=\"/pagamenti\"], a[href*=\"/bacheche\"], a[href*=\"/materiale\"], a[href*=\"/scrutinio\"], a[href*=\"/modulistica\"], a[href*=\"/calendario\"] { display: none !important; opacity: 0 !important; pointer-events: none !important; position: absolute !important; left: -9999px !important; } ' +" +
                    "    '.app-header-user, .user-profile, .dropdown-menu { display: none !important; }';" +
                    "  document.head.appendChild(style);" +
                    "}" +

                    // 2. History Sabotage: Se il sito SPA tenta di cambiare URL ai Voti, lo rimbalziamo sui Compiti
                    "if (!window.nuvolaHistorySabotaged) {" +
                    "  window.nuvolaHistorySabotaged = true;" +
                    "  var originalPushState = history.pushState;" +
                    "  history.pushState = function(state, title, url) {" +
                    "    if (url && (url.indexOf('/voti') !== -1 || url.indexOf('/assenze') !== -1 || url.indexOf('dashboard') !== -1 || url.indexOf('/note') !== -1)) {" +
                    "        url = 'https://nuvola.madisoft.it/area-tutore/compiti';" +
                    "    }" +
                    "    return originalPushState.apply(this, arguments);" +
                    "  };" +
                    "  var originalReplaceState = history.replaceState;" +
                    "  history.replaceState = function(state, title, url) {" +
                    "    if (url && (url.indexOf('/voti') !== -1 || url.indexOf('/assenze') !== -1 || url.indexOf('dashboard') !== -1 || url.indexOf('/note') !== -1)) {" +
                    "        url = 'https://nuvola.madisoft.it/area-tutore/compiti';" +
                    "    }" +
                    "    return originalReplaceState.apply(this, arguments);" +
                    "  };" +
                    "}" +

                    // 3. Text-based MutationObserver per elementi difficili (es. Nome del tutore o tasti Home)
                    "if (!window.nuvolaObserverStarted) {" +
                    "  window.nuvolaObserverStarted = true;" +
                    "  var hideByText = function() {" +
                    "    document.querySelectorAll('li, a, button, span, div').forEach(function(item) {" +
                    "      var text = item.textContent.trim();" +
                    "      if (text.length > 0 && text.length < 50) {" +
                    "        if (text === 'LUCA FEI' || text === 'LF' || text === 'Voti' || text.indexOf('Assenze') !== -1 || text === 'Home' || text.indexOf('Home') === 0) {" +
                    "           var parent = item.closest('a, button, li, .dropdown');" +
                    "           if (parent) { parent.style.display = 'none'; parent.style.opacity = '0'; parent.style.pointerEvents = 'none'; }" +
                    "           item.style.display = 'none'; item.style.opacity = '0'; item.style.pointerEvents = 'none';" +
                    "        }" +
                    "      }" +
                    "    });" +
                    "  };" +
                    "  hideByText();" + // run immediately
                    "  var observer = new MutationObserver(hideByText);" +
                    "  observer.observe(document.documentElement, { childList: true, subtree: true });" +
                    "}" +
                "})();";
                view.evaluateJavascript(js, null);
            }
        });

        webView.loadUrl("https://nuvola.madisoft.it/area-tutore/compiti");
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
