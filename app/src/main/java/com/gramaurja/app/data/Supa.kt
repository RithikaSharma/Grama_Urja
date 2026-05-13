package com.gramaurja.app.data

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.ktor.client.engine.okhttp.OkHttp // Essential for WebSockets

object Supa {
    const val URL = "https://aqjmavxbcmgmbczydlff.supabase.co"
    const val ANON = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImFxam1hdnhiY21nbWJjenlkbGZmIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzcxNzk5MzcsImV4cCI6MjA5Mjc1NTkzN30.R-EKEoWzf-3VIVjLtBqMxQ1_kj9zM5ukAuCV7rEy8hw"

    val client = createSupabaseClient(URL, ANON) {
        // Use OkHttp engine to support WebSockets (fixes the 'Capability' crash)
        httpEngine = OkHttp.create()

        install(Auth)
        install(Postgrest)
        install(Realtime)
    }
}