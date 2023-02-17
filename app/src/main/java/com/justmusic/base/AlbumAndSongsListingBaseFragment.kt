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
package com.justmusic.base

import android.view.LayoutInflater
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.justmusic.R
import com.justmusic.databinding.BottomSheetMoreOptionsBinding
import com.justmusic.domain.models.AlbumContent
import com.justmusic.domain.models.SongContent
import com.justmusic.shared.MyFavoriteItemTypeEnum
import com.justmusic.ui.album.adapter.AlbumAdapter
import com.justmusic.ui.favorite.viewmodel.MyFavoritesScreenViewModel
import com.justmusic.ui.moreoptions.adapter.MoreOptionsAdapter
import com.justmusic.ui.moreoptions.model.MoreOptionItemClickEventData
import com.justmusic.ui.song.adapter.SongsAdapter
import com.justmusic.utils.appenums.MoreMenuClickEventsTypeEnum
import com.justmusic.utils.extensions.showToastMessage

/**
 * This is a Common Listing Base Fragment for Album and Song to Perform Common operation
 * @param id Required to pass layout id
 */
abstract class AlbumAndSongsListingBaseFragment(id: Int) : BaseFragment(id) {
    /**
     * This is a abstract method for checking either list empty or not in Favorites listing
     */
    abstract fun listEmptyForFavoritesListing()

    val myFavoritesScreenViewModel: MyFavoritesScreenViewModel by viewModels()

    val albumAdapter: AlbumAdapter by lazy {
        AlbumAdapter(
            onItemClickedForFavorite = ::handleFavoriteClickFromAlbum,
            onItemClicked = ::onAlbumItemClicked
        )
    }

    private fun onAlbumItemClicked(position: Int, albumContent: AlbumContent) {
        handleOnAlbumClicked(position, albumContent)
    }

    val songsAdapter: SongsAdapter by lazy {
        SongsAdapter(
            onItemClickedForFavorite = ::handleFavoriteClickFromSong,
            onItemClicked = ::onSongItemClicked
        )
    }

    private fun onSongItemClicked(position: Int, songContent: SongContent) {
        handleSongClickEvent(position, songContent, songsAdapter)
    }

    private fun handleFavoriteClickFromAlbum(position: Int, albumContent: AlbumContent) {
        val listOfMoreOptions = mutableListOf<MoreOptionItemClickEventData<AlbumContent>>()
        if (albumContent.isFavorite) {
            listOfMoreOptions.add(
                MoreOptionItemClickEventData(
                    albumContent,
                    position,
                    R.drawable.ic_favorite_blank,
                    getString(R.string.remove_album_my_favorite),
                    MoreMenuClickEventsTypeEnum.CLICK_EVENT_FOR_REMOVE_FAVORITE_ALBUM
                )
            )
        } else {
            listOfMoreOptions.add(
                MoreOptionItemClickEventData(
                    albumContent,
                    position,
                    R.drawable.ic_favorite,
                    getString(R.string.add_album_my_favorite),
                    MoreMenuClickEventsTypeEnum.CLICK_EVENT_FOR_ADD_FAVORITE_ALBUM
                )
            )
        }
        moreOptionsBottomSheet(listOfMoreOptions)
    }

    private fun handleFavoriteClickFromSong(position: Int, songContent: SongContent) {
        val listOfMoreOptions = mutableListOf<MoreOptionItemClickEventData<SongContent>>()

        if (songContent.isFavorite) {
            listOfMoreOptions.add(
                MoreOptionItemClickEventData(
                    songContent,
                    position,
                    R.drawable.ic_favorite_blank,
                    getString(R.string.remove_song_my_favorite),
                    MoreMenuClickEventsTypeEnum.CLICK_EVENT_FOR_REMOVE_FAVORITE_SONG
                )
            )
        } else {
            listOfMoreOptions.add(
                MoreOptionItemClickEventData(
                    songContent,
                    position,
                    R.drawable.ic_favorite,
                    getString(R.string.add_song_my_favorite),
                    MoreMenuClickEventsTypeEnum.CLICK_EVENT_FOR_ADD_FAVORITE_SONG
                )
            )
        }
        moreOptionsBottomSheet(listOfMoreOptions)
    }

    private fun addFavoriteSong(position: Int, songContent: SongContent) {
        myFavoritesScreenViewModel.addFavorite(
            songContent.id,
            MyFavoriteItemTypeEnum.MY_FAVORITE_SONG
        )
        songContent.isFavorite = true
        songsAdapter.updateItem(position, songContent)
        requireActivity().showToastMessage("${songContent.name}  ${getString(R.string.added_as_favorite)}")
    }

    private fun removeFavoriteSong(position: Int, songContent: SongContent) {
        var isFromFavoriteScreen = false
        if (findNavController().currentDestination?.id == R.id.favoritesFragment) {
            isFromFavoriteScreen = true
        }
        myFavoritesScreenViewModel.removeFavorite(songContent.id)
        songContent.isFavorite = false
        if (isFromFavoriteScreen) {
            songsAdapter.removeItem(position)
            if (songsAdapter.listOfSongsContent.isEmpty()) {
                listEmptyForFavoritesListing()
            }
        } else {
            songsAdapter.updateItem(position, songContent)
        }
        requireActivity().showToastMessage("${songContent.name}  ${getString(R.string.removed_as_favorite)}")
    }

    private fun addFavoriteAlbum(
        position: Int,
        albumContent: AlbumContent
    ) {
        myFavoritesScreenViewModel.addFavorite(
            albumContent.id,
            MyFavoriteItemTypeEnum.MY_FAVORITE_ALBUM
        )
        albumContent.isFavorite = true
        albumAdapter.updateItem(position, albumContent)
        requireActivity().showToastMessage("${albumContent.name}  ${getString(R.string.added_as_favorite)}")
    }

    private fun removeFavoriteAlbum(
        position: Int,
        albumContent: AlbumContent
    ) {
        var isFromFavoriteScreen = false
        if (findNavController().currentDestination?.id == R.id.favoritesFragment) {
            isFromFavoriteScreen = true
        }
        myFavoritesScreenViewModel.removeFavorite(albumContent.id)
        albumContent.isFavorite = false
        if (isFromFavoriteScreen) {
            albumAdapter.removeItem(position)
            if (albumAdapter.listOfAlbumsContent.isEmpty()) {
                listEmptyForFavoritesListing()
            }
        } else {
            albumAdapter.updateItem(position, albumContent)
        }
        requireActivity().showToastMessage("${albumContent.name}  ${getString(R.string.removed_as_favorite)}")
    }

    /**
     * This is a method to show menu options with dynamical data and click event
     * @param listOfMoreOptions List of options to show in Bottom Sheet Dialog
     */
    private fun <T> moreOptionsBottomSheet(listOfMoreOptions: MutableList<MoreOptionItemClickEventData<T>>) {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val layoutInflater = LayoutInflater.from(requireContext())
        val bottomSheetMoreOptionsBinding =
            BottomSheetMoreOptionsBinding.inflate(layoutInflater, null, false)
        val moreOptionsAdapter = MoreOptionsAdapter<T> {
            bottomSheetDialog.dismiss()
            handleMoreOptionsClickEvent(it)
        }
        moreOptionsAdapter.listOfMoreOptions = listOfMoreOptions
        bottomSheetMoreOptionsBinding.recyclerview.adapter = moreOptionsAdapter

        bottomSheetDialog.setCancelable(true)
        bottomSheetDialog.setCanceledOnTouchOutside(true)
        bottomSheetDialog.setContentView(bottomSheetMoreOptionsBinding.root)
        bottomSheetDialog.show()
    }

    /**
     * This is a method to handle click event from Bottom Sheet Dialog
     * @param moreOptionItemClickEventData Type of Click event, More details [MoreOptionItemClickEventData]
     */
    private fun <T> handleMoreOptionsClickEvent(moreOptionItemClickEventData: MoreOptionItemClickEventData<T>) {
        when (moreOptionItemClickEventData.moreMenuClickEventsTypeEnum) {
            MoreMenuClickEventsTypeEnum.CLICK_EVENT_FOR_ADD_FAVORITE_SONG -> {
                if (moreOptionItemClickEventData.t is SongContent) {
                    addFavoriteSong(
                        moreOptionItemClickEventData.position,
                        moreOptionItemClickEventData.t as SongContent,
                    )
                }
            }
            MoreMenuClickEventsTypeEnum.CLICK_EVENT_FOR_REMOVE_FAVORITE_SONG -> {
                if (moreOptionItemClickEventData.t is SongContent) {
                    removeFavoriteSong(
                        moreOptionItemClickEventData.position,
                        moreOptionItemClickEventData.t as SongContent,
                    )
                }
            }
            MoreMenuClickEventsTypeEnum.CLICK_EVENT_FOR_ADD_FAVORITE_ALBUM -> {
                if (moreOptionItemClickEventData.t is AlbumContent) {
                    addFavoriteAlbum(
                        moreOptionItemClickEventData.position,
                        moreOptionItemClickEventData.t as AlbumContent,
                    )
                }
            }
            MoreMenuClickEventsTypeEnum.CLICK_EVENT_FOR_REMOVE_FAVORITE_ALBUM -> {
                if (moreOptionItemClickEventData.t is AlbumContent) {
                    removeFavoriteAlbum(
                        moreOptionItemClickEventData.position,
                        moreOptionItemClickEventData.t as AlbumContent,
                    )
                }
            }
        }
    }
}
