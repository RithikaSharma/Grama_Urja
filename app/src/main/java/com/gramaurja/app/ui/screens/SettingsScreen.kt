package com.gramaurja.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gramaurja.app.data.LocalPrefs
import com.gramaurja.app.data.model.ProfileUpdate
import com.gramaurja.app.data.model.Zone
import com.gramaurja.app.data.repo.Repo
import com.gramaurja.app.util.I18n.t
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onLangChange: () -> Unit) {
    val scope = rememberCoroutineScope()
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var zones by remember { mutableStateOf<List<Zone>>(emptyList()) }
    var zoneId by remember { mutableStateOf<String?>(null) }
    var expanded by remember { mutableStateOf(false) }
    var msg by remember { mutableStateOf<String?>(null) }
    var lang by remember { mutableStateOf(LocalPrefs.lang) }

    LaunchedEffect(Unit) {
        val uid = Repo.currentUserId() ?: return@LaunchedEffect
        val p = Repo.getProfile(uid)
        name = p?.displayName.orEmpty(); phone = p?.phone.orEmpty(); zoneId = p?.zoneId
        zones = runCatching { Repo.listZones() }.getOrDefault(emptyList())
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text(t("settings"), style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(name, { name = it }, label = { Text(t("name")) }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(phone, { phone = it }, label = { Text("Phone") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))

        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
            OutlinedTextField(
                value = zones.firstOrNull { it.id == zoneId }?.name ?: t("selectZone"),
                onValueChange = {}, readOnly = true,
                label = { Text(t("myZone")) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                modifier = Modifier.fillMaxWidth().menuAnchor()
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                zones.forEach { z ->
                    DropdownMenuItem(text = { Text(z.name) },
                        onClick = { zoneId = z.id; expanded = false })
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        Text(t("language"))
        Row {
            listOf("en" to "English", "hi" to "हिन्दी", "kn" to "ಕನ್ನಡ").forEach { (code, label) ->
                FilterChip(selected = lang == code,
                    onClick = { lang = code; LocalPrefs.lang = code; onLangChange() },
                    label = { Text(label) },
                    modifier = Modifier.padding(end = 6.dp))
            }
        }

        Spacer(Modifier.height(20.dp))
        Button(
            onClick = {
                val uid = Repo.currentUserId() ?: return@Button
                scope.launch {
                    runCatching {
                        Repo.updateProfile(uid, ProfileUpdate(displayName = name, zoneId = zoneId, phone = phone, language = lang))
                        LocalPrefs.watchedZoneId = zoneId
                    }.onSuccess { msg = "Saved" }.onFailure { msg = it.message }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text(t("saveProfile")) }

        msg?.let { Spacer(Modifier.height(8.dp)); Text(it, color = MaterialTheme.colorScheme.primary) }

        Spacer(Modifier.height(24.dp))
        OutlinedButton(
            onClick = { scope.launch { Repo.signOut() } },
            modifier = Modifier.fillMaxWidth()
        ) { Text(t("signOut")) }
    }
}
