package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "messages",
    indices = [
        Index(value = ["threadAddress"]),
        Index(value = ["timestamp"]),
        Index(value = ["category"])
    ]
)
data class MessageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val threadAddress: String,
    val senderAddress: String,
    val recipientAddress: String,
    val body: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isIncoming: Boolean,
    val status: String = if (isIncoming) "RECEIVED" else "SENT",
    val category: String = "PERSONAL", // PERSONAL, OTP, TRANSACTION, PROMOTION, SPAM
    val isRead: Boolean = !isIncoming,
    val isStarred: Boolean = false,
    val simSlot: Int = 0, // 0 for SIM 1, 1 for SIM 2
    val otpCode: String? = null
)
