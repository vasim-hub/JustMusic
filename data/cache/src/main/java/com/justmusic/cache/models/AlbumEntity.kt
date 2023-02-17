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
package com.justmusic.cache.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.justmusic.cache.utils.TableConstantsName.TABLE_NAME_ALBUMS
import com.justmusic.cache.utils.base.BaseEntity

/**
 * Album Table
 */
@Entity(tableName = TABLE_NAME_ALBUMS)
data class AlbumEntity(
    @ColumnInfo
    val albumId: Long,

    @ColumnInfo
    val name: String,

    @ColumnInfo
    val title: String,

    @ColumnInfo
    val artist: String,

    @ColumnInfo
    val cover: String? = null,

    @ColumnInfo
    val category: String,

    @ColumnInfo
    val releaseDate: String,

    @ColumnInfo
    val formattedReleaseDate: String
) : BaseEntity()
