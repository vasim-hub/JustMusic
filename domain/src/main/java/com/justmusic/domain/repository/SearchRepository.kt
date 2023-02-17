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

import com.justmusic.domain.models.RecentSearchContent
import com.justmusic.domain.models.SearchContent
import com.justmusic.shared.AppContentTypeEnum
import com.justmusic.shared.ResponseHandler
import com.justmusic.shared.model.AdvanceSearchData
import com.justmusic.shared.model.AdvanceSearchParamTypesData

/**
 * This is an interface which use to perform search related operation
 */
interface SearchRepository {
    /**
     * This is a abstract behaviours search data from Albums and Songs and will return result.
     */
    suspend fun searchContent(
        advanceSearchParamTypesData: AdvanceSearchParamTypesData
    ): ResponseHandler<SearchContent>

    /**
     *Get all recent searches
     */
    suspend fun getAllRecentSearches(): ResponseHandler<List<RecentSearchContent>>

    /**
     * Add item in recent search
     */
    suspend fun addInRecentSearch(
        itemId: Long,
        appContentTypeEnum: AppContentTypeEnum
    ): ResponseHandler<Long>

    /**
     * Clear recent search by id
     */
    suspend fun clearRecentSearchById(itemId: Long): ResponseHandler<Int>

    /**
     * Clear all items from recent search
     */
    suspend fun clearALLRecentSearches(): ResponseHandler<Unit>

    /**
     * Get search data for advance search dialog
     */
    suspend fun getAdvanceSearchData(): ResponseHandler<AdvanceSearchData>
}