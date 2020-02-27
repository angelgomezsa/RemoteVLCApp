package com.example.android.core.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import androidx.media.app.NotificationCompat.MediaStyle
import com.example.android.core.R
import com.example.android.model.VLCPlayer

const val ACTION_PLAY = "com.example.android.remotevlcapp.core.service.ACTION_PLAY"
const val ACTION_PAUSE = "com.example.android.remotevlcapp.core.service.ACTION_PAUSE"
const val ACTION_STOP = "com.example.android.remotevlcapp.core.service.ACTION_STOP"
const val ACTION_REWIND = "com.example.android.remotevlcapp.core.service.ACTION_REWIND"
const val ACTION_FAST_FORWARD = "com.example.android.remotevlcapp.core.service.ACTION_FAST_FORWARD"

const val NOW_PLAYING_CHANNEL = "com.example.android.remotevlcapp.core.service.NOW_PLAYING"
const val NOW_PLAYING_NOTIFICATION = 555


class NotificationBuilder(private val context: Context) {

    companion object {
        private const val ACTION_NOTIFICATION_TAP =
            "com.example.android.remotevlcapp.core.service.ACTION_NOTIFICATION_TAP"
    }

    private val contentPendingIntent = TaskStackBuilder.create(context)
        .addNextIntentWithParentStack(
            Intent(ACTION_NOTIFICATION_TAP).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            })
        .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)

//    private val contentPendingIntent =
//        context.packageManager.getLaunchIntentForPackage(context.packageName).let { intent ->
//            PendingIntent.getActivity(context, 0, intent, 0)
//        }

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private val playAction = NotificationCompat.Action(
        R.drawable.ic_play,
        context.getString(R.string.notification_play),
        buildPendingIntent(ACTION_PLAY)
    )

    private val pauseAction = NotificationCompat.Action(
        R.drawable.ic_pause,
        context.getString(R.string.notification_pause),
        buildPendingIntent(ACTION_PAUSE)
    )

    private val stopAction = NotificationCompat.Action(
        R.drawable.ic_stop,
        context.getString(R.string.notification_stop),
        buildPendingIntent(ACTION_STOP)
    )

    private val rewindAction = NotificationCompat.Action(
        R.drawable.ic_fast_rewind,
        context.getString(R.string.notification_rewind),
        buildPendingIntent(ACTION_REWIND)
    )

    private val fastForwardAction = NotificationCompat.Action(
        R.drawable.ic_fast_forward,
        context.getString(R.string.notification_fast_forward),
        buildPendingIntent(ACTION_FAST_FORWARD)
    )

    private fun buildPendingIntent(action: String): PendingIntent {
        val intent = Intent(context, IntentActionService::class.java).apply {
            this.action = action
        }
        return PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    fun buildNotification(state: String, filename: String): Notification {
        if (shouldCreateNowPlayingChannel()) {
            createNowPlayingChannel()
        }

        val builder = NotificationCompat.Builder(context, NOW_PLAYING_CHANNEL)

        builder.addAction(rewindAction)
        builder.addAction(stopAction)
        if (state == VLCPlayer.State.PLAYING) {
            builder.addAction(pauseAction)
        } else {
            builder.addAction(playAction)
        }
        builder.addAction(fastForwardAction)

        val mediaStyle = MediaStyle().setShowActionsInCompactView(0, 2, 3)
        val largeIconBitmap = createLargeIconAsBitmap()

        return builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setCategory(NotificationCompat.CATEGORY_TRANSPORT)
            .setSmallIcon(R.drawable.ic_devices)
            .setContentIntent(contentPendingIntent)
            .setContentTitle(filename)
            .setLargeIcon(largeIconBitmap)
            .setStyle(mediaStyle)
            .setShowWhen(false)
            .setOngoing(true)
            .build()
    }

    private fun createLargeIconAsBitmap(): Bitmap? {
        val d = context.getDrawable(R.drawable.ic_devices) ?: return null
        val b = Bitmap.createBitmap(d.intrinsicWidth, d.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(b)
        // 15px of padding
        d.setBounds(15, 15, canvas.width - 15, canvas.height - 15)
        d.draw(canvas)
        return b
    }

    private fun shouldCreateNowPlayingChannel() =
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !nowPlayingChannelExists()

    @RequiresApi(Build.VERSION_CODES.O)
    private fun nowPlayingChannelExists() =
        notificationManager.getNotificationChannel(NOW_PLAYING_CHANNEL) != null

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNowPlayingChannel() {
        val notificationChannel = NotificationChannel(
            NOW_PLAYING_CHANNEL,
            context.getString(R.string.notification_channel),
            NotificationManager.IMPORTANCE_LOW
        )
            .apply {
                description = context.getString(R.string.notification_channel_description)
            }

        notificationManager.createNotificationChannel(notificationChannel)
    }
}