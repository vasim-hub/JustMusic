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

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.justmusic.R
import com.justmusic.databinding.FragmentSearchBinding
import com.justmusic.shared.AppConstants.ADVANCE_SEARCH_ANY_TIME_YEARS_BACK_FROM_CURRENT_VALUE
import com.justmusic.shared.ResponseHandler
import com.justmusic.shared.model.AdvanceSearchData
import com.justmusic.shared.model.AdvanceSearchParamTypesData
import com.justmusic.ui.search.viewmodel.SearchViewModel
import kotlinx.coroutines.launch

/**
 * Advance search operation
 **/
interface AdvanceSearchHelperContractor {
    /** Load advance search data from Local DB**/
    fun loadAdvanceSearchData()
    /** Set advance search data to use for Advance search dialog [AdvanceSearchDialog]**/
    fun setAdvanceSearchData(advanceSearchData: AdvanceSearchData)
    /** Apply advance search with selected parameters from [AdvanceSearchDialog]**/
    fun applyAdvanceSearch()
    /** Reset advance search with empty **/
    fun resetAdvanceSearch()
    /** Show advance search**/
    fun showAdvanceSearchDialog()
}

/**
 * This is a Advance search helper class which required to manage advance search.
 * @param context Pass context to use resource and open dialog.
 * @param viewLifecycleOwner Pass context to use resource and open dialog.
 * @param searchBinding  Search fragment binding to use UI
 * @param searchViewModel Search view model [SearchViewModel]
 * @param advanceSearchParamTypesData Pass advance search model class [AdvanceSearchParamTypesData].
 * @param recentSearchHelper Helper class for manage recent search.
 */
class AdvanceSearchHelper(
    private val context: Context,
    private val viewLifecycleOwner: LifecycleOwner? = null,
    private val searchBinding: FragmentSearchBinding? = null,
    private val searchViewModel: SearchViewModel,
    private val advanceSearchParamTypesData: AdvanceSearchParamTypesData,
    private val recentSearchHelper: RecentSearchHelper
) : AdvanceSearchHelperContractor {

    var advanceSearchDialog =
        AdvanceSearchDialog(context, this, searchBinding, advanceSearchParamTypesData)

    init {
        /**
         * Put slightly delay to reduce load to open Search Fragment
         */
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed(
            {
                loadAdvanceSearchData()

                /*** As To Set Start date and End Date Initially for default
                 * taken 20 Years [ADVANCE_SEARCH_ANY_TIME_YEARS_BACK_FROM_CURRENT_VALUE]
                 */
                advanceSearchDialog.dateCalculationForReleaseDateRange(context.resources.getString(R.string.any_time))
            },
            200
        )

        viewLifecycleOwner?.let {
            registerAPIObserver()
        }
        searchBinding?.imageViewAdvanceSearch?.setOnClickListener {
            showAdvanceSearchDialog()
        }
    }

    override fun loadAdvanceSearchData() {
        searchViewModel.getAdvanceSearchData()
    }

    override fun setAdvanceSearchData(advanceSearchData: AdvanceSearchData) {
        advanceSearchDialog.advanceSearchData = advanceSearchData
    }

    override fun applyAdvanceSearch() {
        recentSearchHelper.showSearchResultFragments()
        searchBinding?.imageViewAdvanceSearch?.setColorFilter(
            ContextCompat.getColor(
                context,
                R.color.colorPrimary
            ),
            android.graphics.PorterDuff.Mode.SRC_IN
        )

        searchViewModel.searchContent(advanceSearchParamTypesData)
    }

    override fun resetAdvanceSearch() {
        advanceSearchParamTypesData.categoryType = ""
        advanceSearchParamTypesData.artist = ""
        advanceSearchDialog.isFilterApplied = false
        searchBinding?.imageViewAdvanceSearch?.setColorFilter(
            ContextCompat.getColor(
                context,
                R.color.color_757575
            ),
            android.graphics.PorterDuff.Mode.SRC_IN
        )
        searchViewModel.searchContent(advanceSearchParamTypesData)
        if (advanceSearchParamTypesData.searchText.isEmpty()) {
            recentSearchHelper.hideHideSearchResultFragments()
            recentSearchHelper.showRecentSearch()
        }
    }

    override fun showAdvanceSearchDialog() {
        advanceSearchDialog.setDialogContent()
    }

    private fun registerAPIObserver() {
        viewLifecycleOwner?.let {
            viewLifecycleOwner.lifecycleScope.launchWhenCreated {
                viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    launch {
                        searchViewModel.stateFlowAdvanceSearchData.collect { uiState ->
                            when (uiState) {
                                is ResponseHandler.Error -> {
                                }
                                is ResponseHandler.Success<AdvanceSearchData> -> {
                                    setAdvanceSearchData(uiState.data)
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
