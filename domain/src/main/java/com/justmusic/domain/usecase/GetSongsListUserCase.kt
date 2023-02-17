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

import com.justmusic.domain.models.SongContent
import com.justmusic.domain.repository.SongsRepository
import com.justmusic.shared.ResponseHandler
import javax.inject.Inject

/**
 * This is a use case which is used ad intermediate to consume and produce data from data layer
 * to presentation layer
 * @param songsRepository Required to pass specific use case's repository [SongsRepository]
 */
class GetSongsListUserCase @Inject constructor(
    private val songsRepository: SongsRepository
) {

    /**
     * This is a method which execute to load data from Server and used to store in local.
     * This method is execute in background (Worker) thread.
     * @param isRequiredToRefreshData if it is true local data will be erased and API will call for
     * get new data from server.
     * @param count Required to pass count of records which required to fetch.
     * @return [ResponseHandler]
     */
    suspend fun execute(
        isRequiredToRefreshData: Boolean,
        count: Int
    ): ResponseHandler<List<SongContent>> {
        return songsRepository.fetchSongsListing(isRequiredToRefreshData, count)
    }
}