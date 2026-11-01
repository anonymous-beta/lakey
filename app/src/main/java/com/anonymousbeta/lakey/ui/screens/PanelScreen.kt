package com.anonymousbeta.lakey.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anonymousbeta.lakey.ui.theme.BlackDeep
import com.anonymousbeta.lakey.ui.theme.Gold
import com.anonymousbeta.lakey.ui.theme.TextSecondary

@Composable
fun PanelScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BlackDeep),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "PANEL",
                color = Gold,
                fontSize = 28.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Infected devices will appear here\nonce C2 is connected",
                color = TextSecondary,
                fontSize = 14.sp
            )
        }
    }
}