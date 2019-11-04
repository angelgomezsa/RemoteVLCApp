package com.example.android.core.domain.host

import com.example.android.core.data.MediaServer
import com.example.android.core.domain.UseCase
import com.example.android.model.HostInfo
import javax.inject.Inject

class SwitchHostUseCase @Inject constructor(
    private val mediaServer: MediaServer
) : UseCase<HostInfo, Unit>() {

    override suspend fun execute(parameters: HostInfo) {
        mediaServer.switchHost(parameters)
    }
}