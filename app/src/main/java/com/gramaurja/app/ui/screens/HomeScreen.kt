package com.gramaurja.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gramaurja.app.data.LocalPrefs
import com.gramaurja.app.data.Supa
import com.gramaurja.app.data.model.StatusUpdate
import com.gramaurja.app.data.model.Zone
import com.gramaurja.app.data.repo.Repo
import com.gramaurja.app.ui.theme.Brand
import com.gramaurja.app.util.I18n
import com.gramaurja.app.util.I18n.t
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import io.github.jan.supabase.realtime.realtime
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

@Composable
fun HomeScreen(isAdmin: Boolean) {
    val scope = rememberCoroutineScope()
    var zone by remember { mutableStateOf<Zone?>(null) }
    var recent by remember { mutableStateOf<List<StatusUpdate>>(emptyList()) }
    var zoneId by remember { mutableStateOf<String?>(null) }
    var reporting by remember { mutableStateOf(false) }
    var msg by remember { mutableStateOf<String?>(null) }
    var tick by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        val uid = Repo.currentUserId() ?: return@LaunchedEffect
        val p = Repo.getProfile(uid)
        zoneId = p?.zoneId
        LocalPrefs.watchedZoneId = p?.zoneId
        zoneId?.let {
            zone = Repo.getZone(it)
            recent = Repo.recentUpdates(it)
        }
    }

    // Realtime: refresh on any change to this zone
    LaunchedEffect(zoneId) {
        val zid = zoneId ?: return@LaunchedEffect
        val ch = Supa.client.channel("home-$zid")
        val flow = ch.postgresChangeFlow<PostgresAction>(schema = "public") {
            table = "zones"; filter = "id=eq.$zid"
        }
        ch.subscribe()
        flow.collect {
            zone = Repo.getZone(zid)
            recent = Repo.recentUpdates(zid)
        }
    }

    // Refresh "freshness" labels every 30s
    LaunchedEffect(Unit) {
        while (true) { kotlinx.coroutines.delay(30_000); tick++ }
    }

    if (zoneId == null || zone == null) {
        Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Filled.Place, null, modifier = Modifier.size(48.dp))
            Spacer(Modifier.height(12.dp))
            Text(t("noZoneSelected"), style = MaterialTheme.typography.bodyLarge)
        }
        return
    }

    val z = zone!!
    val isOn = z.powerStatus == "on"
    val isOff = z.powerStatus == "off"

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.Place, null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(6.dp))
            Text(z.name, fontWeight = FontWeight.SemiBold)
        }
        Spacer(Modifier.height(12.dp))

        // Big status card
        val cardBg = when {
            isOn -> Brush.linearGradient(listOf(Brand.PowerOn, MaterialTheme.colorScheme.primary))
            isOff -> Brush.linearGradient(listOf(Brand.PowerOff, Color(0xFF0F172A)))
            else -> Brush.linearGradient(listOf(MaterialTheme.colorScheme.surface, MaterialTheme.colorScheme.surface))
        }
        val cardFg = if (isOn) Brand.OnPowerOn else if (isOff) Brand.OnPowerOff else MaterialTheme.colorScheme.onSurface

        Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(28.dp)).background(cardBg).padding(20.dp)) {
            Column {
                Text(t("powerStatus").uppercase(), color = cardFg.copy(alpha = 0.85f), fontSize = 12.sp)
                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.PowerSettingsNew, null, tint = cardFg, modifier = Modifier.size(48.dp))
                    Spacer(Modifier.width(10.dp))
                    Text(
                        t(if (isOn) "on" else if (isOff) "off" else "unknown"),
                        color = cardFg, fontSize = 56.sp, fontWeight = FontWeight.Black
                    )
                }
                Spacer(Modifier.height(8.dp))
                key(tick) {
                    Text(I18n.freshness(z.lastUpdatedAt), color = cardFg.copy(alpha = 0.9f))
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        if (isAdmin) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        val uid = Repo.currentUserId() ?: return@Button
                        reporting = true
                        scope.launch {
                            runCatching { Repo.reportStatus(z.id, uid, "on") }
                                .onSuccess { msg = "Reported: ON" }
                                .onFailure { msg = it.message }
                            reporting = false
                        }
                    },
                    enabled = !reporting,
                    colors = ButtonDefaults.buttonColors(containerColor = Brand.PowerOn, contentColor = Brand.OnPowerOn),
                    modifier = Modifier.weight(1f).height(64.dp)
                ) { Text("⚡ ${t("reportOn")}", fontWeight = FontWeight.Bold) }
                Button(
                    onClick = {
                        val uid = Repo.currentUserId() ?: return@Button
                        reporting = true
                        scope.launch {
                            runCatching { Repo.reportStatus(z.id, uid, "off") }
                                .onSuccess { msg = "Reported: OFF" }
                                .onFailure { msg = it.message }
                            reporting = false
                        }
                    },
                    enabled = !reporting,
                    colors = ButtonDefaults.buttonColors(containerColor = Brand.PowerOff, contentColor = Brand.OnPowerOff),
                    modifier = Modifier.weight(1f).height(64.dp)
                ) { Text("🌑 ${t("reportOff")}", fontWeight = FontWeight.Bold) }
            }
        } else {
            Surface(tonalElevation = 1.dp, shape = RoundedCornerShape(16.dp)) {
                Text("Only admins can change pump status. You'll be notified when it changes.",
                    Modifier.fillMaxWidth().padding(16.dp), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            }
        }
        msg?.let { Spacer(Modifier.height(8.dp)); Text(it, color = MaterialTheme.colorScheme.primary) }

        Spacer(Modifier.height(20.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.History, null, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(6.dp))
            Text(t("recentReports"), fontWeight = FontWeight.SemiBold)
        }
        Spacer(Modifier.height(8.dp))

        Surface(shape = RoundedCornerShape(16.dp), tonalElevation = 1.dp, modifier = Modifier.fillMaxWidth()) {
            if (recent.isEmpty()) {
                Text(t("noUpdates"), Modifier.fillMaxWidth().padding(16.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                LazyColumn {
                    items(recent) { u ->
                        Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically) {
                            val on = u.status == "on"
                            Surface(color = if (on) Brand.PowerOn else Brand.PowerOff,
                                shape = RoundedCornerShape(50)) {
                                Text(t(u.status), color = Color.White, fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                            }
                            Spacer(Modifier.weight(1f))
                            key(tick) {
                                Text(I18n.freshness(u.createdAt),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                            }
                        }
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}
