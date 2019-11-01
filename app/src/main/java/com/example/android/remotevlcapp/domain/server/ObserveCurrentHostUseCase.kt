package com.example.android.remotevlcapp.domain.server

import androidx.lifecycle.LiveData
import com.example.android.remotevlcapp.data.MediaServer
import com.example.android.remotevlcapp.model.HostInfo
import javax.inject.Inject

class ObserveCurrentHostUseCase @Inject constructor(private val mediaServer: MediaServer) {
    fun execute(): LiveData<HostInfo> = mediaServer.currentHost
}