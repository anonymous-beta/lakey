package com.anonymousbeta.lakey.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anonymousbeta.lakey.ui.theme.*

@Composable
fun PanelScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BlackDeep),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("PANEL", color = Gold, fontSize = 28.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                "Infected devices will appear here\nonce C2 is connected",
                color = TextSecondary,
                fontSize = 14.sp
            )
        }
    }
}