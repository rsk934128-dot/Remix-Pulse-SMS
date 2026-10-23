package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ConversationEntity
import com.example.data.model.MessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {

    @Query("SELECT * FROM conversations WHERE isArchived = 0 AND isBlocked = 0 ORDER BY isPinned DESC, timestamp DESC")
    fun getActiveConversationsFlow(): Flow<List<ConversationEntity>>

    @Query("SELECT * FROM conversations WHERE isArchived = 0 AND isBlocked = 0 AND category = :category ORDER BY isPinned DESC, timestamp DESC")
    fun getConversationsByCategoryFlow(category: String): Flow<List<ConversationEntity>>

    @Query("SELECT * FROM conversations WHERE isArchived = 1 ORDER BY timestamp DESC")
    fun getArchivedConversationsFlow(): Flow<List<ConversationEntity>>

    @Query("SELECT * FROM conversations WHERE isBlocked = 1 ORDER BY timestamp DESC")
    fun getBlockedConversationsFlow(): Flow<List<ConversationEntity>>

    @Query("SELECT * FROM conversations WHERE address = :address LIMIT 1")
    suspend fun getConversation(address: String): ConversationEntity?

    @Query("SELECT * FROM conversations WHERE address = :address LIMIT 1")
    fun getConversationFlow(address: String): Flow<ConversationEntity?>

    @Query("SELECT * FROM messages WHERE threadAddress = :threadAddress ORDER BY timestamp ASC")
    fun getMessagesForThreadFlow(threadAddress: String): Flow<List<MessageEntity>>

    @Query("SELECT * FROM messages WHERE body LIKE '%' || :query || '%' OR threadAddress LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    fun searchMessagesFlow(query: String): Flow<List<MessageEntity>>

    @Query("SELECT * FROM conversations WHERE address LIKE '%' || :query || '%' OR contactName LIKE '%' || :query || '%' OR snippet LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    fun searchConversationsFlow(query: String): Flow<List<ConversationEntity>>

    @Query("SELECT COUNT(*) FROM messages WHERE isRead = 0 AND isIncoming = 1")
    fun getTotalUnreadCountFlow(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<MessageEntity>)

    @Update
    suspend fun updateMessage(message: MessageEntity)

    @Query("DELETE FROM messages WHERE id = :id")
    suspend fun deleteMessage(id: Long)

    @Query("DELETE FROM messages WHERE threadAddress = :threadAddress")
    suspend fun deleteMessagesForThread(threadAddress: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertConversation(conversation: ConversationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertConversations(conversations: List<ConversationEntity>)

    @Query("UPDATE messages SET isRead = 1 WHERE threadAddress = :threadAddress")
    suspend fun markMessagesAsRead(threadAddress: String)

    @Query("UPDATE conversations SET unreadCount = 0 WHERE address = :threadAddress")
    suspend fun clearConversationUnreadCount(threadAddress: String)

    @Query("UPDATE conversations SET isPinned = :isPinned WHERE address = :address")
    suspend fun setPinned(address: String, isPinned: Boolean)

    @Query("UPDATE conversations SET isArchived = :isArchived WHERE address = :address")
    suspend fun setArchived(address: String, isArchived: Boolean)

    @Query("UPDATE conversations SET isBlocked = :isBlocked WHERE address = :address")
    suspend fun setBlocked(address: String, isBlocked: Boolean)

    @Query("DELETE FROM conversations WHERE address = :address")
    suspend fun deleteConversation(address: String)
}
