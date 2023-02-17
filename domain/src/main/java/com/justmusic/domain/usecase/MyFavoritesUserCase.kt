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
package com.justmusic.domain.usecase

import com.justmusic.domain.models.AlbumContent
import com.justmusic.domain.models.SongContent
import com.justmusic.domain.repository.MyFavoritesRepository
import com.justmusic.shared.MyFavoriteItemTypeEnum
import com.justmusic.shared.ResponseHandler
import javax.inject.Inject

/**
 * This is a use case which is used ad intermediate to consume and produce data from data layer
 * to presentation layer
 * @param myFavoritesRepository Required to pass specific use case's repository [MyFavoritesRepository]
 */
class MyFavoritesUserCase @Inject constructor(
    private val myFavoritesRepository: MyFavoritesRepository
) {

    suspend fun executeForMyFavoriteSongs(): ResponseHandler<List<SongContent>> {
        return myFavoritesRepository.getMyFavoritesListSongs()
    }

    suspend fun executeForMyFavoriteAlbums(): ResponseHandler<List<AlbumContent>> {
        return myFavoritesRepository.getMyFavoritesListAlbums()
    }

    suspend fun executeForAddFavorite(itemId:Long,myFavoriteItemTypeEnum: MyFavoriteItemTypeEnum) : ResponseHandler<Long> {
        return myFavoritesRepository.addInFavorite(itemId,myFavoriteItemTypeEnum)
    }

    suspend fun executeForRemoveFavorite(itemId: Long): ResponseHandler<Int> {
        return myFavoritesRepository.removeFromFavorite(itemId)
    }
}