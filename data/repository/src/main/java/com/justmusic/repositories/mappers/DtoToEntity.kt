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
package com.justmusic.repositories.mappers

import com.justmusic.cache.models.AlbumEntity
import com.justmusic.cache.models.SongEntity
import com.justmusic.network.models.AlbumsResponse
import com.justmusic.network.models.SongsResponse
import com.justmusic.repositories.utils.DateTimeUtil

/**
 * This is mapper util method used to map from Remote entity to local storage entity
 */
internal fun SongsResponse.Feed.Entry.toSongEntity(): SongEntity {
    val image = this.imImage?.maxByOrNull { it?.attributes?.height ?: 0 }
    val link = this.link?.firstOrNull { it?.attributes?.type == "audio/x-m4a" }
    val itemId = this.id?.attributes?.imId
    return SongEntity(
        songId = itemId ?: 0L,
        name = this.imName?.label ?: "",
        title = this.title?.label ?: "",
        link = link?.attributes?.href ?: "",
        duration = link?.imDuration?.label ?: 0,
        formattedDuration = DateTimeUtil.getFormattedDuration(link?.imDuration?.label),
        artist = this.imArtist?.label ?: "",
        cover = image?.label,
        category = this.category?.attributes?.term ?: "",
        releaseDate = this.imReleaseDate?.label ?: "",
        formattedReleaseDate = this.imReleaseDate?.attributes?.label ?: "",
    )
}

internal fun AlbumsResponse.Feed.Entry.toAlbumEntity(): AlbumEntity {
    val image = this.imImage?.maxByOrNull { it?.attributes?.height ?: 0 }
    val itemId = this.id?.attributes?.imId
    return AlbumEntity(
        albumId = itemId ?: 0L,
        name = this.imName?.label ?: "",
        title = this.title?.label ?: "",
        artist = this.imArtist?.label ?: "",
        cover = image?.label,
        category = this.category?.attributes?.term ?: "",
        releaseDate = this.imReleaseDate?.label ?: "",
        formattedReleaseDate = this.imReleaseDate?.attributes?.label ?: "",
    )
}
