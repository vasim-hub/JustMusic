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

import com.justmusic.domain.models.RecentSearchContent
import com.justmusic.domain.models.SearchContent
import com.justmusic.domain.repository.SearchRepository
import com.justmusic.shared.AppContentTypeEnum
import com.justmusic.shared.ResponseHandler
import com.justmusic.shared.model.AdvanceSearchData
import com.justmusic.shared.model.AdvanceSearchParamTypesData
import javax.inject.Inject

/**
 * This is a use case which is used ad intermediate to consume and produce data from data layer
 * to presentation layer
 * @param searchRepository Required to pass specific use case's repository [SearchRepository]
 */
class SearchDataUserCase @Inject constructor(
    private val searchRepository: SearchRepository
) {

    suspend fun execute(
        advanceSearchParamTypesData: AdvanceSearchParamTypesData
    ): ResponseHandler<SearchContent> {
        return searchRepository.searchContent(advanceSearchParamTypesData)
    }

    suspend fun executeGetALlRecentSearches(): ResponseHandler<List<RecentSearchContent>> {
        return searchRepository.getAllRecentSearches()
    }

    suspend fun executeAddRecentSearches(
        itemId: Long,
        appContentTypeEnum: AppContentTypeEnum
    ): ResponseHandler<Long> {
        return searchRepository.addInRecentSearch(itemId, appContentTypeEnum)
    }

    suspend fun executeClearRecentSearchById(itemId: Long): ResponseHandler<Int> {
        return searchRepository.clearRecentSearchById(itemId)
    }

    suspend fun executeClearALlRecentSearches(): ResponseHandler<Unit> {
        return searchRepository.clearALLRecentSearches()
    }

    suspend fun executeForGetAdvanceSearchData(): ResponseHandler<AdvanceSearchData> {
        return searchRepository.getAdvanceSearchData()
    }
}