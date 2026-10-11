package com.example.myapplication.navigation.login

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.myapplication.ui.theme.MyApplicationTheme

class NavigationLoginActivity : ComponentActivity() {
    private val model: LinkViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Restored state already owns the request, so do not replay the launch intent.
        if (savedInstanceState == null) {
            intent.dataString?.let { model.open(it, replacePath = true) }
        }
        setContent {
            MyApplicationTheme {
                NavigationLoginContent(
                    model = model,
                    recreateActivity = ::recreate,
                    finishActivity = ::finish,
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        intent.dataString?.let { model.open(it, replacePath = false) }
    }
}
