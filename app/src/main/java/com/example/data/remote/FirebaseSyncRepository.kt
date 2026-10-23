package com.example.data.remote

import android.content.Context
import com.example.R
import com.example.data.model.MessageEntity
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FirebaseSyncRepository(
    private val dbProvider: () -> FirebaseFirestore
) {

    constructor(db: FirebaseFirestore) : this({ db })

    constructor(context: Context) : this({
        val appContext = context.applicationContext
        if (FirebaseApp.getApps(appContext).isEmpty()) {
            FirebaseApp.initializeApp(appContext)
        }
        val databaseId = appContext.getString(R.string.firestore_database_id)
        FirebaseFirestore.getInstance(databaseId)
    })

    private val db: FirebaseFirestore
        get() = dbProvider()

    private val auth get() = Firebase.auth

    fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in with Google before accessing Firestore.")
    }

    fun isUserSignedIn(): Boolean = auth.currentUser != null

    suspend fun saveUserProfile(displayName: String, email: String) = withContext(Dispatchers.IO) {
        val uid = requireUserId()
        val path = "users/$uid"
        val userRef = db.collection("users").document(uid)

        val data = mapOf(
            "userId" to uid,
            "displayName" to displayName,
            "email" to email,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )

        try {
            userRef.set(data).await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, path)
            throw e
        }
    }

    suspend fun syncMessage(message: MessageEntity): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val uid = requireUserId()
            val msgId = "sms_${message.id}_${message.timestamp}"
            val path = "users/$uid/messages/$msgId"
            val msgRef = db.collection("users").document(uid).collection("messages").document(msgId)

            val payload = mapOf(
                "id" to msgId,
                "threadAddress" to message.threadAddress,
                "senderAddress" to message.senderAddress,
                "recipientAddress" to message.recipientAddress,
                "body" to message.body,
                "isIncoming" to message.isIncoming,
                "category" to message.category,
                "createdAt" to FieldValue.serverTimestamp()
            )

            msgRef.set(payload).await()
            Result.success(Unit)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, "users/me/messages")
            Result.failure(e)
        }
    }

    fun observeCloudMessages(): Flow<List<CloudMessage>> = flow {
        val uid = requireUserId()
        val path = "users/$uid/messages"
        emitAll(
            db.collection("users").document(uid).collection("messages")
                .snapshots()
                .map { snapshot -> snapshot.toObjects(CloudMessage::class.java) }
                .catch { error ->
                    if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
                    throw error
                }
        )
    }

    suspend fun deleteCloudMessage(messageId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val uid = requireUserId()
            val path = "users/$uid/messages/$messageId"
            db.collection("users").document(uid).collection("messages").document(messageId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, "users/me/messages/$messageId")
            Result.failure(e)
        }
    }
}
