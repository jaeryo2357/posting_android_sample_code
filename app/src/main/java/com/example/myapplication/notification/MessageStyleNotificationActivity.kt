package com.example.myapplication.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.core.app.NotificationCompat
import androidx.core.app.Person
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.IconCompat
import androidx.core.net.toUri
import androidx.core.os.BuildCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.ui.theme.MyApplicationTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.Date


class MessageStyleNotificationActivity : ComponentActivity() {

    companion object {
        private const val channelId = "channelId"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MyApplicationTheme {
                Button(
                    onClick = { showNotification() }
                ) {
                    Text(
                        text = "알림",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }

    private fun showNotification() {
        lifecycleScope.launch {
            getNotificationChannel()
            val notification = getMessageStyleNotification()
            val notificationManager = getSystemService(NotificationManager::class.java) ?: return@launch
            notificationManager.notify(0, notification)
        }
    }

    private fun getNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(NotificationManager::class.java) ?: return

            notificationManager.createNotificationChannel(
                NotificationChannel(channelId, "알림채널", NotificationManager.IMPORTANCE_HIGH)
            )
        }
    }

    private suspend fun getMessageStyleNotification(): Notification {
        val bitmap = withContext(Dispatchers.IO) {
            Glide.with(applicationContext)
                .asBitmap()
                .load("https://attic.sh/o1ihn7z2kmo8na457yr8ohpydpq1")
                .submit()
                .get()
        }

        val file = saveBitmapToJpeg(bitmap)
        val uri = file?.let { getFileUri(it) }
        uri?.grantReadPermissionToSystem()

        val person = Person.Builder()
            .setName("자비스")
            .setIcon(IconCompat.createWithBitmap(bitmap))
            .setImportant(true)
            .setKey(Math.random().toString())
            .build()

        val style = NotificationCompat.MessagingStyle(person)
            .addMessage(
                NotificationCompat.MessagingStyle.Message("image", Date().time, person)
                    .also {
                        if (uri != null) {
                            it.setData("image/", uri)
                        }
                    }

            )

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(
                PendingIntent.getActivity(
                    this, 0, intent.apply {
                    }, PendingIntent.FLAG_IMMUTABLE
                )
            )
            .setStyle(style)
            .setCategory(Notification.CATEGORY_MESSAGE)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        return builder.build()
    }

    private suspend fun saveBitmapToJpeg(bitmap: Bitmap): File? {
        val fileName = "${System.currentTimeMillis()}.jpg"

        val tempFile = File(cacheDir, fileName)
        return withContext(Dispatchers.IO) {
            try {
                tempFile.createNewFile()
                val out = FileOutputStream(tempFile)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
                out.close()

                tempFile
            } catch (ignore: Exception) {
                null
            }
        }
    }

    private fun getFileUri(file: File): Uri {
        return FileProvider.getUriForFile(this, "$packageName.fileprovider", file)
    }

    private fun Uri.grantReadPermissionToSystem() {
        applicationContext.grantUriPermission("com.android.systemui", this,
            Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
}