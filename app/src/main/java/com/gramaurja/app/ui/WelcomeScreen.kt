package com.gramaurja.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun WelcomeScreen(onGetStarted: () -> Unit) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFDF8E1)) // Matches Screenshot 2026-04-27 105036.jpg
            .verticalScroll(scrollState)
            .padding(24.dp)
    ) {
        Spacer(modifier = Modifier.height(60.dp))

        Text(
            text = "Know when the power is back — without leaving home.",
            style = MaterialTheme.typography.displayMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D2417),
                lineHeight = 44.sp
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Grama-Urja shares real-time electricity status across your village. Farmers update once, everyone benefits.",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.DarkGray
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onGetStarted,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B5E20)),
            shape = RoundedCornerShape(50),
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("Get started — it's free", color = Color.White, fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.height(48.dp))

        // Feature items
        FeatureItem(Icons.Default.People, "Community-powered", "Anyone in the village can report status.")
        FeatureItem(Icons.Default.NotificationsActive, "Instant alerts", "Get notified the moment electricity returns.")
        FeatureItem(Icons.Default.Timer, "Smart pump timer", "Built-in timers tuned to your crop type.")
    }
}

@Composable
fun FeatureItem(icon: ImageVector, title: String, desc: String) {
    Row(modifier = Modifier.padding(vertical = 12.dp)) {
        Icon(icon, contentDescription = null, tint = Color(0xFF1B5E20), modifier = Modifier.size(28.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(text = desc, color = Color.Gray, fontSize = 14.sp)
        }
    }
}