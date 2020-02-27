package com.example.android.core.service

import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.Observer
import com.example.android.core.data.MediaServer
import com.example.android.core.result.Result
import com.example.android.model.VLCPlayer
import dagger.android.AndroidInjection
import javax.inject.Inject

class NotificationService : LifecycleService() {

    @Inject
    lateinit var mediaServer: MediaServer

    private lateinit var notificationBuilder: NotificationBuilder
    private lateinit var notificationManager: NotificationManagerCompat

    private var isForegroundService = false
    private var filename: String = ""
    private var state: String = ""

    override fun onCreate() {
        super.onCreate()
        AndroidInjection.inject(this)

        notificationBuilder = NotificationBuilder(this)
        notificationManager = NotificationManagerCompat.from(this)

        mediaServer.playerStatus.observe(this, Observer {
            if (it is Result.Success) {
                val newstate = it.data.state
                val newfilename = it.data.information?.category?.meta?.filename ?: ""
                if (state != newstate || filename != newfilename) {
                    state = newstate
                    filename = newfilename
                    onPlayBackStateChanged()
                }
            } else if (it is Result.Error) {
                stopService()
            }
        })
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        mediaServer.isNotificationServiceRunning = true
        return START_NOT_STICKY
    }

    private fun updateNotification() {
        val notification = notificationBuilder.buildNotification(state, filename)
        if (isForegroundService) {
            notificationManager.notify(NOW_PLAYING_NOTIFICATION, notification)
        } else {
            startForeground(NOW_PLAYING_NOTIFICATION, notification)
            isForegroundService = true
        }
    }

    private fun onPlayBackStateChanged() {
        when (state) {
            VLCPlayer.State.PAUSED,
            VLCPlayer.State.PLAYING -> updateNotification()
            VLCPlayer.State.STOPPED -> stopService()
        }
    }

    private fun stopService() {
        stopForeground(true)
        stopSelf()
    }

    // Called when the user swipes the app away from recents
    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        stopService()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaServer.isNotificationServiceRunning = false
    }
}