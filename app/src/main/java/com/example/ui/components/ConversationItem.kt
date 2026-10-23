package com.example.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ConversationEntity
import com.example.ui.theme.CategoryOtpBgLight
import com.example.ui.theme.CategoryOtpColor
import com.example.ui.theme.CategoryPersonalBgLight
import com.example.ui.theme.CategoryPersonalColor
import com.example.ui.theme.CategoryPromoBgLight
import com.example.ui.theme.CategoryPromoColor
import com.example.ui.theme.CategoryTxBgLight
import com.example.ui.theme.CategoryTxColor
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ConversationItem(
    conversation: ConversationEntity,
    language: String,
    onClick: () -> Unit,
    onTogglePin: () -> Unit,
    onToggleArchive: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }
    val isUnread = conversation.unreadCount > 0

    val displayName = conversation.contactName ?: conversation.address

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("conversation_card_${conversation.address}")
            .combinedClickable(
                onClick = onClick,
                onLongClick = { showMenu = true }
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isUnread) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.28f)
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isUnread) 2.dp else 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SmsAvatar(
                nameOrAddress = displayName,
                category = conversation.category,
                colorSeed = conversation.colorSeed,
                size = 50.dp
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = displayName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = if (isUnread) FontWeight.Bold else FontWeight.SemiBold
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (conversation.isPinned) {
                            Icon(
                                imageVector = Icons.Default.PushPin,
                                contentDescription = "Pinned",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .size(16.dp)
                                    .padding(end = 4.dp)
                            )
                        }

                        Text(
                            text = formatTimestamp(conversation.timestamp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (isUnread) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            fontWeight = if (isUnread) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = conversation.snippet.ifEmpty { "No messages" },
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = if (isUnread) FontWeight.Medium else FontWeight.Normal,
                            color = if (isUnread) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    if (conversation.category != "PERSONAL") {
                        CategoryPill(category = conversation.category, language = language)
                        Spacer(modifier = Modifier.width(6.dp))
                    }

                    if (isUnread) {
                        Badge(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ) {
                            Text(
                                text = "${conversation.unreadCount}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Box {
                IconButton(
                    onClick = { showMenu = true },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Options",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                if (conversation.isPinned) {
                                    if (language == "BN") "আনপিন করুন" else "Unpin"
                                } else {
                                    if (language == "BN") "পিন করে রাখুন" else "Pin to top"
                                }
                            )
                        },
                        leadingIcon = {
                            Icon(
                                if (conversation.isPinned) Icons.Outlined.PushPin else Icons.Default.PushPin,
                                contentDescription = null
                            )
                        },
                        onClick = {
                            showMenu = false
                            onTogglePin()
                        }
                    )

                    DropdownMenuItem(
                        text = {
                            Text(
                                if (conversation.isArchived) {
                                    if (language == "BN") "আন-আর্কাইভ" else "Unarchive"
                                } else {
                                    if (language == "BN") "আর্কাইভ করুন" else "Archive"
                                }
                            )
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Archive, contentDescription = null)
                        },
                        onClick = {
                            showMenu = false
                            onToggleArchive()
                        }
                    )

                    DropdownMenuItem(
                        text = {
                            Text(
                                if (language == "BN") "ডিলিট করুন" else "Delete",
                                color = MaterialTheme.colorScheme.error
                            )
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                        },
                        onClick = {
                            showMenu = false
                            onDelete()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryPill(category: String, language: String, modifier: Modifier = Modifier) {
    val (label, fgColor, bgColor) = when (category) {
        "OTP" -> Triple(if (language == "BN") "ওটিপি" else "OTP", CategoryOtpColor, CategoryOtpBgLight)
        "TRANSACTION" -> Triple(if (language == "BN") "লেনদেন" else "Bank", CategoryTxColor, CategoryTxBgLight)
        "PROMOTION" -> Triple(if (language == "BN") "অফার" else "Promo", CategoryPromoColor, CategoryPromoBgLight)
        else -> Triple(if (language == "BN") "ব্যক্তিগত" else "Personal", CategoryPersonalColor, CategoryPersonalBgLight)
    }

    Surface(
        color = bgColor,
        shape = CircleShape,
        modifier = modifier
    ) {
        Text(
            text = label,
            color = fgColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
        )
    }
}

private fun formatTimestamp(time: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - time
    return when {
        diff < 60 * 1000 -> "Just now"
        diff < 60 * 60 * 1000 -> "${diff / (60 * 1000)}m"
        diff < 24 * 60 * 60 * 1000 -> {
            val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
            sdf.format(Date(time))
        }
        diff < 7 * 24 * 60 * 60 * 1000 -> {
            val sdf = SimpleDateFormat("EEE", Locale.getDefault())
            sdf.format(Date(time))
        }
        else -> {
            val sdf = SimpleDateFormat("MMM d", Locale.getDefault())
            sdf.format(Date(time))
        }
    }
}
