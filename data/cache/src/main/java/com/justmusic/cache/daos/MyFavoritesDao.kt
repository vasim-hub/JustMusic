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
import com.justmusic.cache.models.MyFavoriteEntity
import com.justmusic.cache.utils.TableConstantsName.TABLE_NAME_MY_FAVORITES
import com.justmusic.cache.utils.base.AbstractBaseEntityDao

/**
 * My Favorites Albums or Songs Dao, Here Write all your db query
 */
@Dao
abstract class MyFavoritesDao : AbstractBaseEntityDao<MyFavoriteEntity>() {
    @Query("select count(*) from $TABLE_NAME_MY_FAVORITES")
    abstract fun getCount(): Int

    @Query("select * from $TABLE_NAME_MY_FAVORITES where itemType = :itemType")
    abstract fun getMyFavoritesByItemType(itemType: String): List<MyFavoriteEntity>

    @Query("DELETE FROM $TABLE_NAME_MY_FAVORITES WHERE itemId = :itemId")
    abstract fun deleteMyFavoriteById(itemId: Long) : Int

    @Query("SELECT EXISTS (SELECT 1 FROM $TABLE_NAME_MY_FAVORITES WHERE itemId = :itemId)")
    abstract fun isItemExistInTable(itemId: Long): Boolean
}