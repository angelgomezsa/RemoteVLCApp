package com.example.android.core.service

import android.content.Intent
import android.os.IBinder
import com.example.android.core.domain.server.SendCommandParameters
import com.example.android.core.domain.server.SendCommandUseCase
import dagger.android.DaggerService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

// We use a regular Service instead of a JobIntentService
// because all the actions already happen in a background thread
class IntentActionService : DaggerService() {

    @Inject
    lateinit var sendCommandUseCase: SendCommandUseCase

    private val job = SupervisorJob()
    private val coroutineScope = CoroutineScope(job)

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        coroutineScope.launch {
            when (intent.action) {
                ACTION_PLAY -> {
                    sendCommandUseCase.execute(SendCommandParameters("pl_forceresume"))
                }
                ACTION_PAUSE -> {
                    sendCommandUseCase.execute(SendCommandParameters("pl_forcepause"))
                }
                ACTION_STOP -> {
                    sendCommandUseCase.execute(SendCommandParameters("pl_stop"))
                }
                ACTION_FAST_FORWARD -> {
                    sendCommandUseCase.execute(SendCommandParameters("seek", "+15S"))
                }
                ACTION_REWIND -> {
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