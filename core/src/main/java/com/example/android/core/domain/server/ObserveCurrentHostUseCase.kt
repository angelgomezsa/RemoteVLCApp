package com.example.android.core.domain.server

import androidx.lifecycle.LiveData
import com.example.android.core.data.MediaServer
import com.example.android.model.HostInfo
import javax.inject.Inject

class ObserveCurrentHostUseCase @Inject constructor(private val mediaServer: MediaServer) {
    fun execute(): LiveData<HostInfo> = mediaServer.currentHost
}