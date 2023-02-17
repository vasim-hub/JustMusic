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
package com.justmusic.ui.home.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.justmusic.R
import com.justmusic.base.AlbumAndSongsListingBaseFragment
import com.justmusic.databinding.FragmentHomeBinding
import com.justmusic.domain.models.AlbumContent
import com.justmusic.domain.models.SongContent
import com.justmusic.network.interceptor.NetworkHelper
import com.justmusic.shared.AppConstants
import com.justmusic.shared.ResponseHandler
import com.justmusic.ui.album.viewmodel.AlbumsManageViewModel
import com.justmusic.ui.home.viewmodel.SongsListingScreenViewModel
import com.justmusic.utils.extensions.showSingleButtonDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 *Home fragment
 */
@AndroidEntryPoint
class HomeFragment :
    AlbumAndSongsListingBaseFragment(R.layout.fragment_home),
    SwipeRefreshLayout.OnRefreshListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val albumsManageViewModel: AlbumsManageViewModel by viewModels()
    private val songsListingScreenViewModel: SongsListingScreenViewModel by viewModels()
    @Inject lateinit var networkHelper: NetworkHelper

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)
        setToolbarTitle(binding.includedToolbarHome.txToolbarTitle, getString(R.string.app_name))
        registerAPIObserver()
        initListener()
    }

    private fun registerAPIObserver() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    songsListingScreenViewModel.stateFlowSongsListing.collect { uiState ->
                        when (uiState) {
                            is ResponseHandler.Error -> {
                                hideLoading()
                                showError(uiState.error, uiState.httpStatusEnum)
                            }
                            is ResponseHandler.Success<List<SongContent>> -> {
                                hideLoading()
                                showSongsListing(uiState.data)
                            }
                            else -> showLoading()
                        }
                    }
                }
                launch {
                    albumsManageViewModel.stateFlowAlbumsListing.collect { uiState ->
                        when (uiState) {
                            is ResponseHandler.Error -> {
                                hideLoading()
                                showError(uiState.error, uiState.httpStatusEnum)
                            }
                            is ResponseHandler.Success<List<AlbumContent>> -> {
                                showAlbumsListing(uiState.data)
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
            swipeRefreshLayout.setOnRefreshListener(this@HomeFragment)
        }
    }

    override fun listEmptyForFavoritesListing() {}

    override fun showLoading() {
        binding.apply {
            includedShimmerHome.shimmerLayout.visibility = View.VISIBLE
            includedShimmerHome.shimmerLayout.startShimmer()
            nestedScrollView.visibility = View.GONE
        }
    }

    override fun hideLoading() {
        binding.apply {
            includedShimmerHome.shimmerLayout.visibility = View.GONE
            includedNoInternet.clNoInternet.visibility = View.GONE
            includedShimmerHome.shimmerLayout.stopShimmer()
        }
    }

    override fun onNoInternetConnection() {
        binding.apply {
            includedNoInternet.clNoInternet.visibility = View.VISIBLE
            includedNoInternet.buttonRetry.setOnClickListener { onRetry() }
            nestedScrollView.visibility = View.GONE
        }
    }

    override fun onRetry() {
        bonceAnimationForView(binding.includedNoInternet.txNoInternetTitle)
        if (!networkHelper.isNetworkConnected()) return
        onRefresh()
    }

    // We can implement paging library here
    private fun showAlbumsListing(albumsListing: List<AlbumContent>) {
        binding.apply {
            albumAdapter.listOfAlbumsContent = albumsListing.toMutableList()
            includedGridTitleForAlbums.apply {
                nestedScrollView.visibility = View.VISIBLE
                includedGridTitleForAlbums.recyclerviewAlbumsListing.apply {
                    layoutManager = GridLayoutManager(requireContext(), 2, GridLayoutManager.HORIZONTAL, false)
                    adapter = albumAdapter
                }
                includedGridTitle.clHeaderTitleOfHome.visibility = View.VISIBLE
                includedGridTitle.txGridTitle.text = resources.getString(R.string.top_albums)
                // TODO Remain to implement this feature
                includedGridTitle.txViewAll.setOnClickListener { requireContext().showSingleButtonDialog() }
            }
        }
    }

    // We can implement paging library here
    private fun showSongsListing(songsListing: List<SongContent>) {
        binding.apply {
            songsAdapter.listOfSongsContent = songsListing.toMutableList()
            nestedScrollView.visibility = View.VISIBLE
            recyclerviewSongsListing.adapter = songsAdapter
            includedGridTitleForSongs.clHeaderTitleOfHome.visibility = View.VISIBLE
            includedGridTitleForSongs.txGridTitle.text = resources.getString(R.string.top_songs)
            // TODO Remain to implement this feature
            includedGridTitleForSongs.txViewAll.setOnClickListener { requireContext().showSingleButtonDialog() }
        }
    }

    override fun onRefresh() {
        binding.swipeRefreshLayout.isRefreshing = false
        albumsManageViewModel.fetchAlbumsList(isRequiredToRefreshData = true, AppConstants.HOME_SCREEN_ALBUMS_LIMIT)
        songsListingScreenViewModel.fetchSongsList(isRequiredToRefreshData = true, AppConstants.HOME_SCREEN_SONGS_LIMIT)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
