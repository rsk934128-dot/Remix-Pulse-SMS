package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "conversations")
data class ConversationEntity(
    @PrimaryKey
    val address: String,
    val contactName: String? = null,
    val snippet: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val unreadCount: Int = 0,
    val category: String = "PERSONAL", // PERSONAL, OTP, TRANSACTION, PROMOTION, SPAM
    val isPinned: Boolean = false,
    val isArchived: Boolean = false,
    val isBlocked: Boolean = false,
    val colorSeed: Int = 0
)
