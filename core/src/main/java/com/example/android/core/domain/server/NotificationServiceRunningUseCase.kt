package com.example.android.core.domain.server

import com.example.android.core.data.MediaServer
import javax.inject.Inject

class NotificationServiceRunningUseCase @Inject constructor(private val mediaServer: MediaServer) {

    fun execute(isRunning: Boolean) {
        mediaServer.setNotificationServerRunning(isRunning)
    }
}