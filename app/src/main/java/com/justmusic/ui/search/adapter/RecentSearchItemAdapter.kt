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
package com.justmusic.ui.search.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.justmusic.databinding.ItemRecentSearchBinding
import com.justmusic.domain.models.RecentSearchContent
import com.justmusic.ui.search.adapter.viewholder.SearchItemRecyclerViewHolder

/**
 * Recent adapter to show data in list
 */
class RecentSearchItemAdapter(
    private val onItemClickedRemoveRecentSearch: (
        position: Int,
        recentSearchContent: RecentSearchContent
    ) -> Unit
) :
    RecyclerView.Adapter<SearchItemRecyclerViewHolder>() {

    var listOfRecentSearchContent: MutableList<RecentSearchContent> = mutableListOf()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SearchItemRecyclerViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemRecentSearchBinding.inflate(layoutInflater, parent, false)
        return SearchItemRecyclerViewHolder(binding, onItemClickedRemoveRecentSearch)
    }

    override fun onBindViewHolder(
        holder: SearchItemRecyclerViewHolder,
        position: Int
    ) {
        holder.itemView.context
        val song = listOfRecentSearchContent[position]
        holder.bind(position, song)
    }

    override fun getItemCount() = listOfRecentSearchContent.size

    fun removeItem(position: Int) {
        listOfRecentSearchContent.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, listOfRecentSearchContent.size)
    }
    fun updateList(list: List<RecentSearchContent>) {
        listOfRecentSearchContent.clear()
        listOfRecentSearchContent.addAll(list)
        notifyDataSetChanged()
    }

    fun removeAll() {
        listOfRecentSearchContent.clear()
        notifyDataSetChanged()
    }
}
