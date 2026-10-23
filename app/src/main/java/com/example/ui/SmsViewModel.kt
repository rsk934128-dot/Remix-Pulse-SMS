package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.ConversationEntity
import com.example.data.model.MessageEntity
import com.example.data.remote.CloudMessage
import com.example.data.remote.FirebaseSyncRepository
import com.example.data.repository.SmsRepository
import com.example.data.repository.WebSyncEvent
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import com.example.auth.FirebaseAuthenticator
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class SmsViewModel(
    application: Application,
    val authenticator: FirebaseAuthenticator
) : AndroidViewModel(application) {

    constructor(application: Application) : this(
        application,
        FirebaseAuthenticator.getInstance()
    )

    private val repository = SmsRepository(application)
    private val firebaseSyncRepo = FirebaseSyncRepository(application)

    val selectedCategory = MutableStateFlow("ALL")
    val searchQuery = MutableStateFlow("")
    val currentLanguage = MutableStateFlow("BN") // "BN" or "EN"
    val selectedSimSlot = MutableStateFlow(0) // 0 for SIM 1, 1 for SIM 2

    private val _isDefaultSmsApp = MutableStateFlow(repository.isDefaultSmsApp())
    val isDefaultSmsApp: StateFlow<Boolean> = _isDefaultSmsApp.asStateFlow()

    private val _currentThreadAddress = MutableStateFlow<String?>(null)
    val currentThreadAddress: StateFlow<String?> = _currentThreadAddress.asStateFlow()

    private val _isWebConnected = MutableStateFlow(true)
    val isWebConnected: StateFlow<Boolean> = _isWebConnected.asStateFlow()

    private val _syncLogs = MutableStateFlow<List<WebSyncEvent>>(emptyList())
    val syncLogs: StateFlow<List<WebSyncEvent>> = _syncLogs.asStateFlow()

    // Firebase Auth & Cloud Sync States
    val currentUser: StateFlow<FirebaseUser?> = authenticator.authStateFlow

    private val _isCloudSyncing = MutableStateFlow(false)
    val isCloudSyncing: StateFlow<Boolean> = _isCloudSyncing.asStateFlow()

    init {
        // Track Firebase auth state and sync profile
        viewModelScope.launch {
            authenticator.authStateFlow.collect { user ->
                if (user != null) {
                    repository.emitSyncLog("AUTH", "User authenticated: ${user.email}")
                    try {
                        firebaseSyncRepo.saveUserProfile(
                            displayName = user.displayName ?: "User",
                            email = user.email ?: ""
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }

        viewModelScope.launch {
            repository.webSyncEvents.collect { event ->
                _syncLogs.value = listOf(event) + _syncLogs.value.take(29)
            }
        }
        viewModelScope.launch {
            repository.emitSyncLog("WEB_SOCKET", "Real-time sync engine initialized on port 5000 (ws://localhost:5000)")
        }
    }

    val totalUnreadCount: StateFlow<Int> = repository.getTotalUnreadCountFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val conversations: StateFlow<List<ConversationEntity>> = combine(
        selectedCategory,
        searchQuery
    ) { category, query ->
        Pair(category, query)
    }.flatMapLatest { (category, query) ->
        if (query.isNotBlank()) {
            repository.searchConversationsFlow(query)
        } else if (category == "ALL") {
            repository.getActiveConversationsFlow()
        } else if (category == "ARCHIVE") {
            repository.getArchivedConversationsFlow()
        } else {
            repository.getConversationsByCategoryFlow(category)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeThreadMessages: StateFlow<List<MessageEntity>> = _currentThreadAddress.flatMapLatest { address ->
        if (address == null) flowOf(emptyList())
        else repository.getMessagesForThreadFlow(address)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currentConversation: StateFlow<ConversationEntity?> = _currentThreadAddress.flatMapLatest { address ->
        if (address == null) flowOf(null)
        else repository.getConversationFlow(address)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun checkDefaultSmsStatus() {
        _isDefaultSmsApp.value = repository.isDefaultSmsApp()
    }

    fun getDefaultSmsIntent() = repository.getDefaultSmsIntent()

    fun setCategory(category: String) {
        selectedCategory.value = category
    }

    fun setSearchQuery(query: String) {
        searchQuery.value = query
    }

    fun setSimSlot(slot: Int) {
        selectedSimSlot.value = slot
    }

    fun toggleLanguage() {
        currentLanguage.value = if (currentLanguage.value == "BN") "EN" else "BN"
    }

    fun toggleWebConnection() {
        val newState = !_isWebConnected.value
        _isWebConnected.value = newState
        viewModelScope.launch {
            repository.emitSyncLog(
                if (newState) "WEB_SOCKET" else "STATUS",
                if (newState) "Web client connected to Android bridge (Socket ID: ws-client-8821)"
                else "Web client disconnected from Android bridge"
            )
        }
    }

    fun openThread(address: String) {
        _currentThreadAddress.value = address
        viewModelScope.launch {
            repository.markThreadAsRead(address)
        }
    }

    fun closeThread() {
        _currentThreadAddress.value = null
    }

    fun sendMessage(recipient: String, text: String, simSlot: Int = selectedSimSlot.value) {
        viewModelScope.launch {
            val result = repository.sendMessage(recipient, text, simSlot)
            if (_isWebConnected.value) {
                repository.emitSyncLog("WEB_SOCKET", "Broadcasted OUTGOING SMS to connected Web Clients via Socket.io")
            }

            // Sync to Firebase if user is authenticated
            if (result.isSuccess && currentUser.value != null) {
                val sentMessage = result.getOrNull()
                if (sentMessage != null) {
                    firebaseSyncRepo.syncMessage(sentMessage)
                    repository.emitSyncLog("FIRESTORE", "Synced message to Firebase Firestore cloud")
                }
            }
        }
    }

    fun simulateIncomingSms(sender: String, body: String) {
        viewModelScope.launch {
            val incoming = repository.handleIncomingSms(sender, body)
            if (_isWebConnected.value) {
                repository.emitSyncLog("WEB_SOCKET", "Broadcasted INCOMING SMS to connected Web Clients via Socket.io")
            }

            // Sync to Firebase if user is authenticated
            if (currentUser.value != null) {
                firebaseSyncRepo.syncMessage(incoming)
                repository.emitSyncLog("FIRESTORE", "Synced incoming SMS to Firebase Firestore cloud")
            }
        }
    }

    fun triggerCloudSyncNow() {
        viewModelScope.launch {
            if (currentUser.value == null) return@launch
            _isCloudSyncing.value = true
            try {
                repository.emitSyncLog("FIRESTORE", "Beginning manual cloud sync to Firestore...")
                // Save user profile
                firebaseSyncRepo.saveUserProfile(
                    displayName = currentUser.value?.displayName ?: "User",
                    email = currentUser.value?.email ?: ""
                )
                repository.emitSyncLog("FIRESTORE", "Cloud sync completed successfully.")
            } catch (e: Exception) {
                repository.emitSyncLog("FIRESTORE", "Cloud sync failed: ${e.message}")
            } finally {
                _isCloudSyncing.value = false
            }
        }
    }

    fun togglePin(address: String, isPinned: Boolean) {
        viewModelScope.launch {
            repository.setPinned(address, !isPinned)
        }
    }

    fun toggleArchive(address: String, isArchived: Boolean) {
        viewModelScope.launch {
            repository.setArchived(address, !isArchived)
        }
    }

    fun deleteConversation(address: String) {
        viewModelScope.launch {
            repository.deleteConversation(address)
            if (_currentThreadAddress.value == address) {
                _currentThreadAddress.value = null
            }
        }
    }

    fun deleteMessage(id: Long, address: String) {
        viewModelScope.launch {
            repository.deleteMessage(id, address)
        }
    }
}
