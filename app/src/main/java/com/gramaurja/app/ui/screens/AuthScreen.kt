package com.gramaurja.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.gramaurja.app.data.LocalPrefs
import com.gramaurja.app.data.repo.Repo
import com.gramaurja.app.util.I18n.t
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(onLangChange: () -> Unit) {
    var isSignUp by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var pwd by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var err by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    Column(
        Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(t("appName"), style = MaterialTheme.typography.displaySmall)
        Spacer(Modifier.height(4.dp))
        Text(t("tagline"), style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(24.dp))

        if (isSignUp) {
            OutlinedTextField(name, { name = it }, label = { Text(t("name")) }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
        }
        OutlinedTextField(email, { email = it }, label = { Text(t("email")) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(pwd, { pwd = it }, label = { Text(t("password")) },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(16.dp))

        err?.let { Text(it, color = MaterialTheme.colorScheme.error); Spacer(Modifier.height(8.dp)) }

        Button(
            onClick = {
                err = null; loading = true
                scope.launch {
                    runCatching {
                        if (isSignUp) Repo.signUp(email.trim(), pwd, name.ifBlank { email.substringBefore("@") })
                        else Repo.signIn(email.trim(), pwd)
                    }.onFailure { err = it.message }
                    loading = false
                }
            },
            enabled = !loading && email.isNotBlank() && pwd.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) { Text(if (isSignUp) t("signUp") else t("signIn")) }

        TextButton(onClick = { isSignUp = !isSignUp }) {
            Text(if (isSignUp) t("signIn") else t("signUp"))
        }

        Spacer(Modifier.height(24.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            listOf("en" to "EN", "hi" to "हि", "kn" to "ಕ").forEach { (code, label) ->
                FilterChip(
                    selected = LocalPrefs.lang == code,
                    onClick = { LocalPrefs.lang = code; onLangChange() },
                    label = { Text(label) }
                )
                Spacer(Modifier.width(6.dp))
            }
        }
    }
}
