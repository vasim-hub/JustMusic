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
import com.justmusic.cache.models.RecentSearchEntity
import com.justmusic.cache.models.SongEntity
import com.justmusic.domain.models.AlbumContent
import com.justmusic.domain.models.RecentSearchContent
import com.justmusic.domain.models.SongContent

/**
 * This is mapper util method used to map from entity to domain entity
 */
internal fun SongEntity.toSongDomain(isFavoriteItem: Boolean): SongContent {
    return SongContent(
        id = this.songId,
        name = this.name,
        title = this.title,
        link = this.link,
        duration = this.duration,
        formattedDuration = this.formattedDuration,
        artist = this.artist,
        cover = this.cover,
        isFavorite = isFavoriteItem
    )
}

internal fun AlbumEntity.toAlbumDomain(isFavoriteItem: Boolean): AlbumContent {
    return AlbumContent(
        id = this.albumId,
        name = this.name,
        title = this.title,
        artist = this.artist,
        cover = this.cover,
        isFavorite = isFavoriteItem
    )
}

internal fun RecentSearchEntity.toRecentSearchDomain(name: String,subTitle: String, coverUrl: String?): RecentSearchContent {
    return RecentSearchContent(
        itemId = this.itemId,
        itemType = this.itemType,
        name = name,
        subTitle = subTitle,
        coverUrl = coverUrl,
    )
}