package com.justmusic.cache.datafactory

import com.justmusic.cache.models.AlbumEntity
import com.justmusic.cache.models.MyFavoriteEntity
import com.justmusic.cache.models.RecentSearchEntity
import com.justmusic.cache.models.SongEntity
import com.justmusic.shared.MyFavoriteItemTypeEnum
import kotlin.random.Random

val testMyFavoritesData = arrayListOf(
    MyFavoriteEntity(1, MyFavoriteItemTypeEnum.MY_FAVORITE_SONG.name),
    MyFavoriteEntity(2, MyFavoriteItemTypeEnum.MY_FAVORITE_ALBUM.name),
    MyFavoriteEntity(3, MyFavoriteItemTypeEnum.MY_FAVORITE_SONG.name)
)

val myFavoriteSingleItem = testMyFavoritesData[2]

val testAlbumsData = arrayListOf(
    AlbumEntity(
        Random.nextLong(),
        "A Family Christmas",
        "A Family Christmas - Andrea Bocelli, Matteo Bocelli & Virginia Bocelli",
        "Andrea Bocelli, Matteo Bocelli & Virginia Bocelli",
        "Album Cover",
        "Holiday",
        "2012-05-15T05:00:00-07:00",
        "Album formatted data"
    ),
    AlbumEntity(
        Random.nextLong(),
        "Bell Bottom Country",
        "Bell Bottom Country - Lainey Wilson",
        "Lainey Wilson",
        "Album Cover",
        "Country",
        "2015-09-22T05:00:00-07:00",
        "Album formatted data"
    ),
    AlbumEntity(
        Random.nextLong(),
        "Christmas",
        "Christmas - Michael Bublé",
        "Michael Bublé",
        "Album Cover",
        "Holiday",
        "2020-03-10T05:00:00-07:00",
        "Album formatted data"
    ),
)

val selectedAlbumData = testAlbumsData[2]

val testSongData = arrayListOf(
    SongEntity(
        Random.nextLong(),
        "All I Want For Christmas Is You",
        "All I Want For Christmas Is You - Mariah Carey",
        "Song Link",
        "Mariah Carey",
        "Song Cover",
        30000,
        "Song formatted data",
        "Christmas",
        "2017-06-11T05:00:00-07:00",
        "Song formatted data"
    ),
    SongEntity(
        Random.nextLong(),
        "Watermelon Moonshine",
        "Watermelon Moonshine - Lainey Wilson",
        "Song Link",
        "Lainey Wilson",
        "Song Cover",
        50000,
        "Song formatted data",
        "Country",
        "2013-10-04T05:00:00-07:00",
        "Song formatted data"
    ),
    SongEntity(
        Random.nextLong(),
        "Unholy",
        "Unholy - Sam Smith & Kim Petras",
        "Song Link",
        "Song Artist",
        "Song Cover",
        80000,
        "Song formatted data",
        "Pop",
        "2014-07-13T05:00:00-07:00",
        "Song formatted data"
    ),
)

val selectedSongData = testSongData[0]

val testRecentSearchData = arrayListOf(
    RecentSearchEntity(selectedAlbumData.albumId, MyFavoriteItemTypeEnum.MY_FAVORITE_ALBUM.name),
    RecentSearchEntity(selectedSongData.songId, MyFavoriteItemTypeEnum.MY_FAVORITE_SONG.name),
)
