package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.navigation.login.NavigationLoginActivity
import com.example.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                Box(Modifier.fillMaxSize()) {
                    TextHyperLinkContent()
                    Button(
                        onClick = {
                            startActivity(Intent(this@MainActivity, NavigationLoginActivity::class.java))
                        },
                        modifier = Modifier.align(Alignment.BottomCenter).padding(24.dp),
                    ) {
                        Text("Navigation 3 로그인 실습")
                    }
                }
            }
        }
    }
}
