/*
Copyright 2022 Vasim Mansuri

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package com.justmusic.musicplayer.notifications

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.media.session.MediaButtonReceiver
import com.justmusic.MainActivity
import com.justmusic.R
import com.justmusic.musicplayer.services.MediaService
import com.justmusic.utils.PrintLog.printLogD

/**
 * This is a Notification util class to handler music player controller from notification panel.
 * @param mMediaService Required to pass Android's service component [MediaService]
 */
class MediaNotificationManager(private val mMediaService: MediaService) {
    val notificationManager: NotificationManager = mMediaService.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val mPlayAction: NotificationCompat.Action = NotificationCompat.Action(
        R.drawable.ic_play_arrow_white,
        mMediaService.getString(R.string.label_play),
        MediaButtonReceiver.buildMediaButtonPendingIntent(
            mMediaService,
            PlaybackStateCompat.ACTION_PLAY
        )
    )
    private val mPauseAction: NotificationCompat.Action = NotificationCompat.Action(
        R.drawable.ic_pause_circle_outline_white,
        mMediaService.getString(R.string.label_pause),
        MediaButtonReceiver.buildMediaButtonPendingIntent(
            mMediaService,
            PlaybackStateCompat.ACTION_PAUSE
        )
    )
    private val mNextAction: NotificationCompat.Action = NotificationCompat.Action(
        R.drawable.ic_skip_next_white,
        mMediaService.getString(R.string.label_next),
        MediaButtonReceiver.buildMediaButtonPendingIntent(
            mMediaService,
            PlaybackStateCompat.ACTION_SKIP_TO_NEXT
        )
    )
    private val mPrevAction: NotificationCompat.Action = NotificationCompat.Action(
        R.drawable.ic_skip_previous_white,
        mMediaService.getString(R.string.label_previous),
        MediaButtonReceiver.buildMediaButtonPendingIntent(
            mMediaService,
            PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
        )
    )

    // Does nothing on versions of Android earlier than O.
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel() {
        if (notificationManager.getNotificationChannel(CHANNEL_ID) == null) {
            // The user-visible name of the channel.
            val name: CharSequence = "MediaSession"
            // The user-visible description of the channel.
            val description = "MediaSession and MediaPlayer"
            val importance = NotificationManager.IMPORTANCE_LOW
            val mChannel = NotificationChannel(CHANNEL_ID, name, importance)
            // Configure the notification channel.
            mChannel.description = description
            mChannel.enableLights(true)
            // Sets the notification light color for notifications posted to this
            // channel, if the device supports this feature.
            mChannel.lightColor = Color.RED
            mChannel.enableVibration(true)
            mChannel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            notificationManager.createNotificationChannel(mChannel)
            printLogD(TAG, "createChannel: New channel created")
        } else {
            printLogD(TAG, "createChannel: Existing channel reused")
        }
    }

    private val isAndroidOOrHigher: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

    fun buildNotification(
        state: PlaybackStateCompat,
        token: MediaSessionCompat.Token?, // boolean isPlaying,
        description: MediaDescriptionCompat,
        bitmap: Bitmap?
    ): Notification {
        val isPlaying = state.state == PlaybackStateCompat.STATE_PLAYING

        // Create the (mandatory) notification channel when running on Android Oreo.
        if (isAndroidOOrHigher) {
            createChannel()
        }
        val builder = NotificationCompat.Builder(mMediaService, CHANNEL_ID)
        builder.setStyle(
            androidx.media.app.NotificationCompat.MediaStyle()
                .setMediaSession(token)
                .setShowActionsInCompactView(0, 1, 2)
        )
            .setColor(ContextCompat.getColor(mMediaService, R.color.colorPrimary))
            .setSmallIcon(R.drawable.ic_audiotrack_grey) // Pending intent that is fired when user clicks on notification.
            .setContentIntent(createContentIntent()) // Title - Usually Song name.
            .setContentTitle(description.title) // Subtitle - Usually Artist name.
            .setContentText(description.subtitle)
            .setLargeIcon(bitmap) // When notification is deleted (when playback is paused and notification can be
            // deleted) fire MediaButtonPendingIntent with ACTION_STOP.
            .setDeleteIntent(
                MediaButtonReceiver.buildMediaButtonPendingIntent(
                    mMediaService, PlaybackStateCompat.ACTION_STOP
                )
            ) // Show controls on lock screen even when user hides sensitive content.
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

        // If skip to previous action is enabled.
        if (state.actions and PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS != 0L) {
            builder.addAction(mPrevAction)
        }
        builder.addAction(if (isPlaying) mPauseAction else mPlayAction)

        // If skip to next action is enabled.
        if (state.actions and PlaybackStateCompat.ACTION_SKIP_TO_NEXT != 0L) {
            builder.addAction(mNextAction)
        }
        return builder.build()
    }

    private fun createContentIntent(): PendingIntent {
        val openUI = Intent(mMediaService, MainActivity::class.java)
        openUI.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        return PendingIntent.getActivity(
            mMediaService,
            REQUEST_CODE,
            openUI,
            PendingIntent.FLAG_MUTABLE
        )
    }

    companion object {
        private const val TAG = "MediaNotificationManage"
        private const val CHANNEL_ID = "com.justmusic.channel"
        private const val REQUEST_CODE = 101
        const val NOTIFICATION_ID = 201
    }

    init {
        notificationManager.cancelAll()
    }
}
