package com.angelo.jellyfintv

  import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

private lateinit var webView: WebView
  private lateinit var settingsPanel: LinearLayout
  private lateinit var urlInput: EditText
  private lateinit var prefs: android.content.SharedPreferences

  companion object {
  private const val PREFS_NAME = "jellyfintv_prefs"
private const val KEY_URL = "server_url"
    }

@SuppressLint("SetJavaScriptEnabled")
  override fun onCreate(savedInstanceState: Bundle?) {
  super.onCreate(savedInstanceState)
setContentView(R.layout.activity_main)

prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

webView = findViewById(R.id.webview)
settingsPanel = findViewById(R.id.settingsPanel)
urlInput = findViewById(R.id.urlInput)
val validateButton: Button = findViewById(R.id.validateButton)

  setupWebView()

val savedUrl = prefs.getString(KEY_URL, null)
if (!savedUrl.isNullOrBlank()) {
urlInput.setText(savedUrl)
  hideSettings()
webView.loadUrl(savedUrl)
  } else {
showSettings()
}

validateButton.setOnClickListener { validateAndLoad() }
urlInput.setOnEditorActionListener { _, _, _ ->
  validateAndLoad()
  true
  }
                                  }

private fun validateAndLoad() {
  var url = urlInput.text.toString().trim()
if (url.isBlank()) return
  if (!url.startsWith("http://") && !url.startsWith("https://")) {
url = "http://$url"
  }
prefs.edit().putString(KEY_URL, url).apply()
  hideSettings()
webView.loadUrl(url)
  }

private fun showSettings() {
  settingsPanel.visibility = View.VISIBLE
}

private fun hideSettings() {
  settingsPanel.visibility = View.GONE
}

@SuppressLint("SetJavaScriptEnabled")
  private fun setupWebView() {
  val settings: WebSettings = webView.settings
    settings.javaScriptEnabled = true
    settings.domStorageEnabled = true
    settings.databaseEnabled = true
    settings.loadWithOverviewMode = true
    settings.useWideViewPort = true
    settings.mediaPlaybackRequiresUserGesture = false
    settings.cacheMode = WebSettings.LOAD_DEFAULT
    settings.userAgentString = settings.userAgentString + " JellyfinTVWebView"

    webView.webViewClient = object : WebViewClient() {
override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
  view.loadUrl(url)
  return true
  }
}

webView.webChromeClient = WebChromeClient()
  webView.isFocusable = true
  webView.isFocusableInTouchMode = true
  webView.requestFocus()
  }

override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
  if (keyCode == KeyEvent.KEYCODE_MENU) {
if (settingsPanel.visibility == View.VISIBLE) {
hideSettings()
} else {
showSettings()
}
return true
  }
return super.onKeyDown(keyCode, event)
  }

override fun onBackPressed() {
  if (settingsPanel.visibility == View.VISIBLE) {
hideSettings()
  } else if (webView.canGoBack()) {
webView.goBack()
  } else {
super.onBackPressed()
  }
}
}
