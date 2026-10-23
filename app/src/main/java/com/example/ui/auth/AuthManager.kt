package com.example.ui.auth

import android.content.Context
import androidx.credentials.CredentialManager
import com.example.auth.FirebaseAuthenticator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

object AuthManager {

    val authenticator: FirebaseAuthenticator
        get() = FirebaseAuthenticator.getInstance()

    fun attemptAutoSignIn(
        context: Context,
        credentialManager: CredentialManager,
        onAuthSuccess: () -> Unit,
        onUnauthenticated: () -> Unit,
        scope: CoroutineScope
    ) {
        scope.launch {
            val result = authenticator.attemptAutoSignIn(context, credentialManager)
            if (result.isSuccess && result.getOrNull() != null) {
                onAuthSuccess()
            } else {
                onUnauthenticated()
            }
        }
    }

    fun onGoogleSignInClicked(
        context: Context,
        credentialManager: CredentialManager,
        onAuthSuccess: () -> Unit,
        onAuthError: (String) -> Unit,
        scope: CoroutineScope,
        onAuthCancelled: () -> Unit = {}
    ) {
        scope.launch {
            val result = authenticator.signInWithGoogle(context, credentialManager)
            result.onSuccess {
                onAuthSuccess()
            }.onFailure { throwable ->
                if (throwable is androidx.credentials.exceptions.GetCredentialCancellationException) {
                    onAuthCancelled()
                } else {
                    onAuthError(throwable.localizedMessage ?: "Sign in failed")
                }
            }
        }
    }

    fun signOut(
        context: Context,
        credentialManager: CredentialManager,
        onSignOutComplete: () -> Unit,
        scope: CoroutineScope
    ) {
        scope.launch {
            authenticator.signOut(context, credentialManager)
            onSignOutComplete()
        }
    }
}

