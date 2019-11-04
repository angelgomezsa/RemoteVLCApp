package com.example.android.core.domain.browse

import com.example.android.core.data.MediaServer
import com.example.android.core.domain.UseCase
import com.example.android.model.FileInfo
import javax.inject.Inject

class BrowseUseCase @Inject constructor(private val mediaServer: MediaServer) :
    UseCase<String, List<FileInfo>>() {
    override suspend fun execute(parameters: String): List<FileInfo> =
        mediaServer.browse(parameters)
}