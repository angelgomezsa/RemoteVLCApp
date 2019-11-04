package com.example.android.core.domain.browse

import com.example.android.core.data.MediaServer
import com.example.android.core.domain.UseCase
import javax.inject.Inject

class OpenFileUseCase @Inject constructor(private val mediaServer: MediaServer) :
    UseCase<String, Boolean>() {

    override suspend fun execute(parameters: String): Boolean {
        mediaServer.openFile(parameters)
        return true
    }
}