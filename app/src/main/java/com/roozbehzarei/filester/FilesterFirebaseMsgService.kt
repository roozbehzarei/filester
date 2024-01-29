package com.roozbehzarei.filester

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.roozbehzarei.filester.database.FirebaseUpdateMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Override [FirebaseMessagingService] to implement custom behaviour
 */
class FilesterFirebaseMsgService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    /**
     * Indicate UpdateDialog should be shown
     * if the incoming message contains [KEY_UPDATE] data payload
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (remoteMessage.data[KEY_UPDATE].isNullOrEmpty().not()) {
            remoteMessage.notification?.let { _ ->
                _incomingMessage.update {
                    it.copy(
                        isShown = false
                    )
                }
            }
        }
    }

    /**
     * Expose if Firebase message is received via [incomingMessage]
     */
    companion object {
        const val KEY_UPDATE = "FILESTER_UPDATE"
        private val _incomingMessage = MutableStateFlow(FirebaseUpdateMessage())
        val incomingMessage: StateFlow<FirebaseUpdateMessage> = _incomingMessage.asStateFlow()

        fun incomingMessageShown() {
            _incomingMessage.update {
                it.copy(
                    isShown = true
                )
            }
        }
    }

}