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
import io.github.jan_tennert.supabase.realtime.PostgresAction
import io.github.jan_tennert.supabase.realtime.realtime
import io.github.jan_tennert.supabase.realtime.channel
import io.github.jan_tennert.supabase.realtime.postgresChangeFlow
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.serialization.json.jsonPrimitive

/**
 * PowerWatchService is a Foreground Service that monitors electrical grid status in real-time.
 * 
 * Architecture:
 * 1. Uses Supabase Realtime (WebSockets) to listen for database changes in the 'zones' table.
 * 2. Maintains a 'Foreground' state to prevent the Android OS from killing the process.
 * 3. Compares current status against [LocalPrefs] to trigger high-priority user notifications
 *    only when a definitive status flip occurs.
 */
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
        // Foreground service requirement for Android 8.0+
        startForeground(NOTIF_ID_ONGOING, buildOngoingNotification())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        watch()
        return START_STICKY // Ensures service restarts if killed by the OS
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

    /**
     * Initializes the Supabase Realtime stream.
     * Implements a while(isActive) loop to handle automatic reconnection if the socket drops.
     */
    private fun watch() {
        lifecycleScope.launch {
            while (isActive) {
                val zid = LocalPrefs.watchedZoneId ?: break
                runCatching {
                    // Fix: Use .realtime extension to avoid overload ambiguity
                    val ch = Supa.client.realtime.channel("power-monitor")
                    val flow = ch.postgresChangeFlow<PostgresAction.Update>(schema = "public") {
                        table = "zones"
                        // filter = "id=eq.$zid" // Optional: Filter server-side for efficiency
                    }
                    
                    ch.subscribe()
                    Log.i(TAG, "Successfully subscribed to Realtime channel for zone: $zid")

                    flow.collect { action ->
                        Log.d(TAG, "Realtime event received: ${action.record}")

                        // Latency buffer to ensure database consistency before fetching
                        delay(2000) 
                        
                        val z = Repo.getZone(zid) ?: return@collect
                        val prev = LocalPrefs.lastKnownStatus

                        Log.i(TAG, "Status Check: [Old: $prev] -> [New: ${z.powerStatus}]")

                        if (prev != z.powerStatus) {
                            Log.w(TAG, "Alert: Status change detected for ${z.name}")
                            notifyChange(z.name, z.powerStatus)
                            LocalPrefs.lastKnownStatus = z.powerStatus
                        }
                    }
                }.onFailure { e ->
                    Log.e(TAG, "Realtime Stream Error: ${e.message}. Retrying in 5s...")
                    delay(5000)
                }
            }
        }
    }

    /**
     * Dispatches a high-priority notification to the user.
     * Handles Android 13+ (Tiramisu) notification permission checks.
     */
    private fun notifyChange(zoneName: String, status: String) {
        val body = if (status == "on") "${LocalPrefs.t("poweredOn")} ($zoneName) ⚡"
        else "$zoneName: ${LocalPrefs.t("off")} 🌑"

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
            val watchChan = NotificationChannel(WATCH_CHANNEL, "Background Monitoring", NotificationManager.IMPORTANCE_LOW)
            val powerChan = NotificationChannel(POWER_CHANNEL, "Grid Power Alerts", NotificationManager.IMPORTANCE_HIGH).apply {
                description = "Urgent notifications for power status flips"
                enableLights(true)
                enableVibration(true)
            }
            manager.createNotificationChannels(listOf(watchChan, powerChan))
        }
    }
}
