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
package com.justmusic.ui.song.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.justmusic.databinding.ItemSongBinding
import com.justmusic.domain.models.SongContent
import com.justmusic.ui.song.adapter.viewholder.SongsRecyclerViewHolder

/**
 * Songs adapter to show data in list
 */
class SongsAdapter(
    private val onItemClickedForFavorite: ((position: Int, songContent: SongContent) -> Unit)?,
    private val onItemClicked: ((position: Int, songContent: SongContent) -> Unit)?
) :
    RecyclerView.Adapter<SongsRecyclerViewHolder>() {

    private var hideMoreOptionsMenu = false
    private var isSearchRequiredToEnable: Boolean = false

    constructor(isSearchRequiredToEnable: Boolean, hideMoreOptionsMenu: Boolean, onItemClicked: ((position: Int, songContent: SongContent) -> Unit)?) : this(
        null,
        onItemClicked
    ) {
        this.hideMoreOptionsMenu = hideMoreOptionsMenu
        this.isSearchRequiredToEnable = isSearchRequiredToEnable
    }

    var listOfSongsContent: MutableList<SongContent> = mutableListOf()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SongsRecyclerViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemSongBinding.inflate(layoutInflater, parent, false)
        return SongsRecyclerViewHolder(
            binding,
            onItemClicked,
            onItemClickedForFavorite,
            hideMoreOptionsMenu,
            isSearchRequiredToEnable
        )
    }

    override fun onBindViewHolder(
        holder: SongsRecyclerViewHolder,
        position: Int
    ) {
        holder.itemView.context
        val song = listOfSongsContent[position]
        holder.bind(position, song)
    }

    override fun getItemCount() = listOfSongsContent.size

    fun updateItem(position: Int, songContent: SongContent) {
        listOfSongsContent[position] = songContent
        notifyItemChanged(position)
    }

    fun removeItem(position: Int) {
        listOfSongsContent.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, listOfSongsContent.size)
    }

    fun resetAdapter() {
        listOfSongsContent.clear()
        notifyDataSetChanged()
    }
}
