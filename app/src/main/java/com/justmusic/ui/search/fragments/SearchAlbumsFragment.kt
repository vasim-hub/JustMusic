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
import androidx.recyclerview.widget.GridLayoutManager
import com.justmusic.R
import com.justmusic.base.BaseFragment
import com.justmusic.databinding.FragmentCommonItemListingBinding
import com.justmusic.domain.models.AlbumContent
import com.justmusic.shared.AppContentTypeEnum
import com.justmusic.ui.album.adapter.AlbumAdapter
import dagger.hilt.android.AndroidEntryPoint

/**
 *Search album fragment
 */
@AndroidEntryPoint
class SearchAlbumsFragment :
    BaseFragment(R.layout.fragment_common_item_listing) {

    private var _binding: FragmentCommonItemListingBinding? = null
    private val binding get() = _binding!!

    private val albumAdapter: AlbumAdapter by lazy {
        AlbumAdapter(isSearchRequiredToEnable = true, hideMoreOptionsMenu = true, onItemClicked = ::onAlbumItemClicked)
    }

    private fun onAlbumItemClicked(position: Int, albumContent: AlbumContent) {
        notifyParentFragmentItemAddedInRecentSearch(albumContent.id, AppContentTypeEnum.ALBUMS)
        handleOnAlbumClicked(position, albumContent)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCommonItemListingBinding.bind(view)
        disableSwipeRefreshLayout(binding.swipeRefreshLayout)
        setErrorMessageOnUI()
    }

    fun updateData(list: List<AlbumContent>) {
        if (list.isEmpty()) {
            noDataUI()
        } else {
            hideLoading()
            showAlbumsListing(list)
        }
    }

    fun clearAdapterData() {
        albumAdapter.resetAdapter()
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
            includeNoFavorites.txNoDataShortMessage.text = getString(R.string.no_albums_found)
            includeNoFavorites.txNoDataLongMessage.text = getString(R.string.search_album_default_message)
        }
    }

    override fun onRetry() {}

    // We can implement paging library here
    private fun showAlbumsListing(albumsListing: List<AlbumContent>) {
        binding.apply {
            albumAdapter.listOfAlbumsContent = albumsListing.toMutableList()
            recyclerviewCommonItemListing.apply {
                recyclerviewCommonItemListing.apply {
                    layoutManager =
                        GridLayoutManager(requireContext(), 2)
                    adapter = albumAdapter
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
