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
package com.justmusic.ui.moreoptions.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.justmusic.databinding.ItemBottomSheetBinding
import com.justmusic.ui.moreoptions.model.MoreOptionItemClickEventData

/**
 * More Options recycler viewHolder is use to bind data with view
 */
class MoreOptionsViewHolder<T>(
    private val itemBottomSheetBinding: ItemBottomSheetBinding,
    private val onItemClickedForFavorite: (
        moreOptionItemClickEventData: MoreOptionItemClickEventData<T>
    ) -> Unit
) :
    RecyclerView.ViewHolder(itemBottomSheetBinding.root) {

    fun bind(moreOptionItemClickEventData: MoreOptionItemClickEventData<T>) {
        itemBottomSheetBinding.apply {
            txMenuForMoreOption.text = moreOptionItemClickEventData.menuName
            txMenuForMoreOption.setCompoundDrawablesWithIntrinsicBounds(
                moreOptionItemClickEventData.menuRes,
                0,
                0,
                0
            )
            itemView.setOnClickListener {
                onItemClickedForFavorite.invoke(
                    moreOptionItemClickEventData
                )
            }
        }
    }
}
