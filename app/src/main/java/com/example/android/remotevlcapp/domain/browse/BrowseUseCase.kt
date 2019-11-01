package com.example.android.remotevlcapp.domain.browse

import com.example.android.remotevlcapp.data.MediaServer
import com.example.android.remotevlcapp.domain.UseCase
import com.example.android.remotevlcapp.model.FileInfo
import javax.inject.Inject

class BrowseUseCase @Inject constructor(private val mediaServer: MediaServer) :
    UseCase<String, List<FileInfo>>() {
    override suspend fun execute(parameters: String): List<FileInfo> =
        mediaServer.browse(parameters)
}