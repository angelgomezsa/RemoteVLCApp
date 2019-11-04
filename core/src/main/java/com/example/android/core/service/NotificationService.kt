package com.example.android.core.service

import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.Observer
import com.example.android.core.R
import com.example.android.core.data.MediaServer
import com.example.android.core.result.Result
import com.example.android.core.service.IntentActionService.Companion.ACTION_FAST_FORWARD
import com.example.android.core.service.IntentActionService.Companion.ACTION_PAUSE
import com.example.android.core.service.IntentActionService.Companion.ACTION_PLAY
import com.example.android.core.service.IntentActionService.Companion.ACTION_REWIND
import com.example.android.core.service.IntentActionService.Companion.ACTION_STOP
import com.example.android.model.VLCPlayer
import dagger.android.AndroidInjection
import javax.inject.Inject

class NotificationService : LifecycleService() {

    companion object {
        private const val NOTIFICATION_ID = 100
        private const val NOTIFICATION_CHANNEL = "vlcremote_notification_channel"
        const val EXTRAS_STATE = "extras_state"
        const val EXTRAS_FILENAME = "extras_filename"
    }

    @Inject
    lateinit var mediaServer: MediaServer

    private var contentPendingIntent: PendingIntent? = null
    private lateinit var filename: String
    private lateinit var state: String
    private lateinit var notificationLargeIcon: Bitmap

    override fun onCreate() {
        super.onCreate()
        AndroidInjection.inject(this)
        val intent = Intent("com.example.android.remotevlcapp.notification")
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        contentPendingIntent = TaskStackBuilder.create(this)
            .addNextIntentWithParentStack(intent)
            .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)

        createDefaultNotificationLargeIcon()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            buildNotificationChannel()
        }

        mediaServer.playerStatus.observe(this, Observer {
            if (it is Result.Success) {
                val newstate = it.data.state
                val newfilename = it.data.information?.category?.meta?.filename ?: ""
                if (state != newstate || filename != newfilename) {
                    state = newstate
                    filename = newfilename
                    onPlayBackStateChanged(state, filename)
                }
            } else if (it is Result.Error) {
                onConnectionError()
            }
        })
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        mediaServer.isNotificationServiceRunning = true
        requireNotNull(intent.extras).apply {
            state = getString(EXTRAS_STATE, "")
            filename = getString(EXTRAS_FILENAME, "")
        }
        startForeground(NOTIFICATION_ID, buildNotification(state, filename))
        return START_NOT_STICKY
    }

    private fun onPlayBackStateChanged(state: String, filename: String) {
        when (state) {
            VLCPlayer.State.PAUSED,
            VLCPlayer.State.PLAYING -> showNotification(state, filename)
            VLCPlayer.State.STOPPED -> {
                stopForeground(true)
                stopSelf()
            }
        }
    }

    private fun onConnectionError() {
        stopForeground(true)
        stopSelf()
    }

    private fun buildNotification(state: String, filename: String): Notification {
        val playPauseIcon =
            if (state == VLCPlayer.State.PLAYING) R.drawable.ic_pause else R.drawable.ic_play
        val playPausePendingIntent = if (state == VLCPlayer.State.PLAYING)
            buildPendingIntent(ACTION_PAUSE)
        else buildPendingIntent(ACTION_PLAY)

        val fastForwardPendingIntent = buildPendingIntent(ACTION_FAST_FORWARD)
        val rewindPendingIntent = buildPendingIntent(ACTION_REWIND)
        val stopPendingIntent = buildPendingIntent(ACTION_STOP)

        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setCategory(NotificationCompat.CATEGORY_TRANSPORT)
            .setSmallIcon(R.drawable.ic_devices)
            .setContentIntent(contentPendingIntent)
            .setContentTitle(filename)
            .setLargeIcon(notificationLargeIcon)
            .setShowWhen(false)
            .setOngoing(true)
            .addAction(
                R.drawable.ic_fast_rewind,
                getString(R.string.rewind),
                rewindPendingIntent
            ) // #0
            .addAction(R.drawable.ic_stop, getString(R.string.stop), stopPendingIntent) // #1
            .addAction(playPauseIcon, getString(R.string.play_pause), playPausePendingIntent) // #2
            .addAction(
                R.drawable.ic_fast_forward,
                getString(R.string.fast_forward),
                fastForwardPendingIntent
            ) // #3
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(0, 2, 3)
            )
            .build()
    }

    private fun buildPendingIntent(action: String): PendingIntent {
        val intent = Intent(this, IntentActionService::class.java).apply {
            this.action = action
        }
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun showNotification(state: String, filename: String) {
        val notification = buildNotification(state, filename)
        val notificationManager = NotificationManagerCompat.from(applicationContext)
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun buildNotificationChannel() {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL,
            this.getString(R.string.app_name),
            NotificationManager.IMPORTANCE_LOW
        )
        channel.enableLights(false)
        channel.enableVibration(false)
        channel.setShowBadge(false)

        val notificationManager =
            this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun createDefaultNotificationLargeIcon() {
        val d = getDrawable(R.drawable.ic_devices)!!
        val b = Bitmap.createBitmap(d.intrinsicWidth, d.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(b)
        // 15px of padding
        d.setBounds(15, 15, canvas.width - 15, canvas.height - 15)
        d.draw(canvas)
        notificationLargeIcon = b
    }

    // Called when the user swipes the app away from recents
    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        stopForeground(true)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaServer.isNotificationServiceRunning = false
    }
}