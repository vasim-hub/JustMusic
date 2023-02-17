package com.justmusic.shared.model

/**
 * This is a model class which used for configure advance search data
 */
data class AdvanceSearchParamTypesData(
    var searchText: String = "",
    var categoryType: String = "",
    var artist: String = "",
    var releasedDateStartDate: String = "",
    var releasedDateEndDate: String = ""
)