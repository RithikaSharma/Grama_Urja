package com.gramaurja.app.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.gramaurja.app.MainActivity
import com.gramaurja.app.data.LocalPrefs
import com.gramaurja.app.data.Supa
import com.gramaurja.app.data.repo.Repo
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class PowerWatchService : LifecycleService() {

    companion object {
        const val WATCH_CHANNEL = "watch"
        const val POWER_CHANNEL = "power"
        const val NOTIF_ID_ONGOING = 1
        private const val TAG = "PowerWatch"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
        startForeground(NOTIF_ID_ONGOING, buildOngoingNotification())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        watch()
        return START_STICKY
    }

    private fun buildOngoingNotification(): Notification {
        val pi = PendingIntent.getActivity(this, 0, Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        return NotificationCompat.Builder(this, WATCH_CHANNEL)
            .setContentTitle(LocalPrefs.t("appName"))
            .setContentText(LocalPrefs.t("listening"))
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .setContentIntent(pi)
            .build()
    }

    private fun watch() {
        lifecycleScope.launch {
            while (isActive) {
                val zid = LocalPrefs.watchedZoneId ?: break
                runCatching {
                    val ch = Supa.client.channel("power-monitor")
                    val flow = ch.postgresChangeFlow<PostgresAction>(schema = "public") {
                        table = "zones"
                    }
                    ch.subscribe()
                    Log.d(TAG, "Subscribed and Listening for Bantwal status updates...")

                    flow.collect { action ->
                        Log.d(TAG, "Database Change Detected")

                        delay(2000) // Ensure DB write is finished
                        val z = Repo.getZone(zid) ?: return@collect
                        val prev = LocalPrefs.lastKnownStatus

                        Log.d(TAG, "Comparing Status -> Previously: $prev | Currently: ${z.powerStatus}")

                        if (prev != z.powerStatus) {
                            Log.d(TAG, "Power status changed! Updating notification.")
                            notifyChange(z.name, z.powerStatus)
                            LocalPrefs.lastKnownStatus = z.powerStatus
                        }
                    }
                }.onFailure { e ->
                    Log.e(TAG, "Realtime Stream Error: ${e.message}")
                    delay(5000)
                }
            }
        }
    }

    private fun notifyChange(zoneName: String, status: String) {
        val body = if (status == "on") "${LocalPrefs.t("poweredOn")} ($zoneName)"
        else "$zoneName: ${LocalPrefs.t("off")}"

        val notificationId = System.currentTimeMillis().toInt()
        val pi = PendingIntent.getActivity(this, notificationId,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        val builder = NotificationCompat.Builder(this, POWER_CHANNEL)
            .setContentTitle(LocalPrefs.t("appName"))
            .setContentText(body)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true)
            .setContentIntent(pi)

        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                nm.notify(notificationId, builder.build())
            }
        } else {
            nm.notify(notificationId, builder.build())
        }
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val watchChan = NotificationChannel(WATCH_CHANNEL, "Service Status", NotificationManager.IMPORTANCE_LOW)
            val powerChan = NotificationChannel(POWER_CHANNEL, "Power Alerts", NotificationManager.IMPORTANCE_HIGH).apply {
                enableLights(true)
                enableVibration(true)
            }
            manager.createNotificationChannels(listOf(watchChan, powerChan))
        }
    }
}