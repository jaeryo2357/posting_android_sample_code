package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.myapplication.touch.TouchExpandActivity
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.webview.crash.RenderCrashWebViewScreen
import com.example.myapplication.webview.upload.UploadWebViewScreen
import com.example.myapplication.webview.youtube.YouTubeInWebViewScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity(Intent(this, TouchExpandActivity::class.java))
    }
}