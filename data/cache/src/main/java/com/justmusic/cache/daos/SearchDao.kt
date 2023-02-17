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
import androidx.room.Transaction
import com.justmusic.cache.models.AlbumEntity
import com.justmusic.cache.models.SongEntity
import com.justmusic.cache.utils.TableConstantsName.TABLE_NAME_ALBUMS
import com.justmusic.cache.utils.TableConstantsName.TABLE_NAME_SONGS
import com.justmusic.shared.model.AdvanceSearchData
import com.justmusic.shared.model.AlbumsAdvanceSearchData
import com.justmusic.shared.model.SongsAdvanceSearchData

/**
 * Search Dao, Here Write all your db query
 */
@Dao
abstract class SearchDao {

    @Query("SELECT * FROM $TABLE_NAME_ALBUMS WHERE name LIKE :searchQueryText COLLATE NOCASE || '%' AND category LIKE '%' || :categoryType COLLATE NOCASE || '%' AND artist LIKE  :artist COLLATE NOCASE || '%' AND releaseDate BETWEEN :startDate AND :endDate order by name COLLATE NOCASE")
    abstract fun getAlbumsByAdvanceSearch(searchQueryText: String, categoryType:String, artist:String, startDate:String, endDate:String): List<AlbumEntity>

   @Query("SELECT * FROM $TABLE_NAME_SONGS WHERE name LIKE :searchQueryText COLLATE NOCASE || '%' AND category LIKE '%' || :categoryType COLLATE NOCASE || '%' AND artist LIKE  :artist COLLATE NOCASE || '%' AND releaseDate BETWEEN :startDate AND :endDate order by name COLLATE NOCASE")
   abstract fun getSongsByAdvanceSearch(searchQueryText: String, categoryType:String, artist:String, startDate:String, endDate:String): List<SongEntity>

    @Query("SELECT category from $TABLE_NAME_SONGS GROUP BY category COLLATE NOCASE")
    abstract fun getCategoriesFromSongs(): List<String>

    @Query("SELECT artist from $TABLE_NAME_SONGS GROUP BY artist COLLATE NOCASE")
    abstract fun getArtiestFromSongs(): List<String>

    @Query("SELECT category from $TABLE_NAME_ALBUMS GROUP BY category COLLATE NOCASE")
    abstract fun getCategoriesFromAlbums(): List<String>

    @Query("SELECT artist from $TABLE_NAME_ALBUMS GROUP BY artist COLLATE NOCASE")
    abstract fun getArtiestFromAlbums(): List<String>

    @Transaction
    open fun getAdvanceSearchData(): AdvanceSearchData {
        val albumsAdvanceSearchData = AlbumsAdvanceSearchData(getCategoriesFromAlbums(),getArtiestFromAlbums())
        val songsAdvanceSearchData = SongsAdvanceSearchData(getCategoriesFromSongs(),getArtiestFromSongs())
        return AdvanceSearchData(albumsAdvanceSearchData,songsAdvanceSearchData)
    }
}
