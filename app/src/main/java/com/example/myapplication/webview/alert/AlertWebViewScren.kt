package com.example.myapplication.webview.alert

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.webkit.JsPromptResult
import android.webkit.JsResult
import android.webkit.WebView
import android.widget.EditText
import android.widget.TextView
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.R
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.google.accompanist.web.AccompanistWebChromeClient
import com.google.accompanist.web.WebView
import com.google.accompanist.web.rememberWebViewStateWithHTMLData


@SuppressLint("SetJavaScriptEnabled")
@Composable
fun AlertWebViewScreen() {
    val state = rememberWebViewStateWithHTMLData(
        """
            <!DOCTYPE html>
            <html>
            <head>
                <title>Alert, Prompt, Confirm 예제</title>
            </head>
            <body>
                <h1>Alert, Prompt, Confirm 예제</h1>

                <!-- Alert 버튼 -->
                <button onclick="showAlert()">Alert 보이기</button>

                <!-- Prompt 버튼 -->
                <button onclick="showPrompt()">Prompt 보이기</button>

                <!-- Confirm 버튼 -->
                <button onclick="showConfirm()">Confirm 보이기</button>

                <script>
                    // Alert를 보이는 함수
                    function showAlert() {
                        alert("알림 메시지입니다!");
                    }

                    // Prompt를 보이는 함수
                    function showPrompt() {
                        var userInput = prompt("이름을 입력하세요:", "홍길동");
                        if (userInput !== null) {
                            alert("입력한 이름은 " + userInput + "입니다.");
                        }
                    }

                    // Confirm을 보이는 함수
                    function showConfirm() {
                        var result = confirm("계속 진행하시겠습니까?");
                        if (result) {
                            alert("확인 버튼이 눌렸습니다.");
                        } else {
                            alert("취소 버튼이 눌렸습니다.");
                        }
                    }
                </script>
            </body>
            </html>
        """.trimIndent()
    )

    val alertChromeClient = remember { AlertChromeClient() }

    WebView(
        state = state,
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        onCreated = { webView ->
            webView.settings.javaScriptEnabled = true
        },
        chromeClient = alertChromeClient
    )
}

class AlertChromeClient : AccompanistWebChromeClient() {
    override fun onJsAlert(
        view: WebView,
        url: String,
        message: String,
        result: JsResult
    ): Boolean {
        AlertDialog.Builder(view.context)
            .setTitle("Custom")
            .setMessage(message)
            .setPositiveButton("확인") { _, _ ->
                result.confirm()
            }.setOnCancelListener {
                result.cancel()
            }.show()
        return true
    }

    override fun onJsConfirm(
        view: WebView,
        url: String,
        message: String,
        result: JsResult
    ): Boolean {
        AlertDialog.Builder(view.context)
            .setTitle("Custom")
            .setMessage(message)
            .setPositiveButton("확인") { _, _ ->
                result.confirm()
            }.setNegativeButton("취소") { _, _ ->
                result.cancel()
            }.setOnCancelListener {
                result.cancel()
            }.show()
        return true
    }

    override fun onJsPrompt(
        view: WebView,
        url: String,
        message: String,
        defaultValue: String,
        result: JsPromptResult
    ): Boolean {
        val inflater = LayoutInflater.from(view.context)
        val promptView: View = inflater.inflate(R.layout.dialog_prompt, null, false)
        val messageView = promptView.findViewById<TextView>(R.id.message)
        messageView.text = message
        val valueView = promptView.findViewById<EditText>(R.id.value)
        valueView.setText(defaultValue)

        AlertDialog.Builder(view.context)
            .setView(promptView)
            .setPositiveButton("확인") { _, _ ->
                result.confirm(
                    valueView.text.toString()
                )
            }
            .setOnCancelListener { _ -> result.cancel() }
            .show()

        return true
    }
}

@Preview
@Composable
private fun AlertWebViewScreenPrev() {
    MyApplicationTheme {
        AlertWebViewScreen()
    }
}