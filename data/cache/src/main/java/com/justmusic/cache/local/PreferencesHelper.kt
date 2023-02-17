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
package com.justmusic.cache.local

import android.content.Context
import android.content.SharedPreferences
import com.justmusic.shared.MusicPlayerConstants
import javax.inject.Inject
/**
 * Preference helper class which used to store data in local private storage
 * @param context Required to pass context[Context]
 */
@Suppress("unused")
class PreferencesHelper @Inject constructor(private val context: Context) {
    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences(
            "justmusic_pref",
            Context.MODE_PRIVATE
        )
    }

    fun putString(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    fun getString(key: String, default: String?): String? {
        return sharedPreferences.getString(key, default)
    }

    val playlistId: String?
        get() = sharedPreferences.getString(MusicPlayerConstants.PLAYLIST_ID, "")

    fun savePlaylistId(playlistId: String?) {
        val editor = sharedPreferences.edit()
        editor.putString(MusicPlayerConstants.PLAYLIST_ID, playlistId)
        editor.apply()
    }

    fun saveQueuePosition(position: Int) {
        val editor = sharedPreferences.edit()
        editor.putInt(MusicPlayerConstants.MEDIA_QUEUE_POSITION, position)
        editor.apply()
    }

    val queuePosition: Int
        get() = sharedPreferences.getInt(MusicPlayerConstants.MEDIA_QUEUE_POSITION, -1)

    fun saveLastPlayedArtistImage(url: String?) {
        val editor = sharedPreferences.edit()
        editor.putString(MusicPlayerConstants.LAST_ARTIST_IMAGE, url)
        editor.apply()
    }

    val lastPlayedArtistImage: String?
        get() = sharedPreferences.getString(MusicPlayerConstants.LAST_ARTIST_IMAGE, "")
    val lastPlayedArtist: String?
        get() = sharedPreferences.getString(MusicPlayerConstants.LAST_ARTIST, "")
    val lastCategory: String?
        get() = sharedPreferences.getString(MusicPlayerConstants.LAST_CATEGORY, "")

    fun saveLastPlayedMedia(mediaId: String?) {
        val editor = sharedPreferences.edit()
        editor.putString(MusicPlayerConstants.NOW_PLAYING, mediaId)
        editor.apply()
    }

    val lastPlayedMedia: String?
        get() = sharedPreferences.getString(MusicPlayerConstants.NOW_PLAYING, "")

    fun saveLastPlayedCategory(category: String?) {
        val editor = sharedPreferences.edit()
        editor.putString(MusicPlayerConstants.LAST_CATEGORY, category)
        editor.apply()
    }

    fun saveLastPlayedArtist(artist: String?) {
        val editor = sharedPreferences.edit()
        editor.putString(MusicPlayerConstants.LAST_ARTIST, artist)
        editor.apply()
    }
}