package com.example.android.remotevlcapp.domain.server

import androidx.lifecycle.LiveData
import com.example.android.remotevlcapp.data.MediaServer
import com.example.android.remotevlcapp.event.Result
import com.example.android.remotevlcapp.model.Status
import javax.inject.Inject

class ObserveCurrentStatusUseCase @Inject constructor(private val mediaServer: MediaServer) {
    fun execute(): LiveData<Result<Status>> = mediaServer.playerStatus
}