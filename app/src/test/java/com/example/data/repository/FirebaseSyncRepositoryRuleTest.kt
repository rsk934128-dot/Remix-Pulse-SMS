package com.example.data.repository

import com.example.base.FirestoreEmulatorTestBase
import com.example.data.model.MessageEntity
import com.example.data.remote.FirebaseSyncRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.UUID

class FirebaseSyncRepositoryRuleTest : FirestoreEmulatorTestBase() {

    @Test
    fun syncMessage_authenticatedUser_succeeds() = runBlocking {
        val uniqueEmail = "alice_${UUID.randomUUID()}@test.com"
        signInTestUser(uniqueEmail)
        val repo = FirebaseSyncRepository(firestore)

        val message = MessageEntity(
            id = System.currentTimeMillis(),
            threadAddress = "+8801711223344",
            senderAddress = "Me",
            recipientAddress = "+8801711223344",
            body = "Hello Cloud Sync Test",
            timestamp = System.currentTimeMillis(),
            isIncoming = false,
            category = "PERSONAL"
        )

        val result = withTimeout(DEFAULT_TIMEOUT_MS) {
            repo.syncMessage(message)
        }
        assertTrue(result.isSuccess)
    }

    @Test
    fun observeCloudMessages_authenticatedUser_emitsSavedMessage() = runBlocking {
        val uniqueEmail = "bob_${UUID.randomUUID()}@test.com"
        signInTestUser(uniqueEmail)
        val repo = FirebaseSyncRepository(firestore)

        val uniqueBody = "Cloud Incoming SMS ${UUID.randomUUID()}"
        val message = MessageEntity(
            id = System.currentTimeMillis() + 1,
            threadAddress = "+8801822334455",
            senderAddress = "+8801822334455",
            recipientAddress = "Me",
            body = uniqueBody,
            timestamp = System.currentTimeMillis(),
            isIncoming = true,
            category = "PERSONAL"
        )

        repo.syncMessage(message)

        val messages = withTimeout(FLOW_TIMEOUT_MS) {
            repo.observeCloudMessages().first { list ->
                list.any { it.body == uniqueBody }
            }
        }
        assertNotNull(messages)
        assertTrue(messages.any { it.body == uniqueBody })
    }

    @Test
    fun syncMessage_unauthenticated_fails() = runBlocking {
        auth.signOut()
        val repo = FirebaseSyncRepository(firestore)

        val message = MessageEntity(
            id = System.currentTimeMillis() + 2,
            threadAddress = "+8801933445566",
            senderAddress = "Me",
            recipientAddress = "+8801933445566",
            body = "Should fail without auth",
            timestamp = System.currentTimeMillis(),
            isIncoming = false,
            category = "PERSONAL"
        )

        val result = repo.syncMessage(message)
        assertTrue(result.isFailure)
    }

    private companion object {
        const val DEFAULT_TIMEOUT_MS = 5000L
        const val FLOW_TIMEOUT_MS = 3000L
    }
}
