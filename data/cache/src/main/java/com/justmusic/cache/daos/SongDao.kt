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
package com.justmusic.cache.daos

import androidx.room.Dao
import androidx.room.Query
import com.justmusic.cache.models.SongEntity
import com.justmusic.cache.utils.TableConstantsName.TABLE_NAME_SONGS
import com.justmusic.cache.utils.base.AbstractBaseEntityDao

/**
 * Song Dao, Here Write all your db query
 */
@Dao
abstract class SongDao : AbstractBaseEntityDao<SongEntity>() {
    @Query("select count(*) from $TABLE_NAME_SONGS")
    abstract fun getCount(): Int

    @Query("select * from $TABLE_NAME_SONGS LIMIT :limit OFFSET :offset")
    abstract fun getAll(limit: Int,offset: Int): List<SongEntity>

    @Query("select * from $TABLE_NAME_SONGS WHERE songId = :itemId")
    abstract fun getSongById(itemId: Long): SongEntity?

    @Query("DELETE FROM $TABLE_NAME_SONGS")
    abstract fun deleteAllSongs()
}
