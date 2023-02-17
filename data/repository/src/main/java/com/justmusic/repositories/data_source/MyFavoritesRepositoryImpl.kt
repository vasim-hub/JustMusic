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
package com.justmusic.repositories.data_source

import com.justmusic.cache.AppDatabase
import com.justmusic.cache.models.MyFavoriteEntity
import com.justmusic.domain.models.AlbumContent
import com.justmusic.domain.models.SongContent
import com.justmusic.domain.repository.MyFavoritesRepository
import com.justmusic.network.ApiService
import com.justmusic.network.utils.BaseAPIRequest
import com.justmusic.repositories.mappers.toAlbumDomain
import com.justmusic.repositories.mappers.toSongDomain
import com.justmusic.shared.HttpStatusEnum
import com.justmusic.shared.MyFavoriteItemTypeEnum
import com.justmusic.shared.ResponseHandler
import javax.inject.Inject

/**
 * This is a repository implementation class where data is fetched from server and store
 * in local database along with data modify operation can be performed
 * This method is execute in background (Worker) thread.
 * @param apiService Required to pass Retrofit API service class [ApiService]
 * @param appDatabase Required to pass app database[AppDatabase]
 * @return [ResponseHandler]
 */
class MyFavoritesRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    appDatabase: AppDatabase
) : BaseAPIRequest(), MyFavoritesRepository {

    private val myFavoritesDao = appDatabase.myFavoritesDao()
    private val songDao = appDatabase.songsDao()
    private val albumsDao = appDatabase.albumDao()

    override suspend fun getMyFavoritesListSongs(): ResponseHandler<List<SongContent>> {
        val listMyFavorite =
            myFavoritesDao.getMyFavoritesByItemType(MyFavoriteItemTypeEnum.MY_FAVORITE_SONG.name)
        val listOfSongs = mutableListOf<SongContent>()
        if (listMyFavorite.isNotEmpty()) {

            listMyFavorite.map {
                songDao.getSongById(it.itemId)?.toSongDomain(isFavoriteItem = true)
                    ?.let { it1 -> listOfSongs.add(it1) }
            }
            return ResponseHandler.Success(listOfSongs)
        }
        return ResponseHandler.Error(
            "No data",
            HttpStatusEnum.NO_DATA_IN_LOCAL_DB
        )
    }

    override suspend fun getMyFavoritesListAlbums(): ResponseHandler<List<AlbumContent>> {
        val listMyFavorite =
            myFavoritesDao.getMyFavoritesByItemType(MyFavoriteItemTypeEnum.MY_FAVORITE_ALBUM.name)
        val listOfAlbums = mutableListOf<AlbumContent>()
        if (listMyFavorite.isNotEmpty()) {
            listMyFavorite.map {
                albumsDao.getAlbumById(it.itemId)?.toAlbumDomain(isFavoriteItem = true)
                    ?.let { it1 -> listOfAlbums.add(it1) }
            }
            return ResponseHandler.Success(listOfAlbums)
        }
        return ResponseHandler.Error(
            "No data",
            HttpStatusEnum.NO_DATA_IN_LOCAL_DB
        )
    }

    override suspend fun addInFavorite(
        itemId: Long,
        myFavoriteItemTypeEnum: MyFavoriteItemTypeEnum
    ): ResponseHandler<Long> {
        val myFavoriteEntity = MyFavoriteEntity(itemId, myFavoriteItemTypeEnum.name)
        val rowId = myFavoritesDao.insert(myFavoriteEntity)
        if (rowId > 0) {
            return ResponseHandler.Success(rowId)
        }
        return ResponseHandler.Error(
            "Failed to Insert Local DB",
            HttpStatusEnum.FAILED_TO_INSERT_ITEM_IN_LOCAL
        )
    }

    override suspend fun removeFromFavorite(itemId: Long): ResponseHandler<Int> {
        val rowId = myFavoritesDao.deleteMyFavoriteById(itemId)
        if (rowId > 0) {
            return ResponseHandler.Success(rowId)
        }
        return ResponseHandler.Error(
            "Failed to Delete Local DB",
            HttpStatusEnum.FAILED_TO_DELETE_ITEM_IN_LOCAL
        )
    }
}