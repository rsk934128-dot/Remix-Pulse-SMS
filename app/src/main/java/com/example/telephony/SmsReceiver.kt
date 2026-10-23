package com.example.telephony

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import com.example.data.repository.SmsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SmsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action == Telephony.Sms.Intents.SMS_DELIVER_ACTION ||
            action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION
        ) {
            val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent) ?: return
            val repository = SmsRepository(context)

            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val groupedMessages = messages.groupBy { it.displayOriginatingAddress ?: it.originatingAddress ?: "Unknown" }
                    for ((sender, parts) in groupedMessages) {
                        val fullBody = parts.joinToString(separator = "") { it.messageBody ?: "" }
                        val timestamp = parts.firstOrNull()?.timestampMillis ?: System.currentTimeMillis()
                        repository.handleIncomingSms(sender, fullBody, timestamp)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}
