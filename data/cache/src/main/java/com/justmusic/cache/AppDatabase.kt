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
package com.justmusic.cache

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.justmusic.cache.converters.DateConverter
import com.justmusic.cache.daos.*
import com.justmusic.cache.models.AlbumEntity
import com.justmusic.cache.models.MyFavoriteEntity
import com.justmusic.cache.models.RecentSearchEntity
import com.justmusic.cache.models.SongEntity

/**
 * Create Room database with require entities
 **/
@Database(
    entities = [SongEntity::class,AlbumEntity::class,MyFavoriteEntity::class,RecentSearchEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun songsDao(): SongDao
    abstract fun albumDao(): AlbumsDao
    abstract fun myFavoritesDao(): MyFavoritesDao
    abstract fun searchDao(): SearchDao
    abstract fun recentSearchDao(): RecentSearchDao
}