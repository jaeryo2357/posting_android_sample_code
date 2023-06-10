package com.example.myapplication.webview.youtube

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.WindowManager
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.accompanist.web.AccompanistWebChromeClient
import com.google.accompanist.web.WebView
import com.google.accompanist.web.rememberWebViewState

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun YouTubeInWebViewScreen() {
    val state = rememberWebViewState("https://www.youtube.com/embed/OK986vgCMs8")
    val context = LocalContext.current
    val webViewChromeClient = remember { VideoWebChromeClient(context)}

    WebView(
        state = state,
        modifier = Modifier.fillMaxWidth().height(300.dp),
        onCreated = { webView ->
            webView.settings.javaScriptEnabled = true
        },
        chromeClient = webViewChromeClient
    )
}

class VideoWebChromeClient(
    private val context: Context
) : AccompanistWebChromeClient() {
    private val windowManager: WindowManager by lazy {
        context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }

    private var customView: View? = null
    override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
        if (customView != null) {
            callback?.onCustomViewHidden()
            return
        }

        customView = view
        windowManager.addView(customView, WindowManager.LayoutParams())
    }

    override fun onHideCustomView() {
        super.onHideCustomView()
        windowManager.removeView(customView)
        customView = null
    }
}