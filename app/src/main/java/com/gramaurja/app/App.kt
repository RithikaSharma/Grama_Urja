package com.gramaurja.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.gramaurja.app.data.LocalPrefs

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
        LocalPrefs.init(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = getSystemService(NotificationManager::class.java)
            nm.createNotificationChannel(
                NotificationChannel("power", "Power Status", NotificationManager.IMPORTANCE_HIGH)
                    .apply { description = "Pump / power status updates for your village" }
            )
            nm.createNotificationChannel(
                NotificationChannel("watch", "Background watcher", NotificationManager.IMPORTANCE_LOW)
                    .apply { description = "Keeps the app listening for power changes" }
            )
        }
    }
    companion object { lateinit var instance: App; private set }
}
