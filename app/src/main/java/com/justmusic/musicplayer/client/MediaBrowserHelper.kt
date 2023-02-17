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
package com.justmusic.musicplayer.client

import android.content.ComponentName
import android.content.Context
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.media.MediaBrowserServiceCompat
import com.justmusic.utils.PrintLog.printLogD

/**
 * This is a Media browse helper class which use to establish connection and disconnection
 * for [MediaBrowserCompat]
 * to presentation layer
 * @param mContext Required to pass the context
 * @param mMediaBrowserServiceClass Required to pass Media browser service class
 */
class MediaBrowserHelper(
    private val mContext: Context,
    private val mMediaBrowserServiceClass: Class<out MediaBrowserServiceCompat?>
) {
    private var mMediaBrowser: MediaBrowserCompat? = null
    private var mMediaController: MediaControllerCompat? = null
    private val mMediaBrowserConnectionCallback: MediaBrowserConnectionCallback
    private val mMediaBrowserSubscriptionCallback: MediaBrowserSubscriptionCallback
    private val mMediaControllerCallback: MediaControllerCallback
    private var mMediaBrowserCallback: MediaBrowserHelperContractor? = null

    fun setMediaBrowserHelperCallback(callback: MediaBrowserHelperContractor?) {
        mMediaBrowserCallback = callback
    }

    // Receives callbacks from the MediaController and updates the UI state,
    // i.e.: Which is the current item, whether it's playing or paused, etc.
    private inner class MediaControllerCallback : MediaControllerCompat.Callback() {
        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            printLogD(TAG, "onMetadataChanged: CALLED")
            mMediaBrowserCallback?.onMetadataChanged(metadata)
        }

        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            printLogD(TAG, "onPlaybackStateChanged: CALLED")
            mMediaBrowserCallback?.onPlaybackStateChanged(state)
        }

        // This might happen if the MusicService is killed while the Activity is in the
        // foreground and onStart() has been called (but not onStop()).
        override fun onSessionDestroyed() {
            onPlaybackStateChanged(null)
        }
    }

    fun subscribeToNewPlaylist(currentPlaylistId: String) {
        mMediaBrowser?.unsubscribe(currentPlaylistId)
        mMediaBrowser?.subscribe(currentPlaylistId, mMediaBrowserSubscriptionCallback)
    }

    fun onStart() {
        if (mMediaBrowser == null) {
            mMediaBrowser = MediaBrowserCompat(
                mContext,
                ComponentName(mContext, mMediaBrowserServiceClass),
                mMediaBrowserConnectionCallback,
                null
            )
            mMediaBrowser?.connect()
        }
        printLogD(TAG, "onStart: CALLED: Creating MediaBrowser, and connecting")
    }

    fun onStop() {
        mMediaController?.unregisterCallback(mMediaControllerCallback)
        mMediaController = null

        mMediaBrowser?.let {
            if (it.isConnected) {
                it.disconnect()
                mMediaBrowser = null
            }
        }
        printLogD(TAG, "onStop: CALLED: Releasing MediaController, Disconnecting from MediaBrowser")
    }

    // Receives callbacks from the MediaBrowser when it has successfully connected to the
    // MediaBrowserService (MusicService).
    private inner class MediaBrowserConnectionCallback : MediaBrowserCompat.ConnectionCallback() {
        // Happens as a result of onStart().
        override fun onConnected() {
            printLogD(TAG, "onConnected: CALLED")
            try {
                // Get a MediaController for the MediaSession.
                mMediaController =
                    mMediaBrowser?.sessionToken?.let { MediaControllerCompat(mContext, it) }
                mMediaController?.registerCallback(mMediaControllerCallback)
            } catch (e: Exception) {
                printLogD(TAG, String.format("onConnected: Problem: %s", e.toString()))
                throw RuntimeException(e)
            }
            mMediaBrowser?.let {
                it.subscribe(it.root, mMediaBrowserSubscriptionCallback)
            }

            printLogD(TAG, "onConnected: CALLED: subscribing to: " + mMediaBrowser?.root)
            mMediaBrowserCallback?.onMediaControllerConnected(mMediaController)
        }
    }

    // Receives callbacks from the MediaBrowser when the MediaBrowserService has loaded new media
    // that is ready for playback.
    inner class MediaBrowserSubscriptionCallback : MediaBrowserCompat.SubscriptionCallback() {
        override fun onChildrenLoaded(
            parentId: String,
            children: List<MediaBrowserCompat.MediaItem>
        ) {
            printLogD(TAG, "onChildrenLoaded: CALLED: $parentId, $children")
            for (mediaItem in children) {
                printLogD(TAG, "onChildrenLoaded: CALLED: queue item: " + mediaItem.mediaId)
                mMediaController?.addQueueItem(mediaItem.description)
            }
        }
    }

    val transportControls: MediaControllerCompat.TransportControls?
        get() {
            if (mMediaController == null) {
                printLogD(TAG, "getTransportControls: MediaController is null!")
                throw IllegalStateException("MediaController is null!")
            }
            return mMediaController?.transportControls
        }

    companion object {
        private const val TAG = "MediaBrowserHelper"
    }

    init {
        mMediaBrowserConnectionCallback = MediaBrowserConnectionCallback()
        mMediaBrowserSubscriptionCallback = MediaBrowserSubscriptionCallback()
        mMediaControllerCallback = MediaControllerCallback()
    }
}
