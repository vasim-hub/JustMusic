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
package com.justmusic.shared

/**
 * This class is useful define Constants which required to use at multiple modules
 */
object AppConstants {
    const val HOME_SCREEN_SONGS_LIMIT = 10
    const val HOME_SCREEN_ALBUMS_LIMIT = 10
    const val LIMIT_FOR_API = 100
    const val ADVANCE_SEARCH_ANY_TIME_YEARS_BACK_FROM_CURRENT_VALUE = -20
}

object MusicPlayerConstants {
    const val PLAYLIST_ID = "PLAYLIST_ID"
    const val NOW_PLAYING = "NOW_PLAYING"
    const val MEDIA_QUEUE_POSITION = "MEDIA_QUEUE_POSITION"
    const val SEEK_BAR_PROGRESS = "SEEK_BAR_PROGRESS"
    const val QUEUE_NEW_PLAYLIST = "QUEUE_NEW_PLAYLIST"
    const val LAST_CATEGORY = "LAST_CATEGORY"
    const val LAST_ARTIST = "LAST_ARTIST"
    const val LAST_ARTIST_IMAGE = "LAST_ARTIST_IMAGE"
    const val SEEK_BAR_MAX = "SEEK_BAR_MAX"
}