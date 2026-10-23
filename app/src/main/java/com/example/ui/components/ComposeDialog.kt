package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SimCard
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.QuickTemplatesList

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ComposeDialog(
    language: String,
    initialRecipient: String = "",
    onSendMessage: (recipient: String, body: String, simSlot: Int) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var recipient by remember { mutableStateOf(initialRecipient) }
    var messageText by remember { mutableStateOf("") }
    var selectedSim by remember { mutableIntStateOf(0) }

    // SMS length calculations
    val charCount = messageText.length
    val smsParts = if (charCount <= 160) 1 else (charCount + 152) / 153

    val quickContacts = listOf(
        Pair("তানভীর", "+8801711223344"),
        Pair("Nusrat", "+8801822334455"),
        Pair("রফিক ভাই", "+8801933445566"),
        Pair("Customer Care", "121")
    )

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
                Text(
                    text = if (language == "BN") "নতুন মেসেজ লিখুন" else "Compose New SMS",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Quick Contact chips
            Text(
                text = if (language == "BN") "দ্রুত পরিচিতি নির্বাচন" else "Quick Recipient Selection",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                quickContacts.forEach { (name, phone) ->
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.clickable { recipient = phone }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Contacts,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "$name ($phone)", fontSize = 12.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Recipient field
            OutlinedTextField(
                value = recipient,
                onValueChange = { recipient = it },
                label = { Text(if (language == "BN") "প্রাপক (ফোন নাম্বার বা নাম)" else "To (Phone Number or Name)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("compose_recipient_input"),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // SIM Slot selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = if (language == "BN") "সিম নির্বাচন:" else "SIM Slot:",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )

                FilterChip(
                    selected = selectedSim == 0,
                    onClick = { selectedSim = 0 },
                    label = { Text("SIM 1 (GP/Primary)") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.SimCard, contentDescription = null, modifier = Modifier.size(16.dp))
                    },
                    modifier = Modifier.testTag("sim_1_chip")
                )

                FilterChip(
                    selected = selectedSim == 1,
                    onClick = { selectedSim = 1 },
                    label = { Text("SIM 2") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.SimCard, contentDescription = null, modifier = Modifier.size(16.dp))
                    },
                    modifier = Modifier.testTag("sim_2_chip")
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Message Body
            OutlinedTextField(
                value = messageText,
                onValueChange = { messageText = it },
                label = { Text(if (language == "BN") "এসএমএস মেসেজ" else "SMS Message") },
                placeholder = { Text(if (language == "BN") "এখানে লিখুন..." else "Type message...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("compose_message_input"),
                minLines = 4,
                maxLines = 7,
                shape = RoundedCornerShape(12.dp)
            )

            // Segment / Character counter
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "$charCount chars · $smsParts SMS",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = if (language == "BN") "টেলিফোনি স্ট্যান্ডার্ড" else "Standard Telephony",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Quick templates
            Text(
                text = if (language == "BN") "দ্রুত টেমপ্লেট যোগ করুন" else "Add Quick Template",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                QuickTemplatesList.defaultTemplates.forEach { template ->
                    val text = if (language == "BN") template.textBn else template.textEn
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.clickable {
                            messageText = if (messageText.isBlank()) text else "$messageText $text"
                        }
                    ) {
                        Text(
                            text = text,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Send Action Button
            Button(
                onClick = {
                    if (recipient.isNotBlank() && messageText.isNotBlank()) {
                        onSendMessage(recipient, messageText, selectedSim)
                        onDismiss()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("send_sms_button"),
                enabled = recipient.isNotBlank() && messageText.isNotBlank(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = if (language == "BN") "এসএমএস পাঠান (SIM ${selectedSim + 1})" else "Send SMS (SIM ${selectedSim + 1})",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
