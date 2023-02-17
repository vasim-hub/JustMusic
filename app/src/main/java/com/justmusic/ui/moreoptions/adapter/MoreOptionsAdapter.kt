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
package com.justmusic.ui.moreoptions.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.justmusic.databinding.ItemBottomSheetBinding
import com.justmusic.ui.moreoptions.adapter.viewholder.MoreOptionsViewHolder
import com.justmusic.ui.moreoptions.model.MoreOptionItemClickEventData

/**
 * More Options adapter to show data in list
 */
class MoreOptionsAdapter<T>(private val onItemClickedForMenuOption: (moreOptionItemClickEventData: MoreOptionItemClickEventData<T>) -> Unit) :
    RecyclerView.Adapter<MoreOptionsViewHolder<T>>() {

    var listOfMoreOptions: MutableList<MoreOptionItemClickEventData<T>> = mutableListOf()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MoreOptionsViewHolder<T> {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemBottomSheetBinding.inflate(layoutInflater, parent, false)
        return MoreOptionsViewHolder(binding, onItemClickedForMenuOption)
    }

    override fun onBindViewHolder(
        holder: MoreOptionsViewHolder<T>,
        position: Int
    ) {
        holder.itemView.context
        val moreOptionItemClickEventData = listOfMoreOptions[position]
        holder.bind(moreOptionItemClickEventData)
    }

    override fun getItemCount() = listOfMoreOptions.size
}
