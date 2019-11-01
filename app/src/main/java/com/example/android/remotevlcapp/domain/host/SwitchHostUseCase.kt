package com.example.android.remotevlcapp.domain.host

import com.example.android.remotevlcapp.data.MediaServer
import com.example.android.remotevlcapp.domain.UseCase
import com.example.android.remotevlcapp.model.HostInfo
import javax.inject.Inject

class SwitchHostUseCase @Inject constructor(
    private val mediaServer: MediaServer
) : UseCase<HostInfo, Unit>() {

    override suspend fun execute(parameters: HostInfo) {
        mediaServer.switchHost(parameters)
    }
}