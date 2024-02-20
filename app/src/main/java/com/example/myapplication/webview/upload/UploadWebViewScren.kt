package com.example.myapplication.webview.upload

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.webkit.ValueCallback
import android.webkit.WebView
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.web.AccompanistWebChromeClient
import com.google.accompanist.web.WebView
import com.google.accompanist.web.rememberWebViewStateWithHTMLData

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun UploadWebViewScreen() {
    val state = rememberWebViewStateWithHTMLData(
        """
            <!DOCTYPE html>
            <html>
            <body>

            <p>Click on the "Choose File" button to upload a file:</p>

            <form action="/action_page.php">
              <input type="file" id="myFile" name="filename">
              <input type="submit">
            </form>

            </body>
            </html>
        """.trimIndent()
    )

    var fileChooserIntent by remember { mutableStateOf<Intent?>(null) }

    val webViewChromeClient = remember { FileUploadWebChromeClient(
        onShowFilePicker = {
            fileChooserIntent = it
        }
    ) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data?.data
            if (data != null) {
                // 추가 구현으로 data에서 Uri을 모두 가져와 List로 넘길 수도 있어요
                webViewChromeClient.selectFiles(arrayOf(data))
            } else {
                webViewChromeClient.cancelFileChooser()
            }
        } else {
            webViewChromeClient.cancelFileChooser()
        }
    }

    LaunchedEffect(key1 = fileChooserIntent) {
        if (fileChooserIntent != null) {
            try {
                launcher.launch(fileChooserIntent)
            } catch (e: ActivityNotFoundException) {
                // 기기에 알맞는 File picker가 없을 경우 취소
                webViewChromeClient.cancelFileChooser()
            }
        }
    }


    WebView(
        state = state,
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        onCreated = { webView ->
            webView.settings.javaScriptEnabled = true
        },
        chromeClient = webViewChromeClient
    )
}

class FileUploadWebChromeClient(
    private val onShowFilePicker: (Intent) -> Unit
): AccompanistWebChromeClient() {
    private var filePathCallback: ValueCallback<Array<Uri>>? = null

    override fun onShowFileChooser(
        webView: WebView?,
        filePathCallback: ValueCallback<Array<Uri>>?,
        fileChooserParams: FileChooserParams?
    ): Boolean {
        this.filePathCallback = filePathCallback
        val filePickerIntent = fileChooserParams?.createIntent()
        if (filePickerIntent == null) {
            cancelFileChooser()
        } else {
            onShowFilePicker(filePickerIntent)
        }
        return true
    }

    fun selectFiles(uris: Array<Uri>) {
        filePathCallback?.onReceiveValue(uris)
        filePathCallback = null
    }

    fun cancelFileChooser() {
        filePathCallback?.onReceiveValue(null)
        filePathCallback = null
    }
}