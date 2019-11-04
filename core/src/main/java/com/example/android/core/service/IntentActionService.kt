package com.example.android.core.service

import android.content.Intent
import android.os.IBinder
import com.example.android.core.domain.server.SendCommandParameters
import com.example.android.core.domain.server.SendCommandUseCase
import dagger.android.DaggerService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

// We use a regular Service instead of an IntentService
// because all the actions already happen in a background thread
class IntentActionService : DaggerService() {

    @Inject
    lateinit var sendCommandUseCase: SendCommandUseCase

    private val job = SupervisorJob()
    private val coroutineScope = CoroutineScope(Dispatchers.IO + job)

    companion object {
        const val ACTION_PLAY = "action_play"
        const val ACTION_PAUSE = "action_pause"
        const val ACTION_STOP = "action_stop"
        const val ACTION_REWIND = "action_rewind"
        const val ACTION_FAST_FORWARD = "action_fast_forward"
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        when (intent.action) {
            ACTION_PLAY -> {
                coroutineScope.launch {
                    sendCommandUseCase.execute(SendCommandParameters("pl_forceresume"))
                }
            }
            ACTION_PAUSE -> {
                coroutineScope.launch {
                    sendCommandUseCase.execute(SendCommandParameters("pl_forcepause"))
                }
            }
            ACTION_STOP -> {
                coroutineScope.launch {
                    sendCommandUseCase.execute(SendCommandParameters("pl_stop"))
                }
            }
            ACTION_FAST_FORWARD -> {
                coroutineScope.launch {
                    sendCommandUseCase.execute(SendCommandParameters("seek", "+15S"))
                }
            }
            ACTION_REWIND -> {
                coroutineScope.launch {
                    sendCommandUseCase.execute(SendCommandParameters("seek", "-15S"))
                }
            }
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}