package com.example.data.remote

import com.google.firebase.Timestamp
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class CloudMessage(
    val id: String = "",
    val threadAddress: String = "",
    val senderAddress: String = "",
    val recipientAddress: String = "",
    val body: String = "",
    @get:PropertyName("isIncoming")
    @set:PropertyName("isIncoming")
    var isIncoming: Boolean = false,
    val category: String = "PERSONAL",
    @ServerTimestamp val createdAt: Timestamp? = null
)

@IgnoreExtraProperties
data class CloudConversation(
    val address: String = "",
    val contactName: String? = null,
    val snippet: String = "",
    val category: String = "PERSONAL",
    val unreadCount: Int = 0,
    @get:PropertyName("isPinned")
    @set:PropertyName("isPinned")
    var isPinned: Boolean = false,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class CloudUserProfile(
    val userId: String = "",
    val displayName: String = "",
    val email: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)
