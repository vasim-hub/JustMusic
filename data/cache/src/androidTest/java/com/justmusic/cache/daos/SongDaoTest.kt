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
import com.justmusic.cache.datafactory.selectedSongData
import com.justmusic.cache.datafactory.testSongData
import com.justmusic.cache.utils.MainCoroutineRuleForCache
import com.justmusic.cache.utils.runBlockingTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class SongDaoTest {
    private lateinit var database: AppDatabase
    private lateinit var songDao: SongDao

    @get:Rule
    val coroutineRule = MainCoroutineRuleForCache()

    @Before
    fun setup() = coroutineRule.runBlockingTest {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).build()

        songDao = database.songsDao()
        insertAllItems()
    }

    private fun insertAllItems() = coroutineRule.runBlockingTest {
        songDao.insertAll(testSongData)
    }

    @Test
    fun testSongExistById() = coroutineRule.runBlockingTest {
        val songEntity =
            songDao.getSongById(selectedSongData.songId)
        assertThat(songEntity).isNotNull()
    }

    @Test
    fun testTotalRecordsExistCount() = coroutineRule.runBlockingTest {
        val totalRecordsExistCount =
            songDao.getAll(testSongData.size, 0)
        assertThat(totalRecordsExistCount.size).isEqualTo(testSongData.size)
    }

    @After
    fun tearDown() {
        database.close()
    }
}