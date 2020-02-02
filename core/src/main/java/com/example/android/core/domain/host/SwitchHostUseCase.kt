package com.example.android.core.domain.host

import com.example.android.core.data.MediaServer
import com.example.android.core.domain.UseCase
import com.example.android.model.HostInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SwitchHostUseCase @Inject constructor(
    private val mediaServer: MediaServer
) : UseCase<HostInfo, Unit>() {

    override suspend fun execute(parameters: HostInfo) {
        withContext(Dispatchers.IO) {
            mediaServer.switchHost(parameters)
        }
    }
}