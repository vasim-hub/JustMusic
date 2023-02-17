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
package com.justmusic.ui.search

import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.justmusic.databinding.FragmentSearchBinding
import com.justmusic.domain.models.RecentSearchContent
import com.justmusic.shared.AppContentTypeEnum
import com.justmusic.shared.ResponseHandler
import com.justmusic.ui.search.adapter.RecentSearchItemAdapter
import com.justmusic.ui.search.viewmodel.SearchViewModel
import kotlinx.coroutines.launch

/**
 * Recent search operation
 **/
interface RecentSearchContractor {
    /** When item will found and user will click on that item, tha will considered as
     *  recent search local db by item Id and type [AppContentTypeEnum]**/
    fun addRecentSearch(itemId: Long, appContentTypeEnum: AppContentTypeEnum)

    /** Load recent search which used to show in case cleared search box **/
    fun loadRecentSearches()

    /** Bind data only adapter**/
    fun bindDataWithAdapter(listOfRecentSearches: List<RecentSearchContent>)

    /** Show Recent search UI only**/
    fun showRecentSearch()

    /** Hide Recent search UI only**/
    fun hideRecentSearch()

    /** Show found result UI in form of Album and Song Listing**/
    fun showSearchResultFragments()

    /** Hide found result search screen **/
    fun hideHideSearchResultFragments()

    /** Remove selected item from recent search**/
    fun removeFromRecentSearch(position: Int, itemId: Long)

    /** Remove all items from recent search**/
    fun clearAllRecentSearches()
}

/**
 * This is a Recent search helper class which required to manage recent search.
 * @param viewLifecycleOwner Pass context to use resource and open dialog.
 * @param searchBinding  Search fragment binding to use UI
 * @param searchViewModel Search view model [SearchViewModel]
 */
class RecentSearchHelper(
    private val viewLifecycleOwner: LifecycleOwner? = null,
    private val searchBinding: FragmentSearchBinding? = null,
    private val searchViewModel: SearchViewModel
) : RecentSearchContractor {

    private var recentSearchItemAdapter: RecentSearchItemAdapter? = null

    init {
        viewLifecycleOwner?.let {
            registerAPIObserver()
            loadRecentSearches()
        }
        searchBinding?.txClearRecentSearches?.setOnClickListener {
            clearAllRecentSearches()
            hideRecentSearch()
        }
    }

    override fun addRecentSearch(itemId: Long, appContentTypeEnum: AppContentTypeEnum) {
        searchViewModel.addItemInRecentSearch(itemId, appContentTypeEnum)
    }

    override fun loadRecentSearches() {
        searchViewModel.getALlRecentSearches()
    }

    override fun bindDataWithAdapter(listOfRecentSearches: List<RecentSearchContent>) {
        searchBinding?.apply {
            if (recentSearchItemAdapter == null) {

                recentSearchItemAdapter = RecentSearchItemAdapter { position, recentSearchContent ->
                    removeFromRecentSearch(position, recentSearchContent.itemId)
                }
                recentSearchItemAdapter?.listOfRecentSearchContent =
                    listOfRecentSearches.toMutableList()
                recyclerViewRecentSearches.adapter = recentSearchItemAdapter
            } else {
                recentSearchItemAdapter?.listOfRecentSearchContent =
                    listOfRecentSearches.toMutableList()
                recentSearchItemAdapter?.updateList(listOfRecentSearches)
            }
        }
    }

    override fun showRecentSearch() {
        recentSearchItemAdapter?.let {
            if (it.listOfRecentSearchContent.isNotEmpty()) {
                searchBinding?.apply {
                    llSearchResult.visibility = View.GONE
                    llRecentSearchResultContainer.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun hideRecentSearch() {
        searchBinding?.apply {
            llRecentSearchResultContainer.visibility = View.GONE
        }
    }

    override fun showSearchResultFragments() {
        hideRecentSearch()
        searchBinding?.apply {
            llSearchResult.visibility = View.VISIBLE
        }
    }

    override fun hideHideSearchResultFragments() {
        searchBinding?.apply {
            llSearchResult.visibility = View.GONE
        }
    }

    override fun removeFromRecentSearch(position: Int, itemId: Long) {
        recentSearchItemAdapter?.removeItem(position)
        searchViewModel.clearRecentSearchById(itemId)
        if (recentSearchItemAdapter?.listOfRecentSearchContent?.isEmpty() == true) {
            hideRecentSearch()
        }
    }

    override fun clearAllRecentSearches() {
        searchBinding.apply {
            hideRecentSearch()
            recentSearchItemAdapter?.removeAll()
            searchViewModel.clearAllRecentSearches()
        }
    }

    private fun registerAPIObserver() {
        viewLifecycleOwner?.let {
            viewLifecycleOwner.lifecycleScope.launchWhenCreated {
                viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    launch {
                        searchViewModel.stateFlowRecentAddedItemInRecentSearch.collect { uiState ->
                            when (uiState) {
                                is ResponseHandler.Success<Long> -> {
                                    loadRecentSearches()
                                }
                                else -> {}
                            }
                        }
                    }

                    launch {
                        searchViewModel.stateFlowRecentSearchListing.collect { uiState ->
                            when (uiState) {
                                is ResponseHandler.Success<List<RecentSearchContent>> -> {
                                    bindDataWithAdapter(uiState.data)
                                }
                                else -> {}
                            }
                        }
                    }
                }
            }
        }
    }
}
