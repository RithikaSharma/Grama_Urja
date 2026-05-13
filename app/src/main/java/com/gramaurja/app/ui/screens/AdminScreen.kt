package com.gramaurja.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gramaurja.app.data.model.Crop
import com.gramaurja.app.data.model.Zone
import com.gramaurja.app.data.repo.Repo
import com.gramaurja.app.util.I18n.t
import kotlinx.coroutines.launch

@Composable
fun AdminScreen() {
    val scope = rememberCoroutineScope()
    var zones by remember { mutableStateOf<List<Zone>>(emptyList()) }
    var crops by remember { mutableStateOf<List<Crop>>(emptyList()) }
    var zName by remember { mutableStateOf("") }
    var zDesc by remember { mutableStateOf("") }
    var cName by remember { mutableStateOf("") }
    var cMin by remember { mutableStateOf("30") }
    var msg by remember { mutableStateOf<String?>(null) }

    suspend fun reload() {
        zones = runCatching { Repo.listZones() }.getOrDefault(emptyList())
        crops = runCatching { Repo.listCrops() }.getOrDefault(emptyList())
    }
    LaunchedEffect(Unit) { reload() }

    LazyColumn(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            Text(t("admin"), style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(8.dp))
            Text(t("addZone"), style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(zName, { zName = it }, label = { Text(t("zoneName")) }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(zDesc, { zDesc = it }, label = { Text(t("description")) }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(6.dp))
            Button(onClick = {
                scope.launch {
                    runCatching { Repo.addZone(zName.trim(), zDesc.ifBlank { null }); zName = ""; zDesc = ""; reload() }
                        .onFailure { msg = it.message }
                }
            }, enabled = zName.isNotBlank()) { Text(t("addZone")) }
            Spacer(Modifier.height(16.dp))
        }
        items(zones) { z ->
            Surface(shape = RoundedCornerShape(12.dp), tonalElevation = 1.dp, modifier = Modifier.fillMaxWidth()) {
                Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(z.name)
                        z.description?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
                    }
                    IconButton(onClick = {
                        scope.launch { runCatching { Repo.deleteZone(z.id); reload() }.onFailure { msg = it.message } }
                    }) { Icon(Icons.Filled.Delete, t("delete")) }
                }
            }
        }
        item {
            Spacer(Modifier.height(20.dp))
            Text("Crops", style = MaterialTheme.typography.titleMedium)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                OutlinedTextField(cName, { cName = it }, label = { Text("Crop name") }, modifier = Modifier.weight(1f))
                OutlinedTextField(cMin, { cMin = it.filter { c -> c.isDigit() } }, label = { Text("Min") }, modifier = Modifier.width(80.dp))
            }
            Spacer(Modifier.height(6.dp))
            Button(onClick = {
                scope.launch {
                    runCatching { Repo.addCrop(cName.trim(), cMin.toIntOrNull() ?: 30); cName = ""; cMin = "30"; reload() }
                        .onFailure { msg = it.message }
                }
            }, enabled = cName.isNotBlank()) { Text("Add crop") }
        }
        items(crops) { c ->
            Surface(shape = RoundedCornerShape(12.dp), tonalElevation = 1.dp, modifier = Modifier.fillMaxWidth()) {
                Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(c.name)
                        Text("${c.recommendedMinutes} ${t("minutes")}", style = MaterialTheme.typography.bodySmall)
                    }
                    IconButton(onClick = {
                        scope.launch { runCatching { Repo.deleteCrop(c.id); reload() }.onFailure { msg = it.message } }
                    }) { Icon(Icons.Filled.Delete, t("delete")) }
                }
            }
        }
        msg?.let { item { Text(it, color = MaterialTheme.colorScheme.error) } }
    }
}
