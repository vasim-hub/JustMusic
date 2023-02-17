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
package com.justmusic.ui.search.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.justmusic.domain.CoroutineDispatcherProvider
import com.justmusic.domain.models.RecentSearchContent
import com.justmusic.domain.models.SearchContent
import com.justmusic.domain.usecase.SearchDataUserCase
import com.justmusic.shared.AppContentTypeEnum
import com.justmusic.shared.ResponseHandler
import com.justmusic.shared.model.AdvanceSearchData
import com.justmusic.shared.model.AdvanceSearchParamTypesData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * This is a ViewModel class which hold data for Songs related in stateFlow[StateFlow]
 * @param coroutineDispatcherProvider Required to pass Coroutines dispatcher[CoroutineDispatcherProvider]
 * @param searchDataUserCase Required to pass specific use case which is coupled with this
 * view model [SearchDataUserCase]
 */
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider,
    private val searchDataUserCase: SearchDataUserCase,
) : ViewModel() {

    private val _mutableStateFlowSearchContent =
        MutableStateFlow<ResponseHandler<SearchContent>>(ResponseHandler.Loading())
    val stateFlowSearchContent: StateFlow<ResponseHandler<SearchContent>> =
        _mutableStateFlowSearchContent

    private val _mutableStateFlowRecentSearchListing =
        MutableStateFlow<ResponseHandler<List<RecentSearchContent>>>(ResponseHandler.Loading())
    val stateFlowRecentSearchListing: StateFlow<ResponseHandler<List<RecentSearchContent>>> =
        _mutableStateFlowRecentSearchListing

    private val _mutableStateFlowAdvanceSearchData =
        MutableStateFlow<ResponseHandler<AdvanceSearchData>>(ResponseHandler.Loading())
    val stateFlowAdvanceSearchData: StateFlow<ResponseHandler<AdvanceSearchData>> =
        _mutableStateFlowAdvanceSearchData

    private var _mutableStateFlowAddedItemInRecentSearch =
        MutableStateFlow<ResponseHandler<Long>>(ResponseHandler.Loading())
    val stateFlowRecentAddedItemInRecentSearch: StateFlow<ResponseHandler<Long>> =
        _mutableStateFlowAddedItemInRecentSearch

    fun searchContent(advanceSearchParamTypesData: AdvanceSearchParamTypesData) {
        _mutableStateFlowSearchContent.value = ResponseHandler.Loading()
        viewModelScope.launch(coroutineDispatcherProvider.IO()) {
            _mutableStateFlowSearchContent.value = searchDataUserCase.execute(advanceSearchParamTypesData)
        }
    }

    fun addItemInRecentSearch(itemId: Long, appContentTypeEnum: AppContentTypeEnum) {
        _mutableStateFlowAddedItemInRecentSearch.value = ResponseHandler.Loading()
        viewModelScope.launch(coroutineDispatcherProvider.IO()) {
            _mutableStateFlowAddedItemInRecentSearch.value = searchDataUserCase.executeAddRecentSearches(itemId, appContentTypeEnum)
        }
    }

    fun getALlRecentSearches() {
        viewModelScope.launch(coroutineDispatcherProvider.IO()) {
            _mutableStateFlowRecentSearchListing.value = searchDataUserCase.executeGetALlRecentSearches()
        }
    }

    fun clearRecentSearchById(itemId: Long) {
        viewModelScope.launch(coroutineDispatcherProvider.IO()) {
            searchDataUserCase.executeClearRecentSearchById(itemId)
        }
    }

    fun clearAllRecentSearches() {
        viewModelScope.launch(coroutineDispatcherProvider.IO()) {
            searchDataUserCase.executeClearALlRecentSearches()
        }
    }

    fun getAdvanceSearchData() {
        viewModelScope.launch(coroutineDispatcherProvider.IO()) {
            _mutableStateFlowAdvanceSearchData.value = searchDataUserCase.executeForGetAdvanceSearchData()
        }
    }
}
