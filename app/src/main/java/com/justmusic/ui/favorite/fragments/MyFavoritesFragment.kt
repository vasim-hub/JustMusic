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
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.justmusic.R
import com.justmusic.base.BaseFragment
import com.justmusic.databinding.FragmentMyFavoritesBinding
import com.justmusic.ui.favorite.adapter.MyFavoritesFragmentStateAdapter
import dagger.hilt.android.AndroidEntryPoint

/**
 *My Favorites fragment
 */
@AndroidEntryPoint
class MyFavoritesFragment : BaseFragment(R.layout.fragment_my_favorites) {

    private var _binding: FragmentMyFavoritesBinding? = null
    private val binding get() = _binding!!
    private lateinit var myFavoritesFragmentStateAdapter: MyFavoritesFragmentStateAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMyFavoritesBinding.bind(view)
        setToolbarTitle(binding.includedToolbarFavorites.txToolbarTitle, getString(R.string.title_favorite))
        initListener()
        initViewPagerAdapter()
    }

    private fun initViewPagerAdapter() {
        val tabTitles = arrayOf(getString(R.string.albums), getString(R.string.songs))
        myFavoritesFragmentStateAdapter = MyFavoritesFragmentStateAdapter(this)
        binding.pagerForMyFavorites.adapter = myFavoritesFragmentStateAdapter
        TabLayoutMediator(
            binding.tabLayoutForMyFavorites, binding.pagerForMyFavorites
        ) { myTabLayout: TabLayout.Tab, position: Int ->
            myTabLayout.text = tabTitles[position]
        }.attach()
    }

    private fun initListener() {}

    override fun showLoading() {}

    override fun hideLoading() {}

    override fun onRetry() {}

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
