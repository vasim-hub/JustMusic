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

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.justmusic.R
import com.justmusic.base.BaseFragment
import com.justmusic.databinding.FragmentSearchBinding
import com.justmusic.domain.models.SearchContent
import com.justmusic.shared.ResponseHandler
import com.justmusic.shared.model.AdvanceSearchParamTypesData
import com.justmusic.ui.search.AdvanceSearchHelper
import com.justmusic.ui.search.RecentSearchHelper
import com.justmusic.ui.search.adapter.SearchFragmentStateAdapter
import com.justmusic.ui.search.viewmodel.SearchViewModel
import com.justmusic.utils.extensions.hideKeyboard
import com.justmusic.utils.extensions.showToastMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Locale

/**
 *Search fragment
 */
@AndroidEntryPoint
class SearchFragment : BaseFragment(R.layout.fragment_search), SearchView.OnQueryTextListener {

    private val searchViewModel: SearchViewModel by viewModels()
    lateinit var recentSearchHelper: RecentSearchHelper
    private lateinit var advanceSearchHelper: AdvanceSearchHelper

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var searchFragmentStateAdapter: SearchFragmentStateAdapter
    private val advanceSearchParamTypesData = AdvanceSearchParamTypesData()
    private var searchAlbumsFragment: SearchAlbumsFragment? = null
    private var searchSongsFragment: SearchSongsFragment? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSearchBinding.bind(view)
        recentSearchHelper = RecentSearchHelper(viewLifecycleOwner, binding, searchViewModel)
        advanceSearchHelper = AdvanceSearchHelper(
            requireContext(),
            viewLifecycleOwner,
            binding,
            searchViewModel,
            advanceSearchParamTypesData,
            recentSearchHelper
        )
        initListener()
        binding.searchView.setOnQueryTextListener(this)
        registerAPIObserver()
        initViewPagerAdapter()
        initListener()
        binding.searchView.requestFocus()
    }

    private fun showInputMethod(view: View) {
        val imm = requireContext().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager?
        imm?.showSoftInput(view, 0)
    }

    private fun registerAPIObserver() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    searchViewModel.stateFlowSearchContent.collect { uiState ->
                        when (uiState) {
                            is ResponseHandler.Error -> {
                                hideLoading()
                                showError(uiState.error, uiState.httpStatusEnum)
                            }
                            is ResponseHandler.Success<SearchContent> -> {
                                hideLoading()
                                if (!songsFragmentCreated) {
                                    searchContent = uiState.data
                                }
                                showSearchData(uiState.data)
                            }
                            else -> showLoading()
                        }
                    }
                }
            }
        }
    }

    private fun initViewPagerAdapter() {
        val tabTitles = arrayOf(getString(R.string.albums), getString(R.string.songs))
        searchFragmentStateAdapter = SearchFragmentStateAdapter(this)
        binding.pagerForSearch.adapter = searchFragmentStateAdapter
        TabLayoutMediator(
            binding.tabLayoutForMyFavorites, binding.pagerForSearch
        ) { myTabLayout: TabLayout.Tab, position: Int ->
            myTabLayout.text = tabTitles[position]
        }.attach()
    }

    private fun showSearchData(data: SearchContent) {
        if (searchAlbumsFragment == null) {
            val fragmentZero =
                childFragmentManager.findFragmentByTag("f" + 0)
            if (fragmentZero != null) {
                searchAlbumsFragment = fragmentZero as SearchAlbumsFragment
            }
        }
        searchAlbumsFragment?.updateData(data.listAlbumsContent)

        if (searchSongsFragment == null) {
            val fragmentOne =
                childFragmentManager.findFragmentByTag("f" + 1)
            if (fragmentOne != null) {
                searchSongsFragment = fragmentOne as SearchSongsFragment
            }
        }
        searchSongsFragment?.updateData(data.listSongsContent)
    }

    private fun clearAdaptersForViewPagerFragments() {
        searchAlbumsFragment?.clearAdapterData()
        searchSongsFragment?.clearAdapterData()
    }

    /** Register fragment event listener */
    private fun initListener() {
        binding.imageSearchMic.setOnClickListener { startMicSearch() }
        binding.searchView.setOnQueryTextFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                showInputMethod(view.findFocus())
            }
        }
    }

    /**
     *  This function deals with the logic of search as soon as user clicks search button.
     *  The list will show filtered data as soon as user clicks submit button, the list is be
     *  unchanged when user types anything.
     */
    override fun onQueryTextSubmit(searchString: String?): Boolean {
        searchLogic(searchString)
        return true
    }

    /**
     *  This function deals with the logic of search as soon as user types something in
     *  search view text. The list will show filtered data as soon as user types anything to search
     */
    override fun onQueryTextChange(searchString: String?): Boolean {
        searchLogic(searchString)
        return false
    }

    /**
     *  This function deals with the logic of search the if condition represents if search
     *  view is having a value to search then check if it exists in list else deals with a
     *  condition when search view is empty then reset the list
     */
    private fun searchLogic(searchString: String?) {
        if (searchString?.isNotEmpty() == true) {
            searchText = searchString
            recentSearchHelper.showSearchResultFragments()
            setSearchTextInSearchModel(searchString)
            searchString.let { searchViewModel.searchContent(advanceSearchParamTypesData) }
        } else {
            resetSearchText()
            if (advanceSearchHelper.advanceSearchDialog.isFilterApplied) {
                searchViewModel.searchContent(advanceSearchParamTypesData)
                return
            }
            recentSearchHelper.hideHideSearchResultFragments()
            recentSearchHelper.showRecentSearch()
            clearAdaptersForViewPagerFragments()
        }
    }

    private fun setSearchTextInSearchModel(searchString: String) {
        advanceSearchParamTypesData.searchText = searchString
    }

    private fun startMicSearch() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.speech_prompt_message))
        try {
            speechInputLauncher.launch(intent)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
            requireActivity().showToastMessage(getString(R.string.voice_search_not_supported))
        }
    }

    private val speechInputLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val spokenText: String? =
                    result?.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.get(0)
                binding.searchView.setQuery(spokenText, false)
            }
        }

    override fun showLoading() {}
    override fun hideLoading() {}
    override fun onRetry() {}

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onPause() {
        super.onPause()
        view?.hideKeyboard()
    }

    companion object {
        var songsFragmentCreated: Boolean = false
        var searchContent: SearchContent? = null
        var searchText = ""
    }

    private fun resetSearchText() {
        searchText = ""
        advanceSearchParamTypesData.searchText = ""
    }

    override fun onDestroy() {
        super.onDestroy()
        searchContent = null
        songsFragmentCreated = false
        resetSearchText()
    }
}
