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
package com.justmusic.utils.extensions

import android.os.Build
import android.text.Html
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.justmusic.R
import com.justmusic.ui.search.fragments.SearchFragment

fun TextView.showHtmlText(htmlText: String) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        this.text = Html.fromHtml(htmlText, Html.FROM_HTML_MODE_COMPACT)
    } else {
        @Suppress("DEPRECATION")
        this.text = Html.fromHtml(htmlText)
    }
}

fun TextView.makeSearchHighLight(content: String) {
    if (SearchFragment.searchText.isEmpty()) {
        text = content
        return
    }
    val spannableText: Spannable = SpannableString(content)
    spannableText.setSpan(
        ForegroundColorSpan(ContextCompat.getColor(this.context, R.color.colorPrimary)),
        0,
        SearchFragment.searchText.length,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    text = spannableText
}
