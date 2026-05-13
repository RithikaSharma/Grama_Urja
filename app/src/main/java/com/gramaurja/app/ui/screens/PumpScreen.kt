package com.gramaurja.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gramaurja.app.data.model.Crop
import com.gramaurja.app.data.repo.Repo
import com.gramaurja.app.util.I18n.t
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PumpScreen() {
    var crops by remember { mutableStateOf<List<Crop>>(emptyList()) }
    var selected by remember { mutableStateOf<Crop?>(null) }
    var expanded by remember { mutableStateOf(false) }
    var remainingSec by remember { mutableIntStateOf(0) }
    var running by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        runCatching { crops = Repo.listCrops() }
            .onSuccess { if (selected == null) selected = crops.firstOrNull() }
    }
    LaunchedEffect(running) {
        while (running && isActive && remainingSec > 0) { delay(1000); remainingSec-- }
        if (remainingSec == 0) running = false
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text(t("pump"), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(16.dp))

        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
            OutlinedTextField(
                value = selected?.name ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text(t("cropType")) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                modifier = Modifier.fillMaxWidth().menuAnchor()
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                crops.forEach { c ->
                    DropdownMenuItem(text = { Text("${c.name}  •  ${c.recommendedMinutes} ${t("minutes")}") },
                        onClick = { selected = c; expanded = false })
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        Surface(shape = RoundedCornerShape(28.dp), tonalElevation = 2.dp, modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                val mm = remainingSec / 60
                val ss = remainingSec % 60
                Text(String.format("%02d:%02d", mm, ss), fontSize = 72.sp, fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.primary)
                selected?.let {
                    Text("${t("recommended")}: ${it.recommendedMinutes} ${t("minutes")}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        Spacer(Modifier.height(16.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = {
                    val mins = selected?.recommendedMinutes ?: 0
                    if (!running) { remainingSec = mins * 60; running = true }
                },
                modifier = Modifier.weight(1f).height(56.dp),
                enabled = selected != null && !running
            ) { Text(t("startTimer"), fontWeight = FontWeight.Bold) }
            OutlinedButton(
                onClick = { running = false },
                modifier = Modifier.weight(1f).height(56.dp)
            ) { Text(t("stopTimer")) }
            OutlinedButton(
                onClick = { running = false; remainingSec = 0 },
                modifier = Modifier.weight(1f).height(56.dp)
            ) { Text(t("resetTimer")) }
        }
    }
}
