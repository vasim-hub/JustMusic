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
package com.justmusic.ui.album.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.justmusic.databinding.ItemAlbumBinding
import com.justmusic.domain.models.AlbumContent
import com.justmusic.ui.album.adapter.viewholder.AlbumsRecyclerViewHolder

/**
 * Albums adapter to show data in list
 */
class AlbumAdapter(
    private val onItemClickedForFavorite: ((position: Int, albumContent: AlbumContent) -> Unit)?,
    private val onItemClicked: ((position: Int, albumContent: AlbumContent) -> Unit)?
) :
    RecyclerView.Adapter<AlbumsRecyclerViewHolder>() {

    var listOfAlbumsContent: MutableList<AlbumContent> = mutableListOf()

    private var hideMoreOptionsMenu = false
    private var isSearchRequiredToEnable: Boolean = false

    constructor(isSearchRequiredToEnable: Boolean, hideMoreOptionsMenu: Boolean, onItemClicked: ((position: Int, albumContent: AlbumContent) -> Unit)?) : this(null, onItemClicked) {
        this.hideMoreOptionsMenu = hideMoreOptionsMenu
        this.isSearchRequiredToEnable = isSearchRequiredToEnable
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AlbumsRecyclerViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemAlbumBinding.inflate(layoutInflater, parent, false)
        return AlbumsRecyclerViewHolder(binding, onItemClickedForFavorite, onItemClicked, hideMoreOptionsMenu, isSearchRequiredToEnable)
    }

    override fun onBindViewHolder(
        holder: AlbumsRecyclerViewHolder,
        position: Int
    ) {
        holder.itemView.context
        val album = listOfAlbumsContent[position]
        holder.bind(position, album)
    }

    override fun getItemCount() = listOfAlbumsContent.size

    fun updateItem(position: Int, albumContent: AlbumContent) {
        listOfAlbumsContent[position] = albumContent
        notifyItemChanged(position)
    }

    fun removeItem(position: Int) {
        listOfAlbumsContent.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, listOfAlbumsContent.size)
    }

    fun resetAdapter() {
        listOfAlbumsContent.clear()
        notifyDataSetChanged()
    }
}
