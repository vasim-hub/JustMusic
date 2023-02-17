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
package com.justmusic.ui.favorite.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.justmusic.domain.CoroutineDispatcherProvider
import com.justmusic.domain.models.AlbumContent
import com.justmusic.domain.models.SongContent
import com.justmusic.domain.usecase.MyFavoritesUserCase
import com.justmusic.shared.MyFavoriteItemTypeEnum
import com.justmusic.shared.ResponseHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * This is a ViewModel class which hold data for Songs related in stateFlow[StateFlow]
 * @param coroutineDispatcherProvider Required to pass Coroutines dispatcher[CoroutineDispatcherProvider]
 * @param myFavoritesUserCase Required to pass specific use case which is coupled with this
 * view model [MyFavoritesUserCase]
 */
@HiltViewModel
class MyFavoritesScreenViewModel @Inject constructor(
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider,
    private val myFavoritesUserCase: MyFavoritesUserCase,
) : ViewModel() {

    private val _mutableStateFlowSongsListing =
        MutableStateFlow<ResponseHandler<List<SongContent>>>(ResponseHandler.Loading())
    val stateFlowSongsListing: StateFlow<ResponseHandler<List<SongContent>>> =
        _mutableStateFlowSongsListing

    private val _mutableStateFlowAlbumsListing =
        MutableStateFlow<ResponseHandler<List<AlbumContent>>>(ResponseHandler.Loading())
    val stateFlowAlbumsListing: StateFlow<ResponseHandler<List<AlbumContent>>> =
        _mutableStateFlowAlbumsListing

    /**
     * This is a method which execute to load data for favorites songs
     */
    fun fetchMyFavoritesSongsList() {
        _mutableStateFlowSongsListing.value = ResponseHandler.Loading()
        viewModelScope.launch(coroutineDispatcherProvider.IO()) {
            _mutableStateFlowSongsListing.value = myFavoritesUserCase.executeForMyFavoriteSongs()
        }
    }

    /**
     * This is a method which execute to load data for favorites albums
     */
    fun fetchMyFavoritesAlbumsList() {
        _mutableStateFlowAlbumsListing.value = ResponseHandler.Loading()
        viewModelScope.launch(coroutineDispatcherProvider.IO()) {
            _mutableStateFlowAlbumsListing.value = myFavoritesUserCase.executeForMyFavoriteAlbums()
        }
    }

    /**
     * This is a method which execute to add item as favorite
     */
    fun addFavorite(itemId: Long, myFavoriteItemTypeEnum: MyFavoriteItemTypeEnum) {
        viewModelScope.launch(coroutineDispatcherProvider.IO()) {
            myFavoritesUserCase.executeForAddFavorite(itemId, myFavoriteItemTypeEnum)
        }
    }

    /**
     * This is a method which execute to remove item from favorite
     */
    fun removeFavorite(itemId: Long) {
        viewModelScope.launch(coroutineDispatcherProvider.IO()) {
            myFavoritesUserCase.executeForRemoveFavorite(itemId)
        }
    }
}
