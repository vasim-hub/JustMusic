package com.justmusic.musicplayer

import android.support.v4.media.MediaMetadataCompat

/**
 * This is a Media player callback handler
 */
interface MediaPlayerContractor {
    fun playPause()
    fun onMediaSelected(playlistId: String?, mediaItem: MediaMetadataCompat?, position: Int)
}
