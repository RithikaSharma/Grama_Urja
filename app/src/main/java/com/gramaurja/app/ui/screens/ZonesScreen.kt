package com.gramaurja.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gramaurja.app.data.model.Zone
import com.gramaurja.app.data.repo.Repo
import com.gramaurja.app.ui.theme.Brand
import com.gramaurja.app.util.I18n
import com.gramaurja.app.util.I18n.t

@Composable
fun ZonesScreen() {
    var zones by remember { mutableStateOf<List<Zone>>(emptyList()) }
    LaunchedEffect(Unit) { runCatching { zones = Repo.listZones() } }

    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        items(zones) { z ->
            Surface(shape = RoundedCornerShape(16.dp), tonalElevation = 1.dp, modifier = Modifier.fillMaxWidth()) {
                Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Place, null)
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Text(z.name, fontWeight = FontWeight.SemiBold)
                        Text(I18n.freshness(z.lastUpdatedAt),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    val on = z.powerStatus == "on"
                    val off = z.powerStatus == "off"
                    Surface(
                        color = if (on) Brand.PowerOn else if (off) Brand.PowerOff else MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(t(z.powerStatus),
                            color = if (on || off) androidx.compose.ui.graphics.Color.White else MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
                    }
                }
            }
        }
    }
}
