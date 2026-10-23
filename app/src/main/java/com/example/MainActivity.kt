package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.credentials.CredentialManager
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.SmsViewModel
import com.example.ui.auth.AuthManager
import com.example.ui.components.ComposeDialog
import com.example.ui.components.WebSyncSheet
import com.example.ui.screens.ChatThreadScreen
import com.example.ui.screens.InboxScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    SmsAppRoot()
                }
            }
        }
    }
}

@Composable
fun SmsAppRoot(viewModel: SmsViewModel = viewModel()) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val credentialManager = remember { CredentialManager.create(context) }

    val conversations by viewModel.conversations.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val totalUnreadCount by viewModel.totalUnreadCount.collectAsStateWithLifecycle()
    val isDefaultSms by viewModel.isDefaultSmsApp.collectAsStateWithLifecycle()
    val isWebConnected by viewModel.isWebConnected.collectAsStateWithLifecycle()
    val syncLogs by viewModel.syncLogs.collectAsStateWithLifecycle()
    val currentLanguage by viewModel.currentLanguage.collectAsStateWithLifecycle()

    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val isCloudSyncing by viewModel.isCloudSyncing.collectAsStateWithLifecycle()

    val currentThreadAddress by viewModel.currentThreadAddress.collectAsStateWithLifecycle()
    val activeThreadMessages by viewModel.activeThreadMessages.collectAsStateWithLifecycle()
    val currentConversation by viewModel.currentConversation.collectAsStateWithLifecycle()

    var showCompose by remember { mutableStateOf(false) }
    var showWebSync by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }

    // Silent Auto-Sign-In on App Startup
    LaunchedEffect(Unit) {
        AuthManager.attemptAutoSignIn(
            context = context,
            credentialManager = credentialManager,
            onAuthSuccess = { },
            onUnauthenticated = { },
            scope = coroutineScope
        )
    }

    // Telephony Permissions Launcher
    val permissionsToRequest = buildList {
        add(Manifest.permission.SEND_SMS)
        add(Manifest.permission.RECEIVE_SMS)
        add(Manifest.permission.READ_SMS)
        add(Manifest.permission.READ_CONTACTS)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            add(Manifest.permission.POST_NOTIFICATIONS)
        }
    }.toTypedArray()

    var hasTelephonyPermissions by remember {
        mutableStateOf(false)
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        hasTelephonyPermissions = results.values.all { it }
        viewModel.checkDefaultSmsStatus()
    }

    // Default SMS Role Launcher
    val roleLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { _ ->
        viewModel.checkDefaultSmsStatus()
    }

    val onRequestDefaultSms: () -> Unit = {
        val intent = viewModel.getDefaultSmsIntent()
        if (intent != null) {
            try {
                roleLauncher.launch(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    val onRequestPermissions: () -> Unit = {
        permissionLauncher.launch(permissionsToRequest)
    }

    if (currentThreadAddress != null) {
        ChatThreadScreen(
            conversation = currentConversation,
            messages = activeThreadMessages,
            language = currentLanguage,
            onBack = { viewModel.closeThread() },
            onSendMessage = { recipient, body, simSlot ->
                viewModel.sendMessage(recipient, body, simSlot)
            },
            onDeleteMessage = { id ->
                viewModel.deleteMessage(id, currentThreadAddress!!)
            },
            onDeleteConversation = {
                viewModel.deleteConversation(currentThreadAddress!!)
            }
        )
    } else {
        InboxScreen(
            conversations = conversations,
            selectedCategory = selectedCategory,
            searchQuery = searchQuery,
            totalUnreadCount = totalUnreadCount,
            isDefaultSms = isDefaultSms,
            hasTelephonyPermissions = hasTelephonyPermissions,
            isWebConnected = isWebConnected,
            language = currentLanguage,
            onCategorySelected = { viewModel.setCategory(it) },
            onSearchQueryChanged = { viewModel.setSearchQuery(it) },
            onConversationClicked = { viewModel.openThread(it) },
            onTogglePin = { address, isPinned -> viewModel.togglePin(address, isPinned) },
            onToggleArchive = { address, isArchived -> viewModel.toggleArchive(address, isArchived) },
            onDeleteConversation = { viewModel.deleteConversation(it) },
            onOpenCompose = { showCompose = true },
            onOpenWebSync = { showWebSync = true },
            onToggleLanguage = { viewModel.toggleLanguage() },
            onOpenSettings = { showSettings = true },
            onRequestDefaultSms = onRequestDefaultSms,
            onRequestPermissions = onRequestPermissions
        )
    }

    // Compose SMS Bottom Sheet
    if (showCompose) {
        ComposeDialog(
            language = currentLanguage,
            onSendMessage = { recipient, body, simSlot ->
                viewModel.sendMessage(recipient, body, simSlot)
                viewModel.openThread(recipient)
            },
            onDismiss = { showCompose = false }
        )
    }

    // Web Sync Bridge / SMS Simulator Bottom Sheet
    if (showWebSync) {
        WebSyncSheet(
            isConnected = isWebConnected,
            syncLogs = syncLogs,
            language = currentLanguage,
            onToggleConnection = { viewModel.toggleWebConnection() },
            onSimulateIncoming = { sender, body ->
                viewModel.simulateIncomingSms(sender, body)
            },
            onDismiss = { showWebSync = false }
        )
    }

    // Settings Bottom Sheet
    if (showSettings) {
        SettingsScreen(
            isDefaultSms = isDefaultSms,
            hasTelephonyPermissions = hasTelephonyPermissions,
            language = currentLanguage,
            currentUser = currentUser,
            isCloudSyncing = isCloudSyncing,
            onToggleLanguage = { viewModel.toggleLanguage() },
            onRequestDefaultSms = onRequestDefaultSms,
            onRequestPermissions = onRequestPermissions,
            onTriggerCloudSync = { viewModel.triggerCloudSyncNow() },
            onSignOut = {
                AuthManager.signOut(
                    context = context,
                    credentialManager = credentialManager,
                    onSignOutComplete = {
                        Toast.makeText(context, "Signed out of Google", Toast.LENGTH_SHORT).show()
                    },
                    scope = coroutineScope
                )
            },
            onAuthSuccess = {
                Toast.makeText(context, "Google Sign-In successful!", Toast.LENGTH_SHORT).show()
            },
            onAuthError = { errorMsg ->
                Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
            },
            onDismiss = { showSettings = false }
        )
    }
}
