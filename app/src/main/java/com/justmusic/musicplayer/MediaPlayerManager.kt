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
package com.justmusic.musicplayer

import android.os.Bundle
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.view.View
import com.justmusic.JustMusicApplication
import com.justmusic.MainActivity
import com.justmusic.R
import com.justmusic.cache.local.PreferencesHelper
import com.justmusic.databinding.ActivityMainBinding
import com.justmusic.musicplayer.client.MediaBrowserHelper
import com.justmusic.musicplayer.client.MediaBrowserHelperContractor
import com.justmusic.musicplayer.services.MediaService
import com.justmusic.shared.MusicPlayerConstants
import com.justmusic.ui.draggablefragment.MediaControllerFragment
import com.justmusic.utils.PrintLog

/**
 * This is a helper class which can use to communicated between Player controller and song change
 * @param activity Required to Activity reference
 * @param activityMainBinding Required to pass binding view
 * @param justMusicApplication Required to pass reference of Main Application class[JustMusicApplication]
 * @param preferencesHelper Required to pass local preference reference [PreferencesHelper]
 */
class MediaPlayerManager(
    val activity: MainActivity,
    private val activityMainBinding: ActivityMainBinding,
    val justMusicApplication: JustMusicApplication?,
    val preferencesHelper: PreferencesHelper?
) : MediaPlayerContractor, MediaBrowserHelperContractor {
    private var mIsPlaying = false
    private val mediaControllerFragment = MediaControllerFragment.newInstance()
    private var mMediaBrowserHelper: MediaBrowserHelper? = null

    private var mOnAppOpen = false
    init {
        mMediaBrowserHelper = MediaBrowserHelper(activity, MediaService::class.java)
        mMediaBrowserHelper?.setMediaBrowserHelperCallback(this)
        setupPlayScreenFragment()
    }

    override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
        PrintLog.printLogD(TAG, "onMetadataChanged: called")
        metadata?.let {
            mediaControllerFragment.setMediaTitle(metadata)
        }
    }

    override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
        PrintLog.printLogD(TAG, "onPlaybackStateChanged: called.")
        mIsPlaying = state != null &&
            state.state == PlaybackStateCompat.STATE_PLAYING

        // update UI
        mediaControllerFragment.setIsPlaying(mIsPlaying)
    }

    private fun setupPlayScreenFragment() {
        activity.supportFragmentManager
            .beginTransaction()
            .replace(
                R.id.frameLayoutMediaControllerContainer,
                mediaControllerFragment,
                MediaControllerFragment.TAG
            )
            .commitAllowingStateLoss()
    }

    override fun onMediaControllerConnected(mediaController: MediaControllerCompat?) {}

    override fun onMediaSelected(
        playlistId: String?,
        mediaItem: MediaMetadataCompat?,
        position: Int
    ) {
        if (mediaItem != null) {
            PrintLog.printLogD(
                TAG,
                "onMediaSelected: CALLED: " + mediaItem.description.mediaId
            )
            val bundle = Bundle()
            bundle.putInt(MusicPlayerConstants.MEDIA_QUEUE_POSITION, position)
            bundle.putBoolean(
                MusicPlayerConstants.QUEUE_NEW_PLAYLIST,
                true
            ) // let the player know this is a new playlist
            mMediaBrowserHelper?.apply {
                playlistId?.let { subscribeToNewPlaylist(it) }
                transportControls?.playFromMediaId(
                    mediaItem.description.mediaId,
                    bundle
                )
            }
            mOnAppOpen = true
            showPlayer()
        }
    }

    override fun playPause() {
        if (mOnAppOpen) {
            if (mIsPlaying) {
                mMediaBrowserHelper?.transportControls?.pause()
            } else {
                mMediaBrowserHelper?.transportControls?.play()
            }
        } else {
            if (preferencesHelper?.playlistId != "") {
                preferencesHelper?.queuePosition?.let {
                    onMediaSelected(
                        preferencesHelper.playlistId,
                        justMusicApplication?.getMediaItem(preferencesHelper.lastPlayedMedia),
                        it
                    )
                }
            }
        }
    }

    private fun showPlayer() {
        activityMainBinding.frameLayoutMediaControllerContainer.visibility = View.VISIBLE
    }
    fun playNext() {
        mMediaBrowserHelper?.transportControls?.skipToNext()
    }

    fun playPrevious() {
        mMediaBrowserHelper?.transportControls?.skipToPrevious()
    }

    fun onStart() {
        mMediaBrowserHelper?.onStart()
    }

    fun onStop() {
        PrintLog.printLogD(TAG, "onStop: called.")
        mMediaBrowserHelper?.onStop()
    }

    fun isPlayerExpanded(): Boolean {
        if (mediaControllerFragment.binding.rootLayout.currentState == R.id.play_screen_expanded_normal) {
            mediaControllerFragment.binding.rootLayout.transitionToStart()
            return true
        }
        return false
    }

    companion object {
        private const val TAG = "MediaPlayerManager"
    }
}
