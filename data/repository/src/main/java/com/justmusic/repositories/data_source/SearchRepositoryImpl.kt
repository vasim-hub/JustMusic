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
import com.justmusic.cache.models.RecentSearchEntity
import com.justmusic.domain.models.AlbumContent
import com.justmusic.domain.models.RecentSearchContent
import com.justmusic.domain.models.SearchContent
import com.justmusic.domain.models.SongContent
import com.justmusic.domain.repository.SearchRepository
import com.justmusic.network.ApiService
import com.justmusic.network.utils.BaseAPIRequest
import com.justmusic.repositories.mappers.toAlbumDomain
import com.justmusic.repositories.mappers.toRecentSearchDomain
import com.justmusic.repositories.mappers.toSongDomain
import com.justmusic.shared.AppContentTypeEnum
import com.justmusic.shared.HttpStatusEnum
import com.justmusic.shared.ResponseHandler
import com.justmusic.shared.model.AdvanceSearchData
import com.justmusic.shared.model.AdvanceSearchParamTypesData
import javax.inject.Inject

/**
 * This is a repository implementation class where data is fetched from server and store
 * in local database along with data modify operation can be performed
 * This method is execute in background (Worker) thread.
 * @param apiService Required to pass Retrofit API service class [ApiService]
 * @param appDatabase Required to pass app database[AppDatabase]
 * @return [ResponseHandler]
 */
class SearchRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    appDatabase: AppDatabase
) : BaseAPIRequest(), SearchRepository {

    private val songsDao = appDatabase.songsDao()
    private val albumDao = appDatabase.albumDao()
    private val searchDao = appDatabase.searchDao()
    private val myFavoritesDao = appDatabase.myFavoritesDao()
    private val recentSearchDao = appDatabase.recentSearchDao()

    override suspend fun searchContent(advanceSearchParamTypesData: AdvanceSearchParamTypesData): ResponseHandler<SearchContent> {
        val searchSongsData = searchDao.getSongsByAdvanceSearch(
            advanceSearchParamTypesData.searchText,
            advanceSearchParamTypesData.categoryType,
            advanceSearchParamTypesData.artist,
            advanceSearchParamTypesData.releasedDateStartDate,
            advanceSearchParamTypesData.releasedDateEndDate
        )

        val listOfSongs = mutableListOf<SongContent>()
        if (searchSongsData.isNotEmpty()) {
            searchSongsData.map {
                listOfSongs.add(it.toSongDomain(myFavoritesDao.isItemExistInTable(it.songId)))
            }
        }

        val searchAlbumsData = searchDao.getAlbumsByAdvanceSearch( advanceSearchParamTypesData.searchText,
            advanceSearchParamTypesData.categoryType,
            advanceSearchParamTypesData.artist,
            advanceSearchParamTypesData.releasedDateStartDate,
            advanceSearchParamTypesData.releasedDateEndDate)

        val listOfAlbums = mutableListOf<AlbumContent>()
        if (searchAlbumsData.isNotEmpty()) {
            searchAlbumsData.map {
                listOfAlbums.add(it.toAlbumDomain(myFavoritesDao.isItemExistInTable(it.albumId)))
            }
        }

        val searchContent = SearchContent(listOfAlbums, listOfSongs)
        return ResponseHandler.Success(searchContent)
    }

    override suspend fun getAllRecentSearches(): ResponseHandler<List<RecentSearchContent>> {
        val listOfRecentSearchEntities = recentSearchDao.getAll()
        val listOfRecentSearch = mutableListOf<RecentSearchContent>()
        listOfRecentSearchEntities.map {
            if (it.itemType == AppContentTypeEnum.ALBUMS.name) {
                val albumData = albumDao.getAlbumById(it.itemId)
                albumData?.let { albumEntity ->
                    listOfRecentSearch.add(
                        it.toRecentSearchDomain(
                            albumEntity.name,
                            "<b>Album:</b> "+ albumEntity.artist,
                            albumEntity.cover
                        )
                    )
                }
            }

            if (it.itemType == AppContentTypeEnum.SONGS.name) {
                val songData = songsDao.getSongById(it.itemId)
                songData?.let { songEntity ->
                    listOfRecentSearch.add(
                        it.toRecentSearchDomain(
                            songEntity.name,
                            "<b>Song:</b> "+ songEntity.artist,
                            songEntity.cover
                        )
                    )
                }
            }

        }
        return ResponseHandler.Success(listOfRecentSearch)
    }

    override suspend fun addInRecentSearch(
        itemId: Long,
        appContentTypeEnum: AppContentTypeEnum
    ): ResponseHandler<Long> {

        if (recentSearchDao.isRecentSearchExistInTable(
                itemId,
                appContentTypeEnum.name
            )
        ) {
            return ResponseHandler.Error(
                "Item already exist",
                HttpStatusEnum.ITEM_ALREADY_EXIST_IN_LOCAL_DB
            )
        }
        val rowId = recentSearchDao.insert(RecentSearchEntity(itemId, appContentTypeEnum.name))
        return ResponseHandler.Success(rowId)
    }

    override suspend fun clearRecentSearchById(itemId: Long): ResponseHandler<Int> {
        val rowId = recentSearchDao.deleteRecentSearchById(itemId)
        return ResponseHandler.Success(rowId)
    }

    override suspend fun clearALLRecentSearches(): ResponseHandler<Unit> {
        recentSearchDao.deleteAllRecentSearch()
        return ResponseHandler.Success(Unit)
    }

    override suspend fun getAdvanceSearchData(): ResponseHandler<AdvanceSearchData> {
        return ResponseHandler.Success(searchDao.getAdvanceSearchData())
    }
}