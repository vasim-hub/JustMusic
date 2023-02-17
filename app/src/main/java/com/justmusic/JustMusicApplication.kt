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
package com.justmusic

import android.app.Application
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import dagger.hilt.android.HiltAndroidApp
import java.util.TreeMap
import kotlin.collections.ArrayList

/**
 * Main Application class with Hilt dagger configuration
 */
@HiltAndroidApp
class JustMusicApplication : Application() {
    private val mMediaItems: MutableList<MediaBrowserCompat.MediaItem> = ArrayList()
    val treeMap = TreeMap<String?, MediaMetadataCompat>()
    val mediaItems: List<MediaBrowserCompat.MediaItem>
        get() = mMediaItems

    fun setMediaItems(mediaItems: List<MediaMetadataCompat>) {
        mMediaItems.clear()
        for (item in mediaItems) {
            mMediaItems.add(
                MediaBrowserCompat.MediaItem(
                    item.description, MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
                )
            )
            treeMap[item.description.mediaId] = item
        }
    }

    fun getMediaItem(mediaId: String?): MediaMetadataCompat? {
        return treeMap[mediaId]
    }
}
