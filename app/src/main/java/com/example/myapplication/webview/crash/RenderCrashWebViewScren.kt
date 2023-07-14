package com.example.myapplication.webview.crash

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.View
import android.view.WindowManager
import android.webkit.RenderProcessGoneDetail
import android.webkit.WebView
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.accompanist.web.AccompanistWebChromeClient
import com.google.accompanist.web.AccompanistWebViewClient
import com.google.accompanist.web.WebView
import com.google.accompanist.web.rememberWebViewState

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun RenderCrashWebViewScreen() {
    val state = rememberWebViewState("chrome://crash")
    var onWebViewRenderProcessCrash by remember { mutableStateOf(false) }

    if (onWebViewRenderProcessCrash) {
        WebView(
            state = state,
            modifier = Modifier.fillMaxWidth(),
            onCreated = { webView ->
                webView.settings.javaScriptEnabled = true
            },
            client = remember {
                CrashHandleWebClient(
                    onRenderProcessCrash = {
                        onWebViewRenderProcessCrash = true
                    }
                )
            }
        )
    } else {
        Text(
            text = "WebView rendering process가 죽었어요 ㅜㅜ"
        )
    }
}

class CrashHandleWebClient(
    private val onRenderProcessCrash: () -> Unit = {},
) : AccompanistWebViewClient() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onRenderProcessGone(view: WebView?, detail: RenderProcessGoneDetail?): Boolean {
        /**
         * 1.
         * return true
         *
         * 앱은 죽지 않지만 빈 화면만 사용자에게 표시 됨
         */


        if (detail?.didCrash() == true) {
            onRenderProcessCrash()
            return true
        }
        return super.onRenderProcessGone(view, detail)
    }
}