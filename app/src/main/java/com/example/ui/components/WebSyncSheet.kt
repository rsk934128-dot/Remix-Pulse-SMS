package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cable
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.WebSyncEvent
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebSyncSheet(
    isConnected: Boolean,
    syncLogs: List<WebSyncEvent>,
    language: String,
    onToggleConnection: () -> Unit,
    onSimulateIncoming: (sender: String, body: String) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var customSender by remember { mutableStateOf("+8801911998877") }
    var customBody by remember { mutableStateOf("হ্যালো! কেমন আছেন? এটি একটি লাইভ টেস্ট এসএমএস।") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Cable,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (language == "BN") "রিয়েল-টাইম ওয়েব সিঙ্ক ও সিমুলেটর" else "Real-time Web Sync & Simulator",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Socket.io Bridge · Telephony Receiver",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Socket connection status card
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isConnected) Color(0xFFECFDF5) else Color(0xFFFEF2F2)
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(if (isConnected) Color(0xFF10B981) else Color(0xFFEF4444))
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (isConnected) {
                                    if (language == "BN") "ওয়েব সিঙ্ক সক্রিয় (Active)" else "Web Sync Active (Connected)"
                                } else {
                                    if (language == "BN") "ওয়েব সিঙ্ক সংযোগ বিচ্ছিন্ন" else "Web Sync Disconnected"
                                },
                                fontWeight = FontWeight.Bold,
                                color = if (isConnected) Color(0xFF065F46) else Color(0xFF991B1B),
                                fontSize = 14.sp
                            )
                            Text(
                                text = if (isConnected) "ws://localhost:5000/realtime · Syncing 2-way" else "Server offline",
                                fontSize = 12.sp,
                                color = if (isConnected) Color(0xFF047857) else Color(0xFFB91C1C)
                            )
                        }
                    }

                    Switch(
                        checked = isConnected,
                        onCheckedChange = { onToggleConnection() },
                        modifier = Modifier.testTag("toggle_web_sync_switch")
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Preset fast simulators
            Text(
                text = if (language == "BN") "দ্রুত ইনকামিং এসএমএস টেস্ট (Presets)" else "Quick Incoming SMS Simulation",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        val code = (100000..999999).random()
                        onSimulateIncoming(
                            "Google",
                            "$code is your Google Verification Code. Do not share this with anyone."
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("simulate_otp_button"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("OTP Code", fontSize = 12.sp)
                }

                OutlinedButton(
                    onClick = {
                        val amount = listOf("1,500.00", "3,200.00", "5,000.00").random()
                        onSimulateIncoming(
                            "bKash",
                            "Cash In Tk $amount successful from 01799887766. Fee Tk 0.00. Balance Tk 18,340.00. TrxID ${(10000000..99999999).random()}."
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("simulate_bank_button"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("bKash/Bank", fontSize = 12.sp)
                }

                OutlinedButton(
                    onClick = {
                        onSimulateIncoming(
                            "+8801711223344",
                            "কিরে দোস্ত! অফিস শেষ করে আড্ডা দিবি নাকি? জানা।"
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("simulate_personal_button"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Friend (বাং)", fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Custom incoming test
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = if (language == "BN") "কাস্টম ইনকামিং এসএমএস পাঠান" else "Custom Incoming SMS Dispatcher",
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = customSender,
                        onValueChange = { customSender = it },
                        label = { Text(if (language == "BN") "প্রেরক (নাম বা নাম্বার)" else "Sender (Name or Number)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("custom_sender_input"),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = customBody,
                        onValueChange = { customBody = it },
                        label = { Text(if (language == "BN") "মেসেজের বিবরণ" else "Message Body") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("custom_body_input"),
                        minLines = 2,
                        maxLines = 3
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            if (customSender.isNotBlank() && customBody.isNotBlank()) {
                                onSimulateIncoming(customSender, customBody)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("dispatch_custom_sms_button"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Sensors, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (language == "BN") "এখনই ইনকামিং এসএমএস ট্রিগার করুন" else "Trigger Incoming SMS Now")
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Live event log
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (language == "BN") "রিয়েল-টাইম লাইভ ইভেন্ট লগ" else "Live Real-time Event Stream",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = "${syncLogs.size} events",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 200.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF0F172A))
                    .padding(10.dp)
            ) {
                if (syncLogs.isEmpty()) {
                    Text(
                        text = "Listening for telephony and websocket events...",
                        color = Color(0xFF64748B),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(8.dp)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(syncLogs) { log ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.Top
                            ) {
                                val tagColor = when (log.type) {
                                    "INCOMING" -> Color(0xFF10B981)
                                    "OUTGOING" -> Color(0xFF3B82F6)
                                    "WEB_SOCKET" -> Color(0xFFA855F7)
                                    else -> Color(0xFFF59E0B)
                                }
                                Text(
                                    text = "[${log.type}]",
                                    color = tagColor,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.width(90.dp)
                                )
                                Text(
                                    text = log.description,
                                    color = Color(0xFFE2E8F0),
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
