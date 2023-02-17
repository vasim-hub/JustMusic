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
package com.justmusic.ui.search.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.justmusic.databinding.ItemRecentSearchBinding
import com.justmusic.domain.models.RecentSearchContent
import com.justmusic.utils.extensions.loadImage
import com.justmusic.utils.extensions.showHtmlText

/**
 * Search recycler viewHolder is use to bind data with view
 */
class SearchItemRecyclerViewHolder(
    private val itemSearchBinding: ItemRecentSearchBinding,
    private val onItemClickedRemoveRecentSearch: (
        position: Int,
        recentSearchContent: RecentSearchContent
    ) -> Unit
) :
    RecyclerView.ViewHolder(itemSearchBinding.root) {

    fun bind(position: Int, recentSearchContent: RecentSearchContent) {
        itemSearchBinding.apply {
            recentSearchContent.coverUrl?.let { imageViewItem.loadImage(it) }
            txTitle.text = recentSearchContent.name
            recentSearchContent.subTitle?.let { txSubTitle.showHtmlText(it) }
            imageViewRemoveRecentSearch.setOnClickListener {
                onItemClickedRemoveRecentSearch.invoke(position, recentSearchContent)
            }
        }
    }
}
