package com.example.myapplication

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startActivity(
                /**
                 * Settings.Pannel
                 *
                 * 인터넷 연결: ACTION_INTERNET_CONNECTIVITY
                 *
                 * NFC: ACTION_NFC
                 *
                 * 볼륨: ACTION_VOLUME
                 *
                 * WIFI: ACTION_WIFI
                 */

                Intent(Settings.Panel.ACTION_VOLUME)
            )
        }
    }
}