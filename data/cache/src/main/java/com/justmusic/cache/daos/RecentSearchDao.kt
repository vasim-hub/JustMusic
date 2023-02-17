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
import com.justmusic.cache.models.RecentSearchEntity
import com.justmusic.cache.utils.TableConstantsName.TABLE_NAME_RECENT_SEARCH
import com.justmusic.cache.utils.base.AbstractBaseEntityDao

/**
 * Recent Search Dao, Here Write all your db query
 */
@Dao
abstract class RecentSearchDao : AbstractBaseEntityDao<RecentSearchEntity>() {

    @Query("select * from $TABLE_NAME_RECENT_SEARCH order by updated_at DESC")
    abstract fun getAll(): List<RecentSearchEntity>

    @Query("SELECT EXISTS (SELECT 1 FROM $TABLE_NAME_RECENT_SEARCH WHERE itemId = :itemId and itemType = :itemType )")
    abstract fun isRecentSearchExistInTable(itemId: Long,itemType: String): Boolean

    @Query("DELETE FROM $TABLE_NAME_RECENT_SEARCH WHERE itemId = :itemId")
    abstract fun deleteRecentSearchById(itemId: Long) : Int

    @Query("DELETE FROM $TABLE_NAME_RECENT_SEARCH")
    abstract fun deleteAllRecentSearch()
}
