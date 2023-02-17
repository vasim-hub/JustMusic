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
package com.justmusic.ui.song.adapter.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.justmusic.databinding.ItemSongBinding
import com.justmusic.domain.models.SongContent
import com.justmusic.utils.extensions.loadImage
import com.justmusic.utils.extensions.makeSearchHighLight

/**
 * Songs recycler viewHolder is use to bind data with view
 */
class SongsRecyclerViewHolder(
    private val itemSongBinding: ItemSongBinding,
    private val onItemClicked: ((position: Int, songContent: SongContent) -> Unit)?,
    private val onItemClickedForFavorite: ((position: Int, songContent: SongContent) -> Unit)?,
    private val hideMoreOptionsMenu: Boolean,
    private val isSearchRequiredToEnable: Boolean = false
) :
    RecyclerView.ViewHolder(itemSongBinding.root) {

    fun bind(position: Int, songContent: SongContent) {
        itemSongBinding.apply {
            songContent.cover?.let { imageViewSong.loadImage(it) }

            if (isSearchRequiredToEnable) {
                txSongTitle.makeSearchHighLight(songContent.name)
            } else {
                txSongTitle.text = songContent.name
            }

            // txSongTitle.text = songContent.name
            txSongArtist.text = songContent.artist
            if (hideMoreOptionsMenu) {
                imageViewMoreOptions.visibility = View.GONE
            } else {
                imageViewMoreOptions.visibility = View.VISIBLE
            }
            onItemClickedForFavorite?.let {
                imageViewMoreOptions.setOnClickListener {
                    onItemClickedForFavorite.invoke(position, songContent)
                }
            }

            onItemClicked?.let {
                itemView.setOnClickListener {
                    onItemClicked.invoke(position, songContent)
                }
            }
        }
    }
}
