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
package com.justmusic.musicplayer.players

import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackParameters
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.justmusic.utils.PrintLog.printLogD

/**
 * This is use to perform media utilities operations.
 * @param context Required to pass the context
 * @param playbackInfoListener Required to pass playback info listener [PlaybackInfoListener]
 */
class MediaPlayerAdapter(context: Context, playbackInfoListener: PlaybackInfoListener) :
    PlayerAdapter(context) {
    private val mContext: Context
    override var currentMedia: MediaMetadataCompat? = null
        private set
    private var mCurrentMediaPlayedToCompletion = false
    private var mState = 0
    private var mStartTime: Long = 0
    private val mPlaybackInfoListener: PlaybackInfoListener

    // ExoPlayer objects
    private var mExoPlayer: ExoPlayer? = null

    private var mRenderersFactory: DefaultRenderersFactory? = null
    private var mDataSourceFactory: DataSource.Factory? = null
    private var mExoPlayerEventListener: ExoPlayerEventListener? = null
    private fun initializeExoPlayer() {
        if (mExoPlayer == null) {
            mRenderersFactory = DefaultRenderersFactory(mContext)
            mDataSourceFactory = DefaultDataSource.Factory(mContext)

            val mediaDataSourceFactory: DataSource.Factory = DefaultDataSource.Factory(mContext)

            val mediaSourceFactory = DefaultMediaSourceFactory(mediaDataSourceFactory)

            mExoPlayer = ExoPlayer.Builder(mContext)
                .setMediaSourceFactory(mediaSourceFactory)
                .build()

            mExoPlayerEventListener = ExoPlayerEventListener()

            mExoPlayerEventListener?.let {
                mExoPlayer?.addListener(it)
            }
        }
    }

    private fun release() {
        mExoPlayer?.release()
        mExoPlayer = null
    }

    override fun onPlay() {

        mExoPlayer?.let {
            if (!it.playWhenReady) {
                it.playWhenReady = true
                setNewState(PlaybackStateCompat.STATE_PLAYING)
            }
        }
    }

    override fun onPause() {
        mExoPlayer?.let {
            if (it.playWhenReady) {
                it.playWhenReady = false
                setNewState(PlaybackStateCompat.STATE_PAUSED)
            }
        }
    }

    override fun playFromMedia(metadata: MediaMetadataCompat) {
        startTrackingPlayback()
        playFile(metadata)
    }

    override val isPlaying: Boolean
        get() = mExoPlayer != null && mExoPlayer?.playWhenReady == true

    override fun onStop() {
        // Regardless of whether or not the ExoPlayer has been created / started, the state must
        // be updated, so that MediaNotificationManager can take down the notification.
        printLogD(TAG, "onStop: stopped")
        setNewState(PlaybackStateCompat.STATE_STOPPED)
        release()
    }

    override fun seekTo(position: Long) {
        mExoPlayer?.let {
            it.seekTo(position.toInt().toLong())

            // Set the state (to the current state) because the position changed and should
            // be reported to clients.
            setNewState(mState)
        }
    }

    override fun setVolume(volume: Float) {
        mExoPlayer?.volume = volume
    }

    private fun playFile(metaData: MediaMetadataCompat) {
        val mediaId = metaData.description.mediaId
        var mediaChanged = currentMedia == null || mediaId != currentMedia?.description?.mediaId
        if (mCurrentMediaPlayedToCompletion) {
            // Last audio file was played to completion, the resourceId hasn't changed, but the
            // player was released, so force a reload of the media file for playback.
            mediaChanged = true
            mCurrentMediaPlayedToCompletion = false
        }
        if (!mediaChanged) {
            if (!isPlaying) {
                play()
            }
            return
        } else {
            release()
        }
        currentMedia = metaData
        initializeExoPlayer()
        try {
            val mediaSource = ProgressiveMediaSource.Factory(DefaultHttpDataSource.Factory())
                .createMediaSource(MediaItem.fromUri(Uri.parse(metaData.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI))))

            mExoPlayer?.prepare(mediaSource)

            printLogD(TAG, "onPlayerStateChanged: PREPARE")
        } catch (e: Exception) {
            throw RuntimeException(
                "Failed to play media uri: " +
                    metaData.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI),
                e
            )
        }
        play()
    }

    private fun startTrackingPlayback() {
        val handler = Handler(Looper.getMainLooper())
        val runnable: Runnable = object : Runnable {
            override fun run() {
                if (isPlaying) {
                    mExoPlayer?.contentPosition?.let {
                        mExoPlayer?.duration?.let { it1 ->
                            mPlaybackInfoListener.onSeekTo(
                                it, it1
                            )
                        }
                    }
                    handler.postDelayed(this, 100)
                }
                if (mExoPlayer?.contentPosition!! >= mExoPlayer?.duration!! &&
                    mExoPlayer?.duration!! > 0
                ) {
                    mPlaybackInfoListener.onPlaybackComplete()
                }
            }
        }
        handler.postDelayed(runnable, 100)
    }

    // This is the main reducer for the player state machine.
    private fun setNewState(@PlaybackStateCompat.State newPlayerState: Int) {
        mState = newPlayerState

        // Whether playback goes to completion, or whether it is stopped, the
        // mCurrentMediaPlayedToCompletion is set to true.
        if (mState == PlaybackStateCompat.STATE_STOPPED) {
            mCurrentMediaPlayedToCompletion = true
        }
        val reportPosition = if (mExoPlayer == null) 0 else mExoPlayer?.currentPosition

        // Send playback state information to service
        reportPosition?.let { publishStateBuilder(it) }
    }

    private fun publishStateBuilder(reportPosition: Long) {
        val stateBuilder = PlaybackStateCompat.Builder()
        stateBuilder.setActions(availableActions)
        stateBuilder.setState(
            mState,
            reportPosition,
            1.0f,
            SystemClock.elapsedRealtime()
        )
        mPlaybackInfoListener.onPlaybackStateChange(stateBuilder.build())
        mPlaybackInfoListener.updateUI(currentMedia?.description?.mediaId)
    }

    /**
     * Set the current capabilities available on this session. Note: If a capability is not
     * listed in the bitmask of capabilities then the MediaSession will not handle it. For
     * example, if you don't want ACTION_STOP to be handled by the MediaSession, then don't
     * included it in the bitmask that's returned.
     */
    @get:PlaybackStateCompat.Actions
    private val availableActions: Long
        get() {
            var actions = (
                PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID
                    or PlaybackStateCompat.ACTION_PLAY_FROM_SEARCH
                    or PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                    or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
                )
            actions = when (mState) {
                PlaybackStateCompat.STATE_STOPPED -> actions or (
                    PlaybackStateCompat.ACTION_PLAY
                        or PlaybackStateCompat.ACTION_PAUSE
                    )
                PlaybackStateCompat.STATE_PLAYING -> actions or (
                    PlaybackStateCompat.ACTION_STOP
                        or PlaybackStateCompat.ACTION_PAUSE
                        or PlaybackStateCompat.ACTION_SEEK_TO
                    )
                PlaybackStateCompat.STATE_PAUSED -> actions or (
                    PlaybackStateCompat.ACTION_PLAY
                        or PlaybackStateCompat.ACTION_STOP
                    )
                else -> actions or (
                    PlaybackStateCompat.ACTION_PLAY
                        or PlaybackStateCompat.ACTION_PLAY_PAUSE
                        or PlaybackStateCompat.ACTION_STOP
                        or PlaybackStateCompat.ACTION_PAUSE
                    )
            }
            return actions
        }

    private inner class ExoPlayerEventListener : Player.Listener {
        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            when (playbackState) {
                Player.STATE_ENDED -> {
                    setNewState(PlaybackStateCompat.STATE_PAUSED)
                }
                Player.STATE_BUFFERING -> {
                    printLogD(TAG, "onPlayerStateChanged: BUFFERING")
                    mStartTime = System.currentTimeMillis()
                }
                Player.STATE_IDLE -> {}
                Player.STATE_READY -> {
                    printLogD(TAG, "onPlayerStateChanged: READY")
                    printLogD(
                        TAG,
                        "onPlayerStateChanged: TIME ELAPSED: " + (System.currentTimeMillis() - mStartTime)
                    )
                }
            }
        }

        override fun onRepeatModeChanged(repeatMode: Int) {}
        override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {}
        override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) {}
    }

    companion object {
        private const val TAG = "MediaPlayerAdapter"
    }

    init {
        mContext = context.applicationContext
        mPlaybackInfoListener = playbackInfoListener
    }
}
