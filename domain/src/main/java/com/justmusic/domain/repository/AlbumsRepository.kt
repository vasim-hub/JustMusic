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
import com.justmusic.shared.ResponseHandler

/**
 * This is an interface which use to perform albums related operation
 */
interface AlbumsRepository {
    /**
     * This is a abstract behaviours to load data from Server and used to store in local.
     * @param isRequiredRefreshData if it is true local data will be erased and API will call for
     * get new data from server.
     * @param count Required to pass count of records which required to fetch.
     * @return [ResponseHandler] Return ResponseHandler with generic type data.
     */
    suspend fun fetchAlbumsListing(
        isRequiredRefreshData: Boolean,
        count: Int
    ): ResponseHandler<List<AlbumContent>>
}