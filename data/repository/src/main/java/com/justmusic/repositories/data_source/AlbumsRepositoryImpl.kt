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
import com.justmusic.domain.models.AlbumContent
import com.justmusic.domain.repository.AlbumsRepository
import com.justmusic.network.ApiService
import com.justmusic.network.utils.BaseAPIRequest
import com.justmusic.repositories.mappers.toAlbumDomain
import com.justmusic.repositories.mappers.toAlbumEntity
import com.justmusic.shared.AppConstants
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
class AlbumsRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    appDatabase: AppDatabase
) : BaseAPIRequest(), AlbumsRepository {

    private val albumsDao = appDatabase.albumDao()
    private val favoritesDao = appDatabase.myFavoritesDao()

    override suspend fun fetchAlbumsListing(
        isRequiredRefreshData: Boolean,
        count: Int
    ): ResponseHandler<List<AlbumContent>> {
        if (isRequiredRefreshData) {
            albumsDao.deleteAllAlbums()
        }
        val albumsFromLocal = albumsDao.getCount()
        if (albumsFromLocal == 0) {
            val response = safeApiCall(
                call = { apiService.getAlbums(AppConstants.LIMIT_FOR_API) }
            )
            if (response is ResponseHandler.Success) {
                // Save Data to SQLite
                response.data.feed?.entry?.map { entryFromServer ->
                    entryFromServer?.toAlbumEntity()
                        ?.let { it1 -> albumsDao.insert(it1) }
                }
            } else if (response is ResponseHandler.Error) {
                return ResponseHandler.Error(response.error, response.httpStatusEnum)
            }
        }
        val data = albumsDao.getAll(count,0).map {
            it.toAlbumDomain(favoritesDao.isItemExistInTable(it.albumId))
        }
        return ResponseHandler.Success(data)
    }
}