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
package com.justmusic.ui.moreoptions.model

import com.justmusic.utils.appenums.MoreMenuClickEventsTypeEnum

/**
 * This data class can be used to adapt showing different options at bottom sheet dialog
 * More Options data class
 * @param T generic type of data class from selected list
 * @param position position of list item
 * @param menuRes resource id for menu option in bottom sheet dialog
 * @param moreMenuClickEventsTypeEnum required to pass type of click event,
 * More Details [MoreOptionItemClickEventData]
 */
data class MoreOptionItemClickEventData<T>(
    val t: T,
    val position: Int,
    val menuRes: Int,
    val menuName: String,
    val moreMenuClickEventsTypeEnum: MoreMenuClickEventsTypeEnum
)
