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
package com.justmusic.ui.favorite.fragments

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.justmusic.R
import com.justmusic.base.AlbumAndSongsListingBaseFragment
import com.justmusic.databinding.FragmentCommonItemListingBinding
import com.justmusic.domain.models.SongContent
import com.justmusic.shared.HttpStatusEnum
import com.justmusic.shared.ResponseHandler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 *My favorites songs fragment
 */
@AndroidEntryPoint
class MyFavoriteSongsFragment :
    AlbumAndSongsListingBaseFragment(R.layout.fragment_common_item_listing),
    SwipeRefreshLayout.OnRefreshListener {

    private var _binding: FragmentCommonItemListingBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCommonItemListingBinding.bind(view)
        registerAPIObserver()
        setErrorMessageOnUI()
        initListener()
        callAPIForData()
    }

    private fun callAPIForData() {
        myFavoritesScreenViewModel.fetchMyFavoritesSongsList()
    }

    private fun registerAPIObserver() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    myFavoritesScreenViewModel.stateFlowSongsListing.collect { uiState ->
                        when (uiState) {
                            is ResponseHandler.Error -> {
                                hideLoading()
                                if (uiState.httpStatusEnum == HttpStatusEnum.NO_DATA_IN_LOCAL_DB) {
                                    noDataUI()
                                } else {
                                    showError(uiState.error, uiState.httpStatusEnum)
                                }
                            }
                            is ResponseHandler.Success<List<SongContent>> -> {
                                hideLoading()
                                showSongsListing(uiState.data)
                            }
                            else -> showLoading()
                        }
                    }
                }
            }
        }
    }

    /** Register fragment event listener */
    private fun initListener() {
        binding.apply {
            swipeRefreshLayout.setOnRefreshListener(this@MyFavoriteSongsFragment)
        }
    }

    override fun listEmptyForFavoritesListing() {
        noDataUI()
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
            includeNoFavorites.txNoDataShortMessage.text = getString(R.string.no_favorites_songs)
            includeNoFavorites.txNoDataLongMessage.text = getString(R.string.music_quotes_b)
        }
    }

    override fun onRetry() {
        onRefresh()
    }

    // We can implement paging library here
    private fun showSongsListing(songsListing: List<SongContent>) {
        binding.apply {
            songsAdapter.listOfSongsContent = songsListing.toMutableList()
            recyclerviewCommonItemListing.adapter = songsAdapter
        }
    }

    override fun onRefresh() {
        binding.swipeRefreshLayout.isRefreshing = false
        callAPIForData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
