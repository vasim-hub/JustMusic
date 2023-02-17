package com.justmusic.utils.extensions

import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.getSystemService

fun View.hideKeyboard() {
    val imm =
        context.getSystemService<InputMethodManager>()
    imm?.hideSoftInputFromWindow(this.windowToken, 0)
}
