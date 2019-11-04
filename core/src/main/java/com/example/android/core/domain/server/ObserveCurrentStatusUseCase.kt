package com.example.android.core.domain.server

import androidx.lifecycle.LiveData
import com.example.android.core.data.MediaServer
import com.example.android.core.result.Result
import com.example.android.model.Status
import javax.inject.Inject

class ObserveCurrentStatusUseCase @Inject constructor(private val mediaServer: MediaServer) {
    fun execute(): LiveData<Result<Status>> = mediaServer.playerStatus
}