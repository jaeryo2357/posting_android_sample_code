package com.example.myapplication

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import com.example.myapplication.notification.MessageStyleNotificationActivity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity(
            Intent(this, MessageStyleNotificationActivity::class.java)
        )
    }
}