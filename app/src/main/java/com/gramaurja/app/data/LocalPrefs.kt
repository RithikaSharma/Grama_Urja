package com.gramaurja.app.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object LocalPrefs {
    private lateinit var sp: SharedPreferences

    private val _langSignal = MutableStateFlow("en")
    val langSignal = _langSignal.asStateFlow()

    fun init(ctx: Context) {
        sp = ctx.getSharedPreferences("gu", Context.MODE_PRIVATE)
        _langSignal.value = sp.getString("lang", "en") ?: "en"
    }

    var lang: String
        get() = _langSignal.value
        set(v) {
            sp.edit().putString("lang", v).apply()
            _langSignal.value = v
        }

    fun t(key: String): String {
        val en = mapOf(
            "appName" to "Grama Urja",
            "welcomeMsg" to "Know when the power is back — without leaving home.",
            "getStarted" to "Get started — it's free",
            "howItWorks" to "How it works",
            "poweredOn" to "Power is back ON!",
            "off" to "OFF",
            "listening" to "Listening for power updates..."
        )
        val kn = mapOf(
            "appName" to "ಗ್ರಾಮ ಉರ್ಜಾ",
            "welcomeMsg" to "ಮನೆ ಬಿಟ್ಟು ಹೊರಗೆ ಹೋಗದೆ ವಿದ್ಯುತ್ ಯಾವಾಗ ಬರುತ್ತದೆ ಎಂದು ತಿಳಿಯಿರಿ.",
            "getStarted" to "ಪ್ರಾರಂಭಿಸಿ — ಇದು ಉಚಿತ",
            "howItWorks" to "ಇದು ಹೇಗೆ ಕೆಲಸ ಮಾಡುತ್ತದೆ",
            "poweredOn" to "ವಿದ್ಯುತ್ ಬಂದಿದೆ!",
            "off" to "ಬಂದ್ ಆಗಿದೆ",
            "listening" to "ವಿದ್ಯುತ್ ನವೀಕರಣಗಳಿಗಾಗಿ ಕಾಯಲಾಗುತ್ತಿದೆ..."
        )
        return if (_langSignal.value == "kn") kn[key] ?: key else en[key] ?: key
    }

    // This stores the UUID from Screenshot 2026-05-13 120910_2.png
    var watchedZoneId: String?
        get() = sp.getString("watchedZone", null)
        set(v) { sp.edit().putString("watchedZone", v).apply() }

    // This stores the last power_status so we only notify on a CHANGE
    var lastKnownStatus: String?
        get() = sp.getString("lastStatus", null)
        set(v) { sp.edit().putString("lastStatus", v).apply() }
}