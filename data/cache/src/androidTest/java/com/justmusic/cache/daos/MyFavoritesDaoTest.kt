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

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.justmusic.cache.AppDatabase
import com.justmusic.cache.datafactory.myFavoriteSingleItem
import com.justmusic.cache.datafactory.testMyFavoritesData
import com.justmusic.cache.utils.MainCoroutineRuleForCache
import com.justmusic.cache.utils.runBlockingTest
import com.justmusic.shared.MyFavoriteItemTypeEnum
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class MyFavoritesDaoTest {
    private lateinit var database: AppDatabase
    private lateinit var favoritesDao: MyFavoritesDao

    @get:Rule
    val coroutineRule = MainCoroutineRuleForCache()

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).build()

        favoritesDao = database.myFavoritesDao()
        insertAllItems()
    }

    private fun insertAllItems() = coroutineRule.runBlockingTest {
        favoritesDao.insertAll(testMyFavoritesData)
    }

    @Test
    fun testMyFavoriteAlbumExist() = coroutineRule.runBlockingTest {
        val listOfRecords =
            favoritesDao.getMyFavoritesByItemType(MyFavoriteItemTypeEnum.MY_FAVORITE_ALBUM.name)
        assertThat(listOfRecords).isNotEmpty()
    }

    @Test
    fun testMyFavoriteSongExist() = coroutineRule.runBlockingTest {
        val listOfRecords =
            favoritesDao.getMyFavoritesByItemType(MyFavoriteItemTypeEnum.MY_FAVORITE_SONG.name)
        assertThat(listOfRecords).isNotEmpty()
    }

    @Test
    fun testCheckItemExistOrNotById() = coroutineRule.runBlockingTest {
        val isItemExistById =
            favoritesDao.isItemExistInTable(myFavoriteSingleItem.itemId)

        val isItemNotExistById =
            favoritesDao.isItemExistInTable(10)

        assertThat(isItemExistById).isTrue()
        assertThat(isItemNotExistById).isFalse()
    }

    @Test
    fun testDeleteItemById() = coroutineRule.runBlockingTest {
        val deletedItemId =
            favoritesDao.deleteMyFavoriteById(myFavoriteSingleItem.itemId)
        assertThat(deletedItemId).isEqualTo(1)
    }

    @After
    fun tearDown() {
        database.close()
    }
}