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
package com.justmusic.musicplayer.services

import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.text.TextUtils
import androidx.core.content.ContextCompat
import androidx.media.MediaBrowserServiceCompat
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.justmusic.JustMusicApplication
import com.justmusic.R
import com.justmusic.cache.local.PreferencesHelper
import com.justmusic.musicplayer.notifications.MediaNotificationManager
import com.justmusic.musicplayer.players.MediaPlayerAdapter
import com.justmusic.musicplayer.players.PlaybackInfoListener
import com.justmusic.shared.MusicPlayerConstants
import com.justmusic.utils.PrintLog.printLogD
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * This is a service implementation which is the main part of Media player
 */
@AndroidEntryPoint
class MediaService : MediaBrowserServiceCompat() {
    private var mSession: MediaSessionCompat? = null
    private var mPlayback: MediaPlayerAdapter? = null
    @Inject lateinit var justMusicApplication: JustMusicApplication
    @Inject lateinit var preferencesHelper: PreferencesHelper
    private var mMediaNotificationManager: MediaNotificationManager? = null
    private var mIsServiceStarted = false
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)
    override fun onCreate() {
        super.onCreate()
        // Build a PendingIntent that can be used to launch the UI.
        val sessionActivityPendingIntent =
            packageManager?.getLaunchIntentForPackage(packageName)?.let { sessionIntent ->
                PendingIntent.getActivity(this, 0, sessionIntent, PendingIntent.FLAG_MUTABLE)
            }

        // Build the MediaSession
        mSession = MediaSessionCompat(this, TAG)
            .apply {
                setSessionActivity(sessionActivityPendingIntent)
                isActive = true
            }

        mSession?.setFlags(MediaSessionCompat.FLAG_HANDLES_QUEUE_COMMANDS)
        mSession?.setCallback(MediaSessionCallback())

        // A token that can be used to create a MediaController for this session
        sessionToken = mSession?.sessionToken
        mPlayback = MediaPlayerAdapter(this, MediaPlayerListener())
        mMediaNotificationManager = MediaNotificationManager(this)
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        printLogD(TAG, "onTaskRemoved: stopped")
        super.onTaskRemoved(rootIntent)
        mPlayback?.stop()
        scope.cancel()
        stopSelf()
    }

    override fun onDestroy() {
        mSession?.release()
        printLogD(TAG, "onDestroy: MediaPlayerAdapter stopped, and MediaSession released")
    }

    override fun onGetRoot(s: String, i: Int, bundle: Bundle?): BrowserRoot {
        printLogD(TAG, "onGetRoot: called. ")
        return if (s == applicationContext.packageName) {
            // Allowed to browse media
            BrowserRoot("some_real_playlist", null) // return no media
        } else BrowserRoot("empty_media", null)
        // return no media
    }

    override fun onLoadChildren(s: String, result: Result<List<MediaBrowserCompat.MediaItem>>) {
        printLogD(TAG, "onLoadChildren: called: $s, $result")
        //  Browsing not allowed
        if (TextUtils.equals("empty_media", s)) {
            result.sendResult(null)
            return
        }
        result.sendResult(justMusicApplication.mediaItems) // return all available media
    }

    inner class MediaSessionCallback : MediaSessionCompat.Callback() {
        private val mPlaylist: MutableList<MediaSessionCompat.QueueItem> = ArrayList()
        private var mQueueIndex = -1
        private var mPreparedMedia: MediaMetadataCompat? = null
        private fun resetPlaylist() {
            mPlaylist.clear()
            mQueueIndex = -1
        }

        override fun onPlayFromMediaId(mediaId: String, extras: Bundle) {
            printLogD(TAG, "onPlayFromMediaId: CALLED.")
            if (extras.getBoolean(MusicPlayerConstants.QUEUE_NEW_PLAYLIST, false)) {
                resetPlaylist()
            }
            mPreparedMedia = justMusicApplication.treeMap[mediaId]
            mSession?.setMetadata(mPreparedMedia)
            if (mSession?.isActive == true) {
                mSession?.isActive = true
            }
            mPreparedMedia?.let { mPlayback?.playFromMedia(it) }
            val newQueuePosition = extras.getInt(MusicPlayerConstants.MEDIA_QUEUE_POSITION, -1)
            if (newQueuePosition == -1) {
                mQueueIndex++
            } else {
                mQueueIndex = extras.getInt(MusicPlayerConstants.MEDIA_QUEUE_POSITION)
            }
            preferencesHelper.saveQueuePosition(mQueueIndex)
            preferencesHelper.saveLastPlayedMedia(mPreparedMedia?.description?.mediaId)
        }

        override fun onAddQueueItem(description: MediaDescriptionCompat) {
            printLogD(TAG, "onAddQueueItem: CALLED: position in list: " + mPlaylist.size)
            mPlaylist.add(
                MediaSessionCompat.QueueItem(
                    description,
                    description.hashCode().toLong()
                )
            )
            mQueueIndex = if (mQueueIndex == -1) 0 else mQueueIndex
            mSession?.setQueue(mPlaylist)
        }

        override fun onRemoveQueueItem(description: MediaDescriptionCompat) {
            mPlaylist.remove(
                MediaSessionCompat.QueueItem(
                    description,
                    description.hashCode()
                        .toLong()
                )
            )
            mQueueIndex = if (mPlaylist.isEmpty()) -1 else mQueueIndex
            mSession?.setQueue(mPlaylist)
        }

        override fun onPrepare() {
            if (mQueueIndex < 0 && mPlaylist.isEmpty()) {
                // Nothing to play.
                return
            }
            val mediaId = mPlaylist[mQueueIndex].description.mediaId
            mPreparedMedia = justMusicApplication.treeMap[mediaId]
            mSession?.setMetadata(mPreparedMedia)
            if (mSession?.isActive == true) {
                mSession?.isActive = true
            }
        }

        override fun onPlay() {
            if (!isReadyToPlay) {
                // Nothing to play.
                return
            }
            if (mPreparedMedia == null) {
                onPrepare()
            }
            mPreparedMedia?.let { mPlayback?.playFromMedia(it) }
            preferencesHelper.saveQueuePosition(mQueueIndex)
            preferencesHelper.saveLastPlayedMedia(mPreparedMedia?.description?.mediaId)
        }

        override fun onPause() {
            mPlayback?.pause()
        }

        override fun onStop() {
            mPlayback?.stop()
            mSession?.isActive = false
        }

        override fun onSkipToNext() {
            printLogD(TAG, "onSkipToNext: SKIP TO NEXT")
            // increment and then check using modulus
            mQueueIndex = ++mQueueIndex % mPlaylist.size
            mPreparedMedia = null
            onPlay()
        }

        override fun onSkipToPrevious() {
            printLogD(TAG, "onSkipToPrevious: SKIP TO PREVIOUS")
            mQueueIndex = if (mQueueIndex > 0) mQueueIndex - 1 else mPlaylist.size - 1
            mPreparedMedia = null
            onPlay()
        }

        override fun onSeekTo(pos: Long) {
            mPlayback?.seekTo(pos)
        }

        private val isReadyToPlay: Boolean
            get() = mPlaylist.isNotEmpty()
    }

    inner class MediaPlayerListener internal constructor() : PlaybackInfoListener {
        private val mServiceManager: ServiceManager
        override fun onPlaybackStateChange(state: PlaybackStateCompat?) {
            state?.let {
                mSession?.setPlaybackState(state)
                when (state.state) {
                    PlaybackStateCompat.STATE_PLAYING ->
                        mPlayback?.currentMedia?.getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI)
                            ?.let { it1 ->
                                mServiceManager.updateNotification(
                                    state,
                                    it1
                                )
                            }
                    PlaybackStateCompat.STATE_PAUSED ->
                        mPlayback?.currentMedia?.getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI)
                            ?.let { it1 ->
                                mServiceManager.updateNotification(
                                    state,
                                    it1
                                )
                            }
                    PlaybackStateCompat.STATE_STOPPED -> {
                        printLogD(TAG, "onPlaybackStateChange: STOPPED.")
                        mServiceManager.moveServiceOutOfStartedState()
                    }
                    else -> {}
                }
            }
        }

        override fun onSeekTo(progress: Long, max: Long) {
//            printLogD(TAG, "onSeekTo: CALLED: updating seekbar: " + progress + ", max: " + max);
            val intent = Intent()
            intent.action = getString(R.string.broadcast_seekbar_update)
            intent.putExtra(MusicPlayerConstants.SEEK_BAR_PROGRESS, progress)
            intent.putExtra(MusicPlayerConstants.SEEK_BAR_MAX, max)
            sendBroadcast(intent)
        }

        override fun onPlaybackComplete() {
            printLogD(TAG, "onPlaybackComplete: SKIPPING TO NEXT.")
            mSession?.controller?.transportControls?.skipToNext()
        }

        override fun updateUI(newMediaId: String?) {
            newMediaId?.let {
                printLogD(TAG, "updateUI: CALLED: $newMediaId")
                val intent = Intent()
                intent.action = getString(R.string.broadcast_update_ui)
                intent.putExtra(getString(R.string.broadcast_new_media_id), newMediaId)
                sendBroadcast(intent)
            }
        }

        internal inner class ServiceManager : ICallback {
            private var mDisplayImageUri: String? = null
            private var mCurrentArtistBitmap: Bitmap? = null
            private var mState: PlaybackStateCompat? = null
            fun updateNotification(state: PlaybackStateCompat, displayImageUri: String) {
                mState = state
                if (displayImageUri != mDisplayImageUri) {
                    // download new bitmap
                    scope.launch {
                        val loader = ImageLoader(baseContext)
                        val request = ImageRequest.Builder(baseContext)
                            .data(displayImageUri)
                            .allowHardware(false) // Disable hardware bitmaps.
                            .build()

                        val result = (loader.execute(request) as SuccessResult).drawable
                        val bitmap = (result as BitmapDrawable).bitmap
                        done(bitmap)
                    }
                    mDisplayImageUri = displayImageUri
                } else {
                    displayNotification(mCurrentArtistBitmap)
                }
            }

            private fun displayNotification(bitmap: Bitmap?) {
                // Manage the started state of this service.
                val notification: Notification?
                mState?.let {
                    when (mState?.state) {
                        PlaybackStateCompat.STATE_PLAYING -> {
                            notification = mPlayback?.currentMedia?.description?.let { it1 ->
                                mMediaNotificationManager?.buildNotification(
                                    it, sessionToken, it1, bitmap
                                )
                            }
                            if (!mIsServiceStarted) {
                                ContextCompat.startForegroundService(
                                    this@MediaService,
                                    Intent(this@MediaService, MediaService::class.java)
                                )
                                mIsServiceStarted = true
                            }
                            startForeground(MediaNotificationManager.NOTIFICATION_ID, notification)
                        }
                        PlaybackStateCompat.STATE_PAUSED -> {
                            stopForeground(false)
                            notification = mPlayback?.currentMedia?.description?.let { it1 ->
                                mMediaNotificationManager?.buildNotification(
                                    it, sessionToken, it1, bitmap
                                )
                            }
                            mMediaNotificationManager?.notificationManager?.notify(MediaNotificationManager.NOTIFICATION_ID, notification)
                        }
                        else -> {}
                    }
                }
            }

            fun moveServiceOutOfStartedState() {
                stopForeground(true)
                stopSelf()
                mIsServiceStarted = false
            }

            override fun done(bitmap: Bitmap?) {
                mCurrentArtistBitmap = bitmap
                displayNotification(mCurrentArtistBitmap)
            }
        }

        init {
            mServiceManager = ServiceManager()
        }
    }

    companion object {
        private const val TAG = "MediaService"
    }
}
