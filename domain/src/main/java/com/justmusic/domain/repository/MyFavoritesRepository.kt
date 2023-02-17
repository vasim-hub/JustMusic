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
package com.justmusic.domain.repository

import com.justmusic.domain.models.AlbumContent
import com.justmusic.domain.models.SongContent
import com.justmusic.shared.MyFavoriteItemTypeEnum
import com.justmusic.shared.ResponseHandler

/**
 * This is an interface which use to perform favorites song or album related operation
 */
interface MyFavoritesRepository {
    /**
     * This is a abstract behaviours to load my favorite Songs.
     * @return [ResponseHandler] Return ResponseHandler with generic type data.
     */
    suspend fun getMyFavoritesListSongs(): ResponseHandler<List<SongContent>>

    /**
     * This is a abstract behaviours to load my favorite Albums.
     * @return [ResponseHandler] Return ResponseHandler with generic type data.
     */
    suspend fun getMyFavoritesListAlbums(): ResponseHandler<List<AlbumContent>>

    /**
     * This is a abstract behaviours to add song or album in local DB.
     * @param itemId Item id of either Song or Album.
     * @param myFavoriteItemTypeEnum Item type either Song or Album.More details [MyFavoriteItemTypeEnum]
     * @return [ResponseHandler] Return ResponseHandler with generic type data.
     */
    suspend fun addInFavorite(itemId:Long,myFavoriteItemTypeEnum: MyFavoriteItemTypeEnum) : ResponseHandler<Long>

    /**
     * This is a abstract behaviours to remove item from my favorite.
     * @param itemId Item id of either Song or Album.
     * @return [ResponseHandler] Return ResponseHandler with generic type data.
     */
    suspend fun removeFromFavorite(itemId: Long): ResponseHandler<Int>
}