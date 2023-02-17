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
package com.justmusic.ui.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.justmusic.domain.CoroutineDispatcherProvider
import com.justmusic.domain.models.SongContent
import com.justmusic.domain.usecase.GetSongsListUserCase
import com.justmusic.shared.AppConstants.HOME_SCREEN_SONGS_LIMIT
import com.justmusic.shared.ResponseHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * This is a ViewModel class which hold data for Songs related in stateFlow[StateFlow]
 * @param coroutineDispatcherProvider Required to pass Coroutines dispatcher[CoroutineDispatcherProvider]
 * @param getSongsListUserCase Required to pass specific use case which is coupled with this
 * view model [GetSongsListUserCase]
 */
@HiltViewModel
class SongsListingScreenViewModel @Inject constructor(
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider,
    private val getSongsListUserCase: GetSongsListUserCase,
) : ViewModel() {

    private val _mutableStateFlowSongsListing =
        MutableStateFlow<ResponseHandler<List<SongContent>>>(ResponseHandler.Loading())
    val stateFlowSongsListing: StateFlow<ResponseHandler<List<SongContent>>> =
        _mutableStateFlowSongsListing

    init {
        fetchSongsList(isRequiredToRefreshData = false, count = HOME_SCREEN_SONGS_LIMIT)
    }

    /**
     * This is a method which execute to load data from Server and used to store in local,Also update
     * State based on API Response either success or failure
     * @param isRequiredToRefreshData if it is true local data will be erased and API will call for
     * get new data from server.
     * @param count Required to pass count of records which required to fetch.
     */
    fun fetchSongsList(isRequiredToRefreshData: Boolean, count: Int) {
        _mutableStateFlowSongsListing.value = ResponseHandler.Loading()
        viewModelScope.launch(coroutineDispatcherProvider.IO()) {
            _mutableStateFlowSongsListing.value = getSongsListUserCase.execute(isRequiredToRefreshData, count)
        }
    }
}
