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
package com.justmusic.ui.search.fragments

import android.os.Bundle
import android.view.View
import com.justmusic.R
import com.justmusic.base.BaseFragment
import com.justmusic.databinding.FragmentCommonItemListingBinding
import com.justmusic.domain.models.SongContent
import com.justmusic.shared.AppContentTypeEnum
import com.justmusic.ui.search.fragments.SearchFragment.Companion.songsFragmentCreated
import com.justmusic.ui.song.adapter.SongsAdapter
import dagger.hilt.android.AndroidEntryPoint

/**
 *Search songs fragment
 */
@AndroidEntryPoint
class SearchSongsFragment :
    BaseFragment(R.layout.fragment_common_item_listing) {

    private var _binding: FragmentCommonItemListingBinding? = null
    private val binding get() = _binding!!
    private val songsAdapter: SongsAdapter by lazy {
        SongsAdapter(isSearchRequiredToEnable = true, hideMoreOptionsMenu = true, onItemClicked = ::onSongItemClicked)
    }

    private fun onSongItemClicked(position: Int, songContent: SongContent) {
        notifyParentFragmentItemAddedInRecentSearch(songContent.id, AppContentTypeEnum.SONGS)
        handleSongClickEvent(position, songContent, songsAdapter)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCommonItemListingBinding.bind(view)
        disableSwipeRefreshLayout(binding.swipeRefreshLayout)
        setErrorMessageOnUI()
    }

    override fun setMenuVisibility(isvisible: Boolean) {
        super.setMenuVisibility(isvisible)
        if (isvisible) {
            if (!songsFragmentCreated) {
                songsFragmentCreated = true
                SearchFragment.searchContent?.let {
                    SearchFragment.searchContent?.listSongsContent?.let { it1 -> updateData(it1) }
                }
            }
        }
    }

    fun updateData(list: List<SongContent>) {
        if (list.isEmpty()) {
            noDataUI()
        } else {
            hideLoading()
            showSongsListing(list)
        }
    }

    fun clearAdapterData() {
        songsAdapter.resetAdapter()
    }

    override fun showLoading() {
        binding.apply {
            shimmerLayout.visibility = View.VISIBLE
            shimmerLayout.startShimmer()
            recyclerviewCommonItemListing.visibility = View.GONE
            includeNoFavorites.clNoData.visibility = View.GONE
        }
    }

    override fun hideLoading() {
        binding.apply {
            shimmerLayout.visibility = View.GONE
            recyclerviewCommonItemListing.visibility = View.VISIBLE
            includeNoFavorites.clNoData.visibility = View.GONE
            shimmerLayout.stopShimmer()
        }
    }

    override fun onRetry() {}

    private fun noDataUI() {
        binding.apply {
            shimmerLayout.visibility = View.GONE
            recyclerviewCommonItemListing.visibility = View.GONE
            includeNoFavorites.clNoData.visibility = View.VISIBLE
        }
    }

    private fun setErrorMessageOnUI() {
        binding.apply {
            includeNoFavorites.imageViewNoDataMessage.setImageResource(R.drawable.ic_search)
            includeNoFavorites.txNoDataShortMessage.text = getString(R.string.no_songs_found)
            includeNoFavorites.txNoDataLongMessage.text =
                getString(R.string.search_song_default_message)
        }
    }

    // We can implement paging library here
    private fun showSongsListing(songsListing: List<SongContent>) {
        binding.apply {
            songsAdapter.listOfSongsContent = songsListing.toMutableList()
            recyclerviewCommonItemListing.adapter = songsAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
