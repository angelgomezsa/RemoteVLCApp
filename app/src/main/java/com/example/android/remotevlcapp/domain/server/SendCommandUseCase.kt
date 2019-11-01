package com.example.android.remotevlcapp.domain.server

import com.example.android.remotevlcapp.data.MediaServer
import javax.inject.Inject

class SendCommandUseCase @Inject constructor(
    private val mediaServer: MediaServer
) {

    suspend fun execute(parameters: SendCommandParameters) {
        mediaServer.sendCommand(parameters.command, parameters.value)
    }
}

data class SendCommandParameters(
    val command: String,
    val value: String? = null
)