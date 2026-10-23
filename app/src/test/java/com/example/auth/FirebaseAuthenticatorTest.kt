package com.example.auth

import com.example.base.FirestoreEmulatorTestBase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.UUID

class FirebaseAuthenticatorTest : FirestoreEmulatorTestBase() {

    @Test
    fun authenticator_initialState_unauthenticated() {
        auth.signOut()
        val authenticator = FirebaseAuthenticator(auth)

        assertNull(authenticator.currentUser)
        assertNull(authenticator.currentUserId)
        assertNull(authenticator.currentUserEmail)
        assertFalse(authenticator.isSignedIn)
        assertNull(authenticator.getUserProfile())
    }

    @Test
    fun authenticator_afterSignIn_reflectsUserData() = runTest {
        val testEmail = "testuser_${UUID.randomUUID().toString().take(8)}@example.com"
        val uid = signInTestUser(testEmail)

        val authenticator = FirebaseAuthenticator(auth)

        assertTrue(authenticator.isSignedIn)
        assertEquals(uid, authenticator.currentUserId)
        assertEquals(testEmail, authenticator.currentUserEmail)

        val profile = authenticator.getUserProfile()
        assertNotNull(profile)
        assertEquals(uid, profile?.uid)
        assertEquals(testEmail, profile?.email)
    }

    @Test
    fun authenticator_signOut_clearsSession() = runTest {
        val testEmail = "signout_${UUID.randomUUID().toString().take(8)}@example.com"
        signInTestUser(testEmail)

        val authenticator = FirebaseAuthenticator(auth)
        assertTrue(authenticator.isSignedIn)

        val result = authenticator.signOut()
        assertTrue(result.isSuccess)
        assertFalse(authenticator.isSignedIn)
        assertNull(authenticator.currentUser)
    }

    @Test
    fun authenticator_authStateFlow_emitsCurrentUser() = runTest {
        val testEmail = "flow_${UUID.randomUUID().toString().take(8)}@example.com"
        val uid = signInTestUser(testEmail)

        val authenticator = FirebaseAuthenticator(auth)
        val emittedUser = authenticator.authStateFlow.first()

        assertNotNull(emittedUser)
        assertEquals(uid, emittedUser?.uid)
    }
}
