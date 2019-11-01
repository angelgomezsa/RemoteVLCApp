package com.example.android.remotevlcapp.domain.browse

import com.example.android.remotevlcapp.data.MediaServer
import com.example.android.remotevlcapp.domain.UseCase
import javax.inject.Inject

class OpenFileUseCase @Inject constructor(private val mediaServer: MediaServer) :
    UseCase<String, Boolean>() {

    override suspend fun execute(parameters: String): Boolean {
        mediaServer.openFile(parameters)
        return true
    }
}