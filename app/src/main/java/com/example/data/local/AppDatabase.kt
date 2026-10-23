package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.ConversationEntity
import com.example.data.model.MessageEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [MessageEntity::class, ConversationEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun messageDao(): MessageDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "pulse_sms_database"
                )
                    .addCallback(DatabaseCallback())
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // Seed initial sample data asynchronously
                CoroutineScope(Dispatchers.IO).launch {
                    INSTANCE?.let { database ->
                        populateInitialData(database.messageDao())
                    }
                }
            }

            private suspend fun populateInitialData(dao: MessageDao) {
                val now = System.currentTimeMillis()

                val conversations = listOf(
                    ConversationEntity(
                        address = "+8801711223344",
                        contactName = "তানভীর আহমেদ (Tanvir)",
                        snippet = "কাল বিকেল ৫টায় কফি শপে দেখা হচ্ছে তো?",
                        timestamp = now - 1000 * 60 * 15, // 15 mins ago
                        unreadCount = 1,
                        category = "PERSONAL",
                        isPinned = true,
                        colorSeed = 1
                    ),
                    ConversationEntity(
                        address = "Google",
                        contactName = "Google Security",
                        snippet = "849201 is your Google verification code.",
                        timestamp = now - 1000 * 60 * 45, // 45 mins ago
                        unreadCount = 1,
                        category = "OTP",
                        isPinned = false,
                        colorSeed = 2
                    ),
                    ConversationEntity(
                        address = "bKash",
                        contactName = "bKash Transaction",
                        snippet = "Cash In Tk 2,500.00 from 01712345678 successful. Balance Tk 14,820.50.",
                        timestamp = now - 1000 * 60 * 120, // 2 hrs ago
                        unreadCount = 0,
                        category = "TRANSACTION",
                        isPinned = false,
                        colorSeed = 3
                    ),
                    ConversationEntity(
                        address = "+8801822334455",
                        contactName = "Nusrat Jahan",
                        snippet = "Documents are sent to your email. Check it out!",
                        timestamp = now - 1000 * 60 * 60 * 8, // 8 hrs ago
                        unreadCount = 0,
                        category = "PERSONAL",
                        isPinned = false,
                        colorSeed = 4
                    ),
                    ConversationEntity(
                        address = "GP-Offers",
                        contactName = "Grameenphone Offers",
                        snippet = "Special 5GB Internet pack at only 108 Tk for 7 days! Dial *121*5050#",
                        timestamp = now - 1000 * 60 * 60 * 24, // 1 day ago
                        unreadCount = 0,
                        category = "PROMOTION",
                        isPinned = false,
                        colorSeed = 5
                    )
                )

                dao.upsertConversations(conversations)

                val messages = listOf(
                    // Thread 1: Tanvir
                    MessageEntity(
                        threadAddress = "+8801711223344",
                        senderAddress = "Me",
                        recipientAddress = "+8801711223344",
                        body = "শুভ অপরাহ্ন! প্রজেক্টের কাজ কতদূর?",
                        timestamp = now - 1000 * 60 * 25,
                        isIncoming = false,
                        status = "DELIVERED",
                        category = "PERSONAL",
                        isRead = true
                    ),
                    MessageEntity(
                        threadAddress = "+8801711223344",
                        senderAddress = "+8801711223344",
                        recipientAddress = "Me",
                        body = "কাল বিকেল ৫টায় কফি শপে দেখা হচ্ছে তো?",
                        timestamp = now - 1000 * 60 * 15,
                        isIncoming = true,
                        status = "RECEIVED",
                        category = "PERSONAL",
                        isRead = false
                    ),

                    // Thread 2: Google OTP
                    MessageEntity(
                        threadAddress = "Google",
                        senderAddress = "Google",
                        recipientAddress = "Me",
                        body = "849201 is your Google verification code. Do not share this code with anyone.",
                        timestamp = now - 1000 * 60 * 45,
                        isIncoming = true,
                        status = "RECEIVED",
                        category = "OTP",
                        isRead = false,
                        otpCode = "849201"
                    ),

                    // Thread 3: bKash Transaction
                    MessageEntity(
                        threadAddress = "bKash",
                        senderAddress = "bKash",
                        recipientAddress = "Me",
                        body = "Cash In Tk 2,500.00 from 01712345678 successful. Fee Tk 0.00. Balance Tk 14,820.50. TrxID 9H87G65F.",
                        timestamp = now - 1000 * 60 * 120,
                        isIncoming = true,
                        status = "RECEIVED",
                        category = "TRANSACTION",
                        isRead = true
                    ),

                    // Thread 4: Nusrat
                    MessageEntity(
                        threadAddress = "+8801822334455",
                        senderAddress = "+8801822334455",
                        recipientAddress = "Me",
                        body = "Hi, have you reviewed the mobile design mockups?",
                        timestamp = now - 1000 * 60 * 60 * 9,
                        isIncoming = true,
                        status = "RECEIVED",
                        category = "PERSONAL",
                        isRead = true
                    ),
                    MessageEntity(
                        threadAddress = "+8801822334455",
                        senderAddress = "Me",
                        recipientAddress = "+8801822334455",
                        body = "Yes, looks sleek and responsive! Can you send the specs?",
                        timestamp = now - 1000 * 60 * 60 * 8 - 1000 * 60 * 30,
                        isIncoming = false,
                        status = "DELIVERED",
                        category = "PERSONAL",
                        isRead = true
                    ),
                    MessageEntity(
                        threadAddress = "+8801822334455",
                        senderAddress = "+8801822334455",
                        recipientAddress = "Me",
                        body = "Documents are sent to your email. Check it out!",
                        timestamp = now - 1000 * 60 * 60 * 8,
                        isIncoming = true,
                        status = "RECEIVED",
                        category = "PERSONAL",
                        isRead = true
                    ),

                    // Thread 5: GP Offers
                    MessageEntity(
                        threadAddress = "GP-Offers",
                        senderAddress = "GP-Offers",
                        recipientAddress = "Me",
                        body = "Special 5GB Internet pack at only 108 Tk for 7 days! Dial *121*5050# to activate now. Valid till midnight.",
                        timestamp = now - 1000 * 60 * 60 * 24,
                        isIncoming = true,
                        status = "RECEIVED",
                        category = "PROMOTION",
                        isRead = true
                    )
                )

                dao.insertMessages(messages)
            }
        }
    }
}
