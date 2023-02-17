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
package com.justmusic.ui.album.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.justmusic.domain.CoroutineDispatcherProvider
import com.justmusic.domain.models.AlbumContent
import com.justmusic.domain.usecase.GetAlbumsListUserCase
import com.justmusic.shared.AppConstants.HOME_SCREEN_ALBUMS_LIMIT
import com.justmusic.shared.ResponseHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * This is a ViewModel class which hold data for Albums related in stateFlow[StateFlow]
 * @param coroutineDispatcherProvider Required to pass Coroutines dispatcher[CoroutineDispatcherProvider]
 * @param getAlbumsListUserCase Required to pass specific use case which is coupled with this
 * view model [GetAlbumsListUserCase]
 */
@HiltViewModel
class AlbumsManageViewModel @Inject constructor(
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider,
    private val getAlbumsListUserCase: GetAlbumsListUserCase,
) : ViewModel() {

    private val _mutableStateFlowAlbumsListing =
        MutableStateFlow<ResponseHandler<List<AlbumContent>>>(ResponseHandler.Loading())
    val stateFlowAlbumsListing: StateFlow<ResponseHandler<List<AlbumContent>>> =
        _mutableStateFlowAlbumsListing

    init {
        fetchAlbumsList(isRequiredToRefreshData = false, count = HOME_SCREEN_ALBUMS_LIMIT)
    }

    /**
     * This is a method which execute to load data from Server and used to store in local,Also update
     * State based on API Response either success or failure
     * @param isRequiredToRefreshData if it is true local data will be erased and API will call for
     * get new data from server.
     * @param count Required to pass count of records which required to fetch.
     */
    fun fetchAlbumsList(isRequiredToRefreshData: Boolean, count: Int) {
        _mutableStateFlowAlbumsListing.value = ResponseHandler.Loading()
        viewModelScope.launch(coroutineDispatcherProvider.IO()) {
            _mutableStateFlowAlbumsListing.value = getAlbumsListUserCase.execute(isRequiredToRefreshData, count)
        }
    }
}
