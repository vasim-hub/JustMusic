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
package com.justmusic.ui.search

import android.app.Dialog
import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.justmusic.R
import com.justmusic.databinding.DialogAdvanceSearchOptionsBinding
import com.justmusic.databinding.FragmentSearchBinding
import com.justmusic.shared.AppConstants.ADVANCE_SEARCH_ANY_TIME_YEARS_BACK_FROM_CURRENT_VALUE
import com.justmusic.shared.model.AdvanceSearchData
import com.justmusic.shared.model.AdvanceSearchParamTypesData
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
/**
 * This is a Advance search dialog which required to Set data in dialog,open or hide operation.
 * Data will display based on Album or Song type
 * @param context Context to use resource and open dialog.
 * @param advanceSearchHelper This is a helper class for advance search [AdvanceSearchHelper].
 * @param searchBinding Search fragment binding to use UI.
 * @param advanceSearchParamTypesData Pass advance search model class [AdvanceSearchParamTypesData].
 */
class AdvanceSearchDialog(
    private val context: Context,
    private val advanceSearchHelper: AdvanceSearchHelper,
    private val searchBinding: FragmentSearchBinding? = null,
    private val advanceSearchParamTypesData: AdvanceSearchParamTypesData,
) {

    private var dialog: Dialog? = null
    var advanceSearchData: AdvanceSearchData? = null
    var isFilterApplied = false

    fun setDialogContent() {
        if (isFilterApplied) {
            showOnlyExistingDialog()
            return
        }

        dialog = Dialog(context)
        dialog?.apply {
            setCancelable(false)
            setCanceledOnTouchOutside(true)
            setContentView(R.layout.dialog_advance_search_options)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog?.window?.setBackgroundDrawableResource(R.drawable.bg_dialog)
            val layoutInflater = LayoutInflater.from(context)
            val dialogAdvanceSearchOptionsBinding: DialogAdvanceSearchOptionsBinding =
                DialogAdvanceSearchOptionsBinding.inflate(layoutInflater)
            setContentView(dialogAdvanceSearchOptionsBinding.root)

            advanceSearchData?.let {
                fetchSpinnerDataBasedOnType(
                    dialogAdvanceSearchOptionsBinding,
                    it
                )
            }

            setSpinnerDataAndEvent(
                dialogAdvanceSearchOptionsBinding.spinnerReleaseDuration,
                context.resources.getStringArray(R.array.release_date_types).toList()
            ) {
                dateCalculationForReleaseDateRange(it)
            }

            dialogAdvanceSearchOptionsBinding.txApplySearch.setOnClickListener {
                isFilterApplied = true
                hideAdvanceSearchDialog()
                advanceSearchHelper.applyAdvanceSearch()
            }
            dialogAdvanceSearchOptionsBinding.txCancel.setOnClickListener {
                hideAdvanceSearchDialog()
            }
            dialogAdvanceSearchOptionsBinding.txReset.setOnClickListener {
                hideAdvanceSearchDialog()
                isFilterApplied = false
                advanceSearchHelper.resetAdvanceSearch()
            }
            setWidthAndHeightPercent()
            show()
        }
    }

    private fun showOnlyExistingDialog() {
        if (dialog?.isShowing == false) {
            dialog?.show()
        }
    }

    private fun fetchSpinnerDataBasedOnType(
        dialogAdvanceSearchOptionsBinding: DialogAdvanceSearchOptionsBinding,
        advanceSearchData: AdvanceSearchData
    ) {

        val listOfCategory: MutableList<String>
        val listOfArtist: MutableList<String>

        // T
        if (searchBinding?.pagerForSearch?.currentItem == 0) {
            listOfCategory = advanceSearchData.advanceSearchData.listOfCategories.toMutableList()
            listOfArtist = advanceSearchData.advanceSearchData.listOfArtiest.toMutableList()
        } else {
            listOfCategory = advanceSearchData.songsAdvanceData.listOfCategories.toMutableList()
            listOfArtist = advanceSearchData.songsAdvanceData.listOfArtiest.toMutableList()
        }

        setSpinnerDataAndEvent(
            dialogAdvanceSearchOptionsBinding.spinnerType,
            listOfCategory
        ) {
            advanceSearchParamTypesData.categoryType = it
        }

        setSpinnerDataAndEvent(
            dialogAdvanceSearchOptionsBinding.spinnerArtist,
            listOfArtist
        ) {
            advanceSearchParamTypesData.artist = it
        }
    }

    private fun setSpinnerDataAndEvent(
        spinner: Spinner,
        list: List<String>,
        onSelectSpinnerItem: ((itemText: String) -> Unit)
    ) {
        val ad: ArrayAdapter<String> = ArrayAdapter<String>(
            context,
            android.R.layout.simple_spinner_dropdown_item,
            list
        )

        ad.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        spinner.adapter = ad
        spinner.setSelection(0, false)

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                onSelectSpinnerItem.invoke(list[position])
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    fun dateCalculationForReleaseDateRange(selectedDateRangeType: String) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        var c = Calendar.getInstance()
        when (selectedDateRangeType) {
            context.resources.getString(R.string.any_time) -> {
                c.add(Calendar.YEAR, ADVANCE_SEARCH_ANY_TIME_YEARS_BACK_FROM_CURRENT_VALUE)
            }
            context.resources.getString(R.string.this_week) -> {
                c[Calendar.WEEK_OF_MONTH] = 1
            }
            context.resources.getString(R.string.this_month) -> {
                c[Calendar.DAY_OF_MONTH] = 1
            }
            else -> {
                c[Calendar.DAY_OF_YEAR] = 1
            }
        }
        val startDate: String = dateFormat.format(c.time)
        c = Calendar.getInstance() // reset
        val endDate: String = dateFormat.format(c.time)
        advanceSearchParamTypesData.releasedDateStartDate = startDate
        advanceSearchParamTypesData.releasedDateEndDate = endDate
    }

    private fun setWidthAndHeightPercent() {
        val percentWidth = 99.toFloat() / 100
        val dm = Resources.getSystem().displayMetrics
        val rect = dm.run { Rect(0, 0, widthPixels, heightPixels) }
        val percentWidthAfterCalculated = rect.width() * percentWidth
        dialog?.window?.setLayout(
            percentWidthAfterCalculated.toInt(),
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    private fun hideAdvanceSearchDialog() {
        if (dialog?.isShowing == true) {
            dialog?.dismiss()
        }
    }
}
