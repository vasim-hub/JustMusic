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
package com.justmusic.cache.utils.base

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.justmusic.cache.converters.DateConverter
import java.io.Serializable
import java.util.Date

/**
 * Base Entity for Creating and Managing common columns in local DB
 */
abstract class BaseEntity : Serializable {
    @PrimaryKey(autoGenerate = true)
    open var id: Long = 0

    @ColumnInfo(name = "created_at", defaultValue = "CURRENT_TIMESTAMP")
    @TypeConverters(
        DateConverter::class
    )
    var createdAt: Date? = null

    @ColumnInfo(name = "updated_at", defaultValue = "CURRENT_TIMESTAMP")
    @TypeConverters(
        DateConverter::class
    )
    var updatedAt: Date? = null
}