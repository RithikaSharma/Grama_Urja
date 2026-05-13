package com.gramaurja.app.util

import com.gramaurja.app.data.LocalPrefs

object I18n {
    private val dict: Map<String, Map<String, String>> = mapOf(
        "appName" to mapOf("en" to "Grama-Urja", "hi" to "ग्राम-ऊर्जा", "kn" to "ಗ್ರಾಮ-ಊರ್ಜಾ"),
        "tagline" to mapOf("en" to "Community-powered village electricity tracker",
            "hi" to "समुदाय-संचालित गाँव बिजली ट्रैकर",
            "kn" to "ಸಮುದಾಯ ಚಾಲಿತ ಗ್ರಾಮ ವಿದ್ಯುತ್ ಟ್ರ್ಯಾಕರ್"),
        "signIn" to mapOf("en" to "Sign in", "hi" to "साइन इन", "kn" to "ಸೈನ್ ಇನ್"),
        "signUp" to mapOf("en" to "Create account", "hi" to "खाता बनाएँ", "kn" to "ಖಾತೆ ರಚಿಸಿ"),
        "signOut" to mapOf("en" to "Sign out", "hi" to "साइन आउट", "kn" to "ಸೈನ್ ಔಟ್"),
        "email" to mapOf("en" to "Email", "hi" to "ईमेल", "kn" to "ಇಮೇಲ್"),
        "password" to mapOf("en" to "Password", "hi" to "पासवर्ड", "kn" to "ಪಾಸ್‌ವರ್ಡ್"),
        "name" to mapOf("en" to "Your name", "hi" to "आपका नाम", "kn" to "ನಿಮ್ಮ ಹೆಸರು"),
        "home" to mapOf("en" to "Home", "hi" to "होम", "kn" to "ಹೋಮ್"),
        "zones" to mapOf("en" to "Zones", "hi" to "क्षेत्र", "kn" to "ವಲಯಗಳು"),
        "pump" to mapOf("en" to "Pump Timer", "hi" to "पंप टाइमर", "kn" to "ಪಂಪ್ ಟೈಮರ್"),
        "admin" to mapOf("en" to "Admin", "hi" to "व्यवस्थापक", "kn" to "ನಿರ್ವಾಹಕ"),
        "settings" to mapOf("en" to "Settings", "hi" to "सेटिंग्स", "kn" to "ಸೆಟ್ಟಿಂಗ್ಗಳು"),
        "selectZone" to mapOf("en" to "Choose your village / zone",
            "hi" to "अपना गाँव / क्षेत्र चुनें",
            "kn" to "ನಿಮ್ಮ ಗ್ರಾಮ / ವಲಯವನ್ನು ಆಯ್ಕೆಮಾಡಿ"),
        "powerStatus" to mapOf("en" to "Power status", "hi" to "बिजली स्थिति", "kn" to "ವಿದ್ಯುತ್ ಸ್ಥಿತಿ"),
        "on" to mapOf("en" to "ON", "hi" to "चालू", "kn" to "ಆನ್"),
        "off" to mapOf("en" to "OFF", "hi" to "बंद", "kn" to "ಆಫ್"),
        "unknown" to mapOf("en" to "Unknown", "hi" to "अज्ञात", "kn" to "ಅಜ್ಞಾತ"),
        "reportOn" to mapOf("en" to "Report Power ON", "hi" to "बिजली चालू बताएँ", "kn" to "ವಿದ್ಯುತ್ ಆನ್ ವರದಿ"),
        "reportOff" to mapOf("en" to "Report Power OFF", "hi" to "बिजली बंद बताएँ", "kn" to "ವಿದ್ಯುತ್ ಆಫ್ ವರದಿ"),
        "noUpdates" to mapOf("en" to "No updates yet", "hi" to "अभी कोई अपडेट नहीं", "kn" to "ಇನ್ನೂ ಅಪ್‌ಡೇಟ್‌ಗಳಿಲ್ಲ"),
        "recentReports" to mapOf("en" to "Recent reports", "hi" to "हाल की रिपोर्टें", "kn" to "ಇತ್ತೀಚಿನ ವರದಿಗಳು"),
        "cropType" to mapOf("en" to "Crop type", "hi" to "फसल प्रकार", "kn" to "ಬೆಳೆ ಪ್ರಕಾರ"),
        "startTimer" to mapOf("en" to "Start pump timer", "hi" to "पंप टाइमर शुरू करें", "kn" to "ಪಂಪ್ ಟೈಮರ್ ಆರಂಭಿಸಿ"),
        "stopTimer" to mapOf("en" to "Stop", "hi" to "रोकें", "kn" to "ನಿಲ್ಲಿಸಿ"),
        "resetTimer" to mapOf("en" to "Reset", "hi" to "रीसेट", "kn" to "ಮರುಹೊಂದಿಸಿ"),
        "recommended" to mapOf("en" to "Recommended", "hi" to "अनुशंसित", "kn" to "ಶಿಫಾರಸು"),
        "minutes" to mapOf("en" to "min", "hi" to "मिनट", "kn" to "ನಿ"),
        "language" to mapOf("en" to "Language", "hi" to "भाषा", "kn" to "ಭಾಷೆ"),
        "myZone" to mapOf("en" to "My zone", "hi" to "मेरा क्षेत्र", "kn" to "ನನ್ನ ವಲಯ"),
        "saveProfile" to mapOf("en" to "Save", "hi" to "सहेजें", "kn" to "ಉಳಿಸಿ"),
        "addZone" to mapOf("en" to "Add zone", "hi" to "क्षेत्र जोड़ें", "kn" to "ವಲಯ ಸೇರಿಸಿ"),
        "zoneName" to mapOf("en" to "Zone name", "hi" to "क्षेत्र का नाम", "kn" to "ವಲಯದ ಹೆಸರು"),
        "description" to mapOf("en" to "Description (optional)", "hi" to "विवरण (वैकल्पिक)", "kn" to "ವಿವರಣೆ (ಐಚ್ಛಿಕ)"),
        "delete" to mapOf("en" to "Delete", "hi" to "हटाएँ", "kn" to "ಅಳಿಸಿ"),
        "noZoneSelected" to mapOf("en" to "Pick a village in Settings to start tracking power.",
            "hi" to "बिजली ट्रैक करने के लिए सेटिंग्स में एक गाँव चुनें।",
            "kn" to "ವಿದ್ಯುತ್ ಟ್ರ್ಯಾಕ್ ಮಾಡಲು ಸೆಟ್ಟಿಂಗ್‌ಗಳಲ್ಲಿ ಗ್ರಾಮವನ್ನು ಆಯ್ಕೆಮಾಡಿ."),
        "poweredOn" to mapOf("en" to "Power is ON in your village!",
            "hi" to "आपके गाँव में बिजली चालू है!",
            "kn" to "ನಿಮ್ಮ ಗ್ರಾಮದಲ್ಲಿ ವಿದ್ಯುತ್ ಆನ್ ಆಗಿದೆ!"),
        "justNow" to mapOf("en" to "just now", "hi" to "अभी अभी", "kn" to "ಈಗಷ್ಟೇ"),
        "minAgo" to mapOf("en" to "min ago", "hi" to "मिनट पहले", "kn" to "ನಿ ಹಿಂದೆ"),
        "hrAgo" to mapOf("en" to "h ago", "hi" to "घंटे पहले", "kn" to "ಗಂ ಹಿಂದೆ"),
        "dayAgo" to mapOf("en" to "d ago", "hi" to "दिन पहले", "kn" to "ದಿ ಹಿಂದೆ"),
    )
    fun t(key: String, lang: String = LocalPrefs.lang): String =
        dict[key]?.get(lang) ?: dict[key]?.get("en") ?: key

    fun freshness(iso: String?, lang: String = LocalPrefs.lang): String {
        if (iso.isNullOrBlank()) return t("noUpdates", lang)
        val ms = try { java.time.Instant.parse(iso).toEpochMilli() } catch (e: Exception) { return t("noUpdates", lang) }
        val sec = (System.currentTimeMillis() - ms) / 1000
        return when {
            sec < 30 -> t("justNow", lang)
            sec < 3600 -> "${sec / 60} ${t("minAgo", lang)}"
            sec < 86400 -> "${sec / 3600} ${t("hrAgo", lang)}"
            else -> "${sec / 86400} ${t("dayAgo", lang)}"
        }
    }
}
